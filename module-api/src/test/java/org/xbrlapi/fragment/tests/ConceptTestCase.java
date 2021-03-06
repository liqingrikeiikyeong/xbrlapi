package org.xbrlapi.fragment.tests;

import java.util.List;

import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fact;
import org.xbrlapi.Fragment;
import org.xbrlapi.Item;
import org.xbrlapi.impl.ConceptImpl;
import org.xbrlapi.networks.Network;
import org.xbrlapi.networks.Networks;
import org.xbrlapi.networks.NetworksImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Tests the implementation of the org.xbrlapi.Concept interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ConceptTestCase extends DOMLoadingTestCase {
	private final String FOOTNOTELINKS = "test.data.footnote.links";
	private final String LABELLINKS = "test.data.label.links";
    private final String PRESENTATIONLINKS = "test.data.local.xbrl.presentation.simple";
    private final String TUPLES = "test.data.local.xbrl.instance.tuples.with.units";
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}	
	
	public ConceptTestCase(String arg0) {
		super(arg0);
	}

	public void testGetPeriodType() {	

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                if (concept.getName().equals("CurrentAsset"))
                    assertEquals("duration", concept.getPeriodType());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
	}
	
    public void testGetFacts() {   

        boolean testDone = false;
        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                List<Fact> facts = concept.getFacts();
                for (Fact fact: facts) {
                    assertEquals(fact.getLocalname(), fact.getConcept().getName());
                    testDone = true;
                }
            }
            assertTrue(testDone);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    public void testGetRootFacts() {   

        boolean testDone = false;
        try {
            loader.discover(this.getURI(TUPLES));
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                List<Fact> facts = concept.getRootFacts();
                logger.info(concept.getName() + " # facts = " + facts.size());
                for (Fact fact: facts) {
                    assertEquals(fact.getLocalname(), fact.getConcept().getName());
                    testDone = true;
                }
            }
            assertTrue(testDone);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }    
    
    public void testGetFactCount() {   

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources(ConceptImpl.class);
            assertTrue(concepts.size() > 0);
            boolean foundFacts = false;
            for (Concept concept: concepts) {
                if (concept.getFactCount() > 0) {
                    foundFacts = true;
                }
                assertEquals(concept.getFactCount(),concept.getFacts().size());
                logger.info(concept.getFactCount());
            }
            assertTrue("no concepts found to have facts.", foundFacts);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }    
    
    public void testNumericItemTypeDetection() {   

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));   
            loader.discover(this.getURI(PRESENTATIONLINKS));               
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                logger.info(concept.getTypeLocalname() + " - " + concept.isNumeric());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
	
	public void testGetBalance() {	

		try {
	        loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                if (concept.getName().equals("CurrentAsset"))
                    assertNull(concept.getBalance());
            }
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testGetLocators() {	

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                if (concept.getName().equals("CurrentAsset"))
                    assertEquals(0,concept.getReferencingLocators().size());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
	}	
	
	public void testGetLabels() {

	    try {
	        loader.discover(this.getURI(LABELLINKS));

			List<Concept> concepts = store.getXMLResources("Concept");
			for (Concept concept: concepts) {
				if (concept.getName().equals("CurrentAsset"))
					assertEquals(3,concept.getLabels().size());
			}

		} catch (XBRLException e) {
			fail(e.getMessage());
		}
	}
	
    public void testGetPresentationNetworks() {
    
        try {
            loader.discover(this.getURI(this.PRESENTATIONLINKS));       

            Networks networks = new NetworksImpl(store);
            List<Item> facts = store.getItems();
            assertEquals(1,facts.size());
            for (Item fact: facts) {
                logger.info(fact.getConcept().getLabels().get(0).getStringValue());
                networks = store.getMinimalNetworksWithArcrole(fact.getConcept(),Constants.PresentationArcrole);
            }
            Networks presentationNetworks = networks.getNetworks(Constants.PresentationArcrole);
            
            assertEquals(1,presentationNetworks.getSize());
            
            for (Network network: presentationNetworks) {
                assertEquals(2,network.getNumberOfActiveRelationships());

                List<Fragment> roots = network.getRootFragments(); 
                assertEquals(1,roots.size());
                network.complete();
                
                assertEquals(6,network.getNumberOfActiveRelationships());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
	
}
