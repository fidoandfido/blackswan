package net.fidoandfido.contexts;

import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.engine.event.PeriodGenerator;

public class PeriodGeneratorContextListener implements ServletContextListener {
	// Create a market maker thread...
	List<PeriodGenerator> periodGeneratorList = new Vector<PeriodGenerator>();

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		HibernateUtil.beginTransaction();
		List<String> exchangeNames = StockExchangeDAO.getStockExchangeNameList();
		HibernateUtil.commitTransaction();
		for (String exchangeName : exchangeNames) {
			PeriodGenerator periodGenerator = new PeriodGenerator(exchangeName);
			Thread periodGeneratorThread = new Thread(periodGenerator);
			periodGeneratorThread.setName("PERIOD_GENERATOR_THREAD_" + exchangeName);
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
