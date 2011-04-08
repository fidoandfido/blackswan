package net.fidoandfido.contexts;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.fidoandfido.engine.MarketMakerRunner;

public class MarketMakerContextListener implements ServletContextListener {

	// Create a market maker thread...
	Thread marketMakerThread;
	MarketMakerRunner marketRunner;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		marketRunner = new MarketMakerRunner();
		marketMakerThread = new Thread(marketRunner);
		marketMakerThread.setName("MARKET_RUNNER_THREAD");
		marketMakerThread.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (marketMakerThread != null) {
			marketRunner.setRunning(false);
			marketMakerThread.notify();
		}

	}
}
