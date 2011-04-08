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

	@Id
	@Column(name = "company_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	String id;

	// Company identifiers
	@Column
	private String name;

	@Column
	private String code;

	@Column
	private String sector;

	@Column
	private String profitModifierName;

	// Company balance sheet and share count
	@Column
	private long assetValue;

	@Column
	private long debtValue;

	@Column
	private long capitalisation;

	@Column
	private long outstandingShares;

	// Company profit/loss
	@Column
	private long profit;

	@ManyToOne
	@JoinColumn(name = "stock_exchange_id")
	private StockExchange stockExchange;

	@OneToOne
	@JoinColumn(name = "current_period")
	private CompanyPeriodReport currentPeriod;

	@Column
	private long previousDividend = 0;

	@Column
	private boolean alwaysPayDividend = false;

	@Column
	private boolean neverPayDividend = false;

	/**
	 * Amount of profit that should be distributed (rest goes to capital growth)
	 */
	@Column
	private long dividendRate = 0;

	@Column
	private long lastTradePrice = 0;

	public Company() {
		// Default constructor for persistence
	}

	public Company(String name, String code, long assetValue, long debtValue, long capitilisation, long outstandingShares, String sector,
			String profitModifierName) {
		this.name = name;
		this.code = code;
		this.assetValue = assetValue;
		this.debtValue = debtValue;
		this.capitalisation = capitilisation;
		this.outstandingShares = outstandingShares;
		this.sector = sector;
		this.profitModifierName = profitModifierName;
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

	/**
	 * @return the captilisation
	 */
	public long getCaptilisation() {
		return capitalisation;
	}

	/**
	 * @param captilisation
	 *            the captilisation to set
	 */
	public void setCaptilisation(long captilisation) {
		this.capitalisation = captilisation;
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
	 * @return the capitalisation
	 */
	public long getCapitalisation() {
		return capitalisation;
	}

	/**
	 * @param capitalisation
	 *            the capitalisation to set
	 */
	public void setCapitalisation(long capitalisation) {
		this.capitalisation = capitalisation;
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

}
