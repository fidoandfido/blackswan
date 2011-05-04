package net.fidoandfido.engine.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Trader;
import net.fidoandfido.util.ServerUtil;
import net.fidoandfido.util.WebPageUtil;

import org.apache.log4j.Logger;

public class AITester {

	static Logger logger = Logger.getLogger(AITester.class);

	private TraderDAO traderDAO = new TraderDAO();

	// Seeded (as always!)
	private Random aiSelector = new Random(17);

	// Only do 10 traders each time
	private static final int AI_TRADE_COUNT = 10;

	public static void main(String argv[]) {
		AITester runner = new AITester();
		runner.runTest();
	}

	private void runTest() {
		HibernateUtil.connectToDB();
		AIRunner runner = new AIRunner();
		try {
			logger.info("AIRunner - processing");

			AIStrategyFactory aiFactory = new AIStrategyFactory();

			HibernateUtil.beginTransaction();
			List<Trader> aiTraders = traderDAO.getAITraderList();

			List<Trader> localList = new ArrayList<Trader>(aiTraders);

			for (int i = 0; i < AI_TRADE_COUNT; i++) {
				int index = aiSelector.nextInt(localList.size());
				Trader trader = localList.get(index);
				AITrader aiTrader = aiFactory.getStrategyByName(trader.getAiStrategyName());
				logger.info("AIRunner - Performing trades: " + trader.getName() + " -- " + aiTrader.getName());
				aiTrader.setExecutor(new TestTradeExecutor());
				aiTrader.performTrades(trader);
				localList.remove(index);
			}
			HibernateUtil.commitTransaction();
			logger.info("AIRunner - processing complete");
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			// logger.error("AI Runner - exception thrown! " + e.getMessage());
			ServerUtil.logError(logger, e);
		} finally {
			logger.info("AI runner - processing finished");
		}
	}

	private static class TestTradeExecutor implements AITrader.AITradeExecutor {

		@Override
		public void executeBuy(Trader trader, Company company, boolean veryGood) {
			// TODO Auto-generated method stub
			System.out.println();
			System.out.println();
			System.out.println("TRADER BUYING SHARES!  " + (veryGood ? "Very good" : "normal"));
			print(trader, company);
		}

		@Override
		public void executeSell(Trader trader, Company company, boolean veryBad) {
			// TODO Auto-generated method stub
			System.out.println();
			System.out.println();
			System.out.println("TRADER SELLING SHARES!  " + (veryBad ? "Very good" : "normal"));
			print(trader, company);
		}

		private void print(Trader trader, Company company) {

			System.out.println("Trader Strategy: " + trader.getAiStrategyName());
			System.out.println("Company info for:" + company.getName());
			System.out.println("share price:" + WebPageUtil.formatCurrency(company.getLastTradePrice()));
			System.out.println("prev change:" + WebPageUtil.formatCurrency(company.getLastTradeChange()));
			System.out.println("Book value: " + WebPageUtil.formatCurrency(company.getShareBookValue()));
			System.out.println("Expected share earnings: " + WebPageUtil.formatCurrency(company.getExpectedEarningsPerShare()));
			System.out.println("Earnings Return (as %): " + (company.getCurrentPeriod().getStartingExpectedProfit() * 100 / company.getCapitalisation()));
			System.out.println("Prime interest rate: " + company.getPrimeInterestRateBasisPoints() / 100 + "%");
		}
	}

}
