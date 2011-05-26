package net.fidoandfido.app;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.initialiser.AppInitialiser;

public class InitialiserApp {

	public static void main(String argv[]) {
		System.out.println("Initialising application!");
		HibernateUtil.connectToDB();
		System.out.println("Connected to database, beginning initislisation.");
		AppInitialiser appInitialiser = new AppInitialiser();
		HibernateUtil.beginTransaction();
		try {
			appInitialiser.initApp();
			System.out.println("Initialisation complete, committing transaction.");
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			e.printStackTrace();
		}
		HibernateUtil.beginTransaction();
		AppDataLister appDataLister = new AppDataLister();
		appDataLister.writeData();
		HibernateUtil.commitTransaction();
	}

}
