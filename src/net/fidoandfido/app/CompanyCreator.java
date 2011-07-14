package net.fidoandfido.app;

import java.io.IOException;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.PeriodMessageDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.engine.PeriodGenerator;
import net.fidoandfido.engine.economicmodfiers.EconomicModifier;
import net.fidoandfido.engine.economicmodfiers.EconomicModifierFactory;
import net.fidoandfido.initialiser.AppInitialiser;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodMessage;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;
import net.fidoandfido.util.ServerUtil;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class CompanyCreator {

	Logger logger = Logger.getLogger("net.fidoandfido");
	private StockExchangeDAO stockExchangeDAO;
	private TraderDAO traderDAO;
	private CompanyDAO companyDAO;
	private PeriodMessageDAO periodMessageDAO;

	private PeriodGenerator periodGenerator = new PeriodGenerator("group1");

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		CompanyCreator companyCreator = new CompanyCreator();
		companyCreator.init();
		companyCreator.runTest();
		HibernateUtil.commitTransaction();

	}

	public void init() {
		stockExchangeDAO = new StockExchangeDAO();
		traderDAO = new TraderDAO();
		companyDAO = new CompanyDAO();
		periodMessageDAO = new PeriodMessageDAO();
	}

	public void runTest() {
		// Now create a new company for the exchange (if that is applicable)
		// Since we have flushed and cleared the session, best to get a new handle on our exchange :(
		StockExchange exchange = stockExchangeDAO.getStockExchangeByName("ESX");
		int currentTradingCount = stockExchangeDAO.getTradingCompaniesCountForExchange(exchange);
		EconomicModifier economicModifier = EconomicModifierFactory.getEconomicModifier(exchange.getEconomicModifierName());
		// currentExchangePeriod
		StockExchangePeriod currentExchangePeriod = exchange.getCurrentPeriod();

		if (currentTradingCount < exchange.getMaxTradingCompanyCount()) {
			if (economicModifier.newCompanyToBeFounded(currentExchangePeriod)) {
				periodGenerator.createNewCompany(exchange);
			}
		}
	}

	private void createNewCompany(StockExchange exchange) {
		// SHOULD BE CREATING A NEW COMPANY NOW!!!
		AppInitialiser initialiser = new AppInitialiser();
		try {
			Company company = initialiser.createNewCompany(exchange, companyDAO.getAllCompanyCodes(), companyDAO.getAllCompanyNames(),
					traderDAO.getMarketMaker());
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
}
