package net.fidoandfido.engine.quarter;

import java.util.Random;

import net.fidoandfido.util.Constants.QuarterPerformanceType;

public class RandomQuarterGenerator implements QuarterGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.EventGenerator#getNextEventType()
	 */
	Random random = new Random();

	@Override
	public QuarterPerformanceType getNextEventType() {
		QuarterPerformanceType[] events = QuarterPerformanceType.values();
		return events[random.nextInt(events.length)];
	}

	@Override
	public QuarterPerformanceType getNextEventType(QuarterPerformanceType event) {
		QuarterPerformanceType[] events = QuarterPerformanceType.values();
		return events[random.nextInt(events.length)];
	}

}
