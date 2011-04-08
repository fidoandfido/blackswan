package net.fidoandfido.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "User")
// Nothing in here at the moment.
public class User {

	// For the moment, we will create a super user.
	public static User getSuperUser() {
		return SuperUser;
	}

	private static final User SuperUser = new User();
	static {
		SuperUser.setUserName("SUPER");
		SuperUser.userAdmin = true;
	}

	@Id
	@Column(name = "trader_id")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@Column
	private String userName;

	@Column
	private String password;

	@OneToOne
	private Trader trader;

	@Column
	private boolean userAdmin;

	public User() {
		// Defualt constructor for Hibernate.
	}

	public User(String name, String password, boolean userAdmin) {
		super();
		this.userName = name;
		this.password = password;
		this.userAdmin = userAdmin;
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
	public String getUserName() {
		return userName;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setUserName(String name) {
		this.userName = name;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the trader
	 */
	public Trader getTrader() {
		return trader;
	}

	/**
	 * @param trader
	 *            the trader to set
	 */
	public void setTrader(Trader trader) {
		this.trader = trader;
	}

	/**
	 * @return the userAdmin
	 */
	public boolean isUserAdmin() {
		return userAdmin;
	}

	/**
	 * @param userAdmin
	 *            the userAdmin to set
	 */
	public void setUserAdmin(boolean userAdmin) {
		this.userAdmin = userAdmin;
	}

	public boolean validatePassword(String password2) {
		// TODO Auto-generated method stub
		return (password == null || password.equals(password2));
	}

}
