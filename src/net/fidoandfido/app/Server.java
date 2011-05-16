package net.fidoandfido.app;

import net.fidoandfido.dao.HibernateUtil;

public class Server {

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();

		// Do some work - start threads etc.

		HibernateUtil.commitTransaction();
	}
}
