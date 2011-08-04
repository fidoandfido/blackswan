package net.fidoandfido.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;

import net.fidoandfido.dao.CompanyDAO;
import net.fidoandfido.dao.CompanyPeriodReportDAO;
import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.model.Company;
import net.fidoandfido.model.CompanyPeriodReport;
import net.fidoandfido.model.PeriodQuarter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class CompanyProfitChartGenerator {

	public static void main(String argv[]) {

		HibernateUtil.connectToDB();
		HibernateUtil.beginTransaction();
		CompanyDAO companyDAO = new CompanyDAO();

		int MAX_GRAPHS = 200;
		int graphCounter = 0;

		List<Company> companyList = companyDAO.getCompanyList();
		for (Company company : companyList) {
			CompanyProfitChartGenerator companyProfitChartGenerator = new CompanyProfitChartGenerator();
			JFreeChart chart = companyProfitChartGenerator.generateChart(company);

			JFrame frame = new JFrame("XY Plot Demo " + company.getName() + " --- PROFILE: " + company.getCompanyProfile());
			frame.setContentPane(new ChartPanel(chart));
			frame.pack();
			frame.setVisible(true);
			if (++graphCounter > MAX_GRAPHS) {
				break;
			}
		}
		HibernateUtil.commitTransaction();
	}

	public JFreeChart generateChart(Company company) {

		if (company == null) {
			return null;
		}
		if (!company.isTrading()) {
			return null;
		}

		CompanyPeriodReport currentReport = company.getCurrentPeriod();

		XYSeries expectedProfitSeries = new XYSeries("Expected Operating Profit");
		XYSeries actualProfitSeries = new XYSeries("Actual Operating Profit");

		CompanyPeriodReportDAO companyPeriodReportDAO = new CompanyPeriodReportDAO();
		List<CompanyPeriodReport> reportList = companyPeriodReportDAO.getRecentPeriodReportListByCompany(company, 5);

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

		List<String> xLabels = new ArrayList<String>();

		// Possible overflow?
		long averageExpectedProfit = 0;
		long maxDelta = 0;

		int i = 0;
		for (CompanyPeriodReport report : reportList) {
			long expectedOperatingProfit = report.getStartingExpectedRevenue() - report.getStartingExpectedExpenses();
			long quarterExpectedOperatingProfit = expectedOperatingProfit / 4;
			averageExpectedProfit += quarterExpectedOperatingProfit / reportList.size();
			for (PeriodQuarter periodQuarter : report.getPeriodQuarterList()) {
				String prefix = report.getGeneration() + " - Q" + (periodQuarter.getQuarterIndex() + 1);
				xLabels.add(prefix);
				expectedProfitSeries.add(i, quarterExpectedOperatingProfit);
				long quaterOperatingProfit = periodQuarter.getRevenue() - periodQuarter.getExpenses();
				actualProfitSeries.add(i, quaterOperatingProfit);

				long delta = Math.abs(quarterExpectedOperatingProfit - quaterOperatingProfit);
				if (maxDelta < delta) {
					maxDelta = delta;
				}
				i++;
			}
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(expectedProfitSeries);
		dataset.addSeries(actualProfitSeries);

		JFreeChart chart = ChartFactory.createXYLineChart("Quarterly Profit : " + company.getName(), // title
				"Quarter", // x-axis label
				"Profit", // y-axis label
				dataset, // data
				PlotOrientation.VERTICAL, // Plot orientation
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

		ValueAxis yAxis = plot.getRangeAxis();
		// Get the y 'range'.
		// Delta will be 10x the average expected operating profit
		// Offset will be the expected operating profit
		// Oops. Didn't take into account possibility of negative proffits, or really small profits.

		double minRange = averageExpectedProfit - (10 * averageExpectedProfit);
		double maxRange = averageExpectedProfit + (10 * averageExpectedProfit);
		if (minRange > maxRange) {
			double tmp = minRange;
			minRange = maxRange;
			maxRange = tmp;
		}

		yAxis.setRange(minRange, maxRange);

		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.setRenderer(new XYSplineRenderer(5));
		plot.getRenderer().setSeriesPaint(0, Color.MAGENTA);
		plot.getRenderer().setSeriesPaint(1, Color.BLUE);

		String[] a = {};
		String[] quarterLabels = xLabels.toArray(a);

		ValueAxis xAxis = new SymbolAxis("Quarter", quarterLabels);
		plot.setDomainAxis(xAxis);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false);
			renderer.setBaseShapesFilled(false);
			renderer.setDrawSeriesLineAsPath(false);
		}
		return chart;
	}
}
