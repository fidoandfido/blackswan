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
<%@page import="net.fidoandfido.model.Company" %>
<%@page import="net.fidoandfido.dao.CompanyDAO"%>
<%@page import="java.util.Collection"%>
<%@page import="net.fidoandfido.model.Trader"%>
<%@page import="net.fidoandfido.dao.TraderDAO"%>
<%@page import="net.fidoandfido.util.WebPageUtil"%>
<%@page import="net.fidoandfido.util.Constants"%>
<%@ page import="net.fidoandfido.servlets.BuySharesServlet"%>
<%@page session="true" %>
<%
	HibernateUtil.beginTransaction();
	User user = WebUtil.getCurrentUserBySession(request.getSession().getId());
	boolean isAdmin = user == null ? false : user.isUserAdmin();

	Trader trader = null;
	TraderDAO traderDAO = new TraderDAO();
	if (user != null) {
		trader = traderDAO.getTraderByUser(user);	
	}
	
	CompanyDAO companyDAO = new CompanyDAO();
	
	Company company = null;
	String companyCode = request.getParameter(Constants.COMPANY_CODE_PARM);
	if (companyCode != null) {
		company = companyDAO.getCompanyByCode(companyCode);
	}
	Date currentDate = new Date();
%>


<%@page import="net.fidoandfido.model.CompanyPeriodReport"%>
<%@page import="net.fidoandfido.dao.CompanyPeriodReportDAO"%>
<%@page import="java.util.Date"%>
<%@page import="net.fidoandfido.dao.StockExchangeDAO"%>
<%@page import="net.fidoandfido.model.PeriodEvent"%>
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
		if (company != null) {
	// Show company information!
	CompanyPeriodReport currentReport = company.getCurrentPeriod();
	Map<String, PeriodEvent> events = currentReport.getPeriodPartInformationMappedByEvent();
	PeriodEvent firstQuarterEvent = events.get(PeriodEventGenerator.FIRST_QUARTER);
	PeriodEvent secondQuarterEvent = events.get(PeriodEventGenerator.SECOND_QUARTER);
	PeriodEvent thirdQuarterEvent = events.get(PeriodEventGenerator.THIRD_QUARTER);
	PeriodEvent fourthQuarterEvent = events.get(PeriodEventGenerator.FOURTH_QUARTER);
%>
			<div class="post">
				<h2 class="title"><%= company.getName() %></h2>
				<div class="entry">
					<ul>
					<li>Company Name: <%= company.getName() %></li>
					<li>Company Code: <%= company.getCode() %></li>
					<li>Company Sector: <%= company.getSector() %></li>
					<li>Stock Exchange: <%= company.getStockExchange().getName() %></li>
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
					</ul>
					<b>Balance Sheet</b>
					<ul>
					<li>Assets: <%= WebPageUtil.formatCurrency(company.getAssetValue()) %></li>
					<li>Debts: <%= WebPageUtil.formatCurrency(company.getDebtValue()) %></li>
					<li>Capitalisation: <%= WebPageUtil.formatCurrency(company.getCapitalisation()) %>
					<li>Previous year profit: <%=WebPageUtil.formatCurrency(company.getPreviousProfit())%>					
					<li>Return: <%= (company.getPreviousProfit() * 100) / company.getCapitalisation()  %>%</li>
					</ul>
					<b>Share Information</b>
					<ul>
					<li>Last share trade price: <%= WebPageUtil.formatCurrency(company.getLastTradePrice()) %></li>
					<li>Share Book value: <%= WebPageUtil.formatCurrency(company.getCapitalisation() / company.getOutstandingShares()  ) %></li>
					<li>Projected Earning per share: <%= WebPageUtil.formatCurrency(company.getCurrentPeriod().getStartingExpectedExpenses() / company.getOutstandingShares() ) %></li>
					<li>Last dividend: <%= WebPageUtil.formatCurrency(company.getPreviousDividend()) %></li>
					<li>Starting Profit outlook: <%= currentReport == null ? WebPageUtil.formatCurrency(0) : WebPageUtil.formatCurrency(currentReport.getStartingExpectedProfit()) %></li>
					<li>Current Economic climate: <%= company.getStockExchange().getCurrentPeriod().getEconomicConditions() %></li>
					</ul>
<% 			if (currentReport != null) {
				if (firstQuarterEvent != null && currentDate.after(firstQuarterEvent.getDateInformationAvailable())) { 
					// Show the long term sector forecast...
%>

					<b>First quarter results</b>
					<ul>					
					<li>Headline: <%= firstQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= firstQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getProfit() + ( (currentReport.getStartingExpectedProfit() / 4) * 3) ) %></li>
					</ul>
<% 				}
				if (secondQuarterEvent != null && currentDate.after(secondQuarterEvent.getDateInformationAvailable())) { 
				
					// Show the long term company forecast...
%>
					<b>Second  quarter results</b>
					<ul>
					<li>Headline: <%= secondQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= secondQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getRunningProfit() + ((currentReport.getStartingExpectedProfit() / 4) * 2)) %></li>
					</ul>
<% 				}
				if (thirdQuarterEvent != null && currentDate.after(thirdQuarterEvent.getDateInformationAvailable())) { 
					// Show the short term sector forecast...
%>
					<b>Third quarter results</b>
					<ul>
					<li>Headline: <%= thirdQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= thirdQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getRunningProfit() + ((currentReport.getStartingExpectedProfit() / 4) * 1)) %></li>
					</ul>
		
<% 				}
				if (fourthQuarterEvent != null && currentDate.after(fourthQuarterEvent.getDateInformationAvailable())) { 
					// Show the short term company forecast...
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
				// And this is where we will put the chart!!! 
		TimeSeries sharePrice = new TimeSeries("Traded Value");
		TimeSeries bookValue = new TimeSeries("Book Value");
		TimeSeries earningPerShare = new TimeSeries("Earning Per Share");
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		CompanyPeriodReportDAO companyPeriodReportDAO = new CompanyPeriodReportDAO();
		List<CompanyPeriodReport> reportList = companyPeriodReportDAO.getRecentPeriodReportListByCompany(company, 10);
		for (CompanyPeriodReport report : reportList) {
			earningPerShare.add(new Second(report.getStartDate()), report.getFinalProfit() / report.getOutstandingShareCount());
			bookValue.add(new Second(report.getStartDate()), (report.getStartingAssets() - report.getStartingDebt()) / report.getOutstandingShareCount());
			// expectedProfitSeries.add(new Year((int) report.getGeneration() +
			// 2000), report.getStartingExpectedProfit());
		}
		TradeRecordDAO tradeRecordDAO = new TradeRecordDAO();
		List<TradeRecord> recordList = tradeRecordDAO.getLastTradeRecords(company, 200);

		for (TradeRecord record : recordList) {
			sharePrice.addOrUpdate(new Second(record.getDate()), record.getSharePrice());
			// expectedProfitSeries.add(new Year((int) report.getGeneration() +
			// 2000), report.getStartingExpectedProfit());
		}

		dataset.addSeries(bookValue);
		dataset.addSeries(sharePrice);
		dataset.addSeries(earningPerShare);
		

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Share Price", // title
				"Date", // x-axis label
				"Price (cents)", // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		
		session.setAttribute(GraphServlet.CHART_ATTRIBUTE, chart);
		
%>
				
				<img src="/myapp/graph"/>
				
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
				<li>Golden age? <%= company.getRemainingPeriodsOfGoldenAge() > 0 ? "Yes" : "No" %></li>
				<li>Dark age? <%= company.getRemainingPeriodsOfDarkAge() > 0 ? "Yes" : "No" %></li>
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
<%
			}
%>				

<%			
		} else {
			StockExchangeDAO stockExchangeDAO = new StockExchangeDAO();
			Collection<StockExchange> exchangeList = stockExchangeDAO.getStockExchangeList();
			// List all the companies
			for (StockExchange exchange : exchangeList) {
				Iterable<Company> companyList = companyDAO.getCompaniesByExchange(exchange);
				if (companyList != null && companyList.iterator().hasNext()) {
%>
			<div class="post">
				<h2 class="title">Companies on <%= exchange.getName() %> (Showing <%= exchange.getCompanyCount() %> entries)</h2>
				<div class="entry">
				Current Economic Conditions: <%= exchange.getCurrentPeriod().getEconomicConditions() %><p>
				Prime Interest Rate: <%=  ( exchange.getPrimeInterestRateBasisPoints() +  exchange.getCurrentPeriod().getInterestRateBasisPointsDelta() ) / 100 %> %			
				
					<table>
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
						CompanyPeriodReport currentPeriodReport = currentCompany.getCurrentPeriod();				
%>
					<tr>
					<td><a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=currentCompany.getCode()%>"><%= currentCompany.getName() %></a></td>
					<td><%= currentCompany.getCode() %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getShareBookValue()) %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getPreviousEarningPerShare()) %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getPreviousDividend()) %></td>
					<td></td>
					<td><b><%= WebPageUtil.formatCurrency(currentCompany.getLastTradePrice()) %></b></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getLastTradeChange()) %></td>
					<td><% if (currentCompany.getLastTradeChange() > 0) { %>
						<img src="/myapp/images/arrow-up.png"/>
						<% } else if (currentCompany.getLastTradeChange() < 0) { %>
						<img src="/myapp/images/arrow-down.png"/>
						<% } else if (currentCompany.getLastTradeChange() == 0) { %>
						<img src="/myapp/images/flat-line.png"/>
						<% }  %></td>
					</tr>
<%	
					}
%>
					</table>
				</div>
			</div>
<%
				} else {
		// No companies!
%>
		<div class="post">
			<h2 class="title">No Companies</h2>
			<div class="entry">
				There are no companies to show. This could be because there are no companies
				defined in the data store, or because there was a problem accessing them. I am
				sure this will be sorted out presently. 	
			</div>
		</div>
<%
				}
			}
		}
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