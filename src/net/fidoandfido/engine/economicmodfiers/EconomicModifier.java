package net.fidoandfido.engine.economicmodfiers;

import net.fidoandfido.model.StockExchangePeriod;

public interface EconomicModifier {

	/**
	 * Modify the exchange period values, using the previous period if required.
	 * 
	 * @param currentPeriod
	 * @param previousPeriod
	 */
	public void modifiyExchangePeriod(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod);

	public void modifySectors(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod);

	boolean newCompanyToBeFounded(StockExchangePeriod currentPeriod);

}
