<%@page import="net.fidoandfido.dao.HibernateUtil"%>
<%@page import="net.fidoandfido.model.User"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.List"%>
<%@page import="java.util.Collection"%>
<%@page import="net.fidoandfido.dao.CompanyDAO"%>
<%@page import="net.fidoandfido.dao.ShareParcelDAO"%>
<%@page import="net.fidoandfido.dao.StockExchangeDAO"%>
<%@page import="net.fidoandfido.dao.TraderDAO"%>
<%@page import="net.fidoandfido.model.Company" %>
<%@page import="net.fidoandfido.model.Order"%>
<%@page import="net.fidoandfido.model.ShareParcel"%>
<%@page import="net.fidoandfido.model.StockExchange"%>
<%@page import="net.fidoandfido.model.Trader"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<%@page session="true" %>

<html>
<%@ include file="webTemplates/header.txt" %>
<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>

<div id="page">

<%
	HibernateUtil.beginTransaction();
	User user = User.getSuperUser();
	Trader trader = null;
	//Trader trader = TraderDAO.getTraderByUser(user);
%>
	

	<div id="content">
		<div class="post">
			<h2 class="title">Traders</h2>
			<div class="entry">
<%
	TraderDAO traderDAO = new TraderDAO();
	Iterable<Trader> traders = traderDAO.getTraderList();
		if (traders != null && traders.iterator().hasNext()) {
%>
				<ul><!-- List of traders -->
<%
	for (Trader currentTrader: traders) {
%>
				<li>
					Trader Name: <%=currentTrader.getName()%><br/>
					Trader cash: <%=currentTrader.getCash()%><br/>
<%
	ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
	Iterable<ShareParcel> holdings = shareParcelDAO.getHoldingsByTrader(currentTrader);
		if (holdings != null && holdings.iterator().hasNext()) {
%>
					Holdings:<br>
					<ul>
<%
	for (ShareParcel shareParcel : holdings) {
%>
						<li><%=shareParcel.getShareCount()%> of <%=shareParcel.getCompany().getName()%></li>
<%
	}
%>	
					</ul>
<%
	} else {
%>
					No Holdings.
<%
	}
%>
				</li>
				<!--  Trader list item -->
<%
	}
%>
				</ul>
				<!--  list of traders -->
<%
	} else {
		// No Traders!?!
%>
				No Traders in data store!<br/>
<%
	}
%>
			</div>
			<!-- entry (Traders)-->
		</div>
		<!--  Post (Traders) -->

		<div class="post">
			<h2 class="title">Stock Exchanges</h2>
			<div class="entry">
<%
	StockExchangeDAO exchangeDAO = new StockExchangeDAO();
	Collection<StockExchange> stockExhExchanges = exchangeDAO.getStockExchangeList();
	if (stockExhExchanges != null && stockExhExchanges.size() != 0) {
%>
			<ul>
<%
	for (StockExchange stockExchange: stockExhExchanges) {
%>
			<li>
				Stock Exchange Name: <%=stockExchange.getName()%><br/>
			</li>
<%
	}
%>
			</ul>
<%
	} else {
%>
	No Exchanges in database!<br/>
<%
	}
%>
			</div>
			<!-- entry (Exchange)-->
		</div>
		<!--  Post (Exchange) -->


		<div class="post">
			<h2 class="title">Companies</h2>
			<div class="entry">
<%
	CompanyDAO companyDAO = new CompanyDAO();
	Collection<Company> companyList = companyDAO.getCompanyList();
	if (companyList != null && companyList.size() != 0) {
%>
		<ul>
<%
	for (Company company : companyList) {
%>
			<li>
			Company Name: <%=company.getName()%><br/>
			Company Code: <%=company.getCode()%><br/>
			Company Sector: <%=company.getSector()%><br/>
			Company Outstanding shares: <%=company.getOutstandingShares()%><br/>
			Company Exchange: <%=company.getStockExchange().getName()%>
			</li>
<%
	}
%>	
		</ul>
<%
	} else {
		// Empty...
%>
	No Companies in database!<br/>
<%
	}
%>

			</div>
			<!-- entry (Companies)-->
		</div>
		<!--  Post (Companies) -->

<%-- 
		<div class="post">
			<h2 class="title">Orders</h2>
			<div class="entry">
	
<%
		Collection<Order> orderList = OrderDAO.getAllOrders();
		if (orderList != null && orderList.size() != 0) {
	%>	
	<ul>
<%
	for (Order order : orderList) {
%>
		<li><%=TraderDAO.getTrader(order.getTrader()).getName()%> <%=order.getOrderType() == Order.OrderType.BUY ? " buying " : " selling "%><%=order.getRemainingShareCount()%> of <%=CompanyDAO.getCompanyByKey(order.getCompanyKey()).getName()%> at $<%=order.getOfferPrice()%> <%=order.isActive() ? "Active" : "Not active"%> and <%=order.isExecuted() ? " Executed " : " Not executed."%></li>
<%
	}
%>
	</ul>
<%
	} else {
%>
		No orders in data store.
<%
	}
%>
			</div>
			<!-- entry (Orders)-->
		</div>
		<!--  Post (Orders) -->
--%>
	</div>
	<!-- end #content -->

<%=WebPageUtil.generateSideBar(trader, user)%>
	<div style="clear: both;">&nbsp;</div>
</div>
<!--  end page -->

<%@ include file="webTemplates/footer.txt" %>

</body>
</html>
<%
HibernateUtil.commitTransaction();
%>