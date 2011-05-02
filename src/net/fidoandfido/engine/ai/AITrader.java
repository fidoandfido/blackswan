package net.fidoandfido.engine.ai;

import java.util.List;

import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Order.OrderType;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

public abstract class AITrader {

	Logger logger = Logger.getLogger(getClass());

	private OrderDAO orderDAO = new OrderDAO();
	private ShareParcelDAO shareParcelDAO = new ShareParcelDAO();

	private static final long MAX_BUY_COUNT = 2000;

	private static final int VERY_GOOD_BUY_RATE = 10;
	private static final int GOOD_BUY_RATE = 5;
	private static final int BAD_SELL_RATE = -5;
	private static final int VERY_BAD_SELL_RATE = -10;

	protected void buy(Trader trader, Company company, boolean veryGood) {
		// We are going to buy some shares!
		long offerPrice = company.getLastTradePrice();
		// Since we are buying, adjust the price...
		offerPrice = adjustPrice(offerPrice, veryGood ? VERY_GOOD_BUY_RATE : GOOD_BUY_RATE);
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
			long maxShareCount = usableCash / offerPrice;
			long shareCount = veryGood ? maxShareCount : maxShareCount / 2;
			maxShareCount = maxShareCount > MAX_BUY_COUNT ? MAX_BUY_COUNT : maxShareCount;

			if (maxShareCount < 10) {
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

	protected void sell(Trader trader, Company company, boolean veryBad) {
		ShareParcel parcel = shareParcelDAO.getHoldingsByTraderForCompany(trader, company);
		if (parcel != null) {
			// We are going to sell all our shares!
			long shareCount = veryBad ? parcel.getShareCount() : parcel.getShareCount() / 2;
			if (shareCount < 10) {
				shareCount = parcel.getShareCount();
			}

			if (shareCount == 0) {
				return;
			}

			long askingPrice = company.getLastTradePrice();
			// Since we are selling, drop the price by 5 %
			askingPrice = adjustPrice(askingPrice, veryBad ? VERY_BAD_SELL_RATE : BAD_SELL_RATE);
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
