package net.fidoandfido.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ExchangeGroup")
public class ExchangeGroup {

	@Id
	@Column(name = "group_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@Column(unique = true)
	private String name;

	@Column
	private String description;

	/**
	 * Period length (in milliseconds) for this exchange group
	 */
	@Column
	private long periodLength;

	@OneToMany
	private Set<StockExchange> exchanges = new HashSet<StockExchange>();

	@Column
	private boolean updating = false;

	public ExchangeGroup(String name, String description, long periodLength) {
		this.name = name;
		this.description = description;
		this.periodLength = periodLength;
	}

	public ExchangeGroup() {
		// Nothing to do here
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
	 * @return the periodLength
	 */
	public long getPeriodLength() {
		return periodLength;
	}

	/**
	 * @param periodLength
	 *            the periodLength to set
	 */
	public void setPeriodLength(long periodLength) {
		this.periodLength = periodLength;
	}

	/**
	 * @return the exchangeList
	 */
	public Set<StockExchange> getExchanges() {
		return exchanges;
	}

	/**
	 * @param exchangeList
	 *            the exchangeList to set
	 */
	public void setExchanges(Set<StockExchange> exchangeList) {
		this.exchanges = exchangeList;
	}

	public void addExchange(StockExchange exchange) {
		this.exchanges.add(exchange);
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

}
