package net.fidoandfido.engine.ai;

import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.Trader;

public class ReactiveAI extends AITrader {

	public static final String Name = "Reactive";

	private PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();

	@Override
	public void performTrades(Trader trader) {
		// Get recent events, and process in a naive reactionary way.
		// If shares are overvalued, and we have them, sell them
		// If shares are undervalued and we dont, buy them.

		List<PeriodEvent> recentEvents = periodPartInformationDAO.getLatestEvents(20, new Date());

		for (PeriodEvent periodEvent : recentEvents) {
			Company company = periodEvent.getCompany();

			long sharePrice = company.getLastTradePrice();

			long equity = company.getCapitalisation();
			long outstandingShares = company.getOutstandingShares();
			long shareEquityValue = equity / outstandingShares;

			/**
			 * So, assuming equity of $50,000 and 10,0000 shares, that would be
			 * a value of $5 per share. Since we expect a 10% return, a share
			 * price of up to 10 % more is acceptable.
			 */

			if (shareEquityValue > sharePrice) {
				// Instant buy!!!
				buy(trader, company, true);
				continue;
			}

			if (shareEquityValue > (sharePrice * 110 / 100)) {
				buy(trader, company, false);
				continue;
			}
			if ((sharePrice * 3 / 2) > shareEquityValue) {
				sell(trader, company, false);
				continue;
			}
			if ((sharePrice * 2) > shareEquityValue) {
				sell(trader, company, true);
				continue;
			}

		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

}
