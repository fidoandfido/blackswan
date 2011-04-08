package net.fidoandfido.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TraderDataServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String USER_NAME = "USERNAME";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/html");
		// try {
		// String name = req.getParameter(USER_NAME);
		// Trader trader = TraderDAO.getTraderByTraderName(name);
		// if (trader == null) {
		// resp.getWriter().println("Unable to retrieve trader!<br> ");
		// } else {
		// resp.getWriter().println("Trader retrieved!<br> ");
		// resp.getWriter().println("Trader name: " + trader.getName() + "<br> ");
		// resp.getWriter().println("Trader cash: " + trader.getCash() + "<br> ");
		// resp.getWriter().println("Trader retrieved successfully!<br> ");
		// }
		// } catch (Exception e) {
		// // do nothing for the moment.
		// resp.getWriter().println("Exception - unable to retrieve trader?<br> ");
		// resp.getWriter().println(e.getMessage());
		// }
		resp.getWriter().println("<br>");
		resp.getWriter().println("<a href=\"/Welcome.jsp\">Home</a><br>");
		resp.getWriter().println("<a href=\"/ShowData.jsp\">Show Data</a>");

	}
}
