package net.fidoandfido.util;

public class VerifyWebPageUtil {
	public static void main(String argv[]) {
		long value = 0;
		
		System.out.println("Small amounts");
		System.out.println(value + "\t" + WebPageUtil.formatCurrency(value));
		
		value = 10;
		showCurrency(value);
		
		value = 130;
		showCurrency(value);

		System.out.println("Hundred thousands");
		
		value = 24950000;
		showCurrency(value);

		value = 45959995;
		showCurrency(value);
		
		System.out.println("Millions");
		value = 250000000;
		showCurrency(value);

		value = 120549999;
		showCurrency(value);
				
	}
	
	private static void showCurrency(long value) {
		System.out.println(value + "\t" + WebPageUtil.formatCurrency(value));
		System.out.println(value + "\t" + WebPageUtil.formatCurrencyByHand(value));
		value = value * -1;
		System.out.println(value + "\t" + WebPageUtil.formatCurrency(value));
		System.out.println(value + "\t" + WebPageUtil.formatCurrencyByHand(value));
	}
}
