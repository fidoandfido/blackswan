package net.fidoandfido.app;

import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.engine.ai.AIRunner;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.StockExchange;

public class Tester {

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();

		AIRunner aiRunner = new AIRunner();
		// HibernateUtil

		HibernateUtil.beginTransaction();
		aiRunner.process();
		HibernateUtil.commitTransaction();
	}

	public void foo() {
		// Get the latest available announcements...
		Date date = new Date();
		List<PeriodEvent> infoList = PeriodPartInformationDAO.getLatestEvents(5, date);
		System.out.println(date);
		for (PeriodEvent ppi : infoList) {
			System.out.println("----");
			System.out.println(ppi.getCompany());
			System.out.println(ppi.getDateInformationAvailable());
			// System.out.println(ppi.getEventType());
			System.out.println(ppi.getMessage());
		}

		StockExchange exchange = StockExchangeDAO.getStockExchangeByName("ASX");
		Iterable<Company> companies = CompanyDAO.getCompaniesByExchange(exchange);
		for (Company company : companies) {
			System.out.println(company.getName());
		}

	}

}
