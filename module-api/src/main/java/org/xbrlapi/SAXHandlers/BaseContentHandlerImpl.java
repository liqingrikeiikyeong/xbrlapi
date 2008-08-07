package org.xbrlapi.SAXHandlers;

import java.net.URL;

import org.apache.log4j.Logger;
import org.xbrlapi.loader.Loader;
import org.xbrlapi.utilities.XBRLException;
import org.xbrlapi.xlink.ElementState;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX content handler used in construction of an XBRL 
 * Discoverable Taxonomy Set (DTS).
 * The content handler is responsible for building up the XBRL
 * XML fragments as they are parsed and then passing them over
 * to the underlying data representation for storage.
 * 
 * The content handler needs to be supplied with a variety of helpers
 * to assist with data storage and XLink processing. These are 
 * supplied by the loader.
 * 
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class BaseContentHandlerImpl extends DefaultHandler implements ContentHandler {

	protected static Logger logger = Logger.getLogger(BaseContentHandlerImpl.class);	

	/**
     * The DTS loader that uses this content handler 
     * to process discovered XML
     */
    private Loader loader = null;
    
    /**
     * @see org.xbrlapi.SAXHandlers.ContentHandler#getLoader()
     */
    public Loader getLoader() {
        return loader;
    }
    
    /**
     * @see org.xbrlapi.SAXHandlers.ContentHandler#setLoader(Loader)
     */
    public void setLoader(Loader loader) throws XBRLException {
        if (loader == null) throw new XBRLException("The loader cannot be null.");
        this.loader = loader;
    }

    /**
     * The URL of the document being parsed.  This is used to
     * recover the XML Schema model for the document if required.
     */
    private URL url = null;
    
    /**
     * @see org.xbrlapi.SAXHandlers.ContentHandler#getURL()
     */
    public URL getURL() {
        return url;
    }

    /**
     * @see org.xbrlapi.SAXHandlers.ContentHandler#setURL(URL)
     */
    public void setURL(URL url) throws XBRLException {
        if (url == null) {
            throw new XBRLException("The url must not be null.");
        }
        this.url = url;
    }
    
    /**
     * Data required to track the element scheme XPointer 
     * expressions that can be used to identify XBRL fragments.
     */
    private ElementState state = null;
    
    /**
     * @param state The element state
     */
    public void setState(ElementState state) {
        this.state = state;
    }
    
    /**
     * @return the state for the element currently being parsed.
     */
    public ElementState getState() {
        return state;
    }    
    
    /**
     * @param loader The DTS loader that is using this content handler.
     * @param url The URL of the document being parsed.
     * @throws XBRLException if any of the parameters are null.
     */
	public BaseContentHandlerImpl(Loader loader, URL url) throws XBRLException {
		super();
		setURL(url);
		setLoader(loader);		
	}
    
	/**
	 * @see org.xbrlapi.SAXHandlers.ContentHandler#error(SAXParseException)
	 */
    public void error(SAXParseException exception) throws SAXException {
		System.out.println(exception);
	}

    /**
     * @see org.xbrlapi.SAXHandlers.ContentHandler#fatalError(SAXParseException)
     */
	public void fatalError(SAXParseException exception) throws SAXException {
		System.out.println(exception);
	}

    /**
     * @see org.xbrlapi.SAXHandlers.ContentHandler#warning(SAXParseException)
     */
	public void warning(SAXParseException exception) throws SAXException {
		System.out.println(exception);
	}
    
}