package net.fidoandfido.dao;

import java.util.Date;
import java.util.List;

import net.fidoandfido.model.PeriodQuarter;

import org.hibernate.Query;
import org.hibernate.Session;

public class PeriodPartInformationDAO {

	public void savePeriodPartInformation(PeriodQuarter info) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(info);
	}

	@SuppressWarnings("unchecked")
	public List<PeriodQuarter> getLatestEvents(int i, Date date) {

		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from PeriodQuarter p where p.dateInformationAvailable < :date order by p.dateInformationAvailable desc");
		query.setParameter("date", date);
		query.setFirstResult(0);
		query.setMaxResults(i);

		return query.list();
	}

}
