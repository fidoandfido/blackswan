package net.fidoandfido.engine.eventgenerators;

import java.util.Random;

import net.fidoandfido.engine.event.EventGenerator;
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

	@Override
	public EventType getNextEventType(EventType event) {
		EventType[] events = EventType.values();
		return events[random.nextInt(events.length)];
	}

}
