package net.fidoandfido.engine.economicmodfiers;

import net.fidoandfido.model.StockExchangePeriod;

public class DefaultModifier implements EconomicModifier {

	@Override
	public void modifiyExchangePeriod(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod) {
		// Do not change anything!
	}

}
