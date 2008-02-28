package org.xbrlapi.impl;

import org.w3c.dom.Node;
import org.xbrlapi.UsedOn;
import org.xbrlapi.utilities.XBRLException;

/**
 * TODO Eliminate the usedOn fragment
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class UsedOnImpl extends FragmentImpl implements UsedOn {

    /**
     * Get the namespace URI of the element that can
     * be used on.
     * @throws XBRLException
     * @see org.xbrlapi.UsedOn#getURI()
     */
    public String getURI() throws XBRLException {
    	Node rootNode = getDataRootElement();
    	String u = rootNode.getTextContent().trim();
    	if (u.equals(""))
			throw new XBRLException("The used on declaration does not declare the element that usage is allowed on.");
    	return this.getNamespaceFromQName(u, rootNode);
    }
    
    /**
     * Set the value of the usedOn fragment.
     * @param uri The namespace URI of the element that can
     * be used on
     * @param prefix The prefix to be used in the qName
     * @param localname The local name to be used in the qName 
     * @throws XBRLException
     * @see org.xbrlapi.UsedOn#setValue(String, String, String)
     */
    public void setValue(String uri, String prefix, String localname) throws XBRLException {
    	throw new XBRLException("Data update methods are not yet implemented.");
    }

    /**
     * Get the localname for the element that can be used on.
     * @return the local name of the element that the custom role or arcrole URI can be used on.
     * @throws XBRLException
     * @see org.xbrlapi.UsedOn#getLocalname()
     */
    public String getLocalname() throws XBRLException {
    	Node rootNode = getDataRootElement();
    	String u = rootNode.getTextContent().trim();
    	if (u.equals(""))
			throw new XBRLException("The used on declaration does not declare the element that usage is allowed on.");
    	return this.getLocalnameFromQName(u);
    }
    
    /**
     * Returns true only if the custom role type can be used on the specified element
     * based on this usedOn fragment.
     * @param namespaceURI The namespace of the element being tested for
     * @param localname The local name of the element being tested for
     * @throws XBRLException
     * @see org.xbrlapi.UsedOn#isUsedOn(String, String)
     */
    public boolean isUsedOn(String namespaceURI, String localname) throws XBRLException {
    	if (! getURI().equals(namespaceURI))
    		return false;
    	if (! getLocalname().equals(localname))
    		return false;
    	return true;
    }

}
