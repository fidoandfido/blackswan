package net.fidoandfido.engine.ai;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Order.OrderType;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;

public class NaiveAI implements AITradeStrategy {

	Logger logger = Logger.getLogger(getClass());

	
	public static final String Name = "Naive";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

	@Override
	public void performTrades(Trader trader) {
		List<PeriodEvent> recentEvents = PeriodPartInformationDAO.getLatestEvents(20, new Date());
		Set<Company> companiesProcessed = new HashSet<Company>();
		for (PeriodEvent periodEvent : recentEvents) {
			Company company = periodEvent.getCompany();
			if (companiesProcessed.contains(company)) {
				continue;
			}
			companiesProcessed.add(company);
			switch (periodEvent.getEventType()) {
			case CATASTROPHIC:
			case TERRIBLE:
				sell(trader, company, true);
				break;
			case POOR:
				sell(trader, company, false);
				break;
			case GOOD:
			case GREAT:
				buy(trader, company, false);
				break;
			case EXTRAORDINARY:
				buy(trader, company, true);
				break;
			case AVERAGE:
			}

		}

	}

	private void buy(Trader trader, Company company, boolean veryGood) {
		// We are going to sell all our shares!
		long askingPrice = company.getLastTradePrice();
		// Since we are selling, drop the price by 5 %
		askingPrice = adjustPrice(askingPrice, -5);
		long cash = trader.getCash();
		long committedFunds = 0;
		List<Order> orders = OrderDAO.getOpenOrdersByTrader(trader);
		for (Order order : orders) {
			if (order.getOrderType() == OrderType.BUY) {
				committedFunds = committedFunds + order.getOfferPrice() * order.getRemainingShareCount();
			}
		}
		long usableCash = cash - committedFunds;
		if (usableCash > askingPrice) {
			long maxShareCount = usableCash / askingPrice;
			long shareCount = veryGood ? maxShareCount : maxShareCount / 2;

			List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader, company);
			for (Order order : openOrders) {
				order.setActive(false);
				OrderDAO.saveOrder(order);
			}

			logger.info("naive ai: " + trader.getName() + " buys " + shareCount + " of " + company.getName() + " at " + askingPrice);
			Order buyOrder = new Order(trader, company, shareCount, askingPrice, Order.OrderType.BUY);
			OrderDAO.saveOrder(buyOrder);
			// Attempt to process the order...
			StockExchange exchange = company.getStockExchange();
			exchange.processOrder(buyOrder);
		}
	}

	private void sell(Trader trader, Company company, boolean veryBad) {
		ShareParcel parcel = ShareParcelDAO.getHoldingsByTraderForCompany(trader, company);
		if (parcel != null) {
			// We are going to sell all our shares!
			long shareCount = veryBad ? parcel.getShareCount() : parcel.getShareCount() / 2;
			long askingPrice = company.getLastTradePrice();
			// Since we are buying, raise the price by 5 %
			askingPrice = adjustPrice(askingPrice, 5);

			// Check if we have any open sell order for this company...
			List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader, company);
			for (Order order : openOrders) {
				order.setActive(false);
				OrderDAO.saveOrder(order);
			}
			logger.info("naive ai: " + trader.getName() + " sells " + shareCount + " of " + company.getName() + " at " + askingPrice);
			Order sellOrder = new Order(trader, company, shareCount, askingPrice, Order.OrderType.SELL);
			OrderDAO.saveOrder(sellOrder);

			// Attempt to process the order...
			StockExchange exchange = company.getStockExchange();
			exchange.processOrder(sellOrder);
			
		}
	}

	/**
	 * Adjust the price by the supplied percent
	 * 
	 * Do it retarded to avoid rounding errors - multiply it by 100
	 * 
	 * @param askingPrice
	 * @param percent
	 * @return
	 */
	private long adjustPrice(long val, int percent) {
		long delta = (val * percent);
		long bigVal = (100 * val) + delta;
		return bigVal / 100;
	}

	public static void testAdjustPrice() {

		NaiveAI ai = new NaiveAI();
		long foo = 30;
		System.out.println("foo: " + foo);
		long bar = ((foo / 100) * 5) + foo;
		long baz = ai.adjustPrice(foo, 5);
		System.out.println("foo + 5%:");
		System.out.println("BAR: " + bar);
		System.out.println("BAZ: " + baz);

	}
}
