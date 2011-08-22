package net.fidoandfido.engine;

import java.util.HashMap;
import java.util.Map;

import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.companyprofiles.BlueChipProfile;
import net.fidoandfido.engine.companyprofiles.CompanyProfile;
import net.fidoandfido.engine.companyprofiles.ConsolidatorProfile;
import net.fidoandfido.engine.companyprofiles.DinosaurProfile;
import net.fidoandfido.engine.companyprofiles.DynamicProfile;
import net.fidoandfido.engine.profitmodifers.EventProfitModifier;
import net.fidoandfido.engine.quarter.QuarterGenerator;
import net.fidoandfido.model.Company;

import org.apache.log4j.Logger;

public class CompanyProfileController {

	Logger logger = Logger.getLogger(getClass());

	public static Map<String, CompanyProfile> profiles = new HashMap<String, CompanyProfile>();

	static {
		profiles.put(BlueChipProfile.NAME, new BlueChipProfile());
		profiles.put(DinosaurProfile.NAME, new DinosaurProfile());
		profiles.put(ConsolidatorProfile.NAME, new ConsolidatorProfile());
		profiles.put(DynamicProfile.NAME, new DynamicProfile());
	}

	public void modifyCompanyProfile(Company company) {
		// Get the current profile for the company, and then allow it to modify the company as it sees fit.
		CompanyProfile profile = profiles.get(company.getCompanyProfileName());
		profile.updateProfile(company);
	}

	/**
	 * Return the company modifier that corresponds to the provided company's profile
	 * 
	 * @param company
	 * @return
	 */
	public CompanyModifier getCompanyModifer(Company company) {
		CompanyProfile profile = profiles.get(company.getCompanyProfileName());
		return profile.getCompanyModifier(company);

		// if (profile.equals(REBUILDING)) {
		// long interestRate = company.getPrimeInterestRateBasisPoints() / 100;
		// // set the constants for the company.
		// // -- rate change is 50 % likely
		// // -- mininum expense rate is 10
		// // -- maximum expense rate is 30
		// // -- minimum revenue rate is 10
		// // -- maximum revenue rate is 35
		// // -- minimum operating profit is interest rate - 5
		// // -- maximum operating profit is interest rate + 2
		// return new GentleCompanyModifier(20, 10, 30, 10, 30, interestRate - 5, interestRate + 5);
		// }

	}

	/**
	 * Return the QuarterGenerator that corresponds to the company profile of the provided company
	 * 
	 * @param company
	 * @return
	 */
	public QuarterGenerator getQuarterGenerator(Company company) {
		CompanyProfile profile = profiles.get(company.getCompanyProfileName());
		return profile.getQuarterGenerator(company);
	}

	/**
	 * Return the profit modifier that corresponds to the company profile of the provided company.
	 * 
	 * @param company
	 * @return
	 */
	public EventProfitModifier getProfitModifier(Company company) {
		CompanyProfile profile = profiles.get(company.getCompanyProfileName());
		return profile.getProfitModifier(company);
	}

}
