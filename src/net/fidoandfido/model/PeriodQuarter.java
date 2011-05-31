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

import net.fidoandfido.engine.quarter.QuarterData;
import net.fidoandfido.util.Constants.QuarterPerformanceType;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "PeriodEvent")
public class PeriodQuarter {

	public static class EventCompator implements Comparator<PeriodQuarter> {

		@Override
		public int compare(PeriodQuarter o1, PeriodQuarter o2) {
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
	private QuarterPerformanceType eventType;

	@Column
	private long profit;

	@Column
	private long revenue;

	@Column
	private long expenses;

	@Column
	private long interest;

	@Column
	private long runningProfit;

	@Column
	private long runningRevenue;

	@Column
	private long runningExpenses;

	@Column
	private long runningInterest;

	@Column
	private String announcementType;

	public PeriodQuarter() {
		// Default constructor required for persistence
	}

	public PeriodQuarter(Company company, CompanyPeriodReport companyPeriodReport, Date dateInformationAvailable, String message, QuarterPerformanceType eventType,
			String forecastType) {
		super();
		this.company = company;
		this.companyPeriodReport = companyPeriodReport;
		this.dateInformationAvailable = dateInformationAvailable;
		this.message = message;
		this.eventType = eventType;
		this.announcementType = forecastType;
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
	public QuarterPerformanceType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(QuarterPerformanceType eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the expectedProfit
	 */
	public long getProfit() {
		return profit;
	}

	/**
	 * @param expectedProfit
	 *            the expectedProfit to set
	 */
	public void setProfit(long expectedProfit) {
		this.profit = expectedProfit;
	}

	public long getRevenue() {
		return revenue;
	}

	public void setRevenue(long revenue) {
		this.revenue = revenue;
	}

	public long getExpenses() {
		return expenses;
	}

	public void setExpenses(long expenses) {
		this.expenses = expenses;
	}

	public long getInterest() {
		return interest;
	}

	public void setInterest(long interest) {
		this.interest = interest;
	}

	/**
	 * @return the runningProfit
	 */
	public long getRunningProfit() {
		return runningProfit;
	}

	/**
	 * @param runningProfit
	 *            the runningProfit to set
	 */
	public void setRunningProfit(long runningProfit) {
		this.runningProfit = runningProfit;
	}

	/**
	 * @return the runningRevenue
	 */
	public long getRunningRevenue() {
		return runningRevenue;
	}

	/**
	 * @param runningRevenue
	 *            the runningRevenue to set
	 */
	public void setRunningRevenue(long runningRevenue) {
		this.runningRevenue = runningRevenue;
	}

	/**
	 * @return the runningExpenses
	 */
	public long getRunningExpenses() {
		return runningExpenses;
	}

	/**
	 * @param runningExpenses
	 *            the runningExpenses to set
	 */
	public void setRunningExpenses(long runningExpenses) {
		this.runningExpenses = runningExpenses;
	}

	/**
	 * @return the runningInterest
	 */
	public long getRunningInterest() {
		return runningInterest;
	}

	/**
	 * @param runningInterest
	 *            the runningInterest to set
	 */
	public void setRunningInterest(long runningInterest) {
		this.runningInterest = runningInterest;
	}

	/**
	 * @return the forecastType
	 */
	public String getAnnouncementType() {
		return announcementType;
	}

	/**
	 * @param forecastType
	 *            the forecastType to set
	 */
	public void setAnnouncementType(String forecastType) {
		this.announcementType = forecastType;
	}

	public void setData(QuarterData currentData) {
		this.profit = currentData.getProfit();
		this.expenses = currentData.getExpenses();
		this.revenue = currentData.getRevenue();
		this.interest = currentData.getInterestPaid();
		this.runningProfit = currentData.getRunningProfit();
		this.runningExpenses = currentData.getRunningExpenses();
		this.runningRevenue = currentData.getRunningRevenue();
		this.runningInterest = currentData.getRunningInterestPaid();
	}

	public boolean isBad() {
		boolean isBad = false;
		switch (eventType) {
		case CATASTROPHIC:
		case TERRIBLE:
		case POOR:
			isBad = true;
			break;
		case AVERAGE:
		case EXTRAORDINARY:
		case GOOD:
		case GREAT:
			isBad = false;
			break;
		}
		return isBad;
	}

	public boolean isAverage() {
		return eventType == QuarterPerformanceType.AVERAGE;
	}

	public boolean isGood() {
		boolean isGood = false;
		switch (eventType) {
		case EXTRAORDINARY:
		case GOOD:
		case GREAT:
			isGood = true;
			break;
		case AVERAGE:
		case CATASTROPHIC:
		case TERRIBLE:
		case POOR:
			isGood = false;
			break;
		}
		return isGood;
	}
}
