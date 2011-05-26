package net.fidoandfido.servlets;

import java.io.IOException;

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
	public static final String COMMAND_PARM = "command";

	public static final String DISMISS_MESSAGE = "dismiss";

	TraderMessageDAO traderMessageDAO = new TraderMessageDAO();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("Message servlet starting.");
		HibernateUtil.beginTransaction();
		Trader trader = WebUtil.getCurrentTraderBySession(req.getSession().getId());
		String id = req.getParameter(ID_PARM);
		boolean success = dismissMessage(id, trader);
		HibernateUtil.commitTransaction();
		if (success) {
			logger.info("Message dismissed");
			resp.sendRedirect("/myapp/Trader.jsp");
		} else {
			logger.info("Message dismissed");
			resp.sendRedirect("/myapp/Welcome.jsp");
		}

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
