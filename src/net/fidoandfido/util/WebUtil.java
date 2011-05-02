package net.fidoandfido.util;

import net.fidoandfido.dao.UserSessionDAO;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.User;
import net.fidoandfido.model.UserSession;

public class WebUtil {

	public static User getCurrentUserBySession(String sessionId) {
		UserSessionDAO userSessionDAO = new UserSessionDAO();
		UserSession userSession = userSessionDAO.getUserSessionBySessionId(sessionId);
		if (userSession != null && userSession.isActive()) {
			return userSession.getUser();
		}
		return null;
	}

	public static Trader getCurrentTraderBySession(String sessionId) {
		User user = getCurrentUserBySession(sessionId);
		if (user != null) {
			return user.getTrader();
		}
		return null;
	}
}
