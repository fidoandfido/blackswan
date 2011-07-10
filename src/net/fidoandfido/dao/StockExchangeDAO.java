package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.Trader;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class StockExchangeDAO {

	public void saveStockExchange(StockExchange exchange) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(exchange);
	}

	@SuppressWarnings("unchecked")
	public List<StockExchange> getStockExchangeList() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(StockExchange.class);
		List<StockExchange> results = crit.list();
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<StockExchange> getStockExchangeListForTrader(Trader trader) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(StockExchange.class);
		crit.add(Restrictions.le("requiredLevel", (long) trader.getLevel()));
		List<StockExchange> results = crit.list();
		return results;
	}

	public StockExchange getStockExchangeByName(String exchangeName) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(StockExchange.class);
		crit.add(Restrictions.eq("name", exchangeName));
		StockExchange result = (StockExchange) crit.uniqueResult();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<String> getStockExchangeNameList() {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("select name from StockExchange");
		return query.list();
	}

	public int getTradingCompaniesForExchange(StockExchange exchange) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		crit.add(Restrictions.eq("stockExchange", exchange));
		crit.add(Restrictions.eq("isTrading", Boolean.TRUE));
		crit.setProjection(Projections.rowCount());
		Integer rowCount = (Integer) crit.uniqueResult();
		return rowCount.intValue();
	}

	public StockExchange getStockExchangeById(String id) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(StockExchange.class);
		crit.add(Restrictions.eq("id", id));
		StockExchange result = (StockExchange) crit.uniqueResult();
		return result;
	}
}
