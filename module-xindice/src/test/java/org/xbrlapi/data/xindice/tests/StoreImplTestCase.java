package org.xbrlapi.data.xindice.tests;

import org.xbrlapi.Fragment;
import org.xbrlapi.FragmentList;
import org.xbrlapi.data.xindice.StoreImpl;
import org.xbrlapi.impl.MockFragmentImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the Xindice store implementation.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class StoreImplTestCase extends BaseTestCase {
	private final String STARTING_POINT = "test.data.small.schema";
	
	protected void setUp() throws Exception {
		super.setUp();
		loader.discover(this.getURL(STARTING_POINT));		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public StoreImplTestCase(String arg0) {
		super(arg0);
	}

	/**
	 * Test the addition of a mock fragment to the store.
	 */
	public void testAddFragment() {
		try {
			String index = store.getNextFragmentId();
			MockFragmentImpl d = new MockFragmentImpl(index);
			store.storeFragment(d);
			assertEquals(index,store.getFragment(index).getFragmentIndex());
		} catch (XBRLException e) {
			fail("The addition of a document fragment to the Xindice data store failed." + e.getMessage());
		}
	}

	/**
	 * Test the removal of a fragment from the store.
	 */
	public void testRemoveFragmentUsingIndex() {
		try {
			String index = store.getNextFragmentId();
			store.storeFragment(new MockFragmentImpl(index));
			assertTrue(store.hasFragment(index));
			store.removeFragment(index);
			assertFalse(store.hasFragment(index));
		} catch (XBRLException e) {
			fail("Unexpected exception. " + e.getMessage());
		}
	}

	/**
	 * Test the updating of a fragment in the store
	 * using XUpdate features of the Xindice store.
	 */
	public void testUpdateFragments() {
		try {
			String index = store.getNextFragmentId();
			store.storeFragment(new MockFragmentImpl(index));
	        String xupdate =
	            "<xu:modifications version=\"1.0\""
                    + "      xmlns:xbrlapi=\"http://xbrlapi.org/\" "
	                + "      xmlns:xu=\"http://www.xmldb.org/xupdate\">"
	                + "   <xu:rename select=\"/" + Constants.XBRLAPIPrefix + ":" + "fragment\">quirky</xu:rename>"
	                + "</xu:modifications>";
	        store.updateFragments(xupdate);
	        Fragment f = store.getFragment(index);
			assertEquals("quirky",f.getMetadataRootElement().getLocalName());
			store.removeFragment(index);
		} catch (XBRLException e) {
			fail("Unexpected exception. " + e.getMessage());
		}
	}

	public void testUpdateFragment() {
		try {
			String index = store.getNextFragmentId();
			store.storeFragment(new MockFragmentImpl(index));
	        String xupdate =
	            "<xu:modifications version=\"1.0\""
                    + "      xmlns:xbrlapi=\"http://xbrlapi.org/\" "
	                + "      xmlns:xu=\"http://www.xmldb.org/xupdate\">"
	                + "   <xu:rename select=\"/" + Constants.XBRLAPIPrefix + ":" + "fragment\">quirky</xu:rename>"
	                + "</xu:modifications>";
	        store.updateFragment(index,xupdate);
	        Fragment f = store.getFragment(index);
			assertEquals("quirky",f.getDataRootElement().getOwnerDocument().getDocumentElement().getLocalName());
			store.removeFragment(index);
		} catch (XBRLException e) {
		    e.printStackTrace();
			fail("Unexpected exception. " + e.getMessage());
		}
	}
	
	public void testQueryData() {
		try {			
			String index = store.getNextFragmentId();
	        String xpathQuery = "/" + Constants.XBRLAPIPrefix + ":" + "fragment";
	        FragmentList<Fragment> fragments = store.<Fragment>query(xpathQuery);
			assertEquals("608",(new Long(fragments.getLength())).toString());
	        Fragment fragment = fragments.getFragment(0);
	        assertEquals("fragment",fragment.getMetadataRootElement().getLocalName());
		} catch (XBRLException e) {
		    e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testQueryLoadedFragments() {
		try {
			loader.discover();			
		} catch (XBRLException e) {
			fail(e.getMessage());
		} 
		
		try {
	        String query = "/" + Constants.XBRLAPIPrefix + ":" + "fragment[" + Constants.XBRLAPIPrefix + ":" + "data/" + Constants.XMLSchemaPrefix + ":element]";
	        FragmentList<Fragment> fragments = store.<Fragment>query(query);
	        Fragment fragment = fragments.getFragment(0);
	        assertEquals("element",fragment.getDataRootElement().getLocalName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	public void testAddIndex() {
		try {
			((StoreImpl) store).addIndex("test","name","document");
		} catch (XBRLException e) {
			fail("Unexpected XBRL API exception. " + e.getMessage());
		}
	}
	
	public void testDeleteIndex() {
		try {
			((StoreImpl) store).addIndex("test","name","document");
			((StoreImpl) store).deleteIndex("test");
		} catch (XBRLException e) {
			fail("Unexpected XBRL API exception. " + e.getMessage());
		}
	}

	public void testGetNextFragmentId() {
		try {
			assertEquals("609",store.getNextFragmentId());
		} catch (XBRLException e) {
			e.printStackTrace();
			fail("Unexpected " + e.getMessage());
		}
	}	
	




}
