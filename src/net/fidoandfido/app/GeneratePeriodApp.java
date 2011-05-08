package net.fidoandfido.app;

import java.util.List;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.engine.event.PeriodGenerator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class GeneratePeriodApp {

	static {
		Logger logger = Logger.getRootLogger();
		logger.setLevel(Level.INFO);
	}

	public static void main(String argv[]) {

		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		StockExchangeDAO stockExchangeDAO = new StockExchangeDAO();
		List<String> exchangeNames = stockExchangeDAO.getStockExchangeNameList();
		HibernateUtil.commitTransaction();

		for (String exchangeName : exchangeNames) {
			PeriodGenerator periodGenerator = new PeriodGenerator(exchangeName);

			HibernateUtil.beginTransaction();
			periodGenerator.generatePeriod();
			HibernateUtil.commitTransaction();
		}
	}

}
