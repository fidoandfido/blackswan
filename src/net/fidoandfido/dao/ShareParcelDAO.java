package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.Trader;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ShareParcelDAO {

	public static void saveShareParcel(ShareParcel shareParcel) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(shareParcel);
	}

	@SuppressWarnings("unchecked")
	public static Iterable<ShareParcel> getHoldingsByCompany(Company company) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ShareParcel.class);
		crit.add(Restrictions.eq("company", company));
		List<ShareParcel> results = crit.list();
		return results;
	}

	@SuppressWarnings("unchecked")
	public static Iterable<ShareParcel> getHoldingsByTrader(Trader trader) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ShareParcel.class);
		crit.add(Restrictions.eq("trader", trader));
		List<ShareParcel> results = crit.list();
		return results;
	}

	public static void deleteShareParcel(ShareParcel shareParcel) {
		Session session = HibernateUtil.getSession();
		session.delete(shareParcel);
	}

	public static ShareParcel getHoldingsByTraderForCompany(Trader trader, Company company) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(ShareParcel.class);
		crit.add(Restrictions.eq("company", company));
		crit.add(Restrictions.eq("trader", trader));
		return (ShareParcel) crit.uniqueResult();
	}

}
