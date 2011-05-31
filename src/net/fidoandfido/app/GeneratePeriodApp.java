package net.fidoandfido.app;

import java.util.List;

import net.fidoandfido.dao.ExchangeGroupDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.engine.PeriodGenerator;

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
		ExchangeGroupDAO stockExchangeDAO = new ExchangeGroupDAO();
		List<String> groupNames = stockExchangeDAO.getExchangeGroupNameList();
		HibernateUtil.commitTransaction();

		for (String groupName : groupNames) {
			PeriodGenerator periodGenerator = new PeriodGenerator(groupName);

			HibernateUtil.beginTransaction();
			periodGenerator.generatePeriod();
			HibernateUtil.commitTransaction();
		}
	}
}
