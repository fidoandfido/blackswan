package net.fidoandfido.engine;

import java.io.IOException;
import java.util.Date;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.ExchangeGroupDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.PeriodMessageDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.StockExchangePeriodDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.dao.TraderMessageDAO;
import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.economicmodfiers.EconomicModifier;
import net.fidoandfido.engine.economicmodfiers.EconomicModifierFactory;
import net.fidoandfido.engine.experience.ExperiencePointGenerator;
import net.fidoandfido.engine.experience.ExperiencePointGenerator.ExperienceEvent;
import net.fidoandfido.engine.quarter.QuarterEventGenerator;
import net.fidoandfido.initialiser.AppInitialiser;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.ExchangeGroup;
import net.fidoandfido.model.PeriodMessage;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;
import net.fidoandfido.model.TraderMessage;
import net.fidoandfido.util.ServerUtil;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

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
	private TraderMessageDAO traderMessageDAO;

	private void initDAOs() {
		traderDAO = new TraderDAO();
		shareParcelDAO = new ShareParcelDAO();
		companyDAO = new CompanyDAO();
		exchangeGroupDAO = new ExchangeGroupDAO();
		stockExchangeDAO = new StockExchangeDAO();
		stockExchangePeriodDAO = new StockExchangePeriodDAO();
		traderEventDAO = new TraderEventDAO();
		companyPeriodReportDAO = new CompanyPeriodReportDAO();
		traderMessageDAO = new TraderMessageDAO();
		periodMessageDAO = new PeriodMessageDAO();
	}

	private final String groupName;
	private final long periodLength;

	private boolean running = true;

	private PeriodMessageDAO periodMessageDAO;

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

		CompanyProfileController companyProfileController = new CompanyProfileController();

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
			StockExchangePeriod currentExchangePeriod = new StockExchangePeriod(previousPeriod, currentDate, minimumEndDate);

			EconomicModifier economicModifier = EconomicModifierFactory.getEconomicModifier(exchange.getEconomicModifierName());
			economicModifier.modifiyExchangePeriod(currentExchangePeriod, previousPeriod);
			economicModifier.modifySectors(currentExchangePeriod, previousPeriod);

			exchange.setCurrentPeriod(currentExchangePeriod);

			// Create company modifier and event generator
			QuarterEventGenerator generator = new QuarterEventGenerator();

			Iterable<Company> companyList = companyDAO.getCompaniesByExchange(exchange);
			for (Company company : companyList) {
				// Refresh our handle (since we may have flushed the hibernate session.)
				company = companyDAO.getCompanyById(company.getId());
				if (!company.isTrading()) {
					continue;
				}
				logger.info("Updating company: " + company.getName());

				if (company.getCompanyStatus().equals(Company.IPO_COMPANY_STATUS)) {
					company.setCompanyStatus(Company.TRADING_COMPANY_STATUS);
					String message = company.getName() + " is now trading on " + exchange.getName() + ".";
					PeriodMessage newCompanyMessage = new PeriodMessage(exchange, exchange.getCurrentPeriod(), company.getSector(), company,
							company.getCurrentPeriod(), message);
					periodMessageDAO.saveMessage(newCompanyMessage);
				}

				// Create a new company report entry..
				long generation = 0;
				CompanyPeriodReport currentPeriodReport = company.getCurrentPeriod();

				if (currentPeriodReport != null) {

					// Update the company:
					// Check if the company is to be dissolved, and do so if required.
					// Split stocks (if required)
					// Update the company trading status
					// Update revenue / expense based on company stats modifier
					// Update debt based company stats modifier
					// Close this period off
					// distribute the dividends
					// Finally - save it!

					if (economicModifier.isCompanyToBeDissolved(company)) {
						dissolveCompany(company, currentDate);
						continue;
					}

					if (company.getLastTradePrice() > company.getStockExchange().getMaxSharePrice()) {
						splitStocks(company, currentPeriodReport, currentDate);
					}

					economicModifier.updateCompanyTradingStatus(company);
					generation = currentPeriodReport.getGeneration();
					distributeDividends(currentPeriodReport, currentDate);
					currentPeriodReport.close(currentDate);
					companyPeriodReportDAO.savePeriodReport(currentPeriodReport);
					company.setPreviousPeriodReport(currentPeriodReport);
				}

				// Update the company profile
				companyProfileController.modifyCompanyProfile(company);

				// Get the appropriate company modifier for this company, and modify the debts and the rates
				CompanyModifier companyModifier = companyProfileController.getCompanyModifer(company);
				if (!company.isInsolvent()) {
					companyModifier.modifyCompanyDebts(company);
				}
				companyModifier.modifyCompanyRates(company);

				CompanyPeriodReport newPeriodReport = new CompanyPeriodReport(company, currentDate, exchange.getPeriodLength(), generation + 1);

				// Calculate the expected return based on the current asset/debt
				// Use company expense rate and apply modifiers based on global and sector economic conditions.
				// Ensure revenue and expenses are always at least 1 %, interest can hit 0.
				// 1. Revenues
				long revenueRate = company.getRevenueRate() + currentExchangePeriod.getRevenueRateDelta()
						+ currentExchangePeriod.getSectorRevenueDelta(company.getSector());
				revenueRate = (revenueRate <= 0 ? 1 : revenueRate);
				long expectedRevenues = revenueRate * company.getAssetValue() / 100;
				// 2. Expenses
				long expenseRate = company.getExpenseRate() + currentExchangePeriod.getExpenseRateDelta()
						+ currentExchangePeriod.getSectorExpenseDelta(company.getSector());
				expenseRate = (expenseRate <= 0 ? 1 : expenseRate);
				long expectedExpenses = expenseRate * company.getAssetValue() / 100;
				// 3. Interest
				long primeInterestRateBasisPoints = company.getPrimeInterestRateBasisPoints();
				primeInterestRateBasisPoints = (primeInterestRateBasisPoints < 0 ? 0 : primeInterestRateBasisPoints);
				long expectedInterest = (company.getDebtValue()) * primeInterestRateBasisPoints / 10000;
				// 4. PROFIT!!!
				long expectedProfit = expectedRevenues - expectedExpenses - expectedInterest;

				// Set the expected profit data for the year.
				newPeriodReport.setStartingExpectedProfit(expectedProfit);
				newPeriodReport.setStartingExpectedRevenue(expectedRevenues);
				newPeriodReport.setStartingExpectedExpenses(expectedExpenses);
				newPeriodReport.setStartingExpectedInterest(expectedInterest);

				companyPeriodReportDAO.savePeriodReport(newPeriodReport);
				generator.generateQuarters(newPeriodReport, company, exchange, companyProfileController);
				company.setCurrentPeriod(newPeriodReport);
				companyDAO.saveCompany(company);
				HibernateUtil.flushAndClearSession();
			}

			// Now create a new company for the exchange (if that is applicable)
			// Since we have flushed and cleared the session, best to get a new handle on our exchange :(
			exchange = stockExchangeDAO.getStockExchangeById(exchange.getId());
			int currentTradingCount = stockExchangeDAO.getTradingCompaniesCountForExchange(exchange);
			if (currentTradingCount < exchange.getMaxTradingCompanyCount()) {
				if (economicModifier.newCompanyToBeFounded(currentExchangePeriod)) {
					createNewCompany(exchange);
				}
			}

		}
		return;
	}

	private void dissolveCompany(Company company, Date currentDate) {
		// TODO Auto-generated method stub
		logger.info("Setting company status of Company: " + company.getName() + " = ---" + Company.DISSOLVED_COMPANY_STATUS);
		company.setCompanyStatus(Company.DISSOLVED_COMPANY_STATUS);
		long shareBookValue = company.getShareBookValue();
		if (shareBookValue < 0) {
			shareBookValue = 0;
		}

		company.setAssetValue(0);
		company.setDebtValue(0);
		company.setAlwaysPayDividend(false);
		company.setExpenseRate(0);
		company.setRevenueRate(0);
		company.setOutstandingShares(0);
		company.setTrading(false);
		Date date = new Date();

		StockExchange exchange = company.getStockExchange();

		exchange.incrementCompanyCount(-1);
		stockExchangeDAO.saveStockExchange(exchange);

		String announcemnt = exchange.getName() + " has received a formal declaration that " + company.getName() + " has been dissolved."
				+ " All assets for this company have been liquidated, and it's debts repaid. Any remaining capital has been paid out"
				+ " to remaining shareholders.";
		PeriodMessage newCompanyMessage = new PeriodMessage(exchange, exchange.getCurrentPeriod(), company.getSector(), company, company.getCurrentPeriod(),
				announcemnt);
		periodMessageDAO.saveMessage(newCompanyMessage);
		Iterable<ShareParcel> shareParcels = shareParcelDAO.getHoldingsByCompany(company);
		for (ShareParcel shareParcel : shareParcels) {
			Trader trader = shareParcel.getTrader();
			long shareCount = shareParcel.getShareCount();
			long amount = shareBookValue * shareParcel.getShareCount();
			if (!trader.isAITrader()) {
				TraderEvent event = new TraderEvent(trader, TraderEvent.COMPANY_DISSOLVED, date, company, shareCount, amount, trader.getCash(),
						trader.getCash() + amount);
				traderEventDAO.saveTraderEvent(event);
				trader.giveCash(amount);
				// Send them a message to let them know the company dissovled.
				// Send them a message.
				TraderMessage message = new TraderMessage(trader, currentDate, "Company dissolved - " + company.getName(), company.getName()
						+ " has been dissolved. After the debts were repaid, any remaining assets have been distributed liquidated "
						+ "and the proceeds distribute to shareholders. Your portfolio has been adjusted accordingly.");
				traderMessageDAO.saveMessage(message);
			}
			shareParcelDAO.deleteShareParcel(shareParcel);
		}
		return;
	}

	public void createNewCompany(StockExchange exchange) {
		// SHOULD BE CREATING A NEW COMPANY NOW!!!
		AppInitialiser initialiser = new AppInitialiser();
		try {
			Company company = initialiser.createNewCompany(exchange, companyDAO.getAllCompanyCodes(), companyDAO.getAllCompanyNames(),
					traderDAO.getMarketMaker());
			exchange.incrementCompanyCount(1);
			stockExchangeDAO.saveStockExchange(exchange);
			companyDAO.saveCompany(company);
			// This deserves an announcement!
			String message = company.getName() + " has announced it's intention to list on " + exchange.getName() + ". Orders for shares may be placed"
					+ " this period, shares will begin trading normally at the commencement of the next financial period.";
			PeriodMessage newCompanyMessage = new PeriodMessage(exchange, exchange.getCurrentPeriod(), company.getSector(), company,
					company.getCurrentPeriod(), message);
			periodMessageDAO.saveMessage(newCompanyMessage);
		} catch (SAXException e) {
			logger.error(e.getMessage());
			ServerUtil.logError(logger, e);
		} catch (IOException e) {
			logger.error(e.getMessage());
			ServerUtil.logError(logger, e);
		}
	}

	private void splitStocks(Company company, CompanyPeriodReport currentPeriodReport, Date currentDate) {
		logger.info("Splitting stocks for company: " + company.getName());
		// Time to split the stock!
		// Rounding not a huge issue - the market will correct the price
		// anyhow
		long newPrice = company.getLastTradePrice() / 2;
		company.setLastTradePrice(newPrice);

		long outstandingShares = company.getOutstandingShares() * 2;
		company.setOutstandingShares(outstandingShares);

		currentPeriodReport.setStockSplit(true);
		companyDAO.saveCompany(company);

		String message = company.getName() + " stock has split 2 for 1!";
		PeriodMessage newCompanyMessage = new PeriodMessage(company.getSector(), company, company.getCurrentPeriod(), message);
		periodMessageDAO.saveMessage(newCompanyMessage);

		// Now get all share parcels and update the price (and reduce the
		// effective purchase price)
		Iterable<ShareParcel> shareParcels = shareParcelDAO.getHoldingsByCompany(company);
		for (ShareParcel shareParcel : shareParcels) {
			long previousShareCount = shareParcel.getShareCount();
			shareParcel.setShareCount(previousShareCount * 2);
			long previousAveragePrice = shareParcel.getPurchasePrice();
			shareParcel.setPurchasePrice(previousAveragePrice / 2);
			shareParcelDAO.saveShareParcel(shareParcel);
			Trader owner = shareParcel.getTrader();
			if (!owner.isAITrader()) {
				// Send them a message.
				TraderMessage traderMessage = new TraderMessage(owner, currentDate, "Stock Split - " + company.getName(), "Shares in " + company.getName()
						+ " have split 2 for 1. Your portfolio has been adjusted accordingly.");
				traderMessageDAO.saveMessage(traderMessage);
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
