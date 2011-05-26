package net.fidoandfido.initialiser;

import java.util.ArrayList;
import java.util.List;

import net.fidoandfido.model.Trader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TraderParser extends DefaultHandler {

	public List<Trader> traderList = new ArrayList<Trader>();
	private static final String TRADER_TAG = "trader";
	private static final String NAME_ATTRIB = "name";
	private static final String AI_STRATEGY = "strategy";
	private static final String TRADER_COUNT = "count";
	private static final int DEFAULT_TRADER_COUNT = 10;

	public TraderParser() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(TRADER_TAG)) {
			String name = attributes.getValue(NAME_ATTRIB);
			String strategy = attributes.getValue(AI_STRATEGY);
			int traderCount = DEFAULT_TRADER_COUNT;
			try {
				traderCount = Integer.parseInt(attributes.getValue(TRADER_COUNT));
			} catch (NumberFormatException nfe) {
				// Ignore it - we will se the default.
			}
			for (int i = 0; i < traderCount; i++) {
				Trader trader = new Trader(name + i, AppInitialiser.TRADER_START_CASH, false, strategy);
				traderList.add(trader);
			}
		}
	}
}