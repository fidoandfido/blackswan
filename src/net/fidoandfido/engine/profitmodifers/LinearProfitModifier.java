package net.fidoandfido.engine.profitmodifers;

import java.util.Random;

import net.fidoandfido.engine.ProfitModifier;
import net.fidoandfido.util.Constants.EventType;

/**
 * Adjust the profit in a linear fashion. The modifier is expressed as a percentage change and is set to the maximum
 * possible change; the volatility will then reduce them some amount. The amount that it may vary is defined by the
 * volatility.
 * 
 */
public class LinearProfitModifier implements ProfitModifier {

	public static final String NAME = "linear";

	Volatility volatility = Volatility.MODERATE;

	Random randomModifier = new Random();

	@Override
	public long adjustProfit(EventType eventType, long initialProfit) {
		long modifier = 0;
		switch (eventType) {
		case CATASTROPHIC:
			modifier = -200;
			break;
		case TERRIBLE:
			modifier = -100;
			break;
		case POOR:
			modifier = -50;
			break;
		case AVERAGE:
			modifier = 1;
			break;
		case GOOD:
			modifier = 50;
			break;
		case GREAT:
			modifier = 100;
			break;
		case EXTRAORDINARY:
			modifier = 200;
			break;
		}

		int possibleDeviation = 0;

		switch (volatility) {
		case LOWEST:
			possibleDeviation = 1;
			break;
		case LOW:
			possibleDeviation = 10;
			break;
		case MODERATE:
			possibleDeviation = 25;
			break;
		case HIGH:
			possibleDeviation = 50;
			break;
		case EXTREME:
			possibleDeviation = 100;
			break;
		case RIDICULOUS:
			possibleDeviation = 200;
			break;
		}

		// Create a random deviation...
		long deviation = randomModifier.nextInt(possibleDeviation);
		long modifierDeviation = (modifier * deviation) / 100;

		modifier = modifier - modifierDeviation;
		long profit = initialProfit + (initialProfit * modifier / 100);
		return profit;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setVolatility(Volatility volatility) {
		this.volatility = volatility;
	}

	public static void main(String argv[]) {
		System.out.println("MODIFY - Linear Style!");
		LinearProfitModifier modifier = new LinearProfitModifier();
		int initialProfit = 10000;

		System.out.println("Initial profit: " + initialProfit);
		for (EventType eventType : EventType.values()) {
			System.out.println(eventType);
			for (Volatility volatility : Volatility.values()) {
				System.out.print(volatility + "\t");
			}
			System.out.println();
			for (int i = 0; i < 10; i++) {
				for (Volatility volatility : Volatility.values()) {
					modifier.setVolatility(volatility);
					long profit = initialProfit;
					profit = modifier.adjustProfit(eventType, profit);
					System.out.print(profit);
					System.out.print("\t");
				}
				System.out.println();
			}
		}
	}
}
