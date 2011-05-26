package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.Trader;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class ShareParcelDAO {

	Logger logger = Logger.getLogger(this.getClass().getName());

	public void saveShareParcel(ShareParcel shareParcel) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(shareParcel);
	}

	@SuppressWarnings("unchecked")
	public Iterable<ShareParcel> getHoldingsByCompany(Company company) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ShareParcel.class);
		crit.add(Restrictions.eq("company", company));
		crit.addOrder(Order.asc("trader"));
		List<ShareParcel> results = crit.list();
		return results;
	}

	@SuppressWarnings("unchecked")
	public Iterable<ShareParcel> getHoldingsByTrader(Trader trader) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ShareParcel.class);
		crit.add(Restrictions.eq("trader", trader));
		List<ShareParcel> results = crit.list();
		return results;
	}

	public void deleteShareParcel(ShareParcel shareParcel) {
		Session session = HibernateUtil.getSession();
		session.delete(shareParcel);
	}

	public ShareParcel getHoldingsByTraderForCompany(Trader trader, Company company) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ShareParcel.class);
		crit.add(Restrictions.eq("company", company));
		crit.add(Restrictions.eq("trader", trader));

		// return (ShareParcel) crit.uniqueResult();
		// Bad bug fix...
		List<ShareParcel> parcels = crit.list();
		if (parcels.size() > 1) {
			// Handle this somehow...
			logger.warn("Multiple share parcels for Company: " + company.getName() + "(id: " + company.getId() + ") belong to Trader:" + trader.getName()
					+ " (id:" + trader.getId() + ")");
		}

		if (parcels.isEmpty()) {
			return null;
		}
		return parcels.get(0);

	}

}
