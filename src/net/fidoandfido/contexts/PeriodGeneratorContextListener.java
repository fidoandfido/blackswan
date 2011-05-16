package net.fidoandfido.contexts;

import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.fidoandfido.dao.ExchangeGroupDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.engine.event.PeriodGenerator;

public class PeriodGeneratorContextListener implements ServletContextListener {
	// Create a market maker thread...
	List<PeriodGenerator> periodGeneratorList = new Vector<PeriodGenerator>();

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		HibernateUtil.beginTransaction();
		ExchangeGroupDAO stockExchangeDAO = new ExchangeGroupDAO();
		List<String> groupNames = stockExchangeDAO.getExchangeGroupNameList();
		HibernateUtil.commitTransaction();
		for (String groupName : groupNames) {
			PeriodGenerator periodGenerator = new PeriodGenerator(groupName);
			Thread periodGeneratorThread = new Thread(periodGenerator);
			periodGeneratorThread.setName("PERIOD_GENERATOR_THREAD_" + groupName);
			periodGeneratorThread.start();
			periodGeneratorList.add(periodGenerator);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		for (PeriodGenerator generator : periodGeneratorList) {
			generator.setRunning(false);
			generator.notify();
		}
	}
}
