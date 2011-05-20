package net.fidoandfido.app;

import java.util.ArrayList;
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

public class CompanyModifierApp {

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

	public CompanyModifierApp() {
		initDAOs();
	}

	public static void main(String argv[]) {

		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		CompanyModifierApp app = new CompanyModifierApp();
		app.modifyCompany();
		HibernateUtil.commitTransaction();
	}

	private void modifyCompany() {
		List<Company> allCompanies = companyDAO.getCompanyList();
		// modify the first and second companies.
		List<Company> companies = new ArrayList<Company>();

		for (Company company : allCompanies) {
			if (company.isTrading()) {
				companies.add(company);
			}
		}

		Company company = companies.get(0);
		System.out.println("Company: " + company.getName());
		System.out.println("   To go insolvent soon");
		company.setExpenseRate(11);
		company.setRevenueRate(10);
		company.setDebtValue(50000000);
		company.setAssetValue(51000000);

		company = companies.get(1);
		System.out.println("Company: " + company.getName());
		System.out.println("   To be disolved!");
		company.setAssetValue(-100);

		company = companies.get(2);
		System.out.println("Company: " + company.getName());
		System.out.println("    To go insolvent and crash");
		company.setExpenseRate(25);
		company.setRevenueRate(10);
		company.setDebtValue(50000000);
		company.setAssetValue(5000000);

		company = companies.get(3);
		System.out.println("Company: " + company.getName());
		System.out.println("    To go insolvent and survive.");
		company.setExpenseRate(1);
		company.setRevenueRate(25);
		company.setDebtValue(50000000);
		company.setAssetValue(45000000);

	}

}
