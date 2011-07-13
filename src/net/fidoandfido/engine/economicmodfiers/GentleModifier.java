package net.fidoandfido.engine.economicmodfiers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.fidoandfido.model.SectorOutlook;
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

	private static Random SHIFT_CONDITIONS_RANDOM = new Random(17);
	private static Random SHIFT_SECTOR_RANDOM = new Random(17);
	private static Random CREATE_NEW_COMPANY_RANDOM = new Random(17);

	private static final int CHANCE_OF_SHIFT = 3;

	private static final int CHANCE_OF_SECTOR_SHIFT = 10;
	private static final int CHANCE_OF_EXTREME_SECTOR_SHIFT = 5;

	public static final long STANDARD_SECTOR_SHIFT = 1;
	public static final long EXTREME_SECTOR_SHIFT = 3;

	private static final int CHANCE_OF_NEW_COMPANY = 3;

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
		boolean shiftConditions = (SHIFT_CONDITIONS_RANDOM.nextInt(CHANCE_OF_SHIFT) == 0);
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

	@Override
	public void modifySectors(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod) {

		// Now modify the sector outlooks.
		// If they were not neutral, then make them so
		// Otherwise, there is a small chance that there will be non-neutral conditions.
		// If there are non-neutral conditions, there is a small chance they will be extreme.
		// Apply the settings accordingly!
		Set<String> sectorList = currentPeriod.getStockExchange().getSectors();
		for (String sector : sectorList) {
			SectorOutlook previousOutlook = previousPeriod.getSectorOutlook(sector);
			SectorOutlook currentOutlook = currentPeriod.getSectorOutlook(sector);

			if (previousOutlook.getRevenueModifier() != 0) {
				currentOutlook.setExpenseModifier(0);
				currentOutlook.setRevenueModifier(0);
				currentOutlook.setOutlookMessage(SectorOutlook.DEFAULT_NEUTRAL_MESSAGE);
			} else {
				if (SHIFT_SECTOR_RANDOM.nextInt(CHANCE_OF_SECTOR_SHIFT) == 0) {
					if (SHIFT_SECTOR_RANDOM.nextBoolean()) {
						// positive event
						if (SHIFT_SECTOR_RANDOM.nextInt(CHANCE_OF_EXTREME_SECTOR_SHIFT) == 0) {
							// EXTREME POSITIVE EVENT!
							currentOutlook.setRevenueModifier(EXTREME_SECTOR_SHIFT);
							currentOutlook.setOutlookMessage("Extreme positive situation!");
						} else {
							// Standard positive
							currentOutlook.setRevenueModifier(STANDARD_SECTOR_SHIFT);
							currentOutlook.setOutlookMessage("Positive situation.");
						}
					} else {
						// negative
						if (SHIFT_SECTOR_RANDOM.nextInt(CHANCE_OF_EXTREME_SECTOR_SHIFT) == 0) {
							// EXTREME NEGATIVE EVENT!
							currentOutlook.setRevenueModifier(EXTREME_SECTOR_SHIFT * -1);
							currentOutlook.setOutlookMessage("Extremely negative situation!");
						} else {
							// Standard negative
							currentOutlook.setRevenueModifier(STANDARD_SECTOR_SHIFT * -1);
							currentOutlook.setOutlookMessage("Negative situation!");

						}

					}

				}
			}

		}
	}

	@Override
	public boolean newCompanyToBeFounded(StockExchangePeriod currentPeriod) {
		if (currentPeriod.getEconomicConditions().equals(RECESSION)) {
			return false;
		}
		if (currentPeriod.getStockExchange().getMinTradingCompanyCount() > currentPeriod.getStockExchange().getCompanyCount()) {
			return true;
		}
		if (CREATE_NEW_COMPANY_RANDOM.nextInt(CHANCE_OF_NEW_COMPANY) == 0) {
			return true;
		}
		return false;
	}

	public static void main(String argv[]) {
		StockExchange stockExchange = new StockExchange("FOO", 500);
		stockExchange.addSector("Fizzing");
		GentleModifier modifier = new GentleModifier();

		Map<String, SectorOutlook> outlooks = new HashMap<String, SectorOutlook>();
		outlooks.put("Fizzing", new SectorOutlook("Fizzing", 0, 0, SectorOutlook.DEFAULT_NEUTRAL_MESSAGE));

		StockExchangePeriod previousPeriod = new StockExchangePeriod(stockExchange, null, null, 0, 0, 0, 0, NEUTRAL, outlooks);
		StockExchangePeriod currentPeriod;
		// = new StockExchangePeriod(previousPeriod, null, null);

		System.out.println("Period data - deltas");
		System.out.println("GEN    REVENUE  EXPENSES    INTEREST   CONDITIONS");
		for (int i = 0; i < 90; i++) {
			currentPeriod = new StockExchangePeriod(previousPeriod, null, null);
			modifier.modifiyExchangePeriod(currentPeriod, previousPeriod);
			modifier.modifySectors(currentPeriod, previousPeriod);
			System.out.print(currentPeriod.getGeneration());
			System.out.print("\t");
			System.out.print(currentPeriod.getRevenueRateDelta());
			System.out.print("\t");
			System.out.print(currentPeriod.getExpenseRateDelta());
			System.out.print("\t");
			System.out.print(currentPeriod.getInterestRateBasisPointsDelta());
			System.out.print("\t");
			System.out.print(currentPeriod.getEconomicConditions());
			System.out.print("\t\t");
			System.out.println(currentPeriod.getSectorOutlooks().values().toArray()[0]);
			previousPeriod = currentPeriod;
		}

	}

}
