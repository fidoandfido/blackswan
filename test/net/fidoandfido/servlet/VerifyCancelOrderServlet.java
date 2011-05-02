package net.fidoandfido.servlet;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Trader;
import net.fidoandfido.servlets.CancelOrderServlet;

public class VerifyCancelOrderServlet {

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		CancelOrderServlet cancelOrderServlet = new CancelOrderServlet();
		TraderDAO traderDAO = new TraderDAO();
		Trader trader = traderDAO.getTraderByName("asdf");
		String id = "ff8081812f524d74012f5330fcd640e5";
		cancelOrderServlet.cancelOrder(id, trader);

		Order order = OrderDAO.getOrderById(id);
		HibernateUtil.commitTransaction();
		if (order.isActive()) {
			System.out.println("Order active!?!");
		} else {
			System.out.println("Order inactive.");
		}

		HibernateUtil.beginTransaction();
		Order newOrder = OrderDAO.getOrderById(id);
		HibernateUtil.commitTransaction();
		if (newOrder.isActive()) {
			System.out.println("Order active!?!");
		} else {
			System.out.println("Order inactive.");
		}
	}

}
