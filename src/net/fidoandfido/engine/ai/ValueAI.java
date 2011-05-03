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

public class ValueAI extends AITrader implements AITradeStrategy {

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
		// if the earning per share (as a percent) is above the stock market
		// prime interest rate, we buy
		// if the earning per share (as a percent) is below the stock market
		// prime interest rate, we sell
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
			long priceToEarningsRate = (expectedEarning / sharePrice) * 100;
			if (priceToEarningsRate > (company.getPrimeInterestRateBasisPoints() / 100)) {
				// This one is a buy!
				buy(trader, company, false);
			} else {
				// time to sell!
				sell(trader, company, false);
			}
		}
	}

}
