package net.fidoandfido.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fidoandfido.dao.HibernateUtil;
import net.fidoandfido.dao.TraderMessageDAO;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderMessage;
import net.fidoandfido.util.WebUtil;

import org.apache.log4j.Logger;

public class MessageServlet extends HttpServlet {

	Logger logger = Logger.getLogger(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest ,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public static final String ID_PARM = "message_id";

	public static final String MESSAGE_SUBJECT_PARM = "subject";
	public static final String MESSAGE_BODY_PARM = "body";

	public static final String RESPONSE_FORMAT = "format";
	public static final String AJAX = "ajax";

	public static final String COMMAND_PARM = "command";

	public static final String DISMISS_MESSAGE = "dismiss";
	public static final String POST_MESSAGE = "post";

	TraderMessageDAO traderMessageDAO = new TraderMessageDAO();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("Message servlet starting.");
		boolean success = false;

		boolean ajax = false;
		String format = req.getParameter(RESPONSE_FORMAT);
		if (AJAX.equals(format)) {
			ajax = true;
		}

		HibernateUtil.beginTransaction();
		Trader trader = WebUtil.getCurrentTraderBySession(req.getSession().getId());
		String command = req.getParameter(COMMAND_PARM);
		logger.info("command: [" + command + "]");
		if (DISMISS_MESSAGE.equals(command)) {
			logger.info("Have a dismiss command");
			String id = req.getParameter(ID_PARM);
			success = dismissMessage(id, trader);
			if (success) {
				logger.info("Message dismissed");
			} else {
				logger.error("Could not dismiss message! ID:" + id + " Trader: " + trader.getName());
			}
			if (ajax) {
				if (success) {
					resp.getWriter().println("Okay");
				} else {
					resp.getWriter().println("Failed");
				}
			}
		} else if (POST_MESSAGE.equals(command)) {
			String body = req.getParameter(MESSAGE_BODY_PARM);
			String subject = req.getParameter(MESSAGE_SUBJECT_PARM);
			success = postMessage(subject, body, trader);
			if (success) {
				logger.info("Message saved");
			} else {
				logger.error("Message unable to be saved.");
			}
		} else {
			logger.warn("Unknown command.");
		}
		HibernateUtil.commitTransaction();
		if (!ajax) {
			if (success) {
				resp.sendRedirect("/myapp/Trader.jsp");
			} else {
				logger.info("Some kind of error occured in Message Servlet");
				resp.sendRedirect("/myapp/Welcome.jsp");
			}
		}

	}

	public boolean postMessage(String subject, String body, Trader trader) {
		if (subject == null || subject.isEmpty()) {
			return false;
		}
		if (body == null || body.isEmpty()) {
			return false;
		}
		TraderMessage message = new TraderMessage(new Date(), subject, body, trader, trader);
		traderMessageDAO.saveMessage(message);
		return true;
	}

	public boolean dismissMessage(String id, Trader trader) {
		try {
			if (trader == null) {
				logger.warn("Message failure - trader null");
				return false;
			}
			if (id == null || id.isEmpty()) {
				logger.warn("Message failure - id null or empty");
				return false;
			}
			TraderMessage traderMessage = traderMessageDAO.getMessageById(id);

			if (!traderMessage.getForTrader().equals(trader)) {
				logger.warn("Message failure - bad trader!");
				return false;
			}
			traderMessage.setCurrent(false);
			traderMessageDAO.saveMessage(traderMessage);
		} catch (Exception e) {
			logger.error("Exception thrown in Message servlet: " + e.getMessage());
			return false;
		}
		return true;
	}
}
