package org.xbrlapi.data.exist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xbrlapi.Fragment;
import org.xbrlapi.FragmentList;
import org.xbrlapi.data.XBRLStore;
import org.xbrlapi.data.XBRLStoreImpl;
import org.xbrlapi.impl.FragmentFactory;
import org.xbrlapi.impl.FragmentListImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XUpdateQueryService;

/**
 * Implementation of the XBRL data store using eXist
 * as the underlying database.
 * 
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class StoreImpl extends XBRLStoreImpl implements XBRLStore {

	/**
	 * The database connection used by the data store.
	 */
	private DBConnectionImpl connection = null;
	
	/**
	 * The collection that holds the data in the data store.
	 */
	private Collection collection = null;
	
	/**
	 * The collection manager that operates on the data store collection (for indexing etc.).
	 */
	private CollectionManagementService manager = null;

	/**
	 * The XPath query service for the data collection.
	 */
    private XPathQueryService xpathService = null;
    	
	/**
	 * Initialise the database connection.
	 * 
	 * @param connection The live connection to the Xindice database.
	 * @param storeParentPath The full path to the container collection, which is the
	 * collection that will hold the data collection when it are created.
	 * @param dataCollectionName The name of the data collection.
	 * @throws XBRLException
	 */
	public StoreImpl(
			DBConnectionImpl connection,
			String storeParentPath,
			String dataCollectionName
			) throws XBRLException {
		
		storeParentPath = (storeParentPath.charAt(storeParentPath.length()-1) != '/') ? storeParentPath + "/" : storeParentPath;

		if (connection == null) throw new XBRLException("The Exist database connection is null so no data store can be established.");
		this.connection = connection;
		
		Collection parentCollection = connection.getCollection(storeParentPath);
		if (parentCollection == null) throw new XBRLException("The collection to contain the data store does not exist.");
				
		boolean storeAlreadyExisted = false;
		if (connection.hasCollection(dataCollectionName,parentCollection)) {
			storeAlreadyExisted = true;
			collection = connection.getCollection(dataCollectionName, parentCollection);
		} else {
			collection = connection.createCollection(dataCollectionName,parentCollection);
		}
		
		if (collection == null) throw new XBRLException("Collection " + dataCollectionName + " could not be created in collection " + storeParentPath + ".");

		try {
			manager = (CollectionManagementService) collection.getService("CollectionManagementService", "1.0");
		} catch (XMLDBException e) {
			throw new XBRLException("The collection manager could not be instantiated.",e);
		}
				
		if (! storeAlreadyExisted) {

			// Initialise the store fragment counter.
			this.storeNextFragmentId("1");
			
			// Add the collection configuration (indexing information) if the store uses a new collection.
			Collection configParentCollection = connection.getCollection("system/config");
			Collection dataConfigCollection = null;
			try {
				dataConfigCollection = configParentCollection.getChildCollection(dataCollectionName); 
			if (dataConfigCollection == null)
				dataConfigCollection = connection.createCollection(dataCollectionName, configParentCollection);
			} catch (XMLDBException e) {
				throw new XBRLException("The data configuration collection could not be instantiated.",e);
			}
			
			String dataXconf = 
				"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">\n"
				+ "<index xmlns:xlink=\"" + Constants.XLinkNamespace + "\" >\n"
				+ "    <fulltext default=\"none\" attributes=\"false\" alphanum=\"false\" />\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/"+ Constants.XBRLAPIPrefix + ":" + "data/*/@xlink:label\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/"+ Constants.XBRLAPIPrefix + ":" + "data/*/@xlink:type\" type=\"xs:string\"/>\n"				
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/@type\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/@id\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/@targetDocumentURL\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/@targetPointerValue\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/@parentIndex\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/@url\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/@absoluteHref\" type=\"xs:string\"/>\n"
		        + "    <create path=\"/"+ Constants.XBRLAPIPrefix + ":" + "fragment/"+ Constants.XBRLAPIPrefix + ":" + "xptr/@value\" type=\"xs:string\"/>\n"
		        + "</index>\n"
		        + "</collection>";
			
			try {
				
				XMLResource dataXconfResource = (XMLResource) dataConfigCollection.createResource("indexes.xconf", XMLResource.RESOURCE_TYPE);
				dataXconfResource.setContent(dataXconf);
				dataConfigCollection.storeResource(dataXconfResource);
				
			} catch (XMLDBException e) {
				throw new XBRLException("The system index configuration resource could not be added.",e);
			}

		}

		try {
			xpathService = (XPathQueryService) collection.getService("XPathQueryService","1.0");
			// Not allowed to bind the xml prefix: xpathService.setNamespace(Constants.XMLPrefix, Constants.XMLNamespace);
			xpathService.setNamespace(Constants.XLinkPrefix, Constants.XLinkNamespace);
			xpathService.setNamespace(Constants.XMLSchemaPrefix, Constants.XMLSchemaNamespace);
			xpathService.setNamespace(Constants.XBRL21Prefix, Constants.XBRL21Namespace);
			xpathService.setNamespace(Constants.XBRL21LinkPrefix, Constants.XBRL21LinkNamespace);
			// TODO delete the composite prefix namespace declaration from the store and remove document fragment from tests.
			xpathService.setNamespace(Constants.XBRLAPIPrefix, Constants.XBRLAPINamespace);
			xpathService.setNamespace(Constants.XBRLAPILanguagesPrefix, Constants.XBRLAPILanguagesNamespace);
			// TODO add means for users to add their own namespace declarations to the data store xpath services
			
		} catch (XMLDBException e) {
			throw new XBRLException("The XPath services could not be initialised.",e);
		}		
		
	}


    
  


    

    
	/**
	 * Close the data store.
	 * Throws XBRLException if the data store cannot be closed. 
	 */
	public void close() throws XBRLException {
		try {
			collection.close();
			connection = null;
		} catch (XMLDBException e) {
			throw new XBRLException("The Xindice DTS collection could not be closed.",e);
		}
	}
	
	/**
	 * @see org.xbrlapi.data.Store#delete()
	 */
	public void delete() throws XBRLException {
		try {
			String collectionName = collection.getName();
			collection.close();
			if (connection.hasCollection(collectionName)) {
				manager.removeCollection(collectionName);
			} else {
				logger.debug("The collection is not in the eXist database so it is not deleted.");
			}
			connection = null;
		} catch (XMLDBException e) {
			throw new XBRLException("The data collection could not be deleted.",e);
		}
	}	
	
	/**
	 * Add a fragment to the DTS.  Sets the fragment builder to null because it
	 * is no longer required, stores the fragment data and metadata in the 
	 * data store, sets the store property in the fragment.
	 * @param fragment The fragment to be added to the DTS store.
	 * @throws XBRLException if the fragment cannot be added to the store 
	 * (eg: because one with the same index is already in the store).
	 */
	public void storeFragment(Fragment fragment) throws XBRLException {

		if (fragment == null) throw new XBRLException("The fragment is null so it cannot be added.");
		String index = fragment.getFragmentIndex();
		if (hasFragment(index)) throw new XBRLException("A fragment with index " + index + " already exists.");

        if (fragment.getStore() != null) {
	    	try {
	    		XMLResource resource = (XMLResource) collection.createResource(index, XMLResource.RESOURCE_TYPE);
		        resource.setContent(DOM2String(fragment.getMetadataRootElement()));
		        collection.storeResource(resource);
	        } catch (XMLDBException e) {
	        	throw new XBRLException("The fragment data could not be added to the eXist data store.", e);
	        }			
			return;
		}
		
		try {
			XMLResource resource = (XMLResource) collection.createResource(index, XMLResource.RESOURCE_TYPE);
            resource.setContent(DOM2String(fragment.getBuilder().getMetadata()));
	        collection.storeResource(resource);
        } catch (XMLDBException e) {
        	throw new XBRLException("The fragment data could not be added to the eXist data store.", e);
        }

        // Finalise the fragment, ready for use
        fragment.setResource(fragment.getBuilder().getMetadata());
        fragment.setStore(this);

	}
	
    /**
     * Test if a store contains a specific fragment, as identified by
     * its index.
     * @param index The index of the fragment to test for.
     * @return true iff the store contains a fragment with the specified 
     * fragment index.
     * @throws XBRLException If the test cannot be conducted.
     */
    public boolean hasFragment(String index) throws XBRLException {
    	if (getFragment(index) == null) {
    		return false;
    	}
    	return true;
    }
	
    /**
     * Retrieves a fragment from an XBRL API data store.
     * The fragment will be created as a fragment of the original 
     * fragment type but will be returned as a straight fragment.
     * @param index The index of the fragment.
     * @return The fragment corresponding to the specified index or null if 
     * the fragment is not in the store.
     * @throws XBRLException if the fragment cannot be retrieved.
     */
    public Fragment getFragment(String index) throws XBRLException {
    	try {
    		XMLResource resource = (XMLResource) collection.getResource(index);
    		if (resource == null) return null;
    		Element root = getResourceRootElement(resource);
    		return FragmentFactory.newFragment(this, root);
    	} catch (XMLDBException e) {
    		throw new XBRLException("The fragment with index " + index + " could not be retrieved.",e);
    	}
    }
    
    /**
     * @param resource The resource to get the root element from.
     * @return the root element of the resource.
     * @throws XMLDBException
     */
    private Element getResourceRootElement(XMLResource resource) throws XMLDBException {
		Node node = resource.getContentAsDOM();
    	if (node.getNodeType() == Element.DOCUMENT_NODE) {
    		return ((Document) node).getDocumentElement();
    	} else {
    		return (Element) node;
    	}
    }
    


	/**
	 * Remove a fragment from the DTS.  
	 * If a fragment with the same ID does not already exist in the 
	 * DTs then no action is required.
	 * @param index The index of the fragment to be removed from the DTS store.
	 * @throws XBRLException if the fragment cannot be removed from the store.
	 */
	public void removeFragment(String index) throws XBRLException {
        try {
            if (!hasFragment(index)) {
            	return;
            }

            Resource document = collection.getResource(index);
            collection.removeResource(document);
        
        }
        catch (XMLDBException e) {
        	throw new XBRLException("The fragment removal failed.", e);
        }
	}
	
	/**
	 * Updates the fragment within the DTS specified by the provided index.
	 * @param index The index of the fragment to be updated.
	 * @param updateDeclaration The XUpdate markup describing the changes to make to
	 * the fragments in the DTS.
	 * @return the number of fragments affected by the update.
	 */
	public long updateFragment(String index, String updateDeclaration) throws XBRLException {
        try {
            XUpdateQueryService service = (XUpdateQueryService) collection.getService("XUpdateQueryService", "1.0");         
            return service.updateResource(index, new String(updateDeclaration));
        } catch (XMLDBException e) {
        	throw new XBRLException("The XUpdate of the DTS failed.",e);
        }
	}

	/**
	 * Updates fragments within the DTS
	 * @param updateDeclaration The XUpdate markup describing the changes to make to
	 * the fragments in the DTS.
	 * @return the number of fragments affected by the update.
	 */
	public long updateFragments(String updateDeclaration) throws XBRLException {
        try {
            XUpdateQueryService service = (XUpdateQueryService) collection.getService("XUpdateQueryService", "1.0");
            return service.update(updateDeclaration);
        } catch (XMLDBException e) {
        	throw new XBRLException("The XUpdate of the DTS failed.",e);
        }
	}
	

	
	/**
	 * @see org.xbrlapi.data.Store#query(String)
	 */
    @SuppressWarnings(value = "unchecked")
	public <F extends Fragment> FragmentList<F> query(String query) throws XBRLException {
		
		ResourceSet resources = null;
		try {
			resources = xpathService.query(query);
		} catch (XMLDBException e) {
			throw new XBRLException("The XPath query service failed.", e);
		}

		FragmentList<F> fragments = new FragmentListImpl<F>();
		try {
			ResourceIterator iterator = resources.getIterator();
			while (iterator.hasMoreResources()) {
				Element root = getResourceRootElement((XMLResource) iterator.nextResource());

				fragments.addFragment((F) FragmentFactory.newFragment(this, root));
			}
		} catch (XMLDBException e) {
			throw new XBRLException("The XPath query of the DTS failed.", e);
		}
		return fragments;
	}
	
	/**
	 * Run a query against the collection of all fragments in the store.
	 * @param query The XPath query to run against the set of fragments.
	 * @param fragments The list of fragments to be populated with the query results.
	 * @return a list of matching fragments or the empty list if no matching fragments
	 * exist.
	 * @throws XBRLException if the query cannot be executed.
	 */
/*	public <F extends Fragment> void query(String query) throws XBRLException {
		ResourceSet resources = null;
		try {
			resources = xpathService.query(query);
		} catch (XMLDBException e) {
			throw new XBRLException("The XPath query service failed.", e);
		}
		try {
			ResourceIterator iterator = resources.getIterator();
			while (iterator.hasMoreResources()) {
				XMLResource resource = (XMLResource) iterator.nextResource();
				fragments.addFragment((F) FragmentFactory.newFragment(this, resource));
			}
		} catch (XMLDBException e) {
			throw new XBRLException("The XPath query of the DTS failed.", e);
		}
		return fragments;		
	}*/
	
    /**
     * Stores the maximum fragment ID, to use if the DTS is ever extended.
     * @param id  The next ID to use for the next fragment to be added to the DTS.
     * @throws XBRLException
     */
    public void storeNextFragmentId(String id) throws XBRLException {
    	try {
			XMLResource data = (XMLResource) collection.createResource("summary", XMLResource.RESOURCE_TYPE);
	        data.setContent("<summary maximumFragmentId='" + id + "'/>");
	        collection.storeResource(data);
    		    		
    	} catch (XMLDBException e) {
    		throw new XBRLException("The next Fragment ID could not be stored.",e);
    	}
    }

    /**
     * Get the maximum fragment ID, to use when extending a DTS, instead of starting at 1 again
     * and corrupting the DTS data store with duplicate fragment IDs.
     * @return The next ID to use for the next fragment to be added to the DTS.  Returns 1 if the 
     * DTS data store is empty.
     * @throws XBRLException if the maximum fragment ID cannot be retrieved.
     */
    public String getNextFragmentId() throws XBRLException {
    	try {
    		XMLResource summary = (XMLResource) collection.getResource("summary");
    		Element documentElement = getDocumentNode(summary).getDocumentElement();
    		String maxId = documentElement.getAttribute("maximumFragmentId");
    		if (maxId.equals("")) {
    			return "1";
    		} else {
    			return maxId;
    		}
    	} catch (XMLDBException e) {
    		throw new XBRLException("The next Fragment ID could not be retrieved.",e);
    	}
    }
    
	/**
	 * @param resource The XMLResource to be used to get the DOM document node.
	 * @return the org.w3.Document node from an XMLResource returned by the
	 * data store or null if no document node is available.
	 * @throws XBRLException if the resource content is not available as a DOM object.
	 */
	private Document getDocumentNode(XMLResource resource) throws XBRLException {
		Node node = null;
		try {
			node = resource.getContentAsDOM();
		} catch (XMLDBException e) {
			throw new XBRLException("The resource content was not available as a DOM object.", e);
		}
		
    	if (node.getNodeType() == Element.DOCUMENT_NODE) {
    		return (Document) node;
    	} else {
    		return ((Element) node).getOwnerDocument();  // May return a null value!
    	}
	}    
 
    
    
}