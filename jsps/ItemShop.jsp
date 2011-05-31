<%@page import="net.fidoandfido.servlets.ItemStoreServlet"%>
<%@page import="net.fidoandfido.dao.ReputationItemDAO"%>
<%@page import="net.fidoandfido.model.ReputationItem"%>
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
	User user = WebUtil.getCurrentUserBySession(request.getSession().getId());
	boolean isAdmin = user == null ? false : user.isUserAdmin();

	Trader trader = null;
	TraderDAO traderDAO = new TraderDAO();
	if (user != null) {
		trader = traderDAO.getTraderByUser(user);	
	}
	Date currentDate = new Date();

	// initialise relevant data here.
	List<ReputationItem> itemList = ReputationItemDAO.getItems();
	
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
				<h2 class="title">Spend your hard earned money!</h2>
				<div class="entry">
				By buying items, you will impress your broker and industry insiders. This will give you access
				to rumours earlier. Beware! Not all items impress equally; and some industries may be more
				impressed than others by specific items.<br/>
				
				Your current balance is: <%=WebPageUtil.formatCurrency(trader.getCash())%><br/>
									
				</div>
			</div>	
<%	
			if (itemList.size() > 0) {
%>
			<div class="post">
				<h2 class="title">Items for sale</h2>
				<div class="entry">
					<table>
					<tr>
						<td>Name</td>
						<td>Cost</td>
						<td></td>
						<td>Currently owned</td>
						<td></td>
					</tr>
<%
						for (ReputationItem item : itemList) {
%>
						<tr>
							<td><%= item.getName() %></td>
							<td><%= WebPageUtil.formatCurrency(item.getCost()) %></td>
							<td><img src="<%= WebPageUtil.getImageLocation(item.getImage()) %>" width="60" height="60"/></td>
							<td>0</td>
							<td>
							<form action="/myapp/itemstore" method="post">
							<input type="hidden" name="<%=ItemStoreServlet.BUY_OR_SELL%>" value="<%=ItemStoreServlet.BUY%>"></input></li>
							<input type="hidden" name="<%=ItemStoreServlet.COST%>" value="<%=item.getCost()%>"></input></li>
							<input type="hidden" name="<%=ItemStoreServlet.ITEM_NAME%>" value="<%=item.getName()%>"></input></li>
							<input type="submit" value="Buy!" />
							</form>
							</td>
						</tr>
<%
						}
%>		
					
					</table>
				</div>
			</div>
<%
			} else {
%>			
			<div class="post">
				<h2 class="title">No items in stock!</h2>
				<div class="entry">
					There is currently nothing for sale.
				</div>
			</div>
<%
			} 
%>			
			
			
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