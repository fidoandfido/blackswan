package net.fidoandfido.engine;

import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.companymodifiers.DefaultCompanyModifier;
import net.fidoandfido.engine.companymodifiers.GentleCompanyModifier;
import net.fidoandfido.engine.profitmodifers.ConstantModifier;
import net.fidoandfido.engine.profitmodifers.EventProfitModifier;
import net.fidoandfido.engine.profitmodifers.LinearProfitModifier;
import net.fidoandfido.engine.quarter.AverageQuarterGenerator;
import net.fidoandfido.engine.quarter.DefaultQuarterGenerator;
import net.fidoandfido.engine.quarter.QuarterGenerator;
import net.fidoandfido.model.Company;

import org.apache.log4j.Logger;

public class CompanyProfileController {

	/*
	 * Idea for refactoring:
	 * 
	 * Rather than have switch statements for each possible profile, we could simply create the profile objects that
	 * have a name, company modifier, profit modifier and event generator; then put them in a map and get the
	 * appropriate object that we need.
	 * 
	 * The only problem there is that would not allow the modifiers to be instantiated with the company as a parameter,
	 * so they would not be able to inspect the company information. :(
	 * 
	 * This could be added as a parameter to the get method, but not every modifier needs the company etc.
	 * 
	 * Food for thought.
	 */

	Logger logger = Logger.getLogger(getClass());

	public static final String BLUE_CHIP = "Blue Chip";
	public static final String DINOSAUR = "Dinosaur";
	public static final String REBUILDING = "Rebuilding";

	public static final String[] INITIAL_COMPANY_TYPES = { BLUE_CHIP, DINOSAUR, REBUILDING };

	/**
	 * Return the company modifier that corresponds to the provided company's profile
	 * 
	 * @param company
	 * @return
	 */
	public CompanyModifier getCompanyModifer(Company company) {
		String profile = company.getCompanyProfile();
		if (profile.equals(BLUE_CHIP)) {
			long interestRate = company.getPrimeInterestRateBasisPoints() / 100;
			// set the constants for the company.
			// -- rate change is 20 % likely
			// -- mininum expense rate is 10
			// -- maximum expense rate is 25
			// -- minimum revenue rate is 10
			// -- maximum revenue rate is 35
			// -- minimum operating profit is interest rate - 1
			// -- maximum operating profit is interest rate + 5
			return new GentleCompanyModifier(20, 10, 25, 10, 35, interestRate, interestRate + 8);
		}
		if (profile.equals(DINOSAUR)) {
			long interestRate = company.getPrimeInterestRateBasisPoints() / 100;
			// set the constants for the company.
			// -- rate change is 20 % likely
			// -- mininum expense rate is 10
			// -- maximum expense rate is 30
			// -- minimum revenue rate is 10
			// -- maximum revenue rate is 35
			// -- minimum operating profit is interest rate - 5
			// -- maximum operating profit is interest rate + 2
			return new GentleCompanyModifier(20, 10, 30, 10, 30, interestRate - 8, interestRate);
		}
		if (profile.equals(REBUILDING)) {
			long interestRate = company.getPrimeInterestRateBasisPoints() / 100;
			// set the constants for the company.
			// -- rate change is 50 % likely
			// -- mininum expense rate is 10
			// -- maximum expense rate is 30
			// -- minimum revenue rate is 10
			// -- maximum revenue rate is 35
			// -- minimum operating profit is interest rate - 5
			// -- maximum operating profit is interest rate + 2
			return new GentleCompanyModifier(20, 10, 30, 10, 30, interestRate - 5, interestRate + 5);
		}

		// Unknown company profile!
		logger.error("Error getting company modifier. Profile: " + profile + " for company: " + company.getName() + " (ID:" + company.getId() + ")");
		return new DefaultCompanyModifier();

	}

	/**
	 * Return the QuarterGenerator that corresponds to the company profile of the provided company
	 * 
	 * @param company
	 * @return
	 */
	public QuarterGenerator getQuarterGenerator(Company company) {
		String profile = company.getCompanyProfile();
		if (profile.equals(BLUE_CHIP)) {
			return new AverageQuarterGenerator();
		}
		if (profile.equals(DINOSAUR)) {
			return new AverageQuarterGenerator();
		}
		if (profile.equals(REBUILDING)) {
			return new AverageQuarterGenerator();
		}

		logger.error("Error getting Qaurter generator. Profile: " + profile + " for company: " + company.getName() + " (ID:" + company.getId() + ")");
		return new DefaultQuarterGenerator();

	}

	/**
	 * Return the profit modifier that corresponds to the company profile of the provided company.
	 * 
	 * @param company
	 * @return
	 */
	public EventProfitModifier getProfitModifier(Company company) {
		String profile = company.getCompanyProfile();
		if (profile.equals(BLUE_CHIP)) {
			return new LinearProfitModifier();
		}
		if (profile.equals(DINOSAUR)) {
			return new LinearProfitModifier();
		}
		if (profile.equals(REBUILDING)) {
			return new LinearProfitModifier();
		}

		logger.error("Error getting Qaurter profit modifier. Profile: " + profile + " for company: " + company.getName() + " (ID:" + company.getId() + ")");
		return new ConstantModifier();

	}
}
