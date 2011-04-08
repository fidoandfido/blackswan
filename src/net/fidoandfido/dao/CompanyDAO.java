package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.StockExchange;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class CompanyDAO {

	public static void saveCompany(Company company) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(company);
	}

	@SuppressWarnings("unchecked")
	public static List<Company> getCompanyList() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		List<Company> results = crit.list();
		return results;
	}

	@SuppressWarnings("unchecked")
	public static Iterable<Company> getCompaniesByExchange(StockExchange exchange) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		crit.add(Restrictions.eq("stockExchange", exchange));
		List<Company> results = crit.list();
		return results;
	}

	public static Company getCompanyByCode(String string) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		crit.add(Restrictions.eq("code", string));
		return (Company) crit.uniqueResult();
	}

}
