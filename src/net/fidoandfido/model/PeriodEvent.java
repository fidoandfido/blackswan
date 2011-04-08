package net.fidoandfido.model;

import java.util.Comparator;
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
@Table(name = "PeriodEvent")
public class PeriodEvent {

	public static class EventCompator implements Comparator<PeriodEvent> {

		@Override
		public int compare(PeriodEvent o1, PeriodEvent o2) {
			Date date1 = o1.getDateInformationAvailable();
			Date date2 = o2.getDateInformationAvailable();

			if (date1 == date2) {
				return 0;
			}

			if (date1 == null || date2 == null) {
				if (date1 == null && date2 == null) {
					return 0;
				} else if (date1 == null) {
					return 1;
				} else {
					return -1;
				}
			}
			if (date1.before(date2)) {
				return -1;
			}
			if (date1.after(date2)) {
				return 1;
			}
			return 0;
		}
	}

	@Id
	@Column(name = "period_event_id")
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
	private String message;

	@Column
	private EventType eventType;

	@Column
	private long expectedProfit;

	@Column
	private String forecastType;

	public PeriodEvent() {
		// Default constructor required for persistence
	}

	public PeriodEvent(Company company, CompanyPeriodReport companyPeriodReport, Date dateInformationAvailable, String message, EventType eventType,
			long expectedProfit, String forecastType) {
		super();
		this.company = company;
		this.companyPeriodReport = companyPeriodReport;
		this.dateInformationAvailable = dateInformationAvailable;
		this.message = message;
		this.eventType = eventType;
		this.expectedProfit = expectedProfit;
		this.forecastType = forecastType;
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
	 * @return the expectedProfit
	 */
	public long getExpectedProfit() {
		return expectedProfit;
	}

	/**
	 * @param expectedProfit
	 *            the expectedProfit to set
	 */
	public void setExpectedProfit(long expectedProfit) {
		this.expectedProfit = expectedProfit;
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

}
