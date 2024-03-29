package net.fidoandfido.engine.ai;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodQuarter;
import net.fidoandfido.model.Trader;

public class RandomAI extends AITrader {

	private static final int SELL = 1;

	private static final int BUY = 0;

	public static final String NAME = "Random";

	private PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();

	@Override
	public String getName() {
		return NAME;
	}

	private Random sellOrBuyRandom = new Random();

	public static int CHANCE_TO_BUY_OR_SELL = 3;

	@Override
	public void performTrades(Trader trader, Date tradeDate) {
		// So basically, we are going to get some companies, look at their
		// earnings per share,
		// if the earning per share (as a percent) is above the stock market
		// prime interest rate, we buy
		// if the earning per share (as a percent) is below the stock market
		// prime interest rate, we sell
		Set<Company> companySet = new HashSet<Company>();

		List<PeriodQuarter> recentEvents = periodPartInformationDAO.getLatestEvents(20, tradeDate);
		for (PeriodQuarter periodQuarter : recentEvents) {
			Company company = periodQuarter.getCompany();

			if (company.isTrading() == false) {
				continue;
			}
			if (companySet.contains(company)) {
				continue;
			}
			companySet.add(company);
			if (company.getStockExchange().isUpdating()) {
				continue;
			}

			int decision = sellOrBuyRandom.nextInt(CHANCE_TO_BUY_OR_SELL);
			if (decision == BUY) {
				// This one is a buy!
				adjustPriceAndBuy(trader, company, BUY_RATE, SMALL_BUY_COUNT, tradeDate);
			} else if (decision == SELL) {
				// time to sell!
				adjustPriceAndSell(trader, company, SELL_RATE, SMALL_SELL_COUNT, tradeDate);
			}
		}
	}

}
