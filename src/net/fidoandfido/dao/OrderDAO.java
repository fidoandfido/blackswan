package net.fidoandfido.dao;

import java.util.Collection;
import java.util.List;

import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Order.OrderType;
import net.fidoandfido.model.Trader;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class OrderDAO {

	public static void saveOrder(Order order) {
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(order);
	}

	@SuppressWarnings("unchecked")
	public static Collection<Order> getOpenOrders(OrderType type, Company company) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Order.class);
		crit.add(Restrictions.eq("company", company));
		crit.add(Restrictions.eq("active", Boolean.TRUE));
		crit.add(Restrictions.eq("orderType", type));
		crit.addOrder(org.hibernate.criterion.Order.desc("offerPrice"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public static List<Order> getAllOrders() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Order.class);
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public static List<Order> getOpenOrders() {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Order.class);
		crit.add(Restrictions.eq("active", Boolean.TRUE));
		return crit.list();
	}

	public static List<Order> getOpenOrdersByTrader(Trader trader) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Order.class);
		crit.add(Restrictions.eq("active", Boolean.TRUE));
		crit.add(Restrictions.eq("trader", trader));
		return crit.list();

	}

	public static List<Order> getOpenOrdersByTrader(Trader trader, Company company) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Order.class);
		crit.add(Restrictions.eq("active", Boolean.TRUE));
		crit.add(Restrictions.eq("trader", trader));
		crit.add(Restrictions.eq("company", company));
		return crit.list();
	}

	public static Order getOrderById(String id) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(Order.class);
		crit.add(Restrictions.eq("id", id));
		return (Order) crit.uniqueResult();
	}

}
