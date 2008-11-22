package org.xbrlapi.sax.identifiers.tests;

import java.net.URL;

import org.xbrlapi.Concept;
import org.xbrlapi.data.dom.tests.BaseTestCase;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the loader implementation.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class IdentifierTestCase extends BaseTestCase {
	
	private final String STARTING_POINT = "real.data.sec.usgaap.3";
	private URL url = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		url = new URL(getURL(this.STARTING_POINT));
	}
	
	public IdentifierTestCase(String arg0) {
		super(arg0);
	}
	
	/**
	 * Test fragment identification
	 */
	public void testFragmentIdentification() {
		try {
			loader.stashURL(url);
			loader.discoverNext();
			Concept concept = store.getConcept("http://www.microsoft.com/msft/xbrl/taxonomy/2005-02-28","CoverInformation");
			assertEquals("CoverInformation",concept.getName());
		} catch (XBRLException e) {
			fail("Unexpected " + e.getMessage());
		}
	}

}