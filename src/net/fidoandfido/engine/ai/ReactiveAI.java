package net.fidoandfido.engine.ai;

import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.model.PeriodEvent;
import net.fidoandfido.model.Trader;

public class ReactiveAI implements AITradeStrategy {

	public static final String Name = "Reactive";

	@Override
	public void performTrades(Trader trader) {
		// Get recent events, and process in a naive reactionary way.
		// If shares are overvalued, and we have them, sell them
		// If shares are undervalued and we dont, buy them.

		List<PeriodEvent> recentEvents = PeriodPartInformationDAO.getLatestEvents(20, new Date());

		for (PeriodEvent periodEvent : recentEvents) {
			periodEvent.getExpectedProfit();

		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

}
