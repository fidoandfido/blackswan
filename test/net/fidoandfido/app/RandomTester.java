package net.fidoandfido.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomTester {

	public static void main(String argv[]) {
		RandomTester tester = new RandomTester();
		tester.doTest();
		tester.printResults();
	}

	private Map<Integer, Integer> count = new HashMap<Integer, Integer>();

	private Random random = new Random();

	public RandomTester() {
		// Empty
	}

	public void printResults() {
		System.out.println(count);
	}

	public void doTest() {
		for (int i = 0; i < 531441; i++) {
			int j = 1;
			while (random.nextInt(3) != 0) {
				j++;
			}

			Integer numberOfRollsToHitZero = new Integer(j);
			Integer currentCount = count.get(numberOfRollsToHitZero);
			if (currentCount == null) {
				count.put(numberOfRollsToHitZero, new Integer(1));
			} else {
				Integer newCount = new Integer(currentCount.intValue() + 1);
				count.put(numberOfRollsToHitZero, newCount);
			}
		}
	}

}
