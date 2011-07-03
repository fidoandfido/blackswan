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
	<div id="header">
		<div id="menu">
			<ul>
				<li class="current_page_item"><a href="/myapp/Welcome.jsp">Home</a></li>
				<li class="current_page_item"><a href="/myapp/News.jsp">News</a></li>
				<li class="current_page_item"><a href="/myapp/Exchange.jsp">Exchanges</a></li>
				<li class="current_page_item"><a href="/myapp/logout">Log out</a><li>
			</ul>
		</div>
	</div>
</div>

<div id="page">
	<div id="content">

<%
	RumourDAO rumourDAO = new RumourDAO();
	List<PeriodRumour> rumours = rumourDAO.getLatestRumours(10, currentDate, trader);
	if (rumours.size() != 0) {
%>	
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
					if (parcel != null) {
%>
function rumour_sell_<%=companyCode%>() {
   	remove('tradeDiv');
    var div = document.createElement('div');
    div.id = 'tradeDiv'; 
    document.body.appendChild(div);
    div.innerHTML = 'Sell Shares: (Maximum: <%=parcel.getShareCount()%>)<br/><form action="/myapp/sellshares" method="post"><input type="hidden" name="<%=BuySharesServlet.COMPANY_CODE_PARM%>" value="<%=companyCode%>" ></input><table id="tradeTable"><tr><td>Share Count:</td><td><input name="<%=SellSharesServlet.SHARE_COUNT%>" value="<%=parcel.getShareCount()%>" cols="60"></input></td></tr><tr><td>Asking price (in cents):</td><td><input name="<%=SellSharesServlet.ASKING_PRICE%>" value="<%=company.getLastTradePrice()%>" cols="30"></input></td></tr><tr><td><input type="submit" value="Sell Shares" /></td><td><button id="cancel_button">Cancel</button></td></tr></table></form>';
    var button = document.getElementById("cancel_button");
    button.setAttribute('onclick', 'remove("tradeDiv")'); 
    popUpDiv('tradeDiv', -400, 0);
}
<%
					}
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
			<td onclick='javascript:rumour_sell_<%=companyCode%>()' style="cursor: pointer;"><b>Sell</b></td>
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
			if (parcel != null) {
%>
function announcement_sell_<%=companyCode%>() {
   	remove('tradeDiv');
    var div = document.createElement('div');
    div.id = 'tradeDiv'; 
    document.body.appendChild(div);
    div.innerHTML = 'Sell Shares: (Maximum: <%=parcel.getShareCount()%>)<br/><form action="/myapp/sellshares" method="post"><input type="hidden" name="<%=BuySharesServlet.COMPANY_CODE_PARM%>" value="<%=companyCode%>" ></input><table id="tradeTable"><tr><td>Share Count:</td><td><input name="<%=SellSharesServlet.SHARE_COUNT%>" value="<%=parcel.getShareCount()%>" cols="60"></input></td></tr><tr><td>Asking price (in cents):</td><td><input name="<%=SellSharesServlet.ASKING_PRICE%>" value="<%=company.getLastTradePrice()%>" cols="30"></input></td></tr><tr><td><input type="submit" value="Sell Shares" /></td><td><button id="cancel_button">Cancel</button></td></tr></table></form>';
    var button = document.getElementById("cancel_button");
    button.setAttribute('onclick', 'remove("tradeDiv")'); 
    popUpDiv('tradeDiv', -400, 0);
}
<%
					}
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
		</div><!--  end page -->
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