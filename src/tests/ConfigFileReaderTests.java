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
		assertEquals(1, cfr.getAttributesDomainsRaw().size());
	}

}
