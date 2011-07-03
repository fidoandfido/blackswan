package net.fidoandfido.dao;

import java.util.Date;
import java.util.List;

import net.fidoandfido.model.PeriodRumour;
import net.fidoandfido.model.Trader;

import org.hibernate.Query;
import org.hibernate.Session;

public class RumourDAO {

	public void saveRumour(PeriodRumour rumour) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(rumour);
	}

	public List<PeriodRumour> getLatestRumours(int count, Date date) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from PeriodRumour r where r.dateInformationAvailable < :date order by r.dateInformationAvailable desc");
		query.setParameter("date", date);
		query.setFirstResult(0);
		query.setMaxResults(count);
		return query.list();
	}

	public List<PeriodRumour> getLatestRumours(int count, Date date, Trader trader) {
		Session session = HibernateUtil.getSession();
		Query query = session
				.createQuery("from PeriodRumour r where r.dateInformationAvailable < :date and r.company.stockExchange.requiredLevel <= :level order by r.dateInformationAvailable desc");
		query.setParameter("date", date);
		query.setParameter("level", (long) trader.getLevel());
		query.setFirstResult(0);
		query.setMaxResults(count);
		return query.list();
	}
}
