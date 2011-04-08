<%@page import="net.fidoandfido.dao.UserSessionDAO"%>
<%@page import="net.fidoandfido.model.User"%>
<%@page import="net.fidoandfido.model.UserSession"%>
<%@page import="net.fidoandfido.dao.HibernateUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="net.fidoandfido.servlets.SellSharesServlet"%>
<%@ page import="java.util.List"%>
<%@ page import="net.fidoandfido.dao.TraderDAO"%>
<%@ page import="net.fidoandfido.model.Trader"%>
<%@ page import="net.fidoandfido.util.WebPageUtil"%>

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

<html>
<%@ include file="webTemplates/header.txt" %>
<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>

<div id="page">
	<div id="content">
	
		<div class="post">
			<h2 class="title">Welcome <%=trader.getName()%>. Time to sell some shares!</h2>
			<div class="entry">
				<p>Your current balance is: <%=trader.getCash()%></p>
				<form action="/myapp/sellshares" method="post">
				<ul>
					<li>Company Code:<input name="<%=SellSharesServlet.COMPANY_CODE_PARM%>"  cols="60"></input></li>
					<li>Share Count:<input name="<%=SellSharesServlet.SHARE_COUNT%>"  cols="60"></input></li>
					<li>Asking price:<input name="<%=SellSharesServlet.ASKING_PRICE%>"  cols="60"></input></li>
				</ul>
				<input type="submit" value="Sell Shares" />
				</form>
			<br/>
			</div>
		</div>			
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