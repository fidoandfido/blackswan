package net.fidoandfido.app;

import java.util.List;
import java.util.logging.Logger;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.engine.ai.AITrader;
import net.fidoandfido.engine.ai.LiquididatingAI;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Trader;

public class LiquidatorApp {

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		LiquidatorApp app = new LiquidatorApp();
		app.liquidate("fred6");
	}

	Logger logger = Logger.getLogger(this.getClass().getName());
	private TraderDAO traderDAO;

	public LiquidatorApp() {
		// Nothing here!
		traderDAO = new TraderDAO();
	}

	public void liquidate(String traderName) {
		HibernateUtil.beginTransaction();

		Trader trader = traderDAO.getTraderByName(traderName);
		List<Order> closedOrders = OrderDAO.getOpenOrdersByTrader(trader);
		if (closedOrders.size() > 0) {
			logger.info("AIRunner - removing outstanding orders: " + trader.getName());
		}
		for (Order order : closedOrders) {
			OrderDAO.deleteOrder(order);
		}

		// Liquidate!
		AITrader aiTrader = new LiquididatingAI();
		aiTrader.performTrades(trader);
		trader.setCash(AppInitialiser.TRADER_LIQUIDATE_CASH);
		traderDAO.saveTrader(trader);
		HibernateUtil.commitTransaction();

	}

}
