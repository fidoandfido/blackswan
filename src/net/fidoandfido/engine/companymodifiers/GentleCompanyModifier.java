package net.fidoandfido.engine.companymodifiers;

import java.util.Random;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.WebPageUtil;

import org.apache.log4j.Logger;

public class GentleCompanyModifier implements CompanyModifier {

	Logger logger = Logger.getLogger(getClass());

	private Random random = new Random(17);

	private int percentChanceOfRateChange;
	private long maximumExpenseRate;
	private long minimumExpenseRate;
	private long maximumRevenueRate;
	private long minimumRevenueRate;
	private long minimumSpread;
	private long maximumSpread;

	public static int CHANCE_OF_BORROW_OR_REPAY = 4;
	public static long DEFAULT_LOAN = 50000000;
	public static long MAX_DEBT = 1000000000;

	public GentleCompanyModifier(int percentChanceOfRateChange, long minimumExpenseRate, long maximumExpenseRate, long minimumRevenueRate,
			long maximumRevenueRate, long minimumSpread, long maximumSpread) {
		super();
		this.percentChanceOfRateChange = percentChanceOfRateChange;
		this.maximumExpenseRate = maximumExpenseRate;
		this.minimumExpenseRate = minimumExpenseRate;
		this.maximumRevenueRate = maximumRevenueRate;
		this.minimumRevenueRate = minimumRevenueRate;
		this.minimumSpread = minimumSpread;
		this.maximumSpread = maximumSpread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.companymodifiers.CompanyModifier#modifyCompanyRates (net.fidoandfido.model.Company)
	 */
	@Override
	public boolean modifyCompanyRates(Company company) {
		boolean modified = false;

		long spread = company.getRevenueRate() - company.getExpenseRate();
		if (spread <= maximumSpread && spread >= minimumSpread) {
			// Safe to change our rates...

			// EXPENSES
			if (random.nextInt(100) < percentChanceOfRateChange) {
				modified = true;
				if (random.nextBoolean() == true) {
					// Go up (if we can)
					if (company.getExpenseRate() < maximumExpenseRate) {
						company.modifyExpenseRate(1);
					} else {
						company.modifyExpenseRate(-1);
					}
				} else {
					// Go down (if we can)
					if (company.getExpenseRate() > minimumExpenseRate) {
						company.modifyExpenseRate(-1);
					} else {
						company.modifyExpenseRate(1);
					}
				}
			}

			// REVENUES
			if (random.nextInt(100) < percentChanceOfRateChange) {
				modified = true;
				if (random.nextBoolean() == true) {
					// Go up (if we can)
					if (company.getRevenueRate() < maximumRevenueRate) {
						company.modifyRevenueRate(1);
					} else {
						company.modifyRevenueRate(-1);
					}
				} else {
					// Go down (if we can)
					if (company.getRevenueRate() > minimumRevenueRate) {
						company.modifyRevenueRate(-1);
					} else {
						company.modifyRevenueRate(1);
					}
				}
			}
		}
		// make sure we are still in the 'safe' zone
		// This may have changed for the company; in which case we will adjust the rates accordingly.
		spread = company.getRevenueRate() - company.getExpenseRate();

		if (spread > maximumSpread) {
			modified = true;
			// see if we can reduce the revenue, otherwise increase expenses.
			if (company.getRevenueRate() > minimumRevenueRate) {
				company.modifyRevenueRate(-1);
			} else {
				company.modifyExpenseRate(1);
			}
		} else if (spread < minimumSpread) {
			modified = true;
			// increase the revenue, otherwise decrease expenses.
			if (company.getRevenueRate() < maximumRevenueRate) {
				company.modifyRevenueRate(1);
			} else {
				company.modifyExpenseRate(-1);
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
			 logger.info("Setting company status for company: " + company.getName() +
			 " ---- New loan taken out to grow company. Loan value: "
			 + WebPageUtil.formatCurrency(DEFAULT_LOAN));
			company.setCompanyStatus("New loan taken out to grow company. Loan value: " + WebPageUtil.formatCurrency(DEFAULT_LOAN));
		} else if (val == 1) {
			if (company.getDebtValue() >= DEFAULT_LOAN && company.getAssetValue() > DEFAULT_LOAN) {
				modified = true;
				company.incrementAssetValue(DEFAULT_LOAN * -1);
				company.incrementDebtValue(DEFAULT_LOAN * -1);
				 logger.info("Setting company status for company: " + company.getName()
				 + " ---- Debt restructured, company no longer paying interest! Loan value: " +
				 WebPageUtil.formatCurrency(DEFAULT_LOAN));
				company.setCompanyStatus("Debt restructured, company no longer paying interest! Loan value: " + WebPageUtil.formatCurrency(DEFAULT_LOAN));
			}
		}
		return modified;
	}

	public static void main(String argv[]) {
		StockExchange exchange = new StockExchange("ASX", 400);
		Company company = new Company();
		company.setName("Foo co");
		company.setRevenueRate(20);
		company.setExpenseRate(12);
		company.setAssetValue(100000000);
		company.setDebtValue(50000000);

		long interestRate = company.getPrimeInterestRateBasisPoints() / 100;
		// set the constants for the company.
		// -- rate change is 20 % likely
		// -- mininum expense rate is 10
		// -- maximum expense rate is 30
		// -- minimum revenue rate is 10
		// -- maximum revenue rate is 35
		// -- minimum operating profit is interest rate - 5
		// -- maximum operating profit is interest rate + 2

		GentleCompanyModifier modifier = new GentleCompanyModifier(20, 10, 30, 10, 30, interestRate - 8, interestRate);

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
			modified = modified | modifier.modifyCompanyDebts(company);
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
