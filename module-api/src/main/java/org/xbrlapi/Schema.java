package org.xbrlapi;

import java.net.URI;
import java.util.List;

import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface Schema extends SchemaContent {
    
    /**
     * Checks if the element form is qualified.
     * @return true if the element form is qualified and false otherwise.
     * @throws XBRLException
     */
    public boolean isElementFormQualified() throws XBRLException;

    /**
     * @return a list of SimpleLink fragments, one per XML Schema 
     * import used by this schema.
     * @throws XBRLException
     */
    public List<SimpleLink> getImports() throws XBRLException;
    
    /**
     * @return a list of SimpleLink fragments, one per XML Schema 
     * include used by this schema.
     * @throws XBRLException
     */
    public List<SimpleLink> getIncludes() throws XBRLException;
    
    /**
     * @return a list of the extended links contained by the schema.
     * @throws XBRLException
     */
    public List<ExtendedLink> getExtendedLinks() throws XBRLException;    
    
    /**
     * Get the fragment list of element declarations (that are not concepts) in the schema.
     * @return the list of element declarations in the schema.
     * @throws XBRLException.
     */
    public List<Concept> getOtherElementDeclarations() throws XBRLException;
    
    /**
     * Get the fragment list of concepts in the schema.
     * @return the list of concepts in the schema.
     * @throws XBRLException.
     */
    public List<Concept> getConcepts() throws XBRLException;

    
    
    /**
     * Get a specific concept by its name.
     * return the chosen concept or null if the concept does not exist.
     * @param name The name of the concept
     * @throws XBRLException
     */
    public Concept getConceptByName(String name) throws XBRLException;    

    /**
     * Get a list of concepts based on their type.
     * Returns null if no concepts match the selection criteria.
     *
     * @param namespace The namespaceURI of the concept type
     * @param localName The local name of the concept type
     * @return A list of concept fragments in the containing schema that
     * match the specified element type.
     * 
     * @throws XBRLException
     */
    public List<Concept> getConceptsByType(URI namespace, String localName) throws XBRLException;
    
    /**
     * Get a list concepts based on their substitution group.
     * Returns null if no concepts match the selection criteria.
     *
     * @param namespace The namespaceURI of the concept type
     * @param localname The local name of the concept type
     * 
     * @return a list of concepts in the schema that match the specified
     * substitution group
     * 
     * @throws XBRLException
     */
    public List<Concept> getConceptsBySubstitutionGroup(URI namespace, String localname) throws XBRLException;

 		



    /**
     * Get a reference part declaration in a schema.
     * Returns null if the reference part does not exist in the schema.
     *
     * @param name The name attribute value of the reference part to be retrieved.
     * @throws XBRLException
     */
    public ReferencePartDeclaration getReferencePartDeclaration(String name) throws XBRLException;
    
    /**
     * Get a list of the reference part declarations in a schema.
     * @return a list of reference part declarations in the schema.
     * @throws XBRLException
     */
    public List<ReferencePartDeclaration> getReferencePartDeclarations() throws XBRLException;    
    

    



    



    



    


}
