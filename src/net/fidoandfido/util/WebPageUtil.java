package net.fidoandfido.util;

import java.text.NumberFormat;
import java.util.Date;

import net.fidoandfido.model.Trader;
import net.fidoandfido.model.User;

public class WebPageUtil {

	public static String newLine = System.getProperty("line.separator");

	public static String HOME = "/myapp/Welcome.jsp";

	public static final String DEFAULT_PAGE_TITLE = "Black Swan Trading";

	private static NumberFormat nf = NumberFormat.getCurrencyInstance();
	
	public static String formatCurrency(long valueParm) {
		double value = valueParm;
		value = value / 100;
	    return (nf.format(value));
	}
	
	public static String formatCurrencyByHand(long valueParm) {
		StringBuilder retVal = new StringBuilder();
		retVal.append("$");
		long value = valueParm;
		if (value < 0) {
			retVal.append("-");
			value = value * -1;
		}
		if (value < 100 && value > -100) {
			retVal.append("0.");
			retVal.append(value);
		} else if (value < 100000 && value > -100000){
			// Thousands - show all
			retVal.append(value / 100);
			retVal.append(".");
			retVal.append(value % 100);
		} else if (value < 100000000 && value > -100000000){
			// Hundred of thousands - strip the cents
			retVal.append(value / 100);
		} else if (value < 100000000000L && value > -100000000000L){
			// Millions - strip the cents
			 retVal.append(value / 100);
		} else {
			retVal.append(value / 100000000);
			retVal.append("M");
		}
		return retVal.toString();
	}
	
	public static String generateSideBar(Trader trader, User user) {
		StringBuilder retval = new StringBuilder();
		if (trader != null) {
			retval.append("	<div id=\"sidebar\">");
			retval.append("		<ul>");
			retval.append("			<li>");
			retval.append("			<h2>Trader Status</h2>");
			retval.append("			<ul>");
			retval.append("				<li>Name: " + trader.getName() + "</li>");
			retval.append("				<li>Availabale Cash: " + formatCurrency(trader.getCash()) + "</li>");
			retval.append("			</ul>");
			retval.append("			</li>");
			retval.append("			<li>");
			retval.append("			<h2>Links</h2>");
			retval.append("			<ul>");
			retval.append("				<li><a href=\"/myapp/Trader.jsp\">Trader Information</a></li>");
			retval.append("				<li><a href=\"/myapp/Companies.jsp\">Companies</a></li>");
			retval.append("				<li><a href=\"/myapp/BuyShares.jsp\">Buy Shares</a></li>");
			retval.append("				<li><a href=\"/myapp/SellShares.jsp\">Sell Shares</a></li>");
			retval.append("				<li>USER LINK</li>");
			retval.append("				<li>USER LINK</li>");
			retval.append("				<li>USER LINK</li>");
			// retval.append("				<li><a href=\"/SellShares.jsp\">Sell some shares...</a></li>");
			// retval.append("				<li><a href=\"/BuyShares.jsp\">Buy some shares...</a></li>");
			if (user != null && user.isUserAdmin()) {
				// Show admin links
				retval.append("				<li><a href=\"/myapp/ShowData.jsp\">All Data</a></li>");
				retval.append("				<li>ADMIN LINK</li>");
				retval.append("				<li>ADMIN LINK</li>");
				retval.append("				<li>ADMIN LINK</li>");
				// retval.append("				<li><a href=\"/init\">Initialise Application</a></li>");
				// retval.append("				<li><a href=\"/periodStart?exchange=asx\">Period Start For ASX</a></li>");
			}
			retval.append("			</ul>");
			retval.append("			</li>");
			retval.append("			<li>");
			retval.append("			<h2>Server Time</h2>");
			Date date = new Date();
			retval.append(date.toString());
			retval.append("			</li>");
			retval.append("		</ul>");
			retval.append("	</div>");
		}
		return retval.toString();
	}
}
