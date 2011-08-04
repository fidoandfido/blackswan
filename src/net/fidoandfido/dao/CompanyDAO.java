package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.StockExchange;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class CompanyDAO {

	public void saveCompany(Company company) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(company);
	}

	@SuppressWarnings("unchecked")
	public List<Company> getCompanyList() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		List<Company> results = crit.list();
		return results;
	}

	@SuppressWarnings("unchecked")
	public Iterable<Company> getCompaniesByExchange(StockExchange exchange) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		crit.add(Restrictions.eq("stockExchange", exchange));
		crit.addOrder(Order.asc("name"));
		List<Company> results = crit.list();
		return results;
	}

	public Company getCompanyByCode(String string) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		crit.add(Restrictions.eq("code", string));
		return (Company) crit.uniqueResult();
	}

	/**
	 * Retrieve company by the hibernate generated company ID (NOT THE CODE!)
	 * 
	 * @param id
	 * @return
	 */
	public Company getCompanyById(String id) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Company.class);
		crit.add(Restrictions.eq("id", id));
		return (Company) crit.uniqueResult();
	}

	public List<String> getAllCompanyNames() {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("select company.name from Company company");
		return query.list();
	}

	public List<String> getAllCompanyCodes() {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("select company.code from Company company");
		return query.list();
	}

}
