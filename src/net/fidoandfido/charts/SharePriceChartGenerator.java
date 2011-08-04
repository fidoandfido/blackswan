package net.fidoandfido.charts;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.TradeRecordDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.TradeRecord;

import org.jfree.chart.ChartFactory;
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

public class SharePriceChartGenerator {

	public JFreeChart generateChart(Company company) {
		if (company == null) {
			return null;
		}
		if (!company.isTrading()) {
			return null;
		}

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
