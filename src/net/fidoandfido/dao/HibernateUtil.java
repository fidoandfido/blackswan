package net.fidoandfido.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory;

	private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();

	private static final ThreadLocal<Transaction> threadTransaction = new ThreadLocal<Transaction>();

	private static boolean connected = false;

	public static synchronized void connectToDB() {
		if (!connected) {
			try {
				AnnotationConfiguration cfg = new AnnotationConfiguration();
				sessionFactory = cfg.configure().buildSessionFactory();
				connected = true;
			} catch (Throwable ex) {
				throw new ExceptionInInitializerError(ex);
			}
		}
	}

	// public static void connectToDB(Map<String, String> connectionProperties) throws ConnectionException {
	// String driverName = connectionProperties.get(HibernateDataAccessManager.DATABASE_DRIVER_NAME);
	//
	// String connectionString = connectionProperties.get(HibernateDataAccessManager.DATABASE_CONNECTION_STRING);
	// connectionString = substitute(connectionString, "${host}",
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_HOSTNAME));
	// connectionString = substitute(connectionString, "${database}",
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_NAME));
	// connectionString = substitute(connectionString, "${port}",
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_PORT));
	//
	// try {
	// Class.forName(driverName);
	// } catch (ClassNotFoundException e) {
	// throw new Exception("Could not get driver for database", e);
	// }
	//
	// try {
	// DriverManager.getConnection(connectionString,
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_USERNAME),
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_PASSWORD));
	//
	// // Create the SessionFactory
	// AnnotationConfiguration configuration = new AnnotationConfiguration();
	// configuration.configure(connectionProperties.get(HibernateDataAccessManager.HIBERNATE_CONFIGURATION_FILE));
	//
	//			configuration.setProperty("hibernate.connection.username", connectionProperties.get(HibernateDataAccessManager.DATABASE_USERNAME)); //$NON-NLS-1$
	//			configuration.setProperty("hibernate.connection.password", connectionProperties.get(HibernateDataAccessManager.DATABASE_PASSWORD)); //$NON-NLS-1$
	//			configuration.setProperty("hibernate.connection.url", connectionString); //$NON-NLS-1$ 
	// configuration.setProperty("hibernate.connection.driver",
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_DRIVER_NAME));
	// configuration.setProperty("hibernate.connection.driver_class",
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_DRIVER_NAME));
	// configuration.setProperty("hibernate.dialect",
	// connectionProperties.get(HibernateDataAccessManager.DATABASE_DIALECT));
	//
	// configuration.setProperty("hibernate.c3p0.min_size", "5");
	// configuration.setProperty("hibernate.c3p0.max_size", "20");
	// configuration.setProperty("hibernate.c3p0.timeout", "300");
	// configuration.setProperty("hibernate.c3p0.max_statements", "50");
	// configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");
	// configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
	// configuration.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
	//
	// sessionFactory = configuration.buildSessionFactory();
	//
	// } catch (Throwable ex) {
	// throw new ConnectionException(ex);
	// }
	// }

	/**
	 * This method is called to completely close a connection with the database. It is used in testing to allow a single
	 * test case object or even a single unit test method to close connections in preparation for clearing the
	 * databases.
	 * 
	 * Any pending transactions will be rolled back.
	 */
	public static void disconnectFromDB() {
		rollbackTransaction();
		closeSession();
		sessionFactory.close();
		sessionFactory = null;
	}

	/**
	 * Return the thread for the current session. If no thread is open, it will return null. Sessions can only be opened
	 * by beginning a transaction
	 */
	public static Session getSession() throws HibernateException {
		return threadSession.get();
	}

	/**
	 * Return the session for the current thread.
	 * 
	 * If no session exists then create a new session for this thread, and add it to the threadSession field.
	 * 
	 * @return
	 */
	private static Session openSession() throws HibernateException {
		Session s = threadSession.get();
		if (s == null) {
			if (sessionFactory != null) {
				s = sessionFactory.openSession();
				threadSession.set(s);
			}
		}
		return s;
	}

	/**
	 * This method is a convenience method to ensure that the session for the current thread is closed. This should be
	 * called after a thread has completed any persistence work.
	 */
	private static void closeSession() {
		try {
			Session s = threadSession.get();
			if (s != null && s.isOpen()) {
				s.close();
			}
			threadSession.set(null);
		} catch (HibernateException e) {
			e.printStackTrace();
		}
	}

	public static void beginTransaction() {
		Transaction tx = threadTransaction.get();
		try {
			if (tx == null) {
				tx = openSession().beginTransaction();
				threadTransaction.set(tx);
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commit the current transaction. Note, this will close the current session. This should be called after each unit
	 * of work (including retreival of information!)
	 */
	public static void commitTransaction() {
		Transaction tx = threadTransaction.get();
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.commit();
				threadTransaction.set(null);
			}
		} catch (HibernateException e) {
			rollbackTransaction();
		}
		// For good measure, we will close the session here.
		// A new session will be created as needed, but this will handle query resource management - no more dangling
		// queries!
		closeSession();
	}

	public static void rollbackTransaction() {
		Transaction tx = threadTransaction.get();
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.rollback();
				threadTransaction.set(null);
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new IllegalStateException("Exception attempting to rollback a transaction in HibernateUtil class!", e);
		} finally {
			closeSession();
		}
	}
}
