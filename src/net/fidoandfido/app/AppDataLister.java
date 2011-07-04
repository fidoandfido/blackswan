package net.fidoandfido.app;

import java.util.List;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.ExchangeGroupDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ReputationItemDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.ExchangeGroup;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.PeriodQuarter;
import net.fidoandfido.model.ReputationEffect;
import net.fidoandfido.model.ReputationItem;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;

public class AppDataLister {

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		AppDataLister appDataLister = new AppDataLister();
		appDataLister.writeData();
		HibernateUtil.commitTransaction();

	}

	public AppDataLister() {
		// Nothing to do here!
	}

	public void writeData() {

		TraderDAO traderDAO = new TraderDAO();
		ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
		CompanyDAO companyDAO = new CompanyDAO();
		OrderDAO orderDAO = new OrderDAO();
		StockExchangeDAO stockExchangeDAO = new StockExchangeDAO();
		ExchangeGroupDAO exchangeGroupDAO = new ExchangeGroupDAO();

		System.out.println("Writing data!");
		List<Trader> traderList = traderDAO.getTraderList();
		System.out.println("Retrieved " + traderList.size() + " traders.");
		for (Trader trader : traderList) {
			System.out.println("---");
			System.out.println("Trader: " + trader.getName());
			System.out.println("Cash: " + trader.getCash());
			System.out.println("Is AI: " + trader.isAITrader());
			System.out.println("Is market maker: " + trader.isMarketMaker());
			System.out.println("HOLDINGS");
			Iterable<ShareParcel> holdings = shareParcelDAO.getHoldingsByTrader(trader);
			for (ShareParcel shareParcel : holdings) {
				System.out.println("--> " + shareParcel.getShareCount() + " of " + shareParcel.getCompany().getName());
			}
		}

		System.out.println("--------------------");
		List<ExchangeGroup> exchangeGroupList = exchangeGroupDAO.getAllExchangeGroups();
		for (ExchangeGroup group : exchangeGroupList) {
			System.out.println("Exhange Group: " + group.getName());
		}

		List<StockExchange> exchangeList = stockExchangeDAO.getStockExchangeList();
		for (StockExchange stockExchange : exchangeList) {
			System.out.println(stockExchange.toString());
		}

		List<Company> companyList = companyDAO.getCompanyList();
		for (Company company : companyList) {
			System.out.println();
			System.out.println("---");
			System.out.println(company.getName());
			System.out.println("Outstanding shares: " + company.getOutstandingShares());
			if (company.isAlwaysPayDividend()) {
				System.out.println("Always dividend!");
			} else if (company.isNeverPayDividend()) {
				System.out.println("Never dividend!");
			} else {
				System.out.println("Sometimes dividend!");
			}
			CompanyPeriodReport currentReport = company.getCurrentPeriod();
			System.out.println("CURRENT REPORT - Generation: " + currentReport.getGeneration());
			System.out.println("----> Starting profit expectation: " + currentReport.getStartingExpectedProfit());
			// System.out.println("- Long term sector");
			// printPeriodPartInformation(currentReport.getLongTermSectorInformation());
			// System.out.println("- Long term company");
			// printPeriodPartInformation(currentReport.getLongTermCompanyInformation());
			// System.out.println("- Short term sector");
			// printPeriodPartInformation(currentReport.getShortTermSectorInformation());
			// System.out.println("- Short term company");
			// printPeriodPartInformation(currentReport.getShortTermCompanyInformation());
			for (PeriodQuarter event : currentReport.getPeriodQuarterList()) {
				System.out.println("EVENT: " + event.getAnnouncementType());
				System.out.println("Message: " + event.getMessage());
				System.out.println("Date available: " + event.getDateInformationAvailable());
				System.out.println("Expected profit: " + event.getProfit());
			}
			System.out.println("----> Final profit: " + currentReport.getFinalProfit());

		}

		for (Company company : companyList) {
			System.out.println(company.getCode() + " -- " + company.getName() + " -- " + company.getLastTradePrice());
		}

		List<Order> orderList = orderDAO.getAllOrders();
		for (Order order : orderList) {
			System.out.println("ORDER -- " + order.getOrderType());
			System.out.println(order.getTrader().getName());
			System.out.println(order.getCompany().getName());
			System.out.println("Count: " + order.getOriginalShareCount());
			System.out.println("Price: " + order.getOfferPrice());
			System.out.println("Active? " + order.isActive());
		}

		ReputationItemDAO reputationItemDAO = new ReputationItemDAO();
		List<ReputationItem> itemList = reputationItemDAO.getItems();
		for (ReputationItem item : itemList) {
			System.out.println("Item:" + item.getName());
			System.out.println("Cost:" + item.getCost());
			System.out.println("Image:" + item.getImage());
			for (ReputationEffect effect : item.getEffectList()) {
				System.out.println(" --> " + effect.getSector() + " ---- " + effect.getPoints());
			}

		}

	}

	private void printPeriodPartInformation(PeriodQuarter periodPartInformation) {
		System.out.println("----> Expected profit: " + periodPartInformation.getProfit());
		System.out.println("----> " + periodPartInformation.getMessage());
		System.out.println("----> " + periodPartInformation.getEventType());
		System.out.println("----> " + periodPartInformation.getDateInformationAvailable());
	}
}
