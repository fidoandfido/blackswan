package net.fidoandfido.initialiser;

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
import net.fidoandfido.dao.ExchangeGroupDAO;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ReputationItemDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.UserDAO;
import net.fidoandfido.engine.quarter.QuarterEventGenerator;
import net.fidoandfido.model.AppStatus;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.ExchangeGroup;
import net.fidoandfido.model.ReputationItem;
import net.fidoandfido.model.SectorOutlook;
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
	// 100 k (including cents)
	public static final long TRADER_START_CASH = 10000000;
	// 1 mil (including cents)
	public static final long TRADER_LIQUIDATE_CASH = 100000000;
	// ten mil (including cents)
	public static final long MARKET_MAKER_START_CASH = 1000000000;

	public static final int AIS_TO_GET_SHARES = 20;

	private static final String MARKET_MAKER_NAME = "Market Maker";
	private static final long MIN_SHARE_COUNT = 50000;
	private static final long MAX_SHARE_COUNT = 1000000;

	private Map<String, StockExchange> exchangeMap = new HashMap<String, StockExchange>();
	private Map<String, Trader> traderMap = new HashMap<String, Trader>();

	private TraderDAO traderDAO;
	private ShareParcelDAO shareParcelDAO;
	private CompanyDAO companyDAO;
	private OrderDAO orderDAO;
	private CompanyPeriodReportDAO companyPeriodReportDAO;
	private StockExchangeDAO stockExchangeDAO;
	private ExchangeGroupDAO exchangeGroupDAO;
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
		exchangeGroupDAO = new ExchangeGroupDAO();
	}

	public AppInitialiser() {
		// Nothing to do here.
		initDAOs();
	}

	/**
	 * Initialise the application. This whole unit of work should be unertaken within a transaction.
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
		TraderParser traderParser = new TraderParser();
		reader.setContentHandler(traderParser);
		reader.parse(src);

		marketMakerTrader = new Trader(MARKET_MAKER_NAME, MARKET_MAKER_START_CASH, true, MARKET_MAKER_NAME);
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
		for (ExchangeGroup exchangeGroup : exchangeParser.exchangeGroupList) {
			for (StockExchange exchange : exchangeGroup.getExchanges()) {
				String name = exchange.getName();
				if (!exchangeMap.containsKey(name)) {
					Date periodStartDate = new Date();
					Date endDate = new Date(periodStartDate.getTime() + exchange.getPeriodLength());
					Map<String, SectorOutlook> sectorOutlooks = new HashMap<String, SectorOutlook>();
					for (String sector : exchange.getSectors()) {
						sectorOutlooks.put(sector, new SectorOutlook(sector, 0, 0, SectorOutlook.DEFAULT_NEUTRAL_MESSAGE));
					}
					StockExchangePeriod firstPeriod = new StockExchangePeriod(exchange, periodStartDate, endDate, 0, 0, 0, 0,
							StockExchangePeriod.NEUTRAL_CONDITIONS, sectorOutlooks);
					exchange.setCurrentPeriod(firstPeriod);
					stockExchangeDAO.saveStockExchange(exchange);
					exchangeMap.put(name, exchange);
				}
			}
			exchangeGroupDAO.saveExchangeGroup(exchangeGroup);
		}
	}

	// Seeded!
	Random aiSelectorRandom = new Random(17);

	public Company createNewCompany(StockExchange exchange, List<String> existingCodes, List<String> existingNames, Trader marketMakerTrader)
			throws SAXException, IOException {
		for (String code : existingCodes) {
			this.codes.add(code);
		}
		for (String name : existingNames) {
			this.names.add(name);
		}
		this.marketMakerTrader = marketMakerTrader;
		initCompanyGenerator();
		initialiseSectorList(exchange.getSectors());
		Company company = getNewCompany();
		company.setStockExchange(exchange);
		company.setCompanyStatus(Company.IPO_COMPANY_STATUS);
		// Set up the initial profit for the first period report
		companyDAO.saveCompany(company);
		long currentShareCount = company.getOutstandingShares();
		// The market maker will get all of the shares initially
		long marketMakerCount = currentShareCount;
		ShareParcel mmShareParcel = new ShareParcel(marketMakerTrader, marketMakerCount, company, company.getShareBookValue());
		shareParcelDAO.saveShareParcel(mmShareParcel);

		return company;
	}

	private void createAndSaveCompanies() throws SAXException, IOException {
		// Initialise our company lists...
		initCompanyGenerator();

		Date date = new Date();
		QuarterEventGenerator generator = new QuarterEventGenerator();

		for (StockExchange exchange : exchangeMap.values()) {
			System.out.println("Starting exchange: " + exchange);
			initialiseSectorList(exchange.getSectors());
			for (int i = 0; i < exchange.getCompanyCount(); i++) {

				Company company = getNewCompany();
				company.setStockExchange(exchange);
				companyDAO.saveCompany(company);

				CompanyPeriodReport periodReport = new CompanyPeriodReport(company, date, exchange.getPeriodLength(), 0);

				// Set up the initial profit for the first period report
				setInitialProft(periodReport, company);

				companyPeriodReportDAO.savePeriodReport(periodReport);

				generator.generateQuarters(periodReport, company, exchange);

				company.setCurrentPeriod(periodReport);
				companyDAO.saveCompany(company);

				long currentShareCount = company.getOutstandingShares();
				// int traderCount = traderMap.size();
				// The market maker will get half of the shares to start off
				// with, but will not
				// be trading to make money, so eventually will not have many...
				// hopefully!
				long marketMakerCount = currentShareCount / 2;

				ShareParcel mmShareParcel = new ShareParcel(marketMakerTrader, marketMakerCount, company, company.getShareBookValue());
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
					ShareParcel shareParcel = new ShareParcel(traderToGetShares, aiShareCount, company, company.getShareBookValue());
					shareParcelDAO.saveShareParcel(shareParcel);
					traderList.remove(index);
				}
			}
			stockExchangeDAO.saveStockExchange(exchange);
		}

	}

	private void setInitialProft(CompanyPeriodReport periodReport, Company company) {
		// Set initial profit - made up of revenue, expenses, and interest.
		// So, our initial profit should be double the prime interest rate.
		long primeInterestRateBasisPoints = company.getStockExchange().getPrimeInterestRateBasisPoints();
		// So now we have the initial profit, extrapolate back to work out the
		// interest, expenses and revenue
		long expenses = company.getExpenseRate() * company.getAssetValue() / 100; // WTF?
		long revenues = company.getRevenueRate() * company.getAssetValue() / 100;
		long interest = company.getDebtValue() * primeInterestRateBasisPoints / 10000;
		long profit = revenues - expenses - interest;

		periodReport.setStartingExpectedInterest(interest);
		periodReport.setStartingExpectedExpenses(expenses);
		periodReport.setStartingExpectedRevenue(revenues);
		periodReport.setStartingExpectedProfit(profit);
	}

	// HashSets to ensure company name and code uniqueness.
	private Set<String> codes = new HashSet<String>();
	private Set<String> names = new HashSet<String>();

	private Random assetValueRandom = new Random(17);
	private Random debtValueRandom = new Random(17);
	private Random bookValueRandom = new Random(17);
	private Random dividendRateRandom = new Random(17);
	private Random expenseRateRandom = new Random(17);
	private Random returnRateRandom = new Random(17);

	public void initialiseSectorList(Set<String> sectorList) {
		sectorCompatibleBodyList = bodies;
		currentSectorList = sectorList;
		if (sectorList != null && !sectorList.isEmpty()) {
			sectorCompatibleBodyList = new ArrayList<CompanyNameBody>();
			// Get a list of bodies that are ok.
			for (CompanyNameBody currentBody : bodies) {
				for (String sector : sectorList) {
					if (currentBody.sectors.contains(sector)) {
						sectorCompatibleBodyList.add(currentBody);
						break;
					}
				}
			}
		}
	}

	public Company getNewCompany() {
		String name = "";
		String code = "";
		String sector = "";
		CompanyNameBody body = null;
		boolean unique = false;

		if (sectorCompatibleBodyList == null || sectorCompatibleBodyList.size() == 0) {
			sectorCompatibleBodyList = bodies;
		}

		while (!unique) {
			body = sectorCompatibleBodyList.get(bodyRandom.nextInt(sectorCompatibleBodyList.size()));
			sector = body.sectors.get(bodyRandom.nextInt(body.sectors.size()));

			// Make sure if the current sector list contains any data, that we are getting an appropriate company.
			if (currentSectorList != null && currentSectorList.size() > 0) {
				if (!currentSectorList.contains(sector)) {
					// build a list of sectors and get an appropriate one!
					List<String> goodSectors = new ArrayList<String>();
					for (String goodSector : currentSectorList) {
						if (body.sectors.contains(goodSector)) {
							goodSectors.add(goodSector);
						}
					}
					sector = goodSectors.get(bodyRandom.nextInt(goodSectors.size()));
				}
			}

			boolean prefixed = false;
			code = body.code;
			name = body.value;

			// If we are prefixable, *always* add a prefix if we are not
			// suffixable,
			// otherwise add randomly.
			if (body.prefixable && (!body.suffixable || usePrefixRandom.nextBoolean())) {
				prefixed = true;
				CompanyNamePrefix prefix = prefixes.get(prefixRandom.nextInt(prefixes.size()));
				while (prefix.value.equals(body.value)) {
					prefix = prefixes.get(prefixRandom.nextInt(prefixes.size()));
				}
				if (prefix.spaceAllowed && (!prefix.spaceOptional || useSpace.nextBoolean())) {
					name = prefix.value + " " + body.value;
				} else {
					name = prefix.value + body.value;
				}
				code = prefix.code + code;
			}

			// If we are suffixable, always add if we don't have a prefix,
			// otherwise
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
			if (!names.contains(name)) {
				unique = true;
				names.add(name);
			}
		}

		// Psuedo-randomly generate companies!
		// For the assets, lets get a number between 1 and 200, and multiply it
		// by 100,000 dollars.
		// This gives assets in the range of 100,000 and 20,000,000.
		// For added... something, lets make the number between 10 and 2000 and
		// multiply by 10,000 dollars.
		long assets = (assetValueRandom.nextInt(1990) + 10) * 1000000;

		// Obviously (!) debt must be less than the assets; but how much so?
		// Debt will be between 10 and 90% of the assets.
		long debt = (debtValueRandom.nextInt(80) + 10) * assets / 100;
		// Round the debt off to nearest $10,000 ... Note this may '0' the debt.
		// That is okay.
		debt = (debt / 1000000) * 1000000;

		// Starting book value? Owners equity?
		// We can work out the starting equity...
		long equity = assets - debt;
		// Given equity is between anywhere between 10,000 and 20,000,000
		// dollars,
		// This will be slightly complex. So... Here are the rules...
		// There is a minimum share count of 50,000 and maximum of 1,000,000.
		// Starting book value is randomised to be between 50c and $20 (ie 50
		// and 2000).
		// If this value leads to either the max or min share count being
		// broken, the
		// appropriate one will then be used.
		long shareBookValue = bookValueRandom.nextInt(1950) + 50;
		long shareCount = equity / shareBookValue;
		if (shareCount < MIN_SHARE_COUNT) {
			shareCount = MIN_SHARE_COUNT;
		} else if (shareCount > MAX_SHARE_COUNT) {
			shareCount = MAX_SHARE_COUNT;
		} else {
			// Round the share count to two decimal places...
			// Can't find a better solution to this on the interwebs.
			// There must be a better way right? Right?
			int digits = 0;
			while (shareCount > 100) {
				shareCount = shareCount / 10;
				digits++;
			}
			while (digits > 0) {
				shareCount = shareCount * 10;
				digits--;
			}
		}

		shareBookValue = equity / shareCount;

		// Now the dividend rate.
		// This will be somewhere between 20 and 80 %, stepping in 5%
		// increments.
		long dividendRate = (dividendRateRandom.nextInt(12) * 5) + 20;

		// Expense and revenue rates. This one is a bit simpler;
		// calculate the expense rate and the return delta.
		// Expenses will be between 8 and 25.
		long defaultExpenseRate = expenseRateRandom.nextInt(18) + 8;

		// Return will be calculated slightly differently. Bias towards a return
		// rate of 5%
		int possibleReturns[] = { 2, 3, 3, 4, 4, 5, 5, 5, 6, 7, 8 };
		int returnRate = possibleReturns[returnRateRandom.nextInt(possibleReturns.length)];
		long defaultRevenueRate = defaultExpenseRate + returnRate;

		String performanceProfile = "";
		String performanceProfileDescription = "";

		Company company = new Company(name, code, assets, debt, shareCount, sector, performanceProfile, performanceProfileDescription, dividendRate,
				defaultRevenueRate, defaultExpenseRate);

		// Last trade price will effectively be capitalisation / share count
		company.setLastTradePrice(company.getCapitalisation() / shareCount);

		company.setAlwaysPayDividend(companyAlwaysDividendsRandom.nextBoolean());
		if (!company.isAlwaysPayDividend()) {
			company.setNeverPayDividend(companyNeverDividendsRandom.nextBoolean());
		}
		return company;
	}

	private String fixCode(String originalCode) {
		if (originalCode.length() < CODE_LENGTH) {
			System.out.println("SHORT CODE: " + originalCode);
			return originalCode;
		}

		String code = originalCode.substring(0, CODE_LENGTH);
		int attempts = 0;
		while (codes.contains(code)) {
			code = originalCode;
			if (attempts++ > 10) {
				// triple the length of the code.
				code = code + code + code;
			}
			if (attempts > 20) {
				// add some random instead.
				code = originalCode;
				for (int i = 0; i < originalCode.length(); i++) {
					char c = (char) ('A' + companyCodeSelectorRandom.nextInt(26));
					code = code + c;
				}

			}
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
	private List<CompanyNameBody> sectorCompatibleBodyList = new ArrayList<CompanyNameBody>();
	private Set<String> currentSectorList = new HashSet<String>();
	public List<CompanyNameSuffix> suffixes = new ArrayList<CompanyNameSuffix>();
	private Trader marketMakerTrader;

	public void initCompanyGenerator() throws SAXException, IOException {
		InputSource src = new InputSource(this.getClass().getResourceAsStream("/initdata.xml"));
		XMLReader reader = XMLReaderFactory.createXMLReader();
		CompanyNameParser companyNameParser = new CompanyNameParser(this);
		reader.setContentHandler(companyNameParser);
		reader.parse(src);
	}

}
