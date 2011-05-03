package net.fidoandfido.dao;

import net.fidoandfido.model.StockExchangePeriod;

import org.hibernate.Session;

public class StockExchangePeriodDAO {

	public void save(StockExchangePeriod currentPeriod) {
		Session session = HibernateUtil.getSession();
		session.save(currentPeriod);
	}

}
