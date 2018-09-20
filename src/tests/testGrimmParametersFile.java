package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import Ecore2CSP.ParametersFile;

public class testGrimmParametersFile {

	@Test
	public void createAFirstFile() {
		ParametersFile params= new ParametersFile("tests/testMM.params");
		params.createNewFile();
	}
	
	@Test
	public void readAFirstFile() {
		ParametersFile params= new ParametersFile("tests/testMM.params");
		params.readParamFile();
	}

}
