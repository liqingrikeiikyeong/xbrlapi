package org.xbrlapi.aspects;

import java.util.Comparator;
import java.util.TreeMap;

import org.xbrlapi.Context;
import org.xbrlapi.Fact;
import org.xbrlapi.Fragment;
import org.xbrlapi.Period;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class QuarterlyPeriodAspect extends ContextAspect implements Aspect {

    private class PeriodComparator implements Comparator<String> {
        
        public PeriodComparator() {
            super();
        }
        
        public int compare(String left, String right) {
            
            if (equals(left,right)) return 0;
            
            if (left.equals("forever")) return -1;
            if (right.equals("forever")) return 1;
            
            return left.compareTo(right);
        }
        
        public boolean equals(String left, String right) {
            return left.equals(right);
        }
        
    }
    
    /**
     * @param aspectModel The aspect model with this aspect.
     * @throws XBRLException.
     */
    public QuarterlyPeriodAspect(AspectModel aspectModel) throws XBRLException {
        setAspectModel(aspectModel);
        setTransformer(new Transformer());
        values = new TreeMap<String,AspectValue>(new PeriodComparator());
    }

    /**
     * @see Aspect#getType()
     */
    public String getType() {
        return Aspect.PERIOD;
    }

    private class Transformer extends BaseAspectValueTransformer implements AspectValueTransformer {
        public Transformer() {
            super();
        }

        /**
         * @see AspectValueTransformer#validate(AspectValue)
         */
        public void validate(AspectValue value) throws XBRLException {
            super.validate(value);
            if (! value.getFragment().isa("org.xbrlapi.impl.PeriodImpl")) {
                throw new XBRLException("The aspect value must have a period fragment.");
            }
        }
        
        /**
         * @see AspectValueTransformer#getIdentifier(AspectValue)
         */
        public String getIdentifier(AspectValue value) throws XBRLException {
            validate(value);
            if (hasMapId(value)) {
                return getMapId(value);
            }
            Period f = ((Period) value.getFragment());
            String id = "";
            if (f.isFiniteDurationPeriod()) {
                id += getYear(f.getEnd()) + "-Q" + getQuarter(f.getEnd());
            } else if (f.isInstantPeriod()) {
                id += getYear(f.getInstant()) + "-Q" + getQuarter(f.getInstant());
            } else {
                id += "forever";
            }
            setMapId(value,id);
            return id;
        }
        
        /**
         * @see AspectValueTransformer#getLabel(AspectValue)
         */
        public String getLabel(AspectValue value) throws XBRLException {
            return getIdentifier(value);
        }        
        
        private String getYear(String value) {
            return value.substring(0,4);
        }
        
        private String getQuarter(String value) {
            int month = (new Integer(value.substring(5,6))).intValue();
            if (month <= 3) return "1";
            if (month <= 6) return "2";
            if (month <= 9) return "3";
            return "4";
        }
        
        
    }    

    /**
     * @see org.xbrlapi.aspects.Aspect#getValue(org.xbrlapi.Fact)
     */
    @SuppressWarnings("unchecked")
    public AspectValue getValue(Fact fact) throws XBRLException {
        try {
            return new PeriodAspectValue(this,get(fact));
        } catch (XBRLException e) {
            return null;
        }
    }

    /**
     * @see Aspect#getFromStore(Fact)
     */
    public Fragment getFromStore(Fact fact) throws XBRLException {
        return ((Context) super.getFromStore(fact)).getPeriod();
    }    
    
}
