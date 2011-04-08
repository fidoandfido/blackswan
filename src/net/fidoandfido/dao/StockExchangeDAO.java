package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.StockExchange;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class StockExchangeDAO {

	public static void saveStockExchange(StockExchange exchange) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(exchange);
	}

	@SuppressWarnings("unchecked")
	public static List<StockExchange> getStockExchangeList() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(StockExchange.class);
		List<StockExchange> results = crit.list();
		return results;
	}

	public static StockExchange getStockExchangeByName(String exchangeName) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(StockExchange.class);
		crit.add(Restrictions.eq("name", exchangeName));
		StockExchange result = (StockExchange) crit.uniqueResult();
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getStockExchangeNameList() {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("select name from StockExchange");
		return query.list();
	}
}
