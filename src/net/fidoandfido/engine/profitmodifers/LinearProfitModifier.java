package net.fidoandfido.engine.profitmodifers;

import java.util.Random;

import net.fidoandfido.engine.event.EventData;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.Constants.EventType;
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

	Volatility volatility = Volatility.MODERATE;

	Random randomModifier = new Random();

	// @Override
	@Override
	public EventData adjustProfit(EventType eventType, EventData eventData, Company company, long eventCount) {
		// To begin with, lets get what should, in the perfect world, be the
		// stats for this company.
		long interestPaid = company.getPrimeInterestRate() * company.getDebtValue() / 10000 / eventCount;
		long revenue = company.getAssetValue() * company.getDefaultRevenueRate() / 100 / eventCount;
		long expenses = company.getAssetValue() * company.getDefaultExpenseRate() / 100 / eventCount;

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
		return new EventData(profit, expenses, revenue, interestPaid, eventData);
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

	@Override
	public void setVolatility(Volatility volatility) {
		this.volatility = volatility;
	}

	public static void main(String argv[]) {
		System.out.println("MODIFY - Linear Style!");

		StockExchange exchange = new StockExchange("ASX", "asx", 1, "asdf", 0, 500);
		long assets = 100000000;
		long debts = 50000000;
		long shareCount = 100000;
		long revenueRate = 20;
		long expenseRate = 12;
		long dividendRate = 20;
		Company company = new Company("Foobar Enterprises", "fbe", assets, debts, shareCount, "sector", "", dividendRate, revenueRate, expenseRate);
		company.setStockExchange(exchange);

		long primeInterestRateBasisPoints = company.getPrimeInterestRate();
		// So now we have the initial profit, extrapolate back to work out the
		// interest, expenses and revenue
		long expenses = company.getDefaultExpenseRate() * company.getAssetValue() / 100 / 4;
		long revenues = company.getDefaultRevenueRate() * company.getAssetValue() / 100 / 4;
		long interest = company.getDebtValue() * primeInterestRateBasisPoints / 10000 / 4;
		long profit = revenues - expenses - interest;
		EventData initialData = new EventData(profit, expenses, revenues, interest);
		LinearProfitModifier modifier = new LinearProfitModifier();

		EventData fullYearData = new EventData(profit * 4, expenses * 4, revenues * 4, interest * 4);

		System.out.println("Company data:");
		System.out.println("Assets: " + WebPageUtil.formatCurrency(company.getAssetValue()));
		System.out.println("Debt: " + WebPageUtil.formatCurrency(company.getDebtValue()));
		System.out.println("Equity: " + WebPageUtil.formatCurrency(company.getCapitalisation()));
		System.out.println("Share count: " + company.getOutstandingShares());
		System.out.println("Share value (of equity):" + (WebPageUtil.formatCurrency(company.getCapitalisation() / company.getOutstandingShares())));

		System.out.println("Default revenue rate on assets: " + company.getDefaultRevenueRate());
		System.out.println("Default expense rate on assets: " + company.getDefaultExpenseRate());
		System.out.println("Stock exchange interest rate: " + exchange.getPrimeInterestRate());

		System.out.println("DEFAULT EARNINGS - PER QUARTER THEN PER YEAR");
		System.out.println("Profit\t\trevenue\t\texpenses\tinterest\tEarning/share\tDividend");
		printData(company, initialData);
		printData(company, fullYearData);

		// Show the rest of the data, using moderate volatility
		for (EventType eventType : EventType.values()) {
			System.out.println(eventType);
			System.out.println("Profit\t\trevenue\t\texpenses\tinterest\tEarning/share\tDividend");
			for (int i = 0; i < 10; i++) {
				EventData eventData = initialData;
				eventData = modifier.adjustProfit(eventType, eventData, company, 4);
				printData(company, eventData);
			}
		}

	}

	private static void printData(Company company, EventData initialData) {
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
