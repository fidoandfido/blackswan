package net.fidoandfido.engine.economicmodfiers;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.StockExchangePeriod;

public interface EconomicModifier {

	/**
	 * Modify the exchange period values, using the previous period if required.
	 * 
	 * @param currentPeriod
	 * @param previousPeriod
	 */
	public void modifiyExchangePeriod(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod);

	/**
	 * Modify the sectors for the current stock exchange period.
	 * 
	 * @param currentPeriod
	 *            The period containing sectors to modify
	 * @param previousPeriod
	 *            Previous period for historical comparison
	 */
	public void modifySectors(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod);

	/**
	 * Return true if a new company should be founded. This company will then be put into IPO status, to start trading
	 * normally next financial period.
	 * 
	 * @param currentPeriod
	 *            The current period that the company will be founded.
	 * @return
	 */
	boolean newCompanyToBeFounded(StockExchangePeriod currentPeriod);

	/**
	 * Check whether or not this company is to be dissolved or not.
	 * 
	 * @param company
	 * @return true if and only if this company is to be dissolved
	 */
	boolean isCompanyToBeDissolved(Company company);

	/**
	 * Adjust the trading status (trading normally, insolvent, etc)
	 * 
	 * @param company
	 *            The company to adjust
	 */
	void updateCompanyTradingStatus(Company company);
}
