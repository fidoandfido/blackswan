package net.fidoandfido.engine.quarter;

import java.util.Random;

import net.fidoandfido.util.Constants.QuarterPerformanceType;

public class AverageQuarterGenerator implements QuarterGenerator {

	static Random random = new Random(17);

	private QuarterPerformanceType previousType = QuarterPerformanceType.AVERAGE;

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

		// 0 Catastrophic ( 1 %)
		// 1 - 5 Terrible ( 5 %)
		// 6 - 30 Poor (25 %)
		// 31 - 68 Average (38 %)
		// 69 - 93 Good (25 %)
		// 93 - 98 Great ( 5 %)
		// 99 Extraordinary (1 %)

		// 88 % chance of poor - good - average
		// 98 % chance of terrible - great
		// Modify -> ensure there is some 'stickiness'

		QuarterPerformanceType currentPerformanceType = QuarterPerformanceType.AVERAGE;

		if (eventIndex <= 0) {
			currentPerformanceType = QuarterPerformanceType.CATASTROPHIC;
		} else if (eventIndex <= 5) {
			currentPerformanceType = QuarterPerformanceType.TERRIBLE;
		} else if (eventIndex <= 30) {
			currentPerformanceType = QuarterPerformanceType.POOR;
		} else if (eventIndex <= 68) {
			currentPerformanceType = QuarterPerformanceType.AVERAGE;
		} else if (eventIndex <= 93) {
			currentPerformanceType = QuarterPerformanceType.GOOD;
		} else if (eventIndex <= 98) {
			currentPerformanceType = QuarterPerformanceType.GREAT;
		} else if (eventIndex <= 99) {
			currentPerformanceType = QuarterPerformanceType.EXTRAORDINARY;
		}

		// Now normalise it compared to the previous one.
		switch (previousType) {
		case EXTRAORDINARY:
			// at worse we can be good.
			if (currentPerformanceType == QuarterPerformanceType.CATASTROPHIC || currentPerformanceType == QuarterPerformanceType.TERRIBLE
					|| currentPerformanceType == QuarterPerformanceType.POOR || currentPerformanceType == QuarterPerformanceType.AVERAGE) {
				currentPerformanceType = QuarterPerformanceType.GOOD;
			}
			break;
		case GREAT:
			// At worse we can be average.
			if (currentPerformanceType == QuarterPerformanceType.CATASTROPHIC || currentPerformanceType == QuarterPerformanceType.TERRIBLE
					|| currentPerformanceType == QuarterPerformanceType.POOR) {
				currentPerformanceType = QuarterPerformanceType.AVERAGE;
			}
			break;
		case GOOD:
			// At worse we can be poor.
			if (currentPerformanceType == QuarterPerformanceType.CATASTROPHIC || currentPerformanceType == QuarterPerformanceType.TERRIBLE) {
				currentPerformanceType = QuarterPerformanceType.POOR;
			}
			break;
		case AVERAGE:
			// At worse we can be Terrible, at best Great..
			if (currentPerformanceType == QuarterPerformanceType.CATASTROPHIC) {
				currentPerformanceType = QuarterPerformanceType.TERRIBLE;
			} else if (currentPerformanceType == QuarterPerformanceType.EXTRAORDINARY) {
				currentPerformanceType = QuarterPerformanceType.GREAT;
			}
			break;
		case POOR:
			// At best we can be Good
			if (currentPerformanceType == QuarterPerformanceType.GREAT || currentPerformanceType == QuarterPerformanceType.EXTRAORDINARY) {
				currentPerformanceType = QuarterPerformanceType.GOOD;
			}
			break;
		case TERRIBLE:
			// At best we can be average
			if (currentPerformanceType == QuarterPerformanceType.GOOD || currentPerformanceType == QuarterPerformanceType.GREAT
					|| currentPerformanceType == QuarterPerformanceType.EXTRAORDINARY) {
				currentPerformanceType = QuarterPerformanceType.AVERAGE;
			}
			break;
		case CATASTROPHIC:
			if (currentPerformanceType == QuarterPerformanceType.AVERAGE || currentPerformanceType == QuarterPerformanceType.GOOD
					|| currentPerformanceType == QuarterPerformanceType.GREAT || currentPerformanceType == QuarterPerformanceType.EXTRAORDINARY) {
				currentPerformanceType = QuarterPerformanceType.POOR;
			}
			break;
		}

		previousType = currentPerformanceType;
		return currentPerformanceType;

	}

	public void addPreviousEvent(QuarterPerformanceType eventType) {
		previousType = eventType;
	}
}
