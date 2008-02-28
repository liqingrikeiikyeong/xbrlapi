package org.xbrlapi.impl;

/**
 * A mock fragment object that is used only for testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

import org.xbrlapi.builder.BuilderImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

public class MockFragmentImpl extends FragmentImpl {
	
	/**
	 * No argument constructor.
	 * @throws XBRLException
	 */
	public MockFragmentImpl() throws XBRLException {
		super();
		this.setBuilder(new BuilderImpl());
		getBuilder().appendElement(Constants.XBRLAPINamespace,"fragment",Constants.XBRLAPIPrefix + ":fragment");	
	}
	
	/**
	 * @param id The unique id of the fragment being created,
	 * within the scope of the containing data store.
	 * @throws XBRLException
	 */
	public MockFragmentImpl(String id) throws XBRLException {
		this();
		this.setFragmentIndex(id);
		getBuilder().appendElement(
				Constants.XBRLAPINamespace,
				"fragment",
				Constants.XBRLAPIPrefix + ":fragment");
	}

	/**
	 * @param id The unique id of the fragment being created,
	 * within the scope of the containing DTS.
	 * @param namespace The namespace for the root element of the data in the fragment.
	 * @param name The local name for the root element of the data in the fragment.
	 * @param qName The QName for the root element of the data in the fragment.
	 * @throws XBRLException
	 */
	public MockFragmentImpl(String id, String namespace, String name, String qName) throws XBRLException {
		this(id);
		getBuilder().appendElement(namespace, name, qName);
	}

	/**
	 * Set the information about the sequence to be followed to reach the parent element 
	 * of the fragment.
	 */
    public void setSequenceToParentElement(String sequence) throws XBRLException {
    	this.setMetaAttribute("SequenceToParentElement", sequence);
    }

    /**
     * Set the preceding siblings information for the mock fragment.
     */
    public void setPrecedingSiblings(String precedingSiblings) throws XBRLException {
    	this.setMetaAttribute("precedingSiblings", precedingSiblings);
    }
	
}
