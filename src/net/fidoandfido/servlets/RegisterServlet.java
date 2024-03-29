package net.fidoandfido.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderMessageDAO;
import net.fidoandfido.dao.UserDAO;
import net.fidoandfido.dao.UserSessionDAO;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderMessage;
import net.fidoandfido.model.User;
import net.fidoandfido.model.UserSession;
import net.fidoandfido.util.WebUtil;

public class RegisterServlet extends HttpServlet {
	private static final int DEFAULT_TRADER_START_CASH = 1000000;
	public static final String TRADER_NAME = "trader_name";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		TraderDAO traderDAO = new TraderDAO();
		UserDAO userDAO = new UserDAO();
		UserSessionDAO userSessionDAO = new UserSessionDAO();
		TraderMessageDAO traderMessageDAO = new TraderMessageDAO();
		HibernateUtil.beginTransaction();
		User user = WebUtil.getCurrentUserBySession(req.getSession().getId());
		if (user != null) {
			String traderName = req.getParameter(TRADER_NAME);
			traderName = traderName.trim();
			if (traderName != null && !traderName.isEmpty()) {
				Trader trader = new Trader(user, traderName, DEFAULT_TRADER_START_CASH);
				user.setTrader(trader);
				traderDAO.saveTrader(trader);
				userDAO.saveUser(user);
				TraderMessage message = new TraderMessage(
						trader,
						new Date(),
						"Welcome To BlackSwan!",
						"Welcome to the black swan trading application. From this screen you can view your portfolio, see the latest news and rumours, and access the company list and item marketplace.");
				traderMessageDAO.saveMessage(message);
			}
			resp.sendRedirect("/myapp/Trader.jsp");
		} else {
			String traderName = req.getParameter(TRADER_NAME);
			traderName = traderName.trim();
			String userName = req.getParameter(USER_NAME);
			traderName = traderName.trim();
			String password = req.getParameter(PASSWORD);

			if (traderName != null && !traderName.isEmpty() && userName != null && !userName.isEmpty() && password != null && !password.isEmpty()) {
				// Lets see if this user exists already...
				User existingUser = userDAO.getUserByUsername(userName);
				if (existingUser != null) {
					resp.sendRedirect("/myapp/Welcome.jsp?error=User%20already%20exists");
				} else {
					user = new User(userName, password, false);
					Trader trader = new Trader(user, traderName, DEFAULT_TRADER_START_CASH);
					user.setTrader(trader);
					userDAO.saveUser(user);
					traderDAO.saveTrader(trader);
					TraderMessage message = new TraderMessage(
							trader,
							new Date(),
							"Welcome To BlackSwan!",
							"Welcome to the black swan trading application. From this screen you can view your portfolio, see the latest news and rumours, and access the company list and item marketplace.");
					traderMessageDAO.saveMessage(message);
					UserSession userSession = new UserSession(user, req.getSession().getId());
					userSessionDAO.saveUserSession(userSession);
					resp.sendRedirect("/myapp/Welcome.jsp");
				}
			}
		}
		HibernateUtil.commitTransaction();
	}
}
