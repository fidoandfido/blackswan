package net.fidoandfido.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class GraphServlet extends HttpServlet {
	public static final String CHART_ATTRIBUTE = "chart";
	public static final String COMPANY_CODE = "companyCode";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		// get the chart from storage
		String companyCode = request.getParameter(COMPANY_CODE);
		JFreeChart chart = (JFreeChart) session.getAttribute(CHART_ATTRIBUTE + companyCode);
		if (chart == null) {
			return;
		}

		int width = 800;
		int height = 400;

		OutputStream out = response.getOutputStream();
		// set the content type so the browser can see this as it is
		response.setContentType("image/png");
		ChartUtilities.writeChartAsPNG(out, chart, width, height);

		// JPEGImageEncoder encoder =
		// JPEGCodec.createJPEGEncoder(response.getOutputStream());
		// JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(buf);
		// param.setQuality(1f, true);
		// encoder.encode(buf, param);
	}
}
