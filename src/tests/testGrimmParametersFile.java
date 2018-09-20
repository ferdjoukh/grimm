package tests;

import static org.junit.Assert.*;
import org.junit.Test;

import Ecore2CSP.ParametersFile;
import exceptions.MetaModelNotFoundException;

public class testGrimmParametersFile {

	
	@Test
	public void createAFirstFile() {
		ParametersFile params= new ParametersFile("tests/params/created.params");
		params.createNewFile();
	}
	
	@Test
	public void whichmetamodel() throws MetaModelNotFoundException {
		ParametersFile params= new ParametersFile("tests/params/existing-metamodel.params");
		params.readParamFile();
		assertEquals("tests/test.ecore", params.getMetamodel());
	}
	
	@Test(expected = MetaModelNotFoundException.class) 
	public void metamodelDoesNotExist() throws MetaModelNotFoundException{
		ParametersFile params= new ParametersFile("tests/params/inexisting-metamodel.params");
		params.readParamFile();
	}

}
