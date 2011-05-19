package net.fidoandfido.engine.ai;

import java.util.List;

import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;

public class LiquididatingAI extends AITrader implements AITradeStrategy {

	public static final String NAME = "Liquidating";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void performTrades(Trader trader) {
		ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
		OrderDAO orderDAO = new OrderDAO();
		Iterable<ShareParcel> holdings = shareParcelDAO.getHoldingsByTrader(trader);
		for (ShareParcel shareParcel : holdings) {
			// We are going to sell all our shares!
			long shareCount = shareParcel.getShareCount();
			Company company = shareParcel.getCompany();
			long askingPrice = company.getLastTradePrice();
			// Since we are selling, drop the price by the bad sell rate
			long bigDelta = (askingPrice * DefaultAITradeExecutor.SELL_RATE);
			long bigAskingPrice = (100 * askingPrice) + bigDelta;
			askingPrice = bigAskingPrice / 100;
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
}
