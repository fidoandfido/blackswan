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
%>
<html>

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