package org.xbrlapi;

import org.w3c.dom.NamedNodeMap;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface Arc extends ExtendedLinkContent {

    /**
     * Set the xlink:show attribute value.
     *
     * @throws XBRLException
     */
    public void setShow(String value) throws XBRLException;
	
    /**
     * Set the xlink:actuate attribute value.
     *
     * @throws XBRLException
     */
    public void setActuate(String value) throws XBRLException;

    /**
     * Get the xlink:show attribute value.
     * @return the value of the xlink:show value or null if the attribute is not there.
     * @throws XBRLException
     */
    public String getShow() throws XBRLException;
	
    /**
     * Get the xlink:actuate attribute value.
     * @return the value of the xlink:actuate value or null if the attribute is not there.
     * @throws XBRLException
     */
    public String getActuate() throws XBRLException;
    
    /**
     * Get the xlink:from attribute value.
     * @return the value of the xlink:from value or null if the attribute is not there.
     * @throws XBRLException
     */
    public String getFrom() throws XBRLException;
    
    /**
     * Set the xlink:from attribute value.
     *
     * @param from The value of the from attribute
     * @throws XBRLException
     */
    public void setFrom(String from) throws XBRLException;

    /**
     * Get the xlink:to attribute value.
     * @return the value of the xlink:to value or null if the attribute is not there.
     * @throws XBRLException
     */
    public String getTo() throws XBRLException;
    
    /**
     * @return the arc role of the arc.
     * @throws XBRLException
     */
    public String getArcrole() throws XBRLException;
    
    /**
     * Set the xlink:to attribute value.
     *
     * @param to The value of the to attribute
     * @throws XBRLException
     */
    public void setTo(String to) throws XBRLException;
	
    /**
     * Get the order attribute value.
     * @return the value of the order attribute or default value of 1 if none is provided.
     * @throws XBRLException
     */
    public String getOrder() throws XBRLException;
    
    /**
     * Set the order attribute value.
     *
     * @param order The value of the order attribute
     * @throws XBRLException
     */
    public void setOrder(String order) throws XBRLException;

    /**
     * Get the list of ArcEnd fragments that the arc runs from.
     * @return the list of ArcEnd fragment that the arc runs from.
     * @throws XBRLException
     */
    public <E extends ArcEnd> FragmentList<E> getSourceFragments() throws XBRLException;
    
    /**
     * Get the list of ArcEnd fragments that the arc runs to.
     * @return the list of ArcEnd fragment that the arc runs to.
     * @throws XBRLException
     */
    public <E extends ArcEnd> FragmentList<E> getTargetFragments() throws XBRLException;
    

    /**
     * Gets a list of the attributes that define the semantics of an arc.
     * These are important as part of checking whether two arcs are 
     * semantically equal and so can prohibit or over-ride eachother.
     * @return a map of attribute nodes for semantic analysis.
     * @throws XBRLException
     * @see org.xbrlapi.Arc#semanticEquals(Arc)
     */
    public NamedNodeMap getSemanticAttributes() throws XBRLException;
    
    /**
     * @return A string that is only equal for semantically equal arcs.
     * The string reflects the names and values of the semantic attributes
     * and provides that information in a consistent order.
     * @throws XBRLException
     */
    public String getSemanticKey() throws XBRLException;    
    
    /**
     * Returns true if this arc is semantically equal to the other arc.
     * Semantic equality is defined such that two arcs can prohibit or
     * override each-other only if they are semantically equal.
     * This is a pre-requisite (but not the only) condition for prohibition
     * and over-riding of XLink relationships.
     * @param other The other arc.
     * @return true if and only if the two arcs are semantically equal.
     * @throws XBRLException
     */
    public boolean semanticEquals(Arc other) throws XBRLException;
    
    /**
     * @return the relationship priority specified on the arc.
     * @throws XBRLException
     */
    public Integer getPriority() throws XBRLException;

    /**
     * @return the relationship use, "optional" or
     * "prohibited", as specified on the arc.
     * @throws XBRLException
     */
    public String getUse() throws XBRLException;
    
    /**
     * @return true if the arc has a use attribute 
     * equal to 'prohibited' and false otherwise.
     * @throws XBRLException
     */
    public boolean isProhibited() throws XBRLException;
}
