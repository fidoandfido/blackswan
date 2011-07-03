package net.fidoandfido.app;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.dao.RumourDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.TraderMessageDAO;
import net.fidoandfido.dao.UserDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.PeriodQuarter;
import net.fidoandfido.model.PeriodRumour;
import net.fidoandfido.model.ReputationItem;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderMessage;
import net.fidoandfido.model.User;

public class TraderInfoApp {

	public static void main(String argv[]) {

		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();

		startDate = new Date();

		UserDAO userDAO = new UserDAO();
		User user = userDAO.getUserByUsername("andy");
		Trader trader = user.getTrader();
		Date currentDate = new Date();

		print("RETRIEVED TRADER");

		// Set up a map of company codes to share parcels for this user.
		ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
		Iterable<ShareParcel> traderHoldings = shareParcelDAO.getHoldingsByTrader(trader);
		Map<String, ShareParcel> holdingsMap = new HashMap<String, ShareParcel>();
		for (ShareParcel parcel : traderHoldings) {
			holdingsMap.put(parcel.getCompany().getCode(), parcel);
		}
		long totalValue = trader.getCash();

		print("GETTING PORTFOLIO VALUE -- HOLDINGS");

		// Add the portfolio value.
		long portfolioValue = 0;
		Iterable<ShareParcel> shareParcels = shareParcelDAO.getHoldingsByTrader(trader);
		for (ShareParcel shareParcel : shareParcels) {
			portfolioValue += shareParcel.getShareCount() * shareParcel.getCompany().getLastTradePrice();
		}
		totalValue += portfolioValue;

		print("GETTING PORTFOLIO VALUE -- REP ITEMS");

		// Add the value of the items this trader owns (their current worth - ie sale price)
		for (ReputationItem item : trader.getReputationItems()) {
			totalValue += item.getSalePrice();
		}

		print("SETTING UP HOLDINGS MAP");

		if (shareParcels.iterator().hasNext()) {

			for (ShareParcel shareParcel : shareParcels) {
				String companyName = shareParcel.getCompany().getName();
				String companyCode = shareParcel.getCompany().getCode();
				long marketValue = shareParcel.getShareCount() * shareParcel.getCompany().getLastTradePrice();
				totalValue += marketValue;

			}
		}

		print("GETTING MESSAGES");
		TraderMessageDAO messageDAO = new TraderMessageDAO();
		List<TraderMessage> messages = messageDAO.getCurrentMessages(trader);
		if (messages.size() != 0) {
			for (TraderMessage message : messages) {
				boolean isNew = false;
				if (!message.isRead()) {
					isNew = true;
					message.setRead(true);
					messageDAO.saveMessage(message);
				}
			}
		}

		print("GETTING ORDERS");
		List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader);
		if (openOrders.size() != 0) {
			for (Order order : openOrders) {
				System.out.println(order.getCompany().getName());
			}
		}

		print("GETTING RUMOURS");
		RumourDAO rumourDAO = new RumourDAO();
		List<PeriodRumour> rumours = rumourDAO.getLatestRumours(10, currentDate, trader);
		if (rumours.size() != 0) {
			Set<String> sectors = new HashSet<String>();
			boolean rumourShown = false;
			for (PeriodRumour rumour : rumours) {
				Company company = rumour.getCompany();
				String companyCode = company.getCode();
				if (rumour.getDateRumourExpires().before(currentDate)) {
					continue;
				}
				String sector = rumour.getSector();
				if (trader.getReputation(sector) < rumour.getReputationRequired()) {
					sectors.add(sector);
				} else {
					if (!rumourShown) {
						// set up the rumour table
						rumourShown = true;
					}
				}
			}
		}

		print("GETTING LATEST ANNOUNCEMENTS");
		PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();
		List<PeriodQuarter> events = periodPartInformationDAO.getLatestEvents(10, currentDate, trader);
		for (PeriodQuarter event : events) {
			Company company = event.getCompany();
			String companyCode = company.getCode();
			ShareParcel parcel = holdingsMap.get(company.getCode());
			if (parcel != null) {
				System.out.println("Can sell " + companyCode);
			} else {
				System.out.println("No selling");
			}
		}

		print("ALL DONE!");

	}

	static Date startDate;

	private static void print(String message) {
		// TODO Auto-generated method stub
		Date currentDate = new Date();
		long delta = currentDate.getTime() - startDate.getTime();
		System.out.println("-----" + delta + "----" + message);
	}

}
