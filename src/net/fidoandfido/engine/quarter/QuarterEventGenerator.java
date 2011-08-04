package net.fidoandfido.engine.quarter;

import java.util.Date;
import java.util.Random;

import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.dao.RumourDAO;
import net.fidoandfido.engine.CompanyProfileController;
import net.fidoandfido.engine.profitmodifers.EventProfitModifier;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.PeriodQuarter;
import net.fidoandfido.model.PeriodRumour;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.Constants.QuarterPerformanceType;

public class QuarterEventGenerator {

	// To be used to get the event type.
	private Random randomTime = new Random();

	private static final int PERIOD_PART_COUNT = 4;

	public static final int AGE_PROBABILITY_RANGE = 100;
	public static final int GOLDEN_AGE_HIT = 99;
	public static final int DARK_AGE_HIT = 98;
	public static final int AGE_LENGTH = 4;

	private PeriodPartInformationDAO periodPartInformationDAO;
	private CompanyPeriodReportDAO companyPeriodReportDAO;
	private RumourDAO rumourDAO;

	// private static final int DIVIDEND_ANNOUNCED = 4;

	public QuarterEventGenerator() {
		periodPartInformationDAO = new PeriodPartInformationDAO();
		companyPeriodReportDAO = new CompanyPeriodReportDAO();
		rumourDAO = new RumourDAO();
	}

	Random enterAnAgeRandom = new Random(17);

	public void generateQuarters(CompanyPeriodReport periodReport, Company company, StockExchange stockExchange,
			CompanyProfileController companyProfileController) {

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

		EventProfitModifier profitModifier = companyProfileController.getProfitModifier(company);
		QuarterGenerator quarterGenerator = companyProfileController.getQuarterGenerator(company);
		QuarterData currentQuarterData = new QuarterData(0, 0, 0, 0);

		for (int i = 0; i < 4; i++) {
			QuarterPerformanceType quarterEvent = quarterGenerator.getNextEventType();
			if (company.getRemainingPeriodsOfDarkAge() > 0) {
				if (quarterEvent == QuarterPerformanceType.GOOD || quarterEvent == QuarterPerformanceType.GREAT
						|| quarterEvent == QuarterPerformanceType.EXTRAORDINARY) {
					quarterEvent = QuarterPerformanceType.AVERAGE;
				}
			}
			if (company.getRemainingPeriodsOfGoldenAge() > 0) {
				if (quarterEvent == QuarterPerformanceType.POOR || quarterEvent == QuarterPerformanceType.TERRIBLE
						|| quarterEvent == QuarterPerformanceType.CATASTROPHIC) {
					quarterEvent = QuarterPerformanceType.AVERAGE;
				}
			}

			String message = getMessage(quarterEvent);
			Date longTermCompanyDate = getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, i, true);
			currentQuarterData = profitModifier.adjustProfit(quarterEvent, currentQuarterData, company, periodReport, PERIOD_PART_COUNT);
			PeriodQuarter secondQuarterInformation = new PeriodQuarter(company, periodReport, longTermCompanyDate, message, quarterEvent, i);
			secondQuarterInformation.setData(currentQuarterData);
			periodPartInformationDAO.savePeriodPartInformation(secondQuarterInformation);
			periodReport.addPeriodQuarter(secondQuarterInformation);
			generateRumour(periodReport, secondQuarterInformation, i);

		}

		// ///////////////////////////////////////////////
		// Final profit announcement
		// Profit modifier might effect this based on volatility perhaps?
		periodReport.setFinalRevenue(currentQuarterData.getRunningRevenue());
		periodReport.setFinalExpenses(currentQuarterData.getRunningExpenses());
		periodReport.setFinalInterest(currentQuarterData.getRunningInterestPaid());
		periodReport.setFinalProfit(currentQuarterData.getRunningProfit());

		companyPeriodReportDAO.savePeriodReport(periodReport);
	}

	private String getMessage(QuarterPerformanceType eventType) {
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

	private void generateRumour(CompanyPeriodReport periodReport, PeriodQuarter event, int periodPartIndex) {
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
					reputationRequired, message, (positive ? QuarterPerformanceType.GREAT : QuarterPerformanceType.TERRIBLE), event.getAnnouncementType());

			rumourDAO.saveRumour(rumour);
			periodReport.addRumour(rumour);

		}

	}

	/**
	 * Return a date within a period (as defined in the constants file and supplied period report) based on the number
	 * of parts we want to break the period into, the offset and whether we want to randomize how far into the period we
	 * want the event to occur.
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
