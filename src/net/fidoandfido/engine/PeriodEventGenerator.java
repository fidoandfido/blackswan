package net.fidoandfido.engine;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.engine.EventGenerators.EventGeneratorFactory;
import net.fidoandfido.engine.profitmodifers.ConstantModifier;
import net.fidoandfido.engine.profitmodifers.LinearProfitModifier;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.SectorNewsEvent;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.Constants;
import net.fidoandfido.util.Constants.EventType;

public class PeriodEventGenerator {

	// To be used to get the event type.\
	private Random randomTime = new Random();

	// These will be thrown away!
	Map<String, SectorNewsEvent> sectorEventMap = new HashMap<String, SectorNewsEvent>();

	private static final int PERIOD_PART_COUNT = 5;
	private static final int LONG_SECTOR_PART_INDEX = 0;
	private static final int LONG_COMPANY_PART_INDEX = 1;
	private static final int SHORT_SECTOR_PART_INDEX = 2;
	private static final int SHORT_COMPANY_PART_INDEX = 3;

	public static final String LONG_TERM_SECTOR = "LONG_TERM_SECTOR";
	public static final String LONG_TERM_COMPANY = "LONG_TERM_COMPANY";
	public static final String SHORT_TERM_SECTOR = "SHORT_TERM_SECTOR";
	public static final String SHORT_TERM_COMPANY = "SHORT_TERM_COMPANY";

	// private static final int DIVIDEND_ANNOUNCED = 4;

	/**
	 * The following events occur during the sector report:
	 * <ol>
	 * <li>Initial state</li>
	 * <li>Long term sector forecast</li>
	 * <li>Long term company forecast</li>
	 * <li>Short term sector outlook</li>
	 * <li>Short term company outlook</li>
	 * <li>Profit announcement, dividend payout (on period close).</li>
	 * </ol>
	 * 
	 * @param periodReport
	 * @param company
	 */
	public void generateEvents(CompanyPeriodReport periodReport, Company company, StockExchange stockExchange) {
		// Create a long term sector outlook...
		String sectorName = company.getSector();
		ProfitModifier profitModifer = getProfitModifer(company.getProfitModifierName());
		EventGenerator eventGenerator = EventGeneratorFactory.getGeneratorByName(stockExchange.getName(), stockExchange.getEventGeneratorName());

		long currentProfit = periodReport.getStartingExpectedProfit();

		SectorNewsEvent sectorEvent = null;
		if (sectorEventMap.containsKey(sectorName)) {
			sectorEvent = sectorEventMap.get(sectorName);
		} else {
			// Get the event type...
			sectorEvent = new SectorNewsEvent(sectorName);

			// Generate the long term outlook
			sectorEvent.setLongEventDate(getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, LONG_SECTOR_PART_INDEX, true));
			// TODO - generate appropriate messages and event
			sectorEvent.setLongEventType(eventGenerator.getNextEventType());
			sectorEvent.setLongMessage("Long term sector outlook --> " + sectorEvent.getLongEventType());

			// Generate the short term outlook
			sectorEvent.setShortEventDate(getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, SHORT_SECTOR_PART_INDEX, true));
			// TODO - generate appropriate message and event
			sectorEvent.setShortEventType(eventGenerator.getNextEventType());
			sectorEvent.setShortMessage("Short term sector outlook --> " + sectorEvent.getShortEventType());

			sectorEventMap.put(sectorName, sectorEvent);
		}
		// Now we have the 'sector event' object - populate the period record appropriately.

		// //////////////////////////////////////////////
		// Long term sector outlook.
		currentProfit = profitModifer.adjustProfit(sectorEvent.getLongEventType(), currentProfit);
		PeriodEvent longTermSector = new PeriodEvent(company, periodReport, sectorEvent.getLongEventDate(), sectorEvent.getLongMessage(),
				sectorEvent.getLongEventType(), currentProfit, LONG_TERM_SECTOR);

		PeriodPartInformationDAO.savePeriodPartInformation(longTermSector);
		periodReport.addPeriodEvent(longTermSector);

		// //////////////////////////////////////////////
		// Long term company outlook
		EventType longTermCompanyEventType = eventGenerator.getNextEventType();
		String longTermCompanyMessage = "Long term company outlook --> " + longTermCompanyEventType;
		Date longTermCompanyDate = getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, LONG_COMPANY_PART_INDEX, true);
		currentProfit = profitModifer.adjustProfit(longTermCompanyEventType, currentProfit);

		PeriodEvent longTermCompanyInformation = new PeriodEvent(company, periodReport, longTermCompanyDate, longTermCompanyMessage, longTermCompanyEventType,
				currentProfit, LONG_TERM_COMPANY);

		PeriodPartInformationDAO.savePeriodPartInformation(longTermCompanyInformation);
		periodReport.addPeriodEvent(longTermCompanyInformation);

		// //////////////////////////////////////////////
		// Short term sector outlook
		currentProfit = profitModifer.adjustProfit(sectorEvent.getShortEventType(), currentProfit);
		PeriodEvent shortTermSectorInformation = new PeriodEvent(company, periodReport, sectorEvent.getShortEventDate(), sectorEvent.getShortMessage(),
				sectorEvent.getShortEventType(), currentProfit, SHORT_TERM_SECTOR);

		PeriodPartInformationDAO.savePeriodPartInformation(shortTermSectorInformation);
		periodReport.addPeriodEvent(shortTermSectorInformation);

		// //////////////////////////////////////////////
		// Short term Company outlook
		Date shortTermCompanyDate = getDateWithinPeriod(periodReport, PERIOD_PART_COUNT, SHORT_COMPANY_PART_INDEX, true);
		EventType shortTermCompanyEventType = eventGenerator.getNextEventType();
		String shortTermCompanyMessage = "Short term company outlook --> " + shortTermCompanyEventType;
		currentProfit = profitModifer.adjustProfit(shortTermCompanyEventType, currentProfit);

		PeriodEvent shortTermCompanyInformation = new PeriodEvent(company, periodReport, shortTermCompanyDate, shortTermCompanyMessage,
				shortTermCompanyEventType, currentProfit, SHORT_TERM_COMPANY);

		PeriodPartInformationDAO.savePeriodPartInformation(shortTermCompanyInformation);
		periodReport.addPeriodEvent(shortTermCompanyInformation);

		// ///////////////////////////////////////////////
		// Final profit announcement
		// Profit modifier might effect this based on volatility perhaps?
		periodReport.setFinalProfit(currentProfit);
		CompanyPeriodReportDAO.savePeriodReport(periodReport);

	}

	private ProfitModifier getProfitModifer(String profitModifierName) {
		if (LinearProfitModifier.NAME.equals(profitModifierName)) {
			return new LinearProfitModifier();
		}
		return new ConstantModifier();
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
		long periodPartLength = Constants.DEFAULT_PERIOD_LENGTH_IN_MILLIS / periodPartCount;
		long periodPartOffSet = periodPartLength * periodPartIndex;
		long periodPartStartTimeDelta = 0;
		if (randomizeStartTime) {
			periodPartStartTimeDelta = (periodPartLength * randomTime.nextInt(100)) / 100;
		}
		return new Date(date.getTime() + periodPartOffSet + periodPartStartTimeDelta);
	}

}
