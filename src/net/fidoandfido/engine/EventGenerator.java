package net.fidoandfido.engine;

import net.fidoandfido.util.Constants.EventType;

public interface EventGenerator {

	public EventType getNextEventType();
}
