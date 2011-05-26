package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderMessage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class TraderMessageDAO {

	public void saveMessage(TraderMessage message) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(message);
	}

	@SuppressWarnings("unchecked")
	public List<TraderMessage> getCurrentMessages(Trader trader) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(TraderMessage.class);
		crit.add(Restrictions.eq("forTrader", trader));
		crit.add(Restrictions.eq("current", Boolean.TRUE));
		crit.addOrder(org.hibernate.criterion.Order.desc("date"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<TraderMessage> getUnreadMessages(Trader trader) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(TraderMessage.class);
		crit.add(Restrictions.eq("forTrader", trader));
		crit.add(Restrictions.eq("isRead", Boolean.FALSE));
		crit.addOrder(org.hibernate.criterion.Order.desc("date"));
		return crit.list();
	}

	public TraderMessage getMessageById(String id) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(TraderMessage.class);
		crit.add(Restrictions.eq("id", id));
		return (TraderMessage) crit.uniqueResult();
	}

}
