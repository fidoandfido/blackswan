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

public class NaiveAI extends AITrader implements AITradeStrategy {

	Logger logger = Logger.getLogger(getClass());

	public static final String Name = "Naive";

	private PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

	@Override
	public void performTrades(Trader trader) {
		List<PeriodEvent> recentEvents = periodPartInformationDAO.getLatestEvents(20, new Date());
		Set<Company> companiesProcessed = new HashSet<Company>();
		for (PeriodEvent periodEvent : recentEvents) {
			Company company = periodEvent.getCompany();
			if (companiesProcessed.contains(company)) {
				continue;
			}
			companiesProcessed.add(company);
			if (company.getStockExchange().isUpdating()) {
				continue;
			}

			switch (periodEvent.getEventType()) {
			case CATASTROPHIC:
			case TERRIBLE:
				sell(trader, company, true);
				break;
			case POOR:
				sell(trader, company, false);
				break;
			case GOOD:
			case GREAT:
				buy(trader, company, false);
				break;
			case EXTRAORDINARY:
				buy(trader, company, true);
				break;
			case AVERAGE:
			}

		}

	}

}
