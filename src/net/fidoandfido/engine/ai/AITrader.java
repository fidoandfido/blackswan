package net.fidoandfido.engine.ai;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

public abstract class AITrader implements AITradeStrategy {

	public static interface AITradeExecutor {
		public void executeBuy(Trader trader, Company company, int adjustPriceRate, long shareCount);

		public void executeSell(Trader trader, Company company, int adjustPriceRate, long shareCount);
	}

	Logger logger = Logger.getLogger(getClass());

	private AITradeExecutor executor = new DefaultAITradeExecutor();

	/**
	 * @return the executor
	 */
	public AITradeExecutor getExecutor() {
		return executor;
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setExecutor(AITradeExecutor executor) {
		this.executor = executor;
	}

	protected void buy(Trader trader, Company company, int adjustPriceRate, long shareCount) {
		executor.executeBuy(trader, company, adjustPriceRate, shareCount);
	}

	protected void sell(Trader trader, Company company, int adjustPriceRate, long shareCount) {
		executor.executeSell(trader, company, adjustPriceRate, shareCount);
	}
}
