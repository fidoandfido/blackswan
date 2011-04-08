package net.fidoandfido.contexts;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.fidoandfido.engine.ai.AIRunner;

public class AIContextListener implements ServletContextListener {

	Thread aiThread;
	AIRunner aiRunner;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		aiRunner = new AIRunner();
		aiThread = new Thread(aiRunner);
		aiThread.setName("MARKET_RUNNER_THREAD");
		aiThread.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (aiThread != null) {
			aiRunner.setRunning(false);
			aiThread.notify();
		}

	}

}
