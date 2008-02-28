package org.xbrlapi.data.dom.tests;

import org.xbrlapi.Fragment;
import org.xbrlapi.FragmentList;
import org.xbrlapi.impl.MockFragmentImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the XML DOM XBRLAPI Store implementation.
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

	public void testAddFragment() {
		try {
			String index = loader.getNextFragmentId();
			MockFragmentImpl d = null;
			d = new MockFragmentImpl(index);
			store.storeFragment(d);
			Fragment f = null;
			f = store.getFragment(index);
			assertNotNull(f);
			assertEquals(index,f.getFragmentIndex());
		} catch (XBRLException e) {
			fail(e.getMessage());
		}
	}

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

	public void testRemoveFragmentUsingFragment() {
		try {
			String index = store.getNextFragmentId();
			store.storeFragment(new MockFragmentImpl(index));
			assertTrue(store.hasFragment(index));
			MockFragmentImpl document = (MockFragmentImpl) store.getFragment(index);
			store.removeFragment(index);
			assertFalse(store.hasFragment(index));
		} catch (XBRLException e) {
			fail("Unexpected exception. " + e.getMessage());
		}
	}
	
	public void testQueryData() throws Exception {
		
		String index = store.getNextFragmentId();
		FragmentList<Fragment> fragments = null;
		try {			
		    Fragment mock = new MockFragmentImpl(index);
		    store.serialize(mock.getMetadataRootElement());
			store.storeFragment(mock);
	        String xpathQuery = "/" + Constants.XBRLAPIPrefix + ":" + "fragment/" + Constants.XBRLAPIPrefix + ":" + "data/" + Constants.XBRLAPIPrefix + ":fragment";
	        fragments = store.<Fragment>query(xpathQuery);
		} catch (Exception e) {
		    e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			assertEquals("1",(new Long(fragments.getLength())).toString());
	        assertEquals("fragment",fragments.getFragment(0).getDataRootElement().getLocalName());
			store.removeFragment(index);
		} catch (Exception e) {
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
	        String query = "/" + Constants.XBRLAPIPrefix + ":" + "fragment/" + Constants.XBRLAPIPrefix + ":" + "data/" + Constants.XMLSchemaPrefix + ":element";
	        FragmentList<Fragment> fragments = store.<Fragment>query(query);
	        Fragment fragment = fragments.getFragment(0);
	        assertEquals("element",fragment.getDataRootElement().getLocalName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	public void testGetNextFragmentId() {
		try {
		    String id = store.getNextFragmentId();
		    store.storeFragment(new MockFragmentImpl(id));
			assertEquals((new Integer(store.getNextFragmentId())).intValue(),(new Integer(store.getNextFragmentId())).intValue());
		} catch (XBRLException e) {
			fail("Unexpected " + e.getMessage());
		}
	}	

	public void testHasDocument() {
		try {
			assertTrue(store.hasDocument("http://www.xbrlapi.org/xbrl/xbrl-2.1-roles.xsd"));
			assertFalse(store.hasDocument("http://www.rubbish.gcs/crazy.xyz"));
		} catch (XBRLException e) {
			fail("Unexpected " + e.getMessage());
		}
	}
}
