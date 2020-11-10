package org.grimm.test;

import org.junit.jupiter.api.Test;

import org.grimm.Ecore2CSP.ConfigFileGenerator;
import org.grimm.exception.MetaModelNotFoundException;

class ConfigFileGeneratorTest {

	@Test
	void generateEcoreConfig() throws MetaModelNotFoundException {
		ConfigFileGenerator cfg= new ConfigFileGenerator("ecore1.grimm","tests/Ecore.ecore", "EPackage");
		cfg.createConfigFile();
	}
}
