package org.grimm.test;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.grimm.Ecore2CSP.ParametersFile;
import org.grimm.exception.ConfigurationFileNotFoundException;
import org.grimm.exception.IncorrectOutputFormatException;
import org.grimm.exception.MetaModelNotFoundException;
import org.grimm.exception.MissingInputValueException;
import org.grimm.exception.MissingMetaModelElementException;
import org.grimm.exception.OCLFileNotFoundException;
import org.grimm.exception.ParameterFileDoesNotFileException;
import org.grimm.exception.PositiveIntegerInputException;
import org.grimm.exception.UnknownCSPSolverException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestGrimmParametersFile {

	@Test
	public void createAFirstFile() {
		ParametersFile params= new ParametersFile("tests/params/created.params");
		params.createNewFile();
	}

	@Test
	public void readNonExisitingFile() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(ParameterFileDoesNotFileException.class, () -> {
			ParametersFile params= new ParametersFile("tests/params/false.params");
			params.readParamFile();
		});
	}

	@Test
	public void metamodelExist() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException{

		Assertions.assertThrows(MissingInputValueException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/existing-metamodel.params");
			params.readParamFile();
			assertEquals("tests/test.ecore", params.getMetamodel());
		});
	}

	@Test
	public void metamodelDoesNotExist() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException{
		Assertions.assertThrows(MetaModelNotFoundException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/inexisting-metamodel.params");
			params.readParamFile();	
		});
	}

	@Test
	public void OCLFileExists() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(MissingInputValueException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/existing-ocl.params");
			params.readParamFile();
			assertEquals("tests/maps.ocl", params.getOclFile());
		});
	}

	@Test
	public void OCLFileDoesNotExist() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(OCLFileNotFoundException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/inexisting-ocl.params");
			params.readParamFile();
		});
	}

	@Test
	public void ConfFileExists() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(MissingInputValueException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/existingConfFile.params");
			params.readParamFile();
			assertEquals("tests/Project.grimm", params.getConfFile());
			assertEquals("config", params.getInputMode());
		});
	}

	@Test
	public void ConfFileDoesNotExists() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(ConfigurationFileNotFoundException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/inexistingConfFile.params");
			params.readParamFile();		
		});
	}

	@Test
	public void stringInputForClassBound() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(PositiveIntegerInputException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/stringInputForBounds.params");
			params.readParamFile();
		});
	}

	@Test
	public void missingMetamodel() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(MissingInputValueException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/existingConfFile.params");
			params.readParamFile();
		});
	}

	@Test
	public void inexistingOutputFormat() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(IncorrectOutputFormatException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/inexisting-format.params");
			params.readParamFile();
		});
	}

	@Test
	public void inexistingSolver() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		Assertions.assertThrows(UnknownCSPSolverException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/inexisting-solver.params");
			params.readParamFile();
		});
	}

	@Test
	public void inexistingRootClass() throws Exception {
		Assertions.assertThrows(MissingMetaModelElementException.class, () -> {

			ParametersFile params= new ParametersFile("tests/params/ecore1.params");
			params.readParamFile();
		});
	}

	@Test
	public void FileIsComplete() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		ParametersFile params= new ParametersFile("tests/params/info-complete.params");
		params.readParamFile();
		//System.out.println(params.toString());
		assertEquals(true, params.parameterFileIsComplete());
	}

	@Test
	public void FileIsCompleteQuick() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException, MissingMetaModelElementException {
		ParametersFile params= new ParametersFile("tests/params/info-completeQuick.params");
		params.readParamFile();
		//System.out.println(params.toString());
		assertEquals(true, params.parameterFileIsComplete());
	}
}
