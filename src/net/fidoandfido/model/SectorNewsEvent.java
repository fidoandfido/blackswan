package net.fidoandfido.model;

import net.fidoandfido.util.Constants;

public class SectorNewsEvent {

	private String sector;

	// private String longMessage;
	private Constants.EventType firstEventType;
	// private Date longEventDate;

	// private String shortMessage;
	private Constants.EventType secondEventType;

	// private Date shortEventDate;

	public SectorNewsEvent() {
		// Default constructor required for persistence
	}

	public SectorNewsEvent(String sector) {
		this.sector = sector;
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

	//
	// /**
	// * @return the longMessage
	// */
	// public String getLongMessage() {
	// return longMessage;
	// }
	//
	// /**
	// * @param longMessage
	// * the longMessage to set
	// */
	// public void setLongMessage(String longMessage) {
	// this.longMessage = longMessage;
	// }

	/**
	 * @return the longEventType
	 */
	public Constants.EventType getFirstEventType() {
		return firstEventType;
	}

	/**
	 * @param longEventType
	 *            the longEventType to set
	 */
	public void setFirstEventType(Constants.EventType longEventType) {
		this.firstEventType = longEventType;
	}

	// /**
	// * @return the longEventDate
	// */
	// public Date getLongEventDate() {
	// return longEventDate;
	// }
	//
	// /**
	// * @param longEventDate
	// * the longEventDate to set
	// */
	// public void setLongEventDate(Date longEventDate) {
	// this.longEventDate = longEventDate;
	// }
	//
	// /**
	// * @return the shortMessage
	// */
	// public String getShortMessage() {
	// return shortMessage;
	// }
	//
	// /**
	// * @param shortMessage
	// * the shortMessage to set
	// */
	// public void setShortMessage(String shortMessage) {
	// this.shortMessage = shortMessage;
	// }

	/**
	 * @return the shortEventType
	 */
	public Constants.EventType getSecondEventType() {
		return secondEventType;
	}

	/**
	 * @param shortEventType
	 *            the shortEventType to set
	 */
	public void setSecondEventType(Constants.EventType shortEventType) {
		this.secondEventType = shortEventType;
	}
	//
	// /**
	// * @return the shortEventDate
	// */
	// public Date getShortEventDate() {
	// return shortEventDate;
	// }
	//
	// /**
	// * @param shortEventDate
	// * the shortEventDate to set
	// */
	// public void setShortEventDate(Date shortEventDate) {
	// this.shortEventDate = shortEventDate;
	// }
}
