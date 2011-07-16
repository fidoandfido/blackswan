<%@page import="net.fidoandfido.dao.UserSessionDAO"%>
<%@page import="net.fidoandfido.servlets.MessageServlet"%>
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
	<!--  end #logo -->	<!--  PAGE HEADER -->
<div id="header-wrapper">
	<div id="logo">
		<h1><a href="/myapp/Welcome.jsp">Black Swan Trading</a></h1>
	</div>
	<hr />
	<!--  end #logo -->	
<%@ include file="webTemplates/pageHeaderA.txt" %>
</div>
</div>


<div id="page">
	<div id="content">
			<div class="post">
				<h2 class="title">Post a Message to yourself)</h2>
				<div class="entry">
				Your current balance is: <%=WebPageUtil.formatCurrency(trader.getCash())%><br/>

				<form action="/myapp/message" method="post">
				<input type="hidden" name="<%=MessageServlet.COMMAND_PARM %>" value="<%=MessageServlet.POST_MESSAGE%>" ></input>
				<ul> 
					<li>Subject:<input name="<%=MessageServlet.MESSAGE_SUBJECT_PARM%>"  value="Test" cols="60"></input></li>
					<li>Body:<input type="text" name="<%=MessageServlet.MESSAGE_BODY_PARM%>" value="This is a test message <%= new Date() %>" cols="140"></input></li>
				</ul>
				<input type="submit" value="Post Message" />
				</form>
									
				</div>
			</div>

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