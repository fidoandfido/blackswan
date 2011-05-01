package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.StockExchange;

import org.hibernate.Query;
import org.hibernate.Session;

public class CompanyPeriodReportDAO {

	public static void savePeriodReport(CompanyPeriodReport periodReport) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(periodReport);
	}

	public static List<CompanyPeriodReport> getPeriodPerpotListForCompany(Company company) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from CompanyPeriodReport where company = :company order by generation");
		query.setParameter("company", company);
		return query.list();

	}

	public static List<CompanyPeriodReport> getPeriodPerpotListByExchange(StockExchange stockExchange) {
		return getPeriodPerpotListByExchange(stockExchange, 0);
	}

	public static List<CompanyPeriodReport> getPeriodPerpotListByExchange(StockExchange stockExchange, int generation) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from CompanyPeriodReport where company.stockExchange = :exchange and generation = :generation");
		query.setParameter("exchange", stockExchange);
		query.setParameter("generation", new Long(generation));
		return query.list();
	}

}
