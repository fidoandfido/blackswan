package net.fidoandfido.engine.EventGenerators;

import java.util.HashMap;
import java.util.Map;

import net.fidoandfido.engine.EventGenerator;

public class EventGeneratorFactory {

	static Map<String, EventGenerator> generatorMap = new HashMap<String, EventGenerator>();

	public static EventGenerator getGeneratorByName(String exchangeName, String eventGeneratorName) {

		if (generatorMap.keySet().contains(exchangeName)) {
			return generatorMap.get(exchangeName);
		}

		EventGenerator generator;

		if (CyclicalEventGenerator.NAME.equals(eventGeneratorName)) {
			generator = new CyclicalEventGenerator();
		} else {
			generator = new RandomEventGenerator();
		}
		generatorMap.put(exchangeName, generator);

		return generator;
	}

}
