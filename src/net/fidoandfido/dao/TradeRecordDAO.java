package net.fidoandfido.dao;

import net.fidoandfido.model.TradeRecord;

import org.hibernate.Session;

public class TradeRecordDAO {

	public static void saveTradeRecord(TradeRecord tr) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(tr);
	}
}
