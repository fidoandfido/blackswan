package net.fidoandfido.engine.event;

import java.util.Date;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.StockExchangePeriodDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.engine.companymodifiers.CompanyModiferFactory;
import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.economicmodfiers.EconomicModifier;
import net.fidoandfido.engine.economicmodfiers.EconomicModifierFactory;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;

import org.apache.log4j.Logger;

public class PeriodGenerator implements Runnable {

	Logger logger = Logger.getLogger(getClass());

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		PeriodGenerator generator = new PeriodGenerator("ASX");
		HibernateUtil.beginTransaction();
		generator.generatePeriod();
		HibernateUtil.commitTransaction();
	}

	private TraderDAO traderDAO;
	private ShareParcelDAO shareParcelDAO;
	private CompanyDAO companyDAO;
	private StockExchangeDAO stockExchangeDAO;
	private StockExchangePeriodDAO stockExchangePeriodDAO;
	private TraderEventDAO traderEventDAO;
	private CompanyPeriodReportDAO companyPeriodReportDAO;

	private void initDAOs() {
		traderDAO = new TraderDAO();
		shareParcelDAO = new ShareParcelDAO();
		companyDAO = new CompanyDAO();
		stockExchangeDAO = new StockExchangeDAO();
		stockExchangePeriodDAO = new StockExchangePeriodDAO();
		traderEventDAO = new TraderEventDAO();
		companyPeriodReportDAO = new CompanyPeriodReportDAO();
	}

	private final String exchangeName;
	private final long periodLength;

	private boolean running = true;

	public PeriodGenerator(String exchangeName) {
		initDAOs();
		this.exchangeName = exchangeName;
		HibernateUtil.beginTransaction();
		StockExchange exchange = stockExchangeDAO.getStockExchangeByName(exchangeName);
		this.periodLength = exchange.getCompanyPeriodLength();
		HibernateUtil.commitTransaction();
	}

	@Override
	public void run() {
		while (running) {

			HibernateUtil.beginTransaction();
			start();
			HibernateUtil.commitTransaction();

			HibernateUtil.beginTransaction();
			generatePeriod();
			HibernateUtil.commitTransaction();

			HibernateUtil.beginTransaction();
			finish();
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

	public void generatePeriod() {
		// Start a period. This will initialise companies reporting.
		logger.info("Generating period reports: Retrieving exchange");
		StockExchange exchange = stockExchangeDAO.getStockExchangeByName(exchangeName);
		if (exchange == null) {
			return;
		}
		Date currentDate = new Date();
		logger.info("Date: " + currentDate);

		// Check if this is before the end date of the current stockExchange
		// period.
		StockExchangePeriod previousPeriod = exchange.getCurrentPeriod();
		if (currentDate.before(previousPeriod.getMinimumEndDate())) {
			// Too soon for a new period!
			logger.info("Too soon to generate a new period for " + exchangeName);
			return;
		}
		previousPeriod.close(currentDate);
		stockExchangePeriodDAO.save(previousPeriod);

		Date minimumEndDate = new Date(currentDate.getTime() + exchange.getCompanyPeriodLength());

		// Update the stock exchange period.
		StockExchangePeriod currentPeriod = new StockExchangePeriod(previousPeriod, currentDate, minimumEndDate);

		EconomicModifier economicModifier = EconomicModifierFactory.getEconomicModifier(exchange.getEconomicModifierName());
		economicModifier.modifiyExchangePeriod(currentPeriod, previousPeriod);

		exchange.setCurrentPeriod(currentPeriod);

		CompanyModifier companyModifier = CompanyModiferFactory.getCompanyModifier(exchange.getCompanyModifierName());

		// Get the current period for the stock exchange.
		// Create a period event generator...
		PeriodEventGenerator generator = new PeriodEventGenerator();
		Iterable<Company> companyList = companyDAO.getCompaniesByExchange(exchange);
		for (Company company : companyList) {
			logger.info("Updating company: " + company.getName());
			// Create a new company report entry..
			CompanyPeriodReport currentPeriodReport = company.getCurrentPeriod();

			long generation = 0;

			if (currentPeriodReport != null) {
				if (currentPeriodReport.getMinimumEndDate().after(currentDate)) {
					// Can't end a period report before now...
					// Do something about that...
					logger.info("Too soon for company: " + company.getName() + " end date must be after: " + currentPeriodReport.getMinimumEndDate());
					continue;
				}
				// Close this period off, distribute the dividends and save it.
				generation = currentPeriodReport.getGeneration();
				distributeDividends(currentPeriodReport);
				currentPeriodReport.close(currentDate);
				companyPeriodReportDAO.savePeriodReport(currentPeriodReport);
				company.setPreviousPeriodReport(currentPeriodReport);
			}

			// Update the company based on the exchange provided company stat
			// modifier
			companyModifier.modifyCompanyRates(company);
			companyModifier.modifyCompanyDebts(company);

			CompanyPeriodReport newPeriodReport = new CompanyPeriodReport(company, currentDate, exchange.getCompanyPeriodLength(), generation + 1);

			// Calculate the expected return based on the current asset/debt and
			// so on.
			long primeInterestRateBasisPoints = company.getPrimeInterestRateBasisPoints();
			long expectedExpenses = (company.getExpenseRate() + currentPeriod.getExpenseRateDelta()) * company.getAssetValue() / 100;
			long expectedRevenues = (company.getRevenueRate() + currentPeriod.getRevenueRateDelta()) * company.getAssetValue() / 100;
			long expectedInterest = (company.getDebtValue()) * primeInterestRateBasisPoints / 10000;
			long expectedProfit = expectedRevenues - expectedExpenses - expectedInterest;

			// Set the expected profit data for the year.
			newPeriodReport.setStartingExpectedProfit(expectedProfit);
			newPeriodReport.setStartingExpectedRevenue(expectedRevenues);
			newPeriodReport.setStartingExpectedExpenses(expectedExpenses);
			newPeriodReport.setStartingExpectedInterest(expectedInterest);

			companyPeriodReportDAO.savePeriodReport(newPeriodReport);

			generator.generateEvents(newPeriodReport, company, exchange);
			company.setCurrentPeriod(newPeriodReport);
			companyDAO.saveCompany(company);
		}
		return;
	}

	private void distributeDividends(CompanyPeriodReport currentPeriodReport) {
		Company company = currentPeriodReport.getCompany();
		logger.info("Distributing dividends for company: " + company.getName());

		// For each company, get the profit.
		long profit = currentPeriodReport.getFinalProfit();
		logger.info("Profit: " + profit);

		company.setPreviousDividend(0);

		if (company.isNeverPayDividend()) {
			// Simple - just keep all the profit!
			company.incrementAssetValue(profit);
		} else {
			// Distribute the profit (or loss....)
			if (profit > 0) {
				// We have a profit, and we have to distribute!
				long companyDividendRate = company.getDividendRate();
				if (companyDividendRate > 0) {
					long profitsToDistribute = profit * company.getDividendRate() / 100;
					long profitsToKeep = profit - profitsToDistribute;
					long dividend = profitsToDistribute / company.getOutstandingShares();
					logger.info("Default dividend per share:" + dividend);
					if (company.isAlwaysPayDividend() && dividend < company.getMinimumDividend()) {
						// Update the dividend (take it out of the profits to
						// keep!)
						dividend = company.getMinimumDividend();
						profitsToDistribute = (dividend * company.getOutstandingShares());
						// Profits to keep may now be negative!
						profitsToKeep = profit - profitsToDistribute;
					}

					company.incrementAssetValue(profitsToKeep);
					company.setPreviousDividend(dividend);
					// Get the dividend (in cents!)
					if (dividend == 0) {
						// Can't pay a 0 dividend! Give back the money
						company.incrementAssetValue(profitsToDistribute);
					} else {
						Iterable<ShareParcel> parcels = shareParcelDAO.getHoldingsByCompany(company);
						for (ShareParcel parcel : parcels) {
							Trader trader = parcel.getTrader();
							long payment = dividend * parcel.getShareCount();
							logger.info("Giving cash: " + payment + " to Trader: " + trader.getName());
							if (!trader.isMarketMaker()) {
								TraderEvent event = new TraderEvent(trader, TraderEvent.DIVIDEND_PAYMENT, new Date(), parcel.getCompany(),
										parcel.getShareCount(), payment, trader.getCash(), trader.getCash() + payment);
								traderEventDAO.saveTraderEvent(event);
							}
							trader.giveCash(payment);
							traderDAO.saveTrader(trader);
						}
					}
				} else {
					// We lost money :( (profit will be negative)
					company.incrementAssetValue(profit);
					if (company.isAlwaysPayDividend()) {
						// D'oh - Still pay a dividend
						if (company.getMinimumDividend() > 0) {
							long dividend = company.getMinimumDividend();
							company.setPreviousDividend(dividend);
							company.incrementAssetValue(company.getOutstandingShares() * dividend * -1);
							Iterable<ShareParcel> parcels = shareParcelDAO.getHoldingsByCompany(company);
							for (ShareParcel parcel : parcels) {
								Trader trader = parcel.getTrader();
								long payment = dividend * parcel.getShareCount();
								logger.info("Giving cash: " + payment + " to Trader: " + trader.getName());
								if (!trader.isMarketMaker()) {
									TraderEvent event = new TraderEvent(trader, TraderEvent.DIVIDEND_PAYMENT, new Date(), parcel.getCompany(),
											parcel.getShareCount(), payment, trader.getCash(), trader.getCash() + payment);
									traderEventDAO.saveTraderEvent(event);
								}
								trader.giveCash(payment);
								traderDAO.saveTrader(trader);
							}
						}
					}
				}
			}
		}
		companyDAO.saveCompany(company);
	}

	public void start() {
		// Start a period. This will initialise companies reporting.
		logger.info("Generating period reports: Retrieving exchange");
		StockExchange exchange = stockExchangeDAO.getStockExchangeByName(exchangeName);
		if (exchange == null) {
			return;
		}
		exchange.setUpdating(true);
	}

	public void finish() {
		// Start a period. This will initialise companies reporting.
		logger.info("Generating period reports: Retrieving exchange");
		StockExchange exchange = stockExchangeDAO.getStockExchangeByName(exchangeName);
		if (exchange == null) {
			return;
		}
		exchange.setUpdating(false);
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
