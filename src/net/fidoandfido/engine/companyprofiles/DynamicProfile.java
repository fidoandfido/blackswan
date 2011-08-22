package net.fidoandfido.engine.companyprofiles;

import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.companymodifiers.GentleCompanyModifier;
import net.fidoandfido.engine.profitmodifers.EventProfitModifier;
import net.fidoandfido.engine.profitmodifers.LinearProfitModifier;
import net.fidoandfido.engine.quarter.AverageQuarterGenerator;
import net.fidoandfido.engine.quarter.QuarterGenerator;
import net.fidoandfido.model.Company;

public class DynamicProfile extends CompanyProfile {

	public static final String NAME = "Dynamic Company";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void initialiseProfile(Company company) {
		// TODO Auto-generated method stub
		// Nothing to do here yet
	}

	@Override
	public void updateProfile(Company company) {
		// TODO Auto-generated method stub
		// Nothing to do here yet
	}

	@Override
	public CompanyModifier getCompanyModifier(Company company) {
		long interestRate = company.getPrimeInterestRateBasisPoints() / 100;
		// set the constants for the company.
		// -- rate change is 50 % likely
		// -- mininum expense rate is 10
		// -- maximum expense rate is 30
		// -- minimum revenue rate is 10
		// -- maximum revenue rate is 35
		// -- minimum operating profit is interest rate - 5
		// -- maximum operating profit is interest rate + 10
		return new GentleCompanyModifier(50, 10, 30, 10, 30, interestRate - 5, interestRate + 10);
	}

	@Override
	public QuarterGenerator getQuarterGenerator(Company company) {
		return new AverageQuarterGenerator();
	}

	@Override
	public EventProfitModifier getProfitModifier(Company company) {
		return new LinearProfitModifier();
	}

}
