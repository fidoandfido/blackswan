package net.fidoandfido.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "StockExchangePeriod")
public class StockExchangePeriod {

	public static final String POOR_CONDITIONS = "Poor";
	public static final String ADVERSE_CONDITIONS = "Adverse";
	public static final String NEUTRAL_CONDITIONS = "Neutral";
	public static final String STRONG_CONDITIONS = "Strong";
	public static final String GOOD_CONDITIONS = "Good";

	@Id
	@Column(name = "stock_exchange_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@ManyToOne
	private StockExchange stockExchange;

	@Column
	private long interestRateBasisPointsDelta = 0;

	@Column
	private long revenueRateDelta = 0;

	@Column
	private long expenseRateDelta = 0;

	@Column
	private String economicConditions;

	@Column
	private Date startDate;

	@Column
	private Date minimumEndDate;

	@Column
	private Date closeDate;

	@Column
	private boolean open;

	@Column
	private long generation;

	public StockExchangePeriod() {
		// Default constructor for persistence layer
	}

	public StockExchangePeriod(StockExchange stockExchange, Date startDate, Date minimumEndDate, long generation, long interestRateBasisPoints,
			long revenueRateDelta, long expenseRateDelta, String economicConditions) {
		super();
		this.stockExchange = stockExchange;
		this.startDate = startDate;
		this.minimumEndDate = minimumEndDate;
		this.generation = generation;
		this.interestRateBasisPointsDelta = interestRateBasisPointsDelta;
		this.revenueRateDelta = revenueRateDelta;
		this.expenseRateDelta = expenseRateDelta;
		this.economicConditions = economicConditions;
		this.open = true;
	}

	public StockExchangePeriod(StockExchangePeriod currentPeriod, Date startDate, Date minimumEndDate) {
		this.stockExchange = currentPeriod.stockExchange;
		this.startDate = startDate;
		this.minimumEndDate = minimumEndDate;
		this.generation = currentPeriod.generation + 1;
		this.interestRateBasisPointsDelta = currentPeriod.interestRateBasisPointsDelta;
		this.revenueRateDelta = currentPeriod.revenueRateDelta;
		this.expenseRateDelta = currentPeriod.expenseRateDelta;
		this.economicConditions = currentPeriod.economicConditions;
		this.open = true;

	}

	public void close(Date dateClosed) {
		this.closeDate = dateClosed;
		open = false;
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
	 * @return the interestRateBasisPointsDelta
	 */
	public long getInterestRateBasisPointsDelta() {
		return interestRateBasisPointsDelta;
	}

	/**
	 * @param interestRateBasisPointsDelta
	 *            the interestRateBasisPointsDelta to set
	 */
	public void setInterestRateBasisPointsDelta(long interestRateBasisPointsDelta) {
		this.interestRateBasisPointsDelta = interestRateBasisPointsDelta;
	}

	/**
	 * @return the revenueRateDelta
	 */
	public long getRevenueRateDelta() {
		return revenueRateDelta;
	}

	/**
	 * @param revenueRateDelta
	 *            the revenueRateDelta to set
	 */
	public void setRevenueRateDelta(long revenueRateDelta) {
		this.revenueRateDelta = revenueRateDelta;
	}

	/**
	 * @return the expenseRateDelta
	 */
	public long getExpenseRateDelta() {
		return expenseRateDelta;
	}

	/**
	 * @param expenseRateDelta
	 *            the expenseRateDelta to set
	 */
	public void setExpenseRateDelta(long expenseRateDelta) {
		this.expenseRateDelta = expenseRateDelta;
	}

	/**
	 * @return the economicConditions
	 */
	public String getEconomicConditions() {
		return economicConditions;
	}

	/**
	 * @param economicConditions
	 *            the economicConditions to set
	 */
	public void setEconomicConditions(String economicConditions) {
		this.economicConditions = economicConditions;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the minimumEndDate
	 */
	public Date getMinimumEndDate() {
		return minimumEndDate;
	}

	/**
	 * @param minimumEndDate
	 *            the minimumEndDate to set
	 */
	public void setMinimumEndDate(Date minimumEndDate) {
		this.minimumEndDate = minimumEndDate;
	}

	/**
	 * @return the closeDate
	 */
	public Date getCloseDate() {
		return closeDate;
	}

	/**
	 * @param closeDate
	 *            the closeDate to set
	 */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param open
	 *            the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * @return the generation
	 */
	public long getGeneration() {
		return generation;
	}

	/**
	 * @param generation
	 *            the generation to set
	 */
	public void setGeneration(long generation) {
		this.generation = generation;
	}

}
