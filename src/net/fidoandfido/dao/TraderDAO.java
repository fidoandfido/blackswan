package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Trader;
import net.fidoandfido.model.User;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class TraderDAO {

	public void saveTrader(Trader trader) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(trader);

	}

	public Trader getMarketMaker() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Trader.class);
		crit.add(Restrictions.eq("isMarketMaker", true));
		Trader trader = (Trader) crit.uniqueResult();
		if (trader == null) {
			throw new IllegalStateException("Market Maker trader not found.");
		}
		return trader;
	}

	@SuppressWarnings("unchecked")
	public List<Trader> getTraderList() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Trader.class);
		List<Trader> results = crit.list();
		return results;
	}

	public Trader getTraderByName(String name) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Trader.class);
		crit.add(Restrictions.eq("name", name));
		return (Trader) crit.uniqueResult();
	}

	public Trader getTraderByUser(User user) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Trader.class);
		crit.add(Restrictions.eq("user", user));
		return (Trader) crit.uniqueResult();

	}

	public List<Trader> getAITraderList() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Trader.class);
		crit.add(Restrictions.eq("isAITrader", Boolean.TRUE));
		List<Trader> results = crit.list();
		return results;
	}
}
