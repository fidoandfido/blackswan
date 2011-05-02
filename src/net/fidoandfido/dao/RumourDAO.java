package net.fidoandfido.dao;

import java.util.Date;
import java.util.List;

import net.fidoandfido.model.PeriodRumour;

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
}
