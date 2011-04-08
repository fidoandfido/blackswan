package net.fidoandfido.engine.ai;

import net.fidoandfido.model.Trader;

public class RandomAI implements AITradeStrategy {

	public static final String NAME = "Random";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void performTrades(Trader trader) {
		// Perform some trades...

	}

}
