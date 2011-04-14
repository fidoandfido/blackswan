<%@page import="net.fidoandfido.model.TraderEvent"%>
<%@page import="net.fidoandfido.dao.TraderEventDAO"%>
<%@page import="net.fidoandfido.model.Order"%>
<%@page import="net.fidoandfido.dao.OrderDAO"%>
<%@page import="net.fidoandfido.dao.ShareParcelDAO"%>
<%@page import="net.fidoandfido.model.ShareParcel"%>
<%@page import="net.fidoandfido.model.UserSession"%>
<%@page import="net.fidoandfido.dao.UserSessionDAO"%>
<%@page import="net.fidoandfido.servlets.LogInServlet"%>
<%@page import="net.fidoandfido.servlets.RegisterServlet"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<%@page import="net.fidoandfido.model.User"%>
<%@page import="net.fidoandfido.dao.HibernateUtil"%>
<%@page import="java.util.List"%>
<%@page import="net.fidoandfido.dao.TraderDAO"%>
<%@page import="net.fidoandfido.model.Trader"%>
<%@page import="net.fidoandfido.util.Constants"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%	
	HibernateUtil.beginTransaction();
	User user = null;
	Trader trader = null;

	UserSession userSession = UserSessionDAO.getUserSessionBySessionId(request.getSession().getId());

	if (userSession != null && userSession.isActive()) {
		user = userSession.getUser();
		trader = user.getTrader();
	}
	
	if (user == null || trader == null) {
		response.sendRedirect("/myapp/Welcome.jsp");
		return;
	}
	
	
%>

<html>
<%@ include file="webTemplates/header.txt" %>
<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>

<div id="page">
	<div id="content">

			<div class="post">
				<h2 class="title">Trader Page - <%= trader.getName() %></h2>
				<div class="entry">
				<p>Hello <%= user.getUserName() %> and Welcome to the new Black Swan application!</p>
				<p>
				Current balance: <%= WebPageUtil.formatCurrency(trader.getCash()) %><br/>
				Available balance: <%= WebPageUtil.formatCurrency(trader.getCash()) %><br/>
<%
	long totalValue = trader.getCash();
	Iterable<ShareParcel> shareParcels = ShareParcelDAO.getHoldingsByTrader(trader);
	if (shareParcels.iterator().hasNext()) {
%>
	<ul>
<%
		for (ShareParcel shareParcel : shareParcels) {
			String companyName = shareParcel.getCompany().getName();
			String companyCode = shareParcel.getCompany().getCode();
			long marketValue = shareParcel.getShareCount() * shareParcel.getCompany().getLastTradePrice();
			totalValue += marketValue;
%>
			<li>
			Company: <a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=companyCode%>"><%= companyName %></a><br/>
			Share count: <%= shareParcel.getShareCount() %><br/>
			Last traded price: <%= WebPageUtil.formatCurrency(shareParcel.getCompany().getLastTradePrice()) %><br/>
			Market Value: <%= WebPageUtil.formatCurrency(marketValue)  %><br/>
			</li>
<%		
		}
%>
	</ul>
		 	Total current net worth: <%= WebPageUtil.formatCurrency(totalValue) %>
<%
	} else {
%>
			No current holdings are registered for this trader.	
<%
	}
%>
				</p>
<%
	List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader);
	if (openOrders.size() != 0) {
%>
				<p> Outstanding orders </p>
				<ul>
<%
		for (Order order : openOrders) {
%>
				<li>
					Order Type: <%= order.getOrderType() %></br>
					Company: <%= order.getCompany().getName() %></br>
					Share count: <%= order.getRemainingShareCount() %></br>
					Price: <%= WebPageUtil.formatCurrency(order.getOfferPrice()) %></br>
				</li>
<%
		}
%>
				</ul>
<%
	} else {
%>
		<p>No orders currently outstanding.</p>
<%		
	}

	List<TraderEvent> eventList = TraderEventDAO.getTraderEventList(trader);
	if (eventList.size() != 0) {
%>
		<table>
			<tr>
			<td>Date</td>
			<td>Type</td>
			<td>Company()</td>
			<td>ShareCount</td>
			<td>Amount Transferred</td>
			<td>Starting Cash</td>
			<td>Ending Cash</td>
			</tr>
<%
		for (TraderEvent  event : eventList) {			
%>
			<tr>
			<td><%= event.getDate() %></td>
			<td><%= event.getEventType() %></td>
			<td><%= event.getCompany().getName() %></td>
			<td><%= event.getShareCount() %></td>
			<td><%= WebPageUtil.formatCurrency(event.getAmountTransferred()) %></td>
			<td><%= WebPageUtil.formatCurrency(event.getStartingCash()) %></td>
			<td><%= WebPageUtil.formatCurrency(event.getEndingCash()) %></td>
			</tr>
<%	
		}
%>
		</table>
<%
	} else {
%>
	<p>No Trader events</p>		
<%	
	}
%>
				
				</div><!-- end entry -->
			</div><!-- end post -->


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