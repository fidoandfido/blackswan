package net.fidoandfido.engine.quarter;

import java.util.Random;

import net.fidoandfido.util.Constants.QuarterPerformanceType;

public class AverageQuarterGenerator implements QuarterGenerator {

	static Random random = new Random(17);

	@Override
	public QuarterPerformanceType getNextEventType() {

		int eventIndex = random.nextInt(100);
		// 0 - 2 Catastrophic ( 3 %)
		// 3 - 14 Terrible ( 12 %)
		// 15 - 34 Poor (20 %)
		// 35 - 64 Average (30 %)
		// 65 - 84 Good (20 %)
		// 85 - 96 Great ( 12 %)
		// 97 - 99 Extraordinary (3 %)

		// 70 % chance of poor - good - average
		// 96 % chance of terrible - great

		if (eventIndex <= 2) {
			return QuarterPerformanceType.CATASTROPHIC;
		}
		if (eventIndex <= 14) {
			return QuarterPerformanceType.TERRIBLE;
		}
		if (eventIndex <= 34) {
			return QuarterPerformanceType.POOR;
		}
		if (eventIndex <= 64) {
			return QuarterPerformanceType.AVERAGE;
		}
		if (eventIndex <= 84) {
			return QuarterPerformanceType.GOOD;
		}
		if (eventIndex <= 96) {
			return QuarterPerformanceType.GREAT;
		}
		if (eventIndex <= 99) {
			return QuarterPerformanceType.EXTRAORDINARY;
		}

		// WTF???
		return QuarterPerformanceType.AVERAGE;

	}
}
