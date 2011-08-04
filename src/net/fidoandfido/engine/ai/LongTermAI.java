package net.fidoandfido.engine.ai;

import java.util.Date;

import net.fidoandfido.model.Trader;

public class LongTermAI extends AITrader {

	public static final String NAME = "LongTerm";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void performTrades(Trader trader, Date tradeDate) {

	}

}
