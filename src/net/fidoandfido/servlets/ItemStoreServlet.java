package net.fidoandfido.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.ReputationItemDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.model.ReputationItem;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;
import net.fidoandfido.util.ServerUtil;
import net.fidoandfido.util.WebUtil;

import org.apache.log4j.Logger;

public class ItemStoreServlet extends HttpServlet {

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
	public static String ITEM_NAME = "item_name";
	public static String COST = "cost";
	public static String BUY_OR_SELL = "buy_or_sell";
	public static String BUY = "buy";
	public static String SELL = "sell";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HibernateUtil.beginTransaction();
		logger.info("Item store servlet starting.");
		Trader trader = WebUtil.getCurrentTraderBySession(req.getSession().getId());
		boolean success = process(req, trader);
		HibernateUtil.commitTransaction();
		if (success) {
			logger.info("Store item success");
			resp.sendRedirect("/myapp/Trader.jsp");
		} else {
			logger.info("Store item failure");
			resp.sendRedirect("/myapp/Welcome.jsp");
		}
	}

	private boolean process(HttpServletRequest req, Trader trader) {

		boolean success = false;

		try {
			String itemName = req.getParameter(ITEM_NAME);
			long cost = Long.parseLong(req.getParameter(COST));
			String buyOrSell = req.getParameter(BUY_OR_SELL);

			ReputationItem item = ReputationItemDAO.getItem(itemName, cost);

			if (trader == null) {
				logger.info("trader null");
				return false;
			}
			if (item == null) {
				logger.info("Item null");
				return false;
			}

			if (BUY.equals(buyOrSell)) {
				// Attempt to buy the item.
				if (trader.canMakeTrade(cost)) {
					logger.info("Trader buying item! " + trader.getName() + " -- " + item.getName() + " for: " + item.getCost());
					TraderEvent event = new TraderEvent(trader, "buy item", new Date(), item, cost, trader.getAvailableCash(), trader.getAvailableCash() - cost);
					trader.takeCash(cost);
					trader.addItem(item);
					TraderEventDAO.saveTraderEvent(event);
					TraderDAO.saveTrader(trader);
					success = true;
				} else {
					logger.info("Trader failed to buy: " + trader.getName() + " -- " + item.getName() + " for: " + item.getCost());
				}
			} else if (SELL.equals(buyOrSell)) {
				// Attempt to sell the item
				if (trader.hasItem(item)) {
					logger.info("Trader selling item! " + trader.getName() + " -- " + item.getName() + " for: " + item.getCost());
					TraderEvent event = new TraderEvent(trader, "sell item", new Date(), item, cost, trader.getAvailableCash(), trader.getAvailableCash()
							+ cost);
					trader.giveCash(cost);
					trader.removeItem(item);
					TraderEventDAO.saveTraderEvent(event);
					TraderDAO.saveTrader(trader);
					success = true;
				}
			} else {
				logger.info("Invalid BUY_OR_SELL parameter: " + buyOrSell);
			}
		} catch (Exception e) {
			logger.error("Exception generated in item store servlet");
			ServerUtil.logError(logger, e);
			return false;
		}
		return success;
	}

}
