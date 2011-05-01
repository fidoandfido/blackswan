package net.fidoandfido.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ReputationItem")
public class ReputationItem {

	@Id
	@Column(name = "reputation_item_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@Column
	private String name;

	@Column
	private long cost = 0;

	@Column
	private String image;

	@Column
	private boolean isLimited = false;

	@Column
	private long remainingCount = 0;

	@OneToMany()
	@Cascade(value = CascadeType.ALL)
	private Set<ReputationEffect> effectList = new HashSet<ReputationEffect>();

	public ReputationItem() {
		// Nothing to do here!
	}

	public ReputationItem(String name, long cost, String image) {
		super();
		this.name = name;
		this.cost = cost;
		this.image = image;
	}

	public ReputationItem(String name, long cost, String image, boolean isLimited, long remainingCount) {
		super();
		this.name = name;
		this.cost = cost;
		this.image = image;
		this.isLimited = isLimited;
		this.remainingCount = remainingCount;
	}

	public void addEffect(ReputationEffect effect) {
		effectList.add(effect);
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
		result = prime * result + (int) (cost ^ (cost >>> 32));
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
		ReputationItem other = (ReputationItem) obj;
		if (cost != other.cost)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
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
	 * @return the cost
	 */
	public long getCost() {
		return cost;
	}

	/**
	 * @param cost
	 *            the cost to set
	 */
	public void setCost(long cost) {
		this.cost = cost;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the isLimited
	 */
	public boolean isLimited() {
		return isLimited;
	}

	/**
	 * @param isLimited
	 *            the isLimited to set
	 */
	public void setLimited(boolean isLimited) {
		this.isLimited = isLimited;
	}

	/**
	 * @return the remainingCount
	 */
	public long getRemainingCount() {
		return remainingCount;
	}

	/**
	 * @param remainingCount
	 *            the remainingCount to set
	 */
	public void setRemainingCount(long remainingCount) {
		this.remainingCount = remainingCount;
	}

	/**
	 * @return the effectList
	 */
	public Set<ReputationEffect> getEffectList() {
		return effectList;
	}

	/**
	 * @param effectList
	 *            the effectList to set
	 */
	public void setEffectList(Set<ReputationEffect> effectList) {
		this.effectList = effectList;
	}

	public int getReputation(String sector) {
		if (sector == null || sector.isEmpty()) {
			return 0;
		}
		for (ReputationEffect effect : effectList) {
			if (sector.equals(effect.getSector())) {
				return effect.getPoints();
			}
		}
		return 0;
	}

}
