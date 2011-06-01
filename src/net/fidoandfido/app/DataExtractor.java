package net.fidoandfido.app;

import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.AppStatusDAO;
import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ReputationItemDAO;
import net.fidoandfido.dao.RumourDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.UserDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.PeriodRumour;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.User;
import net.fidoandfido.util.WebPageUtil;

public class DataExtractor {

	private TraderDAO traderDAO;
	private ShareParcelDAO shareParcelDAO;
	private CompanyDAO companyDAO;
	private OrderDAO orderDAO;
	private CompanyPeriodReportDAO companyPeriodReportDAO;
	private StockExchangeDAO stockExchangeDAO;
	private AppStatusDAO appStatusDAO;
	private UserDAO userDAO;
	private ReputationItemDAO reputationItemDAO;
	private RumourDAO rumourDAO;

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
		rumourDAO = new RumourDAO();
	}

	public DataExtractor() {
		initDAOs();
	}

	public static void main(String argv[]) {

		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		DataExtractor dataExtractor = new DataExtractor();
		dataExtractor.loolkAtCompany("UDFO");
		HibernateUtil.commitTransaction();
	}

	private void loolkAtCompany(String code) {

		Company company = companyDAO.getCompanyByCode(code);
		List<CompanyPeriodReport> recentPeriodReportListByCompany = companyPeriodReportDAO.getRecentPeriodReportListByCompany(company, 5);

		for (CompanyPeriodReport companyPeriodReport : recentPeriodReportListByCompany) {
			System.out.println(companyPeriodReport.getStartingAssets());
			System.out.println(companyPeriodReport.getFinalProfit());

		}

	}

	public void getUser(String userName) {
		User user = userDAO.getUserByUsername(userName);
		if (user != null) {
			System.out.println("User name: " + user.getUserName());
			System.out.println("Is admin? " + user.isUserAdmin());
			System.out.println("Trader: " + user.getTrader());
		} else {
			System.out.println("User is null.");
		}
	}

	public void writeData() {
		List<StockExchange> stockExchangeList = stockExchangeDAO.getStockExchangeList();
		for (StockExchange stockExchange : stockExchangeList) {
			System.out.println("Showing companies for stock exchange: " + stockExchange.getName());
			Iterable<Company> companies = companyDAO.getCompaniesByExchange(stockExchange);
			for (Company company : companies) {
				System.out.println(company.getName());
				System.out.println("Shares: " + company.getOutstandingShares());
				System.out.println("Share price: " + company.getLastTradePrice());
				System.out.println("Last profit: " + company.getPreviousProfit());
				System.out.println("Assets: " + company.getAssetValue());
			}

		}
	}

	public void blob() {
		Company company = companyDAO.getCompanyByCode("AUDT");
		CompanyPeriodReport periodReport = company.getCurrentPeriod();
		System.out.println("Quarters since bad: " + periodReport.getQuartersSinceBadQuarter());
		System.out.println("Quarters since good: " + periodReport.getQuartersSinceGoodQuarter());

		ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
		Iterable<ShareParcel> holdings = shareParcelDAO.getHoldingsByCompany(company);
		for (ShareParcel parcel : holdings) {
			System.out.println("Holdings -- Trader: " + parcel.getTrader().getName());
		}
	}

	public void getOrders() {
		OrderDAO orderDAO = new OrderDAO();
		Company company = companyDAO.getCompanyByCode("AUDT");
		Iterable<Order> orders = orderDAO.getLastExecutedOrders(company, 10);
		for (Order order : orders) {
			System.out.println(order.getTrader().getName());
			System.out.println(WebPageUtil.formatCurrency(order.getOfferPrice()));
			System.out.println(order.getOriginalShareCount());
			System.out.println(order.getOrderType());
		}
	}

	public void ruma() {
		List<PeriodRumour> rumours = rumourDAO.getLatestRumours(10, new Date());
		for (PeriodRumour rumour : rumours) {
			System.out.println("rumour: " + rumour.getCompany().getName() + " --- " + rumour.getMessage());
		}
	}

	public void bass() {

		List<StockExchange> stockExchangeList = stockExchangeDAO.getStockExchangeList();
		for (StockExchange stockExchange : stockExchangeList) {
			System.out.println("Showing period reports for stock exchange: " + stockExchange.getName());
			List<CompanyPeriodReport> cprList = companyPeriodReportDAO.getPeriodPerpotListByExchange(stockExchange);
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
		List<Trader> traderList = traderDAO.getAITraderList();
		for (Trader trader : traderList) {
			List<Order> closedOrders = OrderDAO.getClosedOrdersByTrader(trader);
			for (Order order : closedOrders) {

				System.out.println(order.getDateCreated() + " -- " + order.getCompany().getName());
			}
		}

	}

	public void foo() {
		Company company = companyDAO.getCompanyList().get(0);

		List<CompanyPeriodReport> reportList = companyPeriodReportDAO.getPeriodPerpotListForCompany(company);
		System.out.println("Report list length:" + reportList.size());

		for (CompanyPeriodReport companyPeriodReport : reportList) {
			System.out.println(WebPageUtil.formatCurrency(companyPeriodReport.getFinalProfit()));

		}
	}

}
