package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.AppStatus;

import org.hibernate.Session;

public class AppStatusDAO {
	public static AppStatus getStatus() {
		AppStatus status;
		Session session = HibernateUtil.getSession();
		List<?> statusList = session.createCriteria(AppStatus.class).list();
		if (statusList.size() == 0) {
			status = new AppStatus(AppStatus.UNINITIALISED);
			session.save(status);
		} else if (statusList.size() > 1) {
			// This is bad...
			throw new IllegalStateException();
		} else {
			status = (AppStatus) statusList.get(0);
		}
		return status;
	}

	public static void saveStatus(AppStatus appStatus) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(appStatus);
	}

}
