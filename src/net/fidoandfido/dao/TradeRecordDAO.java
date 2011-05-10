package net.fidoandfido.dao;

import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.TradeRecord;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class TradeRecordDAO {

	public void saveTradeRecord(TradeRecord tr) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(tr);
	}

	public List<TradeRecord> getLastTradeRecords(Company company, int count) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(TradeRecord.class);
		crit.add(Restrictions.eq("company", company));
		crit.addOrder(Order.desc("date"));
		crit.setMaxResults(count);
		return crit.list();
	}
}
