package net.fidoandfido.initialiser;

import java.util.ArrayList;
import java.util.List;

import net.fidoandfido.model.ExchangeGroup;
import net.fidoandfido.model.StockExchange;
import net.fidoandfido.util.Constants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ExchangeParser extends DefaultHandler {

	public List<ExchangeGroup> exchangeGroupList = new ArrayList<ExchangeGroup>();
	// <exchange name="MX" company-count="10"
	// description="The MX is the mining exchange. A little volatility here - mainly from companies that strike it big in their mines, and then lose it all if their mines run out."/>

	private static final String EXCHANGE_TAG = "exchange";
	private static final String SECTOR_TAG = "sector";
	private static final String EXCHANGE_GROUP_TAG = "exchange-group";
	private static final String NAME_ATTRIB = "name";
	private static final String COMPANIES_ATTRIB = "company-count";
	private static final String DESCRIPTION_ATTRIB = "description";
	private static final String EVENT_GENERATOR_NAME = "event-generator";
	private static final String PERIOD_LENGTH_ATTRIB = "period-length-mins";
	private static final String STARTING_INTEREST = "interest-rate";
	private static final String ECONOMIC_MODIFIER_NAME = "economic-modifier";
	private static final String COMPANY_MODIFIER_NAME = "company-modifier";
	private static final String MAX_SHARE_PRICE_ATTRIB = "max-share-price";
	private static final String REQUIRED_LEVEL_TAG = "required-level";
	private static final String MAX_TRADING_COMPANY_COUNT = "max-trading-company-count";

	private ExchangeGroup currentExchangeGroup;
	private StockExchange stockExchange;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(EXCHANGE_GROUP_TAG)) {

			String name = attributes.getValue(NAME_ATTRIB);
			String description = attributes.getValue(DESCRIPTION_ATTRIB);
			String periodLengthString = attributes.getValue(PERIOD_LENGTH_ATTRIB);
			long periodLength = Constants.DEFAULT_PERIOD_LENGTH_IN_MILLIS;
			try {
				periodLength = Long.parseLong(periodLengthString);
				periodLength = periodLength * 1000 * 60;
			} catch (NumberFormatException nfe) {
				// and ignore it.
			}
			currentExchangeGroup = new ExchangeGroup(name, description, periodLength);
			exchangeGroupList.add(currentExchangeGroup);
		} else if (localName.equals(EXCHANGE_TAG)) {
			String name = attributes.getValue(NAME_ATTRIB);
			String description = attributes.getValue(DESCRIPTION_ATTRIB);
			int companyCount = Integer.parseInt(attributes.getValue(COMPANIES_ATTRIB));
			String eventGeneratorName = attributes.getValue(EVENT_GENERATOR_NAME);
			String economicModifierName = attributes.getValue(ECONOMIC_MODIFIER_NAME);
			String companyModifierName = attributes.getValue(COMPANY_MODIFIER_NAME);
			long maxSharePrice = Long.parseLong(attributes.getValue(MAX_SHARE_PRICE_ATTRIB));
			long interestRate = Long.parseLong(attributes.getValue(STARTING_INTEREST));
			long maxCompantTradingCount = Long.parseLong(attributes.getValue(MAX_TRADING_COMPANY_COUNT));
			String requiredLevelString = attributes.getValue(REQUIRED_LEVEL_TAG);
			long requiredLevel = 0;
			try {
				requiredLevel = Long.parseLong(requiredLevelString);
			} catch (NumberFormatException nfe) {
				// and ignore it.
			}

			// EventGenerator generator =
			// EventGeneratorFactory.getGeneratorByName(eventGeneratorName);
			stockExchange = new StockExchange(currentExchangeGroup, name, description, companyCount, eventGeneratorName,
					currentExchangeGroup.getPeriodLength(), interestRate, economicModifierName, companyModifierName, maxSharePrice, requiredLevel,
					maxCompantTradingCount);
			currentExchangeGroup.addExchange(stockExchange);

		} else if (localName.equals(SECTOR_TAG)) {
			String name = attributes.getValue(NAME_ATTRIB);
			stockExchange.addSector(name);
		}
	}
}