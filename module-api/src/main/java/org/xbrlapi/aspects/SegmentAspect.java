package org.xbrlapi.aspects;

import org.xbrlapi.Context;
import org.xbrlapi.Entity;
import org.xbrlapi.Fact;
import org.xbrlapi.Fragment;
import org.xbrlapi.Segment;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class SegmentAspect extends BaseContextAspect implements Aspect {

    /**
     * @param aspectModel The aspect model with this aspect.
     * @throws XBRLException.
     */
    public SegmentAspect(AspectModel aspectModel) throws XBRLException {
        setAspectModel(aspectModel);
        setTransformer(new Transformer());
    }

    /**
     * @see Aspect#getType()
     */
    public String getType() {
        return Aspect.SEGMENT;
    }

    private class Transformer implements AspectValueTransformer {
        public Transformer() {
            super();
        }
        public String transform(AspectValue value) throws XBRLException {
            if (! value.getFragment().isa("org.xbrlapi.impl.SegmentImpl")) {
                throw new XBRLException("The fragment is not the correct fragment type.");
            }
            Segment f = ((Segment) value.getFragment());
            return f.getStore().serializeToString(f.getDataRootElement());
        }
    }    
    
    /**
     * @see org.xbrlapi.aspects.Aspect#getValue(org.xbrlapi.Fact)
     */
    @SuppressWarnings("unchecked")
    public SegmentAspectValue getValue(Fact fact) throws XBRLException {
        try {
            return new SegmentAspectValue(this,getFragment(fact));
        } catch (XBRLException e) {
            return null;
        }
    }

    /**
     * @see Aspect#getFragmentFromStore(Fact)
     */
    public Fragment getFragmentFromStore(Fact fact) throws XBRLException {
        Entity entity = ((Context) super.getFragment(fact)).getEntity();
        Segment segment = entity.getSegment();
        if (segment == null) throw new XBRLException("The segment fragment is not available.");
        return segment;
    }
}