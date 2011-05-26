package net.fidoandfido.engine.ai;

import java.util.List;
import java.util.Random;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.Trader;

public class LiquididatingAI extends AITrader {

	public static final String NAME = "Liquidating";
	private static final long SHARE_COUNT = 2000;
	private static final int COMPANIES_TO_BUY = 20;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void performTrades(Trader trader) {
		ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
		CompanyDAO companyDAO = new CompanyDAO();
		Iterable<ShareParcel> holdings = shareParcelDAO.getHoldingsByTrader(trader);
		for (ShareParcel shareParcel : holdings) {
			// We are going to sell all our shares!
			long shareCount = shareParcel.getShareCount();
			Company company = shareParcel.getCompany();
			// Since we are selling, drop the price by the sell rate
			long askingPrice = adjustPrice(company.getLastTradePrice(), SELL_RATE);
			sell(trader, company, askingPrice, shareCount);

		}
		// Now try to buy 1000 shares in 20 companies... (skip over the
		// non-traders though.
		List<Company> companyList = companyDAO.getCompanyList();
		Random companyRandom = new Random();
		for (int i = 0; (i < COMPANIES_TO_BUY) && (companyList.size() > 0); i++) {
			int index = companyRandom.nextInt(companyList.size());
			Company company = companyList.get(index);
			companyList.remove(index);
			while (company.isTrading() == false) {
				// While isTrading is false, keep getting new ones...
				index = companyRandom.nextInt(companyList.size());
				company = companyList.get(index);
				companyList.remove(index);
			}
			long askingPrice = adjustPrice(company.getLastTradePrice(), BUY_RATE);
			buy(trader, company, askingPrice, SHARE_COUNT);
		}

	}
}
