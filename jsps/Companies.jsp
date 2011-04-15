<%@page import="net.fidoandfido.engine.PeriodEventGenerator"%>
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
			PeriodEvent longTermSectorInformation = events.get(PeriodEventGenerator.LONG_TERM_SECTOR);
			PeriodEvent longTermCompanyInformation = events.get(PeriodEventGenerator.LONG_TERM_COMPANY);
			PeriodEvent shortTermSectorInformation = events.get(PeriodEventGenerator.SHORT_TERM_SECTOR);
			PeriodEvent shortTermCompanyInformation = events.get(PeriodEventGenerator.SHORT_TERM_COMPANY);
			
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
					<li>Starting Expected Profit: <%= currentReport == null ? WebPageUtil.formatCurrency(0) : WebPageUtil.formatCurrency(currentReport.getStartingExpectedProfit()) %></li>
					
<% 			if (currentReport != null) { %>
					<li>Period ID: <%= currentReport.getGeneration() %></li>
					
							
<% 				if (longTermSectorInformation != null && currentDate.after(longTermSectorInformation.getDateInformationAvailable())) { 
					// Show the long term sector forecast...
%>
					<li><b>Long Term Sector Forecast</b></li>
					<li>Headline: <%= longTermSectorInformation.getMessage() %></li>
					<li>Analyst reaction: <%= longTermSectorInformation.getEventType() %></li>	
<% 				}
				if (longTermCompanyInformation != null && currentDate.after(longTermCompanyInformation.getDateInformationAvailable())) { 
					// Show the long term company forecast...
%>
					<li><b>Long Term Company Forecast</b></li>
					<li>Headline: <%= longTermCompanyInformation.getMessage() %></li>
					<li>Analyst reaction: <%= longTermCompanyInformation.getEventType() %></li>	
<% 				}
				if (shortTermSectorInformation != null && currentDate.after(shortTermSectorInformation.getDateInformationAvailable())) { 
					// Show the short term sector forecast...
%>
					<li><b>Short Term Sector Forecast</b></li>
					<li>Headline: <%= shortTermSectorInformation.getMessage() %></li>
					<li>Analyst reaction: <%= shortTermSectorInformation.getEventType() %></li>
		
<% 				}
				if (shortTermCompanyInformation != null && currentDate.after(shortTermCompanyInformation.getDateInformationAvailable())) { 
					// Show the short term company forecast...
%>
					<li><b>Short Term Company Forecast</b></li>
					<li>Headline: <%= shortTermCompanyInformation.getMessage() %></li>
					<li>Analyst reaction: <%= shortTermCompanyInformation.getEventType() %></li>
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
				<li>Long term sector outlook available after: <%= longTermSectorInformation.getDateInformationAvailable().toString() %></li>
				<li>event type: <%= longTermSectorInformation.getEventType() %> </li>
				<li>updated profit: <%= WebPageUtil.formatCurrency(longTermSectorInformation.getExpectedProfit())%></li>
				<li>Long term company outlook available after: <%= longTermCompanyInformation.getDateInformationAvailable().toString() %></li>
				<li>event type: <%= longTermCompanyInformation.getEventType()%></li>
				<li>updated profit: <%= WebPageUtil.formatCurrency(longTermCompanyInformation.getExpectedProfit())%></li>
				<li>Short term sector outlook available: <%= shortTermSectorInformation.getDateInformationAvailable().toString() %></li>
				<li>event type: <%= shortTermSectorInformation.getEventType()%> </li>
				<li>updated profit: <%= WebPageUtil.formatCurrency(shortTermSectorInformation.getExpectedProfit())%></li>
				<li>Short term company outlook available: <%=  shortTermCompanyInformation.getDateInformationAvailable().toString() %></li>
				<li>event type: <%= shortTermCompanyInformation.getEventType() %></li>
				<li>updated profit: <%= WebPageUtil.formatCurrency(shortTermCompanyInformation.getExpectedProfit()) %></li>
				
				
					</ul>
				</div>
			</div>	
<%
			}
%>				

<%			
		} else {
			// List all the companies
			Collection<Company> companyList = CompanyDAO.getCompanyList();
			if (companyList != null && companyList.size() != 0) {
%>
			<div class="post">
				<h2 class="title">Companies (Showing <%= companyList.size() %> entries)</h2>
				<div class="entry">
					<ul>
<%	
				for (Company currentCompany : companyList) {
					CompanyPeriodReport currentPeriodReport = currentCompany.getCurrentPeriod();				
%>
					<li>
	Company Name: <a href="Companies.jsp?<%= Constants.COMPANY_CODE_PARM %>=<%=currentCompany.getCode()%>"><%= currentCompany.getName() %></a><br/>
	Company Code: <%= currentCompany.getCode() %><br/>
	Company Exchange: <%= currentCompany.getStockExchange().getName() %><br/>
	Company Outstanding shares: <%= currentCompany.getOutstandingShares() %><br/>
	Company last share trade price: <%= WebPageUtil.formatCurrency(currentCompany.getLastTradePrice()) %><br/>
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