<%@page import="net.fidoandfido.model.UserSession"%>
<%@page import="net.fidoandfido.dao.UserSessionDAO"%>
<%@page import="net.fidoandfido.servlets.LogInServlet"%>
<%@page import="net.fidoandfido.servlets.RegisterServlet"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<%@page import="net.fidoandfido.model.User"%>
<%@page import="net.fidoandfido.dao.HibernateUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List"%>
<%@page import="net.fidoandfido.dao.TraderDAO"%>
<%@page import="net.fidoandfido.model.Trader"%>

<%@page session="true" %>
<%	
	HibernateUtil.beginTransaction();
	UserSessionDAO userSessionDAO = new UserSessionDAO();
	UserSession userSession = userSessionDAO.getUserSessionBySessionId(request.getSession().getId());

	User user = null;
	Trader trader = null;
	if (userSession != null && userSession.isActive()) {
		user = userSession.getUser();
		trader = user.getTrader();
	}
	if (user != null && trader != null) {
		HibernateUtil.commitTransaction();
		response.sendRedirect("/myapp/Trader.jsp");
		return;
	}
%>

<html>
<%@ include file="webTemplates/header.txt" %>
<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>

<div id="page">
	<div id="content">

<%
	if (user != null) {
		// We have a user!
%>
			<div class="post">
				<h2 class="title">Welcome back, <%= user.getUserName() %> </h2>
				<div class="entry">
				<p>Hello <%= user.getUserName() %> and Welcome to the new Black Swan application!</p>
<%
		if (user.getTrader() == null) {
%>
				<form action="/myapp/register" method="post">
				<div>Enter a name for you trader:<input name="trader_name"  cols="60"></input></div>
				<div><input type="submit" value="Create Trader" /></div>
				</form>
<%
		}
%>
				</div><!-- end entry -->
			</div><!-- end post -->
<%
	} else {
		// Show the login...
%>
			<div class="post">
				<h2 class="title">Please Login</h2>
				<div class="entry">
				<p>Welcome to the new Black Swan application! Please log in to access your trading profile.</p>
				<form action="/myapp/login" method="post">
					<ul>
						<li>User name:<input name="<%=LogInServlet.USER_NAME%>"  cols="60"></input></li>
						<li>Password:<input type="password" name="<%=LogInServlet.PASSWORD%>"  cols="60"></input></li>
					</ul>
					<input type="submit" value="Log in" />
				</form>
				</div><!-- end entry -->
			</div><!-- end post -->
			
			<div class="post">
				<h2 class="title">Or register a new profile!</h2>
				<div class="entry">
				<p>Registration is quick and easy.</p>
<%
				if (request.getParameter("error") != null) {
%>
				<p><b><%= request.getParameter("error") %></b><p>	
<%
				}
%>
				<form action="/myapp/register" method="post">
					<ul>
						<li>User name:<input name="<%= RegisterServlet.USER_NAME%>"  cols="60"></input></li>
						<li>Password:<input type="password" name="<%= RegisterServlet.PASSWORD%>"  cols="60"></input></li>
						<li>Trader name:<input name="<%= RegisterServlet.TRADER_NAME %>" cols="60"></input></li>
					</ul>
					<input type="submit" value="Register" />
				</form>
				</div><!-- end entry -->
			</div><!-- end post -->
			
<%
	}
%>

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