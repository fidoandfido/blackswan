package net.fidoandfido.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "TraderMessage")
public class TraderMessage {

	@Id
	@Column(name = "group_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@Column
	private Date date;

	@Column
	private String subject;

	@Column
	private String body;

	@ManyToOne
	private Trader forTrader;

	@Column
	private boolean gameMessage;

	@ManyToOne
	private Trader fromTrader;

	@Column
	private boolean isRead;

	@Column
	private boolean current;

	public TraderMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TraderMessage(Trader forTrader, Date date, String subject, String body) {
		super();
		this.date = date;
		this.subject = subject;
		this.body = body;
		this.forTrader = forTrader;
		this.fromTrader = null;
		this.gameMessage = true;
		this.isRead = false;
		this.current = true;
	}

	public TraderMessage(Date date, String subject, String body, Trader forTrader, Trader fromTrader) {
		super();
		this.date = date;
		this.subject = subject;
		this.body = body;
		this.forTrader = forTrader;
		this.fromTrader = fromTrader;
		this.gameMessage = false;
		this.isRead = false;
		this.current = true;
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
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the gameMessage
	 */
	public boolean isGameMessage() {
		return gameMessage;
	}

	/**
	 * @param gameMessage
	 *            the gameMessage to set
	 */
	public void setGameMessage(boolean gameMessage) {
		this.gameMessage = gameMessage;
	}

	/**
	 * @return the fromTrader
	 */
	public Trader getFromTrader() {
		return fromTrader;
	}

	/**
	 * @param fromTrader
	 *            the fromTrader to set
	 */
	public void setFromTrader(Trader fromTrader) {
		this.fromTrader = fromTrader;
	}

	/**
	 * @return the isRead
	 */
	public boolean isRead() {
		return isRead;
	}

	/**
	 * @param isRead
	 *            the isRead to set
	 */
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	/**
	 * @return the forTrader
	 */
	public Trader getForTrader() {
		return forTrader;
	}

	/**
	 * @param forTrader
	 *            the forTrader to set
	 */
	public void setForTrader(Trader forTrader) {
		this.forTrader = forTrader;
	}

	/**
	 * @return the current
	 */
	public boolean isCurrent() {
		return current;
	}

	/**
	 * @param current
	 *            the current to set
	 */
	public void setCurrent(boolean current) {
		this.current = current;
	}

}
