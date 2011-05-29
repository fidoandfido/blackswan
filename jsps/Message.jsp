<%@page import="net.fidoandfido.servlets.MessageServlet"%>
<%@page import="net.fidoandfido.engine.event.PeriodEventGenerator"%>
<%@page import="java.util.Map"%>
<%@page import="net.fidoandfido.model.PeriodEvent"%>
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
	User user = WebUtil.getCurrentUserBySession(request.getSession().getId());
	boolean isAdmin = user == null ? false : user.isUserAdmin();
	TraderDAO traderDAO = new TraderDAO();
	Trader trader = null;
	if (user != null) {
		trader = traderDAO.getTraderByUser(user);	
	}
	Date currentDate = new Date();

	
%>



<html>

<%@ include file="webTemplates/header.txt" %>
<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>

<div id="page">
	<div id="content">
<%
	if (user == null) {
%>
<p>Hello!
<a href="/myapp/Welcome.jsp">Sign in</a>
to access (or create) your trader profile.</p>
<%
	} else if (trader == null) {
		// Show trader registration form
%>
	<form action="/myapp/register" method="post">
		<div>Enter a name for you trader:<input name="trader_name"  cols="60"></input></div>
		<div><input type="submit" value="Create Trader" /></div>
	</form>
<%
	} else {
%>
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
			
<%
			if (isAdmin) {
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

<%			
		}
%>

	</div>
	<!-- end #content -->


<%= WebPageUtil.generateSideBar(trader, user) %>

	<div style="clear: both;">&nbsp;</div>
</div>
<!--  end page -->

<%@ include file="webTemplates/footer.txt" %>

</body>
</html>

<%
HibernateUtil.commitTransaction();
%>