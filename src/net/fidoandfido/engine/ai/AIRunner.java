package net.fidoandfido.engine.ai;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.initialiser.AppInitialiser;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Trader;
import net.fidoandfido.util.ServerUtil;

import org.apache.log4j.Logger;

/**
 * @author andy
 * 
 */
public class AIRunner implements Runnable {

	Logger logger = Logger.getLogger(getClass());

	public static long DEFUALT_TIMEOUT = 30000;

	private final long timeout;

	private boolean running = true;

	private TraderDAO traderDAO = new TraderDAO();

	// Seeded (as always!)
	private Random aiSelector = new Random(new Date().getTime());

	private static final int CHANCE_OF_LIQUIDATION = 60;

	// Only do 5 traders each time
	private static final int AI_TRADE_COUNT = 10;

	public AIRunner() {
		timeout = DEFUALT_TIMEOUT;
	}

	public AIRunner(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public void run() {
		while (running) {
			process();
			// Wait and go again
			synchronized (this) {
				try {
					wait(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error("AI runner interrupted...");
					break;
				}
			}
		}
	}

	public void process() {
		process(new Date());
	}

	public void process(Date tradeDate) {
		try {
			logger.info("AIRunner - processing");
			HibernateUtil.beginTransaction();
			AIStrategyFactory aiFactory = new AIStrategyFactory();
			List<Trader> aiTraders = traderDAO.getAITraderList();
			for (Trader trader : aiTraders) {
				List<Order> closedOrders = OrderDAO.getOpenOrdersByTrader(trader);
				if (closedOrders.size() > 0) {
					logger.info("AIRunner - removing outstanding orders: " + trader.getName());
				}
				for (Order order : closedOrders) {
					OrderDAO.deleteOrder(order);
				}
			}
			HibernateUtil.commitTransaction();

			HibernateUtil.beginTransaction();
			aiTraders = traderDAO.getAITraderList();
			List<Trader> localList = new ArrayList<Trader>(aiTraders);

			for (int i = 0; i < AI_TRADE_COUNT; i++) {
				int index = aiSelector.nextInt(localList.size());
				Trader trader = localList.get(index);
				trader = traderDAO.getTraderById(trader.getId());
				AITrader aiTrader = aiFactory.getStrategyByName(trader.getAiStrategyName());
				logger.info("AIRunner - Performing trades: " + trader.getName() + " -- " + aiTrader.getName());
				aiTrader.performTrades(trader, tradeDate);
				localList.remove(index);
				HibernateUtil.flushAndClearSession();
			}
			// Now have a 1 in CHANCE_OF_LIQUIDATION chance that a single trader
			// might liquidate
			// *all* their holdings.
			if (aiSelector.nextInt(CHANCE_OF_LIQUIDATION) == 0 || true) {
				int index = aiSelector.nextInt(localList.size());
				Trader trader = localList.get(index);
				trader = traderDAO.getTraderById(trader.getId());
				// Liquidate!
				AITrader aiTrader = new LiquididatingAI();
				aiTrader.performTrades(trader, tradeDate);
				trader.setCash(AppInitialiser.TRADER_LIQUIDATE_CASH);
				localList.remove(index);
				HibernateUtil.flushAndClearSession();
			}

			HibernateUtil.commitTransaction();
			logger.info("AIRunner - processing complete");
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			// logger.error("AI Runner - exception thrown! " + e.getMessage());
			ServerUtil.logError(logger, e);
			e.printStackTrace();
		} finally {
			logger.info("AI runner - processing finished");
		}
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

}
