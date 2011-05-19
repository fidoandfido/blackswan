package net.fidoandfido.engine.ai;

import java.util.List;

import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.engine.ai.AITrader.AITradeExecutor;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Order.OrderType;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

public class DefaultAITradeExecutor implements AITradeExecutor {

	Logger logger = Logger.getLogger(getClass());

	private OrderDAO orderDAO = new OrderDAO();
	private ShareParcelDAO shareParcelDAO = new ShareParcelDAO();

	public static final long DEFAULT_BUY_COUNT = 1000;
	public static final long DEFAULT_SELL_COUNT = 1000;
	public static final long MAX_BUY_COUNT = 2000;

	public static final int VERY_GOOD_BUY_RATE = 10;
	public static final int GOOD_BUY_RATE = 5;
	public static final int BUY_RATE = 2;
	public static final int SELL_RATE = -2;
	public static final int BAD_SELL_RATE = -5;
	public static final int VERY_BAD_SELL_RATE = -10;

	@Override
	public void executeBuy(Trader trader, Company company, int rate, long preferredShareCount) {
		// We are going to buy some shares!
		long offerPrice = company.getLastTradePrice();
		// Since we are buying, adjust the price...
		if (rate < 0) {
			logger.error("Trader " + trader + " trying to buy shares with negative adjusted price rate!" + rate);
		}
		offerPrice = adjustPrice(offerPrice, rate < 0 ? 0 : rate);
		logger.info("AI Buying shares:  " + trader.getAiStrategyName() + " co: " + company.getName() + " at: " + offerPrice);
		if (offerPrice < 10) {
			offerPrice = 10;
		}

		long cash = trader.getCash();
		long committedFunds = 0;
		List<Order> orders = OrderDAO.getOpenOrdersByTrader(trader);
		for (Order order : orders) {
			if (order.getOrderType() == OrderType.BUY) {
				committedFunds = committedFunds + order.getOfferPrice() * order.getRemainingShareCount();
			}
		}
		long usableCash = cash - committedFunds;
		if (usableCash > offerPrice) {
			long shareCount = usableCash / offerPrice;
			if (shareCount > preferredShareCount) {
				shareCount = preferredShareCount;
			}
			shareCount = shareCount > MAX_BUY_COUNT ? MAX_BUY_COUNT : preferredShareCount;

			if (shareCount < 10) {
				return;
			}

			List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader, company);
			for (Order order : openOrders) {
				order.setActive(false);
				orderDAO.saveOrder(order);
			}

			logger.info("ai: " + trader.getAiStrategyName() + "(" + trader.getName() + ") buys " + shareCount + " of " + company.getName() + " at "
					+ offerPrice);
			Order buyOrder = new Order(trader, company, shareCount, offerPrice, Order.OrderType.BUY);
			orderDAO.saveOrder(buyOrder);
			// Attempt to process the order...
			StockExchange exchange = company.getStockExchange();
			exchange.processOrder(buyOrder);
		}
	}

	@Override
	public void executeSell(Trader trader, Company company, int rate, long shareCount) {
		ShareParcel parcel = shareParcelDAO.getHoldingsByTraderForCompany(trader, company);
		if (parcel != null) {
			// We are going to sell all our shares!
			if (shareCount < 10) {
				shareCount = parcel.getShareCount();
			}

			if (shareCount == 0) {
				return;
			}

			long askingPrice = company.getLastTradePrice();
			// Since we are selling, drop the price...
			if (rate > 0) {
				logger.error("Trader " + trader + " trying to buy shares with positive adjusted price rate!" + rate);
			}
			askingPrice = adjustPrice(askingPrice, rate > 0 ? 0 : rate);
			if (askingPrice == 0) {
				askingPrice = 1;
			}

			// Check if we have any open sell order for this company...
			List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader, company);
			for (Order order : openOrders) {
				order.setActive(false);
				orderDAO.saveOrder(order);
			}
			logger.info("ai: " + trader.getAiStrategyName() + "(" + trader.getName() + ") sells " + shareCount + " of " + company.getName() + " at "
					+ askingPrice);
			Order sellOrder = new Order(trader, company, shareCount, askingPrice, Order.OrderType.SELL);
			orderDAO.saveOrder(sellOrder);

			// Attempt to process the order...
			StockExchange exchange = company.getStockExchange();
			exchange.processOrder(sellOrder);

		}
	}

	/**
	 * Adjust the price by the supplied percent
	 * 
	 * @param askingPrice
	 * @param percent
	 * @return
	 */
	private static long adjustPrice(long val, int percent) {
		long delta = (val * percent);
		long bigVal = (100 * val) + delta;
		return bigVal / 100;
	}
}
