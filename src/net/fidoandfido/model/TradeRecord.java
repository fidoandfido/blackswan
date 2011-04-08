package net.fidoandfido.model;

import java.util.Date;

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
@Table(name = "TradeRecord")
public class TradeRecord {

	@Id
	@Column(name = "trade_record_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@ManyToOne
	private Trader seller;

	@ManyToOne
	private Trader buyer;

	@ManyToOne
	private Company company;

	@Column
	private long shareCount;

	@Column
	private long sharePrice;

	@Column
	private Date date;

	public TradeRecord() {
		// Default constructor required for persistence
	}

	public TradeRecord(Trader buyer, Trader seller, Company company, long shareCount, long sharePrice, Date date) {
		this.seller = seller;
		this.buyer = buyer;
		this.company = company;
		this.shareCount = shareCount;
		this.sharePrice = sharePrice;
		this.date = date;
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

	/**
	 * @return the sharePrice
	 */
	public long getSharePrice() {
		return sharePrice;
	}

	/**
	 * @param sharePrice
	 *            the sharePrice to set
	 */
	public void setSharePrice(long sharePrice) {
		this.sharePrice = sharePrice;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the seller
	 */
	public Trader getSeller() {
		return seller;
	}

	/**
	 * @param seller
	 *            the seller to set
	 */
	public void setSeller(Trader seller) {
		this.seller = seller;
	}

	/**
	 * @return the buyer
	 */
	public Trader getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer
	 *            the buyer to set
	 */
	public void setBuyer(Trader buyer) {
		this.buyer = buyer;
	}

}
