package net.fidoandfido.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "PeriodMessage")
public class PeriodMessage {

	@Id
	@Column(name = "company_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	String id;

	private StockExchange exchange;

	private StockExchangePeriod exchangePeriod;

	private String sector;

	private Company company;

	private CompanyPeriodReport companyPeriod;

	private String message;

	private Date date;

	/**
	 * Default constructor for persistence layer.
	 */
	public PeriodMessage() {
		// Default constructor for persistence layer
	}

	/**
	 * Exchange level message
	 * 
	 * @param exchange
	 * @param exchangePeriod
	 * @param message
	 * @param date
	 */
	public PeriodMessage(StockExchange exchange, StockExchangePeriod exchangePeriod, String message, Date date) {
		super();
		this.exchange = exchange;
		this.exchangePeriod = exchangePeriod;
		this.message = message;
		this.date = date;
	}

	/**
	 * Sector level message
	 * 
	 * @param exchange
	 * @param exchangePeriod
	 * @param sector
	 * @param message
	 * @param date
	 */
	public PeriodMessage(StockExchange exchange, StockExchangePeriod exchangePeriod, String sector, String message, Date date) {
		super();
		this.exchange = exchange;
		this.exchangePeriod = exchangePeriod;
		this.sector = sector;
		this.message = message;
		this.date = date;
	}

	/**
	 * Company level message
	 * 
	 * @param exchange
	 * @param exchangePeriod
	 * @param sector
	 * @param company
	 * @param companyPeriod
	 * @param message
	 * @param date
	 */
	public PeriodMessage(StockExchange exchange, StockExchangePeriod exchangePeriod, String sector, Company company, CompanyPeriodReport companyPeriod,
			String message, Date date) {
		super();
		this.exchange = exchange;
		this.exchangePeriod = exchangePeriod;
		this.sector = sector;
		this.company = company;
		this.companyPeriod = companyPeriod;
		this.message = message;
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
	 * @return the exchange
	 */
	public StockExchange getExchange() {
		return exchange;
	}

	/**
	 * @param exchange
	 *            the exchange to set
	 */
	public void setExchange(StockExchange exchange) {
		this.exchange = exchange;
	}

	/**
	 * @return the exchangePeriod
	 */
	public StockExchangePeriod getExchangePeriod() {
		return exchangePeriod;
	}

	/**
	 * @param exchangePeriod
	 *            the exchangePeriod to set
	 */
	public void setExchangePeriod(StockExchangePeriod exchangePeriod) {
		this.exchangePeriod = exchangePeriod;
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
	 * @return the companyPeriod
	 */
	public CompanyPeriodReport getCompanyPeriod() {
		return companyPeriod;
	}

	/**
	 * @param companyPeriod
	 *            the companyPeriod to set
	 */
	public void setCompanyPeriod(CompanyPeriodReport companyPeriod) {
		this.companyPeriod = companyPeriod;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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

}
