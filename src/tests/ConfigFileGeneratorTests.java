package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Ecore2CSP.ConfigFileGenerator;
import Ecore2CSP.ConfigFileReader;
import exceptions.MetaModelNotFoundException;

class ConfigFileGeneratorTests {

	@Test
	void generateEcoreConfig() throws MetaModelNotFoundException {
		ConfigFileGenerator cfg= new ConfigFileGenerator("ecore1.grimm","tests/Ecore.ecore", "EPackage");
		cfg.createConfigFile();
	}
}
