package net.fidoandfido.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "TradeOrder")
public class Order {

	public enum OrderType {
		BUY, SELL;
	}

	@Id
	@Column(name = "order_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@ManyToOne
	@JoinColumn(name = "trader_id")
	private Trader trader;

	@ManyToOne
	@JoinColumn(name = "company")
	private Company company;

	@Column
	private long originalShareCount;

	@Column
	private long remainingShareCount;

	@Column
	private long offerPrice;

	@Column
	private boolean allowPartialOrder;

	@Column
	private Date dateCreated;

	@Column
	private boolean active;

	@Column
	private boolean executed;

	@Column
	private Date dateExecuted;

	@Column
	private OrderType orderType;

	public Order() {
		// Default constructor required for persistence
	}

	public Order(Trader trader, Company company, long shareCount, long offerPrice, OrderType orderType) {
		this.trader = trader;
		this.company = company;
		this.remainingShareCount = shareCount;
		this.offerPrice = offerPrice;
		this.orderType = orderType;
		active = true;
		dateCreated = new Date();
		allowPartialOrder = false;
		originalShareCount = shareCount;
		executed = false;
		dateExecuted = null;
	}

	public void executeOrder(Date executedDate) {
		this.setExecuted(true);
		this.setActive(false);
		this.setDateExecuted(executedDate);
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
	 * @return the trader
	 */
	public Trader getTrader() {
		return trader;
	}

	/**
	 * @param trader
	 *            the trader to set
	 */
	public void setTrader(Trader trader) {
		this.trader = trader;
	}

	/**
	 * @return the company
	 */
	public Company getCompany() {
		return company;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * @return the shareCount
	 */
	public long getRemainingShareCount() {
		return remainingShareCount;
	}

	/**
	 * @param shareCount
	 *            the shareCount to set
	 */
	public void setRemainingShareCount(long shareCount) {
		this.remainingShareCount = shareCount;
	}

	/**
	 * @return the offerPrice
	 */
	public long getOfferPrice() {
		return offerPrice;
	}

	/**
	 * @param offerPrice
	 *            the offerPrice to set
	 */
	public void setOfferPrice(long offerPrice) {
		this.offerPrice = offerPrice;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the executed
	 */
	public boolean isExecuted() {
		return executed;
	}

	/**
	 * @param executed
	 *            the executed to set
	 */
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	/**
	 * @return the dateExecuted
	 */
	public Date getDateExecuted() {
		return dateExecuted;
	}

	/**
	 * @param dateExecuted
	 *            the dateExecuted to set
	 */
	public void setDateExecuted(Date dateExecuted) {
		this.dateExecuted = dateExecuted;
	}

	/**
	 * @return the orderType
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType
	 *            the orderType to set
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	/**
	 * @return the allowPartialOrder
	 */
	public boolean isAllowPartialOrder() {
		return allowPartialOrder;
	}

	/**
	 * @param allowPartialOrder
	 *            the allowPartialOrder to set
	 */
	public void setAllowPartialOrder(boolean allowPartialOrder) {
		this.allowPartialOrder = allowPartialOrder;
	}

	/**
	 * @return the originalShareCount
	 */
	public long getOriginalShareCount() {
		return originalShareCount;
	}

	/**
	 * @param originalShareCount
	 *            the originalShareCount to set
	 */
	public void setOriginalShareCount(long originalShareCount) {
		this.originalShareCount = originalShareCount;
	}

}
