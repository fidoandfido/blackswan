<%@page import="net.fidoandfido.servlets.ItemStoreServlet"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="net.fidoandfido.model.Company"%>
<%@page import="java.util.HashSet"%>
<%@page import="net.fidoandfido.servlets.MessageServlet"%>
<%@page import="net.fidoandfido.model.TraderMessage"%>
<%@page import="net.fidoandfido.dao.TraderMessageDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="net.fidoandfido.model.ReputationItem"%>
<%@page import="net.fidoandfido.model.PeriodRumour"%>
<%@page import="net.fidoandfido.model.PeriodQuarter"%>
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
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
	
	// Set up a map of company codes to share parcels for this user.
	ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
	Iterable<ShareParcel> traderHoldings = shareParcelDAO.getHoldingsByTrader(trader);
	Map<String, ShareParcel> holdingsMap = new HashMap<String, ShareParcel>();
	for (ShareParcel parcel : traderHoldings) {
		holdingsMap.put(parcel.getCompany().getCode(), parcel);
	}
	
	
%>
<html>

<script type="text/javascript" src="/myapp/scripts/popup.js""></script>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Black Swan Trading</title>

	<link href="stylesheets/new-style.css" rel="stylesheet" type="text/css" media="screen" />
</head>


<!--  PAGE HEADER -->
<div id="header-wrapper">
	<div id="logo">
		<h1><a href="/myapp/Welcome.jsp">Black Swan Trading</a></h1>
	</div>
	<hr />
	<!--  end #logo -->

<%@ include file="webTemplates/pageHeaderA.txt" %>
</div>

<div id="page">
	<div id="content">

			<div class="post">
				<h2 class="title">Trader Page - <%=trader.getName()%></h2>
				<div class="entry">
				<p>Your current experience points: <%= trader.getExperiencePoints() %></p>
				<p>Your current trader level: <%= trader.getLevel() %></p>
<%
	// Generate total net worth of the trader.
	long totalValue = trader.getCash();
	
	// Add the portfolio value.
	long portfolioValue = 0;
	Iterable<ShareParcel> shareParcels = shareParcelDAO.getHoldingsByTrader(trader);
	for (ShareParcel shareParcel : shareParcels) {
		portfolioValue += shareParcel.getShareCount() * shareParcel.getCompany().getLastTradePrice();
	}
	totalValue += portfolioValue;
	
	// Add the value of the items this trader owns (their current worth - ie sale price)
	for (ReputationItem item : trader.getReputationItems()) {
		totalValue += item.getSalePrice();
	}
%>

				<p>
					<b>Summary:</b>
				</p>
				
				<table id="table-1">
					<tr>
						<th>Net Wealth</th>
					</tr>
					<tr>
						<td ><%=WebPageUtil.formatCurrency(totalValue)%></td>
					</tr>				
					<tr class="table-foot">
						<th>Portfolio Value</th>
						<th>Current Balance</th>
						<th>Available Balance</th>

					</tr>
					<tr>
						<td ><%=WebPageUtil.formatCurrency(portfolioValue)%></td>
						<td ><%=WebPageUtil.formatCurrency(trader.getCash())%></td> 
						<td><%=WebPageUtil.formatCurrency(trader.getCash())%></td>
					</tr>
				</table>
				<br/>
				<p>
				<a href="/myapp/TraderAuditReport.jsp">Full audit report</a>
				</p>
				
				<p>
					<b>Portfolio:</b>
				</p>

<%
	if (shareParcels.iterator().hasNext()) {
%>

				<table class="table-balance-sheet">

				<tr class="table-head">	
					<th></th>						
					<th>Company Name</th>
					<th>Quantity</th>
					<th>Avg. Purchase Price</th>
					<th>Market Price</th>
					<th>Market Value</th>
					<th></th>
					<th></th>
				</tr>
<%
		for (ShareParcel shareParcel : shareParcels) {
			String companyName = shareParcel.getCompany().getName();
			String companyCode = shareParcel.getCompany().getCode();
			long marketValue = shareParcel.getShareCount() * shareParcel.getCompany().getLastTradePrice();
			totalValue += marketValue;
%>
<script type="text/javascript">
function buy_<%=companyCode%>() {
   	remove('tradeDiv');
    var div = document.createElement('div');
    div.id = 'tradeDiv'; 
    document.body.appendChild(div);
    div.innerHTML = '<b>Buy More Shares</b> (Max buying power at market rate: <%=trader.getAvailableCash() / shareParcel.getCompany().getLastTradePrice()%>)<br/><form action="/myapp/buyshares" method="post"><input type="hidden" name="<%=BuySharesServlet.COMPANY_CODE_PARM%>" value="<%=shareParcel.getCompany().getCode()%>" ></input><table class="tradeTable"><tr><td>Share Count:</td><td><input name="<%=BuySharesServlet.SHARE_COUNT%>" value="<%=trader.getAvailableCash() / shareParcel.getCompany().getLastTradePrice()%>" cols="60"></input></td></tr><tr><td>Offer price (in cents):</td><td><input name="<%=BuySharesServlet.OFFER_PRICE%>" value="<%=shareParcel.getCompany().getLastTradePrice()%>" cols="30"></input></td></tr><tr><td><input type="submit" value="Buy Shares" /></td><td><button id="cancel_button">Cancel</button></td></tr></table></form>';
    var button = document.getElementById("cancel_button");
    button.setAttribute('onclick', 'remove("tradeDiv")'); 
    popUpDiv('tradeDiv', -400, 0);
}

function sell_<%=companyCode%>() {
   	remove('tradeDiv');
    var div = document.createElement('div');
    div.id = 'tradeDiv';
    document.body.appendChild(div);
    div.innerHTML = '<b>Sell Shares</b> (Maximum: <%=shareParcel.getShareCount()%>)<br/><form action="/myapp/sellshares" method="post"><input type="hidden" name="<%=BuySharesServlet.COMPANY_CODE_PARM%>" value="<%=shareParcel.getCompany().getCode()%>" ></input><table class="tradeTable"><tr><td>Share Count:</td><td><input name="<%=SellSharesServlet.SHARE_COUNT%>" value="<%=shareParcel.getShareCount()%>" cols="60"></input></td></tr><tr><td>Asking price (in cents):</td><td><input name="<%=SellSharesServlet.ASKING_PRICE%>" value="<%=shareParcel.getCompany().getLastTradePrice()%>" cols="30"></input></td></tr><tr><td><input type="submit" value="Sell Shares" /></td><td><button id="cancel_button">Cancel</button></td></tr></table></form>';
    var button = document.getElementById("cancel_button");
    button.setAttribute('onclick', 'remove("tradeDiv")'); 
    popUpDiv('tradeDiv', -400, 0);
}
</script>

				<tr>
					<td><%= shareParcel.getCompany().getStockExchange().getName() %></td>
					<!-- Company Name  -->
					<td><a href="Companies.jsp?<%=Constants.COMPANY_CODE_PARM%>=<%=companyCode%>"><%=companyName%></a></td>
					<!-- Share Quantity  -->
					<td><%=shareParcel.getShareCount()%></td>
					<!-- Purchase Price  -->
					<td><%=WebPageUtil.formatCurrency(shareParcel.getPurchasePrice())%></td>
					<!-- Last Traded Price  -->
					<td onclick='javascript:popUpGraph("<%=companyCode%>");' style="cursor: pointer;" >
						<b><%= WebPageUtil.formatCurrency(shareParcel.getCompany().getLastTradePrice()) %></b>
					</td>
					<!-- Market Value  -->
					
					<td><%=WebPageUtil.formatCurrency(marketValue)%></td>
					<td onclick='javascript:buy_<%=companyCode%>()'style="cursor: pointer;"><b>Buy</b></td>
					<td onclick='javascript:sell_<%=companyCode%>()' style="cursor: pointer;"><b>Sell</b></td>
				</tr>

<%
		}
%>
				<tr class="table-foot">
					<th>Portfolio Value</th>
					<th colspan=3></th>
					<th><%=WebPageUtil.formatCurrency(portfolioValue)%></th>
				</tr>	
				</table> <!-- END TABLE  -->
<%
	} else {
%>
			<p>No current holdings are registered for this trader.</p>	
<%
	}
%>
				</div><!-- end entry -->
			</div><!-- end post -->
			
			<div class="post">
				<h2 class="title">Messages</h2>
				<div class="entry">
<%
	TraderMessageDAO messageDAO = new TraderMessageDAO();
	List<TraderMessage> messages = messageDAO.getCurrentMessages(trader);
	if (messages.size() != 0) {
%>
				<p>There are messages for you to view.</p>
				<div id="messages">
<%
		for (TraderMessage message : messages) {
			boolean isNew = false;
			if (!message.isRead()) {
				isNew = true;
				message.setRead(true);
				messageDAO.saveMessage(message);
			}
			// Add a javascript function to remove this message.
%>
		

<script type="text/javascript">

function removeMessage_<%=message.getId()%>() {
	request = createRequest();
	if (request == null) {
		alert("Unable to create request!");
	} else {
		request.onreadystatechange = deleteMessage_<%=message.getId()%>;
		url = "/myapp/message";
		request.open("POST", url, true);
		var requestData = "<%=MessageServlet.COMMAND_PARM%>=<%=MessageServlet.DISMISS_MESSAGE%>" +
				"&<%=MessageServlet.ID_PARM%>=<%=message.getId()%>"  +
				"&<%=MessageServlet.RESPONSE_FORMAT%>=<%=MessageServlet.AJAX%>";
		request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		request.send(requestData);
	}
	
}

function deleteMessage_<%=message.getId()%>() {
	if (request.readyState == 4) {
		if (request.responseText == "<%=MessageServlet.OKAY%>") {
			removeElement("messages", "message_<%=message.getId()%>");
		} else {
			// ??
			//alert("Error removing message");
		}
	}
}

</script>

				<div id="message_<%=message.getId()%>">
				<p>
				<%=isNew ? "<b>" : ""%>
				<%=message.getSubject()%>
				<br/>
				<%=message.getBody()%>
				<br/>
				<%=message.getDate()%>
				<%=isNew ? "</b>" : ""%>		
				<form>
				<input type="hidden" name="<%=MessageServlet.COMMAND_PARM%>" value="<%=MessageServlet.DISMISS_MESSAGE%>" ></input>
				<input type="hidden" name="<%=MessageServlet.ID_PARM%>" value="<%=message.getId()%>" ></input>
				<input type="button" value="Dismiss" onclick="removeMessage_<%=message.getId()%>()" />
				</form>
				</p>
				</div>
<%
		}
%>
			</div>
<%
	} else {
%>	
	No current messages.		
<%
	}
%>				
				<br/><a href="/myapp/Message.jsp">Post a Message</a><br/>
<%
	List<Order> openOrders = OrderDAO.getOpenOrdersByTrader(trader);
	if (openOrders.size() != 0) {
%>
				</div><!-- end entry -->
			</div><!-- end post -->
			
			<div class="post">
				<h2 class="title">Current orders</h2>
				<div class="entry">
				<ul>
<%
		for (Order order : openOrders) {
%>
				<li>
					Order Type: <%=order.getOrderType()%><br/>
					Company: <%=order.getCompany().getName()%><br/>
					Share count: <%=order.getRemainingShareCount()%><br/>
					Price: <%=WebPageUtil.formatCurrency(order.getOfferPrice())%><br/>
					<form action="/myapp/cancelorder" method="post">
					<input type="hidden" name="<%=CancelOrderServlet.ID_PARM%>" value="<%=order.getId()%>"/>
					<input type="submit" value="Cancel Order" />
				</form>
				</li>
<%
		}
%>
				</ul>
<%
	}

	RumourDAO rumourDAO = new RumourDAO();
	List<PeriodRumour> rumours = rumourDAO.getLatestRumours(10, currentDate, trader);
	if (rumours.size() != 0) {
%>				
		</div><!-- end entry -->
	</div><!-- end post -->
	<div class="post">
		<h2 class="title">Latest Rumours</h2>
		<div class="entry">
<%
		Set<String> sectors = new HashSet<String>();
		boolean rumourShown = false;
		for (PeriodRumour rumour : rumours) {
			Company company = rumour.getCompany();
			String companyCode = company.getCode();
			if (rumour.getDateRumourExpires().before(currentDate)) {
				continue;
			}
			String sector = rumour.getSector();
			if (trader.getReputation(sector) < rumour.getReputationRequired()) {
				sectors.add(sector);
			} else {
				if (!rumourShown) {
					//set up the rumour table
						rumourShown = true;
%>
			<table id="table-1">

				<tr class="table-head">							
					<th></th>
					<th>Company Name</th>
					<th>Date Rumour Released</th>
					<th>Message</th>
					<th>Reaction</th>
					<th></th>
					<th></th>
				</tr>
			
<%				
					}
%>
<script type="text/javascript">
function rumour_buy_<%=company.getCode()%>() {
   	remove('tradeDiv');
    var div = document.createElement('div');
    div.id = 'tradeDiv'; 
    document.body.appendChild(div);
    div.innerHTML = 'Buy More Shares: (Max buying power at market rate: <%=trader.getAvailableCash() / company.getLastTradePrice()%>)<br/><form action="/myapp/buyshares" method="post"><input type="hidden" name="<%=BuySharesServlet.COMPANY_CODE_PARM%>" value="<%=company.getCode()%>" ></input><table id="tradeTable"><tr><td>Share Count:</td><td><input name="<%=BuySharesServlet.SHARE_COUNT%>" value="<%=trader.getAvailableCash() / company.getLastTradePrice()%>" cols="60"></input></td></tr><tr><td>Offer price (in cents):</td><td><input name="<%=BuySharesServlet.OFFER_PRICE%>" value="<%=company.getLastTradePrice()%>" cols="30"></input></td></tr><tr><td><input type="submit" value="Buy Shares" /></td><td><button id="cancel_button">Cancel</button></td></tr></table></form>';
    var button = document.getElementById("cancel_button");
    button.setAttribute('onclick', 'remove("tradeDiv")'); 
    popUpDiv('tradeDiv', -400, 0);
}
<%
					ShareParcel parcel = holdingsMap.get(company.getCode());

%>
</script>



			<tr>
			<td><%= rumour.getCompany().getStockExchange().getName() %></td>
			<td><a href="Companies.jsp?<%=Constants.COMPANY_CODE_PARM%>=<%=rumour.getCompany().getCode()%>"><%=rumour.getCompany().getName()%></a></td>
			<td><%=rumour.getDateInformationAvailable()%></td>
			<td><%=rumour.getMessage()%></td>
			<td><%=rumour.getEventType()%></td>
			
			<td onclick='javascript:rumour_buy_<%=companyCode%>()'style="cursor: pointer;"><b>Buy</b></td>
<%
				if (parcel != null) {
					%>
			<td onclick='javascript:sell_<%=companyCode%>()' style="cursor: pointer;"><b>Sell</b></td>
					<%
				} else {
%>
				<td></td>
					<%
				} 
%>
			</tr>
<%
				}
		}
		if (rumourShown) {
		//close off the rumour table
%>
			</table>
<%			
		}
		if (sectors.size() != 0) {
%>

			<br/>
				Rumours are available in the following sectors:
				<ul>
<%
			for (String sector : sectors) {
%>
					<li><%=sector%></li>
<%
			}
%>				
				</ul> 
				but you require more reputation points.
				Perhaps you should visit the <a href="/myapp/ItemShop.jsp">store</a> to buy some items?
<%
		}
	}
%>

		</div><!-- end entry -->
	</div><!-- end post -->
	<div class="post">
		<h2 class="title">Latest Announcements</h2>
		<div class="entry">
				<table id="table-1">
				<tr class="table-head">
					<th></th>
					<th>Company Name</th>
					<th>Event</th>
					<th>Date Released</th>
					<th>Message</th>
					<th>Reaction</th>
					<th></th>
					<th></th>
				</tr>
				
								
<%
	PeriodPartInformationDAO periodPartInformationDAO = new PeriodPartInformationDAO();
	List<PeriodQuarter> events = periodPartInformationDAO.getLatestEvents(10, currentDate, trader);
	for (PeriodQuarter event : events) {
		Company company = event.getCompany();
		String companyCode = company.getCode();
		
								%>
								
								
<script type="text/javascript">
function announcement_buy_<%=company.getCode()%>() {
   	remove('tradeDiv');
    var div = document.createElement('div');
    div.id = 'tradeDiv'; 
    document.body.appendChild(div);
    div.innerHTML = 'Buy More Shares: (Max buying power at market rate: <%=trader.getAvailableCash() / company.getLastTradePrice()%>)<br/><form action="/myapp/buyshares" method="post"><input type="hidden" name="<%=BuySharesServlet.COMPANY_CODE_PARM%>" value="<%=company.getCode()%>" ></input><table id="tradeTable"><tr><td>Share Count:</td><td><input name="<%=BuySharesServlet.SHARE_COUNT%>" value="<%=trader.getAvailableCash() / company.getLastTradePrice()%>" cols="60"></input></td></tr><tr><td>Offer price (in cents):</td><td><input name="<%=BuySharesServlet.OFFER_PRICE%>" value="<%=company.getLastTradePrice()%>" cols="30"></input></td></tr><tr><td><input type="submit" value="Buy Shares" /></td><td><button id="cancel_button">Cancel</button></td></tr></table></form>';
    var button = document.getElementById("cancel_button");
    button.setAttribute('onclick', 'remove("tradeDiv")'); 
    popUpDiv('tradeDiv', -400, 0);
}
<%
			ShareParcel parcel = holdingsMap.get(company.getCode());
%>			
</script>
				<tr>
				<td><%= event.getCompany().getStockExchange().getName() %></td>
				<td><a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=event.getCompany().getCode()%>"><%= event.getCompany().getName() %></a></td>
				<td><%= event.getAnnouncementType() %></td>
				<td><%= event.getDateInformationAvailable() %></td>
				<td><%= event.getMessage() %></td>
				<td><%= event.getEventType() %></td>
				<td onclick='javascipr:announcement_buy_<%=companyCode%>()' style="cursor: pointer;">Buy</td>
				<%
				if (parcel != null) {
					%>
				<td onclick='javascript:sell_<%=companyCode%>()' style="cursor: pointer;"><b>Sell</b></td>
					<%
				} else {
%>
				<td></td>
<%
				} 
%>
				</tr>
<% 				
	}
%>	
				</table>
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
<script type="text/javascript">
function item_sell_<%=item.getName()%>() {
   	remove('tradeDiv');
    var div = document.createElement('div');
    div.id = 'tradeDiv'; 
    document.body.appendChild(div);
    div.innerHTML = '<form></form>';
    var button = document.getElementById("cancel_button");
    button.setAttribute('onclick', 'remove("tradeDiv")'); 
    popUpDiv('tradeDiv', -400, 0);
}	
</script>
						<tr>
							<td><%= item.getName() %></td>
							<td><%= WebPageUtil.formatCurrency(item.getCost()) %></td>
							<td><img src="<%= WebPageUtil.getImageLocation(item.getImage()) %>" width="60" height="60"/></td>
							<td>
							<form action="/myapp/itemstore" method="post">
							<input type="hidden" name="<%=ItemStoreServlet.BUY_OR_SELL%>" value="<%=ItemStoreServlet.SELL%>"></input></li>
							<input type="hidden" name="<%=ItemStoreServlet.COST%>" value="<%=item.getCost()%>"></input></li>
							<input type="hidden" name="<%=ItemStoreServlet.ITEM_NAME%>" value="<%=item.getName()%>"></input></li>
							<input type="submit" value="Sell!" />
							</form>
							</td>
						</tr>
<%
		}
%>		
					
					</table>
					
					You may purchase more reputation enhancing items at the <a href="/myapp/ItemShop.jsp">Item Store</a>.			
<%
	}
			
%>
				</div><!-- end entry -->
			</div><!-- end post -->
			
</div>
<!-- end #content -->
	<div style="clear: both;">&nbsp;</div>
</div>
<!--  end page -->

<%@ include file="webTemplates/footer.txt" %>

</body>
</html>

<%
	HibernateUtil.commitTransaction();
%>