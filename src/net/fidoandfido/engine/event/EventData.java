package net.fidoandfido.engine.event;

/**
 * @author andy
 * 
 */
public class EventData {

	private long runningProfit = 0;
	private long runningExpenses = 0;
	private long runningRevenue = 0;
	private long runningInterestPaid = 0;

	private long profit;
	private long expenses;
	private long revenue;
	private long interestPaid;

	public EventData(long profit, long expenses, long revenue, long interestPaid) {
		this.profit = profit;
		this.expenses = expenses;
		this.revenue = revenue;
		this.interestPaid = interestPaid;
	}

	public EventData(long profit, long expenses, long revenue, long interestPaid, EventData previousData) {
		this.profit = profit;
		this.expenses = expenses;
		this.revenue = revenue;
		this.interestPaid = interestPaid;
		// Add the running totals
		this.runningProfit = previousData.getRunningProfit() + profit;
		this.runningExpenses = previousData.getRunningExpenses() + expenses;
		this.runningRevenue = previousData.getRunningRevenue() + revenue;
		this.runningInterestPaid = previousData.getRunningInterestPaid() + interestPaid;

	}

	/**
	 * @return the profit
	 */
	public long getProfit() {
		return profit;
	}

	/**
	 * @param profit
	 *            the profit to set
	 */
	public void setProfit(long profit) {
		this.profit = profit;
	}

	/**
	 * @return the expenses
	 */
	public long getExpenses() {
		return expenses;
	}

	/**
	 * @param expenses
	 *            the expenses to set
	 */
	public void setExpenses(long expenses) {
		this.expenses = expenses;
	}

	/**
	 * @return the revenue
	 */
	public long getRevenue() {
		return revenue;
	}

	/**
	 * @param revenue
	 *            the revenue to set
	 */
	public void setRevenue(long revenue) {
		this.revenue = revenue;
	}

	/**
	 * @return the interest
	 */
	public long getInterestPaid() {
		return interestPaid;
	}

	/**
	 * @param interest
	 *            the interest to set
	 */
	public void setInterestPaid(long interest) {
		this.interestPaid = interest;
	}

	/**
	 * @return the runningProfit
	 */
	public long getRunningProfit() {
		return runningProfit;
	}

	/**
	 * @param runningProfit
	 *            the runningProfit to set
	 */
	public void setRunningProfit(long runningProfit) {
		this.runningProfit = runningProfit;
	}

	/**
	 * @return the runningExpenses
	 */
	public long getRunningExpenses() {
		return runningExpenses;
	}

	/**
	 * @param runningExpenses
	 *            the runningExpenses to set
	 */
	public void setRunningExpenses(long runningExpenses) {
		this.runningExpenses = runningExpenses;
	}

	/**
	 * @return the runningRevenue
	 */
	public long getRunningRevenue() {
		return runningRevenue;
	}

	/**
	 * @param runningRevenue
	 *            the runningRevenue to set
	 */
	public void setRunningRevenue(long runningRevenue) {
		this.runningRevenue = runningRevenue;
	}

	/**
	 * @return the runningInterestPaid
	 */
	public long getRunningInterestPaid() {
		return runningInterestPaid;
	}

	/**
	 * @param runningInterestPaid
	 *            the runningInterestPaid to set
	 */
	public void setRunningInterestPaid(long runningInterestPaid) {
		this.runningInterestPaid = runningInterestPaid;
	}

}
