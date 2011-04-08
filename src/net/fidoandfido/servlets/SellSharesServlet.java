package net.fidoandfido.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;
import net.fidoandfido.util.WebUtil;

import org.apache.log4j.Logger;

public class SellSharesServlet extends HttpServlet {

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
	public static String COMPANY_CODE_PARM = "company_code";
	public static String SHARE_COUNT = "share_count";
	public static String ASKING_PRICE = "asking_price";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HibernateUtil.beginTransaction();
		logger.info("Sell share servlet starting.");
		Trader trader = WebUtil.getCurrentTraderBySession(req.getSession().getId());
		boolean success = sellShares(req, trader);
		HibernateUtil.commitTransaction();
		if (success) {
			logger.info("Sell success");
			resp.sendRedirect("/myapp/Trader.jsp");
		} else {
			logger.info("Sell failure");
			resp.sendRedirect("/myapp/Welcome.jsp");
		}
	}

	private boolean sellShares(HttpServletRequest req, Trader trader) {
		try {
			String companyCode = req.getParameter(COMPANY_CODE_PARM);
			long shareCount = Long.parseLong(req.getParameter(SHARE_COUNT));
			long askingPrice = Long.parseLong(req.getParameter(ASKING_PRICE));

			Company company = CompanyDAO.getCompanyByCode(companyCode);

			if (trader == null) {
				return false;
			}
			if (company == null) {
				return false;
			}

			if (!validateOrder(trader, company, shareCount)) {
				return false;
			}
			Order sellOrder = new Order(trader, company, shareCount, askingPrice, Order.OrderType.SELL);
			OrderDAO.saveOrder(sellOrder);

			// Attempt to process the order...
			StockExchange exchange = company.getStockExchange();
			exchange.processOrder(sellOrder);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean validateOrder(Trader trader, Company company, long shareCount) {
		// Get holdings of this company by this trader...
		ShareParcel shareParcel = ShareParcelDAO.getHoldingsByTraderForCompany(trader, company);
		long holdingCount = 0;
		if (shareParcel != null) {
			holdingCount = holdingCount + shareParcel.getShareCount();
			if (holdingCount > shareCount) {
				return true;
			}
		}
		return false;
	}
}
