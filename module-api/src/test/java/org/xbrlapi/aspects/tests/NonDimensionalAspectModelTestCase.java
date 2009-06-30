package org.xbrlapi.aspects.tests;

import java.util.List;
import java.util.Set;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fact;
import org.xbrlapi.aspects.Aspect;
import org.xbrlapi.aspects.AspectModel;
import org.xbrlapi.aspects.AspectValue;
import org.xbrlapi.aspects.NonDimensionalAspectModel;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class NonDimensionalAspectModelTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT1 = "test.data.small.instance";
    private final String STARTING_POINT2 = "test.data.small.instance.2";	
	
	protected void setUp() throws Exception {
		super.setUp();
        loader.discover(this.getURI(STARTING_POINT1));       
		loader.discover(this.getURI(STARTING_POINT2));		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		
	}	
	
	public NonDimensionalAspectModelTestCase(String arg0) {
		super(arg0);
	}

	
	

	
	public void testCreatingNonDimensionalAspectModel() {
		try {
			List<Fact> facts = store.<Fact>getXMLs("SimpleNumericItem");
			assertEquals(2,facts.size());
            AspectModel model = new NonDimensionalAspectModel();
            model.arrangeAspect(Aspect.CONCEPT,"row");
            model.arrangeAspect(Aspect.ENTITY_IDENTIFIER,"row");
            model.arrangeAspect(Aspect.PERIOD,"col");
            model.arrangeAspect(Aspect.UNIT,"col");
            model.arrangeAspect(Aspect.SCENARIO,"col");
            for (Fact fact: facts) {
                model.addFact(fact);
            }
            for (Aspect aspect: model.getAspects()) {
                logger.info(aspect.getType());
                for (AspectValue value: aspect.getValues()) {
                    logger.info(value.getId());
                }
            }
            assertEquals(6,model.getAspects().size());
            
            model.setCriterion(model.getAspect(Aspect.CONCEPT).getValues().get(0));
            model.setCriterion(model.getAspect(Aspect.ENTITY_IDENTIFIER).getValues().get(0));
            model.setCriterion(model.getAspect(Aspect.PERIOD).getValues().get(0));
            model.setCriterion(model.getAspect(Aspect.UNIT).getValues().get(0));
            List<List<AspectValue>> rowMatrix = model.getAspectValueCombinationsForDimension("row");
            List<List<AspectValue>> colMatrix = model.getAspectValueCombinationsForDimension("col");
            
            for (AspectValue v: colMatrix.get(0)) {
                if (v==null) logger.info("First combination: " + null + "=" + null);
                else logger.info("First combination: " + v.getAspect().getType() + "=" + v.getId());
            }
            assertEquals(4,rowMatrix.size());
            assertEquals(2,rowMatrix.get(0).size());
            assertEquals(2,colMatrix.size());
            assertEquals(3,colMatrix.get(0).size());
            for (List<AspectValue> rowCombination: rowMatrix) {
                for (List<AspectValue> colCombination: colMatrix) {
                    model.clearAllCriteria();
                    model.setCriteria(rowCombination);
                    model.setCriteria(colCombination);
                    Set<Fact> matchingFacts = model.getMatchingFacts();
                    for (Fact matchingFact: matchingFacts) {
                        logger.info(matchingFact.getIndex());
                    }
                }
            }
            
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
