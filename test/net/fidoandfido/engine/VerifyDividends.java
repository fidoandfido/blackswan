package net.fidoandfido.engine;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;


import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.PeriodPartInformationDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.PeriodEvent;

public class VerifyDividends {

	public static void main (String argv[]) {
		System.out.println("Initialising application!");
		HibernateUtil.connectToDB();
		System.out.println("Connected to database, attempting to retrieve data...");
	
		HibernateUtil.beginTransaction();
	
		Session session = HibernateUtil.getSession();
		Criteria companyCrit = session.createCriteria(Company.class);
		companyCrit.add(Restrictions.eq("code", "infc"));
		Company infc = (Company) companyCrit.uniqueResult();
		Criteria crit = session.createCriteria(CompanyPeriodReport.class);
		crit.add(Restrictions.le("finalProfit", new Long(0)));
		crit.add(Restrictions.eq("company", infc));
		List<CompanyPeriodReport> reports = crit.list();
		for (CompanyPeriodReport report : reports) {
			long shareCount = infc.getOutstandingShares();
			long dividend = ((report.getFinalProfit() * 100) / shareCount) / 100;
			System.out.println(report.getCompany() + " -- " + report.getFinalProfit() + " -- " + dividend);
		}
		HibernateUtil.commitTransaction();
	}

}
