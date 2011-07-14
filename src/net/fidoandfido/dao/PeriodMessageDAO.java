package net.fidoandfido.dao;

import net.fidoandfido.model.PeriodMessage;

import org.hibernate.Session;

public class PeriodMessageDAO {

	public void saveMessage(PeriodMessage message) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(message);
	}

	// ????
}
