package org.xbrlapi.aspects;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.xbrlapi.Fact;
import org.xbrlapi.utilities.XBRLException;

/**
 * Abstract implementation of common aspect model methods.
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
abstract public class BaseAspectModel implements AspectModel {

    /**
     * From aspect type to aspect.
     */
    private Map<String,Aspect> aspects = new HashMap<String,Aspect>();
    /**
     * From dimension name to list of aspects in dimension.
     */
    private Map<String,LinkedList<Aspect>> dimensions = new HashMap<String,LinkedList<Aspect>>();
    
    private Set<Fact> facts = new TreeSet<Fact>(); 
    
    /**
     * @see AspectModel#getAspects()
     */
    public Collection<Aspect> getAspects() {
        return aspects.values();
    }
    
    /**
     * @see AspectModel#getAspect(String)
     */
    public Aspect getAspect(String type) throws XBRLException {
        if (hasAspect(type)) {
            return aspects.get(type);
        }
        throw new XBRLException("The aspect model does not include aspect " + type);
    }
    
    /**
     * @see AspectModel#hasAspect(String)
     */
    public boolean hasAspect(String type) {
        return aspects.containsKey(type);
    }

    /**
     * @see AspectModel#getOrphanAspects()
     */
    public Collection<Aspect> getOrphanAspects() {
        Collection<Aspect> aspects = this.aspects.values();
        for (Aspect aspect: aspects) {
            if (aspect.getDimension() != null) aspects.remove(aspect);
        }
        return aspects;
    }

    /**
     * @see AspectModel#getDimensionAspects(String)
     */
    public LinkedList<Aspect> getDimensionAspects(String dimension) {
        return dimensions.get(dimension);
    }


    /**
     * @see AspectModel#setAspect(Aspect)
     */
    public void setAspect(Aspect aspect) {
        try {
            aspect.setAspectModel(this);
        } catch (XBRLException e) {
            ;//Not possible
        }
        if (aspects.containsKey(aspect.getType())) {
            Aspect old = aspects.get(aspect.getType());
            if (old.getDimension() != null) {
                List<Aspect> dimensionAspects = dimensions.get(old.getDimension());
                LOOP: for (Aspect dimensionAspect: dimensionAspects) {
                    if (dimensionAspect.getType().equals(old.getType())) {
                        dimensionAspects.remove(dimensionAspect);
                        break LOOP;
                    }
                }
            }
        }
        aspects.put(aspect.getType(),aspect);
        aspect.setDimension(null);
    }
    
    /**
     * @see AspectModel#arrangeAspect(Aspect, String)
     */
    public void arrangeAspect(String aspectType, String dimension) throws XBRLException {

        if (! aspects.containsKey(aspectType)) throw new XBRLException("The aspect is not part of the aspect model.");
        Aspect aspect = aspects.get(aspectType);
        
        if (! aspect.isOrphan()) {
           LinkedList<Aspect> dimensionAspects = dimensions.get(aspect.getDimension());
           dimensionAspects.remove(aspect);
        }
       
        LinkedList<Aspect> dimensionAspects;
        if (dimensions.containsKey(dimension)) {
            dimensionAspects = this.dimensions.get(dimension);
        } else {
            dimensionAspects = new LinkedList<Aspect>();
            dimensions.put(dimension,dimensionAspects);
        }
        dimensionAspects.add(aspect);

        aspect.setDimension(dimension);
    }
    
    /**
     * @see AspectModel#arrangeAspect(Aspect, String, String)
     */
    public void arrangeAspect(String aspectType, String dimension, String parentType) throws XBRLException {
        if (! aspects.containsKey(aspectType)) throw new XBRLException("The aspect is not part of the aspect model.");
        if (! aspects.containsKey(parentType)) throw new XBRLException("The parent aspect is not part of the aspect model.");
        if (! this.dimensions.containsKey(dimension)) throw new XBRLException("The dimension is not part of the aspect model.");
        
        Aspect aspect = aspects.get(aspectType);
        if (aspect.getDimension() != null) {
            List<Aspect> dimensionAspects = dimensions.get(aspect.getDimension());
            dimensionAspects.remove(aspect);
        }
        
        List<Aspect> dimensionAspects = this.dimensions.get(dimension);
        FINDPARENT: for (Aspect dimensionAspect: dimensionAspects) {
            if (dimensionAspect.getType().equals(parentType)) {
                int index = dimensionAspects.indexOf(dimensionAspect)+1;
                dimensionAspects.add(index, aspect);
                break FINDPARENT;
            }
        }

        aspect.setDimension(dimension);
        
    }

    /**
     * @see AspectModel#addFact(Fact)
     */
    public void addFact(Fact fact) throws XBRLException {
        facts.add(fact);
        Collection<Aspect> aspects = this.getAspects();
        for (Aspect aspect: aspects) {
            aspect.addFact(fact);
        }
    }
 

    public Set<Fact> getFacts(Collection<AspectValue> values) throws XBRLException {

        if (values == null) throw new XBRLException("The list of aspect values must not be null.");
        
        if (values.isEmpty()) return getAllFacts();
        
        Set<Fact> matches = null;
        for (AspectValue value: values) {
            if (matches == null) {
                matches = value.getAspect().getFacts(value);
            } else {
                Set<Fact> candidates = value.getAspect().getFacts(value);
                matches.retainAll(candidates);
            }
        }
        return matches;
    }
    
    public Set<Fact> getAllFacts() throws XBRLException {
        return facts;
    }
    

    
    /**
     * @see AspectModel#getMatchingFacts()
     */
    public Set<Fact> getMatchingFacts() throws XBRLException {
        Set<Fact> matches = null;
        for (Aspect aspect: getAspects()) {
            if (matches == null) {
                if(aspect.hasSelectionCriterion()) {
                    matches = aspect.getMatchingFacts();
                } else {
                    matches = getAllFacts();
                }
            } else {
                if(aspect.hasSelectionCriterion()) {
                    Set<Fact> candidates = aspect.getMatchingFacts();
                    matches.retainAll(candidates);
                }
            }
        }
        return matches;
    }
    
    /**
     * @see AspectModel#setCriterion(AspectValue)
     */
    public void setCriterion(AspectValue criterion) throws XBRLException {
        Aspect aspect = this.getAspect(criterion.getAspect().getType());
        aspect.setSelectionCriterion(criterion);
    }

    /**
     * @see AspectModel#clearAllCriteria()
     */
    public void clearAllCriteria() {
        for (Aspect aspect: getAspects()) {
            aspect.clearSelectionCriterion();
        }
    }
    
    /**
     * @see AspectModel#getAspectValueCombinationsForDimension(String)
     */
    public List<List<AspectValue>> getAspectValueCombinationsForDimension(String dimension) {
        
        // Set up the result matrix
        List<Aspect> aspects = getDimensionAspects(dimension);
        List<List<AspectValue>> result = new Vector<List<AspectValue>>();
        int combinations = aspects.get(0).getDescendantCount() * aspects.get(0).getDescendantCount();
        for (int i=0; i<combinations; i++) {
            result.add(new Vector<AspectValue>());
        }
        for (Aspect aspect: aspects) {
            List<AspectValue> values = aspect.getValues();
            int vCount = values.size();
            int dCount = aspect.getDescendantCount();
            int aCount = aspect.getAncestorCount();
            System.out.println(aspect.getType());
            System.out.println(aCount);
            System.out.println(dCount);
            System.out.println(vCount);
            for (int i=0; i<aCount; i++) {
                for (int j=0; j<dCount; j++) {
                    for (int k=0; k<vCount; k++) {
                        int index = i*dCount*vCount + j*vCount+k;
                        System.out.println(index);
                        result.get(index).add(values.get(k));
                    }
                }
            }
        }
        return result;
    }
    
}
