package org.xbrlapi.data.exist.tests;

import java.net.URI;
import java.util.Set;
/**
 * Tests performance with larger data sets.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */
public abstract class LoadPerformanceTestCase extends BaseTestCase {
	private final String STARTING_POINT = "real.data.large.schema";
	
	protected void setUp() throws Exception {
		super.setUp();
		loader.discover(getURI(STARTING_POINT));		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public LoadPerformanceTestCase(String arg0) {
		super(arg0);
	}

	/**
	 * Test creation of an reconnection to a large data store.
	 */
	public void testLargerStore() {
		try {

			Set<URI> uris = store.getDocumentURIs();
			assertTrue(uris.size() > 22);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
