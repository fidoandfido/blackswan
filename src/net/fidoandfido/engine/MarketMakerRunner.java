package net.fidoandfido.engine;

import java.util.List;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Order.OrderType;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

public class MarketMakerRunner implements Runnable {

	Logger logger = Logger.getLogger(getClass());

	public static long DEFUALT_TIMEOUT = 20000;

	private final long timeout;
	private OrderProcessor processor = new OrderProcessor();

	private boolean running = true;

	public MarketMakerRunner() {
		timeout = DEFUALT_TIMEOUT;
	}

	public MarketMakerRunner(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public void run() {
		while (running) {
			HibernateUtil.beginTransaction();
			process();
			HibernateUtil.commitTransaction();
			// Wait 20 seconds and go again
			synchronized (this) {
				try {
					wait(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error("Market maker runner interrupted...");
					break;
				}
			}
		}
	}

	/**
	 * Check if there are any outstanding share offers that we can take.
	 */
	public void process() {
		logger.info("Processing.");
		// Check if there are any open orders...
		Trader marketMaker = TraderDAO.getTraderByName("MarketMaker");
		List<Order> openOrders = OrderDAO.getOpenOrders();
		for (Order order : openOrders) {
			if (order.getTrader().equals(marketMaker)) {
				// This is an old order... inactive it!
				order.setActive(false);
				OrderDAO.saveOrder(order);
				continue;
			}
			long offerPrice = order.getOfferPrice();
			Company company = order.getCompany();
			// Will try to accept any order within... say... 10% of the current price.
			long maxDelta = company.getLastTradePrice() / 10;
			if (maxDelta == 0) {
				maxDelta = 1;
			}

			// Get the delta (as a positive price)
			long delta = offerPrice - company.getLastTradePrice();
			if (delta < 0) {
				delta = delta * -1;
			}

			if (delta > maxDelta) {
				continue;
			}
			// Okay, the price is close enough, lets keep going
			long shareCount = order.getRemainingShareCount();

			Order marketMakerOrder;
			if (order.getOrderType().equals(OrderType.BUY)) {
				// If they are trying to buy, make sure we have some shares to sell!
				ShareParcel mmHoldings = ShareParcelDAO.getHoldingsByTraderForCompany(marketMaker, company);
				if (mmHoldings == null) {
					continue;
				}
				// Make sure we have enough shares...

				if (mmHoldings.getShareCount() <= shareCount) {
					if (!order.isAllowPartialOrder()) {
						continue;
					}
					shareCount = mmHoldings.getShareCount();
				}
				logger.info("Market maker is selling some shares...");
				marketMakerOrder = new Order(marketMaker, company, shareCount, offerPrice, OrderType.SELL);
			} else {
				logger.info("Market maker is buying some shares....");
				marketMakerOrder = new Order(marketMaker, company, shareCount, offerPrice, OrderType.BUY);
			}
			OrderDAO.saveOrder(marketMakerOrder);
			processor.processOrder(marketMakerOrder);
		}
		logger.info("Processing complete.");
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
