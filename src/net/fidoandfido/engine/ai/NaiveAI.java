package net.fidoandfido.engine.ai;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodQuarter;
import net.fidoandfido.model.Trader;

public class NaiveAI extends AITrader {

	public static final String Name = "Naive";

	private PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

	@Override
	public void performTrades(Trader trader, Date tradeDate) {
		List<PeriodQuarter> recentEvents = periodPartInformationDAO.getLatestEvents(20, tradeDate);
		Set<Company> companiesProcessed = new HashSet<Company>();
		for (PeriodQuarter periodQuarter : recentEvents) {

			Company company = periodQuarter.getCompany();
			if (company.isTrading() == false) {
				continue;
			}
			if (companiesProcessed.contains(company)) {
				continue;
			}
			companiesProcessed.add(company);
			if (company.getStockExchange().isUpdating()) {
				continue;
			}

			switch (periodQuarter.getEventType()) {
			case CATASTROPHIC:
				adjustPriceAndSell(trader, company, VERY_BAD_SELL_RATE, DEFAULT_SELL_COUNT, tradeDate);
				break;
			case TERRIBLE:
				adjustPriceAndSell(trader, company, BAD_SELL_RATE, DEFAULT_SELL_COUNT, tradeDate);
				break;
			case POOR:
				adjustPriceAndSell(trader, company, SELL_RATE, DEFAULT_SELL_COUNT, tradeDate);
				break;
			case GOOD:
				adjustPriceAndBuy(trader, company, BUY_RATE, DEFAULT_BUY_COUNT, tradeDate);
				break;
			case GREAT:
				adjustPriceAndBuy(trader, company, GOOD_BUY_RATE, DEFAULT_BUY_COUNT, tradeDate);
				break;
			case EXTRAORDINARY:
				adjustPriceAndBuy(trader, company, VERY_GOOD_BUY_RATE, MAX_BUY_COUNT, tradeDate);
				break;
			case AVERAGE:
			}

		}

	}

}
