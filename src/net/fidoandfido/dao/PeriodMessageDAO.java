package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.PeriodMessage;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class PeriodMessageDAO {

	public void saveMessage(PeriodMessage message) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(message);
	}

	public List getLatestPeriodMessages(StockExchange exchange) {
		StockExchangePeriod currentPeriod = exchange.getCurrentPeriod();
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(PeriodMessage.class);
		crit.add(Restrictions.eq("exchangePeriod", currentPeriod));
		return crit.list();
	}

}
