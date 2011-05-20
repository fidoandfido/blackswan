package net.fidoandfido.engine.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.dao.RumourDAO;
import net.fidoandfido.engine.eventgenerators.EventGeneratorFactory;
import net.fidoandfido.engine.profitmodifers.LinearProfitModifier;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.PeriodRumour;
import net.fidoandfido.model.SectorNewsEvent;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.Constants.EventType;

public class PeriodEventGenerator {

	// To be used to get the event type.
	private Random randomTime = new Random();

	// These will be thrown away!
	Map<String, SectorNewsEvent> sectorEventMap = new HashMap<String, SectorNewsEvent>();

	private static final int PERIOD_PART_COUNT = 4;
	private static final int FIRST_QUARTER_PART_INDEX = 0;
	private static final int SECOND_QUARTER_PART_INDEX = 1;
	private static final int THIRD_QUARTER_PART_INDEX = 2;
	private static final int FOURTH_QUARTER_PART_INDEX = 3;

	public static final String FIRST_QUARTER = "First quarter forecast";
	public static final String SECOND_QUARTER = "Second quarter forecast";
	public static final String THIRD_QUARTER = "Third quarter forecast";
	public static final String FOURTH_QUARTER = "Fourth quarter forecast";

	public static final int AGE_PROBABILITY_RANGE = 100;
	public static final int GOLDEN_AGE_HIT = 99;
	public static final int DARK_AGE_HIT = 98;
	public static final int AGE_LENGTH = 4;

	private PeriodPartInformationDAO periodPartInformationDAO;
	private CompanyPeriodReportDAO companyPeriodReportDAO;
	private RumourDAO rumourDAO;

	// private static final int DIVIDEND_ANNOUNCED = 4;

	public PeriodEventGenerator() {
		super();
		periodPartInformationDAO = new PeriodPartInformationDAO();
		companyPeriodReportDAO = new CompanyPeriodReportDAO();
		rumourDAO = new RumourDAO();
	}

	Random enterAnAgeRandom = new Random(17);

	public void generateEvents(CompanyPeriodReport periodReport, Company company, StockExchange stockExchange) {

		// Check to see if we are in a golden age / battler age.
		// If not, roll the dice to see if we go into one!
		if (company.getRemainingPeriodsOfDarkAge() > 0) {
			company.decrementRemainingPeriodsOfDarkAge();
		} else if (company.getRemainingPeriodsOfGoldenAge() > 0) {
			company.decrementRemainingPeriodsOfGoldenAge();
		} else {
			// We weren't in any age, see if we go in one now...
			int enterAge = enterAnAgeRandom.nextInt(AGE_PROBABILITY_RANGE);
			if (enterAge == DARK_AGE_HIT) {
				company.setRemainingPeriodsOfDarkAge(AGE_LENGTH);
			} else if (enterAge == GOLDEN_AGE_HIT) {
				company.setRemainingPeriodsOfGoldenAge(AGE_LENGTH);
			}
		}

		String sectorName = company.getSector();
		LinearProfitModifier profitModifier = new LinearProfitModifier();
		EventGenerator eventGenerator = EventGeneratorFactory.getGeneratorByName(stockExchange.getName(), stockExchange.getEventGeneratorName());

		EventData eventData = new EventData(0, 0, 0, 0);
		SectorNewsEvent sectorEvent = null;
		if (sectorEventMap.containsKey(sectorName)) {
			sectorEvent = sectorEventMap.get(sectorName);
		} else {
			// Get the event type...
			sectorEvent = new SectorNewsEvent(sectorName);

			sectorEvent.setFirstEventType(eventGenerator.getNextEventType());
			sectorEvent.setSecondEventType(eventGenerator.getNextEventType(sectorEvent.getFirstEventType()));

			sectorEventMap.put(sectorName, sectorEvent);
		}
		// Now we have the 'sector event' object - populate the period record
		// appropriately.

		// //////////////////////////////////////////////
		// First quarter sector outlook - decided by sector.

		eventData = profitModifier.adjustProfit(sectorEvent.getFirstEventType(), eventData, company, periodReport, PERIOD_PART_COUNT);
		Date firstQuarterDate = (getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, FIRST_QUARTER_PART_INDEX, true));
		PeriodEvent firstQuarterEvent = new PeriodEvent(company, periodReport, firstQuarterDate, getMessage(sectorEvent.getFirstEventType()),
				sectorEvent.getFirstEventType(), FIRST_QUARTER);

		firstQuarterEvent.setData(eventData);

		periodPartInformationDAO.savePeriodPartInformation(firstQuarterEvent);
		periodReport.addPeriodEvent(firstQuarterEvent);
		generateRumour(periodReport, firstQuarterEvent, FIRST_QUARTER_PART_INDEX);

		// //////////////////////////////////////////////
		// Second quarter outlook
		EventType secondQuarterEvent = eventGenerator.getNextEventType();

		if (company.getRemainingPeriodsOfDarkAge() > 0) {
			if (secondQuarterEvent == EventType.GOOD || secondQuarterEvent == EventType.GREAT || secondQuarterEvent == EventType.EXTRAORDINARY) {
				secondQuarterEvent = EventType.AVERAGE;
			}
		}
		if (company.getRemainingPeriodsOfGoldenAge() > 0) {
			if (secondQuarterEvent == EventType.POOR || secondQuarterEvent == EventType.TERRIBLE || secondQuarterEvent == EventType.CATASTROPHIC) {
				secondQuarterEvent = EventType.AVERAGE;
			}
		}

		String longTermCompanyMessage = getMessage(secondQuarterEvent);
		Date longTermCompanyDate = getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, SECOND_QUARTER_PART_INDEX, true);
		eventData = profitModifier.adjustProfit(secondQuarterEvent, eventData, company, periodReport, PERIOD_PART_COUNT);

		PeriodEvent secondQuarterInformation = new PeriodEvent(company, periodReport, longTermCompanyDate, longTermCompanyMessage, secondQuarterEvent,
				SECOND_QUARTER);

		secondQuarterInformation.setData(eventData);

		periodPartInformationDAO.savePeriodPartInformation(secondQuarterInformation);
		periodReport.addPeriodEvent(secondQuarterInformation);
		generateRumour(periodReport, secondQuarterInformation, SECOND_QUARTER_PART_INDEX);

		// //////////////////////////////////////////////
		// third quarter outlook
		eventData = profitModifier.adjustProfit(sectorEvent.getSecondEventType(), eventData, company, periodReport, PERIOD_PART_COUNT);
		Date thirdQuarterDate = getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, THIRD_QUARTER_PART_INDEX, true);

		PeriodEvent thirdQuarterInformation = new PeriodEvent(company, periodReport, thirdQuarterDate, getMessage(sectorEvent.getSecondEventType()),
				sectorEvent.getSecondEventType(), THIRD_QUARTER);

		thirdQuarterInformation.setData(eventData);
		periodPartInformationDAO.savePeriodPartInformation(thirdQuarterInformation);
		periodReport.addPeriodEvent(thirdQuarterInformation);
		generateRumour(periodReport, thirdQuarterInformation, THIRD_QUARTER_PART_INDEX);

		// //////////////////////////////////////////////
		// Fourth quarter outlook
		Date shortTermCompanyDate = getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, FOURTH_QUARTER_PART_INDEX, true);
		EventType fourthQuarterEvent = eventGenerator.getNextEventType();

		if (company.getRemainingPeriodsOfDarkAge() > 0) {
			if (fourthQuarterEvent == EventType.GOOD || fourthQuarterEvent == EventType.GREAT || fourthQuarterEvent == EventType.EXTRAORDINARY) {
				fourthQuarterEvent = EventType.AVERAGE;
			}
		}
		if (company.getRemainingPeriodsOfGoldenAge() > 0) {
			if (fourthQuarterEvent == EventType.POOR || fourthQuarterEvent == EventType.TERRIBLE || fourthQuarterEvent == EventType.CATASTROPHIC) {
				fourthQuarterEvent = EventType.AVERAGE;
			}
		}

		String shortTermCompanyMessage = getMessage(fourthQuarterEvent);
		eventData = profitModifier.adjustProfit(fourthQuarterEvent, eventData, company, periodReport, PERIOD_PART_COUNT);

		PeriodEvent shortTermCompanyInformation = new PeriodEvent(company, periodReport, shortTermCompanyDate, shortTermCompanyMessage, fourthQuarterEvent,
				FOURTH_QUARTER);

		shortTermCompanyInformation.setData(eventData);
		periodPartInformationDAO.savePeriodPartInformation(shortTermCompanyInformation);
		periodReport.addPeriodEvent(shortTermCompanyInformation);
		generateRumour(periodReport, secondQuarterInformation, FOURTH_QUARTER_PART_INDEX);

		// ///////////////////////////////////////////////
		// Final profit announcement
		// Profit modifier might effect this based on volatility perhaps?
		periodReport.setFinalRevenue(eventData.getRunningRevenue());
		periodReport.setFinalExpenses(eventData.getRunningExpenses());
		periodReport.setFinalInterest(eventData.getRunningInterestPaid());
		periodReport.setFinalProfit(eventData.getRunningProfit());

		companyPeriodReportDAO.savePeriodReport(periodReport);
	}

	private String getMessage(EventType eventType) {
		String message = "Something happened!";
		switch (eventType) {
		case CATASTROPHIC:
			message = "Revenues slashed on weak ecomonic figures; rising costs of yak food threaten company";
			break;
		case TERRIBLE:
			message = "Expenses sky-rocket as union reopens negotiations for paid Narwhal carers leave.";
			break;
		case POOR:
			message = "Expenses steady as company battles difficult market";
			break;
		case AVERAGE:
			message = "Company position steady, analysts bored.";
			break;
		case GOOD:
			message = "Company synergises to better align processes; profits up!";
			break;
		case GREAT:
			message = "Company in control as stranglehold on market exerted, regulators look on powerless";
			break;
		case EXTRAORDINARY:
			message = "Fantastic profits ahead as company takes advantage of yak shortage by substituting cheaper better oxes.";
			break;
		}
		return message;
	}

	private Random rumourRandom = new Random();
	private Random reputationRequiredRandom = new Random();

	private void generateRumour(CompanyPeriodReport periodReport, PeriodEvent event, int periodPartIndex) {
		int rumourRandomValue = rumourRandom.nextInt(100);
		boolean generateRumour = false;
		boolean positive = false;

		switch (event.getEventType()) {

		case CATASTROPHIC:
			// rumour chance: 80 %
			if (rumourRandomValue <= 80) {
				generateRumour = true;
				positive = false;
			}
			break;
		case TERRIBLE:
			// rumour chance: 50 %
			if (rumourRandomValue <= 50) {
				generateRumour = true;
				positive = false;
			}
			break;
		case POOR:
			// rumour chance: 10 %
			if (rumourRandomValue <= 10) {
				generateRumour = true;
				positive = false;
			}
			break;
		case AVERAGE:
			break;
		case GOOD:
			// rumour chance: 10 %
			if (rumourRandomValue <= 10) {
				generateRumour = true;
				positive = true;
			}
			break;
		case GREAT:// rumour chance: 40 %
			if (rumourRandomValue <= 40) {
				generateRumour = true;
				positive = true;
			}
			break;
		case EXTRAORDINARY:
			// rumour chance: 80 %
			if (rumourRandomValue <= 80) {
				generateRumour = true;
				positive = true;
			}
			break;
		}

		if (generateRumour) {
			// Get the time the rumour will be available...
			// This will basically be the start of the period for which it
			// applies.
			int isRepRequired = reputationRequiredRandom.nextInt(2);
			int reputationRequired = 0;

			switch (isRepRequired) {
			case 0:
				break;
			case 1:
				reputationRequired = 100;
				break;
			}

			Date rumourStarts = getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, periodPartIndex, false);
			String message = positive ? "Analysts expecting good news as CEO seen driving new sports car!"
					: "CEO lost in tropics, no-one at the helm - results look troubling!";
			PeriodRumour rumour = new PeriodRumour(periodReport.getCompany(), periodReport, rumourStarts, event.getDateInformationAvailable(),
					reputationRequired, message, (positive ? EventType.GREAT : EventType.TERRIBLE), event.getAnnouncementType());

			rumourDAO.saveRumour(rumour);
			periodReport.addRumour(rumour);

		}

	}

	/**
	 * Return a date within a period (as defined in the constants file and
	 * supplied period report) based on the number of parts we want to break the
	 * period into, the offset and whether we want to randomize how far into the
	 * period we want the event to occur.
	 * 
	 * @param periodReport
	 * @return
	 */
	private Date getDateWithinPeriod(CompanyPeriodReport periodReport, long periodPartCount, long periodPartIndex, boolean randomizeStartTime) {
		Date date = periodReport.getStartDate();
		long periodPartLength = periodReport.getPeriodLength() / periodPartCount;
		long periodPartOffSet = periodPartLength * periodPartIndex;
		long periodPartStartTimeDelta = 0;
		if (randomizeStartTime) {
			periodPartStartTimeDelta = (periodPartLength * randomTime.nextInt(100)) / 100;
		}
		return new Date(date.getTime() + periodPartOffSet + periodPartStartTimeDelta);
	}

}
