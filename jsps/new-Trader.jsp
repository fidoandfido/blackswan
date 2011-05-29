<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="net.fidoandfido.model.ReputationItem"%>
<%@page import="net.fidoandfido.model.PeriodRumour"%>
<%@page import="net.fidoandfido.model.PeriodEvent"%>
<%@page import="java.util.Date"%>
<%@page import="net.fidoandfido.dao.PeriodPartInformationDAO"%>
<%@page import="net.fidoandfido.dao.RumourDAO"%>
<%@page import="net.fidoandfido.servlets.CancelOrderServlet"%>
<%@page import="net.fidoandfido.servlets.BuySharesServlet"%>
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
<%@page import="net.fidoandfido.servlets.SellSharesServlet"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page session="true" %>
<%	
	HibernateUtil.beginTransaction();
	User user = null;
	Trader trader = null;
	Date currentDate = new Date();

	UserSessionDAO userSessionDAO = new UserSessionDAO();
	UserSession userSession = userSessionDAO.getUserSessionBySessionId(request.getSession().getId());

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
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Black Swan Trading</title>

	<link href="stylesheets/new-style.css" rel="stylesheet" type="text/css" media="screen" />
</head>

<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>

<div id="page">
	<div id="content">

			<div class="post">
				<h2 class="title">Trader Page - <%= trader.getName() %></h2>
				<div class="entry">
				<p>Hello <%= user.getUserName() %> and Welcome to the new Black Swan application!</p>
				
<%
		long totalValue = trader.getCash();
		long portfolioValue = 0;
		ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
		Iterable<ShareParcel> shareParcels = shareParcelDAO.getHoldingsByTrader(trader);
		for (ShareParcel shareParcel : shareParcels) {
			portfolioValue += shareParcel.getShareCount() * shareParcel.getCompany().getLastTradePrice();
		}
		totalValue += portfolioValue;
%>

				<p>
				<b>Summary:<b>
				</p>
				
				<table id="table-2">
					<tr>
						<th>Net Wealth</th>
					</tr>
					<tr>
						<td ><%= WebPageUtil.formatCurrency(totalValue) %></td>
					</tr>				
					<tr class="table-foot">
						<th>Portfolio Value</th>
						<th>Current Balance</th>
						<th>Available Balance</th>

					</tr>
					<tr>
						<td ><%= WebPageUtil.formatCurrency(portfolioValue) %></td>
						<td ><%= WebPageUtil.formatCurrency(trader.getCash()) %></td> 
						<td><%= WebPageUtil.formatCurrency(trader.getCash()) %></td>
					</tr>
				</table>
				<p></p>				
				
				<p>
				
				<p>	<b>Portfolio:</b></p>
				
<%
	if (shareParcels.iterator().hasNext()) {
%>
				<table id="table-1">

				<tr class="table-head">							
					<th>Company Name</th>
					<th>Quantity</th>
					<th>Book Price</th>
					<th>Market Price</th>
					<th>Market Value</th>
				</tr>
<%
				for (ShareParcel shareParcel : shareParcels) {
					String companyName = shareParcel.getCompany().getName();
					String companyCode = shareParcel.getCompany().getCode();
					long marketValue = shareParcel.getShareCount() * shareParcel.getCompany().getLastTradePrice();
					totalValue += marketValue;
%>
				<tr>
					<!-- Company Name  -->
					<td><a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=companyCode%>"><%= companyName %></a></td>
					<!-- Share Quantity  -->
					<td><%= shareParcel.getShareCount() %></td>
					<!-- Purchase Price  -->
					<td><%= WebPageUtil.formatCurrency(shareParcel.getPurchasePrice())  %></td>
					<!-- Last Traded Price  -->
					<td><%= WebPageUtil.formatCurrency(shareParcel.getCompany().getLastTradePrice()) %></td>
					<!-- Market Value  -->
					<td><%= WebPageUtil.formatCurrency(marketValue)  %></td>
				</tr>
			
<%		
				}
		// Update net worth to include purchased items...
		for (ReputationItem item : trader.getReputationItems()) {
			totalValue += item.getCost();
		}
%>
				<tr class="table-foot">
					<th>Portfolio Value</th>
					<th colspan=3></th>
					<th><%= WebPageUtil.formatCurrency(portfolioValue) %></th>
				</tr>	
				</table> <!-- END TABLE  -->
<%
	} else {
%>
			No current holdings are registered for this trader.	
<%
	}
%>
		<p>
		<p> Outstanding orders: </p>
<%
	List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader);
	if (openOrders.size() != 0) {
%>
				<ul>
<%
		for (Order order : openOrders) {
%>
				<li>
					Order Type: <%= order.getOrderType() %></br>
					Company: <%= order.getCompany().getName() %></br>
					Share count: <%= order.getRemainingShareCount() %></br>
					Price: <%= WebPageUtil.formatCurrency(order.getOfferPrice()) %></br>
					<form action="/myapp/cancelorder" method="post">
					<input type="hidden" name="<%= CancelOrderServlet.ID_PARM %>" value="<%= order.getId() %>"/>
					<input type="submit" value="Cancel Order" />
				</form>
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

%>
				
				</div><!-- end entry -->
			</div><!-- end post -->
			<div class="post">
				<h2 class="title">News - All the latest!</h2>
				<div class="entry">
				<p><b>Latest Rumours:</b></p>
				<ul>
				
<%
			RumourDAO rumourDAO = new RumourDAO();
			List<PeriodRumour> rumours = rumourDAO.getLatestRumours(5, currentDate);
			List<String> sectors = new ArrayList<String>();
			for (PeriodRumour rumour : rumours) {
				if (rumour.getDateRumourExpires().before(currentDate)) {
					continue;
				}
				String sector = rumour.getSector();
				if (trader.getReputation(sector) < rumour.getReputationRequired()) {
					sectors.add(sector);
				} else {
%>
				<li>
				Company: <a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=rumour.getCompany().getCode()%>"><%= rumour.getCompany().getName() %></a>
				Date released: <%= rumour.getDateInformationAvailable() %><br/>
				Event message: <%= rumour.getMessage() %><br/>
				Analyst Reaction: <%= rumour.getEventType() %><br/>
				</li>
<% 				
				}
			}
%>					
				</ul>
<%
			if (sectors.size() != 0) {
%>
				Rumours are available in the following sectors:
				<ul>
<%
				for (String sector : sectors) {
%>
					<li><%= sector %></li>
<%				
				}
%>				
				</ul> 
				but you require more reputation points.
				Perhaps you should visit the <a href="/myapp/ItemShop.jsp">store</a> to buy some items?
<%						
				
			}

%>
				<p><b>Latest Announcements:</b></p>
				<ul>
								
<%
			PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();
			List<PeriodEvent> events = periodPartInformationDAO.getLatestEvents(10, currentDate);
			for (PeriodEvent event : events) {				
%>
				<li>
				Company: <a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=event.getCompany().getCode()%>"><%= event.getCompany().getName() %></a><br/>
				Event: <%= event.getAnnouncementType() %><br/>
				Date released: <%= event.getDateInformationAvailable() %><br/>
				Event message: <%= event.getMessage() %><br/>
				Analyst Reaction: <%= event.getEventType() %><br/>
				</li>
<% 				
			}
%>	
				</ul>
				</div><!-- end entry -->
			</div><!-- end post -->
			
			<div class="post">
				<h2 class="title">Trader Items</h2>
				<div class="entry">
			
<%
			Set<ReputationItem> reputationItems = trader.getReputationItems();
			if (reputationItems.size() == 0) {
%>
				No items to display. You may purchase reputation enhancing items at the <a href="/myapp/ItemShop.jsp">Item Store</a>.
<%				
			} else {
%>
					<table>
					<tr>
						<td>Name</td>
						<td>Cost</td>
						<td></td>
						<td></td>
					</tr>
<%
					for (ReputationItem item : reputationItems) {
%>
						<tr>
							<td><%= item.getName() %></td>
							<td><%= WebPageUtil.formatCurrency(item.getCost()) %></td>
							<td><img src="<%= WebPageUtil.getImageLocation(item.getImage()) %>" width="60" height="60"/></td>
							<td>Sell</td>
						</tr>
<%
						}
%>		
					
					</table>			
<%
				}
			
%>
				</div><!-- end entry -->
			</div><!-- end post -->
			
			<div class="post">
				<h2 class="title">Trader Audit Events</h2>
				<div class="entry">
			
<%
	
	TraderEventDAO traderEventDAO = new TraderEventDAO();
	List<TraderEvent> eventList = traderEventDAO.getTraderEventList(trader);
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
			<td><%= event.getCompany() == null ? event.getItem().getName() : event.getCompany().getName() %></td>
			<td><%= event.getCompany() == null ? 1 : event.getShareCount() %></td>
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