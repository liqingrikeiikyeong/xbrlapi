package org.xbrlapi.xdt.aspects;

import org.xbrlapi.Fragment;
import org.xbrlapi.aspects.Aspect;
import org.xbrlapi.aspects.BaseAspectValue;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class ScenarioRemainderAspectValue extends BaseAspectValue {

    public ScenarioRemainderAspectValue(Aspect aspect, Fragment fragment)
            throws XBRLException {
        super(aspect, fragment);
        if (! fragment.isa("org.xbrlapi.impl.ScenarioImpl")) {
            throw new XBRLException("Fragment does not match the type of the aspect value.");
        }
    }

}