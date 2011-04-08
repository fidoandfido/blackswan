package net.fidoandfido.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.UserDAO;
import net.fidoandfido.dao.UserSessionDAO;
import net.fidoandfido.model.Trader;
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
		HibernateUtil.beginTransaction();
		User user = WebUtil.getCurrentUserBySession(req.getSession().getId());
		if (user != null) {
			String traderName = req.getParameter(TRADER_NAME);
			traderName = traderName.trim();
			if (traderName != null && !traderName.isEmpty()) {
				Trader trader = new Trader(user, traderName, DEFAULT_TRADER_START_CASH);
				user.setTrader(trader);
				TraderDAO.saveTrader(trader);
				UserDAO.saveUser(user);
			}
			resp.sendRedirect("/myapp/Trader.jsp");
		} else {
			String traderName = req.getParameter(TRADER_NAME);
			traderName = traderName.trim();
			String userName = req.getParameter(USER_NAME);
			traderName = traderName.trim();
			String password = req.getParameter(PASSWORD);

			if (traderName != null && !traderName.isEmpty() && userName != null && !userName.isEmpty() && password != null && !password.isEmpty()) {
				user = new User(userName, password, false);
				Trader trader = new Trader(user, traderName, DEFAULT_TRADER_START_CASH);
				user.setTrader(trader);
				UserDAO.saveUser(user);
				TraderDAO.saveTrader(trader);
				UserSession userSession = new UserSession(user, req.getSession().getId());
				UserSessionDAO.saveUserSession(userSession);
			}
			resp.sendRedirect("/myapp/Welcome.jsp");
		}
		HibernateUtil.commitTransaction();
	}
}
