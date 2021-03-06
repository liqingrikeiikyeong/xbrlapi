package org.xbrlapi.xdt.values;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

import org.xbrlapi.Concept;
import org.xbrlapi.LabelResource;
import org.xbrlapi.utilities.XBRLException;

/**
 * Replaces the naive sorting of explicit dimension values
 * for the same explicit dimension with one based upon
 * the labels of the domain members (XBRL 2.1 or generic labels).
 * If a domain member does not have a label with the specified
 * resource role then the domain member is ranked after domain members
 * with labels.  Ordering of domain members without labels is still
 * based upon their namespaces and local names.
 * If any domain members have more than one label with the given language
 * and resource role, then that domain member gets ordered in an application
 * dependent manner, by using any one of the labels matching the required 
 * criteria.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ExplicitDimensionValueLabelComparator extends DimensionValueComparator implements Comparator<DimensionValue> {
    
    private URI role = null;
    
    private String language = null;
    
    /**
     * @param language The ISO language code of the label language to use in the ordering.
     * @param labelrole The XLink role value of the labels to be used.
     * @throws XBRLException if the XLink role value is null or not a valid URI.
     */
    public ExplicitDimensionValueLabelComparator(String language, URI labelrole)  throws XBRLException {
        super();
        if (labelrole == null) throw new XBRLException("The label role must not be null.");
        role = labelrole;
        if (language == null) throw new XBRLException("The language must not be null.");
        this.language = language;
    }
    
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(DimensionValue v1, DimensionValue v2) throws ClassCastException {
        try {

            int result = compareDimensions(v1,v2);
            if (result != 0) return result;
            
            if (v1.isTypedDimensionValue() && v2.isTypedDimensionValue()) {
                result = compareTypedDimensionValues(v1, v2);
            }
            if (result != 0) return result;
            
            
            if (v1.isExplicitDimensionValue() && v2.isExplicitDimensionValue()) {
                result = compareExplicitDimensionValues(v1, v2);
            }
            if (result != 0) return result;
            
            return compareItems(v1,v2);
            
        } catch (XBRLException e) {
            throw new ClassCastException("Dimension value comparison is not possible." + e.getMessage());
        }
    }

    /**
     * @param v1 The first explicit dimension value.
     * @param v2 The second explicit dimension value.
     * @return a negative integer, zero, or a positive integer 
     * as the first argument is less than, equal to, or greater than the second.
     * @throws XBRLException
     */
    private int compareExplicitDimensionValues(DimensionValue v1, DimensionValue v2) throws XBRLException {

        Concept m1 = (Concept) v1.getValue();
        Concept m2 = (Concept) v2.getValue();
        
        List<LabelResource> m1Labels = m1.getLabelsWithLanguageAndResourceRole(language, role);
        List<LabelResource> m2Labels = m2.getLabelsWithLanguageAndResourceRole(language, role);
        
        if (m1Labels.size() > 0 && m2Labels.size() > 0) {
            return m1Labels.get(0).getStringValue().compareTo(m2Labels.get(0).getStringValue());
        }
        
        if (m1Labels.size() > 0) {
            return -1;
        }        

        if (m2Labels.size() > 0) {
            return 1;
        }        

        URI v1ns = m1.getTargetNamespace();
        URI v2ns = m2.getTargetNamespace();
        int result = v1ns.compareTo(v2ns);
        if (result != 0) return result;
        
        String v1ln = m1.getName();
        String v2ln = m2.getName();
        return v1ln.compareTo(v2ln);

    }
    



        
    
}
