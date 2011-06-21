package net.fidoandfido.app;

import java.util.HashSet;
import java.util.Set;

import net.fidoandfido.initialiser.AppInitialiser;
import net.fidoandfido.model.Company;
import net.fidoandfido.util.WebPageUtil;

public class VerfiyAppinitialiser {

	public VerfiyAppinitialiser() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String argv[]) throws Exception {
		System.out.println("Starting.");
		VerfiyAppinitialiser verfiyAppinitialiser = new VerfiyAppinitialiser();
		verfiyAppinitialiser.verifyCompanyGenerator();
		System.out.println("Complete.");
	}

	public void verifyCompanyGenerator() throws Exception {
		System.out.println("Verifying company generation.");
		AppInitialiser appInitialiser = new AppInitialiser();
		appInitialiser.initCompanyGenerator();

		// System.out.print("Name");
		// System.out.print("\t");
		System.out.print("Code");
		System.out.print("\t");
		System.out.print("Asset");
		System.out.print("\t\t");
		System.out.print("Debt");
		System.out.print("\t\t");
		System.out.print("OE");
		System.out.print("\t\t");
		System.out.print("Shares");
		System.out.print("\t");
		System.out.print("Book V.");
		System.out.print("\t");
		System.out.print("Expns %");
		System.out.print("\t");
		System.out.print("Revnu %");
		System.out.print("\t");
		System.out.print("Return %");
		System.out.print("\t");
		System.out.print("Dvd?");
		System.out.print("\t");
		System.out.print("Dvd Rate");
		System.out.print("\t");
		System.out.println();

		for (int i = 0; i < 100; i++) {
			Company company = appInitialiser.getNewCompany();
			// System.out.print(company.getName());
			// System.out.print("\t");
			System.out.println(company.getName());
			System.out.print(company.getCode());
			System.out.print("\t");
			System.out.print(company.getSector());
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getAssetValue()));
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getDebtValue()));
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getCapitalisation()));
			System.out.print("\t");
			System.out.print(company.getOutstandingShares());
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getShareBookValue()));
			System.out.print("\t");
			System.out.print(company.getExpenseRate());
			System.out.print("\t");
			System.out.print(company.getRevenueRate());
			System.out.print("\t");
			System.out.print(company.getRevenueRate() - company.getExpenseRate());
			System.out.print("\t");
			System.out.print(!company.isNeverPayDividend());
			System.out.print("\t");
			System.out.print(company.getDividendRate());
			System.out.print("\t");
			System.out.println();
		}

		System.out.println("---------------------------------------------");
		System.out.println("MINING AND RESOURCES");
		System.out.println("---------------------------------------------");
		// Now just get resources and mining stocks.
		Set<String> sectors = new HashSet<String>();
		sectors.add("mining");
		sectors.add("resources");
		for (int i = 0; i < 20; i++) {
			appInitialiser.initialiseSectorList(sectors);
			Company company = appInitialiser.getNewCompany();
			// System.out.print(company.getName());
			// System.out.print("\t");
			System.out.println(company.getName());
			System.out.print(company.getCode());
			System.out.print("\t");
			System.out.print(company.getSector());
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getAssetValue()));
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getDebtValue()));
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getCapitalisation()));
			System.out.print("\t");
			System.out.print(company.getOutstandingShares());
			System.out.print("\t");
			System.out.print(WebPageUtil.formatCurrency(company.getShareBookValue()));
			System.out.print("\t");
			System.out.print(company.getExpenseRate());
			System.out.print("\t");
			System.out.print(company.getRevenueRate());
			System.out.print("\t");
			System.out.print(company.getRevenueRate() - company.getExpenseRate());
			System.out.print("\t");
			System.out.print(!company.isNeverPayDividend());
			System.out.print("\t");
			System.out.print(company.getDividendRate());
			System.out.print("\t");
			System.out.println();
		}

	}
}
