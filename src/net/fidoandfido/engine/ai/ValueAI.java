package net.fidoandfido.engine.ai;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;

public class ValueAI extends AITrader {

	public static final String Name = "Value";
	Logger logger = Logger.getLogger(getClass());

	@Override
	public String getName() {
		return Name;
	}

	private PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();

	@Override
	public void performTrades(Trader trader) {
		// So basically, we are going to get some companies, look at their
		// earnings per share,
		// We are looking at the book value + the current earning % as a
		// premium, minimum price is the book value.

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

			long bookValue = company.getShareBookValue();
			long sharePrice = company.getLastTradePrice();
			long expectedEarning = company.getExpectedEarningsPerShare();
			long priceToEarningsRate = (expectedEarning * 100 / sharePrice);

			// bookvalue = 1000
			// p2e = 5
			// fp = ((1000 * (100 + 5)) / 100) = 1050 <- correct!
			long fairPrice = ((bookValue * (100 + priceToEarningsRate)) / 100);
			if (fairPrice < bookValue) {
				fairPrice = bookValue;
			}

			if (fairPrice < company.getLastTradePrice()) {
				// This one is a buy!
				buy(trader, company, DefaultAITradeExecutor.GOOD_BUY_RATE, DefaultAITradeExecutor.DEFAULT_BUY_COUNT);
			} else {
				// time to sell!
				sell(trader, company, DefaultAITradeExecutor.SELL_RATE, DefaultAITradeExecutor.DEFAULT_SELL_COUNT);
			}
		}
	}

}
