package net.fidoandfido.engine.economicmodfiers;

import net.fidoandfido.model.Company;
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

	@Override
	public void updateCompanyTradingStatus(Company company) {
		// Dont do anything!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.companymodifiers.CompanyModifier#isCompanyInsolvent(net.fidoandfido.model.Company)
	 */
	@Override
	public boolean isCompanyToBeDissolved(Company company) {
		// No insolvency here!
		if (company.getCapitalisation() < 0) {
			return true;
		}
		return false;
	}

}
