package net.fidoandfido.engine.EventGenerators;

import java.util.Random;

import net.fidoandfido.engine.EventGenerator;
import net.fidoandfido.util.Constants.EventType;

public class RandomEventGenerator implements EventGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.EventGenerator#getNextEventType()
	 */
	Random random = new Random();

	@Override
	public EventType getNextEventType() {
		EventType[] events = EventType.values();
		return events[random.nextInt(events.length)];
	}

}
