package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import Ecore2CSP.ConfigFileReader;

class ConfigFileReaderTests {

	@Test
	void readScaffoldConf1() {
		ConfigFileReader cfr = new ConfigFileReader("tests/config/scaffold1.grimm");
		cfr.read();
		assertEquals(2, cfr.getClassInstances().size());
		
		int vertex = cfr.getClassInstances().get("Vertex");
		assertEquals(50, vertex);
		
		String attrWeightDom = cfr.getAttributesDomains().get("Edge/weight").toString();
		assertEquals("[l, 13, 45, 2, 3, 4, 5]", attrWeightDom);
		
		assertEquals(3, cfr.getReferencesUB());
	}
	
	@Test
	void readScaffoldConf2() {
		ConfigFileReader cfr = new ConfigFileReader("tests/config/scaffold2-invalid.grimm");
		cfr.read();
		
		int vertex = cfr.getClassInstances().get("Vertex");
		assertEquals(0, vertex);
		
		int edges = cfr.getClassInstances().get("Edge");
		assertEquals(0, edges);
		
		assertEquals(1, cfr.getAttributesDomains().size());
		
		String attrWeightDom = cfr.getAttributesDomains().get("Edge/weight").toString();
		assertEquals("[i, 13, 45]", attrWeightDom);
	}
	
	@Test
	void readScaffoldConf3() {
		ConfigFileReader cfr = new ConfigFileReader("tests/config/scaffold3.grimm");
		cfr.read();
		
		int vertex = cfr.getClassInstances().get("Vertex");
		assertEquals(75, vertex);
		
		int edges = cfr.getClassInstances().get("Edge");
		assertEquals(120, edges);
		
		boolean nameDoesnotExisit = cfr.getAttributesDomains().containsKey("Edge/name");
		assertEquals(false, nameDoesnotExisit);
		
		boolean weightIncluded = cfr.getAttributesDomains().containsKey("Edge/weight");
		assertEquals(true, weightIncluded);
		
		String attrWeightDom = cfr.getAttributesDomains().get("Edge/weight").toString();
		assertEquals("[l, 13, 45, 46, 100]", attrWeightDom);
	}

}
