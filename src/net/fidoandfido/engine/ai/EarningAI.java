package net.fidoandfido.engine.ai;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.Trader;

public class EarningAI extends AITrader {

	public static final String Name = "Earnings";

	private PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();

	@Override
	public void performTrades(Trader trader) {
		// Get recent events, and process the companies.
		// We look ONLY at earning per share.
		// If it is above the cash rate - we buy.
		// If it is below, we sell.

		Set<Company> companySet = new HashSet<Company>();
		List<PeriodEvent> recentEvents = periodPartInformationDAO.getLatestEvents(20, new Date());

		for (PeriodEvent periodEvent : recentEvents) {
			Company company = periodEvent.getCompany();
			if (companySet.contains(company)) {
				continue;
			}
			companySet.add(company);
			if (company.getStockExchange().isUpdating()) {
				continue;
			}

			long sharePrice = company.getLastTradePrice();
			long expectedEarning = company.getExpectedEarningsPerShare();
			long priceToEarningsRate = (expectedEarning * 100 / sharePrice);
			if (priceToEarningsRate > (company.getPrimeInterestRateBasisPoints() / 100)) {
				// This one is a buy!
				long delta = (priceToEarningsRate - (company.getPrimeInterestRateBasisPoints() / 100));
				if (delta > 5) {
					// More than 5 % spread between earnings and cash rate...
					buy(trader, company, DefaultAITradeExecutor.VERY_GOOD_BUY_RATE, DefaultAITradeExecutor.DEFAULT_BUY_COUNT);
				} else {
					buy(trader, company, DefaultAITradeExecutor.GOOD_BUY_RATE, DefaultAITradeExecutor.DEFAULT_BUY_COUNT);
				}
			} else {
				// time to sell!
				sell(trader, company, DefaultAITradeExecutor.SELL_RATE, DefaultAITradeExecutor.DEFAULT_SELL_COUNT);
			}
		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

}
