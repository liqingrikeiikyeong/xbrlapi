package org.xbrlapi.aspects.alt;

import java.net.URI;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.xbrlapi.Period;
import org.xbrlapi.utilities.XBRLException;

public class PeriodAspectValue extends AspectValueImpl implements AspectValue {

    /**
     * 
     */
    private static final long serialVersionUID = -5811178785037672992L;

    protected final static Logger logger = Logger
    .getLogger(PeriodAspectValue.class);

    /**
     * Flag for missing period aspect values.
     */
    private boolean isMissing = true;
    
    /**
     * The start and end dates for periods.
     * The end is for instants and the ends of finite durations.
     */
    private XMLGregorianCalendar start, end;
    
    
    /**
     * The start and end date string values as given in the
     * XBRL contexts.  The rawEnd is used for ends of finite 
     * durations and for instants.
     */
    private String rawStart = null;
    private String rawEnd = null;
    
    /**
     * The boolean flags for date/only values
     */
    private boolean startDateOnly, endDateOnly;

    /**
     * Flag for null period aspect values.
     */
    private boolean isForever = false;

    /**
     * Missing aspect value constructor - relevant for tuples and nil facts.
     */
    public PeriodAspectValue() {
        super();
    }
    
    /**
     * @param period The context period.
     * @throws XBRLException if the parameter is null.
     */
    public PeriodAspectValue(Period period) throws XBRLException {
        super();
        if (period == null) throw new XBRLException("The context period must not be null.");
        isMissing = false;
        if (period.isInstantPeriod()) {
            rawEnd = period.getInstant();
            end = period.getInstantCalendar();
            endDateOnly = period.instantIsDateOnly();
        } else if (period.isFiniteDurationPeriod()) {
            start = period.getStartCalendar();
            end = period.getEndCalendar();
            rawStart = period.getStart();
            rawEnd = period.getEnd();
            endDateOnly = period.endIsDateOnly();
            startDateOnly = period.startIsDateOnly();
        } else {
            isForever = true;
        }
    }

    /**
     * @see AspectValue#getId()
     */
    public String getId() {
        if (this.isMissing()) return "";
        if (isForever) return "forever";
        if (isFiniteDuration()) return getString(start) + " to " + getString(end);
        return getString(end);
    }
    
    /**
     * @return the start date/time Calendar or null if it is not defined.
     */
    public XMLGregorianCalendar getStart() {
        return start;
    }
    
    /**
     * @return the end date/time Calendar or null if it is not defined.
     */
    public XMLGregorianCalendar getEnd() {
        if (! isDuration()) return null;
        return end;
    }
    
    /**
     * @return the raw String representation of the finite duration end date
     * or the instant date in a context period.  Returns null if the period is
     * forever.
     */
    public String getRawEnd() {
        return rawEnd;
    }
    
    /**
     * @return the raw String representation of the finite duration start date 
     * in a context period.  Returns null if the period is not a finite duration.
     */
    public String getRawStart() {
        return rawEnd;
    }    
    
    /**
     * @return the instant date/time Calendar or null if it is not defined.
     */
    public XMLGregorianCalendar getInstant() {
        if (isDuration()) return null;
        return end;
    }    
    
    /**
     * @return true if the period is forever and false otherwise.
     */
    public boolean isForever() {
        return this.isForever;
    }
    
    /**
     * @return true if the period is forever and false otherwise.
     */
    public boolean isDuration() {
        if (isForever()) return true;
        return start != null;
    }
    
    /**
     * @return true if the period is a finite duration and false otherwise.
     */
    public boolean isFiniteDuration() {
        return (start != null);
    }
    
    /**
     * @param calendar The calendar to provide a string representation for.
     * @return a string representation of the calendar, corresponding to
     * an XBRL period instant or an end date.
     */
    private String getString(XMLGregorianCalendar calendar) {

        String result = "" + calendar.getYear() + "-" + pad(calendar.getMonth()) + "-" + pad(calendar.getDay());

        if (calendar.getFractionalSecond() != null)
            result += ", " + pad(calendar.getHour()) + ":" + pad(calendar.getMinute()) + ":" + pad((new Double(calendar.getSecond()).doubleValue() + new Double(calendar.getFractionalSecond().toString()).doubleValue()));
        else 
            result += ", " + pad(calendar.getHour()) + ":" + pad(calendar.getMinute()) + ":" + pad(calendar.getSecond());
            
        if (calendar.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
             result += " no timezone";
        } else {
            result += " " + calendar.getTimeZone(0).getID();
        }
        
        return result;

    }
    
    private String pad(int value) {
        if (value < 10) return "0" + value;
        return "" + value;
    }
    
    private String pad(double value) {
        if (value < 10) return "0" + value;
        return "" + value;
    }    
    
    /**
     * @see AspectHandler#getAspectId()
     */
    public URI getAspectId() {
        return PeriodAspect.ID;
    }
    
    /**
     * @see AspectValue#isMissing()
     */
    public boolean isMissing() {
        return isMissing;
    }

    /**
     * @return true if the period is a finite duration and the start
     * date has no time component and false otherwise.
     */
    public boolean startIsDateOnly() {
        if (! isFiniteDuration()) return false;
        return this.getStart().getXMLSchemaType().getLocalPart().equals("date");
    }    

    public boolean endIsDateOnly() {
        if (isForever()) return false;
        return this.getEnd().getXMLSchemaType().getLocalPart().equals("date");
    }    

}
