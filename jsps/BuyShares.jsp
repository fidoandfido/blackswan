<%@page import="net.fidoandfido.dao.UserSessionDAO"%>
<%@page import="net.fidoandfido.model.UserSession"%>
<%@page import="net.fidoandfido.model.User"%>
<%@page import="net.fidoandfido.dao.HibernateUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="net.fidoandfido.servlets.BuySharesServlet"%>
<%@ page import="java.util.List"%>
<%@ page import="net.fidoandfido.dao.TraderDAO"%>
<%@ page import="net.fidoandfido.model.Trader"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<html>

<%	
	HibernateUtil.beginTransaction();
	User user = null;
	Trader trader = null;

	UserSession userSession = UserSessionDAO .getUserSessionBySessionId(request.getSession().getId());

	if (userSession != null && userSession.isActive()) {
		user = userSession.getUser();
		trader = user.getTrader();
	}
	
	if (user == null || trader == null) {
		response.sendRedirect("/myapp/Welcome.jsp");
		HibernateUtil.commitTransaction();
		return;
	}
	
%>


<%@ include file="webTemplates/header.txt" %>
<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>



<div id="page">
	<div id="content">

		<div class="post">
			<h2 class="title">Welcome <%=trader.getName()%>.</h2>
			<div class="entry">
				<p>Your current balance is: <%=trader.getCash()%></p>
				<form action="/myapp/buyshares" method="post">
				<ul>
					<li>Please enter all fields!</li>
					<li>Company Code:<input name="<%=BuySharesServlet.COMPANY_CODE_PARM%>"  cols="60"></input></li>
					<li>Share Count:<input name="<%=BuySharesServlet.SHARE_COUNT%>"  cols="60"></input></li>
					<li>Offer price:<input name="<%=BuySharesServlet.OFFER_PRICE%>"  cols="60"></input></li>
				</ul>
				<input type="submit" value="Buy Shares" />
				</form>
			<br/>
			</div>
		</div>
<%
	
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