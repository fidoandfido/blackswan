package net.fidoandfido.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.UserSessionDAO;
import net.fidoandfido.model.User;
import net.fidoandfido.model.UserSession;
import net.fidoandfido.util.WebUtil;

public class LogoutServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HibernateUtil.beginTransaction();
		User user = WebUtil.getCurrentUserBySession(req.getSession().getId());
		if (user != null) {
			UserSession userSession = UserSessionDAO.getUserSessionBySessionId(req.getSession().getId());
			userSession.setActive(false);
			UserSessionDAO.saveUserSession(userSession);
		}
		HibernateUtil.commitTransaction();
		resp.sendRedirect("/myapp/Welcome.jsp");
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doLogout(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doLogout(req, resp);
	}

}
