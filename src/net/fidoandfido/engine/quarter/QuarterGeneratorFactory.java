package net.fidoandfido.engine.quarter;

import java.util.HashMap;
import java.util.Map;


public class QuarterGeneratorFactory {

	static Map<String, QuarterGenerator> generatorMap = new HashMap<String, QuarterGenerator>();

	public static QuarterGenerator getGeneratorByName(String exchangeName, String eventGeneratorName) {

		if (generatorMap.keySet().contains(exchangeName)) {
			return generatorMap.get(exchangeName);
		}

		QuarterGenerator generator;

		if (CyclicalQuarterGenerator.NAME.equals(eventGeneratorName)) {
			generator = new CyclicalQuarterGenerator();
		} else {
			generator = new RandomQuarterGenerator();
		}
		generatorMap.put(exchangeName, generator);

		return generator;
	}

}
