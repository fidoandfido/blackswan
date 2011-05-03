package net.fidoandfido.engine.economicmodfiers;

import net.fidoandfido.model.StockExchangePeriod;

public interface EconomicModifier {

	public void modifiyExchangePeriod(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod);

}
