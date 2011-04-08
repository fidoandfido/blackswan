package net.fidoandfido.contexts;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.fidoandfido.dao.HibernateUtil;

public class HibernateListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		HibernateUtil.connectToDB();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		HibernateUtil.disconnectFromDB();
	}

}
