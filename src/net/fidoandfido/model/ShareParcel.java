package net.fidoandfido.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author Andy
 * 
 */

@Entity
@Table(name = "ShareParcel")
public class ShareParcel {
	@Id
	@Column(name = "share_parcel_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@ManyToOne
	private Trader trader;

	@Column
	private long shareCount;

	@ManyToOne
	private Company company;

	public ShareParcel() {
		// Default constructor required for persistence
	}

	public ShareParcel(Trader trader, long shareCount, Company company) {
		super();
		this.trader = trader;
		this.shareCount = shareCount;
		this.company = company;
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
	 * @return the traderKey
	 */
	public Trader getTrader() {
		return trader;
	}

	/**
	 * @param traderKey
	 *            the traderKey to set
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
	public long getShareCount() {
		return shareCount;
	}

	/**
	 * @param shareCount
	 *            the shareCount to set
	 */
	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}

	public void removeShares(long sharesToRemove) {
		this.shareCount -= sharesToRemove;
	}

	public void addShares(long sharesToAdd) {
		this.shareCount += sharesToAdd;
	}

}
