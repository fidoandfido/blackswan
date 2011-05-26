package net.fidoandfido.engine.ai;

import java.util.Date;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.TradeRecordDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Order.OrderType;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.TradeRecord;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;

import org.apache.log4j.Logger;

public class AITradeExecutor {

	Logger logger = Logger.getLogger(getClass());

	private OrderDAO orderDAO = new OrderDAO();
	private ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
	private TraderDAO traderDAO = new TraderDAO();
	private TradeRecordDAO tradeRecordDAO = new TradeRecordDAO();
	private CompanyDAO companyDAO = new CompanyDAO();

	private TraderEventDAO traderEventDAO = new TraderEventDAO();

	// Execute a buy.
	// Return as soon as something goes wrong!
	public void executeBuy(Trader trader, Company company, long suppliedPrice, long preferredShareCount) {
		// We are going to buy some shares!
		if (!company.isTrading()) {
			logger.warn("Trader attempting to trade non-trading company! co:" + company.getName() + " trader: " + trader.getName());
			return;
		}
		Trader marketMaker = traderDAO.getMarketMaker();
		if (trader.equals(marketMaker)) {
			logger.warn("Market maker was attempting to execute a trade!");
			return;
		}

		long price = suppliedPrice;
		if (price < 10) {
			price = 10;
		}
		logger.info("AI Buying shares:  " + trader.getAiStrategyName() + " co: " + company.getName() + " at: " + price);
		long cash = trader.getCash();
		if (cash < price) {
			logger.info("AI can't affor shares in this company (" + company.getName() + ") AI: " + trader.getName() + " Cash: " + trader.getCash());
		}

		long shareCount = cash / price;
		if (shareCount > preferredShareCount) {
			shareCount = preferredShareCount;
		}
		if (shareCount < 10) {
			logger.info("Too few shares to buy at supplied price: " + price + " trader cash: " + trader.getCash());
			return;
		}

		logger.info("ai: " + trader.getAiStrategyName() + "(" + trader.getName() + ") buys " + shareCount + " of " + company.getName() + " at " + price);
		Order buyOrder = new Order(trader, company, shareCount, price, Order.OrderType.BUY);
		orderDAO.saveOrder(buyOrder);

		// At this point we will see if market maker can match the order.
		// If not - the order will simply stay open (to be executed later or cancelled)
		ShareParcel mmHoldings = shareParcelDAO.getHoldingsByTraderForCompany(marketMaker, company);
		if (mmHoldings == null || mmHoldings.getShareCount() < shareCount) {
			logger.info("AI attempting to buy but Market Maker does not have enought shares.");
			return;
		}
		logger.info("Market maker is selling some shares...");
		Order marketMakerOrder = new Order(marketMaker, company, shareCount, price, OrderType.SELL);
		orderDAO.saveOrder(marketMakerOrder);

		Date date = new Date();
		long saleAmount = shareCount * price;
		TraderEvent event = new TraderEvent(trader, TraderEvent.BUY_SHARES_PAYMENT, date, buyOrder.getCompany(), shareCount, saleAmount * -1, trader.getCash(),
				trader.getCash() - saleAmount);
		traderEventDAO.saveTraderEvent(event);

		// Don't do a trader event for the market maker!
		trader.takeCash(saleAmount);
		marketMaker.giveCash(saleAmount);
		traderDAO.saveTrader(trader);
		traderDAO.saveTrader(marketMaker);

		// Update and then save the share parcels.
		mmHoldings.removeShares(shareCount);
		if (mmHoldings.getShareCount() == 0) {
			shareParcelDAO.deleteShareParcel(mmHoldings);
		} else {
			shareParcelDAO.saveShareParcel(mmHoldings);
		}
		ShareParcel buyerParcel = shareParcelDAO.getHoldingsByTraderForCompany(trader, company);
		if (buyerParcel == null) {
			buyerParcel = new ShareParcel(trader, shareCount, company, price);
		} else {
			buyerParcel.addShares(shareCount, price);
		}
		shareParcelDAO.saveShareParcel(buyerParcel);

		// Create the trade records for the AI
		TradeRecord txRecord = new TradeRecord(trader, marketMaker, company, shareCount, price, date);
		tradeRecordDAO.saveTradeRecord(txRecord);

		// Update and save the company
		company.setLastTradeChange(buyOrder.getOfferPrice() - company.getLastTradePrice());
		company.setLastTradePrice(buyOrder.getOfferPrice());
		companyDAO.saveCompany(company);

		// DAO methods to save everything...
		buyOrder.executeOrder(date);
		marketMakerOrder.executeOrder(date);
		orderDAO.saveOrder(marketMakerOrder);
		logger.info("Trade executed!");

	}

	public void executeSell(Trader trader, Company company, long suppliedPrice, long preferredShareCount) {
		if (!company.isTrading()) {
			logger.warn("Trader attempting to trade non-trading company! co:" + company.getName() + " trader: " + trader.getName());
			return;
		}

		Trader marketMaker = traderDAO.getMarketMaker();
		if (trader.equals(marketMaker)) {
			logger.warn("Market maker was attempting to execute a trade!");
			return;
		}

		ShareParcel parcel = shareParcelDAO.getHoldingsByTraderForCompany(trader, company);
		if (parcel == null || parcel.getShareCount() == 0) {
			logger.info("Attempting to sell shares that trader doesn't own! Trader:" + trader.getName() + " Company: " + company.getName());
			return;
		}
		long shareCount = preferredShareCount;
		if (shareCount > parcel.getShareCount()) {
			shareCount = parcel.getShareCount();
		}

		if (shareCount == 0) {
			logger.warn("Attempting to sell 0 shares! Trader: " + trader.getName() + " company: " + company.getName());
			return;
		}

		// Can't sell for 0 cents!
		long price = suppliedPrice;
		if (price == 0) {
			price = 1;
		}
		logger.info("ai: " + trader.getAiStrategyName() + "(" + trader.getName() + ") sells " + shareCount + " of " + company.getName() + " at " + price);
		Order sellOrder = new Order(trader, company, shareCount, price, Order.OrderType.SELL);
		orderDAO.saveOrder(sellOrder);

		// Market maker will buy these shares. (it always does!)
		// At this point we will see if market maker can fullfill the order.
		logger.info("Market maker is buying some shares...");
		Order marketMakerOrder = new Order(marketMaker, company, shareCount, price, OrderType.BUY);
		orderDAO.saveOrder(marketMakerOrder);

		Date date = new Date();
		long saleAmount = shareCount * price;
		TraderEvent event = new TraderEvent(trader, TraderEvent.SELL_SHARES_PAYMENT, date, sellOrder.getCompany(), shareCount, saleAmount * -1,
				trader.getCash(), trader.getCash() + saleAmount);
		traderEventDAO.saveTraderEvent(event);

		// Don't do a trader event for the market maker!

		trader.giveCash(saleAmount);
		marketMaker.takeCash(saleAmount);
		traderDAO.saveTrader(trader);
		traderDAO.saveTrader(marketMaker);

		// Update and then save the share parcels.
		ShareParcel mmHoldings = shareParcelDAO.getHoldingsByTraderForCompany(marketMaker, company);
		if (mmHoldings == null) {
			mmHoldings = new ShareParcel(trader, shareCount, company, price);
		} else {
			mmHoldings.addShares(shareCount, price);
		}
		shareParcelDAO.saveShareParcel(mmHoldings);

		ShareParcel sellerParcel = shareParcelDAO.getHoldingsByTraderForCompany(trader, company);
		sellerParcel.removeShares(shareCount);
		if (sellerParcel.getShareCount() == 0) {
			shareParcelDAO.deleteShareParcel(sellerParcel);
		} else {
			shareParcelDAO.saveShareParcel(sellerParcel);
		}

		// Now - create the records for the buyer and seller.
		TradeRecord txRecord = new TradeRecord(trader, marketMaker, company, shareCount, price, date);
		tradeRecordDAO.saveTradeRecord(txRecord);

		// Update and save the company
		company.setLastTradeChange(price - company.getLastTradePrice());
		company.setLastTradePrice(price);
		companyDAO.saveCompany(company);

		// Save the orders...
		sellOrder.executeOrder(date);
		marketMakerOrder.executeOrder(date);
		orderDAO.saveOrder(marketMakerOrder);
		orderDAO.saveOrder(sellOrder);

		logger.info("Trade executed!");
	}
}
