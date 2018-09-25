package Ecore2CSP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.sun.corba.se.spi.orbutil.fsm.InputImpl;

import exceptions.ConfigurationFileNotFoundException;
import exceptions.IncorrectOutputFormatException;
import exceptions.PositiveIntegerInputException;
import exceptions.UnknownCSPSolverException;
import exceptions.MetaModelNotFoundException;
import exceptions.MissingInputValueException;
import exceptions.OCLFileNotFoundException;
import exceptions.ParameterFileDoesNotFileException;

/**
* @author: Adel Ferdjoukh
* @email: ferdjoukh@gmail.com
*
*/

public class ParametersFile {
	private String filePath;
	private String metamodel;
	private String rootClass;
	private String oclFile="";
	private String confFile;
	private int classLowerBound=2;
	private int classUpperBound=3;
	private int referenceUpperBound=2;
	private String inputMode="quick";
	private int numberOfSolutions=1;
	private String outputFormat="xmi";
	private String CSPSolver="abscon";
	
	
	public ParametersFile(String filePath) {
		this.filePath= filePath;
	}
	
	
	///////////////////////////////////////////
	// 
	// Setters
	//
	//////////////////////////////////////////
	private void setMetamodel(String mm) throws MetaModelNotFoundException {
		try {
			if(metamodelExists(mm)) {
				this.metamodel=mm;
			}
		} catch (MetaModelNotFoundException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	private void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}
	
	private void setOclFile(String oclFile) throws OCLFileNotFoundException {
		try {
			if(oclFileExists(oclFile)) {
				this.oclFile=oclFile;
			}
		} catch (OCLFileNotFoundException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	private void setConfFile(String confFile) throws ConfigurationFileNotFoundException {
		try {
			if(confFileExists(confFile)) {
				this.confFile = confFile;
				this.inputMode= "config";
			}
		}catch(ConfigurationFileNotFoundException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	private void setClassLowerBound(String value) throws PositiveIntegerInputException {
		try {
			this.classLowerBound = isIntegerValue(value, "ClassLowerBound");
			this.inputMode= "quick";
		}catch(PositiveIntegerInputException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}

	private void setClassUpperBound(String value) throws PositiveIntegerInputException {
		try {
			this.classUpperBound = isIntegerValue(value, "ClassUpperBound");
			this.inputMode= "quick";
		}catch(PositiveIntegerInputException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	private void setReferenceUpperBound(String value) throws PositiveIntegerInputException {
		try {
			this.referenceUpperBound = isIntegerValue(value, "referenceUpperBound");
			this.inputMode= "quick";
		}catch(PositiveIntegerInputException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	private void setNumberOfSolutions(String value) throws PositiveIntegerInputException {
		try {
			this.numberOfSolutions = isIntegerValue(value, "numberOfSolutions");

		}catch(PositiveIntegerInputException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}

	private void setOutputFormat(String value) throws IncorrectOutputFormatException {
		try {
			if(formatIsCorrect(value))
				this.outputFormat = value;
			
		}catch(IncorrectOutputFormatException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}

	private void setCSPSolver(String value) throws UnknownCSPSolverException {
		try {
			if(CSPSolverIsCorrect(value))
				this.CSPSolver = value;
		}catch(UnknownCSPSolverException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	/////////////////////////////////////////////
	//
	// getters
	//
	/////////////////////////////////////////////
	
	public String getMetamodel() {
		return metamodel;
	}
	
	public String getRootClass() {
		return rootClass;
	}
	
	public String getOclFile() {
		return oclFile;
	}
	
	public String getConfFile() {
		return confFile;
	}
	
	/**
	 * 
	 * @return the input mode of a generation process: quick or config
	 */
	public String getInputMode() {
		return inputMode;
	}

	public int getNumberOfSolutions() {
		return numberOfSolutions;
	}
	
	/**
	 * 
	 * @return The format for generated models: xmi or dot
	 */
	public String getOutputFormat() {
		return outputFormat;
	}
	
	public String getCSPSolver() {
		return CSPSolver;
	}
	
	public int getClassLowerBound() {
		return classLowerBound;
	}
	
	public int getClassUpperBound() {
		return classUpperBound;
	}

	public int getReferenceUpperBound() {
		return referenceUpperBound;
	}
	

	/**
	 * 
	 * This method creates an empty parameters file to fill
	 *  
	 */
	public void createNewFile(){
		
		File file= new File(filePath);
		
		try {
			file.createNewFile();
			
			PrintWriter pw= new PrintWriter(file);
			pw.write("# This file contains all the generation parameters of GRIMM tool\n");
			pw.write("#\n");
			pw.write("# Fill the file with your own information\n");
			pw.write("#   + are mondatory\n");
			pw.write("#   - must be filled or removed\n");
			pw.write("#   (1) and (2) block must not appear at the same time\n");
			pw.write("#\n");
			
			pw.write("+meta-model =tests/test.ecore\n");
			pw.write("+rootClass =map\n");
			pw.write("-ocl file =tests/maps.ocl\n");
			
			pw.write("#(1)\n");
			pw.write("lowerBound for classes =2\n");
			pw.write("upperBound for classes =4\n");
			pw.write("upperBound for references =2\n");
			
			pw.write("#(2)\n");
			pw.write("configuration file =tests/test.grimm\n");
			
			pw.write("#\n");
			pw.write("#\n");
			pw.write("number of solutions =1\n");
			pw.write("#\n");
			pw.write("output format =xmi or dot\n");
			pw.write("CSP solver =abscon\n");
			
			pw.close();
						
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * This method reads an existing parameters file
	 * @throws MetaModelNotFoundException 
	 * @throws OCLFileNotFoundException 
	 * @throws ConfigurationFileNotFoundException 
	 * @throws PositiveIntegerInputException 
	 * @throws ParameterFileDoesNotFileException 
	 * @throws MissingInputValueException 
	 * @throws IncorrectOutputFormatException 
	 * @throws UnknownCSPSolverException 
	 */
	public void readParamFile() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, PositiveIntegerInputException, ParameterFileDoesNotFileException, MissingInputValueException, IncorrectOutputFormatException, UnknownCSPSolverException{
		
		try {
			
			File file= parameterFileExist();
			
			InputStream in= new FileInputStream(file);
			InputStreamReader isr= new InputStreamReader(in);
			BufferedReader br= new BufferedReader(isr);
			
			String line;
			while( (line=br.readLine())!=null  ) {
				if(!line.startsWith("#")) {
					
					//Meta-model
					if(line.startsWith("+meta-model")) {
						String mm= line.substring(line.lastIndexOf("=")+1);
						setMetamodel(mm);
					}
					
					//RootClass
					if(line.startsWith("+rootClass")) {
						String root= line.substring(line.lastIndexOf("=")+1);
						setRootClass(root);
					}
					
					//OCL file
					if(line.startsWith("-ocl file")) {
						String ocl= line.substring(line.lastIndexOf("=")+1);
						setOclFile(ocl);
					}
					
					//Configuration file
					if(line.startsWith("configuration file")) {
						String conf=line.substring(line.lastIndexOf("=")+1);
						setConfFile(conf);
					}
					
					//lower, upper bounds
					//
					//
					if(line.startsWith("lowerBound for classes")) {
						String value=line.substring(line.lastIndexOf("=")+1);
						setClassLowerBound(value);
					}
					
					if(line.startsWith("upperBound for classes")) {
						String value=line.substring(line.lastIndexOf("=")+1);
						setClassUpperBound(value);
					}
					
					if(line.startsWith("upperBound for references")) {
						String value=line.substring(line.lastIndexOf("=")+1);
						setReferenceUpperBound(value);
					}
					
					//number of solutions
					if(line.startsWith("number of solutions")) {
						String value=line.substring(line.lastIndexOf("=")+1);
						setNumberOfSolutions(value);
					}
					
					//output format
					if(line.startsWith("output format")) {
						String value=line.substring(line.lastIndexOf("=")+1);
						setOutputFormat(value);
					}
					
					//solver
					if(line.startsWith("CSP solver")) {
						String value=line.substring(line.lastIndexOf("=")+1);
						setCSPSolver(value);
					}
					
					
				}
			}
			
			br.close();
			
			//Check is there is any missing information
			try {
				parameterFileIsComplete();
			}
			catch(MissingInputValueException e) {
				System.out.println(e.getMessage());
				throw e;
			}
			
		}catch(ParameterFileDoesNotFileException e) {
			throw e;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	////////////////////////////////////////////
	//
	// Missing value
	//
	////////////////////////////////////////////
	public boolean parameterFileIsComplete() throws MissingInputValueException {
		if(this.metamodel == null) {
			throw new MissingInputValueException("meta-model");
		}
		
		if(this.rootClass == null) {
			throw new MissingInputValueException("root Class");
		}
		
		return true;
	}
	
	////////////////////////////////////////////
	//
	// Existence of files and throw exceptions
	//
	////////////////////////////////////////////
	public boolean metamodelExists(String metamodel) throws MetaModelNotFoundException {
		File mm= new File(metamodel);
		
		if(mm.exists()) {
			return true;
		}else {
			throw new MetaModelNotFoundException(metamodel);
		}
	}
	
	public boolean oclFileExists(String oclFile) throws OCLFileNotFoundException{
		File ocl= new File(oclFile);
		
		if(ocl.exists()) {
			return true;
		}else {
			throw new OCLFileNotFoundException(oclFile);
		}
	}
	
	public boolean confFileExists(String conf) throws ConfigurationFileNotFoundException{
		File config= new File(conf);
		
		if(config.exists()) {
			return true;
		}else {
			throw new ConfigurationFileNotFoundException(conf);
		}	
	}
	
	public int isIntegerValue(String value, String forwhat) throws PositiveIntegerInputException{
		try {
			int res= Integer.parseInt(value);
			if(res<0) {
				throw new PositiveIntegerInputException(value, forwhat);
			}
			return res;
		}catch(Exception e) {
			throw new PositiveIntegerInputException(value, forwhat);
		}
	}
	
	public File parameterFileExist() throws ParameterFileDoesNotFileException{
		File param= new File(this.filePath);
		
		if(param.exists()) {
			return param;
		}else {
			throw new ParameterFileDoesNotFileException(this.filePath);
		}
	}
	
	public boolean formatIsCorrect(String value) throws IncorrectOutputFormatException {
		
		if(value.equals("xmi") || value.equals("dot"))
			return true;
		else
			throw new IncorrectOutputFormatException(value);
	}
	
	private boolean CSPSolverIsCorrect(String value) throws UnknownCSPSolverException {
		
		if(value.toLowerCase().equals("abscon")) {
			return true;
		}else {
			throw new UnknownCSPSolverException(value);
		}
			
		
	}
	
	/***********
	 * 
	 * 
	 * toString method
	 * 
	 */
	public String toString(){
		String result="These are the information collected from ParamatersFile: "+ filePath;
		result=result+"\n  metamodel: "+metamodel;
		result=result+"\n  rootClass: "+rootClass;
		
		if(this.oclFile!=null) {
			result=result+"\n  OCLFile: "+oclFile;
		}
		
		result=result+"\n  Input mode: "+inputMode;
		
		if(this.confFile!=null) {
			result=result+"\n  configFile: "+confFile;
		}else {
			result=result+"\n  classLowerBound: "+classLowerBound;
			result=result+"\n  classUpperBound: "+classUpperBound;
			result=result+"\n  referenceUpperBound: "+referenceUpperBound;
		}
		
		result=result+"\n  numberOfSolutions: "+numberOfSolutions;
		result=result+"\n  outputFormat: "+outputFormat;
		result=result+"\n  CSP solver: "+CSPSolver;
		
		return result;
		
	}
}
