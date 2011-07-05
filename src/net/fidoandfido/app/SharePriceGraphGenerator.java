package net.fidoandfido.app;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.TradeRecordDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.TradeRecord;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class SharePriceGraphGenerator {

	public static void main(String[] args) {

		SharePriceGraphGenerator graphGenerator = new SharePriceGraphGenerator();
		graphGenerator.initDB();

		// create and display a frame...
		ChartFrame frame = new ChartFrame("First", graphGenerator.makeChart("CWMF"));
		frame.pack();
		frame.setVisible(true);

	}

	public SharePriceGraphGenerator() {
		// stuff
	}

	public void initDB() {
		// init DB stuff
		HibernateUtil.connectToDB();
	}

	public JFreeChart makeChart(String companyCode) {
		HibernateUtil.beginTransaction();

		CompanyDAO companyDAO = new CompanyDAO();

		Company company = companyDAO.getCompanyByCode(companyCode);
		if (company == null) {
			throw new IllegalArgumentException("Unknown company for code: " + companyCode);
		}
		if (!company.isTrading()) {
			throw new IllegalArgumentException("Company no longer trading: " + company.getName());
		}
		Date currentDate = new Date();

		Logger logger = Logger.getLogger("net.fidoandfido");
		logger.error("CompanyGraph.jsp -- generating graph ");
		logger.error("CompanyGraph.jsp -- " + companyCode);

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

		boolean stockJustSplit = false;
		boolean stockSplit = false;
		for (CompanyPeriodReport report : reportList) {
			if (stockJustSplit) {
				stockSplit = true;
				Date reportDate = report.getStartDate();
				Date nullValueDate = new Date(reportDate.getTime() - 1000);
				Date preSplitDate = new Date(reportDate.getTime() - 2000);
				// Add the pre-split values, as well as the post split ones.
				earningPerShare.add(new Second(preSplitDate), (report.getFinalProfit() / report.getOutstandingShareCount()) * 2);
				bookValue.add(new Second(preSplitDate), ((report.getStartingAssets() - report.getStartingDebt()) / report.getOutstandingShareCount()) * 2);

				// Add null values to *all* our lines
				earningPerShare.add(new Second(nullValueDate), null);
				bookValue.add(new Second(nullValueDate), null);
				sharePrice.addOrUpdate(new Second(nullValueDate), null);

				stockJustSplit = false;

			}
			earningPerShare.addOrUpdate(new Second(report.getStartDate()), report.getFinalProfit() / report.getOutstandingShareCount());
			bookValue.addOrUpdate(new Second(report.getStartDate()),
					(report.getStartingAssets() - report.getStartingDebt()) / report.getOutstandingShareCount());

			if (report.isStockSplit()) {
				stockJustSplit = true;
			}
		}

		// Add data points to bring the lines to the edge of the graph.
		Date latestDatePointDate = new Date();
		earningPerShare.add(new Second(latestDatePointDate), currentReport.getStartingExpectedProfit() / currentReport.getOutstandingShareCount());
		bookValue.add(new Second(latestDatePointDate),
				((currentReport.getStartingAssets() - currentReport.getStartingDebt()) / currentReport.getOutstandingShareCount()));
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
		return chart;
	}
}
