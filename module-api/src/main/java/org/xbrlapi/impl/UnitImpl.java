package org.xbrlapi.impl;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xbrlapi.Unit;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class UnitImpl extends FactDimensionContainerImpl implements Unit {

    /**
     * Get the numerator set of measures for the unit
     * @return the numerator measure set
     * @throws XBRLException
     * @see org.xbrlapi.Unit#getNumeratorMeasures()
     */
    public NodeList getNumeratorMeasures() throws XBRLException {
    	Element numerator = this.getNumeratorContainer();
    	return numerator.getElementsByTagNameNS(Constants.XBRL21Namespace,"measure");
    }

    /**
     * Determines if the unit has a denominator
     * 
     * @return true if the unit has a denominator that has 
     * at least one measure in it, and false otherwise.
     * @throws XBRLException
     * @see org.xbrlapi.Unit#hasDenominator()
     */
    public boolean hasDenominator() throws XBRLException {
    	NodeList candidates = this.getDataRootElement().getElementsByTagNameNS(Constants.XBRL21Namespace,"divide");
    	if (candidates.getLength() == 1) return true;
    	return false;
    }

	/**
	 * @return the divide element or null if it is not in the unit element.
	 * @throws XBRLException
	 */
    private Element getDivide() throws XBRLException {
    	if (hasDenominator()) return (Element) this.getDataRootElement().getElementsByTagNameNS(Constants.XBRL21Namespace,"divide").item(0);
    	return null;
	}

	/**
	 * @return the unitDenominator element which is the denominator 
	 * measures container element.
	 * @throws XBRLException if the unit does not have a denominator.
	 */
    private Element getDenominatorContainer() throws XBRLException {
    	if (! hasDenominator()) return null;
		NodeList candidates = getDivide().getElementsByTagNameNS(Constants.XBRL21Namespace,"unitDenominator");
		if (candidates.getLength() == 1) return (Element) candidates.item(0);
		throw new XBRLException("The denominator container could not be retrieved.");
	}

	/**
	 * @return the numerator measures container element 
	 * or null if it is not in the unit element.
	 * @throws XBRLException if the unit does not have a numerator.
	 */
    private Element getNumeratorContainer() throws XBRLException {
    	if (! hasDenominator()) return this.getDataRootElement();
    	NodeList candidates = getDivide().getElementsByTagNameNS(Constants.XBRL21Namespace,"unitNumerator");
		if (candidates.getLength() == 1) return (Element) candidates.item(0);
		throw new XBRLException("The numerator container could not be retrieved.");
	}    
    
    /**
     * Get the set of measure elements in the denominator.
     * @return the denominator measure set.
     * @throws XBRLException if the denominator does not exist.
     * @see org.xbrlapi.Unit#getDenominatorMeasures()
     */
    public NodeList getDenominatorMeasures() throws XBRLException {
    	if (this.hasDenominator()) {
        	Element denominator = this.getDenominatorContainer();
        	return denominator.getElementsByTagNameNS(Constants.XBRL21Namespace,"measure");
    	}
    	return null;
    }

    /**
     * Removes the denominator set of measures from the unit
     * @throws XBRLException
     * @see org.xbrlapi.Unit#removeDenominator()
     */
    public void removeDenominator() throws XBRLException {
    	throw new XBRLException("Data update methods are not yet implemented.");
    }

    /**
     * Tests if the unit is u-equal to another unit
     * @param unit The unit being compared.
     * @return true if the units are u-equal and false otherwise
     * @throws XBRLException
     * @see org.xbrlapi.Unit#equals(Unit)
     */
    public boolean equals(Unit unit) throws XBRLException {
    	if (this.hasDenominator()) {
    		if (! unit.hasDenominator()) return false;
    		if (! measuresMatch(this.getDenominatorMeasures(),unit.getDenominatorMeasures())) return false;
    	}
		if (! measuresMatch(this.getNumeratorMeasures(),unit.getNumeratorMeasures())) return false;
		return true;
    }

    /**
     * Check if the measures in list a match those in list b.
     * @param a A node list of measure elements.
     * @param b A second node list of measure elements.
     * @return true if the measures exactly match, one for one and false otherwise.
     * @throws XBRLException
     */
    private boolean measuresMatch(NodeList a, NodeList b) throws XBRLException {
    	
    	if (a.getLength() != b.getLength()) return false;
    	
    	HashMap<String,Integer> measures = new HashMap<String,Integer>();
    	for (int i=0; i<a.getLength(); i++) {
    		Element measure = (Element) a.item(i);
    		String content = measure.getTextContent();
    		String key = this.getNamespaceFromQName(content,measure) + this.getLocalnameFromQName(content);
    		if (measures.containsKey(key)) {
    			Integer newValue = new Integer((measures.get(key)).intValue() + 1);
    			measures.put(key, newValue); 
    		} else measures.put(key,new Integer(1));
    		
    		measure = (Element) b.item(i);
    		content = measure.getTextContent();
    		key = this.getNamespaceFromQName(content,measure) + this.getLocalnameFromQName(content);
    		if (measures.containsKey(key)) {
    			Integer newValue = new Integer((measures.get(key)).intValue() - 1);
    			measures.put(key, newValue);
    		} else measures.put(key,new Integer(-1));
    		
    	}
    	
    	for (Integer value: measures.values()) {
    		if (value.intValue() != 0) return false;
    	}
    	return true;
    }
    
    private String getMeasureNamespace(Element measure) throws XBRLException {
    	return this.getNamespaceFromQName(measure.getTextContent(),measure);
    }
    
    private String getMeasureName(Element measure) {
    	return this.getLocalnameFromQName(measure.getTextContent());
    }    

}
