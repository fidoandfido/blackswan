package net.fidoandfido.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.fidoandfido.util.Constants.EventType;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "PeriodPartRumour")
public class PeriodRumour {

	@Id
	@Column(name = "period_rumour_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "company_period_report")
	private CompanyPeriodReport companyPeriodReport;

	@Column
	private Date dateInformationAvailable;

	@Column
	private Date dateRumourExpires;

	@Column
	private int reputationRequired = 0;

	@Column
	private String sector;

	@Column
	private String message;

	@Column
	private EventType eventType;

	@Column
	private String forecastType;

	public PeriodRumour(Company company, CompanyPeriodReport companyPeriodReport, Date dateInformationAvailable, Date rumourExpires, int reputationRequired,
			String message, EventType eventType, String forecastType) {
		this.company = company;
		this.companyPeriodReport = companyPeriodReport;
		this.dateInformationAvailable = dateInformationAvailable;
		this.dateRumourExpires = rumourExpires;
		this.reputationRequired = reputationRequired;
		this.sector = company.getSector();
		this.message = message;
		this.eventType = eventType;
		this.forecastType = forecastType;
	}

	public PeriodRumour() {
		super();
		// TODO Auto-generated constructor stub
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
	 * @return the companyPeriodReport
	 */
	public CompanyPeriodReport getCompanyPeriodReport() {
		return companyPeriodReport;
	}

	/**
	 * @param companyPeriodReport
	 *            the companyPeriodReport to set
	 */
	public void setCompanyPeriodReport(CompanyPeriodReport companyPeriodReport) {
		this.companyPeriodReport = companyPeriodReport;
	}

	/**
	 * @return the dateInformationAvailable
	 */
	public Date getDateInformationAvailable() {
		return dateInformationAvailable;
	}

	/**
	 * @param dateInformationAvailable
	 *            the dateInformationAvailable to set
	 */
	public void setDateInformationAvailable(Date dateInformationAvailable) {
		this.dateInformationAvailable = dateInformationAvailable;
	}

	/**
	 * @return the dateRumourExpires
	 */
	public Date getDateRumourExpires() {
		return dateRumourExpires;
	}

	/**
	 * @param dateRumourExpires
	 *            the dateRumourExpires to set
	 */
	public void setDateRumourExpires(Date dateRumourExpires) {
		this.dateRumourExpires = dateRumourExpires;
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
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the forecastType
	 */
	public String getForecastType() {
		return forecastType;
	}

	/**
	 * @param forecastType
	 *            the forecastType to set
	 */
	public void setForecastType(String forecastType) {
		this.forecastType = forecastType;
	}

	/**
	 * @return the reputationRequired
	 */
	public int getReputationRequired() {
		return reputationRequired;
	}

	/**
	 * @param reputationRequired
	 *            the reputationRequired to set
	 */
	public void setReputationRequired(int reputationRequired) {
		this.reputationRequired = reputationRequired;
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

}
