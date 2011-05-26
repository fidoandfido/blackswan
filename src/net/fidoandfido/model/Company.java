package net.fidoandfido.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author Andy
 * 
 */
@Entity
@Table(name = "Company")
public class Company {

	// Default status strings - other may be used.
	public static final String TRADING_COMPANY_STATUS = "Trading as normal.";
	public static final String INSOLVENT_COMPANY_STATUS = "The company has been declared insolvent!";
	public static final String NO_MORE_INSOLVENT_COMPANY_STATUS = "The company is no longer insolvent! It is now trading as normal.";
	public static final String DISSOLVED_COMPANY_STATUS = "The comapny has been dissolved; it is no longer able to be traded.";
	public static final String UNLISTED_COMPANY_STATUS = "The company has not been formally listed.";

	@Id
	@Column(name = "company_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	String id;

	// Company identifiers
	/**
	 * Company name
	 */
	@Column
	private String name;

	/**
	 * Unique company code
	 */
	@Column
	private String code;

	/**
	 * Company sector
	 */
	@Column
	private String sector;

	/**
	 * Profit modifier class name (currently always LinearModifier)
	 */
	@Column
	private String profitModifierName;

	// Company balance sheet and share count
	/**
	 * Asset value
	 */
	@Column
	private long assetValue;

	/**
	 * Debt value
	 */
	@Column
	private long debtValue;

	/**
	 * Outstanding shares
	 */
	@Column
	private long outstandingShares;

	/**
	 * Stock exchange this company belongs to
	 */
	@ManyToOne
	@JoinColumn(name = "stock_exchange_id")
	private StockExchange stockExchange;

	/**
	 * Current period
	 */
	@OneToOne
	@JoinColumn(name = "current_period")
	private CompanyPeriodReport currentPeriod;

	/**
	 * Previous period report.
	 */
	@OneToOne
	@JoinColumn(name = "previous_period")
	private CompanyPeriodReport previousPeriodReport;

	/**
	 * Whether this company should keep borrowing or not
	 */
	@Column
	private boolean keepBorrowing = false;

	/**
	 * Indicate whether the company should always pay a dividend; no matter how
	 * sick financially!
	 */
	@Column
	private boolean alwaysPayDividend = false;

	/**
	 * Minimum dividend rate (only applicable if we always pay dividend)
	 */
	@Column
	private long minimumDividend = 0;

	/**
	 * Some companies just dont pay dividends!
	 */
	@Column
	private boolean neverPayDividend = false;

	/**
	 * Amount of profit that should be distributed (rest goes to capital growth)
	 */
	@Column
	private long dividendRate;

	/**
	 * Default rate the company earns revenue (as a percentage of asset value)
	 */
	@Column
	private long revenueRate;

	/**
	 * Default rate the company has expenses (as a percentage of asset value)
	 */
	@Column
	private long expenseRate;

	// STATS
	/**
	 * Last share trade price
	 */
	@Column
	private long lastTradePrice = 0;

	/**
	 * Change in last share price
	 */
	@Column
	private long lastTradeChange = 0;

	/**
	 * Last dividend paid
	 */
	@Column
	private long previousDividend = 0;

	// @Column
	// private long previousProfit = 0;

	/**
	 * Indicate how many good / bad quarters we have in a row prior to the start
	 * of this period.
	 */
	@Column
	private long quartersSinceGoodQuarter = 0;

	@Column
	private long quartersSinceBadQuarter = 0;

	/**
	 * Indicate how many periods (if any) this company has left of a golden age.
	 * 
	 * Small chance each period a company may enter a golden age; this is set by
	 * the period generator.
	 * 
	 * Period event generators *may* use this to override the default event
	 * types for the company. (They also may ignore it)
	 * 
	 */
	@Column
	private long remainingPeriodsOfGoldenAge = 0;

	@Column
	private long remainingPeriodsOfDarkAge = 0;

	@Column
	private String companyStatus;

	@Column
	private boolean isInsolvent;

	@Column
	private boolean isTrading;

	/**
	 * @return the isInsolvent
	 */
	public boolean isInsolvent() {
		return isInsolvent;
	}

	/**
	 * @param isInsolvent
	 *            the isInsolvent to set
	 */
	public void setInsolvent(boolean isInsolvent) {
		this.isInsolvent = isInsolvent;
	}

	/**
	 * @return the isTrading
	 */
	public boolean isTrading() {
		return isTrading;
	}

	/**
	 * @param isTrading
	 *            the isTrading to set
	 */
	public void setTrading(boolean isTrading) {
		this.isTrading = isTrading;
	}

	public Company() {
		// Default constructor for persistence
	}

	public Company(String name, String code, long assetValue, long debtValue, long outstandingShares, String sector, String profitModifierName,
			long dividendRate, long defaultRevenueRate, long defaultExpenseRate) {
		this.name = name;
		this.code = code;
		this.assetValue = assetValue;
		this.debtValue = debtValue;
		this.outstandingShares = outstandingShares;
		this.sector = sector;
		this.profitModifierName = profitModifierName;
		this.dividendRate = dividendRate;
		this.revenueRate = defaultRevenueRate;
		this.expenseRate = defaultExpenseRate;
		companyStatus = TRADING_COMPANY_STATUS;
		isTrading = true;
		isInsolvent = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(" ");
		builder.append(code);
		builder.append(" ");
		builder.append(sector);
		return builder.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public long getPrimeInterestRateBasisPoints() {
		if (stockExchange != null) {
			return getStockExchange().getPrimeInterestRateBasisPoints();
		}
		return 0;
	}

	public long getExpectedEarningsPerShare() {
		if (currentPeriod == null) {
			return 0;
		}
		long profit = currentPeriod.getStartingExpectedProfit();
		if (outstandingShares == 0) {
			return 0; 
		}
		return (profit / outstandingShares);
	}

	public long getPreviousEarningPerShare() {
		if (outstandingShares != 0) {
			return getPreviousProfit() / outstandingShares;
		}
		return 0;
	}

	public long getShareBookValue() {
		if (outstandingShares != 0) {
			return ((assetValue - debtValue) / outstandingShares);
		}
		return 0;
	}

	public long getPreviousProfit() {
		long profit = 0;
		if (previousPeriodReport != null) {
			profit = previousPeriodReport.getFinalProfit();
		}
		return profit;
	}

	// /////////////////////////////////////
	// / FIELD GETTERS AND SETTERS FOLLOW

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the assetValue
	 */
	public long getAssetValue() {
		return assetValue;
	}

	/**
	 * @param assetValue
	 *            the assetValue to set
	 */
	public void setAssetValue(long assetValue) {
		this.assetValue = assetValue;
	}

	/**
	 * Increment the assest value by the supplied delta
	 * 
	 * @param delta
	 */
	public void incrementAssetValue(long delta) {
		this.assetValue = this.assetValue + delta;
	}

	/**
	 * @return the debtValue
	 */
	public long getDebtValue() {
		return debtValue;
	}

	/**
	 * @param debtValue
	 *            the debtValue to set
	 */
	public void setDebtValue(long debtValue) {
		this.debtValue = debtValue;
	}

	public void incrementDebtValue(long modifier) {
		debtValue += modifier;
	}

	/**
	 * @return the outstandingShares
	 */
	public long getOutstandingShares() {
		return outstandingShares;
	}

	/**
	 * @param outstandingShares
	 *            the outstandingShares to set
	 */
	public void setOutstandingShares(long outstandingShares) {
		this.outstandingShares = outstandingShares;
	}

	/**
	 * @return the sector
	 */
	public String getSector() {
		return sector;
	}

	/**
	 * @param sector
	 *            the sector to set
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}

	/**
	 * @return the capitalisation
	 */
	public long getCapitalisation() {
		return assetValue - debtValue;
	}

	/**
	 * @return the previousDividend
	 */
	public long getPreviousDividend() {
		return previousDividend;
	}

	/**
	 * @param previousDividend
	 *            the previousDividend to set
	 */
	public void setPreviousDividend(long previousDividend) {
		this.previousDividend = previousDividend;
	}

	/**
	 * @return the profitModifierName
	 */
	public String getProfitModifierName() {
		return profitModifierName;
	}

	/**
	 * @param profitModifierName
	 *            the profitModifierName to set
	 */
	public void setProfitModifierName(String profitModifierName) {
		this.profitModifierName = profitModifierName;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the stockExchange
	 */
	public StockExchange getStockExchange() {
		return stockExchange;
	}

	/**
	 * @param stockExchange
	 *            the stockExchange to set
	 */
	public void setStockExchange(StockExchange stockExchange) {
		this.stockExchange = stockExchange;
	}

	/**
	 * @return the currentPeriod
	 */
	public CompanyPeriodReport getCurrentPeriod() {
		return currentPeriod;
	}

	/**
	 * @param currentPeriod
	 *            the currentPeriod to set
	 */
	public void setCurrentPeriod(CompanyPeriodReport currentPeriod) {
		this.currentPeriod = currentPeriod;
	}

	/**
	 * @return the previousPeriod
	 */
	public CompanyPeriodReport getPreviousPeriodReport() {
		return previousPeriodReport;
	}

	/**
	 * @param previousPeriodReport
	 *            the previousPeriod to set
	 */
	public void setPreviousPeriodReport(CompanyPeriodReport previousPeriodReport) {
		this.previousPeriodReport = previousPeriodReport;
	}

	/**
	 * @return the alwaysPayDivident
	 */
	public boolean isAlwaysPayDividend() {
		return alwaysPayDividend;
	}

	/**
	 * @param alwaysPayDivident
	 *            the alwaysPayDivident to set
	 */
	public void setAlwaysPayDividend(boolean alwaysPayDivident) {
		this.alwaysPayDividend = alwaysPayDivident;
	}

	/**
	 * @return the neverPayDivident
	 */
	public boolean isNeverPayDividend() {
		return neverPayDividend;
	}

	/**
	 * @param neverPayDivident
	 *            the neverPayDivident to set
	 */
	public void setNeverPayDividend(boolean neverPayDivident) {
		this.neverPayDividend = neverPayDivident;
	}

	/**
	 * @return the lastTradePrice
	 */
	public long getLastTradePrice() {
		return lastTradePrice;
	}

	/**
	 * @param lastTradePrice
	 *            the lastTradePrice to set
	 */
	public void setLastTradePrice(long lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
	}

	/**
	 * @return the lastTradeChange
	 */
	public long getLastTradeChange() {
		return lastTradeChange;
	}

	/**
	 * @param lastTradeChange
	 *            the lastTradeChange to set
	 */
	public void setLastTradeChange(long lastTradeChange) {
		this.lastTradeChange = lastTradeChange;
	}

	/**
	 * This is how much of the profits (if they are positive) will be paid out
	 * as dividends
	 * 
	 * @return the dividendRate
	 */
	public long getDividendRate() {
		return dividendRate;
	}

	/**
	 * @param dividendRate
	 *            the dividendRate to set
	 */
	public void setDividendRate(long dividendRate) {
		this.dividendRate = dividendRate;
	}

	/**
	 * @return the keepBorrowing
	 */
	public boolean isKeepBorrowing() {
		return keepBorrowing;
	}

	/**
	 * @param keepBorrowing
	 *            the keepBorrowing to set
	 */
	public void setKeepBorrowing(boolean keepBorrowing) {
		this.keepBorrowing = keepBorrowing;
	}

	/**
	 * @return the defaultRevenueRate
	 */
	public long getRevenueRate() {
		return revenueRate;
	}

	/**
	 * @param defaultRevenueRate
	 *            the defaultRevenueRate to set
	 */
	public void setRevenueRate(long defaultRevenueRate) {
		this.revenueRate = defaultRevenueRate;
	}

	public void modifyRevenueRate(long modifier) {
		this.revenueRate += modifier;
	}

	/**
	 * @return the defaultExpenseRate
	 */
	public long getExpenseRate() {
		return expenseRate;
	}

	/**
	 * @param defaultExpenseRate
	 *            the defaultExpenseRate to set
	 */
	public void setExpenseRate(long defaultExpenseRate) {
		this.expenseRate = defaultExpenseRate;
	}

	public void modifyExpenseRate(long modifier) {
		this.expenseRate += modifier;
	}

	/**
	 * @return the minimumDividendRate
	 */
	public long getMinimumDividend() {
		return minimumDividend;
	}

	/**
	 * @param minimumDividendRate
	 *            the minimumDividendRate to set
	 */
	public void setMinimumDividend(long minimumDividendRate) {
		this.minimumDividend = minimumDividendRate;
	}

	/**
	 * @return the remainingPeriodsOfGoldenAge
	 */
	public long getRemainingPeriodsOfGoldenAge() {
		return remainingPeriodsOfGoldenAge;
	}

	public void decrementRemainingPeriodsOfGoldenAge() {
		this.remainingPeriodsOfGoldenAge--;
	}

	/**
	 * @param remainingPeriodsOfGoldenAge
	 *            the remainingPeriodsOfGoldenAge to set
	 */
	public void setRemainingPeriodsOfGoldenAge(long remainingPeriodsOfGoldenAge) {
		this.remainingPeriodsOfGoldenAge = remainingPeriodsOfGoldenAge;
	}

	/**
	 * @return the remainingPeriodsOfBattlerAge
	 */
	public long getRemainingPeriodsOfDarkAge() {
		return remainingPeriodsOfDarkAge;
	}

	public void decrementRemainingPeriodsOfDarkAge() {
		this.remainingPeriodsOfDarkAge--;
	}

	/**
	 * @param remainingPeriodsOfBattlerAge
	 *            the remainingPeriodsOfBattlerAge to set
	 */
	public void setRemainingPeriodsOfDarkAge(long remainingPeriodsOfDarkAge) {
		this.remainingPeriodsOfDarkAge = remainingPeriodsOfDarkAge;
	}

	/**
	 * @return the companyStatus
	 */
	public String getCompanyStatus() {
		return companyStatus;
	}

	/**
	 * @param companyStatus
	 *            the companyStatus to set
	 */
	public void setCompanyStatus(String companyStatus) {
		this.companyStatus = companyStatus;
	}

	/**
	 * @return the quartersSinceGoodQuarter
	 */
	public long getQuartersSinceGoodQuarter() {
		return quartersSinceGoodQuarter;
	}

	/**
	 * @param quartersSinceGoodQuarter
	 *            the quartersSinceGoodQuarter to set
	 */
	public void setQuartersSinceGoodQuarter(long quartersSinceGoodQuarter) {
		this.quartersSinceGoodQuarter = quartersSinceGoodQuarter;
	}

	public void incrementQuartersSinceGoodQuarter() {
		quartersSinceGoodQuarter++;
	}

	/**
	 * @return the quartersSinceBadQuarter
	 */
	public long getQuartersSinceBadQuarter() {
		return quartersSinceBadQuarter;
	}

	/**
	 * @param quartersSinceBadQuarter
	 *            the quartersSinceBadQuarter to set
	 */
	public void setQuartersSinceBadQuarter(long quartersSinceBadQuarter) {
		this.quartersSinceBadQuarter = quartersSinceBadQuarter;
	}

	public void incrementQuartersSinceBadQuarter() {
		quartersSinceBadQuarter++;
	}

}
