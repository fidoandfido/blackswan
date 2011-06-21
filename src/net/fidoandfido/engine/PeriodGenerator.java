package net.fidoandfido.engine;

import java.util.Date;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.ExchangeGroupDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.StockExchangePeriodDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.engine.ExperiencePointGenerator.ExperienceEvent;
import net.fidoandfido.engine.companymodifiers.CompanyModiferFactory;
import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.economicmodfiers.EconomicModifier;
import net.fidoandfido.engine.economicmodfiers.EconomicModifierFactory;
import net.fidoandfido.engine.quarter.QuarterEventGenerator;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.ExchangeGroup;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;
import net.fidoandfido.util.ServerUtil;

import org.apache.log4j.Logger;

public class PeriodGenerator implements Runnable {

	Logger logger = Logger.getLogger(getClass());

	private TraderDAO traderDAO;
	private ShareParcelDAO shareParcelDAO;
	private CompanyDAO companyDAO;
	private StockExchangeDAO stockExchangeDAO;
	private StockExchangePeriodDAO stockExchangePeriodDAO;
	private TraderEventDAO traderEventDAO;
	private CompanyPeriodReportDAO companyPeriodReportDAO;
	private ExchangeGroupDAO exchangeGroupDAO;

	private void initDAOs() {
		traderDAO = new TraderDAO();
		shareParcelDAO = new ShareParcelDAO();
		companyDAO = new CompanyDAO();
		exchangeGroupDAO = new ExchangeGroupDAO();
		stockExchangeDAO = new StockExchangeDAO();
		stockExchangePeriodDAO = new StockExchangePeriodDAO();
		traderEventDAO = new TraderEventDAO();
		companyPeriodReportDAO = new CompanyPeriodReportDAO();
	}

	private final String groupName;
	private final long periodLength;

	private boolean running = true;

	public PeriodGenerator(String groupName) {
		initDAOs();
		this.groupName = groupName;
		HibernateUtil.beginTransaction();
		ExchangeGroup exchangeGroup = exchangeGroupDAO.getExchangeGroupByName(groupName);
		this.periodLength = exchangeGroup.getPeriodLength();
		HibernateUtil.commitTransaction();
	}

	@Override
	public void run() {
		while (running) {

			HibernateUtil.beginTransaction();
			start();
			HibernateUtil.commitTransaction();

			try {
				HibernateUtil.beginTransaction();
				generatePeriod();
				HibernateUtil.commitTransaction();
			} catch (Exception e) {
				logger.error("Exception generating period for exchange group: " + groupName);
				ServerUtil.logError(logger, e);
				HibernateUtil.rollbackTransaction();
			}

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

	/**
	 * Generate a period for the named stock exhange for this generator.
	 * 
	 * This method will handle all of it's own transactions, since the process is quite complex.
	 * 
	 * Basically, a transaction will start, and then the session context will be cleared and flushed at the end of each
	 * company to ensure caches don't get too big.
	 * 
	 * A single transaction will be used to ensure that any exceptions trigger a roll back (rather than some parts
	 * already being committed!)
	 */
	public void generatePeriod() {
		Date currentDate = new Date();
		generatePeriod(currentDate);
	}

	/**
	 * Generate a period starting at a provided date
	 * 
	 * @param currentDate
	 */
	public void generatePeriod(Date currentDate) {
		// Start a period. This will initialise companies reporting.

		logger.info("Generating period reports: Retrieving exchange");
		ExchangeGroup exchangeGroup = exchangeGroupDAO.getExchangeGroupByName(groupName);
		if (exchangeGroup == null) {
			return;
		}
		logger.info("Date: " + currentDate);

		for (StockExchange exchange : exchangeGroup.getExchanges()) {
			// Check if this is before the end date of the current stockExchange
			// period.
			exchange = stockExchangeDAO.getStockExchangeByName(exchange.getName());
			StockExchangePeriod previousPeriod = exchange.getCurrentPeriod();
			if (currentDate.before(previousPeriod.getMinimumEndDate())) {
				// Too soon for a new period!
				logger.info("Too soon to generate a new period for " + exchange.getName() + " in group: " + exchangeGroup.getName() + " (Must be after: "
						+ previousPeriod.getMinimumEndDate() + ")");
				return;
			}
			previousPeriod.close(currentDate);
			stockExchangePeriodDAO.save(previousPeriod);

			Date minimumEndDate = new Date(currentDate.getTime() + exchange.getPeriodLength());

			// Update the stock exchange period.
			StockExchangePeriod currentPeriod = new StockExchangePeriod(previousPeriod, currentDate, minimumEndDate);

			EconomicModifier economicModifier = EconomicModifierFactory.getEconomicModifier(exchange.getEconomicModifierName());
			economicModifier.modifiyExchangePeriod(currentPeriod, previousPeriod);

			exchange.setCurrentPeriod(currentPeriod);
			CompanyModifier companyModifier = CompanyModiferFactory.getCompanyModifier(exchange.getCompanyModifierName());

			// Get the current period for the stock exchange.
			// Create a period event generator...
			QuarterEventGenerator generator = new QuarterEventGenerator();
			Iterable<Company> companyList = companyDAO.getCompaniesByExchange(exchange);

			for (Company company : companyList) {
				company = CompanyDAO.getCompanyById(company.getId());

				if (!company.isTrading()) {
					continue;
				}

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

					// Update the company:
					// Split stocks (if required)
					// Update the company status
					// Update revenue / expense based on company stats modifier
					// Update debt based company stats modifier
					// Close this period off
					// distribute the dividends
					// Finally - save it!
					splitStocks(company);

					companyModifier.updateCompanyTradingStatus(company);
					if (!company.isTrading()) {
						continue;
					}

					companyModifier.modifyCompanyRates(company);
					if (!company.isInsolvent()) {
						companyModifier.modifyCompanyDebts(company);
					}

					generation = currentPeriodReport.getGeneration();
					distributeDividends(currentPeriodReport, currentDate);
					currentPeriodReport.close(currentDate);
					companyPeriodReportDAO.savePeriodReport(currentPeriodReport);
					company.setPreviousPeriodReport(currentPeriodReport);
				}

				CompanyPeriodReport newPeriodReport = new CompanyPeriodReport(company, currentDate, exchange.getPeriodLength(), generation + 1);

				// Calculate the expected return based on the current asset/debt
				// Use company expense rate and apply modifyers based on global and sector economic conditions.
				long primeInterestRateBasisPoints = company.getPrimeInterestRateBasisPoints();
				long expenseRate = company.getExpenseRate() + currentPeriod.getExpenseRateDelta() + currentPeriod.getSectorExpenseDelta(company.getSector());
				long expectedExpenses = expenseRate * company.getAssetValue() / 100;
				long m = company.getRevenueRate() + currentPeriod.getRevenueRateDelta();
				long expectedRevenues = m * company.getAssetValue() / 100;
				long expectedInterest = (company.getDebtValue()) * primeInterestRateBasisPoints / 10000;
				long expectedProfit = expectedRevenues - expectedExpenses - expectedInterest;

				// Set the expected profit data for the year.
				newPeriodReport.setStartingExpectedProfit(expectedProfit);
				newPeriodReport.setStartingExpectedRevenue(expectedRevenues);
				newPeriodReport.setStartingExpectedExpenses(expectedExpenses);
				newPeriodReport.setStartingExpectedInterest(expectedInterest);

				companyPeriodReportDAO.savePeriodReport(newPeriodReport);

				generator.generateQuarters(newPeriodReport, company, exchange);
				company.setCurrentPeriod(newPeriodReport);
				companyDAO.saveCompany(company);
				HibernateUtil.flushAndClearSession();
			}
		}
		return;
	}

	private void splitStocks(Company company) {
		if (company.getLastTradePrice() > company.getStockExchange().getMaxSharePrice()) {
			logger.info("Splitting stocks for company: " + company.getName());
			// Time to split the stock!
			// Rounding not a huge issue - the market will correct the price
			// anyhow :)
			long newPrice = company.getLastTradePrice() / 2;
			company.setLastTradePrice(newPrice);

			long outstandingShares = company.getOutstandingShares() * 2;
			company.setOutstandingShares(outstandingShares);

			companyDAO.saveCompany(company);

			// Now get all share parcels and update the price (and reduce the
			// effective purchase price)
			Iterable<ShareParcel> shareParcels = shareParcelDAO.getHoldingsByCompany(company);
			for (ShareParcel shareParcel : shareParcels) {
				long previousShareCount = shareParcel.getShareCount();
				shareParcel.setShareCount(previousShareCount * 2);
				long previousAveragePrice = shareParcel.getPurchasePrice();
				shareParcel.setPurchasePrice(previousAveragePrice / 2);
				shareParcelDAO.saveShareParcel(shareParcel);
			}

		}
	}

	private void distributeDividends(CompanyPeriodReport currentPeriodReport, Date currentDate) {
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
							// logger.trace("Giving cash: " + payment +
							// " to Trader: " + trader.getName());
							if (!trader.isMarketMaker()) {
								TraderEvent event = new TraderEvent(trader, TraderEvent.DIVIDEND_PAYMENT, currentDate, parcel.getCompany(),
										parcel.getShareCount(), payment, trader.getCash(), trader.getCash() + payment);
								traderEventDAO.saveTraderEvent(event);
							}

							ExperiencePointGenerator generator = new ExperiencePointGenerator();
							generator.addExperiencePoints(trader, ExperienceEvent.DIVIDEND, payment);
							trader.giveCash(payment);
							traderDAO.saveTrader(trader);
						}
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
							logger.debug("Giving cash: " + payment + " to Trader: " + trader.getName());
							if (!trader.isMarketMaker()) {
								TraderEvent event = new TraderEvent(trader, TraderEvent.DIVIDEND_PAYMENT, currentDate, parcel.getCompany(),
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
		companyDAO.saveCompany(company);
	}

	public void start() {
		// Start a period. This will initialise companies reporting.
		logger.info("Generating period reports: Starting exchange group:" + groupName);

		ExchangeGroup exchangeGroup = exchangeGroupDAO.getExchangeGroupByName(groupName);
		if (exchangeGroup == null) {
			return;
		}
		exchangeGroup.setUpdating(true);
	}

	public void finish() {
		// Start a period. This will initialise companies reporting.
		logger.info("Generating period reports: finishing exchange " + groupName);
		ExchangeGroup exchangeGroup = exchangeGroupDAO.getExchangeGroupByName(groupName);
		if (exchangeGroup == null) {
			return;
		}
		exchangeGroup.setUpdating(false);
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
