package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class TraderEventDAO {

	public void saveTraderEvent(TraderEvent event) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(event);
	}

	@SuppressWarnings("unchecked")
	public List<TraderEvent> getTraderEventList(Trader trader) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(TraderEvent.class);
		crit.add(Restrictions.eq("trader", trader));
		List<TraderEvent> results = crit.list();
		return results;
	}
}
