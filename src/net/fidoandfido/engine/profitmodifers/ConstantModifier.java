package net.fidoandfido.engine.profitmodifers;

import net.fidoandfido.engine.event.EventData;
import net.fidoandfido.model.Company;
import net.fidoandfido.util.Constants.EventType;

public class ConstantModifier implements EventProfitModifier {

	public final static String NAME = "ConstantModifier";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fidoandfido.engine.ProfitModifier#adjustProfit(net.fidoandfido.util
	 * .Constants.EventType, long)
	 */
	@Override
	public EventData adjustProfit(EventType eventType, EventData data, Company company, long eventCount) {
		// Dont actually adjust the profit :)
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.ProfitModifier#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fidoandfido.engine.ProfitModifier#setVolatility(net.fidoandfido.engine
	 * .ProfitModifier.Volatility)
	 */
	@Override
	public void setVolatility(Volatility volatility) {
		// Ignore it!
	}

}
