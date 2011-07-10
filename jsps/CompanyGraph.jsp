<%@page import="net.fidoandfido.util.ChartGenerator"%>
<%@page import="org.jfree.chart.title.TextTitle"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="org.apache.log4j.Logger"%>
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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
    
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

	Logger logger = Logger.getLogger("net.fidoandfido");
	logger.error("CompanyGraph.jsp -- generating graph ");
	logger.error("CompanyGraph.jsp -- " + companyCode);
%>


<%@page import="net.fidoandfido.model.CompanyPeriodReport"%>
<%@page import="net.fidoandfido.dao.CompanyPeriodReportDAO"%>
<%@page import="java.util.Date"%>
<%@page import="net.fidoandfido.dao.StockExchangeDAO"%>
<%@page import="net.fidoandfido.model.PeriodQuarter"%>
<%@page import="net.fidoandfido.dao.PeriodPartInformationDAO"%>

<div id="graph">
	<div id="content">
<%
	if (user != null) {
		ChartGenerator chartGenerator = new ChartGenerator();
		session.setAttribute(GraphServlet.CHART_ATTRIBUTE + company.getCode(), chartGenerator.generateChart(company));
	}
%>
				
		<img src="/myapp/graph?<%=GraphServlet.COMPANY_CODE%>=<%=company.getCode()%>"/>
		</div>
</div>	

<%
HibernateUtil.commitTransaction();
%>