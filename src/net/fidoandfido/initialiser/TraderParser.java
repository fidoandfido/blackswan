package net.fidoandfido.initialiser;

import java.util.ArrayList;
import java.util.List;

import net.fidoandfido.app.AppInitialiser;
import net.fidoandfido.model.Trader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TraderParser extends DefaultHandler {

	public List<Trader> traderList = new ArrayList<Trader>();
	private static final String TRADER_TAG = "trader";
	private static final String NAME_ATTRIB = "name";
	private static final String MARKET_MAKER = "isMarketMaker";
	private static final String AI_STRATEGY = "strategy";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 * org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(TRADER_TAG)) {
			String name = attributes.getValue(NAME_ATTRIB);
			String marketMakerAttribute = attributes.getValue(MARKET_MAKER);
			String strategy = attributes.getValue(AI_STRATEGY);
			boolean isMarketMaker = Boolean.parseBoolean(marketMakerAttribute);
			Trader trader = new Trader(name, isMarketMaker ? AppInitialiser.MARKET_MAKER_START_CASH : AppInitialiser.TRADER_START_CASH, true, isMarketMaker);
			trader.setAiStrategyName(strategy);
			traderList.add(trader);
		}
	}
}