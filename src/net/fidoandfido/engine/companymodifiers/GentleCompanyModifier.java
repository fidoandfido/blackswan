package net.fidoandfido.engine.companymodifiers;

import java.util.Random;

import net.fidoandfido.model.Company;
import net.fidoandfido.util.WebPageUtil;

import org.apache.log4j.Logger;

public class GentleCompanyModifier implements CompanyModifier {

	Logger logger = Logger.getLogger(getClass());

	public static final String NAME = "GENTLE_COMPANY_MODIFIER";
	public static long MAX_REVENUE_RATE = 25;
	public static long MIN_REVENUE_RATE = 8;

	public static long MAX_EXPENSE_RATE = 15;
	public static long MIN_EXPENSE_RATE = 5;

	// public static long MINIMUM_SPREAD = 4;

	public static int CHANCE_OF_REVENUE_CHANGE = 5;
	public static int CHANCE_OF_EXPENSE_CHANGE = 5;

	private Random random = new Random(17);

	public static int CHANCE_OF_BORROW_OR_REPAY = 4;

	public static long DEFAULT_LOAN = 50000000;
	public static long MAX_DEBT = 1000000000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.companymodifiers.CompanyModifier#modifyCompanyRates (net.fidoandfido.model.Company)
	 */
	@Override
	public boolean modifyCompanyRates(Company company) {
		boolean modified = false;
		// EXPENSES
		if (random.nextInt(CHANCE_OF_EXPENSE_CHANGE) == 0) {
			modified = true;
			if (random.nextBoolean() == true) {
				// Go up (if we can)
				if (company.getExpenseRate() < MAX_EXPENSE_RATE) {
					company.modifyExpenseRate(1);
				} else {
					company.modifyExpenseRate(-1);
				}
			} else {
				// Go down (if we can)
				if (company.getExpenseRate() > MIN_EXPENSE_RATE) {
					company.modifyExpenseRate(-1);
				} else {
					company.modifyExpenseRate(1);
				}
			}
		}

		// REVENUES
		if (random.nextInt(CHANCE_OF_REVENUE_CHANGE) == 0) {
			modified = true;
			if (random.nextBoolean() == true) {
				// Go up (if we can)
				if (company.getRevenueRate() < MAX_REVENUE_RATE) {
					company.modifyRevenueRate(1);
				} else {
					company.modifyRevenueRate(-1);
				}
			} else {
				// Go down (if we can)
				if (company.getRevenueRate() > MIN_REVENUE_RATE) {
					company.modifyRevenueRate(-1);
				} else {
					company.modifyRevenueRate(1);
				}
			}
		}
		return modified;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.companymodifiers.CompanyModifier#modifyCompanyDebts (net.fidoandfido.model.Company)
	 */
	@Override
	public boolean modifyCompanyDebts(Company company) {
		boolean modified = false;
		int val = random.nextInt(CHANCE_OF_BORROW_OR_REPAY);
		if (val == 0 && company.getDebtValue() < MAX_DEBT) {
			modified = true;
			company.incrementDebtValue(DEFAULT_LOAN);
			company.incrementAssetValue(DEFAULT_LOAN);
			logger.info("Setting company status for company: " + company.getName() + " ---- New loan taken out to grow company. Loan value: "
					+ WebPageUtil.formatCurrency(DEFAULT_LOAN));
			company.setCompanyStatus("New loan taken out to grow company. Loan value: " + WebPageUtil.formatCurrency(DEFAULT_LOAN));
		} else if (val == 1) {
			if (company.getDebtValue() >= DEFAULT_LOAN && company.getAssetValue() > DEFAULT_LOAN) {
				modified = true;
				company.incrementAssetValue(DEFAULT_LOAN * -1);
				company.incrementDebtValue(DEFAULT_LOAN * -1);
				logger.info("Setting company status for company: " + company.getName()
						+ " ---- Debt restructured, company no longer paying interest! Loan value: " + WebPageUtil.formatCurrency(DEFAULT_LOAN));
				company.setCompanyStatus("Debt restructured, company no longer paying interest! Loan value: " + WebPageUtil.formatCurrency(DEFAULT_LOAN));
			}
		}
		return modified;
	}

	public static void main(String argv[]) {
		Company company = new Company();
		company.setRevenueRate(20);
		company.setExpenseRate(12);
		company.setAssetValue(100000000);
		company.setDebtValue(50000000);

		GentleCompanyModifier modifier = new GentleCompanyModifier();

		System.out.println("Company info");
		System.out.println("iter  revenues   expenses  assets   debt");
		System.out.print(0);
		System.out.print("\t");
		System.out.print(company.getRevenueRate());
		System.out.print("\t");
		System.out.print(company.getExpenseRate());
		System.out.print("\t");
		System.out.print(WebPageUtil.formatCurrency(company.getAssetValue()));
		System.out.print("\t");
		System.out.print(WebPageUtil.formatCurrency(company.getDebtValue()));
		System.out.print("\t");
		System.out.println();
		for (int i = 0; i < 100; i++) {
			boolean modified = modifier.modifyCompanyRates(company);
			modified = modifier.modifyCompanyDebts(company);
			if (modified) {
				System.out.print(i);
				System.out.print("\t");
				System.out.print(company.getRevenueRate());
				System.out.print("\t");
				System.out.print(company.getExpenseRate());
				System.out.print("\t");
				System.out.print(WebPageUtil.formatCurrency(company.getAssetValue()));
				System.out.print("\t");
				System.out.print(WebPageUtil.formatCurrency(company.getDebtValue()));
				System.out.print("\t");
				System.out.println();
			}

		}

	}

}
