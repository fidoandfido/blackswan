package net.fidoandfido.engine.companyprofiles;

import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.profitmodifers.EventProfitModifier;
import net.fidoandfido.engine.quarter.QuarterGenerator;
import net.fidoandfido.model.Company;

public abstract class CompanyProfile {

	/**
	 * Get the name of this profile
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Perform any required initialisation to a company that has been assigned this profile.
	 * 
	 * @param company
	 */
	public abstract void initialiseProfile(Company company);

	/**
	 * Update the profile of the company, including transitioning to a new 'profile' if required.
	 * 
	 * @param company
	 * @return
	 */
	public abstract void updateProfile(Company company);

	/**
	 * Return the company modifier for this company.
	 * 
	 * @param company
	 * @return
	 */
	public abstract CompanyModifier getCompanyModifier(Company company);

	/**
	 * Return a quarter generator for this company
	 * 
	 * @param company
	 * @return
	 */
	public abstract QuarterGenerator getQuarterGenerator(Company company);

	/**
	 * Return an event profit modifier according to the profile and the company
	 * 
	 * @param company
	 * @return
	 */
	public abstract EventProfitModifier getProfitModifier(Company company);

	protected void doSomething() {

	}

}
