package net.fidoandfido.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.fidoandfido.engine.experience.LevelManager;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Trader")
public class Trader {
	@Id
	@Column(name = "trader_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@Column
	private String name;

	@Column
	private long cash = 0;

	@Column
	private boolean isAITrader = false;

	@Column(nullable = true)
	private String aiStrategyName = null;

	@Column
	private boolean isMarketMaker = false;

	@OneToOne
	private User user;

	@Column
	private long experiencePoints = 0;

	@ManyToMany
	private Set<ReputationItem> reputationItems = new HashSet<ReputationItem>();

	@ManyToOne
	private ExchangeGroup group;;

	public Trader() {
		// Default constructor required for persistence code.
	}

	/**
	 * Used to create a new trader for a playing User by the application
	 * 
	 * @param user
	 * @param name
	 * @param cash
	 */
	public Trader(User user, String name, long cash) {
		this.user = user;
		this.name = name;
		this.cash = cash;
	}

	/**
	 * User to create a new AI trader in the application.
	 * 
	 * @param name
	 * @param cash
	 * @param isMarketMaker
	 */
	public Trader(String name, long cash, boolean isMarketMaker, String strategy) {
		this.name = name;
		this.cash = cash;
		this.isAITrader = true;
		this.isMarketMaker = isMarketMaker;
		this.aiStrategyName = strategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(" - cash: ");
		builder.append(cash);
		return builder.toString();
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
	 * @return the cash
	 */
	public long getCash() {
		return cash;
	}

	public long getAvailableCash() {
		// by default, return the cash
		return cash;
	}

	/**
	 * @param cash
	 *            the cash to set
	 */
	public void setCash(long cash) {
		this.cash = cash;
	}

	public void takeCash(long i) {
		cash = cash - i;
	}

	public void giveCash(long i) {
		cash = cash + i;
	}

	public boolean canMakeTrade(long orderValue) {
		if (isMarketMaker) {
			return true;
		}
		if (cash >= orderValue) {
			return true;
		}
		return false;
	}

	/**
	 * @return the isAI
	 */
	public boolean isAITrader() {
		return isAITrader;
	}

	/**
	 * @param isAI
	 *            the isAI to set
	 */
	public void setAITrader(boolean isAI) {
		this.isAITrader = isAI;
	}

	/**
	 * @return the isAIFluidity
	 */
	public boolean isMarketMaker() {
		return isMarketMaker;
	}

	/**
	 * @param isAIFluidity
	 *            the isAIFluidity to set
	 */
	public void setMarketMaker(boolean isAIFluidity) {
		this.isMarketMaker = isAIFluidity;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the experiencePoints
	 */
	public long getExperiencePoints() {
		return experiencePoints;
	}

	/**
	 * @param experiencePoints
	 *            the experiencePoints to set
	 */
	public void setExperiencePoints(long experiencePoints) {
		this.experiencePoints = experiencePoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trader other = (Trader) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the aiStrategyName
	 */
	public String getAiStrategyName() {
		return aiStrategyName;
	}

	/**
	 * @param aiStrategyName
	 *            the aiStrategyName to set
	 */
	public void setAiStrategyName(String aiStrategyName) {
		this.aiStrategyName = aiStrategyName;
	}

	/**
	 * @return the reputationItems
	 */
	public Set<ReputationItem> getReputationItems() {
		return reputationItems;
	}

	/**
	 * @param reputationItems
	 *            the reputationItems to set
	 */
	public void setReputationItems(Set<ReputationItem> reputationItems) {
		this.reputationItems = reputationItems;
	}

	public int getReputation(String sector) {
		int reputation = 0;
		for (ReputationItem item : reputationItems) {
			reputation = reputation + item.getReputation(sector);
		}
		return reputation;
	}

	public void addItem(ReputationItem item) {
		this.reputationItems.add(item);
	}

	public boolean hasItem(ReputationItem item) {
		return reputationItems.contains(item);
	}

	public void removeItem(ReputationItem item) {
		reputationItems.remove(item);
	}

	/**
	 * @return the group
	 */
	public ExchangeGroup getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(ExchangeGroup group) {
		this.group = group;
	}

	public void addExperiencePoints(long points) {
		// Notify the level manager if we level up.
		LevelManager levelManager = new LevelManager();
		int level = levelManager.getLevel(this);
		this.experiencePoints += points;
		if (levelManager.getLevel(this) > level) {
			levelManager.levelUp(this);
		}
	}

	public int getLevel() {
		return new LevelManager().getLevel(this);
	}

}
