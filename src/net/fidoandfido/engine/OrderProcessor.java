package net.fidoandfido.engine;

import java.util.Collection;
import java.util.Date;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.TradeRecordDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.dao.TraderEventDAO;
import net.fidoandfido.engine.ExperiencePointGenerator.ExperienceEvent;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.Order.OrderType;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.TradeRecord;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderEvent;

import org.apache.log4j.Logger;

public class OrderProcessor {

	Logger logger = Logger.getLogger(getClass());

	private static final int MARKET_MAKER_DELTA_PERCENT = 10;

	private TraderDAO traderDAO;
	private ShareParcelDAO shareParcelDAO;
	private CompanyDAO companyDAO;
	private OrderDAO orderDAO;
	private TradeRecordDAO tradeRecordDAO;
	private TraderEventDAO traderEventDAO;

	private void initDAOs() {
		traderDAO = new TraderDAO();
		shareParcelDAO = new ShareParcelDAO();
		companyDAO = new CompanyDAO();
		orderDAO = new OrderDAO();
		tradeRecordDAO = new TradeRecordDAO();
		traderEventDAO = new TraderEventDAO();
	}

	public OrderProcessor() {
		initDAOs();
	}

	/**
	 * Given a new order, process it!
	 * 
	 * Note, this will save and update objects DAO methods, but will NOT manage
	 * transactions - this should be done by calling code.
	 * 
	 * @param buyOrder
	 */
	public void processOrder(Order order) {
		// Make sure the company that this order is for is still trading!
		if (!order.getCompany().isTrading()) {
			return;
		}

		// ascertain whether this is a buy or sell order.
		if (order.getOrderType().equals(Order.OrderType.BUY)) {
			logger.info("Processing buy order");
			Order buyOrder = order;
			// its a buy -- ensure that the buyer can make the purchase. If not,
			// return.
			if (!validateBuyer(buyOrder)) {
				return;
			}

			// Now look for all active sell orders for this company.
			Collection<Order> sellOrders = orderDAO.getOpenOrders(Order.OrderType.SELL, buyOrder.getCompany());
			for (Order sellOrder : sellOrders) {
				if (canMatchOrder(buyOrder, sellOrder)) {
					executeTrade(buyOrder, sellOrder);
					if (buyOrder.getRemainingShareCount() == 0) {
						break;
					}
				}
			}
		} else if (order.getOrderType().equals(Order.OrderType.SELL)) {
			logger.info("Processing sell order");
			// its a sell -- ensure that the buyer can make the purchase.
			Order sellOrder = order;
			if (!validateSeller(sellOrder)) {
				return;
			}
			// Now look for all active sell orders for this company.
			Collection<Order> buyOrders = orderDAO.getOpenOrders(Order.OrderType.BUY, sellOrder.getCompany());
			for (Order buyOrder : buyOrders) {
				if (canMatchOrder(buyOrder, sellOrder)) {
					executeTrade(buyOrder, sellOrder);
					if (sellOrder.getRemainingShareCount() == 0) {
						break;
					}
				}
			}
		}

		if (order.getRemainingShareCount() != 0) {
			// See if the MarketMaker wants to trade
			Trader marketMaker = traderDAO.getMarketMaker();
			if (order.getTrader().equals(marketMaker)) {
				logger.info("Market maker can't sell to itself");
				return;
			}
			logger.info("Market maker attempting to trade");
			long offerPrice = order.getOfferPrice();
			Company company = order.getCompany();
			Trader trader = order.getTrader();
			// Market maker will accept all reasonable offers --- and any offers
			// from ai :)

			if (!trader.isAITrader()) {
				if (order.getOrderType().equals(OrderType.BUY)) {
					// For humans, to buy the offer price must be at least the
					// last market trade
					if (offerPrice < company.getLastTradePrice()) {
						return;
					}
				} else {
					// For humans, to sell the offer price must be at most the
					// last market trade
					if (offerPrice > company.getLastTradePrice()) {
						return;
					}
				}
			}
			// Okay, the price is close enough, lets keep going
			long shareCount = order.getRemainingShareCount();
			Order marketMakerOrder = null;
			if (order.getOrderType().equals(OrderType.BUY)) {
				// If they are trying to buy, make sure we have some shares
				// to sell.
				ShareParcel mmHoldings = shareParcelDAO.getHoldingsByTraderForCompany(marketMaker, company);
				if (mmHoldings != null) {
					// Make sure we have enough shares...
					if (mmHoldings.getShareCount() <= shareCount && order.isAllowPartialOrder()) {
						// Not enough - buy as many as we can...
						shareCount = mmHoldings.getShareCount();
					} else if (mmHoldings.getShareCount() <= shareCount) {
						// Not enough, and no partial buying allowed!
						shareCount = 0;
					}
					if (shareCount != 0) {
						logger.info("Market maker is selling some shares...");
						marketMakerOrder = new Order(marketMaker, company, shareCount, offerPrice, OrderType.SELL);
						orderDAO.saveOrder(marketMakerOrder);
					}
				}
			} else {
				logger.info("Market maker is buying some shares....");
				// Market maker can *always* buy shares :)
				marketMakerOrder = new Order(marketMaker, company, shareCount, offerPrice, OrderType.BUY);
				orderDAO.saveOrder(marketMakerOrder);
			}
			if (marketMakerOrder != null) {
				executeTrade(order, marketMakerOrder);
			}
		}
	}

	private boolean validateBuyer(Order buyOrder) {
		Trader buyer = buyOrder.getTrader();
		long orderValue = buyOrder.getOfferPrice() * buyOrder.getRemainingShareCount();
		if (buyer == null) {
			throw new IllegalStateException("Somehow the buyer in validate buyer is null");
		}
		if (!buyer.canMakeTrade(orderValue)) {
			return false;
		}
		return true;
	}

	private boolean validateSeller(Order sellOrder) {
		Trader seller = sellOrder.getTrader();
		ShareParcel parcel = shareParcelDAO.getHoldingsByTraderForCompany(seller, sellOrder.getCompany());
		if (parcel == null) {
			return false;
		}
		if (parcel.getShareCount() < sellOrder.getRemainingShareCount()) {
			return false;
		}
		return true;
	}

	/**
	 * Execute the trade
	 * 
	 * @param buyOrder
	 * @param buyer
	 * @param sellOrder
	 * @return true if the trade was executed, false otherwise
	 */
	private boolean executeTrade(Order firstOrder, Order secondOrder) {
		logger.info("Executing trade");
		// Guess the order types.
		Order buyOrder = null;
		Order sellOrder = null;
		// See if we got it right
		if (firstOrder.getOrderType().equals(OrderType.BUY)) {
			buyOrder = firstOrder;
			sellOrder = secondOrder;
		} else {
			buyOrder = secondOrder;
			sellOrder = firstOrder;
		}
		// Validate the order types.
		if (buyOrder.getOrderType().equals(OrderType.SELL) || (sellOrder.getOrderType().equals(OrderType.BUY))) {
			// throw new IllegalArgumentException(
			// "Must have a buy and a sell order to execute a trade!");
			logger.error("Invalid order types!");
			return false;
		}

		Trader seller = sellOrder.getTrader();
		Trader buyer = buyOrder.getTrader();

		if (seller.equals(buyer)) {
			logger.info("Can't sell to yourself! " + buyer.getName());
		}

		// Validate the buyer can afford the purchase, and the seller has the
		// shares
		if (!validateBuyer(buyOrder)) {
			logger.info("bad buyer");
			return false;
		}
		if (!validateSeller(sellOrder)) {
			logger.info("bad seller");
			return false;
		}

		ShareParcel sellerParcel = shareParcelDAO.getHoldingsByTraderForCompany(sellOrder.getTrader(), sellOrder.getCompany());

		// Share count will be the minimum of the two order's remaining share
		// count
		long shareCount = buyOrder.getRemainingShareCount() < sellOrder.getRemainingShareCount() ? buyOrder.getRemainingShareCount() : sellOrder
				.getRemainingShareCount();

		// Price is always based on the buyer's offer
		long saleAmount = shareCount * buyOrder.getOfferPrice();

		Date date = new Date();
		if (!buyer.isMarketMaker()) {
			TraderEvent event = new TraderEvent(buyer, TraderEvent.BUY_SHARES_PAYMENT, date, buyOrder.getCompany(), shareCount, saleAmount * -1,
					buyer.getCash(), buyer.getCash() - saleAmount);
			traderEventDAO.saveTraderEvent(event);
		}
		if (!seller.isMarketMaker()) {
			TraderEvent event = new TraderEvent(seller, TraderEvent.SELL_SHARES_PAYMENT, date, buyOrder.getCompany(), shareCount, saleAmount, buyer.getCash(),
					buyer.getCash() + saleAmount);
			traderEventDAO.saveTraderEvent(event);
		}

		ExperiencePointGenerator generator = new ExperiencePointGenerator();
		generator.addExperiencePoints(buyer, ExperienceEvent.BUY_SHARES, saleAmount);
		generator.addExperiencePoints(seller, ExperienceEvent.SELL_SHARES, saleAmount);

		buyer.takeCash(saleAmount);
		seller.giveCash(saleAmount);
		traderDAO.saveTrader(seller);
		traderDAO.saveTrader(buyer);

		sellerParcel.removeShares(shareCount);
		if (sellerParcel.getShareCount() == 0) {
			shareParcelDAO.deleteShareParcel(sellerParcel);
		} else {
			shareParcelDAO.saveShareParcel(sellerParcel);
		}

		ShareParcel buyerParcel = shareParcelDAO.getHoldingsByTraderForCompany(buyer, buyOrder.getCompany());

		// Now - create the records for the buyer and seller.
		Date executedDate = new Date();
		TradeRecord txRecord = new TradeRecord(buyer, seller, buyOrder.getCompany(), shareCount, buyOrder.getOfferPrice(), executedDate);

		// Update the orders
		Company company = buyOrder.getCompany();

		company.setLastTradeChange(buyOrder.getOfferPrice() - company.getLastTradePrice());
		company.setLastTradePrice(buyOrder.getOfferPrice());

		setOrderExecuted(sellOrder, executedDate);
		setOrderExecuted(buyOrder, executedDate);

		if (buyerParcel == null) {
			buyerParcel = new ShareParcel(buyer, shareCount, buyOrder.getCompany(), buyOrder.getOfferPrice());
		} else {
			buyerParcel.addShares(shareCount, buyOrder.getOfferPrice());
		}

		// DAO methods to save everything...
		companyDAO.saveCompany(company);
		shareParcelDAO.saveShareParcel(buyerParcel);
		tradeRecordDAO.saveTradeRecord(txRecord);
		orderDAO.saveOrder(sellOrder);
		orderDAO.saveOrder(buyOrder);
		logger.info("Trade executed!");
		return true;
	}

	/**
	 * See if we these two orders will allow them to be fullfilled
	 * 
	 * @param buyOrder
	 *            The buy Order
	 * @param sellOrder
	 *            The sell order
	 * @return true if and only if the two orders can fullfill each other.
	 */
	private boolean canMatchOrder(Order buyOrder, Order sellOrder) {
		// Given a buy and a sell order, see if the sell order can match the buy
		// order.
		if (buyOrder.getOfferPrice() >= sellOrder.getOfferPrice()) {
			if (buyOrder.getRemainingShareCount() == sellOrder.getRemainingShareCount()) {
				return true;
			}
			if (buyOrder.getRemainingShareCount() < sellOrder.getRemainingShareCount() && sellOrder.isAllowPartialOrder()) {
				return true;
			}
			if (sellOrder.getRemainingShareCount() < buyOrder.getRemainingShareCount() && buyOrder.isAllowPartialOrder()) {
				return true;
			}
		}
		return false;
	}

	private void setOrderExecuted(Order order, Date executedDate) {
		order.setExecuted(true);
		order.setActive(false);
		order.setDateExecuted(executedDate);
	}

}
