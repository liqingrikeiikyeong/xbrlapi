package org.xbrlapi.utils.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.xbrlapi.utils.tests");
		//$JUnit-BEGIN$
        suite.addTestSuite(DOMBuilderTest.class);

		//$JUnit-END$
		return suite;
	}

}
