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

	@Column
	private long purchasePrice = 0;

	public ShareParcel() {
		// Default constructor required for persistence
	}

	public ShareParcel(Trader trader, long shareCount, Company company, long purchasePrice) {
		super();
		this.trader = trader;
		this.shareCount = shareCount;
		this.company = company;
		this.purchasePrice = purchasePrice;
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
	 * @return the purchasePrice
	 */
	public long getPurchasePrice() {
		return purchasePrice;
	}

	/**
	 * @param purchasePrice
	 *            the purchasePrice to set
	 */
	public void setPurchasePrice(long purchasePrice) {
		this.purchasePrice = purchasePrice;
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

	public void addShares(long sharesToAdd, long price) {
		long originalValue = shareCount * purchasePrice;
		long newValue = (sharesToAdd * price) + originalValue;

		this.shareCount += sharesToAdd;
		// Update the purchased price.
		purchasePrice = newValue / this.shareCount;

	}

}
