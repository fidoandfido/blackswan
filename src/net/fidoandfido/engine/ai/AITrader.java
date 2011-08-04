package net.fidoandfido.engine.ai;

import java.util.Date;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

public abstract class AITrader implements AITradeStrategy {

	Logger logger = Logger.getLogger(getClass());

	public static final long DEFAULT_BUY_COUNT = 1000;
	public static final long DEFAULT_SELL_COUNT = 1000;
	public static final long MAX_BUY_COUNT = 2000;

	public static final long SMALL_BUY_COUNT = 100;
	public static final long SMALL_SELL_COUNT = 100;

	public static final int VERY_GOOD_BUY_RATE = 10;
	public static final int GOOD_BUY_RATE = 5;
	public static final int BUY_RATE = 2;
	public static final int SELL_RATE = -2;
	public static final int BAD_SELL_RATE = -5;
	public static final int VERY_BAD_SELL_RATE = -10;

	private AITradeExecutor executor = new AITradeExecutor();

	protected void buy(Trader trader, Company company, long price, long shareCount, Date date) {
		executor.executeBuy(trader, company, price, shareCount, date);
	}

	protected void adjustPriceAndBuy(Trader trader, Company company, int rate, long shareCount, Date date) {
		long price = adjustPrice(company.getLastTradePrice(), rate);
		executor.executeBuy(trader, company, price, shareCount, date);
	}

	protected void sell(Trader trader, Company company, long price, long shareCount, Date date) {
		executor.executeSell(trader, company, price, shareCount, date);
	}

	protected void adjustPriceAndSell(Trader trader, Company company, int rate, long shareCount, Date date) {
		long price = adjustPrice(company.getLastTradePrice(), rate);
		executor.executeSell(trader, company, price, shareCount, date);
	}

	/**
	 * Adjust the price by the supplied percent
	 * 
	 * @param askingPrice
	 * @param percent
	 * @return
	 */
	public long adjustPrice(long val, int percent) {
		long delta = (val * percent);
		long bigVal = (100 * val) + delta;
		return bigVal / 100;
	}

}
