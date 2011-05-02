package net.fidoandfido.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.fidoandfido.engine.OrderProcessor;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "StockExchange")
public class StockExchange {

	@Id
	@Column(name = "stock_exchange_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@Column
	private String name;

	@Column
	private String description;

	@Column
	private int companyCount;

	@Column
	private String eventGeneratorName;

	@Column
	private long companyPeriodLength;

	@Column
	private long primeInterestRateBasisPoints;

	@Column
	private boolean updating = false;

	@OneToOne
	@Cascade(value = CascadeType.ALL)
	private StockExchangePeriod currentPeriod;

	public StockExchange() {
		// Default constructor required for persistence
	}

	public StockExchange(String name, String description, int companyCount, String eventGeneratorName, long companyPeriodLength,
			long primeInterestRateBasisPoints)

	{
		super();
		this.description = description;
		this.name = name;
		this.companyCount = companyCount;
		this.eventGeneratorName = eventGeneratorName;
		this.companyPeriodLength = companyPeriodLength;
		this.primeInterestRateBasisPoints = primeInterestRateBasisPoints;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + " -> Company count: " + companyCount + " -- " + description;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the companyCount
	 */
	public int getCompanyCount() {
		return companyCount;
	}

	/**
	 * @param companyCount
	 *            the companyCount to set
	 */
	public void setCompanyCount(int companyCount) {
		this.companyCount = companyCount;
	}

	/**
	 * @return the eventGeneratorName
	 */
	public String getEventGeneratorName() {
		return eventGeneratorName;
	}

	/**
	 * @param eventGeneratorName
	 *            the eventGeneratorName to set
	 */
	public void setEventGeneratorName(String eventGeneratorName) {
		this.eventGeneratorName = eventGeneratorName;
	}

	/**
	 * @return the companyPeriodLength
	 */
	public long getCompanyPeriodLength() {
		return companyPeriodLength;
	}

	/**
	 * @param companyPeriodLength
	 *            the companyPeriodLength to set
	 */
	public void setCompanyPeriodLength(long companyPeriodLength) {
		this.companyPeriodLength = companyPeriodLength;
	}

	/**
	 * @return the primeInterestRate
	 */
	public long getPrimeInterestRate() {
		return primeInterestRateBasisPoints;
	}

	/**
	 * @param primeInterestRate
	 *            the primeInterestRate to set
	 */
	public void setPrimeInterestRate(long primeInterestRate) {
		this.primeInterestRateBasisPoints = primeInterestRate;
	}

	/**
	 * Process an order.
	 * 
	 * @param buyOrder
	 */
	public void processOrder(Order buyOrder) {
		OrderProcessor processor = new OrderProcessor();
		processor.processOrder(buyOrder);
	}

	/**
	 * @return the primeInterestRateBasisPoints
	 */
	public long getPrimeInterestRateBasisPoints() {
		return primeInterestRateBasisPoints;
	}

	/**
	 * @param primeInterestRateBasisPoints
	 *            the primeInterestRateBasisPoints to set
	 */
	public void setPrimeInterestRateBasisPoints(long primeInterestRateBasisPoints) {
		this.primeInterestRateBasisPoints = primeInterestRateBasisPoints;
	}

	/**
	 * @return the updating
	 */
	public boolean isUpdating() {
		return updating;
	}

	/**
	 * @param updating
	 *            the updating to set
	 */
	public void setUpdating(boolean updating) {
		this.updating = updating;
	}

	/**
	 * @return the currentPeriod
	 */
	public StockExchangePeriod getCurrentPeriod() {
		return currentPeriod;
	}

	/**
	 * @param currentPeriod
	 *            the currentPeriod to set
	 */
	public void setCurrentPeriod(StockExchangePeriod currentPeriod) {
		this.currentPeriod = currentPeriod;
	}

}
