package net.fidoandfido.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.JFreeChart;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class GraphServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		// get the chart from storage
		JFreeChart chart = (JFreeChart) session.getAttribute("chart");
		// set the content type so the browser can see this as it is
		response.setContentType("image/jpeg");

		// send the picture
		BufferedImage buf = chart.createBufferedImage(400, 400, null);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(response.getOutputStream());
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(buf);
		param.setQuality(0.75f, true);
		encoder.encode(buf, param);
	}
}
