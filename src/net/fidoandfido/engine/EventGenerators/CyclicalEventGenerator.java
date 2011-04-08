package net.fidoandfido.engine.EventGenerators;

import java.util.Random;

import net.fidoandfido.engine.EventGenerator;
import net.fidoandfido.util.Constants.EventType;

public class CyclicalEventGenerator implements EventGenerator {

	public static final String NAME = "cyclicEvents";

	static Random random = new Random();

	EventType[] sortedArray = { EventType.CATASTROPHIC, // 0
			EventType.TERRIBLE, // 1
			EventType.POOR, // 2
			EventType.AVERAGE, // 3
			EventType.GOOD, // 4
			EventType.GREAT, // 5
			EventType.EXTRAORDINARY }; // 6

	private int lowerEventCountBound = 100;

	private int upperEventCountBound = 200;

	EventType currentEventCycle = EventType.AVERAGE;

	private int currentEventCount = 0;

	private boolean gettingBetter = true;

	public CyclicalEventGenerator() {
		// Default
		resetEventCount();
	}

	public CyclicalEventGenerator(int lowerEventCountBound, int upperEventCountBound) {
		this.lowerEventCountBound = lowerEventCountBound;
		this.upperEventCountBound = upperEventCountBound;
		resetEventCount();
	}

	@Override
	public EventType getNextEventType() {
		// This will produce events of the same general type.
		// it will be biased against the extreme events.

		// First up, see if we need to change.
		if (--currentEventCount == 0) {
			resetEventCount();
			switch (currentEventCycle) {
			case POOR:
				gettingBetter = true;
				currentEventCycle = EventType.AVERAGE;
				break;
			case AVERAGE:
				if (gettingBetter) {
					currentEventCycle = EventType.GOOD;
				} else {
					currentEventCycle = EventType.POOR;
				}
				break;
			case GOOD:
				gettingBetter = false;
				currentEventCycle = EventType.AVERAGE;
				break;
			default:
				currentEventCycle = EventType.AVERAGE;
				gettingBetter = true;
				break;
			}
		}

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
				return EventType.CATASTROPHIC;
			case 1:
			case 2:
				return EventType.TERRIBLE;
			case 3:
			case 4:
			case 5:
				return EventType.POOR;
			case 6:
			case 7:
				return EventType.AVERAGE;
			case 8:
				return EventType.GOOD;
			case 9:
				return EventType.GREAT;
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
				return EventType.TERRIBLE;
			case 1:
			case 2:
				return EventType.POOR;
			case 3:
			case 4:
			case 5:
			case 6:
				return EventType.AVERAGE;
			case 7:
			case 8:
				return EventType.GOOD;
			case 9:
				return EventType.GREAT;
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
				return EventType.TERRIBLE;
			case 1:
				return EventType.POOR;
			case 2:
			case 3:
				return EventType.AVERAGE;
			case 4:
			case 5:
			case 6:
				return EventType.GOOD;
			case 7:
			case 8:
				return EventType.GREAT;
			case 9:
				return EventType.EXTRAORDINARY;
			}
			break;
		default:
			// Strange - we are not in the expected event cycle!
			currentEventCycle = EventType.AVERAGE;
			gettingBetter = true;
		}
		// SAFETY
		return currentEventCycle;
	}

	private void resetEventCount() {
		if (upperEventCountBound == lowerEventCountBound) {
			currentEventCount = lowerEventCountBound;
		} else {
			currentEventCount = random.nextInt(upperEventCountBound - lowerEventCountBound) + lowerEventCountBound;
		}
	}

	public static void main(String argv[]) {

		CyclicalEventGenerator gen = new CyclicalEventGenerator(100, 100);
		int[] counts = new int[7];
		for (int i = 0; i < 10; i++) {
			System.out.println("Current event cycle: " + gen.currentEventCycle);
			for (int j = 0; j < 100; j++) {
				EventType et = gen.getNextEventType();
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
