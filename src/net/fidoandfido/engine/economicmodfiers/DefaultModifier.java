package net.fidoandfido.engine.economicmodfiers;

import net.fidoandfido.model.StockExchangePeriod;

public class DefaultModifier implements EconomicModifier {

	@Override
	public void modifiyExchangePeriod(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod) {
		// Do not change anything!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fidoandfido.engine.economicmodfiers.EconomicModifier#modifySectors(net.fidoandfido.model.StockExchangePeriod,
	 * net.fidoandfido.model.StockExchangePeriod)
	 */
	@Override
	public void modifySectors(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod) {
		// Do not change anything!
	}

	@Override
	public boolean newCompanyToBeFounded(StockExchangePeriod currentPeriod) {
		// TODO Auto-generated method stub
		return false;
	}

}
