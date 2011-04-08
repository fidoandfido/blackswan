package net.fidoandfido.engine.profitmodifers;

import net.fidoandfido.engine.ProfitModifier;
import net.fidoandfido.util.Constants.EventType;

public class ConstantModifier implements ProfitModifier {

	public final static String NAME = "ConstantModifier";

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.ProfitModifier#adjustProfit(net.fidoandfido.util.Constants.EventType, long)
	 */
	@Override
	public long adjustProfit(EventType eventType, long profit) {
		// Dont actually adjust the profit :)
		return profit;
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
	 * @see net.fidoandfido.engine.ProfitModifier#setVolatility(net.fidoandfido.engine.ProfitModifier.Volatility)
	 */
	@Override
	public void setVolatility(Volatility volatility) {
		// Ignore it!
	}

}
