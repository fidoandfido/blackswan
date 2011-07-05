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
		if (company != null) {
			if (company.isTrading()) {
				CompanyPeriodReport currentReport = company.getCurrentPeriod();
				// And this is where we will put the chart!!! 
				TimeSeries sharePrice = new TimeSeries("Traded Value");
				TimeSeries bookValue = new TimeSeries("Book Value");
				TimeSeries earningPerShare = new TimeSeries("Earning Per Share");
				TimeSeriesCollection dataset = new TimeSeriesCollection();
		
				Date startOfChartDate = new Date();
				CompanyPeriodReportDAO companyPeriodReportDAO = new CompanyPeriodReportDAO();
				List<CompanyPeriodReport> reportList = companyPeriodReportDAO.getRecentPeriodReportListByCompany(company, 10);
				
				// Sort the list based on generation.
				Collections.sort(reportList, new Comparator<CompanyPeriodReport>() {
					@Override
					// Simple implementation - can safely assume no null / identical reports
					public int compare(CompanyPeriodReport o1, CompanyPeriodReport o2) {
						if (o1.getGeneration() < o2.getGeneration()) {
							return -1;
						}
						return 1;

					}
				});

				// Now that the list is sorted, we can get the 'start' time of the chart.
				startOfChartDate = reportList.get(0).getStartDate();

				// Draw the stock price line
				// This done first to allow us to 'update' this TimeSeries with null values in the
				// event of a stock split.
				TradeRecordDAO tradeRecordDAO = new TradeRecordDAO();
				List<TradeRecord> recordList = tradeRecordDAO.getLastTradeRecords(company, startOfChartDate);
				// This list is sorted in reverse order; this is okay for our graph.
				Date previousDate = new Date();
				for (TradeRecord record : recordList) {
					if (record.getDate().getTime() < previousDate.getTime() - 10000) {
						sharePrice.addOrUpdate(new Second(record.getDate()), record.getSharePrice());
						previousDate = record.getDate();
					}
				}

				// Flag to indicate a stock split occurred. This will be used to update the title.
				boolean stockSplit = false;
				// Flag to indicate the stock split occured at the end of the previous period.
				boolean stockJustSplit = false;
				
				for (CompanyPeriodReport report : reportList) {
					if (stockJustSplit) {
						// If stock split, add a null value and also add the pre-split value
						// 1 and 2 seconds before the split.
						// Create times for our null value 
						Date reportDate = report.getStartDate();
						Date nullValueDate = new Date(reportDate.getTime() - 1000);
						Date preSplitDate = new Date(reportDate.getTime() - 2000);
						
						// Add the pre-split values earning and bookvalue series
						earningPerShare.add(new Second(preSplitDate), (report.getFinalProfit() / report.getOutstandingShareCount()) * 2);
						bookValue.add(new Second(preSplitDate), ((report.getStartingAssets() - report.getStartingDebt()) / report.getOutstandingShareCount()) * 2);

						// Add null values to *all* our lines
						earningPerShare.add(new Second(nullValueDate), null);
						bookValue.add(new Second(nullValueDate), null);
						sharePrice.addOrUpdate(new Second(nullValueDate), null);
						
						// Update our flags.
						stockSplit = true;
						stockJustSplit = false;
					}
					
					// Add the entries for the current report.
					earningPerShare.add(new Second(report.getStartDate()), report.getFinalProfit() / report.getOutstandingShareCount());
					bookValue.add(new Second(report.getStartDate()), (report.getStartingAssets() - report.getStartingDebt()) / report.getOutstandingShareCount());

					// Check if the stock split at the end of this period.
					if (report.isStockSplit()) {
						stockJustSplit = true;
					}
				}
							
				// Add data points to bring the lines to the edge of the graph.
				Date latestDatePointDate = new Date();
				earningPerShare.add(new Second(latestDatePointDate), currentReport.getStartingExpectedProfit() / currentReport.getOutstandingShareCount());
				bookValue.add(new Second(latestDatePointDate), ((currentReport.getStartingAssets() - currentReport.getStartingDebt()) / currentReport.getOutstandingShareCount()));
				sharePrice.add(new Second(latestDatePointDate), company.getLastTradePrice());
				
				
				dataset.addSeries(bookValue);
				dataset.addSeries(sharePrice);
				dataset.addSeries(earningPerShare);
				
				JFreeChart chart = ChartFactory.createTimeSeriesChart("Share Price: " + company.getName(), // title
						"Date", // x-axis label
						"Price (cents)", // y-axis label
						dataset, // data
						true, // create legend?
						true, // generate tooltips?
						false // generate URLs?
						);
		
				chart.setBackgroundPaint(Color.white);
				if (stockSplit) {
					chart.addSubtitle(new TextTitle("Break in graph denotes stock splitting event"));
				}
				
				XYPlot plot = (XYPlot) chart.getPlot();
				plot.setBackgroundPaint(Color.lightGray);
				plot.setDomainGridlinePaint(Color.white);
				plot.setRangeGridlinePaint(Color.white);
				plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
				plot.setDomainCrosshairVisible(true);
				plot.setRangeCrosshairVisible(true);
				plot.getRenderer().setSeriesPaint(0, Color.MAGENTA);
				plot.getRenderer().setSeriesPaint(1, Color.BLUE);
				plot.getRenderer().setSeriesPaint(2, Color.BLACK);
		
				XYItemRenderer r = plot.getRenderer();
				if (r instanceof XYLineAndShapeRenderer) {
					XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
					renderer.setBaseShapesVisible(false);
					renderer.setBaseShapesFilled(false);
					renderer.setDrawSeriesLineAsPath(false);
				}
				DateAxis axis = (DateAxis) plot.getDomainAxis();
				axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
				session.setAttribute(GraphServlet.CHART_ATTRIBUTE + company.getCode(), chart);
			}
		}
	}
%>
				
		<img src="/myapp/graph?<%=GraphServlet.COMPANY_CODE%>=<%=company.getCode()%>"/>
		</div>
</div>	

<%
HibernateUtil.commitTransaction();
%>