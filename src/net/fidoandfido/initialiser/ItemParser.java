package net.fidoandfido.initialiser;

import java.util.HashSet;
import java.util.Set;

import net.fidoandfido.model.ReputationEffect;
import net.fidoandfido.model.ReputationItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ItemParser extends DefaultHandler {

	private static final String ITEM_TAG = "item";
	private static final String NAME_ATTRIB = "name";
	private static final String COST_ATTRIB = "cost";
	private static final String IMAGE_ATTRIB = "image";

	private static final String REPUTATION_TAG = "reputation";
	private static final String SECTOR_ATTRIB = "sector";
	private static final String POINTS_ATTRIB = "points";

	public Set<ReputationItem> itemList = new HashSet<ReputationItem>();

	private ReputationItem currentItem;

	public ItemParser() {
		// nothing to do here!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(ITEM_TAG)) {
			String name = attributes.getValue(NAME_ATTRIB);
			long cost = Long.parseLong(attributes.getValue(COST_ATTRIB));
			String image = attributes.getValue(IMAGE_ATTRIB);
			currentItem = new ReputationItem(name, cost, image);
		} else if (localName.equals(REPUTATION_TAG)) {
			String sector = attributes.getValue(SECTOR_ATTRIB);
			String pointString = attributes.getValue(POINTS_ATTRIB);
			int points = 0;
			try {
				points = Integer.parseInt(pointString);
			} catch (Exception e) {
				System.err.println("exception!");
				System.err.println("sector: " + sector);
				System.err.println("current item:" + currentItem == null ? "null" : currentItem.getName());
			}
			ReputationEffect effect = new ReputationEffect(sector, points);
			if (currentItem != null) {
				currentItem.addEffect(effect);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals(ITEM_TAG)) {
			itemList.add(currentItem);
			currentItem = null;
		}
	}

}
