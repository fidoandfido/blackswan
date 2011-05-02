package net.fidoandfido.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.fidoandfido.dao.AppStatusDAO;
import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ReputationItemDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.UserDAO;
import net.fidoandfido.engine.event.PeriodEventGenerator;
import net.fidoandfido.initialiser.CompanyNameBody;
import net.fidoandfido.initialiser.CompanyNameParser;
import net.fidoandfido.initialiser.CompanyNamePrefix;
import net.fidoandfido.initialiser.CompanyNameSuffix;
import net.fidoandfido.initialiser.ExchangeParser;
import net.fidoandfido.initialiser.ItemParser;
import net.fidoandfido.initialiser.TraderParser;
import net.fidoandfido.model.AppStatus;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.ReputationItem;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.User;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class AppInitialiser {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final int CODE_LENGTH = 4;
	// 50 k (including cents)
	public static final long TRADER_START_CASH = 5000000;
	// ten mil (including cents)
	public static final long MARKET_MAKER_START_CASH = 1000000000;

	public static final int AIS_TO_GET_SHARES = 20;

	/**
	 * Number of instances of each trader strategy to create
	 */
	private static final int TRADER_STRATEGY_INSTANCE_COUNT = 10;
	private static final String MARKET_MAKER_NAME = "Market Maker";

	private Map<String, StockExchange> exchangeMap = new HashMap<String, StockExchange>();
	private Map<String, Trader> traderMap = new HashMap<String, Trader>();

	private TraderDAO traderDAO;
	private ShareParcelDAO shareParcelDAO;
	private CompanyDAO companyDAO;
	private OrderDAO orderDAO;
	private CompanyPeriodReportDAO companyPeriodReportDAO;
	private StockExchangeDAO stockExchangeDAO;
	private AppStatusDAO appStatusDAO;
	private UserDAO userDAO;
	private ReputationItemDAO reputationItemDAO;

	private void initDAOs() {
		traderDAO = new TraderDAO();
		shareParcelDAO = new ShareParcelDAO();
		companyDAO = new CompanyDAO();
		orderDAO = new OrderDAO();
		stockExchangeDAO = new StockExchangeDAO();
		appStatusDAO = new AppStatusDAO();
		userDAO = new UserDAO();
		reputationItemDAO = new ReputationItemDAO();
		companyPeriodReportDAO = new CompanyPeriodReportDAO();
	}

	public static void main(String argv[]) {
		System.out.println("Initialising application!");
		HibernateUtil.connectToDB();
		System.out.println("Connected to database, beginning initislisation.");
		AppInitialiser appInitialiser = new AppInitialiser();
		HibernateUtil.beginTransaction();
		try {
			appInitialiser.initApp();
			System.out.println("Initialisation complete, committing transaction.");
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			e.printStackTrace();
		}
		HibernateUtil.beginTransaction();
		AppDataLister appDataLister = new AppDataLister();
		appDataLister.writeData();
		HibernateUtil.commitTransaction();
	}

	public AppInitialiser() {
		// Nothing to do here.
		initDAOs();
	}

	/**
	 * Initialise the application. This whole unit of work should be unertaken
	 * within a transaction.
	 * 
	 * @throws Exception
	 */
	public void initApp() throws Exception {
		if (appNotInited()) {
			System.out.println("Creating and saving Users");
			createAndSaveUsers();
			System.out.println("Creating and saving traders.");
			createAndSaveTraders();
			System.out.println("Creating and saving reputation items.");
			createAndSaveItems();
			System.out.println("Creating and saving exchanges.");
			createAndSaveExchanges();
			System.out.println("Creating and saving companies.");
			createAndSaveCompanies();
			updateStatus();
		} else {
			System.out.println("App already initialised!");
		}
	}

	private void updateStatus() {
		AppStatus status = appStatusDAO.getStatus();
		status.setStatus(AppStatus.INITIALISED);
		appStatusDAO.saveStatus(status);
	}

	private boolean appNotInited() {
		AppStatus status = appStatusDAO.getStatus();
		if (AppStatus.INITIALISED.equals(status.getStatus())) {
			return false;
		}
		return true;
	}

	private void createAndSaveUsers() {
		User andy = new User("andy", "andy", true);
		userDAO.saveUser(andy);
		User asdf = new User("foo", "foo", false);
		userDAO.saveUser(asdf);
	}

	private void createAndSaveTraders() throws IOException, SAXException {

		XMLReader reader = XMLReaderFactory.createXMLReader();
		InputSource src = new InputSource(this.getClass().getResourceAsStream("/initdata.xml"));
		TraderParser traderParser = new TraderParser(TRADER_STRATEGY_INSTANCE_COUNT);
		reader.setContentHandler(traderParser);
		reader.parse(src);

		marketMakerTrader = new Trader(MARKET_MAKER_NAME, MARKET_MAKER_START_CASH, true, true);
		traderDAO.saveTrader(marketMakerTrader);

		// Now create the traders from the XML
		for (Trader trader : traderParser.traderList) {
			String name = trader.getName();
			if (!traderMap.containsKey(name)) {
				traderDAO.saveTrader(trader);
				traderMap.put(name, trader);
			}
		}
	}

	private void createAndSaveItems() throws IOException, SAXException {

		XMLReader reader = XMLReaderFactory.createXMLReader();
		InputSource src = new InputSource(this.getClass().getResourceAsStream("/initdata.xml"));
		ItemParser itemParser = new ItemParser();
		reader.setContentHandler(itemParser);
		reader.parse(src);
		for (ReputationItem item : itemParser.itemList) {
			reputationItemDAO.saveItem(item);
		}

	}

	private void createAndSaveExchanges() throws IOException, SAXException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		ExchangeParser exchangeParser = new ExchangeParser();
		reader.setContentHandler(exchangeParser);
		InputSource src = new InputSource(this.getClass().getResourceAsStream("/initdata.xml"));
		reader.parse(src);
		for (StockExchange exchange : exchangeParser.exchangeList) {
			String name = exchange.getName();
			if (!exchangeMap.containsKey(name)) {
				Date periodStartDate = new Date();
				Date endDate = new Date(periodStartDate.getTime() + exchange.getCompanyPeriodLength());
				StockExchangePeriod firstPeriod = new StockExchangePeriod(exchange, periodStartDate, endDate, 0, exchange.getPrimeInterestRateBasisPoints(), 0,
						0, StockExchangePeriod.NEUTRAL_CONDITIONS);
				exchange.setCurrentPeriod(firstPeriod);
				stockExchangeDAO.saveStockExchange(exchange);
				exchangeMap.put(name, exchange);
			}
		}
	}

	// Seeded!
	Random aiSelectorRandom = new Random(17);

	private void createAndSaveCompanies() throws SAXException, IOException {
		// Initialise our company lists...
		initCompanyGenerator();

		Date date = new Date();
		PeriodEventGenerator generator = new PeriodEventGenerator();

		for (StockExchange exchange : exchangeMap.values()) {

			for (int i = 0; i < exchange.getCompanyCount(); i++) {
				Company company = getNewCompany();
				company.setStockExchange(exchange);
				companyDAO.saveCompany(company);

				CompanyPeriodReport periodReport = new CompanyPeriodReport(company, date, exchange.getCompanyPeriodLength(), 0);

				// Set up the initial profit for the first period report
				setInitialProft(periodReport, company);

				companyPeriodReportDAO.savePeriodReport(periodReport);

				generator.generateEvents(periodReport, company, exchange);

				company.setCurrentPeriod(periodReport);
				companyDAO.saveCompany(company);

				long currentShareCount = company.getOutstandingShares();
				// int traderCount = traderMap.size();
				// The market maker will get half of the shares to start off
				// with, but will not
				// be trading to make money, so eventually will not have many...
				// hopefully!
				long marketMakerCount = currentShareCount / 2;

				ShareParcel mmShareParcel = new ShareParcel(marketMakerTrader, marketMakerCount, company);
				shareParcelDAO.saveShareParcel(mmShareParcel);

				// To vary the portfolio, we will only be giving the shares to a
				// small number of AIs
				//
				// 1 Trader is the market maker.
				long remainingShares = currentShareCount / 2;

				// We will distribute this to 1 / 5th of the available traders.
				long aiShareCount = remainingShares / AIS_TO_GET_SHARES;

				List<Trader> traderList = new ArrayList<Trader>(traderMap.values());
				for (int j = 0; j < AIS_TO_GET_SHARES; j++) {
					int index = aiSelectorRandom.nextInt(traderList.size());
					Trader traderToGetShares = traderList.get(index);
					ShareParcel shareParcel = new ShareParcel(traderToGetShares, aiShareCount, company);
					shareParcelDAO.saveShareParcel(shareParcel);
					traderList.remove(index);
				}
			}
		}

	}

	private void setInitialProft(CompanyPeriodReport periodReport, Company company) {
		// Set initial profit - made up of revenue, expenses, and interest.
		// So, our initial profit should be double the prime interest rate.
		long primeInterestRateBasisPoints = company.getStockExchange().getPrimeInterestRate();
		// So now we have the initial profit, extrapolate back to work out the
		// interest, expenses and revenue
		long expenses = company.getDefaultExpenseRate() * company.getAssetValue() / 100; // WTF?
		long revenues = company.getDefaultRevenueRate() * company.getAssetValue() / 100;
		long interest = company.getDebtValue() * primeInterestRateBasisPoints / 10000;
		long profit = revenues - expenses - interest;

		periodReport.setStartingExpectedInterest(interest);
		periodReport.setStartingExpectedExpenses(expenses);
		periodReport.setStartingExpectedRevenue(revenues);
		periodReport.setStartingExpectedProfit(profit);
	}

	public Company getNewCompany() {
		CompanyNameBody body = bodies.get(bodyRandom.nextInt(bodies.size()));
		while (body.value == null) {
			body = bodies.get(bodyRandom.nextInt(bodies.size()));
		}

		boolean prefixed = false;
		String name = body.value;
		String code = body.code;

		// If we are prefixable, *always* add a prefix if we are not suffixable,
		// otherwise add randomly.
		if (body.prefixable && (!body.suffixable && usePrefixRandom.nextBoolean())) {
			prefixed = true;
			CompanyNamePrefix prefix = prefixes.get(prefixRandom.nextInt(prefixes.size()));
			while (prefix.value == null || prefix.value.equals(body.value)) {
				prefix = prefixes.get(prefixRandom.nextInt(prefixes.size()));
			}
			if (prefix.spaceAllowed && (!prefix.spaceOptional || useSpace.nextBoolean())) {
				name = prefix.value + " " + body.value;
			} else {
				name = prefix.value + body.value;
			}
			code = prefix.code + code;
		}

		// If we are suffixable, always add if we don't have a prefix, otherwise
		// add randomly.
		if (body.suffixable && (!prefixed || suffixRandom.nextBoolean())) {
			CompanyNameSuffix suffix = suffixes.get(suffixRandom.nextInt(suffixes.size()));
			if (suffix.spaceAllowed && (!suffix.spaceOptional || useSpace.nextBoolean())) {
				name = name + " " + suffix.value;
			} else {
				name = name + suffix.value;
			}
			code = code + suffix.code;
		}
		code = fixCode(code);

		String sector = body.sector;
		String modifierName = body.strategy;

		// At the moment, companies start out all the same.
		// This should be tweaked!!!
		long assets = /*			*/100000000; // formatting retarded for clarity!
		long debt = /*				 */50000000;
		long shareCount = /*           */100000;
		long dividendRate = 25;
		long defaultRevenueRate = 20;
		long defaultExpenseRate = 12;

		Company company = new Company(name, code, assets, debt, shareCount, sector, modifierName, dividendRate, defaultRevenueRate, defaultExpenseRate);

		// Last trade price will effectively be capitalisation / share count
		company.setLastTradePrice(company.getCapitalisation() / shareCount);

		company.setAlwaysPayDividend(companyAlwaysDividendsRandom.nextBoolean());
		if (!company.isAlwaysPayDividend()) {
			company.setNeverPayDividend(companyNeverDividendsRandom.nextBoolean());
		}
		return company;
	}

	private Set<String> codes = new HashSet<String>();

	private String fixCode(String originalCode) {
		if (originalCode.length() < CODE_LENGTH) {
			// ????
			System.out.println("SHORT CODE: " + originalCode);
			return originalCode;
		}

		String code = originalCode.substring(0, CODE_LENGTH);
		while (codes.contains(code)) {
			code = originalCode;
			while (code.length() > CODE_LENGTH) {
				// Remove a random char
				int charToRemove = companyCodeSelectorRandom.nextInt(code.length());
				code = code.substring(0, charToRemove) + code.substring(charToRemove + 1, code.length());
			}
		}
		codes.add(code);
		return code;
	}

	// Guaranteed random by fair dice roll.
	// private static final int SEED = 4;
	// 17 is prime and gives better results :)
	private static final int SEED = 17;

	Random companyCodeSelectorRandom = new Random(SEED);
	Random usePrefixRandom = new Random(SEED);
	Random prefixRandom = new Random(SEED);
	Random bodyRandom = new Random(SEED);
	Random suffixRandom = new Random(SEED);
	Random useSpace = new Random(SEED);
	Random companyAlwaysDividendsRandom = new Random(SEED);
	Random companyNeverDividendsRandom = new Random(SEED);

	public List<CompanyNamePrefix> prefixes = new ArrayList<CompanyNamePrefix>();
	public List<CompanyNameBody> bodies = new ArrayList<CompanyNameBody>();
	public List<CompanyNameSuffix> suffixes = new ArrayList<CompanyNameSuffix>();
	private Trader marketMakerTrader;

	private void initCompanyGenerator() throws SAXException, IOException {
		InputSource src = new InputSource(this.getClass().getResourceAsStream("/initdata.xml"));
		XMLReader reader = XMLReaderFactory.createXMLReader();
		CompanyNameParser companyNameParser = new CompanyNameParser(this);
		reader.setContentHandler(companyNameParser);
		reader.parse(src);
	}

}
