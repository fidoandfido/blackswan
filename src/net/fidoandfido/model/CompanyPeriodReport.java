package net.fidoandfido.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * @author andy
 * 
 */
@Entity
@Table(name = "CompanyPeriodReport")
public class CompanyPeriodReport {
	@Id
	@Column(name = "company_period_report_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;

	@Column
	private Date startDate;

	@Column
	private long startingExpectedProfit;

	@Column
	private long startingExpectedRevenue;

	@Column
	private long startingExpectedExpenses;

	@Column
	private long startingExpectedInterest;

	@Column
	private long startingAssets;

	@Column
	private long startingDebt;

	/**
	 * Need this, since the company may have shares splitting
	 */
	@Column
	private long outstandingShareCount;

	// ///////////////////////
	// List of 'forecasts'
	// ///////////////////////
	@OneToMany
	@Sort(type = SortType.COMPARATOR, comparator = PeriodEvent.EventCompator.class)
	private Set<PeriodEvent> periodEventList = new HashSet<PeriodEvent>();
	// ///////////////////////
	// List of 'rumours'
	// ///////////////////////
	@OneToMany(fetch = FetchType.LAZY)
	private Set<PeriodRumour> periodRumourList = new HashSet<PeriodRumour>();

	// /////////////////////////////
	// FINAL INFORMATION
	// /////////////////////////////

	@Column
	private long finalProfit;

	@Column
	private long finalRevenue;

	@Column
	private long finalExpenses;

	@Column
	private long finalInterest;

	@Column
	private Date minimumEndDate;

	@Column
	private Date closeDate;

	@Column
	private boolean open;

	@Column
	private long generation;

	public CompanyPeriodReport() {
		// Default constructor required for persistence
	}

	public CompanyPeriodReport(Company company, Date startDate, long periodLength, long generation) {
		this.company = company;
		this.startDate = startDate;
		this.startingAssets = company.getAssetValue();
		this.startingDebt = company.getDebtValue();
		this.outstandingShareCount = company.getOutstandingShares();
		// Set the minimum end date to be the start date plus period length
		// minus the period buffer.
		this.minimumEndDate = new Date(startDate.getTime() + periodLength);
		this.open = true;
		this.generation = generation;
		periodEventList = new TreeSet<PeriodEvent>(new PeriodEvent.EventCompator());
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
	 * @return the startingExpectedProfit
	 */
	public long getStartingExpectedProfit() {
		return startingExpectedProfit;
	}

	/**
	 * @param startingExpectedProfit
	 *            the startingExpectedProfit to set
	 */
	public void setStartingExpectedProfit(long startingExpectedProfit) {
		this.startingExpectedProfit = startingExpectedProfit;
	}

	public long getStartingExpectedRevenue() {
		return startingExpectedRevenue;
	}

	public void setStartingExpectedRevenue(long startingExpectedRevenue) {
		this.startingExpectedRevenue = startingExpectedRevenue;
	}

	public long getStartingExpectedExpenses() {
		return startingExpectedExpenses;
	}

	public void setStartingExpectedExpenses(long startingExpectedExpenses) {
		this.startingExpectedExpenses = startingExpectedExpenses;
	}

	public long getStartingExpectedInterest() {
		return startingExpectedInterest;
	}

	public void setStartingExpectedInterest(long startingExpectedInterest) {
		this.startingExpectedInterest = startingExpectedInterest;
	}

	/**
	 * @return the finalProfit
	 */
	public long getFinalProfit() {
		return finalProfit;
	}

	/**
	 * @param finalProfit
	 *            the finalProfit to set
	 */
	public void setFinalProfit(long finalProfit) {
		this.finalProfit = finalProfit;
	}

	public long getFinalRevenue() {
		return finalRevenue;
	}

	public void setFinalRevenue(long finalRevenue) {
		this.finalRevenue = finalRevenue;
	}

	public long getFinalExpenses() {
		return finalExpenses;
	}

	public void setFinalExpenses(long finalExpenses) {
		this.finalExpenses = finalExpenses;
	}

	public long getFinalInterest() {
		return finalInterest;
	}

	public void setFinalInterest(long finalInterest) {
		this.finalInterest = finalInterest;
	}

	/**
	 * @return the startingAssets
	 */
	public long getStartingAssets() {
		return startingAssets;
	}

	/**
	 * @param startingAssets
	 *            the startingAssets to set
	 */
	public void setStartingAssets(long startingAssets) {
		this.startingAssets = startingAssets;
	}

	/**
	 * @return the startingDebt
	 */
	public long getStartingDebt() {
		return startingDebt;
	}

	/**
	 * @param startingDebt
	 *            the startingDebt to set
	 */
	public void setStartingDebt(long startingDebt) {
		this.startingDebt = startingDebt;
	}

	/**
	 * @return the outstandingShareCount
	 */
	public long getOutstandingShareCount() {
		return outstandingShareCount;
	}

	/**
	 * @param outstandingShareCount
	 *            the outstandingShareCount to set
	 */
	public void setOutstandingShareCount(long outstandingShareCount) {
		this.outstandingShareCount = outstandingShareCount;
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

	public void close(Date currentDate) {
		open = false;
		this.closeDate = currentDate;
	}

	/**
	 * @return the periodEventList
	 */
	public Set<PeriodEvent> getPeriodEventList() {
		return periodEventList;
	}

	/**
	 * @param periodEventList
	 *            the periodEventList to set
	 */
	public void setPeriodEventList(Set<PeriodEvent> periodEventList) {
		this.periodEventList = periodEventList;
	}

	/**
	 * @return the periodRumourList
	 */
	public Set<PeriodRumour> getPeriodRumourList() {
		return periodRumourList;
	}

	/**
	 * @param periodRumourList
	 *            the periodRumourList to set
	 */
	public void setPeriodRumourList(Set<PeriodRumour> periodRumourList) {
		this.periodRumourList = periodRumourList;
	}

	public void addRumour(PeriodRumour rumour) {
		periodRumourList.add(rumour);
	}

	public void addPeriodEvent(PeriodEvent periodEvent) {
		periodEventList.add(periodEvent);
	}

	public Map<String, PeriodEvent> getPeriodPartInformationMappedByEvent() {
		HashMap<String, PeriodEvent> resultsMap = new HashMap<String, PeriodEvent>();
		for (PeriodEvent event : periodEventList) {
			resultsMap.put(event.getAnnouncementType(), event);
		}
		return resultsMap;
	}

	public long getPeriodLength() {
		return getCompany().getStockExchange().getPeriodLength();
	}

}
