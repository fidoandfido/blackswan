package net.fidoandfido.initialiser;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CompanyNameParser extends DefaultHandler {

	// Snippet of XML that we will be parsing:
	// <prefix value="Smithson" space="yes" />
	// <prefix value="First" space="optional" />
	// <body value="Printing" prefixable="yes" suffixable="yes" sector="logistics"/>
	// <suffix value="Enterprises" space="yes"/>
	// <suffix value="Tel" space="optional"/>

	/**
	 * 
	 */
	public final AppInitialiser appInitialiser;

	/**
	 * @param appInitialiser
	 */
	public CompanyNameParser(AppInitialiser appInitialiser) {
		this.appInitialiser = appInitialiser;
	}

	private static final String PREFIX_TAG = "prefix";
	private static final String BODY_TAG = "body";
	private static final String SUFFIX_TAG = "suffix";

	private static final String VALUE_ATTRIB = "value";
	private static final String SPACE_ATTRIB = "space";
	private static final String PREFIXABLE_ATTRIB = "prefixable";
	private static final String SUFFIXABLE_ATTRIB = "suffixable";
	private static final String SECTOR_ATTRIB = "sector";
	private static final String CODE_ATTRIB = "code";
	private static final String PROFIT_STRATEGY_ATTRIB = "strategy";

	private static final String YES_VALUE = "yes";
	// private static final String NO_VALUE = "no";
	private static final String OPTIONAL_VALUE = "optional";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 * org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(PREFIX_TAG)) {
			String value = attributes.getValue(VALUE_ATTRIB);
			String spaceSetting = attributes.getValue(SPACE_ATTRIB);
			String code = attributes.getValue(CODE_ATTRIB);
			boolean spaceAllowed = false;
			boolean spaceOptional = false;
			if (YES_VALUE.equals(spaceSetting)) {
				spaceAllowed = true;
			} else if (OPTIONAL_VALUE.equals(spaceSetting)) {
				spaceAllowed = true;
				spaceOptional = true;
			}
			this.appInitialiser.prefixes.add(new CompanyNamePrefix(value, spaceAllowed, spaceOptional, code));
		} else if (localName.equals(BODY_TAG)) {
			String value = attributes.getValue(VALUE_ATTRIB);
			boolean prefixable = YES_VALUE.equals(attributes.getValue(PREFIXABLE_ATTRIB));
			boolean suffixable = YES_VALUE.equals(attributes.getValue(SUFFIXABLE_ATTRIB));
			String code = attributes.getValue(CODE_ATTRIB);
			String sector = attributes.getValue(SECTOR_ATTRIB);
			String strategy = attributes.getValue(PROFIT_STRATEGY_ATTRIB);
			this.appInitialiser.bodies.add(new CompanyNameBody(value, prefixable, suffixable, sector, code, strategy));
		} else if (localName.equals(SUFFIX_TAG)) {
			String value = attributes.getValue(VALUE_ATTRIB);
			String spaceSetting = attributes.getValue(SPACE_ATTRIB);
			String code = attributes.getValue(CODE_ATTRIB);
			boolean spaceAllowed = false;
			boolean spaceOptional = false;
			if (YES_VALUE.equals(spaceSetting)) {
				spaceAllowed = true;
			} else if (OPTIONAL_VALUE.equals(spaceSetting)) {
				spaceAllowed = true;
				spaceOptional = true;
			}
			this.appInitialiser.suffixes.add(new CompanyNameSuffix(value, spaceAllowed, spaceOptional, code));
		}
	}
}