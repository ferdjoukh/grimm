package org.grimm.CSP2Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.ocl.ParserException;
import org.grimm.Ecore.MetaModelReader;
import org.grimm.Ecore2CSP.ConfigFileReader;
import org.grimm.Ecore2CSP.XCSPgenerator;
import org.grimm.utils.ClassInstance;
import org.grimm.utils.Utils;
import org.grimm.utils.ocl.OclConstraints;

public abstract class ModelBuilder {

	protected MetaModelReader reader;
	protected String metaModelFile;
	protected String root;
	protected ConfigFileReader configfilereader;
	protected String CSPInstanceFile;
	protected String modelFilePath;
	protected int referenceUpperBound;
	protected ArrayList<Integer> classSizes;
	protected ArrayList<Integer> classMinSizes;
	protected String oclFilePath;
	protected int maxDomains;
	protected ArrayList<FoundSolution> foundSolutions= new ArrayList<FoundSolution>();
	protected ArrayList<ClassInstance> allCreatedEObjects= new ArrayList<ClassInstance>();
	
	/***
	 * 
	 * @param ModelFile: .ECORE meta-model File
	 * @param root: Root Class of this meta-model
	 * @param CSPInstanceFile: .XML file path of the produced CSP instance
	 */
	public ModelBuilder(String metaModelFile, String root,String CSPInstanceFile, String oclFilePath){
		
		this.metaModelFile = metaModelFile;
		this.root = root;
		this.CSPInstanceFile = CSPInstanceFile;
		this.oclFilePath = oclFilePath;
		
		DateFormat df = new SimpleDateFormat("-kkmmss-ddMMyy");
		Date date = new Date();
		String dateF = df.format(date.getTime());
		this.modelFilePath= root+"/"+root+dateF;
	}
	
	public ModelBuilder(String metaModelFile, String root, String CSPInstanceFile, String oclFilePath, String modelFile){
		
		this.metaModelFile = metaModelFile;
		this.root = root;
		this.CSPInstanceFile = CSPInstanceFile;
		this.oclFilePath = oclFilePath;
		
		DateFormat df = new SimpleDateFormat("-kkmmss-ddMMyy");
		Date date = new Date();
		String dateF = df.format(date.getTime());
		this.modelFilePath = modelFile;
	}
	
	public ArrayList<FoundSolution> getFoundSolutions() {
		return foundSolutions;
	}

	public void initMetamodelReader(int lb, int ub, int rb) {
		this.reader= new MetaModelReader(metaModelFile, root,lb,ub);
		this.referenceUpperBound=rb;
		this.classSizes=reader.getClassSize();
		this.classMinSizes=reader.getClassSizeMin();
		this.maxDomains = computeMaxDomain();
	}
	
	public void initMetaModelReader(String configFilePath) {
		ConfigFileReader cfr= new ConfigFileReader(configFilePath);
		cfr.read();
		this.configfilereader = cfr;
		this.reader= new MetaModelReader(metaModelFile, root, cfr);
		this.classSizes= reader.getClassSize();
		this.classMinSizes= reader.getClassSizeMin();
		this.referenceUpperBound= cfr.getReferencesUB();
		this.maxDomains = computeMaxDomain();
	}
	
	/***
	 * 
	 * @param lb: Class instance number lower bound
	 * @param ub: Class instance number upper bound
	 * @param rb: Unbounded References bound (*references)
	 * @param sym: To break or not to break symmetries {0,1} 
	 * @param sol: Solutions number ?
	 * @throws IOException
	 */
	public void CallCSPGenrator(int lb, int ub, int rb, int sym, int sol) throws IOException
	{
		initMetamodelReader(lb, ub, rb);
		
		long timeBegin; double timeCounter;
		timeBegin=System.nanoTime();
			
		////////////////////////////////////////////////////////////////
		// Generate CSP instance
		///////////////////////////////////////////////////////////////
		System.out.println("CSP instance generator is running");
		XCSPgenerator CSPgenerator = new XCSPgenerator(reader, rb, sym);
		CSPgenerator.generateXCSP(CSPInstanceFile,1);
				
		timeCounter=(System.nanoTime()-timeBegin)/1000000;
		System.out.println("\t[OK] CSP istance generation time = "+ timeCounter+ " (ms)");
				
		/////////////////////////////////////////////////////////////
		// OCL constraints parsing
		////////////////////////////////////////////////////////////
		if (!oclFilePath.equals("")) {
			try {
				timeBegin = System.nanoTime();
				System.out.println("OCL parser is running");
			
				OclConstraints oclCons = new OclConstraints(reader, oclFilePath, CSPgenerator.getXCSPinstance());
				Utils.saveXML(oclCons.getResultDocumentXCSP(), CSPInstanceFile);
				
				timeCounter = (System.nanoTime()-timeBegin)/1000000;
				System.out.println("\t[OK] OCL constraints parsing duration = "+ timeCounter+ " ms");
			} catch (FileNotFoundException | ParserException e) {
				System.err.println("\t[PROBLEM] OCL constraints were not parsed :(");				
			}
		}
		
		///////////////////////////////////////////////////////////////
		// Execute CSP solver
		////////////////////////////////////////////////////////////
		System.out.println("CSP Solver is running");
		BufferedReader bufferedreader=executeAbsconSolver(CSPInstanceFile, sol);
		findAllSolutions(bufferedreader);
	}
	
	/***
	 * 
	 * @param configFilePath: Input a Configuration file 
	 * @param sym: To break or not to break symmetries {0,1} 
	 * @param sol: Solutions number ?
	 * @return 
	 * @throws IOException
	 */
	public void CallCSPGenrator(String configFilePath, int sym, int sol) throws IOException
	{
		////////////////////////////////////////////////////////
		// Init and read the configuration file
		/////////////////////////////////////////////////////////
		initMetaModelReader(configFilePath);
		
		long debut; double duree;
		debut=System.nanoTime();
		
		////////////////////////////////////////////////////////
		// Generate CSP instance
		/////////////////////////////////////////////////////////
		System.out.println("CSP instance generator is running");
	    XCSPgenerator CSPgenerator = new XCSPgenerator(reader, this.configfilereader, sym);
		CSPgenerator.generateXCSP(CSPInstanceFile,1);
				
		duree=(System.nanoTime()-debut)/1000000;
		System.out.println("\t[OK] CSP instance generation time = "+ duree+ " ms");
				
		
		/////////////////////////////////////////////////////////////
		// OCL constraints parsing
		////////////////////////////////////////////////////////////
		if (!oclFilePath.equals("")) {
			try {
				debut = System.nanoTime();
				System.out.println("OCL parser is running");	
				OclConstraints oclCons = new OclConstraints(reader, oclFilePath, CSPgenerator.getXCSPinstance());
				Utils.saveXML(oclCons.getResultDocumentXCSP(), CSPInstanceFile);
				duree = (System.nanoTime()-debut)/1000000;
				System.out.println("\t[OK] OCL constraints treatment duration= "+ duree+ " ms");
			} catch (FileNotFoundException | ParserException e) {
				System.err.println("\tProblems when parsing OCL file !");
			}
		}
		
		///////////////////////////////////////////////////////////////
		// Execute CSP solver
		////////////////////////////////////////////////////////////
		System.out.println("CSP Solver is running");
		BufferedReader bufferedreader;
		bufferedreader=executeAbsconSolver(CSPInstanceFile,sol);
		findAllSolutions(bufferedreader);
	}
		
	public int computeMaxDomain() {
		int max=0;
		for(Integer i: classSizes) {
			max= max + i;
		}
		return max;
	}
	
	public BufferedReader executeAbsconSolver(String Instancefile, int sol){
			
		String cmd = "java -jar abssol.jar " + Instancefile +" -s="+ sol;

		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			return reader;
		} catch (IOException e) {
			System.err.println("\tProblem when excecuting the CSP solver !");
		}
        return null;
	}
	
	/**
	 * This method reads the output of execution of Abscon solver 
	 * and finds all the returned solutions
	 * 
	 * @param reader
	 */
	public void findAllSolutions(BufferedReader reader){
		String line;
		String du = "";
		int found=0;
		
		try {
			while((line = reader.readLine()) != null) {
				if(line.startsWith("s SATISFIABLE")) {	        
			    	found=1;
			    }
			    else if(line.startsWith("   totalWckTime"))
			    {
			    	int kk=line.indexOf("CpuTime=");
			    	du="= "+line.subSequence(kk+8, line.length())+" CPUtime";
			    
			    }else if(line.startsWith("    solution  #")) {
			    	String solutionLine= line.substring(line.indexOf("#")+4);
			    	FoundSolution foundsolution= new FoundSolution(solutionLine);
			    	foundSolutions.add(foundsolution);
			    }
			}
			if(found==0) {
				System.out.println("\t[PROBLEM] CSP instance is unsatisfiable :(");
			}
			else{
				System.out.println("\t[OK] resolutuon time " +du);
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());	
		}
	}
	
	/***
	 * This method generates a text file .chr that contains the 
	 *  chromosome of each generated graph
	 * 
	 * @param values: the ArrayList<Integer> that was given by the solver
	 * @param outputFileName: the name of generated .chr file
	 * @throws IOException
	 */
	protected void CSP2CHR(ArrayList<Integer> values, String outputFileName) throws IOException{
		
		///////////////////////////////////////
		// Generate safe CSPs
		///////////////////////////////////////
		SafeCSPGenerator(outputFileName);
				
		///////////////////////////////////////
		// Move the xcsp file and config file
		///////////////////////////////////////
		String configFilePath = reader.getConfigFileReader().getConfigFilePath();
		
		String moveXMLcmd = "cp "+ CSPInstanceFile+ " " +outputFileName+".xml";
		String moveGrimmcmd = "cp "+ configFilePath +" "+ outputFileName+".grimm";
		
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(moveXMLcmd);
		    p = Runtime.getRuntime().exec(moveGrimmcmd);
		}
		catch(Exception e){
			
			System.out.println("\t[PROBLEM] moving xcsp and config files");
		}

		PrintWriter printwriter =  new PrintWriter(new BufferedWriter(new FileWriter(outputFileName +".chr")));
		
		String chromosome= ArrayList2CHR(values);
		printwriter.write(chromosome+"\n");
		printwriter.write(outputFileName +".xml\n");
		printwriter.write(outputFileName +"-safe.xml\n");
		printwriter.write(outputFileName +".grimm\n");
		printwriter.write(reader.getMetamodel()+"\n");
		printwriter.write(root+"\n");
		printwriter.close();
		
		System.out.println("\t[OK] chromosome generated >> "+outputFileName +".chr");
	}
	
	/**
	 * This method generates a safe CSP when chr is generated
	 * 
	 * @param fileName
	 */
	public void SafeCSPGenerator(String fileName){
		
		System.out.println("Safe CSP instance generation ...");
		XCSPgenerator CSPgenerator = new XCSPgenerator(reader, this.configfilereader, 0);
		CSPgenerator.generateXCSP(fileName+"-safe.xml",0);
		
		System.out.println("\t[OK] safe csp generated >> "+ fileName +"-safe.xml");
	}
	
	/**
	 * This method transforms an ArrayList of integer into a chromosome
	 * 
	 * @param values
	 * @return
	 */
	protected String ArrayList2CHR(ArrayList<Integer> values) {
		String res= "";
		
		for (Integer i: values) {
			res= res+ i +" ";
		}
		
		return res;	
	}
	
	/**
	 * This method generate models using a detailed configuration file
	 * 
	 * @param string
	 * @param sym
	 * @param numberOfSolutions
	 * @throws IOException
	 * @throws Exception 
	 */
	public void generateModel(String string, int sym, int numberOfSolutions, boolean chr) throws IOException, Exception {}
	
	/**
	 * This method is called for generating model using the quick launch mode
	 * 
	 * @param lb
	 * @param ub
	 * @param rb
	 * @param sym
	 * @param numberOfSolutions
	 * @throws IOException
	 */
	public void generateModel(int lb,int ub,int rb,int sym, int numberOfSolutions) throws IOException {}
	
	/**
	 * This method generates all the models that were found by the solver
	 */
	public void Solutions2Models() {}
}
