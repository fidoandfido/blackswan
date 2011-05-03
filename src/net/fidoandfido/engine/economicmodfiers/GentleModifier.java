package net.fidoandfido.engine.economicmodfiers;

import net.fidoandfido.model.StockExchangePeriod;

public class GentleModifier implements EconomicModifier {

	public static final String NAME = "GENTLE_MODIFIER";

	public static final long MAX_DELTA_RATE = 3;

	public static final long MAX_DELTA_BASIS_POINTS = 300;

	@Override
	public void modifiyExchangePeriod(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod) {
		// Only make small adjustments, and keep within a small range.

		// In here, we can modify the expected expenses, revenues and interest
		// rates for *ALL* companies.

		// For the moment just return :)

	}

}
