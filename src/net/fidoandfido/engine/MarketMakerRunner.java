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

	private TraderDAO traderDAO;
	private ShareParcelDAO shareParcelDAO;
	private OrderDAO orderDAO;

	public MarketMakerRunner() {
		this(DEFUALT_TIMEOUT);
	}

	public MarketMakerRunner(long timeout) {
		this.timeout = timeout;
		traderDAO = new TraderDAO();
		shareParcelDAO = new ShareParcelDAO();
		orderDAO = new OrderDAO();
	}

	@Override
	public void run() {
		while (running) {
			try {
				logger.info("Processing starting");
				HibernateUtil.beginTransaction();
				process();
				HibernateUtil.commitTransaction();
			} catch (Exception e) {
				HibernateUtil.rollbackTransaction();
				logger.error("Exception thrown! Type: " + e.getClass());
				logger.error("Exception stack: ");
				StackTraceElement stack[] = e.getStackTrace();
				for (int i = 0; i < 10 && i < stack.length; i++) {
					StackTraceElement element = stack[i];
					if (!element.isNativeMethod()) {
						logger.error(element.getFileName() + " --> " + element.getClassName() + " --> " + element.getMethodName() + " --> "
								+ element.getLineNumber());
					}
				}

				// + e.getStackTrace()[0].getMethodName());
			} finally {
				logger.info("Processing finished.");
			}
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
		logger.info("Market maker - Processing.");
		// Check if there are any open orders...
		Trader marketMaker = traderDAO.getMarketMaker();
		List<Order> openOrders = OrderDAO.getOpenOrders();
		for (Order order : openOrders) {
			if (order.getTrader().equals(marketMaker)) {
				// This is an old order... inactive it!
				order.setActive(false);
				orderDAO.saveOrder(order);
				continue;
			}

			long offerPrice = order.getOfferPrice();
			Company company = order.getCompany();
			// Will accept any order by an AI
			Trader trader = order.getTrader();
			if (!trader.isAITrader()) {
				if (order.getOrderType().equals(OrderType.BUY)) {
					// For humans, to buy the offer price must be at least the
					// last market trade
					if (offerPrice < company.getLastTradePrice()) {
						continue;
					}
				} else {
					// For humans, to sell the offer price must be at most the
					// last market trade
					if (offerPrice > company.getLastTradePrice()) {
						continue;
					}
				}
			}

			long shareCount = order.getRemainingShareCount();

			Order marketMakerOrder;
			if (order.getOrderType().equals(OrderType.BUY)) {
				// If they are trying to buy, make sure we have some shares to
				// sell!
				ShareParcel mmHoldings = shareParcelDAO.getHoldingsByTraderForCompany(marketMaker, company);
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
			logger.trace("saving market maker order");
			orderDAO.saveOrder(marketMakerOrder);
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
