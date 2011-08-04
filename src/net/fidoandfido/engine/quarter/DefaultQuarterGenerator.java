package net.fidoandfido.engine.quarter;

import net.fidoandfido.util.Constants.QuarterPerformanceType;

public class DefaultQuarterGenerator implements QuarterGenerator {

	@Override
	public QuarterPerformanceType getNextEventType() {
		return QuarterPerformanceType.AVERAGE;
	}

}
