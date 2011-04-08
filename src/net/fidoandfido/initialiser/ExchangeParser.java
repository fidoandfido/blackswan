package net.fidoandfido.initialiser;

import java.util.ArrayList;
import java.util.List;

import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.Constants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ExchangeParser extends DefaultHandler {
	public List<StockExchange> exchangeList = new ArrayList<StockExchange>();
	// <exchange name="MX" company-count="10"
	// description="The MX is the mining exchange. A little volatility here - mainly from companies that strike it big in their mines, and then lose it all if their mines run out."/>

	private static final String EXCHANGE_TAG = "exchange";
	private static final String NAME_ATTRIB = "name";
	private static final String COMPANIES_ATTRIB = "company-count";
	private static final String DESCRIPTION_ATTRIB = "description";
	private static final String EVENT_GENERATOR_NAME = "event-generator";
	private static final String PERIOD_LENGTH_ATTRIB = "period-length-mins";

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(EXCHANGE_TAG)) {
			String name = attributes.getValue(NAME_ATTRIB);
			String description = attributes.getValue(DESCRIPTION_ATTRIB);
			int companyCount = Integer.parseInt(attributes.getValue(COMPANIES_ATTRIB));
			String eventGeneratorName = attributes.getValue(EVENT_GENERATOR_NAME);
			String periodLengthString = attributes.getValue(PERIOD_LENGTH_ATTRIB);
			long periodLength = Constants.DEFAULT_PERIOD_LENGTH_IN_MILLIS;
			try {
				periodLength = Long.parseLong(periodLengthString);
				periodLength = periodLength * 1000 * 60;
			} catch (NumberFormatException nfe) {
				// and ignore it.
			}
			// EventGenerator generator = EventGeneratorFactory.getGeneratorByName(eventGeneratorName);
			StockExchange stockExchange = new StockExchange(name, description, companyCount, eventGeneratorName, periodLength);
			exchangeList.add(stockExchange);
		}
	}
}