package net.fidoandfido.engine.companymodifiers;

import net.fidoandfido.model.Company;

public class DefaultCompanyModifier implements CompanyModifier {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fidoandfido.engine.companymodifiers.CompanyModifier#modifyCompanyRates
	 * (net.fidoandfido.model.Company)
	 */
	@Override
	public boolean modifyCompanyRates(Company company) {
		// Dont actually modify anything!!!
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fidoandfido.engine.companymodifiers.CompanyModifier#modifyCompanyDebts
	 * (net.fidoandfido.model.Company)
	 */
	@Override
	public boolean modifyCompanyDebts(Company company) {
		// Dont actually modify anything!!!
		return false;
	}

	@Override
	public void updateCompanyTradingStatus(Company company) {
		// Dont do anything!
	}

}
