package tests;

import static org.junit.Assert.*;
import org.junit.Test;

import Ecore2CSP.ParametersFile;
import exceptions.ConfigurationFileNotFoundException;
import exceptions.IncorrectOutputFormatException;
import exceptions.PositiveIntegerInputException;
import exceptions.UnknownCSPSolverException;
import exceptions.MetaModelNotFoundException;
import exceptions.MissingInputValueException;
import exceptions.OCLFileNotFoundException;
import exceptions.ParameterFileDoesNotFileException;

public class testGrimmParametersFile {
	
	@Test
	public void createAFirstFile() {
		ParametersFile params= new ParametersFile("tests/params/created.params");
		params.createNewFile();
	}
	
	@Test(expected = ParameterFileDoesNotFileException.class)
	public void readNonExisitingFile() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/false.params");
		params.readParamFile();
	}
	
	@Test(expected = MissingInputValueException.class)
	public void metamodelExist() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException{
		ParametersFile params= new ParametersFile("tests/params/existing-metamodel.params");
		params.readParamFile();
		assertEquals("tests/test.ecore", params.getMetamodel());
	}
	
	@Test(expected = MetaModelNotFoundException.class)
	public void metamodelDoesNotExist() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException{
		ParametersFile params= new ParametersFile("tests/params/inexisting-metamodel.params");
		params.readParamFile();	
	}
	
	@Test(expected = MissingInputValueException.class)
	public void OCLFileExists() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/existing-ocl.params");
		params.readParamFile();
		assertEquals("tests/maps.ocl", params.getOclFile());
	}
	
	@Test(expected = OCLFileNotFoundException.class)
	public void OCLFileDoesNotExist() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/inexisting-ocl.params");
		params.readParamFile();	
	}
	
	@Test(expected = MissingInputValueException.class)
	public void ConfFileExists() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/existingConfFile.params");
		params.readParamFile();
		assertEquals("tests/Project.grimm", params.getConfFile());
		assertEquals("config", params.getInputMode());
	}
	
	@Test(expected = ConfigurationFileNotFoundException.class)
	public void ConfFileDoesNotExists() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/inexistingConfFile.params");
		params.readParamFile();		
	}
	
	@Test(expected = PositiveIntegerInputException.class)
	public void stringInputForClassBound() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/stringInputForBounds.params");
		params.readParamFile();
	}
	
	@Test(expected = MissingInputValueException.class)
	public void missingMetamodel() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/existingConfFile.params");
		params.readParamFile();
	}
	
	@Test(expected = IncorrectOutputFormatException.class)
	public void inexistingOutputFormat() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/inexisting-format.params");
		params.readParamFile();
	}
	
	@Test(expected = UnknownCSPSolverException.class)
	public void inexistingSolver() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/inexisting-solver.params");
		params.readParamFile();
	}
	
	@Test
	public void FileIsComplete() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/info-complete.params");
		params.readParamFile();
		System.out.println(params.toString());
		assertEquals(true, params.parameterFileIsComplete());
	}
	
	@Test
	public void FileIsCompleteQuick() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException {
		ParametersFile params= new ParametersFile("tests/params/info-completeQuick.params");
		params.readParamFile();
		System.out.println(params.toString());
		assertEquals(true, params.parameterFileIsComplete());
	}
}
