package net.fidoandfido.engine.event;

import net.fidoandfido.util.Constants.EventType;

public interface EventGenerator {

	public EventType getNextEventType();

	public EventType getNextEventType(EventType event);
}
