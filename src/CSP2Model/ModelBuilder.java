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
import Utils.OCL.OclConstraints;

public abstract class ModelBuilder {

	protected MetaModelReader reader;
	protected String ModelFile;
	protected String root;
	protected String InstanceFile;
	protected String Model;
	protected int refB;
	protected ArrayList<Integer> sizes;
	protected ArrayList<Integer> sizesMin;
	protected String oclFilePath;
	protected int maxDomains;
	protected ArrayList<FoundSolution> foundSolutions= new ArrayList<FoundSolution>();
	
	/***
	 * 
	 * @param ModelFile: .ECORE meta-model File
	 * @param racine: Root Class of this meta-model
	 * @param InstanceFile: .XML file path of the produced CSP instance
	 */
	public ModelBuilder(String ModelFile, String root,String InstanceFile, String oclFilePath){
		
		this.ModelFile = ModelFile;
		this.root = root;
		this.InstanceFile = InstanceFile;
		this.oclFilePath = oclFilePath;
		this.Model= root+new Date().getDate()+new Date().getHours()+new Date().getMinutes()+ new Date().getSeconds()+"UB"+refB;
		
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
		this.reader= new MetaModelReader(ModelFile, root,lb,ub);
		this.refB=rb;
		this.sizes=reader.getClassSize();
		this.sizesMin=reader.getClassSizeMin();
		
		long debut; double duree;
		debut=System.nanoTime();
			
		//Call the CSP generator
		System.out.print("CSP instance generator is running...");
		GenXCSP generation= new GenXCSP(ModelFile,root,reader,ub,rb,sym);
		generation.GenerateXCSP(InstanceFile);
		maxDomains=generation.getMaxDomains();
		System.out.println(" OK");
				
		duree=(System.nanoTime()-debut)/1000000;
		System.out.println("\tCSP istance generation time= "+ duree+ " ms");
				
		/////////////////////////////////////////////////////////////
		// Contraintes OCL
		////////////////////////////////////////////////////////////
		if (!oclFilePath.equals("")) {
			try {
				debut = System.nanoTime();
				System.out.print("OCL parser is running...");
			
				OclConstraints oclCons = new OclConstraints(reader, oclFilePath, GenXCSP.getXCSPinstance());
				generation.saveXML(oclCons.getResultDocumentXCSP(), InstanceFile);
				
				duree = (System.nanoTime()-debut)/1000000;
				System.out.println(" OK");
				System.out.println("\tOCL constraints parsing duration = "+ duree+ " ms");
			} catch (FileNotFoundException | ParserException e) {
				System.err.println("Problems when parsing OCL constraints !");
				e.printStackTrace();
			}
		}
		
		///////////////////////////////////////////////////////////////
		//Excuter le solveur
		////////////////////////////////////////////////////////////
		System.out.print("CSP Solver is running...");
		BufferedReader bufferedreader=executeAbsconSolver(InstanceFile, sol);
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
		/*
		 * 
		 * Initialize the ConfigFileReader to read the input configuration file and
		 * Get: - RefBound
		 *      - FeaturesBound
		 *      - Read Class sizes
		 */
		ConfigFileReader cfr= new ConfigFileReader(configFilePath);
		cfr.read();
		
		this.reader= new MetaModelReader(ModelFile, root, cfr);
		sizes= reader.getClassSize();
		sizesMin= reader.getClassSizeMin();
		this.refB= cfr.getRefsBound();
						
		long debut; double duree;
		debut=System.nanoTime();
		
		System.out.print("CSP instance generator is running...");
	    GenXCSP generation= new GenXCSP(ModelFile,root,reader,cfr,sym);
		generation.GenerateXCSP(InstanceFile);
		maxDomains=generation.getMaxDomains();
		System.out.println(" OK");
				
		duree=(System.nanoTime()-debut)/1000000;
		System.out.println("\tCSP instance generation time = "+ duree+ " ms");
				
		
		// Contraintes OCL
		if (!oclFilePath.equals("")) {
			try {
				debut = System.nanoTime();
				System.out.print("OCL parser is running...");	
				OclConstraints oclCons = new OclConstraints(reader, oclFilePath, GenXCSP.getXCSPinstance());
				generation.saveXML(oclCons.getResultDocumentXCSP(), InstanceFile);

				duree = (System.nanoTime()-debut)/1000000;
				System.out.println(" OK");
				System.out.println("\tOCL constraints treatment duration= "+ duree+ " ms");
			} catch (FileNotFoundException | ParserException e) {
				System.err.println("\tProblems when parsing OCL file !");
				e.printStackTrace();
			}
		}
		
		///////////////////////////////////////////////////////////////
		//Ececuter le solveur
		////////////////////////////////////////////////////////////
		System.out.print("CSP Solver is running...");
		BufferedReader r;
		r=executeAbsconSolver(InstanceFile,sol);
		findAllSolutions(r);
	}
	
	public BufferedReader executeAbsconSolver(String Instancefile, int sol){
			
			String cmd = "java -jar abssol.jar " + Instancefile +" -s="+ sol;
	
			Process p = null;
				try {
					p = Runtime.getRuntime().exec(cmd);
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					return reader;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				System.out.println(" Not OK :(");
				System.out.println("\tCSP instance is unsatisfiable :(");
			}
			else{
				System.out.println(" OK");
				System.out.println("\tResolutuon time" +du);
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());	
		}
	}
	
	public int domaineSum(int k)
	{
		int s=0;
		if (k==0)
			return 0;
		for(int i=0;i<=k-1;i++)
		{
			s+= sizes.get(i);
		}
		return s;
	}
	
	public int domaineSumMin(int k)
	{
		int s=0;
		if (k==0)
			return 0;
		for(int i=0;i<=k-1;i++)
		{
			s+= sizesMin.get(i);
		}
		return s;
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
