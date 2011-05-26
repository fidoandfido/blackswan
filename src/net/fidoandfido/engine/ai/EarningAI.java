package net.fidoandfido.engine.ai;

import java.util.List;
import java.util.Random;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Trader;

public class EarningAI extends AITrader {

	public static final String Name = "Earnings";

	private static final int COMPANIES_TO_BUY = 20;

	@Override
	public void performTrades(Trader trader) {
		// If it is above the cash rate - we buy.
		// If it is below, we sell.
		CompanyDAO companyDAO = new CompanyDAO();
		List<Company> companyList = companyDAO.getCompanyList();
		Random companyRandom = new Random();
		for (int i = 0; i < COMPANIES_TO_BUY; i++) {
			int index = companyRandom.nextInt(companyList.size());
			Company company = companyList.get(index);
			companyList.remove(index);

			// Iterable<Company> companies = companyDAO.getCompanyList();
			// for (Company company : companies) {
			if (company.getStockExchange().isUpdating()) {
				continue;
			}
			if (company.isTrading() == false) {
				continue;
			}

			long sharePrice = company.getLastTradePrice();
			long expectedEarning = company.getExpectedEarningsPerShare();
			long priceToEarningsRate = (expectedEarning * 100 / sharePrice);
			if (priceToEarningsRate > (company.getPrimeInterestRateBasisPoints() / 100)) {
				// This one is a buy!
				long delta = (priceToEarningsRate - (company.getPrimeInterestRateBasisPoints() / 100));
				if (delta > 5) {
					// More than 5 % spread between earnings and cash rate...
					sharePrice = adjustPrice(sharePrice, VERY_GOOD_BUY_RATE);
					buy(trader, company, sharePrice, DEFAULT_BUY_COUNT);
				} else {
					sharePrice = adjustPrice(sharePrice, GOOD_BUY_RATE);
					buy(trader, company, sharePrice, DEFAULT_BUY_COUNT);
				}
			} else {
				// time to sell!
				sharePrice = adjustPrice(sharePrice, SELL_RATE);
				sell(trader, company, sharePrice, DEFAULT_SELL_COUNT);
			}
		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Name;
	}

}
