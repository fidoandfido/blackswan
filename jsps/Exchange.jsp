<%@page import="net.fidoandfido.model.CompanyPeriodReport"%>
<%@page import="net.fidoandfido.dao.CompanyDAO"%>
<%@page import="net.fidoandfido.model.Company"%>
<%@page import="net.fidoandfido.dao.StockExchangeDAO"%>
<%@page import="net.fidoandfido.model.StockExchange"%>
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
	StockExchangeDAO exchangeDAO = new StockExchangeDAO();
	CompanyDAO companyDAO = new CompanyDAO();
	
	// See if there is a particular exchange, or if we are going to show
	// all of them.
	StockExchange exchange = null;
	//String exchangeName  = request.getParameter(Constants.EXCHANGE_NAME_PARM);
	String exchangeName  = request.getParameter("exchange_name");
	if (exchangeName != null) {
		exchange = exchangeDAO.getStockExchangeByName(exchangeName);
	}
	List<StockExchange> availableExchanges = exchangeDAO.getStockExchangeListForTrader(trader);
	if (exchange == null && availableExchanges.size() == 1) {
		exchange = availableExchanges.get(0);
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
				<li class="current_page_item"><a href="/myapp/Exchange.jsp">Exchanges</a></li>
				<li class="current_page_item"><a href="/myapp/logout">Log out</a><li>
			</ul>
		</div>
	</div>
</div>

<div id="page">
	<div id="content">

<%
if (exchange == null) {
%>
			<div class="post">
				<h2 class="title">Please select a stock exchange</h2>
				<div class="entry">
					<ul>
<%
	for (StockExchange currentExchange : availableExchanges) {
%>
					<li><a href="/myapp/Exchange.jsp?exchange_name=<%= currentExchange.getName() %>"><%= currentExchange.getName() %></a></li>
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
				<h2 class="title">Companies trading on <%= exchange.getName() %></h2>
				<div class="entry">
					Put info about the exchange here.
				
				</div>
			</div>
			<div class="post">
				<h2 class="title"><%= exchange.getName() %></h2>
				<div class="entry">
<%
		Iterable<Company> companyList = companyDAO.getCompaniesByExchange(exchange);

%>
					<table id="table-1">
					<thead>
					<tr>
					<td>Company</td>
					<td>Code</td>
					<td>Share<br/>Book<br/>value</td>
					<td>Earning<br/>per<br/>share</td>
					<td>Dividend</td>
					<td></td>
					<td>Last Trade</td>
					<td>Change</td>
					<td></td>
					<tr>
					</thead>
<%
		for (Company currentCompany : companyList) {
			if (!currentCompany.isTrading()) {
				continue;
			}
			CompanyPeriodReport currentPeriodReport = currentCompany.getCurrentPeriod();				
%>
					<tr>
					<td><a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=currentCompany.getCode()%>"><%= currentCompany.getName() %></a></td>
					<td>
					<script type="text/javascript">
					</script>
					<b onclick='javascript:popUpData("<%=currentCompany.getCode()%>");' style="cursor: pointer;" ><%=currentCompany.getCode()%></b>
					</td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getShareBookValue()) %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getPreviousEarningPerShare()) %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getPreviousDividend()) %></td>
					<td></td>
					<td>
						<b onclick='javascript:popUpGraph("<%=currentCompany.getCode()%>");' style="cursor: pointer;" >
						<%= WebPageUtil.formatCurrency(currentCompany.getLastTradePrice()) %></b>
					</td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getLastTradeChange()) %></td>
					<td><% if (currentCompany.getLastTradeChange() > 0) { %>
						<img src="/myapp/images/arrow-up.png"/>
						<% } else if (currentCompany.getLastTradeChange() < 0) { %>
						<img src="/myapp/images/arrow-down.png"/>
						<% } else if (currentCompany.getLastTradeChange() == 0) { %>
						<img src="/myapp/images/flat-line.png"/>
						<% }  %>
					</td>
					</tr>
<%	
		}
%>
					</table>
			
				
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