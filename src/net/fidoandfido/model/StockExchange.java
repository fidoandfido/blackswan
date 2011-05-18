package net.fidoandfido.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
	private long periodLength;

	@Column
	private long defaultPrimeInterestRateBasisPoints;

	@Column
	private String economicModifierName = "";

	@Column
	private String companyModifierName = "";

	@Column
	private boolean updating = false;

	@ManyToOne
	private ExchangeGroup exchangeGroup;

	/**
	 * Maximum price a share can be before it is split 2-1.
	 */
	@Column
	private long maxSharePrice = 0;

	@Column
	long requiredExperiencePoints = 0;

	@OneToOne
	@Cascade(value = CascadeType.ALL)
	private StockExchangePeriod currentPeriod;

	public StockExchange() {
		// Default constructor required for persistence
	}

	public StockExchange(ExchangeGroup exchangeGroup, String name, String description, int companyCount, String eventGeneratorName, long companyPeriodLength,
			long defaultPrimeInterestRateBasisPoints, String economicModifierName, String companyModifierName, long maxSharePrice, long requiredExperiencePoints)

	{
		super();
		this.exchangeGroup = exchangeGroup;
		this.name = name;
		this.description = description;
		this.companyCount = companyCount;
		this.eventGeneratorName = eventGeneratorName;
		this.periodLength = companyPeriodLength;
		this.defaultPrimeInterestRateBasisPoints = defaultPrimeInterestRateBasisPoints;
		this.economicModifierName = economicModifierName;
		this.companyModifierName = companyModifierName;
		this.maxSharePrice = maxSharePrice;
		this.requiredExperiencePoints = requiredExperiencePoints;
	}

	/**
	 * For testing purposes
	 * 
	 * @param name
	 * @param defaultPrimeInterestRateBasisPoints
	 */
	public StockExchange(String name, long defaultPrimeInterestRateBasisPoints) {
		this.name = name;
		this.defaultPrimeInterestRateBasisPoints = defaultPrimeInterestRateBasisPoints;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + " -> Company count: " + companyCount + " -- " + description + " -- " + exchangeGroup.getName();
	}

	/**
	 * @return the primeInterestRateBasisPoints
	 */
	public long getPrimeInterestRateBasisPoints() {
		if (currentPeriod != null) {
			return currentPeriod.getInterestRateBasisPointsDelta() + defaultPrimeInterestRateBasisPoints;
		}
		return defaultPrimeInterestRateBasisPoints;
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
	public long getPeriodLength() {
		return periodLength;
	}

	/**
	 * @param companyPeriodLength
	 *            the companyPeriodLength to set
	 */
	public void setPeriodLength(long companyPeriodLength) {
		this.periodLength = companyPeriodLength;
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

	/**
	 * @return the modifierName
	 */
	public String getEconomicModifierName() {
		return economicModifierName;
	}

	/**
	 * @param modifierName
	 *            the modifierName to set
	 */
	public void setEconomicModifierName(String modifierName) {
		this.economicModifierName = modifierName;
	}

	/**
	 * @return the defaultPrimeInterestRateBasisPoints
	 */
	public long getDefaultPrimeInterestRateBasisPoints() {
		return defaultPrimeInterestRateBasisPoints;
	}

	/**
	 * @param defaultPrimeInterestRateBasisPoints
	 *            the defaultPrimeInterestRateBasisPoints to set
	 */
	public void setDefaultPrimeInterestRateBasisPoints(long defaultPrimeInterestRateBasisPoints) {
		this.defaultPrimeInterestRateBasisPoints = defaultPrimeInterestRateBasisPoints;
	}

	/**
	 * @return the companyModifierName
	 */
	public String getCompanyModifierName() {
		return companyModifierName;
	}

	/**
	 * @param companyModifierName
	 *            the companyModifierName to set
	 */
	public void setCompanyModifierName(String companyModifierName) {
		this.companyModifierName = companyModifierName;
	}

	/**
	 * @return the maxSharePrice
	 */
	public long getMaxSharePrice() {
		return maxSharePrice;
	}

	/**
	 * @param maxSharePrice
	 *            the maxSharePrice to set
	 */
	public void setMaxSharePrice(long maxSharePrice) {
		this.maxSharePrice = maxSharePrice;
	}

	/**
	 * @return the exchangeGroup
	 */
	public ExchangeGroup getExchangeGroup() {
		return exchangeGroup;
	}

	/**
	 * @param exchangeGroup
	 *            the exchangeGroup to set
	 */
	public void setExchangeGroup(ExchangeGroup exchangeGroup) {
		this.exchangeGroup = exchangeGroup;
	}

	/**
	 * @return the requiredExperiencePoints
	 */
	public long getRequiredExperiencePoints() {
		return requiredExperiencePoints;
	}

	/**
	 * @param requiredExperiencePoints
	 *            the requiredExperiencePoints to set
	 */
	public void setRequiredExperiencePoints(long requiredExperiencePoints) {
		this.requiredExperiencePoints = requiredExperiencePoints;
	}

}
