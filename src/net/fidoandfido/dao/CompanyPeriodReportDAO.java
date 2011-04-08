package net.fidoandfido.dao;

import net.fidoandfido.model.CompanyPeriodReport;

import org.hibernate.Session;

public class CompanyPeriodReportDAO {

	public static void savePeriodReport(CompanyPeriodReport periodReport) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(periodReport);
	}

}
