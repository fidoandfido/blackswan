package net.fidoandfido.engine;

import java.util.Collection;
import java.util.Date;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.OrderDAO;
import net.fidoandfido.dao.ShareParcelDAO;
import net.fidoandfido.dao.TradeRecordDAO;
import net.fidoandfido.dao.TraderDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.Order;
import net.fidoandfido.model.ShareParcel;
import net.fidoandfido.model.TradeRecord;
import net.fidoandfido.model.Trader;

public class OrderProcessor {

	/**
	 * Given a new order, process it!
	 * 
	 * Note, this will save and update objects DAO methods, but will NOT manage transactions - this should be done by
	 * calling code.
	 * 
	 * @param buyOrder
	 */
	public void processOrder(Order order) {
		// ascertain whether this is a buy or sell order.
		if (order.getOrderType().equals(Order.OrderType.BUY)) {
			Order buyOrder = order;
			// its a buy - look for Sell orders that match.
			// First things first -- ensure that the buyer can make the purchase. If not, return.
			if (!validateBuyer(buyOrder)) {
				return;
			}

			// Now look for all active sell orders for this company.
			Collection<Order> sellOrders = OrderDAO.getOpenOrders(Order.OrderType.SELL, buyOrder.getCompany());
			for (Order sellOrder : sellOrders) {
				if (canMatchOrder(buyOrder, sellOrder)) {
					// We got one!
					executeTrade(buyOrder, sellOrder);

					if (buyOrder.getRemainingShareCount() == 0) {
						// We have completed this buy order!
						break;
					}
				}
			}
		} else if (order.getOrderType().equals(Order.OrderType.SELL)) {
			// its a buy - look for Sell orders that match.
			// First things first -- ensure that the buyer can make the purchase. If not, return.
			Order sellOrder = order;
			Collection<Order> buyOrders = OrderDAO.getOpenOrders(Order.OrderType.BUY, sellOrder.getCompany());
			for (Order buyOrder : buyOrders) {
				if (canMatchOrder(buyOrder, sellOrder)) {
					executeTrade(buyOrder, sellOrder);
					if (sellOrder.getRemainingShareCount() == 0) {
						break;
					}
				}
			}
		}
	}

	private boolean validateBuyer(Order buyOrder) {
		Trader buyer = buyOrder.getTrader();
		long orderValue = buyOrder.getOfferPrice() * buyOrder.getRemainingShareCount();
		if (!buyer.canMakeTrade(orderValue)) {
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
	private boolean executeTrade(Order buyOrder, Order sellOrder) {

		Trader seller = sellOrder.getTrader();
		Trader buyer = buyOrder.getTrader();

		// Validate the buyer can afford the purchase, and the seller has the shares
		if (!validateBuyer(buyOrder)) {
			return false;
		}

		ShareParcel sellerParcel = ShareParcelDAO.getHoldingsByTraderForCompany(sellOrder.getTrader(), sellOrder.getCompany());
		if (sellerParcel == null) {
			// D'oh!!! Check the next trader I guess :(
			return false;
		}

		// Share count will be the minimum of the two order's remaining share count
		long shareCount = buyOrder.getRemainingShareCount() < sellOrder.getRemainingShareCount() ? buyOrder.getRemainingShareCount() : sellOrder
				.getRemainingShareCount();

		// Price is always based on the buyer's offer
		long saleAmount = shareCount * buyOrder.getOfferPrice();

		buyer.takeCash(saleAmount);
		seller.giveCash(saleAmount);

		TraderDAO.saveTrader(seller);
		TraderDAO.saveTrader(buyer);

		sellerParcel.removeShares(shareCount);
		if (sellerParcel.getShareCount() == 0) {
			ShareParcelDAO.deleteShareParcel(sellerParcel);
		} else {
			ShareParcelDAO.saveShareParcel(sellerParcel);
		}

		ShareParcel buyerParcel = ShareParcelDAO.getHoldingsByTraderForCompany(buyer, buyOrder.getCompany());

		// Now - create the records for the buyer and seller.
		Date executedDate = new Date();
		TradeRecord txRecord = new TradeRecord(buyer, seller, buyOrder.getCompany(), shareCount, buyOrder.getOfferPrice(), executedDate);

		// Update the orders
		Company company = buyOrder.getCompany();
		company.setLastTradePrice(buyOrder.getOfferPrice());

		setOrderExecuted(sellOrder, executedDate);
		setOrderExecuted(buyOrder, executedDate);

		if (buyerParcel == null) {
			buyerParcel = new ShareParcel(buyer, shareCount, buyOrder.getCompany());
		} else {
			buyerParcel.addShares(shareCount);
		}

		// DAO methods to save everything...
		CompanyDAO.saveCompany(company);
		ShareParcelDAO.saveShareParcel(buyerParcel);
		TradeRecordDAO.saveTradeRecord(txRecord);
		OrderDAO.saveOrder(sellOrder);
		OrderDAO.saveOrder(buyOrder);
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
		// Given a buy and a sell order, see if the sell order can match the buy order.
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
