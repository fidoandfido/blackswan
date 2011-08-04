package net.fidoandfido.engine.ai;

import java.util.Date;

import net.fidoandfido.model.Trader;

public interface AITradeStrategy {

	public String getName();

	public void performTrades(Trader trader, Date date);

}
