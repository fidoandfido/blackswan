package net.fidoandfido.dao;

import net.fidoandfido.model.UserSession;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class UserSessionDAO {

	public void saveUserSession(UserSession userSession) {
		HibernateUtil.getSession().saveOrUpdate(userSession);
	}

	public UserSession getUserSessionBySessionId(String id) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(UserSession.class);
		crit.add(Restrictions.eq("sessionId", id));
		crit.add(Restrictions.eq("active", Boolean.TRUE));
		return (UserSession) crit.uniqueResult();
	}
}
