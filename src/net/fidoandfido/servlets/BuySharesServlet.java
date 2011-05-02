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

public class BuySharesServlet extends HttpServlet {

	Logger logger = Logger.getLogger(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	public static String TRADER_NAME_PARM = "trader_name";
	public static String COMPANY_CODE_PARM = "company_code";
	public static String SHARE_COUNT = "share_count";
	public static String OFFER_PRICE = "offer_price";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HibernateUtil.beginTransaction();
		logger.info("Buying shares servlet starting.");
		Trader trader = WebUtil.getCurrentTraderBySession(req.getSession().getId());
		boolean success = buyShares(req, trader);
		HibernateUtil.commitTransaction();
		if (success) {
			logger.info("Buy success");
			resp.sendRedirect("/myapp/Trader.jsp");
		} else {
			logger.info("Buy failure");
			resp.sendRedirect("/myapp/Welcome.jsp");
		}

	}

	private boolean buyShares(HttpServletRequest req, Trader trader) {
		try {
			String companyCode = req.getParameter(COMPANY_CODE_PARM);
			long shareCount = Long.parseLong(req.getParameter(SHARE_COUNT));
			long offerPrice = Long.parseLong(req.getParameter(OFFER_PRICE));
			CompanyDAO companyDAO = new CompanyDAO();
			Company company = companyDAO.getCompanyByCode(companyCode);

			if (trader == null) {
				return false;
			}
			if (company == null) {
				return false;
			}

			if (!validateOrder(trader, company, shareCount, offerPrice)) {
				return false;
			}
			Order buyOrder = new Order(trader, company, shareCount, offerPrice, Order.OrderType.BUY);
			OrderDAO orderDAO = new OrderDAO();
			orderDAO.saveOrder(buyOrder);
			// Attempt to process the order...
			StockExchange exchange = company.getStockExchange();
			exchange.processOrder(buyOrder);
		} catch (Exception e) {
			// ??
			return false;
		}
		return true;
	}

	private boolean validateOrder(Trader trader, Company company, long shareCount, long offerPrice) {
		// Get holdings of this company by this trader...
		long cost = shareCount * offerPrice;
		if (company.getOutstandingShares() < shareCount) {
			return false;
		}
		if (cost > trader.getAvailableCash()) {
			return false;
		}
		return true;
	}
}
