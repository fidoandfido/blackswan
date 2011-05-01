package net.fidoandfido.engine.profitmodifers;

import net.fidoandfido.engine.event.EventData;
import net.fidoandfido.model.Company;
import net.fidoandfido.util.Constants.EventType;

public interface EventProfitModifier {

	public enum Volatility {
		LOWEST, LOW, MODERATE, HIGH, EXTREME, RIDICULOUS
	}

	public String getName();

	public void setVolatility(Volatility volatility);

	public EventData adjustProfit(EventType eventType, EventData eventData, Company company, long eventCount);
}
