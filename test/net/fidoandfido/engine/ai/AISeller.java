package net.fidoandfido.engine.ai;

import java.util.List;
import java.util.Random;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.engine.ai.AITrader.AITradeExecutor;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

public class AISeller {

	public class SellExecutor implements AITradeExecutor {

		private ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
		private OrderDAO orderDAO = new OrderDAO();

		@Override
		public void executeBuy(Trader trader, Company company, boolean veryGood) {
			// TODO Auto-generated method stub

		}

		@Override
		public void executeSell(Trader trader, Company company, boolean veryBad) {
			ShareParcel parcel = shareParcelDAO.getHoldingsByTraderForCompany(trader, company);
			if (parcel != null) {
				// We are going to sell all our shares!
				long shareCount = 2000;
				if (shareCount > parcel.getShareCount()) {
					shareCount = parcel.getShareCount() / 2;
				}
				if (shareCount < 10) {
					shareCount = parcel.getShareCount();
				}

				if (shareCount == 0) {
					return;
				}

				long askingPrice = company.getLastTradePrice();
				// Since we are selling, drop the price by 5 %
				askingPrice = adjustPrice(askingPrice, 5);
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

		private long adjustPrice(long val, int percent) {
			long delta = (val * percent);
			long bigVal = (100 * val) + delta;
			return bigVal / 100;
		}

	}

	static Logger logger = Logger.getLogger(AITester.class);

	private TraderDAO traderDAO = new TraderDAO();

	// Seeded (as always!)
	private Random aiSelector = new Random(17);

	private CompanyDAO companyDAO = new CompanyDAO();

	// Only do 10 traders each time
	private static final int AI_TRADE_COUNT = 10;

	public static void main(String argv[]) {
		AISeller seller = new AISeller();
		seller.runTest();
	}

	private void runTest() {
		HibernateUtil.connectToDB();
		AIRunner runner = new AIRunner();
		try {
			logger.info("AISeller - processing");

			AIStrategyFactory aiFactory = new AIStrategyFactory();

			HibernateUtil.beginTransaction();
			List<Trader> aiTraders = traderDAO.getAITraderList();
			HibernateUtil.commitTransaction();
			for (Trader trader : aiTraders) {
				HibernateUtil.beginTransaction();
				// load the trader in the current hibernate context
				trader = traderDAO.getTraderByName(trader.getName());
				AITrader aiTrader = aiFactory.getStrategyByName(trader.getAiStrategyName());
				aiTrader.setExecutor(new SellExecutor());
				List<Company> companyList = companyDAO.getCompanyList();
				for (Company company : companyList) {
					aiTrader.sell(trader, company, true);
				}
				HibernateUtil.commitTransaction();
			}
			logger.info("AIRunner - processing complete");
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			// logger.error("AI Runner - exception thrown! " + e.getMessage());
			e.printStackTrace();
			// ServerUtil.logError(logger, e);
		} finally {
			logger.info("AI runner - processing finished");
		}

	}
}
