package net.fidoandfido.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author andy
 * 
 */
@Embeddable
public class SectorOutlook {

	public static final String DEFAULT_NEUTRAL_MESSAGE = "Neutral conditions for sector ahead.";

	@Column(nullable = false)
	private String sector;

	@Column(nullable = false)
	private long revenueModifier;

	@Column(nullable = false)
	private long expenseModifier;

	@Column(nullable = false)
	private String outlookMessage;

	/**
	 * @return the sector
	 */
	public String getSector() {
		return sector;
	}

	public SectorOutlook() {
		// Default constructor (for persistence)
	}

	public SectorOutlook(String sector, int revenueModifier, int expenseModifier, String outlook) {
		super();
		this.sector = sector;
		this.revenueModifier = revenueModifier;
		this.expenseModifier = expenseModifier;
		this.outlookMessage = outlook;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SectorOutlook: " + sector + ", revenueModifier=" + revenueModifier + ", expenseModifier=" + expenseModifier + ", outlookMessage="
				+ outlookMessage;
	}

	/**
	 * @param sector
	 *            the sector to set
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}

	/**
	 * @return the revenueModifier
	 */
	public long getRevenueModifier() {
		return revenueModifier;
	}

	/**
	 * @param revenueModifier
	 *            the revenueModifier to set
	 */
	public void setRevenueModifier(long revenueModifier) {
		this.revenueModifier = revenueModifier;
	}

	/**
	 * @return the expenseModifier
	 */
	public long getExpenseModifier() {
		return expenseModifier;
	}

	/**
	 * @param expenseModifier
	 *            the expenseModifier to set
	 */
	public void setExpenseModifier(long expenseModifier) {
		this.expenseModifier = expenseModifier;
	}

	/**
	 * @return the outlook
	 */
	public String getOutlookMessage() {
		return outlookMessage;
	}

	/**
	 * @param outlook
	 *            the outlook to set
	 */
	public void setOutlookMessage(String outlook) {
		this.outlookMessage = outlook;
	}

}