package net.fidoandfido.app;

import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.ExchangeGroupDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.engine.PeriodGenerator;
import net.fidoandfido.engine.ai.AIRunner;
import net.fidoandfido.initialiser.AppInitialiser;
import net.fidoandfido.model.ExchangeGroup;

public class InitialiserApp {

	public static void main(String argv[]) {
		System.out.println("Initialising application!");
		HibernateUtil.connectToDB();
		System.out.println("Connected to database, beginning initialisation.");
		AppInitialiser appInitialiser = new AppInitialiser();

		// //////////////////////////////////
		// / THIS IS IMPORTANT!!!!
		// //////////////////////////////////
		boolean doHistoricalTrades = true;

		Date currentDate = new Date();
		// Put it back 65 minutes (60 * 1000 milliseconds)
		long offset = 65 * (60 * 1000);

		Date historicalStartDate = new Date(currentDate.getTime() - offset);

		HibernateUtil.beginTransaction();
		try {
			appInitialiser.initApp(historicalStartDate);
			System.out.println("Initialisation complete, committing transaction.");
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			e.printStackTrace();
		}

		ExchangeGroupDAO exchangeGroupDAO = new ExchangeGroupDAO();

		HibernateUtil.beginTransaction();
		List<ExchangeGroup> allExchangeGroups = exchangeGroupDAO.getAllExchangeGroups();
		HibernateUtil.commitTransaction();
		AIRunner aiRunner = new AIRunner();
		// Generate more periods, as required.
		for (ExchangeGroup exchangeGroup : allExchangeGroups) {
			PeriodGenerator periodGenerator = new PeriodGenerator(exchangeGroup.getName());
			Date nextPeriodStartDate = new Date(historicalStartDate.getTime() + exchangeGroup.getPeriodLength());

			// First up, perform historical AI trades, up until the next period start date.
			Date aiTradeDate = historicalStartDate;
			HibernateUtil.beginTransaction();
			if (doHistoricalTrades) {
				while (aiTradeDate.before(nextPeriodStartDate) && aiTradeDate.before(currentDate)) {
					// Trade every minute.
					aiTradeDate = new Date(aiTradeDate.getTime() + 60000);
					aiRunner.process(aiTradeDate);
				}
			}
			HibernateUtil.commitTransaction();

			// Now continue to generate historical periods and make historical trades until
			// the next period start date is after now.
			while (nextPeriodStartDate.before(currentDate)) {
				HibernateUtil.beginTransaction();
				System.err.println("CREATING NEW PERIOD!");
				periodGenerator.generatePeriod(nextPeriodStartDate);
				Date endDate = new Date(nextPeriodStartDate.getTime() + exchangeGroup.getPeriodLength());

				if (doHistoricalTrades) {
					// Perform some historical AI trades, up until end of this period.
					while (aiTradeDate.before(endDate) && aiTradeDate.before(currentDate)) {
						// Trade every minute.
						aiTradeDate = new Date(aiTradeDate.getTime() + 60000);
						aiRunner.process(aiTradeDate);
					}
				}
				HibernateUtil.commitTransaction();
				nextPeriodStartDate = endDate;
			}
		}

		// HibernateUtil.beginTransaction();
		//
		// AppDataLister appDataLister = new AppDataLister();
		// appDataLister.writeData();
		// HibernateUtil.commitTransaction();
	}
}
