package net.fidoandfido.dao;

import java.util.Date;
import java.util.List;

import net.fidoandfido.model.PeriodEvent;

import org.hibernate.Query;
import org.hibernate.Session;

public class PeriodPartInformationDAO {

	public static void savePeriodPartInformation(PeriodEvent info) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(info);
	}

	@SuppressWarnings("unchecked")
	public static List<PeriodEvent> getLatestEvents(int i, Date date) {

		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from PeriodEvent p where p.dateInformationAvailable < :date order by p.dateInformationAvailable desc");
		query.setParameter("date", date);
		query.setMaxResults(i);

		return query.list();
	}

}
