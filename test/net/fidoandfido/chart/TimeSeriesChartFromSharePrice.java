/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2009, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -------------------------
 * TimeSeriesChartDemo1.java
 * -------------------------
 * (C) Copyright 2003-2009, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   ;
 *
 * Changes
 * -------
 * 09-Mar-2005 : Version 1, copied from the demo collection that ships with
 *               the JFreeChart Developer Guide (DG);
 *
 */

package net.fidoandfido.chart;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JPanel;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.TradeRecordDAO;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.TradeRecord;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class TimeSeriesChartFromSharePrice extends ApplicationFrame {

	/**
	 * A demonstration application showing how to create a simple time series
	 * chart. This example uses monthly data.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public TimeSeriesChartFromSharePrice(String title) {
		super(title);
		ChartPanel chartPanel = (ChartPanel) createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return A chart.
	 */
	private static JFreeChart createChart(XYDataset dataset) {

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

		return chart;

	}

	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 * 
	 * @return The dataset.
	 */
	private static XYDataset createDataset() {

		TimeSeries sharePrice = new TimeSeries("Traded Value");
		TimeSeries bookValue = new TimeSeries("Book Value");
		TimeSeries earningPerShare = new TimeSeries("Earning Per Share");

		HibernateUtil.beginTransaction();
		CompanyDAO companyDAO = new CompanyDAO();
		Company company = companyDAO.getCompanyList().get(0);
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

		HibernateUtil.commitTransaction();
		// ******************************************************************
		// More than 150 demo applications are included with the JFreeChart
		// Developer Guide...for more information, see:
		//
		// > http://www.object-refinery.com/jfreechart/guide.html
		//
		// ******************************************************************
		return dataset;
	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 * 
	 * @return A panel.
	 */
	public static JPanel createDemoPanel() {
		JFreeChart chart = createChart(createDataset());
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);
		return panel;
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		HibernateUtil.connectToDB();
		TimeSeriesChartFromSharePrice demo = new TimeSeriesChartFromSharePrice("Time Series Chart Demo 1");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}
