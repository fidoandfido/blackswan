package net.fidoandfido.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;
import net.fidoandfido.util.WebUtil;

import org.apache.log4j.Logger;

public class CancelOrderServlet extends HttpServlet {

	Logger logger = Logger.getLogger(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public static String ID_PARM = "order_id";
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("Cancelling order servlet starting.");
		HibernateUtil.beginTransaction();
		Trader trader = WebUtil.getCurrentTraderBySession(req.getSession().getId());
		String id = req.getParameter(ID_PARM);
		boolean success = cancelOrder(id, trader);
		HibernateUtil.commitTransaction();
		if (success) {
			logger.info("Buy success");
			resp.sendRedirect("/myapp/Trader.jsp");
		} else {
			logger.info("Buy failure");
			resp.sendRedirect("/myapp/Welcome.jsp");
		}

	}

	public boolean cancelOrder(String id, Trader trader) {
		try {
			if (trader == null) {
				logger.info("Buy failure - trader null");
				return false;
			}
			if (id == null || id.isEmpty()) {
				logger.info("Buy failure - id null or empty");
				return false;
			}
			Order order = OrderDAO.getOrderById(id);
			if (!order.getTrader().equals(trader)) {
				logger.info("Buy failure - bad trader!");
				return false;
			}
			order.setActive(false);
			OrderDAO.saveOrder(order);
		} catch (Exception e) {
			logger.error("Exception thrown in cancel order: " + e.getMessage());
			return false;
		}
		return true;
	}
}
