package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.ReputationItem;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class ReputationItemDAO {

	public void saveItem(ReputationItem item) {
		Session session = HibernateUtil.getSession();
		session.save(item);
	}

	@SuppressWarnings("unchecked")
	public List<ReputationItem> getItems() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ReputationItem.class);
		crit.addOrder(Order.asc("cost"));
		return crit.list();
	}

	public ReputationItem getItem(String name, long cost) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ReputationItem.class);
		crit.add(Restrictions.eq("name", name));
		crit.add(Restrictions.eq("cost", new Long(cost)));
		return (ReputationItem) crit.uniqueResult();
	}
}
