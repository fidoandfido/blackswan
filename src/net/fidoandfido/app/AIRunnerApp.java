package net.fidoandfido.app;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.engine.ai.AIRunner;

public class AIRunnerApp {

	public static void main(String[] args) {
		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		AIRunner runner = new AIRunner();
		runner.process();
		HibernateUtil.commitTransaction();
	}
}
