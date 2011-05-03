package net.fidoandfido.engine.economicmodfiers;

public class EconomicModifierFactory {

	public static EconomicModifier getEconomicModifier(String name) {

		if (GentleModifier.NAME.equals(name)) {
			return new GentleModifier();
		}

		return new DefaultModifier();
	}

}
