package net.fidoandfido.engine.economicmodfiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.fidoandfido.dao.PeriodMessageDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.PeriodMessage;
import net.fidoandfido.model.SectorOutlook;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.model.StockExchangePeriod;

public class GentleModifier implements EconomicModifier {

	private static final String NEGATIVE_SITUATION = "Negative situation!";
	private static final String EXTREMELY_NEGATIVE_SITUATION = "Extremely negative situation!";
	private static final String POSITIVE_SITUATION = "Positive situation.";
	private static final String EXTREME_POSITIVE_SITUATION = "Extreme positive situation!";

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

	// A company will always remain solvent if their capitisation is greater
	// than this.
	private static long CAPITALISATION_TO_ALWAYS_STAY_SOLVENT = 100000000;

	// Capital must be at least this % of debt for company to stay solvent
	private static long MINIMUM_RATE_OF_CAPITAL_TO_DEBT = 10;

	private PeriodMessageDAO periodMessageDAO = new PeriodMessageDAO();

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
		//
		// At each level change, generate a message to indicate that the economic climate has shifted.
		//
		// Job DONE!
		//
		StockExchange exchange = currentPeriod.getStockExchange();

		boolean shiftConditions = (SHIFT_CONDITIONS_RANDOM.nextInt(CHANCE_OF_SHIFT) == 0);
		if (!shiftConditions) {
			// No change to economic outlook!
			return;
		}

		if (previousPeriod.getEconomicConditions().equals(NEUTRAL)) {
			updateEconomicConditions(currentPeriod, exchange, MAX_DELTA_RATE, 0, 0, GROWING);
		} else if (previousPeriod.getEconomicConditions().equals(GROWING)) {
			updateEconomicConditions(currentPeriod, exchange, MAX_DELTA_RATE, MAX_DELTA_RATE, 0, BOOMING);
		} else if (previousPeriod.getEconomicConditions().equals(BOOMING)) {
			updateEconomicConditions(currentPeriod, exchange, MAX_DELTA_RATE, MAX_DELTA_RATE, MAX_DELTA_BASIS_POINTS, STABILISING);
		} else if (previousPeriod.getEconomicConditions().equals(STABILISING)) {
			updateEconomicConditions(currentPeriod, exchange, 0, MAX_DELTA_RATE, 0, CONTRACTING);
		} else if (previousPeriod.getEconomicConditions().equals(CONTRACTING)) {
			updateEconomicConditions(currentPeriod, exchange, MAX_DELTA_RATE * -1, MAX_DELTA_RATE, MAX_DELTA_BASIS_POINTS * -1, RECESSION);
		} else if (previousPeriod.getEconomicConditions().equals(RECESSION)) {
			updateEconomicConditions(currentPeriod, exchange, MAX_DELTA_RATE * -1, 0, MAX_DELTA_BASIS_POINTS * -1, RECOVERING);
		} else if (previousPeriod.getEconomicConditions().equals(RECOVERING)) {
			updateEconomicConditions(currentPeriod, exchange, 0, 0, 0, NEUTRAL);
		} else {
			// UNKNOWN STATE !!! Set to neutral (as for recovering)
			updateEconomicConditions(currentPeriod, exchange, 0, 0, 0, NEUTRAL);
		}
	}

	private void updateEconomicConditions(StockExchangePeriod currentPeriod, StockExchange exchange, long revenueDeltaRate, long expenseDeltaRate,
			long interstRateDelta, String economicConditions) {
		currentPeriod.setRevenueRateDelta(revenueDeltaRate);
		currentPeriod.setExpenseRateDelta(expenseDeltaRate);
		currentPeriod.setInterestRateBasisPointsDelta(interstRateDelta);
		currentPeriod.setEconomicConditions(economicConditions);
		periodMessageDAO.saveMessage(new PeriodMessage(exchange, currentPeriod, getEconomicConditionsMessage(economicConditions)));
	}

	Random messageChooser = new Random(17);

	// This should actually go and find a message from somewhere...
	private String getEconomicConditionsMessage(String economicConditions) {
		ArrayList<String> messages = new ArrayList<String>();
		if (economicConditions.equals(RECOVERING)) {
			messages.add("Govt takes credit for expected recovery; expenses to stabilise as interest rates drop.");
			messages.add("Analysts bouyed at recovery prospects, latest figures show interest still low and expenses coming down.");
			messages.add("Economy recovering, fundamentals good say economists. Words backed by lower interest rates and lower expenses.");
		} else if (economicConditions.equals(RECESSION)) {
			messages.add("Credit binge takes it toll, revenues down as companies tighten belts.");
			messages.add("Interest rates cutting spending, revenues down, doom and gloom set in.");
			messages.add("Drop in consumer confidence denting company profits, drop in interest rates demanded (but unheeded).");
		} else if (economicConditions.equals(CONTRACTING)) {
			messages.add("Interest rates dropped to spur investment. No result yet.");
			messages.add("Revenues still down as central bank attempts to boost spending with interest cuts.");
			messages.add("Economists puzzled by market failures as interest rates drop and revenues stay low.");
			messages.add("Government blamed for low revenues as interest rates drop.");
		} else if (economicConditions.equals(STABILISING)) {
			messages.add("Interest rates lifted to put brakes on overheated economy.");
			messages.add("Companies still enjoying high turnover as central bank tries to dampen mood.");
			messages.add("Economy motoring despite high interest rates.");
		} else if (economicConditions.equals(BOOMING)) {
			messages.add("Economy booming, turnover high. 'This time is different' according to analysts.");
			messages.add("High revenues leading to higher prices - calls to lift interest rates unheeded.");
			messages.add("Boom times enjoyed as companies roll in cash. No need to lift interest rates according to Government.");
		} else if (economicConditions.equals(GROWING)) {
			messages.add("Goverment takes credit for growth figures - revenues up.");
			messages.add("Economists say economy has strong fundamentals, boom times coming.");
			messages.add("Analysts predict growth as economy gathers pace.");
		} else if (economicConditions.equals(NEUTRAL)) {
			messages.add("Economy neutral, companies look to push up production.");
			messages.add("Analysts bored with current economy, looking for opportunities for excitement.");
			messages.add("Economists forced to over-complicate matters as economy shifts to neutral conditions.");
		} else {
			// Unkown state!
			messages.add("Economy in strange state, analysts, economists and Government alike all baffled.");
		}
		String message = messages.get(messageChooser.nextInt(messages.size()));
		if (message == null || message.isEmpty()) {
			message = "Economy shifted, currently: " + economicConditions;
		}
		return message;
	}

	@Override
	public void modifySectors(StockExchangePeriod currentPeriod, StockExchangePeriod previousPeriod) {
		// Modify the sector outlooks.
		// If they are already changed, return them to 0,
		// otherwise modify them
		Set<String> sectorList = currentPeriod.getStockExchange().getSectors();
		for (String sector : sectorList) {
			SectorOutlook previousOutlook = previousPeriod.getSectorOutlook(sector);
			SectorOutlook currentOutlook = currentPeriod.getSectorOutlook(sector);
			if (previousOutlook.getRevenueModifier() != 0) {
				currentOutlook.setExpenseModifier(0);
				currentOutlook.setRevenueModifier(0);
				currentOutlook.setOutlookMessage(SectorOutlook.DEFAULT_NEUTRAL_MESSAGE);
			} else {
				modifySector(currentPeriod, currentOutlook, sector);
			}
		}
	}

	// There is a small chance that there will be non-neutral conditions.
	// If there are non-neutral conditions, there is a small chance they will be extreme.
	// Apply the settings accordingly!
	private void modifySector(StockExchangePeriod currentPeriod, SectorOutlook currentOutlook, String sector) {
		if (SHIFT_SECTOR_RANDOM.nextInt(CHANCE_OF_SECTOR_SHIFT) == 0) {
			// We have an event!
			if (SHIFT_SECTOR_RANDOM.nextBoolean()) {
				// positive event
				if (SHIFT_SECTOR_RANDOM.nextInt(CHANCE_OF_EXTREME_SECTOR_SHIFT) == 0) {
					// EXTREME POSITIVE EVENT!
					updateOutlook(currentPeriod, sector, currentOutlook, EXTREME_SECTOR_SHIFT, EXTREME_POSITIVE_SITUATION);
				} else {
					// Standard positive
					updateOutlook(currentPeriod, sector, currentOutlook, STANDARD_SECTOR_SHIFT, POSITIVE_SITUATION);
				}
			} else {
				// negative
				if (SHIFT_SECTOR_RANDOM.nextInt(CHANCE_OF_EXTREME_SECTOR_SHIFT) == 0) {
					// EXTREME NEGATIVE EVENT!
					updateOutlook(currentPeriod, sector, currentOutlook, EXTREME_SECTOR_SHIFT * -1, EXTREMELY_NEGATIVE_SITUATION);
				} else {
					// Standard negative
					updateOutlook(currentPeriod, sector, currentOutlook, STANDARD_SECTOR_SHIFT * -1, NEGATIVE_SITUATION);
				}
			}

		}
	}

	private void updateOutlook(StockExchangePeriod currentPeriod, String sector, SectorOutlook currentOutlook, long revenueModifier, String outlook) {
		currentOutlook.setRevenueModifier(revenueModifier);
		currentOutlook.setOutlookMessage(outlook);
		periodMessageDAO.saveMessage(new PeriodMessage(currentPeriod.getStockExchange(), currentPeriod, sector, getSectorMessage(outlook, sector)));
	}

	private String getSectorMessage(String situation, String sector) {
		// TODO Auto-generated method stub
		ArrayList<String> messages = new ArrayList<String>();
		if (situation.equals(EXTREMELY_NEGATIVE_SITUATION)) {
			messages.add("Overdue regulation reform to " + sector + " sector leads to industry shake up, revenues hit, CEOs warn of calamity ahead.");
			messages.add("The " + sector + " sector hit by critival skills shortage as worker move on; profits hit.");
			messages.add("Market for " + sector + " sector decimated by foriegn competition, CEOs blame government for inaction.");
		} else if (situation.equals(NEGATIVE_SITUATION)) {
			messages.add("The " + sector + " sector effected by shortfall in required infrastructure, revenues down as a result.");
			messages.add("Market for " + sector + " sector squeezed as consumers lose interest, new marketing failing to attract. Industry bodies alarmed.");
			messages.add("Government shows no love for the " + sector + " sector, as CEOs clamour for easing on regulations.");
		} else if (situation.equals(POSITIVE_SITUATION)) {
			messages.add("The " + sector + " sector benefits from new technology, revenues up.");
			messages.add("Demand in " + sector + " sector up as consumers start to take interest.");
			messages.add("Goverment stimulus to " + sector + " sector pushes up revenues, CEOs relaxed.");
		} else if (situation.equals(EXTREME_POSITIVE_SITUATION)) {
			messages.add("Boom times ahead for " + sector + " as investment and competition lead to new efficiencies.");
			messages.add("Deregulation allows for potentially record breaking profits in " + sector + " sector, consumer groups concerned.");
			messages.add("The " + sector + " sector dispute government claims for increased revenue, 'It was all us' say leading group of CEOs.");
		}
		String message = messages.get(messageChooser.nextInt(messages.size()));
		if (message == null || message.isEmpty()) {
			message = "The " + sector + " sector has shifted, currently: " + situation;
		}
		return message;
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

	@Override
	public void updateCompanyTradingStatus(Company company) {

		// Check if we meet the requirements for being insolvent.
		// Large debt, small capitalisation.
		boolean insolvent = false;
		long assets = company.getAssetValue();
		long debts = company.getDebtValue();
		long capitalisation = assets - debts;

		if (capitalisation < 0) {
			// Okay, we effectively have negative capitalisation (assets are
			// less than debts!)
			insolvent = true;
		} else if (capitalisation > CAPITALISATION_TO_ALWAYS_STAY_SOLVENT) {
			// Who cares about debt, there is plenty of assets.
			insolvent = false;
		} else {
			if (capitalisation < (debts * MINIMUM_RATE_OF_CAPITAL_TO_DEBT / 100)) {
				// Our debts have kicked into the warning threshold
				// check if the company is actually profitable.
				if (company.getCurrentPeriod().getFinalProfit() < 0 && company.getPreviousProfit() < 0) {
					// Two consecutive loss years - not so profitable...
					insolvent = true;
				}
			}
		}
		if (insolvent) {
			company.setCompanyStatus(Company.INSOLVENT_COMPANY_STATUS);
			company.setInsolvent(true);
		} else if (company.isInsolvent()) {
			company.setCompanyStatus(Company.NO_MORE_INSOLVENT_COMPANY_STATUS);
			company.setInsolvent(false);
		} else {
			company.setCompanyStatus(Company.TRADING_COMPANY_STATUS);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.fidoandfido.engine.companymodifiers.CompanyModifier#isCompanyInsolvent(net.fidoandfido.model.Company)
	 */
	@Override
	public boolean isCompanyToBeDissolved(Company company) {
		// Check if the company is insolvent, and if it is, make it so!
		boolean toBeDisolved = false;
		if (company.isInsolvent()) {
			if (company.getCurrentPeriod().getFinalProfit() < 0) {
				toBeDisolved = true;
			}
		}
		if (company.getAssetValue() < 0) {
			toBeDisolved = true;
		}
		return toBeDisolved;
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
