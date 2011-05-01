package net.fidoandfido.app;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.StockExchangeDAO;
import net.fidoandfido.engine.event.PeriodGenerator;
import net.fidoandfido.model.StockExchange;

public class Server {

	public static void main(String argv[]) {
		HibernateUtil.connectToDB();

		// Start out market maker thread
		// new Thread(new MarketMakerRunner()).start();

		// Start period generators
		HibernateUtil.beginTransaction();
		Iterable<StockExchange> exchanges = StockExchangeDAO.getStockExchangeList();
		for (StockExchange exchange : exchanges) {
			new Thread(new PeriodGenerator(exchange.getName())).start();
		}
		HibernateUtil.commitTransaction();

		// Start (currently non-existant) AI threads

	}
}
