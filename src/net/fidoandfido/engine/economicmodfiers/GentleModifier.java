package net.fidoandfido.engine.economicmodfiers;

import java.util.Random;

import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;

public class GentleModifier implements EconomicModifier {

	private static final String RECOVERING = "Recovering";
	private static final String RECESSION = "Recession";
	private static final String CONTRACTING = "Contracting";
	private static final String STABILISING = "Stabilising";
	private static final String BOOMING = "Booming";
	private static final String GROWING = "Growing";
	private static final String NEUTRAL = "Neutral";

	public static final String NAME = "GENTLE_MODIFIER";

	public static final long MAX_DELTA_RATE = 2;

	public static final long MAX_DELTA_BASIS_POINTS = 200;

	private Random shiftConditionsRandom = new Random(17);

	private static final int CHANCE_OF_SHIFT = 3;

	@Override
	public void modifiyExchangePeriod(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod) {
		// Only make small adjustments, and keep within a small range.
		// The behaviour is like this:
		// When everything is in equilibrium, the revenue rate will start to
		// increase.
		// As revenue rate increases, there is a change expenses increase.
		// As this happens, the interest rate increase.
		// After this, there is less money in the economy, so the revenue rate
		// declines
		// revenues declining lead to efficiencies, so the expense rate drops
		// As this contraction takes place, the interest rate will be lowered,
		// to spur investment.
		// Job DONE!
		boolean shiftConditions = (shiftConditionsRandom.nextInt(CHANCE_OF_SHIFT) == 0);
		if (!shiftConditions) {
			// No change to economic outlook!
			return;
		}

		if (previousPeriod.getEconomicConditions().equals(NEUTRAL)) {
			currentPeriod.setRevenueRateDelta(MAX_DELTA_RATE);
			currentPeriod.setExpenseRateDelta(0);
			currentPeriod.setInterestRateBasisPointsDelta(0);
			currentPeriod.setEconomicConditions(GROWING);
		} else if (previousPeriod.getEconomicConditions().equals(GROWING)) {
			currentPeriod.setRevenueRateDelta(MAX_DELTA_RATE);
			currentPeriod.setExpenseRateDelta(MAX_DELTA_RATE);
			currentPeriod.setInterestRateBasisPointsDelta(0);
			currentPeriod.setEconomicConditions(BOOMING);
		} else if (previousPeriod.getEconomicConditions().equals(BOOMING)) {
			currentPeriod.setRevenueRateDelta(MAX_DELTA_RATE);
			currentPeriod.setExpenseRateDelta(MAX_DELTA_RATE);
			currentPeriod.setInterestRateBasisPointsDelta(MAX_DELTA_BASIS_POINTS);
			currentPeriod.setEconomicConditions(STABILISING);
		} else if (previousPeriod.getEconomicConditions().equals(STABILISING)) {
			currentPeriod.setRevenueRateDelta(0);
			currentPeriod.setExpenseRateDelta(MAX_DELTA_RATE);
			currentPeriod.setInterestRateBasisPointsDelta(0);
			currentPeriod.setEconomicConditions(CONTRACTING);
		} else if (previousPeriod.getEconomicConditions().equals(CONTRACTING)) {
			currentPeriod.setRevenueRateDelta(MAX_DELTA_RATE * -1);
			currentPeriod.setExpenseRateDelta(MAX_DELTA_RATE);
			currentPeriod.setInterestRateBasisPointsDelta(MAX_DELTA_BASIS_POINTS * -1);
			currentPeriod.setEconomicConditions(RECESSION);
		} else if (previousPeriod.getEconomicConditions().equals(RECESSION)) {
			currentPeriod.setRevenueRateDelta(0);
			currentPeriod.setExpenseRateDelta(MAX_DELTA_RATE);
			currentPeriod.setInterestRateBasisPointsDelta(MAX_DELTA_BASIS_POINTS * -1);
			currentPeriod.setEconomicConditions(RECOVERING);
		} else if (previousPeriod.getEconomicConditions().equals(RECOVERING)) {
			currentPeriod.setRevenueRateDelta(0);
			currentPeriod.setExpenseRateDelta(0);
			currentPeriod.setInterestRateBasisPointsDelta(0);
			currentPeriod.setEconomicConditions(NEUTRAL);
		} else {
			// UNKNOWN STATE !!! Set to neutral (as for recovering)
			currentPeriod.setRevenueRateDelta(0);
			currentPeriod.setExpenseRateDelta(0);
			currentPeriod.setInterestRateBasisPointsDelta(0);
			currentPeriod.setEconomicConditions(NEUTRAL);
		}
	}

	public static void main(String argv[]) {
		StockExchange stockExchange = new StockExchange("FOO", 500);
		GentleModifier modifier = new GentleModifier();

		StockExchangePeriod previousPeriod = new StockExchangePeriod(stockExchange, null, null, 0, 0, 0, 0, NEUTRAL);
		StockExchangePeriod currentPeriod;
		// = new StockExchangePeriod(previousPeriod, null, null);

		System.out.println("Period data - deltas");
		System.out.println("GEN    REVENUE  EXPENSES    INTEREST   CONDITIONS");
		for (int i = 0; i < 90; i++) {
			currentPeriod = new StockExchangePeriod(previousPeriod, null, null);
			modifier.modifiyExchangePeriod(currentPeriod, previousPeriod);
			System.out.print(currentPeriod.getGeneration());
			System.out.print("\t");
			System.out.print(currentPeriod.getRevenueRateDelta());
			System.out.print("\t");
			System.out.print(currentPeriod.getExpenseRateDelta());
			System.out.print("\t");
			System.out.print(currentPeriod.getInterestRateBasisPointsDelta());
			System.out.print("\t");
			System.out.print(currentPeriod.getEconomicConditions());
			System.out.println();
			previousPeriod = currentPeriod;
		}

	}

}
