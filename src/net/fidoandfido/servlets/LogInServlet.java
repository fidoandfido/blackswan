package net.fidoandfido.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.UserDAO;
import net.fidoandfido.dao.UserSessionDAO;
import net.fidoandfido.model.User;
import net.fidoandfido.model.UserSession;
import net.fidoandfido.util.WebUtil;

public class LogInServlet extends HttpServlet {

	public static final String PASSWORD = "password";
	public static final String USER_NAME = "user_name";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HibernateUtil.beginTransaction();
		boolean login = doLogin(req);
		HibernateUtil.commitTransaction();
		if (login) {
			resp.sendRedirect("/myapp/Trader.jsp?");
		} else {
			resp.sendRedirect("/myapp/Welcome.jsp");
		}
	}

	private boolean doLogin(HttpServletRequest req) {
		// ensure we aren't actually logged in already
		User user = WebUtil.getCurrentUserBySession(req.getSession().getId());
		if (user != null) {
			return true;
		}

		// Retrieve the user
		String userName = req.getParameter(USER_NAME);
		String password = req.getParameter(PASSWORD);
		user = UserDAO.getUserByUsername(userName);
		if (user == null) {
			// Invalid username
			return false;
		}

		if (!user.validatePassword(password)) {
			// Bad password
			return false;
		}

		// See if there is a userSession for the current connection.
		UserSession userSession = UserSessionDAO.getUserSessionBySessionId(req.getSession().getId());
		if (userSession == null || !userSession.getUser().equals(user)) {
			userSession = new UserSession(user, req.getSession().getId());
		} else {
			userSession.setActive(true);
		}

		HibernateUtil.beginTransaction();
		UserSessionDAO.saveUserSession(userSession);
		HibernateUtil.commitTransaction();

		return true;
	}
}
