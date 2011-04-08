package net.fidoandfido.engine;

import net.fidoandfido.util.Constants.EventType;

public interface ProfitModifier {

	public enum Volatility {
		LOWEST, LOW, MODERATE, HIGH, EXTREME, RIDICULOUS
	}

	public String getName();

	public long adjustProfit(EventType eventType, long profit);

	public void setVolatility(Volatility volatility);
}
