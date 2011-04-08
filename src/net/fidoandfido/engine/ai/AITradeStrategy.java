package net.fidoandfido.engine.ai;

import net.fidoandfido.model.Trader;

public interface AITradeStrategy {

	public String getName();

	public void performTrades(Trader trader);

}
