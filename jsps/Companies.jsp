<%@page import="net.fidoandfido.charts.CompanyProfitChartGenerator"%>
<%@page import="net.fidoandfido.charts.SharePriceChartGenerator"%>
<%@page import="net.fidoandfido.model.UserSession"%>
<%@page import="net.fidoandfido.dao.UserSessionDAO"%>
<%@page import="net.fidoandfido.model.Order"%>
<%@page import="net.fidoandfido.dao.OrderDAO"%>
<%@page import="net.fidoandfido.model.ShareParcel"%>
<%@page import="net.fidoandfido.dao.ShareParcelDAO"%>
<%@page import="net.fidoandfido.servlets.GraphServlet"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.jfree.chart.axis.DateAxis"%>
<%@page import="org.jfree.chart.renderer.xy.XYLineAndShapeRenderer"%>
<%@page import="org.jfree.chart.renderer.xy.XYItemRenderer"%>
<%@page import="org.jfree.ui.RectangleInsets"%>
<%@page import="org.jfree.chart.plot.XYPlot"%>
<%@page import="java.awt.Color"%>
<%@page import="org.jfree.chart.ChartFactory"%>
<%@page import="org.jfree.chart.JFreeChart"%>
<%@page import="net.fidoandfido.model.TradeRecord"%>
<%@page import="net.fidoandfido.dao.TradeRecordDAO"%>
<%@page import="org.jfree.data.time.Second"%>
<%@page import="org.jfree.data.time.TimeSeriesCollection"%>
<%@page import="org.jfree.data.time.TimeSeries"%>
<%@page import="net.fidoandfido.model.StockExchange"%>
<%@page import="net.fidoandfido.engine.quarter.QuarterEventGenerator"%>
<%@page import="java.util.Map"%>
<%@page import="net.fidoandfido.model.PeriodQuarter"%>
<%@page import="net.fidoandfido.dao.HibernateUtil"%>
<%@page import="net.fidoandfido.model.User"%>
<%@page import="net.fidoandfido.util.WebUtil"%>
<%@page import="java.util.List"%>
<%@page import="net.fidoandfido.model.Company" %>
<%@page import="net.fidoandfido.dao.CompanyDAO"%>
<%@page import="java.util.Collection"%>
<%@page import="net.fidoandfido.model.Trader"%>
<%@page import="net.fidoandfido.dao.TraderDAO"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<%@page import="net.fidoandfido.util.Constants"%>
<%@page import="net.fidoandfido.servlets.BuySharesServlet"%>
<%@page import="net.fidoandfido.model.CompanyPeriodReport"%>
<%@page import="net.fidoandfido.dao.CompanyPeriodReportDAO"%>
<%@page import="java.util.Date"%>
<%@page import="net.fidoandfido.dao.StockExchangeDAO"%>
<%@page import="net.fidoandfido.model.PeriodQuarter"%>
<%@page import="net.fidoandfido.dao.PeriodPartInformationDAO"%>
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
	boolean isAdmin = user == null ? false : user.isUserAdmin();
	
	CompanyDAO companyDAO = new CompanyDAO();
	Company company = null;
	String companyCode = request.getParameter(Constants.COMPANY_CODE_PARM);
	if (companyCode != null) {
		company = companyDAO.getCompanyByCode(companyCode);
	}
	if (company == null) {
		response.sendRedirect("/myapp/Trader.jsp");
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
	<!--  end #logo -->
<%@ include file="webTemplates/pageHeaderA.txt" %>
</div>


<div id="page">
	<div id="content">
<%

%>
<%
	if (!company.isTrading()) {
%>
				<div class="post">
				<h2 class="title"><%=company.getName()%></h2>
				<div class="entry">
					<ul>
					<li>Company Name: <%=company.getName()%></li>
					<li>Company Code: <%=company.getCode()%></li>
					<li>Company Sector: <%=company.getSector()%></li>
					<li>Stock Exchange: <%=company.getStockExchange().getName()%></li>
					<li>Company Status: <%=company.getCompanyStatus()%></li>
				</div>
			</div>	
<%
	} else {
		
		// Show company information!
		CompanyPeriodReport currentReport = company.getCurrentPeriod();
		Map<String, PeriodQuarter> events = currentReport.getPeriodPartInformationMappedByEvent();
		PeriodQuarter firstQuarterEvent = events.get(PeriodQuarter.FIRST_QUARTER);
		PeriodQuarter secondQuarterEvent = events.get(PeriodQuarter.SECOND_QUARTER);
		PeriodQuarter thirdQuarterEvent = events.get(PeriodQuarter.THIRD_QUARTER);
		PeriodQuarter fourthQuarterEvent = events.get(PeriodQuarter.FOURTH_QUARTER);
%>
			<div class="post">
				<h2 class="title"><%= company.getName() %></h2>
				<div class="entry">
					<ul>
					<li>Company Code: <%= company.getCode() %></li>
					<li>Company Sector: <%= company.getSector() %></li>
					<li>Stock Exchange: <%= company.getStockExchange().getName() %></li>
					<li>Company Status: <%= company.getCompanyStatus() %></li>
					</ul>
					
					<!--  Balance sheet -->
					<!-- Previous profit loss statement -->
					<p><b>Balance Sheet</b></p>
					<table class="table-balance-sheet">
					<tr>
						<th colspan='3'>Balance Sheet</th>
					</tr>
					<tr>
						<td class="table-balance-sheet-label">Assets</td>
						<td class="table-balance-sheet-value"><%=WebPageUtil.formatCurrency(company.getAssetValue())%></td>
						
					</tr>
					<tr>
						<td class="table-balance-sheet-label">Debts</td>
						<td class="table-balance-sheet-value">(<%=WebPageUtil.formatCurrency(company.getDebtValue())%>)</td>
					</tr>
					<tr>
						<td class="table-balance-sheet-label">Equity</td>
						<td class="table-balance-sheet-value"><%=WebPageUtil.formatCurrency(company.getCapitalisation())%></td>
					</tr>
					</table>
					
<%
			if (company.getPreviousPeriodReport() != null) {
%>					
					<!-- Previous profit loss statement -->
					<p><b>Previous Year Profit Loss Statement</b></p>
					<table class="table-balance-sheet">
					<tr>
						<th></th>
						<th>Outgoing</th>
						<th>Incoming</th>
					</tr>
					<tr>
						<td class="table-balance-sheet-label">Revenues</td>
						<td></td>
						<td class="table-balance-sheet-value"><%=WebPageUtil.formatCurrency(company.getPreviousPeriodReport().getFinalRevenue())%></td>
					</tr>
					<tr>
						<td class="table-balance-sheet-label">Expenses</td>
						<td class="table-balance-sheet-value"><%=WebPageUtil.formatCurrency(company.getPreviousPeriodReport().getFinalExpenses())%></td>
						<td></td>
					</tr>
					<tr>
						<td class="table-balance-sheet-label">Interest</td>
						<td class="table-balance-sheet-value"><%=WebPageUtil.formatCurrency(company.getPreviousPeriodReport().getFinalInterest())%></td>
						<td></td>
					</tr>
					<tr>
						<td class="table-balance-sheet-label">Final Profit</td>
						<td></td>
						<td class="table-balance-sheet-value"><%=WebPageUtil.formatCurrency(company.getPreviousPeriodReport().getFinalProfit())%></td>
					</tr>
					</table>
<%
			}
%>	
					<p><b>Share Information</b></p>
					<ul>
					<li>Company Outstanding shares: <%= company.getOutstandingShares() %></li>
					<li>Dividend Scheme: 
<%			if (company.isNeverPayDividend()) {  %>					
					Never pays dividend.
<%			} else if (company.isAlwaysPayDividend()) {   %>
					Always pays dividend. </li><li>Rate: <%= company.getDividendRate() %>% of profits, miminum dividend: <%= WebPageUtil.formatCurrency(company.getMinimumDividend()) %>
<%			} else { %>					
					Dividend paid when profits allow.  </li><li>Rate: <%= company.getDividendRate() %>% of profits.
<%			} %>										
					</li>
					
					<li>Last share trade price: <%= WebPageUtil.formatCurrency(company.getLastTradePrice()) %></li>
					<li>Share Book value: <%= WebPageUtil.formatCurrency(company.getCapitalisation() / company.getOutstandingShares()  ) %></li>
					<li>Projected Earning per share: <%= WebPageUtil.formatCurrency(company.getCurrentPeriod().getStartingExpectedProfit() / company.getOutstandingShares() ) %> ( 
						<%= ((company.getCurrentPeriod().getStartingExpectedProfit() * 100 / company.getOutstandingShares()) / company.getShareBookValue() )  %> 
						% return)</li>
					<li>Last dividend: <%= WebPageUtil.formatCurrency(company.getPreviousDividend()) %></li>
					<li>Starting Profit outlook: <%= currentReport == null ? WebPageUtil.formatCurrency(0) : WebPageUtil.formatCurrency(currentReport.getStartingExpectedProfit()) %></li>
					<li>Current Economic climate: <%= company.getStockExchange().getCurrentPeriod().getEconomicConditions() %></li>
					</ul>
<% 			if (currentReport != null) {
				if (firstQuarterEvent != null && currentDate.after(firstQuarterEvent.getDateInformationAvailable())) { 
					// Show the first quarter results...
%>

					<b>First quarter results</b>
					<ul>					
					<li>Headline: <%= firstQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= firstQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getProfit() + ( (currentReport.getStartingExpectedProfit() / 4) * 3) ) %></li>
					</ul>
<%
				}
				if (secondQuarterEvent != null && currentDate.after(secondQuarterEvent.getDateInformationAvailable())) { 
					// Show the second quarter results...
%>
					<b>Second  quarter results</b>
					<ul>
					<li>Headline: <%= secondQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= secondQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getRunningProfit() + ((currentReport.getStartingExpectedProfit() / 4) * 2)) %></li>
					</ul>
<%
				}
				if (thirdQuarterEvent != null && currentDate.after(thirdQuarterEvent.getDateInformationAvailable())) { 
					// Show the third quarter results...
%>
					<b>Third quarter results</b>
					<ul>
					<li>Headline: <%= thirdQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= thirdQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getRunningProfit() + ((currentReport.getStartingExpectedProfit() / 4) * 1)) %></li>
					</ul>
		
<%
				}
				if (fourthQuarterEvent != null && currentDate.after(fourthQuarterEvent.getDateInformationAvailable())) { 
					// Show the fourth quarter results...
%>
					<b>Fourth quarter results</b>
					<ul>
					<li>Headline: <%= fourthQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= fourthQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(fourthQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(fourthQuarterEvent.getRunningProfit()) %></li>
					</ul>
<% 				}%>				
<% 			} %>

				Your current balance is: <%=WebPageUtil.formatCurrency(trader.getCash())%><br/>
				Last sale price: <%=WebPageUtil.formatCurrency(company.getLastTradePrice()) %><br/>
				Maximum buy count: <%= trader.getCash() / company.getLastTradePrice() %>
				
				<form action="/myapp/buyshares" method="post">
				<input type="hidden" name="<%=BuySharesServlet.COMPANY_CODE_PARM%>" value="<%=company.getCode()%>"></input></li>
				<ul>
					<li>Share Count:<input name="<%=BuySharesServlet.SHARE_COUNT%>"  cols="60"></input></li>
					<li>Offer price (in cents):<input name="<%=BuySharesServlet.OFFER_PRICE%>" value="<%=company.getLastTradePrice() %>" cols="60"></input></li>
				</ul>
				<input type="submit" value="Buy Shares" />
				</form>

<%
			String graphType  = "sharePrice";
			SharePriceChartGenerator chartGenerator = new SharePriceChartGenerator();
			session.setAttribute(GraphServlet.CHART_ATTRIBUTE + company.getCode() + graphType, chartGenerator.generateChart(company));
			
%>
				
				<img src="/myapp/graph?<%=GraphServlet.COMPANY_CODE%>=<%=company.getCode()%>&<%=GraphServlet.GRAPH_TYPE%>=<%=graphType%>"/>
			<p></p>
<%
			String profitGraphType  = "operatingProfit";
			CompanyProfitChartGenerator profitChartGenerator = new CompanyProfitChartGenerator();
			session.setAttribute(GraphServlet.CHART_ATTRIBUTE + company.getCode() + profitGraphType, profitChartGenerator.generateChart(company));
			
%>
				
				<img src="/myapp/graph?<%=GraphServlet.COMPANY_CODE%>=<%=company.getCode()%>&<%=GraphServlet.GRAPH_TYPE%>=<%=profitGraphType%>"/>
				
				</div>
			</div>	
<%
			if (isAdmin) {
				// Show the time that all the bits will be available.
%>

			<div class="post">
				<h2 class="title">Admin Information: <%= company.getName() %></h2>
				<div class="entry">
				<ul>
				<li>Current Period: <%= currentReport == null ? 0 : currentReport.getGeneration() %></li>
				<li>Current Period must end after: <%=  currentReport == null ? "NA" : currentReport.getMinimumEndDate().toString() %></li>
				<li>Revenue rate: <%= company.getRevenueRate() + company.getStockExchange().getCurrentPeriod().getRevenueRateDelta() %>%</li>
				<li>Expense rate: <%= company.getExpenseRate() + company.getStockExchange().getCurrentPeriod().getExpenseRateDelta() %>%</li>
				<li>Interest rate: <%= company.getStockExchange().getPrimeInterestRateBasisPoints() %> Basis points</li>
				<li>Company Profile: <%= company.getCompanyProfile() %></li>
				<li><b>FIRST QUARTER</b></li>					
				<li>First Quarter available after: <%= firstQuarterEvent.getDateInformationAvailable().toString() %></li>
				<li>event type: <%= firstQuarterEvent.getEventType() %> </li>
				<li>revenue: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getRevenue()) %></li>
				<li>expenses: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getExpenses()) %></li>
				<li>interest: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getInterest()) %></li>
				<li>quarter profit: <%=WebPageUtil.formatCurrency(firstQuarterEvent.getProfit())%></li>
				<li><b>SECOND QUARTER</b></li>
				<li>Second quarter available after: <%=secondQuarterEvent.getDateInformationAvailable().toString()%></li>
				<li>event type: <%=secondQuarterEvent.getEventType()%></li>
				<li>revenue: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getRevenue()) %></li>
				<li>expenses: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getExpenses()) %></li>
				<li>interest: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getInterest()) %></li>
				<li>quarter profit: <%=WebPageUtil.formatCurrency(secondQuarterEvent.getProfit())%></li>
				<li><b>THIRD QUARTER</b></li>
				<li>Third quarter available: <%=thirdQuarterEvent.getDateInformationAvailable().toString()%></li>
				<li>event type: <%=thirdQuarterEvent.getEventType()%> </li>
				<li>revenue: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getRevenue()) %></li>
				<li>expenses: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getExpenses()) %></li>
				<li>interest: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getInterest()) %></li>
				<li>quarter profit: <%=WebPageUtil.formatCurrency(thirdQuarterEvent.getProfit())%></li>
				<li><b>FOURTH QUARTER</b></li>
				<li>Fourth quarter available: <%=fourthQuarterEvent.getDateInformationAvailable().toString()%></li>
				<li>event type: <%=fourthQuarterEvent.getEventType()%></li>
				<li>revenue: <%= WebPageUtil.formatCurrency(fourthQuarterEvent.getRevenue()) %></li>
				<li>expenses: <%= WebPageUtil.formatCurrency(fourthQuarterEvent.getExpenses()) %></li>
				<li>interest: <%= WebPageUtil.formatCurrency(fourthQuarterEvent.getInterest()) %></li>
				<li>quarter profit: <%=WebPageUtil.formatCurrency(fourthQuarterEvent.getProfit())%></li>
				<li><b>Final profit: <%= WebPageUtil.formatCurrency(currentReport.getFinalProfit()) %></b></li>
					</ul>
				</div>
			</div>

			<div class="post">
				<h2 class="title">Admin Information - Last trades</h2>
				<div class="entry">
				<table>
<%

			OrderDAO orderDAO = new OrderDAO();
			Iterable<Order> orders = orderDAO.getLastExecutedOrders(company, 30);
			for (Order order : orders)	{
%>
				<tr>
					<td><%= order.getTrader().getName() %></td>
					<td><%= WebPageUtil.formatCurrency(order.getOfferPrice()) %></td>
					<td><%= order.getOriginalShareCount() %></td>
					<td><%= order.getOrderType() %></td>
					<td><%= order.getTrader().getAiStrategyName() %></td>
				</tr>
<%
			}
%>
				</table>
				</div>
			</div>	

			
			<div class="post">
				<h2 class="title">Admin Information - Holdings</h2>
				<div class="entry">
				<table>
<%

			ShareParcelDAO shareParcelDAO = new ShareParcelDAO();
			Iterable<ShareParcel> holdings = shareParcelDAO.getHoldingsByCompany(company);
			for (ShareParcel holding : holdings)	{
%>
				<tr>
					<td><%= holding.getTrader().getName() %></td>
					<td><%= holding.getTrader().isAITrader() %></td>
					<td>
<% 
				if (holding.getTrader().isAITrader()) {
%>
						<%= holding.getTrader().getAiStrategyName() %>
<%
					trader.getAiStrategyName();
				} else {
%>
					---
<%
				}
%>						 					
					</td>
					<td><%= holding.getShareCount() %></td>
					<td><%= WebPageUtil.formatCurrency(holding.getPurchasePrice()) %></td>
				</tr>
<%
			}
%>
				</table>
				</div>
			</div>	
<%
		}
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