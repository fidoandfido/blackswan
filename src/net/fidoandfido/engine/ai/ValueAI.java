package net.fidoandfido.engine.ai;

import java.util.List;
import java.util.Random;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Trader;

public class ValueAI extends AITrader {

	public static final String Name = "Value";
	private static final int COMPANIES_TO_BUY = 20;

	@Override
	public String getName() {
		return Name;
	}

	@Override
	public void performTrades(Trader trader) {
		// So basically, we are going to get the companies, look at their
		// earnings per share,
		// We are looking at the book value + the current earning % as a
		// premium, minimum price is the book value.
		CompanyDAO companyDAO = new CompanyDAO();
		List<Company> companyList = companyDAO.getCompanyList();
		Random companyRandom = new Random();
		for (int i = 0; i < COMPANIES_TO_BUY; i++) {
			int index = companyRandom.nextInt(companyList.size());
			Company company = companyList.get(index);
			companyList.remove(index);
			if (company.isTrading() == false) {
				continue;
			}

			// Iterable<Company> companies = companyDAO.getCompanyList();
			// for (Company company : companies) {

			if (company.getStockExchange().isUpdating()) {
				continue;
			}
			if (company.isTrading() == false) {
				continue;
			}
			long bookValue = company.getShareBookValue();
			long sharePrice = company.getLastTradePrice();
			long expectedEarning = company.getExpectedEarningsPerShare();
			long priceToEarningsRate = (expectedEarning * 100 / sharePrice);

			// bookvalue = 1000
			// p2e = 5
			// fp = ((1000 * (100 + 5)) / 100) = 1050 <- correct!
			long fairPrice = ((bookValue * (100 + priceToEarningsRate)) / 100);
			if (fairPrice < bookValue) {
				fairPrice = bookValue;
			}
			if (fairPrice > sharePrice) {
				// This one is a buy!
				// Make the offer price at least half way between the two
				long halfwayPoint = ((fairPrice - sharePrice) / 2) + sharePrice;
				long askingPrice = adjustPrice(sharePrice, GOOD_BUY_RATE);
				if (askingPrice < halfwayPoint) {
					askingPrice = halfwayPoint;
				}
				buy(trader, company, askingPrice, DEFAULT_BUY_COUNT);
			} else {
				// time to sell!
				long halfwayPoint = ((sharePrice - fairPrice) / 2) + fairPrice;
				long askingPrice = adjustPrice(sharePrice, BAD_SELL_RATE);
				if (askingPrice < halfwayPoint) {
					askingPrice = halfwayPoint;
				}
				sell(trader, company, askingPrice, DEFAULT_SELL_COUNT);
			}
		}
	}
}
