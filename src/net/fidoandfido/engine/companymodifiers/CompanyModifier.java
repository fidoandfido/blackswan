package net.fidoandfido.engine.companymodifiers;

import net.fidoandfido.model.Company;

public interface CompanyModifier {

	/**
	 * Adjust the company rates (revenue and expenses) for this company, according to the company profile.
	 * 
	 * @param company
	 * @return true if and only if the rates have changed.
	 */
	public boolean modifyCompanyRates(Company company);

	/**
	 * Adjust the company debts according to the company profile. This entails borrowing or repaying debts.
	 * 
	 * @param company
	 *            to adjust
	 * @return true if and only if the rates have changed.
	 */
	public boolean modifyCompanyDebts(Company company);

}
