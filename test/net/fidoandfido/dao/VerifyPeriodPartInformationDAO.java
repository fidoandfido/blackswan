package net.fidoandfido.dao;

import java.util.Date;
import java.util.List;

import net.fidoandfido.app.AppDataLister;
import net.fidoandfido.app.AppInitialiser;
import net.fidoandfido.model.PeriodEvent;

public class VerifyPeriodPartInformationDAO {

	public static void main(String argv[]) {
		
		System.out.println("Initialising application!");
		HibernateUtil.connectToDB();
		System.out.println("Connected to database, attempting to retrieve data...");

		HibernateUtil.beginTransaction();
		List<PeriodEvent> recentEvents = PeriodPartInformationDAO.getLatestEvents(20, new Date());
		for (PeriodEvent event : recentEvents) {
			System.out.println(event);
		}

		HibernateUtil.commitTransaction();
	}
	
}
