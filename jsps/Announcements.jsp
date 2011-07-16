<%@page import="net.fidoandfido.model.PeriodMessage"%>
<%@page import="net.fidoandfido.model.StockExchange"%>
<%@page import="net.fidoandfido.dao.StockExchangeDAO"%>
<%@page import="net.fidoandfido.dao.PeriodMessageDAO"%>
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

<%
	StockExchangeDAO stockExchangeDAO = new StockExchangeDAO();
	PeriodMessageDAO periodMessageDAO = new PeriodMessageDAO();
	List<StockExchange> exchangeList = stockExchangeDAO.getStockExchangeListForTrader(trader);
	for (StockExchange exchange : exchangeList) {
	
%>	
	<div class="post">
		<h2 class="title"><%= exchange.getName() %> Announcements</h2>
		<div class="entry">
<%
		List<PeriodMessage> announcements = periodMessageDAO.getLatestPeriodMessages(exchange);
		if (announcements.size() == 0) {
%>
			<p>There are currently no annoucements for this exchange.</p>
<%				
		} else {
%>
			<table id="table-1">

				<tr class="table-head">							
					<th>Exchange</th>
					<th>Sector</th>
					<th>Company</th>
					<th>Message</th>
				</tr>
			
<%				
			for (PeriodMessage announcement : announcements) {
%>
				<tr>
					<td><%= announcement.getExchange().getName() %></td>
					<td>
<% 
						if (announcement.getSector() != null) {
%>
						<%= announcement.getSector() %>
<% 
						} 
%>
					</td>
					<td>
<% 
						if (announcement.getCompany() != null) {
%>
						<%= announcement.getCompany().getName() %>
<% 
						} 
%>
					</td>
					<td><%=announcement.getMessage()%></td>
			</tr>
<%
			}
		}
		//close off the rumour table
%>
				</table>
			</div><!-- end entry -->
		</div><!-- end post -->
<%
	}
%>
	
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