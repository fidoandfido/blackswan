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
	if (user != null) {
		trader = TraderDAO.getTraderByUser(user);	
	}
	Company company = null;
	String companyCode = request.getParameter(Constants.COMPANY_CODE_PARM);
	if (companyCode != null) {
		company = CompanyDAO.getCompanyByCode(companyCode);
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
					<li>Company Outstanding shares: <%= company.getOutstandingShares() %></li>
					<li>Stock Exchange: <%= company.getStockExchange().getName() %></li>
					<li>Last share trade price: <%= WebPageUtil.formatCurrency(company.getLastTradePrice()) %></li>
					<li>Earning per share: <%= WebPageUtil.formatCurrency(company.getCurrentPeriod().getStartingExpectedExpenses() / company.getOutstandingShares() ) %></li>
					<li>Dividend Scheme: 
<%			if (company.isNeverPayDividend()) {  %>					
						Never pays dividend.
<%			} else if (company.isAlwaysPayDividend()) {   %>
						Always pays dividend. </li><li>Rate: <%= company.getDividendRate() %>% of profits, miminum dividend: <%= WebPageUtil.formatCurrency(company.getMinimumDividend()) %>
<%			} else { %>					
						Dividend paid when profits allow.  </li><li>Rate: <%= company.getDividendRate() %>% of profits.
<%			} %>										
					</li>
					<li>Assets: <%= WebPageUtil.formatCurrency(company.getAssetValue()) %></li>
					<li>Debts: <%= WebPageUtil.formatCurrency(company.getDebtValue()) %></li>
					<li>Previous year profit: <%=WebPageUtil.formatCurrency(company.getPreviousProfit())%>					
					<li>Last dividend: <%= WebPageUtil.formatCurrency(company.getPreviousDividend()) %></li>
					<li>Starting Profit outlook: <%= currentReport == null ? WebPageUtil.formatCurrency(0) : WebPageUtil.formatCurrency(currentReport.getStartingExpectedProfit()) %></li>
<% 			if (currentReport != null) { %>
					<li>Period ID: <%= currentReport.getGeneration() %></li>
					
							
<% 				if (firstQuarterEvent != null && currentDate.after(firstQuarterEvent.getDateInformationAvailable())) { 
					// Show the long term sector forecast...
%>
					<li><b>First quarter results</b></li>
					<li>Headline: <%= firstQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= firstQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(firstQuarterEvent.getProfit() + ( (currentReport.getStartingExpectedProfit() / 4) * 3) ) %></li>
<% 				}
				if (secondQuarterEvent != null && currentDate.after(secondQuarterEvent.getDateInformationAvailable())) { 
				
					// Show the long term company forecast...
%>
					<li><b>Second  quarter results</b></li>
					<li>Headline: <%= secondQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= secondQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(secondQuarterEvent.getRunningProfit() + ((currentReport.getStartingExpectedProfit() / 4) * 2)) %></li>
<% 				}
				if (thirdQuarterEvent != null && currentDate.after(thirdQuarterEvent.getDateInformationAvailable())) { 
					// Show the short term sector forecast...
%>
					<li><b>Third quarter results</b></li>
					<li>Headline: <%= thirdQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= thirdQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(thirdQuarterEvent.getRunningProfit() + ((currentReport.getStartingExpectedProfit() / 4) * 1)) %></li>
		
<% 				}
				if (fourthQuarterEvent != null && currentDate.after(fourthQuarterEvent.getDateInformationAvailable())) { 
					// Show the short term company forecast...
%>
					<li><b>Fourth quarter results</b></li>
					<li>Headline: <%= fourthQuarterEvent.getMessage() %></li>
					<li>Analyst reaction: <%= fourthQuarterEvent.getEventType() %></li>
					<li>Quarter Profit: <%= WebPageUtil.formatCurrency(fourthQuarterEvent.getProfit()) %></li>
					<li>Updated Projected Yearly Profit: <%= WebPageUtil.formatCurrency(fourthQuarterEvent.getRunningProfit()) %></li>
<% 				}%>				
<% 			} %>
					
					</ul>

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
				<li>Long term sector outlook available after: <%= firstQuarterEvent.getDateInformationAvailable().toString() %></li>
				<li>event type: <%= firstQuarterEvent.getEventType() %> </li>
				<li>updated profit: <%=WebPageUtil.formatCurrency(firstQuarterEvent.getProfit())%></li>
				<li>Long term company outlook available after: <%=secondQuarterEvent.getDateInformationAvailable().toString()%></li>
				<li>event type: <%=secondQuarterEvent.getEventType()%></li>
				<li>updated profit: <%=WebPageUtil.formatCurrency(secondQuarterEvent.getProfit())%></li>
				<li>Short term sector outlook available: <%=thirdQuarterEvent.getDateInformationAvailable().toString()%></li>
				<li>event type: <%=thirdQuarterEvent.getEventType()%> </li>
				<li>updated profit: <%=WebPageUtil.formatCurrency(thirdQuarterEvent.getProfit())%></li>
				<li>Short term company outlook available: <%=fourthQuarterEvent.getDateInformationAvailable().toString()%></li>
				<li>event type: <%=fourthQuarterEvent.getEventType()%></li>
				<li>updated profit: <%=WebPageUtil.formatCurrency(fourthQuarterEvent.getProfit())%></li>
				
				
					</ul>
				</div>
			</div>	
<%
			}
%>				

<%			
		} else {
			Collection<StockExchange> exchangeList = StockExchangeDAO.getStockExchangeList();
			// List all the companies
			for (StockExchange exchange : exchangeList) {
				Iterable<Company> companyList = CompanyDAO.getCompaniesByExchange(exchange);
				if (companyList != null && companyList.iterator().hasNext()) {
%>
			<div class="post">
				<h2 class="title">Companies on <%= exchange.getName() %> (Showing <%= exchange.getCompanyCount() %> entries)</h2>
				<div class="entry">
					<table>
					<tr>
					<td>Company</td>
					<td>Code</td>
					<td>Last Trade</td>
					<td>Change</td>
					<td></td>
					<td>Prev. Dividend</td>
					</tr>

<%
					for (Company currentCompany : companyList) {
						CompanyPeriodReport currentPeriodReport = currentCompany.getCurrentPeriod();				
%>
					<tr>
					<td><a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=currentCompany.getCode()%>"><%= currentCompany.getName() %></a></td>
					<td><%= currentCompany.getCode() %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getLastTradePrice()) %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getLastTradeChange()) %></td>
					<td><% if (currentCompany.getLastTradeChange() > 0) { %>
						<img src="/myapp/images/arrow-up.png"/>
						<% } else if (currentCompany.getLastTradeChange() < 0) { %>
						<img src="/myapp/images/arrow-down.png"/>
						<% } else if (currentCompany.getLastTradeChange() == 0) { %>
						<img src="/myapp/images/flat-line.png"/>
						<% }  %></td>
					<td><%= WebPageUtil.formatCurrency(currentCompany.getPreviousDividend()) %></td>
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