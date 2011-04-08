package net.fidoandfido.engine.ai;

import java.util.List;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

/**
 * @author andy
 * 
 */
public class AIRunner implements Runnable {

	Logger logger = Logger.getLogger(getClass());

	public static long DEFUALT_TIMEOUT = 5000;

	private final long timeout;

	private boolean running = true;

	public AIRunner() {
		timeout = DEFUALT_TIMEOUT;
	}

	public AIRunner(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public void run() {
		while (running) {
			try {
			HibernateUtil.beginTransaction();
			process();
			HibernateUtil.commitTransaction();
			} catch (Exception e) {
				logger.error("AI Runner - exception thrown! " + e.getMessage());
			} finally {
				logger.info("AI runner - processing finished");
			}
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
		logger.info("AIRunner - processing");
		AIStrategyFactory aiFactory = new AIStrategyFactory();
		List<Trader> aiTraders = TraderDAO.getAITraderList();
		for (Trader trader : aiTraders) {
			AITradeStrategy strategy = aiFactory.getStrategyByName(trader.getAiStrategyName());
			logger.info("AIRunner - Performing trades: " + trader.getName() + " -- " + trader.getAiStrategyName() + " -- " + strategy.getName());
			strategy.performTrades(trader);
		}
		logger.info("AIRunner - processing complete");
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
