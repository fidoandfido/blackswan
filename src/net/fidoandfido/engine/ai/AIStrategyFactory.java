package net.fidoandfido.engine.ai;

public class AIStrategyFactory {

	public AITradeStrategy getStrategyByName(String strategyName) {
		if (NaiveAI.Name.equals(strategyName)) {
			return new NaiveAI();
		}
		if (ReactiveAI.Name.equals(strategyName)) {
			return new ReactiveAI();
		}
		if (ValueAI.Name.equals(strategyName)) {
			return new ValueAI();
		}

		return new RandomAI();
	}

}
