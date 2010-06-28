package org.xbrlapi.aspects.alt;

import java.io.Serializable;
import java.net.URI;

import org.xbrlapi.Fact;
import org.xbrlapi.utilities.XBRLException;

/**
 * <h2>
 * Aspect explanation.</h2>
 * 
 * <p>
 * An aspect is a characteristic of XBRL facts. Not all XBRL facts need to
 * define a value for an aspect. For example, non-numeric facts do not define
 * values for the aspect that captures measurement units.
 * </p>
 * 
 * <p>
 * Aspects have the following characteristics:
 * </p>
 * 
 * <ul>
 * <li>Each aspect has a unique identifier. This is expressed
 * by an absolute URI and is accessed via the static ID field that is
 * made public by each concrete aspect class.</li>
 * <li>Where an aspect does not have a value for a fact, the aspect value for
 * that aspect/fact combination is identified as a "missing" aspect value.</li>
 * <li>Aspects can be associated with a set of values for that aspect.
 * <li>Aspects can be used in isolation but can also be combined with other
 * aspects to form an aspect model.</li>
 * <li>Aspects can have multiple human-readable labels, one per language.</li>
 * <li>Aspects define a mapping from a fact to an aspect value. Thus, given a
 * fact, the aspect can produce its aspect value (sometimes that is the missing
 * aspect value).</li>
 * <li>All aspects only have constructors with no arguments.</li>
 * <li>All aspects define a default domain for themselves.</li>
 * </ul>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public interface Aspect extends Serializable {

    /**
     * @param fact
     *            The fact to get the aspect value for.
     * @return a value for this aspect for the given fact.
     * @throws XBRLException
     */
    public AspectValue getValue(Fact fact) throws XBRLException;

    /**
     * @return The domain of valid values for this aspect.
     */
    public Domain getDomain();
    
    /**
     * @return the URI that identifies this aspect.
     */
    public URI getId();
    
    /**
     * @return the missing aspect value for this aspect.
     */
    public AspectValue getMissingValue();
    
}
