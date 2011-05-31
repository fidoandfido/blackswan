package net.fidoandfido.util;

public class Constants {

	public static final String COMPANY_CODE_PARM = "company_code";
	public static final String COMPANY_NAME_PARM = "company_name";

	public static final long MILLIS_IN_MINUTE = 1000 * 60;
	public static final long MILLIS_IN_HOUR = 1000 * 60 * 60;
	public static final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

	public static final long DEFAULT_PERIOD_LENGTH_IN_MILLIS = MILLIS_IN_DAY * 7;

	// public static final long PERIOD_LENGTH_BUFFER = MILLIS_IN_MINUTE * 1;

	public static enum QuarterPerformanceType {
		EXTRAORDINARY, GREAT, GOOD, AVERAGE, POOR, TERRIBLE, CATASTROPHIC;
	}

}
