<%@page import="net.fidoandfido.model.UserSession"%>
<%@page import="net.fidoandfido.dao.UserSessionDAO"%>
<%@page import="net.fidoandfido.engine.quarter.QuarterEventGenerator"%>
<%@page import="java.util.Map"%>
<%@page import="net.fidoandfido.model.PeriodQuarter"%>
<%@page import="net.fidoandfido.dao.HibernateUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="net.fidoandfido.model.User"%>
<%@page import="net.fidoandfido.util.WebUtil"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collection"%>
<%@page import="net.fidoandfido.model.Trader"%>
<%@page import="net.fidoandfido.dao.TraderDAO"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<%@page import="net.fidoandfido.util.Constants"%>
<%@page import="java.util.Date"%>
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

	// initialise relevant data here.
	List<String> someList = new ArrayList<String>();	
	someList.add("Cat");
	someList.add("dog");
	someList.add("Guinea pig");

	
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
				<li class="current_page_item"><a href="/myapp/Exchange.jsp">Exchanges</a></li>
				<li class="current_page_item"><a href="/myapp/logout">Log out</a><li>
			</ul>
		</div>
	</div>
</div>

<div id="page">
	<div id="content">
	
			<div class="post">
				<h2 class="title">Template Heading</h2>
				<div class="entry">
				Your current balance is: <%=WebPageUtil.formatCurrency(trader.getCash())%><br/>
				</div>
			</div>	
<%	
		if (someList.size() > 0) {
%>
			<div class="post">
				<h2 class="title">Some Data</h2>
				<div class="entry">
					Pets:
					<ul>
<%
			for (String petString : someList) {
%>
						<li><%= petString %></li>
<%
			}
%>				
					</ul>

				</div>
			</div>
<%
		} else {
%>			
			<div class="post">
				<h2 class="title">No Data!</h2>
				<div class="entry">
					There was nothing in the list. Or whatever.


				</div>
			</div>
<%
		} 
%>			
			
			
<%
		if (user.isUserAdmin()) {
				// Show the time that all the bits will be available.
%>
			<div class="post">
				<h2 class="title">Admin Information</h2>
				<div class="entry">
				Trader has administrator privileges.
				</div>
			</div>	
<%
		}
%>	

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