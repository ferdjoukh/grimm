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
import exceptions.InputValueIsNotAnIntegerException;
import exceptions.MetaModelNotFoundException;
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
	private String oclFile;
	private String confFile;
	private int classLowerBound=2;
	private int classUpperBound=3;
	private int referenceUpperBound=2;
	private String inputMode="quick";
	private int numberOfSolutions=1;
	private String outputFormat="xmi";
	private String CSPSolver="abscon";
	
	///////////////////////////////////////////
	// 
	// Getters and Setters
	//
	///////////////////////////////////////////
	public ParametersFile(String filePath) {
		this.filePath= filePath;
	}
		
	public String getMetamodel() {
		return metamodel;
	}
	
	public void setMetamodel(String mm) throws MetaModelNotFoundException {
		try {
			if(metamodelExists(mm)) {
				this.metamodel=mm;
			}
		} catch (MetaModelNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public String getRootClass() {
		return rootClass;
	}

	public void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}

	public String getOclFile() {
		return oclFile;
	}

	public void setOclFile(String oclFile) throws OCLFileNotFoundException {
		try {
			if(oclFileExists(oclFile)) {
				this.oclFile=oclFile;
			}
		} catch (OCLFileNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public String getConfFile() {
		return confFile;
	}

	public void setConfFile(String confFile) throws ConfigurationFileNotFoundException {
		try {
			if(confFileExists(confFile)) {
				this.confFile = confFile;
				this.inputMode= "config";
			}
		}catch(ConfigurationFileNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		}
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
	
	public void setClassLowerBound(String value) throws InputValueIsNotAnIntegerException {
		try {
			this.classLowerBound = isIntegerValue(value, "ClassLowerBound");
			this.inputMode= "quick";
		}catch(InputValueIsNotAnIntegerException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	public void setClassUpperBound(String value) throws InputValueIsNotAnIntegerException {
		try {
			this.classUpperBound = isIntegerValue(value, "ClassUpperBound");
			this.inputMode= "quick";
		}catch(InputValueIsNotAnIntegerException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public void setReferenceUpperBound(String value) throws InputValueIsNotAnIntegerException {
		try {
			this.referenceUpperBound = isIntegerValue(value, "referenceUpperBound");
			this.inputMode= "quick";
		}catch(InputValueIsNotAnIntegerException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	public String getInputMode() {
		return inputMode;
	}

	public void setInputMode(String inputMode) {
		this.inputMode = inputMode;
	}

	public int getNumberOfSolutions() {
		return numberOfSolutions;
	}

	public void setNumberOfSolutions(int numberOfSolutions) {
		this.numberOfSolutions = numberOfSolutions;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getCSPSolver() {
		return CSPSolver;
	}

	public void setCSPSolver(String cSPSolver) {
		CSPSolver = cSPSolver;
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
	 * @throws InputValueIsNotAnIntegerException 
	 * @throws ParameterFileDoesNotFileException 
	 */
	public void readParamFile() throws MetaModelNotFoundException, OCLFileNotFoundException, ConfigurationFileNotFoundException, InputValueIsNotAnIntegerException, ParameterFileDoesNotFileException{
		
		try {
			File file;
			file= parameterFileExist();
			
			InputStream in= new FileInputStream(new File(filePath));
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
					if(line.startsWith("lowerBound for classes")) {
						String value=line.substring(line.lastIndexOf("=")+1);
						setClassLowerBound(value);
					}
				}
			}
			
			br.close();
			
		}catch(ParameterFileDoesNotFileException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
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
	
	public int isIntegerValue(String value, String forwhat) throws InputValueIsNotAnIntegerException{
		try {
			int res= Integer.parseInt(value);
			return res;
		}catch(Exception e) {
			throw new InputValueIsNotAnIntegerException(value, forwhat);
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
}
