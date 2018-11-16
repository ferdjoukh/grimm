package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Ecore2CSP.ConfigFileGenerator;
import Ecore2CSP.ConfigFileReader;
import exceptions.MetaModelNotFoundException;

class ConfigFileGeneratorTests {

	@Test
	void generateEcoreConfig() throws MetaModelNotFoundException {
		ConfigFileGenerator cfg= new ConfigFileGenerator("test-config-ecore1.grimm","tests/Ecore.ecore", "EPackage");
		cfg.createConfigFile();

		ConfigFileGenerator cfg2= new ConfigFileGenerator("test-config-maps1.grimm","tests/maps.ecore", "map");
		cfg2.createConfigFile();
		
		ConfigFileGenerator cfg3= new ConfigFileGenerator("test-config-scaffold1.grimm","tests/ScaffoldGraph.ecore", "Graph");
		cfg3.createConfigFile();
	}

}
