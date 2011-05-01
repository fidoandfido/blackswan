package net.fidoandfido.app;

import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.RumourDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.PeriodRumour;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;
import net.fidoandfido.util.WebPageUtil;

public class DataExtractor {

	public static void main(String argv[]) {

		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		DataExtractor dataExtractor = new DataExtractor();
		dataExtractor.writeData();
		HibernateUtil.commitTransaction();
	}

	public void writeData() {
		List<StockExchange> stockExchangeList = StockExchangeDAO.getStockExchangeList();
		for (StockExchange stockExchange : stockExchangeList) {
			System.out.println("Showing companies for stock exchange: " + stockExchange.getName());
			Iterable<Company> companies = CompanyDAO.getCompaniesByExchange(stockExchange);
			for (Company company : companies) {
				System.out.println(company.getName());
			}

		}
	}

	public void blbo() {
		Company company = CompanyDAO.getCompanyByCode("ONMO");

		CompanyPeriodReport periodReport = company.getCurrentPeriod();

	}

	public void ruma() {
		List<PeriodRumour> rumours = RumourDAO.getLatestRumours(10, new Date());
		for (PeriodRumour rumour : rumours) {
			System.out.println("rumour: " + rumour.getCompany().getName() + " --- " + rumour.getMessage());
		}
	}

	public void bass() {

		List<StockExchange> stockExchangeList = StockExchangeDAO.getStockExchangeList();
		for (StockExchange stockExchange : stockExchangeList) {
			System.out.println("Showing period reports for stock exchange: " + stockExchange.getName());
			List<CompanyPeriodReport> cprList = CompanyPeriodReportDAO.getPeriodPerpotListByExchange(stockExchange);
			int rumourCount = 0;
			for (CompanyPeriodReport companyPeriodReport : cprList) {
				if (companyPeriodReport.getPeriodRumourList().size() > 0) {
					rumourCount++;
					System.out.println("Period report for company: " + companyPeriodReport.getCompany().getName());
					System.out.println("Rumour list: " + companyPeriodReport.getPeriodRumourList().size());
					for (PeriodRumour rumour : companyPeriodReport.getPeriodRumourList()) {
						System.out.println("rumour: " + rumour.getMessage());
					}
				}
			}

		}

	}

	public void bar() {
		List<Trader> traderList = TraderDAO.getAITraderList();
		for (Trader trader : traderList) {
			List<Order> closedOrders = OrderDAO.getClosedOrdersByTrader(trader);
			for (Order order : closedOrders) {

				System.out.println(order.getDateCreated() + " -- " + order.getCompany().getName());
			}
		}

	}

	public void foo() {
		Company company = CompanyDAO.getCompanyList().get(0);

		List<CompanyPeriodReport> reportList = CompanyPeriodReportDAO.getPeriodPerpotListForCompany(company);
		System.out.println("Report list length:" + reportList.size());

		for (CompanyPeriodReport companyPeriodReport : reportList) {
			System.out.println(WebPageUtil.formatCurrency(companyPeriodReport.getFinalProfit()));

		}
	}

}
