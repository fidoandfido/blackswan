package net.fidoandfido.util;

import org.apache.log4j.Logger;

public class ServerUtil {

	public static void logError(Logger logger, Exception e) {
		logger.error("Exception thrown! Type: " + e.getClass());
		logger.error("Exception stack: ");
		StackTraceElement stack[] = e.getStackTrace();
		for (int i = 0; i < 10 && i < stack.length; i++) {
			StackTraceElement element = stack[i];
			if (!element.isNativeMethod()) {
				logger.error(element.getFileName() + " --> " + element.getClassName() + " --> " + element.getMethodName() + " --> " + element.getLineNumber());
			}
		}
	}

}
