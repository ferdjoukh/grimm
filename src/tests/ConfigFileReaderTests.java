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
		
		assertEquals(1, cfr.getAttributesDomainsRaw().size());
	}
	
	@Test
	void readScaffoldConf2() {
		ConfigFileReader cfr = new ConfigFileReader("tests/config/scaffold2.grimm");
		cfr.read();
		
		int vertex = cfr.getClassInstances().get("Vertex");
		assertEquals(0, vertex);
		
		int edges = cfr.getClassInstances().get("Edge");
		assertEquals(0, edges);
		
		String attrNameDom = cfr.getAttributesDomains().get("Edge/name").toString();
		assertEquals("[l, izan, ibecan, aka, kan]", attrNameDom);
		
		String attrWeightDom = cfr.getAttributesDomains().get("Edge/weight").toString();
		assertEquals("[i, 1, 100]", attrWeightDom);
	}

}
