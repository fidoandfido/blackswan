package net.fidoandfido.engine.profitmodifers;

import java.util.Date;
import java.util.Random;

import net.fidoandfido.engine.quarter.QuarterData;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.Constants.QuarterPerformanceType;
import net.fidoandfido.util.WebPageUtil;

/**
 * Adjust the profit in a linear fashion. The modifier is expressed as a
 * percentage change and is set to the maximum possible change; the volatility
 * will then reduce them some amount. The amount that it may vary is defined by
 * the volatility.
 * 
 */
public class LinearProfitModifier implements EventProfitModifier {

	public static final String NAME = "linear";

	Random randomModifier = new Random();

	// @Override
	@Override
	public QuarterData adjustProfit(QuarterPerformanceType eventType, QuarterData eventData, Company company, CompanyPeriodReport companyPeriodReport, long eventCount) {
		// Work out how much the company should, ideally, be earning/paying for
		// this period part.
		long interestPaid = companyPeriodReport.getStartingExpectedInterest() / eventCount;
		long revenue = companyPeriodReport.getStartingExpectedRevenue() / eventCount;
		long expenses = companyPeriodReport.getStartingExpectedExpenses() / eventCount;

		// Now simply modify these based on the event type.
		switch (eventType) {
		case CATASTROPHIC:
			// Revenues WAY down, expenses WAY UP - variance: LARGE
			revenue = revenue * getPercentageInRange(50, 100) / 100;
			expenses = expenses * getPercentageInRange(120, 200) / 100;
			break;
		case TERRIBLE:
			// Revenues down, expenses up - variance 25%
			revenue = revenue * getPercentageInRange(65, 90) / 100;
			expenses = expenses * getPercentageInRange(105, 125) / 100;
			break;
		case POOR:
			// Revenues down, expenses even - variance 20%
			revenue = revenue * getPercentageInRange(80, 100) / 100;
			expenses = expenses * getPercentageInRange(95, 115) / 100;
			break;
		case AVERAGE:
			// revenues even, expenses even - variance 10%
			revenue = revenue * getPercentageInRange(95, 105) / 100;
			expenses = expenses * getPercentageInRange(95, 105) / 100;
			break;
		case GOOD:
			// Revenues up, expenses even - variance 20%
			revenue = revenue * getPercentageInRange(100, 120) / 100;
			expenses = expenses * getPercentageInRange(80, 120) / 100;
			break;
		case GREAT:
			// Revenues up, expenses down - variance 25%
			revenue = revenue * getPercentageInRange(105, 130) / 100;
			expenses = expenses * getPercentageInRange(75, 100) / 100;
			break;
		case EXTRAORDINARY:
			// Revenues WAY UP, Expenses SLASHED - variance LARGE
			revenue = revenue * getPercentageInRange(120, 200) / 100;
			expenses = expenses * getPercentageInRange(50, 100) / 100;
			break;
		}

		long profit = revenue - expenses - interestPaid;
		return new QuarterData(profit, expenses, revenue, interestPaid, eventData);
	}

	/**
	 * Return a value within the range provided
	 * 
	 * @param low
	 *            value of low end of range
	 * @param high
	 *            value of high end of range
	 * @return Random value between low and high value
	 */
	private int getPercentageInRange(int low, int high) {
		int delta = randomModifier.nextInt(high - low);
		return low + delta;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public static void main(String argv[]) {
		System.out.println("MODIFY - Linear Style!");

		StockExchange exchange = new StockExchange("ASX", 500);
		long assets = 100000000;
		long debts = 50000000;
		long shareCount = 100000;
		long revenueRate = 20;
		long expenseRate = 12;
		long dividendRate = 20;
		Company company = new Company("Foobar Enterprises", "fbe", assets, debts, shareCount, "sector", "", dividendRate, revenueRate, expenseRate);
		company.setStockExchange(exchange);

		long primeInterestRateBasisPoints = company.getPrimeInterestRateBasisPoints();
		// So now we have the initial profit, extrapolate back to work out the
		// interest, expenses and revenue
		CompanyPeriodReport companyPeriodReport = new CompanyPeriodReport(company, new Date(), 1000, 0);
		long expenses = company.getExpenseRate() * company.getAssetValue() / 100 / 4;
		long revenues = company.getRevenueRate() * company.getAssetValue() / 100 / 4;
		long interest = company.getDebtValue() * primeInterestRateBasisPoints / 10000 / 4;
		long profit = revenues - expenses - interest;
		companyPeriodReport.setStartingExpectedExpenses(expenses);
		companyPeriodReport.setStartingExpectedInterest(interest);
		companyPeriodReport.setStartingExpectedRevenue(revenues);
		QuarterData initialData = new QuarterData(profit, expenses, revenues, interest);
		LinearProfitModifier modifier = new LinearProfitModifier();

		QuarterData fullYearData = new QuarterData(profit * 4, expenses * 4, revenues * 4, interest * 4);

		System.out.println("Company data:");
		System.out.println("Assets: " + WebPageUtil.formatCurrency(company.getAssetValue()));
		System.out.println("Debt: " + WebPageUtil.formatCurrency(company.getDebtValue()));
		System.out.println("Equity: " + WebPageUtil.formatCurrency(company.getCapitalisation()));
		System.out.println("Share count: " + company.getOutstandingShares());
		System.out.println("Share value (of equity):" + (WebPageUtil.formatCurrency(company.getCapitalisation() / company.getOutstandingShares())));

		System.out.println("Default revenue rate on assets: " + company.getRevenueRate());
		System.out.println("Default expense rate on assets: " + company.getExpenseRate());
		System.out.println("Stock exchange interest rate: " + exchange.getPrimeInterestRateBasisPoints());

		System.out.println("DEFAULT EARNINGS - PER QUARTER THEN PER YEAR");
		System.out.println("Profit\t\trevenue\t\texpenses\tinterest\tEarning/share\tDividend");
		printData(company, initialData);
		printData(company, fullYearData);

		// Show the rest of the data, using moderate volatility
		for (QuarterPerformanceType eventType : QuarterPerformanceType.values()) {
			System.out.println(eventType);
			System.out.println("Profit\t\trevenue\t\texpenses\tinterest\tEarning/share\tDividend");
			for (int i = 0; i < 10; i++) {
				QuarterData eventData = initialData;
				eventData = modifier.adjustProfit(eventType, eventData, company, companyPeriodReport, 4);
				printData(company, eventData);
			}
		}

	}

	private static void printData(Company company, QuarterData initialData) {
		System.out.print(WebPageUtil.formatCurrency(initialData.getProfit()));
		System.out.print("\t");
		System.out.print(WebPageUtil.formatCurrency(initialData.getRevenue()));
		System.out.print("\t");
		System.out.print(WebPageUtil.formatCurrency(initialData.getExpenses()));
		System.out.print("\t");
		System.out.print(WebPageUtil.formatCurrency(initialData.getInterestPaid()));
		System.out.print("\t");
		System.out.print(WebPageUtil.formatCurrency(initialData.getProfit() / company.getOutstandingShares()));
		System.out.print("\t\t");
		System.out.print(WebPageUtil.formatCurrency((initialData.getProfit() / company.getOutstandingShares()) * company.getDividendRate() / 100));
		System.out.println();
	}
}
