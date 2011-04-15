package net.fidoandfido.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;



@Entity
@Table(name = "TraderEvent")
public class TraderEvent {
	
	public static final String DIVIDEND_PAYMENT = "dividend";
	public static final String BUY_SHARES_PAYMENT = "buy";
	public static final String SELL_SHARES_PAYMENT = "sell"; 
	
	@Id
	@Column(name = "trader_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;
	
	@ManyToOne
	private Trader trader;
	
	@Column
	private String eventType;
	
	@Column
	private Date date;
	
	@ManyToOne
	private Company company;

	@Column
	private long shareCount;
	
	@Column
	private long amountTransferred;
	
	@Column
	private long startingCash;
	
	@Column
	private long endingCash;

	public TraderEvent() {
		// Default constructor - for ORM
	}
	
	public TraderEvent(Trader trader, String eventType, Date date,
			Company company, long shareCount, long amountTransferred,
			long startingCash, long endingCash) {
		super();
		this.trader = trader;
		this.eventType = eventType;
		this.date = date;
		this.company = company;
		this.shareCount = shareCount;
		this.amountTransferred = amountTransferred;
		this.startingCash = startingCash;
		this.endingCash = endingCash;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Trader getTrader() {
		return trader;
	}

	public void setTrader(Trader trader) {
		this.trader = trader;
	}

	public long getAmountTransferred() {
		return amountTransferred;
	}

	public void setAmountTransferred(long amountTransferred) {
		this.amountTransferred = amountTransferred;
	}

	public long getStartingCash() {
		return startingCash;
	}

	public void setStartingCash(long startingCash) {
		this.startingCash = startingCash;
	}

	public long getEndingCash() {
		return endingCash;
	}

	public void setEndingCash(long endingCash) {
		this.endingCash = endingCash;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public long getShareCount() {
		return shareCount;
	}

	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}
	
	
}
