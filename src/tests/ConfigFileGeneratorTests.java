package tests;

import org.junit.Test;
import Ecore2CSP.ConfigFileGenerator;
import exceptions.MetaModelNotFoundException;

class ConfigFileGeneratorTests {

	@Test
	void generateEcoreConfig() throws MetaModelNotFoundException {
		ConfigFileGenerator cfg= new ConfigFileGenerator("ecore1.grimm","tests/Ecore.ecore", "EPackage");
		cfg.createConfigFile();
	}
}
