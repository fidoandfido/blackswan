package net.fidoandfido.engine;

import java.util.Date;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;

import org.apache.log4j.Logger;

public class PeriodGenerator implements Runnable {

	Logger logger = Logger.getLogger(getClass());

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		PeriodGenerator generator = new PeriodGenerator("ASX");
		HibernateUtil.beginTransaction();
		generator.generatePeriodReports();
		HibernateUtil.commitTransaction();
	}

	private final String exchangeName;
	private final long periodLength;

	private boolean running = true;

	public PeriodGenerator(String exchangeName) {
		this.exchangeName = exchangeName;
		HibernateUtil.beginTransaction();
		StockExchange exchange = StockExchangeDAO
				.getStockExchangeByName(exchangeName);
		this.periodLength = exchange.getCompanyPeriodLength();
		HibernateUtil.commitTransaction();
	}

	@Override
	public void run() {
		while (running) {
			HibernateUtil.beginTransaction();
			generatePeriodReports();
			HibernateUtil.commitTransaction();
			synchronized (this) {
				try {
					wait(periodLength);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error("INTERRUPTED!!!");
					break;
				}
			}

		}
	}

	public void generatePeriodReports() {
		// Start a period. This will initialise companies reporting.
		logger.info("Generating period reports: Retrieving exchange");
		StockExchange exchange = StockExchangeDAO
				.getStockExchangeByName(exchangeName);
		if (exchange == null) {
			return;
		}
		Date currentDate = new Date();
		// Create a period event generator...
		PeriodEventGenerator generator = new PeriodEventGenerator();
		Iterable<Company> companyList = CompanyDAO
				.getCompaniesByExchange(exchange);
		for (Company company : companyList) {
			logger.info("Updating company: " + company.getName());
			// Create a new company report entry. Somehow...
			CompanyPeriodReport currentPeriodReport = company
					.getCurrentPeriod();

			long expectedProfit = 0;
			long generation = 0;
			if (currentPeriodReport != null) {
				if (currentPeriodReport.getMinimumEndDate().after(currentDate)) {
					// Can't end a period report before now...
					// Do something about that...
					logger.info("Too soon for company: " + company.getName());
					continue;
				}

				// Close this period off, distribute the dividends and save it.
				generation = currentPeriodReport.getGeneration();
				distributeDividends(currentPeriodReport);
				currentPeriodReport.close(currentDate);
				CompanyPeriodReportDAO.savePeriodReport(currentPeriodReport);
				expectedProfit = (currentPeriodReport.getFinalProfit() + company
						.getCapitalisation() * 5 / 100) / 2;
			} else {
				expectedProfit = company.getCapitalisation() * 5 / 100;
			}
			CompanyPeriodReport newPeriodReport = new CompanyPeriodReport(
					company, expectedProfit, currentDate,
					exchange.getCompanyPeriodLength(), generation + 1);
			CompanyPeriodReportDAO.savePeriodReport(newPeriodReport);
			generator.generateEvents(newPeriodReport, company, exchange);
			company.setCurrentPeriod(newPeriodReport);
			CompanyDAO.saveCompany(company);
		}
		return;
	}

	private void distributeDividends(CompanyPeriodReport currentPeriodReport) {
		Company company = currentPeriodReport.getCompany();
		logger.info("Distributing dividends for company: " + company.getName());

		// For each company, get the profit.
		long profit = currentPeriodReport.getFinalProfit();
		logger.info("Profit: " + profit);

		if (profit > 0) {
			// Distribute the profit.
			// 25% will go to the company, the rest will be dividend.
			company.incrementAssetValue(profit / 4);
			// Get the dividend (in cents!)
			long dividend = (profit / 4) * 3;
			dividend = dividend / company.getOutstandingShares();
			logger.info("Dividend per share:" + dividend);
			Iterable<ShareParcel> parcels = ShareParcelDAO
					.getHoldingsByCompany(company);
			for (ShareParcel parcel : parcels) {
				Trader trader = parcel.getTrader();
				long payment = dividend * parcel.getShareCount();
				logger.info("Giving cash: " + payment + " to Trader: "
						+ trader.getName());
				if (!trader.isMarketMaker()) {
					TraderEvent event = new TraderEvent(trader,
							TraderEvent.DIVIDEND_PAYMENT, new Date(),
							parcel.getCompany(), parcel.getShareCount(),
							payment, trader.getCash(), trader.getCash()
									+ payment);
					TraderEventDAO.saveTraderEvent(event);
				}
				trader.giveCash(payment);
				TraderDAO.saveTrader(trader);
			}
			CompanyDAO.saveCompany(company);
		}
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
