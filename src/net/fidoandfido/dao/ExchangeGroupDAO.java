package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.ExchangeGroup;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ExchangeGroupDAO {

	public void saveExchangeGroup(ExchangeGroup exchangeGroup) {
		Session session = HibernateUtil.getSession();
		session.save(exchangeGroup);
	}

	public ExchangeGroup getExchangeGroupByName(String name) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ExchangeGroup.class);
		crit.add(Restrictions.eq("name", name));
		return (ExchangeGroup) crit.uniqueResult();
	}

	public List<ExchangeGroup> getAllExchangeGroups() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ExchangeGroup.class);
		return crit.list();

	}

	public List<String> getExchangeGroupNameList() {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("select name from ExchangeGroup");
		return query.list();
	}

}
