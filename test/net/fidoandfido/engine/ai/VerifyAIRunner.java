package net.fidoandfido.engine.ai;

import net.fidoandfido.dao.HibernateUtil;

import org.apache.log4j.Logger;


public class VerifyAIRunner {

	static Logger logger = Logger.getLogger(VerifyAIRunner.class);
	
	public static void main(String argv[]) {
		HibernateUtil.connectToDB();
		AIRunner runner= new AIRunner();
		runner.process();
		
	}
	
	
}
