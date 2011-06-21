package net.fidoandfido.engine.experience;

import net.fidoandfido.model.Trader;

public class ExperiencePointGenerator {

	public static enum ExperienceEvent {
		BUY_SHARES, SELL_SHARES, DIVIDEND, BUY_ITEM, SELL_ITEM
	}

	public void addExperiencePoints(Trader trader, ExperienceEvent event, long transactionValue) {
		if (trader.isAITrader() || trader.isMarketMaker()) {
			return;
		}

		long currentExperiencePoints = trader.getExperiencePoints();

		long points = 0;
		switch (event) {
		case BUY_SHARES:
		case SELL_SHARES:
			points = 10;
			points = modifyPoints(currentExperiencePoints, points, transactionValue);
			trader.addExperiencePoints(points);
			break;
		case BUY_ITEM:
		case SELL_ITEM:
			break;
		case DIVIDEND:
			points = 10;
			points = modifyPoints(currentExperiencePoints, points, transactionValue);
			trader.addExperiencePoints(points);
			break;
		}

	}

	private long modifyPoints(long currentExperiencePoints, long startingPoints, long transactionValue) {
		long points = startingPoints;
		if (currentExperiencePoints >= 1000) {
			points = points / 10;
		} else if (currentExperiencePoints >= 500) {
			points = points / 5;
		} else if (currentExperiencePoints >= 200) {
			points = points / 2;
		}
		if (points < 1) {
			points = 1;
		}
		if (transactionValue >= 1000000) {
			points = points * 2;
		}
		return points;
	}

}
