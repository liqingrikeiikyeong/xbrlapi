package org.xbrlapi.data.exist.tests;

import org.xbrlapi.data.Store;
import org.xbrlapi.data.exist.StoreImpl;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the eXist XBRLAPI Store implementation.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class StoreImplConstructorTestCase extends BaseTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public StoreImplConstructorTestCase(String arg0) {
		super(arg0);
	}

	/**
	 * Test the a new store that uses existing collections for 
	 * data and metadata instead of creating them.
	 */
	public void testStoreImplConnectsToAnExistingStore() {

		try {
			Store newStore = createStore();
			assertEquals(store,newStore);
			stores.add(newStore);
			assertEquals("Fragment counts do not match.",store.getSize(),newStore.getSize());
		} catch (XBRLException e) {
			e.printStackTrace();
			fail("The connection to an existing store failed to be created. " + e.getMessage());
		}
	
	}

	/**
	 * Test store creation using a nonexistent parent collection to hold the store collection.
	 */
	public void testStoreImplFailsOnNonexistentContainer() {
		try {
			new StoreImpl(host,port,database, username, password,"/nonexistentStoreParent/", dataCollectionName);
			fail("The store should have failed to be created because no parent exists for the store collection.");
		} catch (XBRLException expected) {
			;
		}
	}

	/**
	 * Test the a new store that uses existing collections for 
	 * data and metadata instead of creating them.
	 * TODO Figure out what to do about Exist handling bad names for adding and deleting.
	 */
	public void testStoreImplFailsWithBadCharacters() {
		String badName = "&&<>";
		try {
			store = new StoreImpl(host,port,database, username, password, storeParentPath, badName);
			fail("The store creation succeeded with store name " + badName);
		} catch (XBRLException e) {
		    ;
		}
	}
	
	
}
