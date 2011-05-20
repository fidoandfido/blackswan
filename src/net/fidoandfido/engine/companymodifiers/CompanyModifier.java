package net.fidoandfido.engine.companymodifiers;

import net.fidoandfido.model.Company;

public interface CompanyModifier {

	public boolean modifyCompanyRates(Company company);

	public boolean modifyCompanyDebts(Company company);

	void updateCompanyTradingStatus(Company company);

}
