package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.StockExchange;

import org.hibernate.Query;
import org.hibernate.Session;

public class CompanyPeriodReportDAO {

	public void savePeriodReport(CompanyPeriodReport periodReport) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(periodReport);
	}

	public List<CompanyPeriodReport> getPeriodReportListForCompany(Company company) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from CompanyPeriodReport where company = :company order by generation");
		query.setParameter("company", company);
		return query.list();
	}

	public List<CompanyPeriodReport> getPeriodPerpotListByExchange(StockExchange stockExchange) {
		return getPeriodPerpotListByExchange(stockExchange, 0);
	}

	public List<CompanyPeriodReport> getPeriodPerpotListByExchange(StockExchange stockExchange, int generation) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from CompanyPeriodReport where company.stockExchange = :exchange and generation = :generation");
		query.setParameter("exchange", stockExchange);
		query.setParameter("generation", new Long(generation));
		return query.list();
	}

	public List<CompanyPeriodReport> getRecentPeriodReportListByCompany(Company company, int i) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from CompanyPeriodReport where company = :company order by generation desc");
		query.setParameter("company", company);
		query.setMaxResults(i);
		return query.list();
	}

}
