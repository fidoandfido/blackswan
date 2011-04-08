<%@page import="net.fidoandfido.dao.CompanyDAO"%>
<%@page import="net.fidoandfido.model.Company"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.List"%>
<%@page import="net.fidoandfido.model.Order" %>
<%@page import="net.fidoandfido.dao.OrderDAO"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="java.util.Collection"%>
<%@page import="com.google.appengine.api.users.User"%>
<%@page import="net.fidoandfido.model.Trader"%>
<%@page import="net.fidoandfido.dao.TraderDAO"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<%@page import="net.fidoandfido.util.Constants"%>

<%
	
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null) {
		response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
	}
	boolean isAdmin = userService.isUserAdmin();
	Trader trader = null;
	if (user != null) {
		trader = TraderDAO.getTraderByUser(user);	
	}
	Order order = null;
	Date currentDate = new Date();
%>


<%@page import="net.fidoandfido.model.CompanyPeriodReport"%>
<%@page import="net.fidoandfido.dao.CompanyPeriodReportDAO"%>
<%@page import="java.util.Date"%>
<%@page import="net.fidoandfido.dao.StockExchangeDAO"%>
<%@page import="net.fidoandfido.model.PeriodPartInformation"%>
<%@page import="net.fidoandfido.dao.PeriodPartInformationDAO"%><html>

<%@ include file="webTemplates/header.txt" %>
<%@ include file="webTemplates/pageHeaderA.txt" %>
<%@ include file="webTemplates/pageHeaderB.txt" %>

<div id="page">
	<div id="content">
<%
	if (user == null) {
%>
<p>Hello!
<a href="<%=userService.createLoginURL(request.getRequestURI())%>">Sign in</a>
to access (or create) your trader profile.</p>
<% 
	} else if (trader == null) {
		// Show trader registration form
%>
	<form action="/register" method="post">
		<div>Enter a name for you trader:<input name="trader_name"  cols="60"></input></div>
		<div><input type="submit" value="Create Trader" /></div>
	</form>
<% 
	} else {
	
		// List all the companies
		Collection<Order> orderList = OrderDAO.getAllOrders();
		if (orderList != null && orderList.size() != 0) {
%>
			<div class="post">
				<h2 class="title">Orders (Showing <%= orderList.size() %> entries)</h2>
				<div class="entry">
					<ul>
<%	
			for (Order currentOrder: orderList) {
				Company company = CompanyDAO.getCompanyByKey(order.getCompanyKey());
				Trader orderTrader = TraderDAO.getTraderByKey(order.getTraderKey());
%>
					<li>
	Company Name: <a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=company.getCode()%>"><%= company.getName() %></a><br/>			
	Company Outstanding shares: <%= company.getOutstandingShares() %><br/>
	Order Offer price <%= order.getOfferPrice() %><br/>
	Order share count<%= order.getOriginalShareCount() %><br/>
	Order remaining share count <%= order.getRemainingShareCount() %><br/>
	Order Date created<%= order.getDateCreated() %><br/>
	Order Date executed<%= order.getDateExecuted() %><br/>
	Order type <%= order.getOrderType() %><br/>
						</li>
<%	
			}
%>
					</ul>
				</div>
			</div>
<%
		} else {
	// No companies!
%>
		<div class="post">
			<h2 class="title">No Orders!</h2>
			<div class="entry">
				There are no orders to show. This could be because there are no orders
				defined in the data store, or because there was a problem accessing them. I am
				sure this will be sorted out presently. 	
			</div>
		</div>
<%
		} 
	}

%>
	</div>
	<!-- end #content -->


<%= WebPageUtil.generateSideBar(trader, userService) %>

	<div style="clear: both;">&nbsp;</div>
</div>
<!--  end page -->

<%@ include file="webTemplates/footer.txt" %>

</body>
</html>