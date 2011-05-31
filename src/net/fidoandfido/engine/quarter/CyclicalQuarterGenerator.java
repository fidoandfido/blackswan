package net.fidoandfido.engine.quarter;

import java.util.Random;

import net.fidoandfido.util.Constants.QuarterPerformanceType;

public class CyclicalQuarterGenerator implements QuarterGenerator {

	public static final String NAME = "cyclicEvents";

	static Random random = new Random();

	QuarterPerformanceType[] sortedArray = { QuarterPerformanceType.CATASTROPHIC, // 0
			QuarterPerformanceType.TERRIBLE, // 1
			QuarterPerformanceType.POOR, // 2
			QuarterPerformanceType.AVERAGE, // 3
			QuarterPerformanceType.GOOD, // 4
			QuarterPerformanceType.GREAT, // 5
			QuarterPerformanceType.EXTRAORDINARY }; // 6

	private int lowerEventCountBound = 1000;

	private int upperEventCountBound = 2000;

	QuarterPerformanceType currentEventCycle = QuarterPerformanceType.AVERAGE;

	private int currentEventCount = 0;

	private boolean gettingBetter = true;

	public CyclicalQuarterGenerator() {
		// Default
		resetEventCount();
	}

	public CyclicalQuarterGenerator(int lowerEventCountBound, int upperEventCountBound) {
		this.lowerEventCountBound = lowerEventCountBound;
		this.upperEventCountBound = upperEventCountBound;
		resetEventCount();
	}

	@Override
	public QuarterPerformanceType getNextEventType() {
		return getNextEventType(QuarterPerformanceType.AVERAGE);
	}

	@Override
	public QuarterPerformanceType getNextEventType(QuarterPerformanceType previousEvent) {
		// This will produce events of the same general type.
		// it will be biased against the extreme events.

		// First up, see if we need to change.
		if (--currentEventCount == 0) {
			resetEventCount();
			switch (currentEventCycle) {
			case POOR:
				gettingBetter = true;
				currentEventCycle = QuarterPerformanceType.AVERAGE;
				break;
			case AVERAGE:
				if (gettingBetter) {
					currentEventCycle = QuarterPerformanceType.GOOD;
				} else {
					currentEventCycle = QuarterPerformanceType.POOR;
				}
				break;
			case GOOD:
				gettingBetter = false;
				currentEventCycle = QuarterPerformanceType.AVERAGE;
				break;
			default:
				currentEventCycle = QuarterPerformanceType.AVERAGE;
				gettingBetter = true;
				break;
			}
		}

		QuarterPerformanceType newEvent = QuarterPerformanceType.AVERAGE;

		// Now figure out what type of event we need to return
		switch (currentEventCycle) {
		case POOR:
			// 10 % chance of CATASTROPHIC
			// 20 % chance of TERRIBLE
			// 30 % chance of POOR
			// 20 % chance of AVERAGE
			// 10 % chance of GOOD
			// 10 % chance of GREAT
			// 0 % chance of EXTRAORDINARY
			switch (random.nextInt(10)) {
			case 0:
				newEvent = QuarterPerformanceType.CATASTROPHIC;
				break;
			case 1:
			case 2:
				newEvent = QuarterPerformanceType.TERRIBLE;
				break;
			case 3:
			case 4:
			case 5:
				newEvent = QuarterPerformanceType.POOR;
				break;
			case 6:
			case 7:
				newEvent = QuarterPerformanceType.AVERAGE;
				break;
			case 8:
				newEvent = QuarterPerformanceType.GOOD;
				break;
			case 9:
				newEvent = QuarterPerformanceType.GREAT;
				break;
			}
			break;
		case AVERAGE:
			// 0 % chance of CATASTROPHIC
			// 10 % chance of TERRIBLE
			// 20 % chance of POOR
			// 40 % chance of AVERAGE
			// 20 % chance of GOOD
			// 10 % chance of GREAT
			// 0 % chance of EXTRAORDINARY
			switch (random.nextInt(10)) {
			case 0:
				newEvent = QuarterPerformanceType.TERRIBLE;
				break;
			case 1:
			case 2:
				newEvent = QuarterPerformanceType.POOR;
				break;
			case 3:
			case 4:
			case 5:
			case 6:
				newEvent = QuarterPerformanceType.AVERAGE;
				break;
			case 7:
			case 8:
				newEvent = QuarterPerformanceType.GOOD;
				break;
			case 9:
				newEvent = QuarterPerformanceType.GREAT;
				break;
			}
			break;
		case GOOD:
			// 0 % chance of CATASTROPHIC
			// 10 % chance of TERRIBLE
			// 10 % chance of POOR
			// 20 % chance of AVERAGE
			// 30 % chance of GOOD
			// 20 % chance of GREAT
			// 10 % chance of EXTRAORDINARY
			int rand = random.nextInt(10);
			switch (rand) {
			case 0:
				newEvent = QuarterPerformanceType.TERRIBLE;
				break;
			case 1:
				newEvent = QuarterPerformanceType.POOR;
				break;
			case 2:
			case 3:
				newEvent = QuarterPerformanceType.AVERAGE;
				break;
			case 4:
			case 5:
			case 6:
				newEvent = QuarterPerformanceType.GOOD;
				break;
			case 7:
			case 8:
				newEvent = QuarterPerformanceType.GREAT;
				break;
			case 9:
				newEvent = QuarterPerformanceType.EXTRAORDINARY;
				break;
			}
			break;
		default:
			// Strange - we are not in the expected event cycle!
			currentEventCycle = QuarterPerformanceType.AVERAGE;
			gettingBetter = true;
		}

		// Make sure that our old event is not too far away from the new one.
		// Don't worry about the average case.
		switch (previousEvent) {
		case EXTRAORDINARY:
		case GREAT:
			if (newEvent == QuarterPerformanceType.TERRIBLE || newEvent == QuarterPerformanceType.CATASTROPHIC) {
				newEvent = QuarterPerformanceType.POOR;
			}
			break;
		case GOOD:
			if (newEvent == QuarterPerformanceType.CATASTROPHIC) {
				newEvent = QuarterPerformanceType.TERRIBLE;
			}
		case AVERAGE:
			break;
		case POOR:
			if (newEvent == QuarterPerformanceType.EXTRAORDINARY) {
				newEvent = QuarterPerformanceType.GREAT;
			}
			break;
		case TERRIBLE:
		case CATASTROPHIC:
			if (newEvent == QuarterPerformanceType.EXTRAORDINARY || newEvent == QuarterPerformanceType.GREAT) {
				newEvent = QuarterPerformanceType.GOOD;
			}
			break;
		}
		return newEvent;
	}

	private void resetEventCount() {
		if (upperEventCountBound == lowerEventCountBound) {
			currentEventCount = lowerEventCountBound;
		} else {
			currentEventCount = random.nextInt(upperEventCountBound - lowerEventCountBound) + lowerEventCountBound;
		}
	}

	public static void main(String argv[]) {

		CyclicalQuarterGenerator gen = new CyclicalQuarterGenerator(100, 100);
		int[] counts = new int[7];
		for (int i = 0; i < 10; i++) {
			System.out.println("Current event cycle: " + gen.currentEventCycle);
			for (int j = 0; j < 100; j++) {
				QuarterPerformanceType et = gen.getNextEventType();
				switch (et) {
				case CATASTROPHIC:
					counts[0]++;
					break;
				case TERRIBLE:
					counts[1]++;
					break;
				case POOR:
					counts[2]++;
					break;
				case AVERAGE:
					counts[3]++;
					break;
				case GOOD:
					counts[4]++;
					break;
				case GREAT:
					counts[5]++;
					break;
				case EXTRAORDINARY:
					counts[6]++;
					break;
				}
			}
			for (int c = 0; c < 7; c++)
				System.out.print(counts[c] + "\t");
			System.out.println();
			counts = new int[7];
		}

	}

}
