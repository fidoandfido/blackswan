package net.fidoandfido.engine.companymodifiers;

import java.util.Date;
import java.util.Random;

import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;
import net.fidoandfido.util.WebPageUtil;

import org.apache.log4j.Logger;

public class GentleCompanyModifier implements CompanyModifier {

	Logger logger = Logger.getLogger(getClass());

	public static final String NAME = "GENTLE_COMPANY_MODIFIER";
	private static final String COMPANY_DISSOLVED = "Company dissolved.";
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

	// A company will always remain solvent if their capitisation is greater
	// than this.
	public static long CAPITALISATION_TO_ALWAYS_STAY_SOLVENT = 100000000;

	// Capital must be at least this % of debt for company to stay solvent
	public static long MINIMUM_RATE_OF_CAPITAL_TO_DEBT = 10;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fidoandfido.engine.companymodifiers.CompanyModifier#modifyCompanyRates
	 * (net.fidoandfido.model.Company)
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
	 * @see
	 * net.fidoandfido.engine.companymodifiers.CompanyModifier#modifyCompanyDebts
	 * (net.fidoandfido.model.Company)
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

	@Override
	public void updateCompanyTradingStatus(Company company) {
		// Check if the company is insolvent, and if it is, make it so!
		boolean toBeDisolved = false;
		if (company.isInsolvent()) {
			if (company.getCurrentPeriod().getFinalProfit() < 0) {
				toBeDisolved = true;
			}
		}
		if (company.getAssetValue() < 0) {
			toBeDisolved = true;
		}

		if (toBeDisolved) {
			logger.info("Setting company status of Company: " + company.getName() + " = ---" + COMPANY_DISSOLVED);
			company.setCompanyStatus(Company.DISSOLVED_COMPANY_STATUS);
			long shareBookValue = company.getShareBookValue();
			if (shareBookValue < 0) {
				shareBookValue = 0;
			}

			company.setAssetValue(0);
			company.setDebtValue(0);
			company.setAlwaysPayDividend(false);
			company.setExpenseRate(0);
			company.setRevenueRate(0);
			company.setOutstandingShares(0);
			company.setTrading(false);
			Date date = new Date();

			ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
			TraderEventDAO traderEventDAO = new TraderEventDAO();

			Iterable<ShareParcel> shareParcels = shareParcelDAO.getHoldingsByCompany(company);
			for (ShareParcel shareParcel : shareParcels) {
				Trader trader = shareParcel.getTrader();
				long shareCount = shareParcel.getShareCount();
				long amount = shareBookValue * shareParcel.getShareCount();
				TraderEvent event = new TraderEvent(trader, COMPANY_DISSOLVED, date, company, shareCount, amount, trader.getCash(), trader.getCash() + amount);
				traderEventDAO.saveTraderEvent(event);
				trader.giveCash(amount);

				shareParcelDAO.deleteShareParcel(shareParcel);

			}
			return;
		}

		// Check if we meet the requirements for being insolvent.
		// Large debt, small capitalisation.
		boolean insolvent = false;
		long assets = company.getAssetValue();
		long debts = company.getDebtValue();
		long capitalisation = assets - debts;

		if (capitalisation < 0) {
			// Okay, we effectively have negative capitalisation (assets are
			// less than debts!)
			insolvent = true;
		} else if (capitalisation > CAPITALISATION_TO_ALWAYS_STAY_SOLVENT) {
			// Who cares about debt, there is plenty of assets.
			insolvent = false;
		} else {
			if (capitalisation < (debts * MINIMUM_RATE_OF_CAPITAL_TO_DEBT / 100)) {
				// Our debts have kicked into the warning threshold
				// check if the company is actually profitable.
				if (company.getCurrentPeriod().getFinalProfit() < 0 && company.getPreviousProfit() < 0) {
					// Two consecutive loss years - not so profitable...
					insolvent = true;
				}
			}
		}
		if (insolvent) {

			logger.info("Setting company status of Company: " + company.getName() + " = ---" + Company.INSOLVENT_COMPANY_STATUS);
			company.setCompanyStatus(Company.INSOLVENT_COMPANY_STATUS);
			company.setInsolvent(true);
		} else if (company.isInsolvent()) {
			logger.info("Setting company status of Company: " + company.getName() + " = ---" + Company.NO_MORE_INSOLVENT_COMPANY_STATUS);
			company.setCompanyStatus(Company.NO_MORE_INSOLVENT_COMPANY_STATUS);
			company.setInsolvent(false);
		} else {
			company.setCompanyStatus(Company.TRADING_COMPANY_STATUS);
		}
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
