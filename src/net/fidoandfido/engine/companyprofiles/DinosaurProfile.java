package net.fidoandfido.engine.companyprofiles;

import java.util.Random;
import java.util.Set;

import net.fidoandfido.engine.companymodifiers.CompanyModifier;
import net.fidoandfido.engine.companymodifiers.GentleCompanyModifier;
import net.fidoandfido.engine.profitmodifers.EventProfitModifier;
import net.fidoandfido.engine.profitmodifers.GentleProfitModifier;
import net.fidoandfido.engine.quarter.AverageQuarterGenerator;
import net.fidoandfido.engine.quarter.QuarterGenerator;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.PeriodQuarter;

public class DinosaurProfile extends CompanyProfile {

	public final static String NAME = "Dinosaur";

	private static Random profileChangeRandom = new Random(17);
	private static Random profileSelectorRandom = new Random(17);

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void initialiseProfile(Company company) {
		company.setProfileIndex(5);
		company.setProfileStatus("Dinosaur");
	}

	@Override
	public void updateProfile(Company company) {
		int profileIndex = company.getProfileIndex();
		if (profileIndex > 0) {
			company.setProfileIndex(profileIndex - 1);
		} else {
			// Possible to change from this profile
			if (profileChangeRandom.nextInt(10) == 0) {
				// Time to change!
				CompanyProfile[] nextProfile = { new BlueChipProfile() };
				CompanyProfile newProfile = nextProfile[profileSelectorRandom.nextInt(nextProfile.length)];
				company.setCompanyProfileName(newProfile.getName());
				newProfile.initialiseProfile(company);
			}
		}
	}

	@Override
	public CompanyModifier getCompanyModifier(Company company) {
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

	@Override
	public QuarterGenerator getQuarterGenerator(Company company) {
		AverageQuarterGenerator averageQuarterGenerator = new AverageQuarterGenerator();
		CompanyPeriodReport previousPeriodReport = company.getPreviousPeriodReport();
		if (previousPeriodReport != null) {
			Set<PeriodQuarter> periodQuarterList = previousPeriodReport.getPeriodQuarterList();
			for (PeriodQuarter periodQuarter : periodQuarterList) {
				averageQuarterGenerator.addPreviousEvent(periodQuarter.getEventType());
			}
		}
		return averageQuarterGenerator;
	}

	@Override
	public EventProfitModifier getProfitModifier(Company company) {
		return new GentleProfitModifier();
	}

}
