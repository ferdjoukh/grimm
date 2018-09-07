package Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.ocl.ParserException;

import Utils.OCL.OclConstraints;

public abstract class ModelBuilder {

	protected ModelReader r;
	protected String ModelFile;
	protected String root;
	protected String InstanceFile;
	protected String Model;
	protected int refB;
	protected ArrayList<Integer> sizes;
	protected ArrayList<Integer> sizesMin;
	protected String oclFilePath;
	protected int maxDomains;
	
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
	
	/***
	 * 
	 * @param lb: Class instance number lower bound
	 * @param ub: Class instance number upper bound
	 * @param rb: Unbounded References bound (*references)
	 * @param sym: To break or not to break symmetries {0,1} 
	 * @param sol: Solutions number ?
	 * @throws IOException
	 */
	public ArrayList<Integer> CallCSPGenrator(int lb, int ub, int rb, int sym, int sol) throws IOException
	{
		this.r= new ModelReader(ModelFile, root,lb,ub);
		this.refB=rb;
		this.sizes=r.getClassSize();
		this.sizesMin=r.getClassSizeMin();
		
		long debut; double duree;
		debut=System.nanoTime();
			
		//Call the CSP generator
		System.out.print("CSP instance generator is running...");
		GenXCSP generation= new GenXCSP(ModelFile,root,r,ub,rb,sym);
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
			
				OclConstraints oclCons = new OclConstraints(r, oclFilePath, GenXCSP.getXCSPinstance());
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
		BufferedReader r;
		r=Execute(InstanceFile, sol);
		ArrayList<Integer> vals= new ArrayList<Integer>();
		vals=RValues(r);
		return(vals);
	}
	
	/***
	 * 
	 * @param configFilePath: Input a Configuration file 
	 * @param sym: To break or not to break symmetries {0,1} 
	 * @param sol: Solutions number ?
	 * @return 
	 * @throws IOException
	 */
	public ArrayList<Integer> CallCSPGenrator(String configFilePath, int sym, int sol) throws IOException
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
		
		
		this.r= new ModelReader(ModelFile, root, cfr);
		sizes= r.getClassSize();
		sizesMin= r.getClassSizeMin();
		this.refB= cfr.getRefsBound();
						
		long debut; double duree;
		debut=System.nanoTime();
		
		System.out.print("CSP instance generator is running...");
	    GenXCSP generation= new GenXCSP(ModelFile,root,r,cfr,sym);
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
				OclConstraints oclCons = new OclConstraints(r, oclFilePath, GenXCSP.getXCSPinstance());
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
		r=Execute(InstanceFile,sol);
		ArrayList<Integer> vals= new ArrayList<Integer>();
		vals=RValues(r);
		
		return vals;
		
		
	}
	
	public BufferedReader Execute(String Instancefile, int sol){
			
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
	
	public ArrayList<Integer> RValues(BufferedReader reader){
		ArrayList<Integer> vals= new ArrayList<Integer>();
		String line;
		String du = null;
		int found=0;
		
		try {
			while((line = reader.readLine()) != null) {
			//	System.out.println(line);
				if(line.startsWith("s SATISFIABLE")) {	        
			    	
			    	found=1;
			    }
			    else  if(line.startsWith("v ")){
			    	int i=2;int varl=0;
			    	while(i<line.length())
			    	{
			    		varl=line.indexOf(" ", i);
			    		vals.add(Integer.parseInt((line.substring(i, varl))));
			    		i= varl+1;
			    	}
			    } 
			    else if(line.startsWith("   totalWckTime"))
			    {
			    	int kk=line.indexOf("CpuTime=");
			    	du="= "+line.subSequence(kk+8, line.length())+" CPUtime";
			    }
			}
			if(found==0)
			{
				System.out.println(" Not OK :(");
				System.out.println("\tCSP instance is unsatisfiable :(");
				return null;
			}
			else
			{
				System.out.println(" OK");
				System.out.println("\tResolutuon time" +du);
				return vals;
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		    	
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

	public void generateModel(String string, int i, int j) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public void generateModel(int lb,int ub,int rb,int sym, int sol) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
