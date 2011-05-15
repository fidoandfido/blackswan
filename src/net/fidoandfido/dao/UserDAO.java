package net.fidoandfido.dao;

import net.fidoandfido.model.User;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class UserDAO {

	public void saveUser(User user) {
		HibernateUtil.getSession().saveOrUpdate(user);
	}

	public User getUserByUsername(String userName) {
		Session session = HibernateUtil.getSession();
		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("userName", userName));
		return (User) crit.uniqueResult();
	}

}
