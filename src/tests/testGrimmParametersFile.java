package tests;

import static org.junit.Assert.*;
import org.junit.Test;

import Ecore2CSP.ParametersFile;
import exceptions.MetaModelNotFoundException;
import exceptions.OCLFileNotFoundException;

public class testGrimmParametersFile {
	
	@Test
	public void createAFirstFile() {
		ParametersFile params= new ParametersFile("tests/params/created.params");
		params.createNewFile();
	}
	
	@Test
	public void metamodelExist() throws MetaModelNotFoundException {
		ParametersFile params= new ParametersFile("tests/params/existing-metamodel.params");
		params.readParamFile();
		assertEquals("tests/test.ecore", params.getMetamodel());
	}
	
	@Test(expected = MetaModelNotFoundException.class) 
	public void metamodelDoesNotExist() throws MetaModelNotFoundException{
		ParametersFile params= new ParametersFile("tests/params/inexisting-metamodel.params");
		params.readParamFile();
	}
	
	@Test
	public void OCLFileExists() {
		ParametersFile params= new ParametersFile("tests/params/existing-ocl.params");
		params.readParamFile();
		assertEquals("tests/maps.ocl", params.getOclFile());
	}
	
	@Test(expected = OCLFileNotFoundException.class)
	public void OCLFileDoesNotExist() {
		ParametersFile params= new ParametersFile("tests/params/inexisting-ocl.params");
		params.readParamFile();
	}

}
