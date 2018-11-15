package CSP2Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.ParserException;

import Ecore.MetaModelReader;
import Ecore2CSP.ConfigFileReader;
import Ecore2CSP.GenXCSP;
import Utils.ClassInstance;
import Utils.OCL.OclConstraints;

public abstract class ModelBuilder {

	protected MetaModelReader reader;
	protected String metaModelFile;
	protected String root;
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
		this.modelFilePath= root+new Date().getDate()+new Date().getHours()+new Date().getMinutes()+ new Date().getSeconds()+"UB"+referenceUpperBound;
	}
	
	public ArrayList<FoundSolution> getFoundSolutions() {
		return foundSolutions;
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
		this.reader= new MetaModelReader(metaModelFile, root,lb,ub);
		this.referenceUpperBound=rb;
		this.classSizes=reader.getClassSize();
		this.classMinSizes=reader.getClassSizeMin();
		
		long timeBegin; double timeCounter;
		timeBegin=System.nanoTime();
			
		////////////////////////////////////////////////////////////////
		// Generate CSP instance
		///////////////////////////////////////////////////////////////
		System.out.println("CSP instance generator is running");
		GenXCSP generation= new GenXCSP(metaModelFile,root,reader,ub,rb,sym);
		generation.GenerateXCSP(CSPInstanceFile);
		maxDomains=generation.getMaxDomains();
				
		timeCounter=(System.nanoTime()-timeBegin)/1000000;
		System.out.println("\t[OK] CSP istance generation time = "+ timeCounter+ " (ms)");
				
		/////////////////////////////////////////////////////////////
		// OCL constraints parsing
		////////////////////////////////////////////////////////////
		if (!oclFilePath.equals("")) {
			try {
				timeBegin = System.nanoTime();
				System.out.println("OCL parser is running");
			
				OclConstraints oclCons = new OclConstraints(reader, oclFilePath, GenXCSP.getXCSPinstance());
				generation.saveXML(oclCons.getResultDocumentXCSP(), CSPInstanceFile);
				
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
		ConfigFileReader cfr= new ConfigFileReader(configFilePath);
		cfr.read();
		this.reader= new MetaModelReader(metaModelFile, root, cfr);
		classSizes= reader.getClassSize();
		classMinSizes= reader.getClassSizeMin();
		this.referenceUpperBound= cfr.getRefsBound();
						
		long debut; double duree;
		debut=System.nanoTime();
		
		////////////////////////////////////////////////////////
		// Generate CSP instance
		/////////////////////////////////////////////////////////
		System.out.println("CSP instance generator is running");
	    GenXCSP generation= new GenXCSP(metaModelFile,root,reader,cfr,sym);
		generation.GenerateXCSP(CSPInstanceFile);
		maxDomains=generation.getMaxDomains();
				
		duree=(System.nanoTime()-debut)/1000000;
		System.out.println("\t[OK] CSP instance generation time = "+ duree+ " ms");
				
		
		/////////////////////////////////////////////////////////////
		// OCL constraints parsing
		////////////////////////////////////////////////////////////
		if (!oclFilePath.equals("")) {
			try {
				debut = System.nanoTime();
				System.out.println("OCL parser is running");	
				OclConstraints oclCons = new OclConstraints(reader, oclFilePath, GenXCSP.getXCSPinstance());
				generation.saveXML(oclCons.getResultDocumentXCSP(), CSPInstanceFile);

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
	
	/**
	 * This method read class sizes and returns the begin of the domain of each class
	 * 
	 * domain D = [a,b]
	 * 
	 * to get a: call the method with classID-1, then add 1
	 * to get b: call the method with classID
	 * 
	 * @param classID
	 * @return
	 */
	public int domaineSum(int classID)
	{
		int begin=0;
		if (classID==0)
			return 0;
		for(int i=0;i<=classID-1;i++)
		{
			begin+= classSizes.get(i);
		}
		return begin;
	}

	/**
	 * This method generate models using a detailed configuration file
	 * 
	 * @param string
	 * @param sym
	 * @param numberOfSolutions
	 * @throws IOException
	 */
	public void generateModel(String string, int sym, int numberOfSolutions) throws IOException {}
	
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
