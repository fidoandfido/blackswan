package net.fidoandfido.dao;

import java.util.Date;
import java.util.List;

import net.fidoandfido.model.PeriodQuarter;

public class VerifyPeriodPartInformationDAO {

	public static void main(String argv[]) {

		System.out.println("Initialising application!");
		HibernateUtil.connectToDB();
		System.out.println("Connected to database, attempting to retrieve data...");
		PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();
		HibernateUtil.beginTransaction();
		List<PeriodQuarter> recentEvents = periodPartInformationDAO.getLatestEvents(20, new Date());
		for (PeriodQuarter event : recentEvents) {
			System.out.println(event);
		}

		HibernateUtil.commitTransaction();
	}

}
