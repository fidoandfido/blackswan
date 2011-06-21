package net.fidoandfido.engine.experience;

import java.util.Date;

import net.fidoandfido.dao.TraderMessageDAO;
import net.fidoandfido.model.Trader;
import net.fidoandfido.model.TraderMessage;

public class LevelManager {

	public LevelManager() {
		// Default constructor
	}

	public int getLevel(Trader trader) {
		// Figure out the current level of a trader.
		long xp = trader.getExperiencePoints();
		if (xp < 100) {
			return 1;
		}
		if (xp < 200) {
			return 2;
		}
		if (xp < 500) {
			return 3;
		}
		if (xp < 1000) {
			return 4;
		}
		return 5;
	}

	public void levelUp(Trader trader) {
		// We have just levelled up...
		if (trader.getLevel() == 2) {
			// Send a message to the trader.
			TraderMessageDAO messageDAO = new TraderMessageDAO();
			TraderMessage level2Message = new TraderMessage(trader, new Date(), "Welcome to the real trading floor rookie!",
					"Congratulations, you have now been promoted to rookie trader. You will now have access to additional exchanges. Keep trading and good luck!");
			messageDAO.saveMessage(level2Message);
		}

	}
}
