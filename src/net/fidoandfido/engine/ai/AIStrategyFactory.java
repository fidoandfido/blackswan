package net.fidoandfido.engine.ai;

public class AIStrategyFactory {

	public AITrader getStrategyByName(String strategyName) {
		if (NaiveAI.Name.equals(strategyName)) {
			return new NaiveAI();
		}
		if (ReactiveAI.Name.equals(strategyName)) {
			return new ReactiveAI();
		}
		if (ValueAI.Name.equals(strategyName)) {
			return new ValueAI();
		}
		if (LongTermAI.NAME.equals(strategyName)) {
			return new LongTermAI();
		}

		return new RandomAI();
	}

}
