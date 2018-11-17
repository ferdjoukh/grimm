package CSP2Model;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import Utils.AttributeInstantiator;

public class CSP2dot extends ModelBuilder{
	
	/***
	 * 
	 * @param ModelFile: .ECORE meta-model File
	 * @param racine: Root Class of this meta-model
	 * @param InstanceFile: .XML file path of the produced CSP instance
	 */
	public CSP2dot(String ModelFile, String racine,String InstanceFile, String oclFilePath){
		super(ModelFile, racine,InstanceFile,oclFilePath);
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
	public void generateModel(int lb, int ub, int rb, int sym, int sol) throws IOException{
		super.CallCSPGenrator(lb, ub, rb, sym, sol);
		Solutions2Models();
	}
	
	/***
	 * 
	 * @param configFilePath: Input a Configuration file 
	 * @param sym: To break or not to break symmetries {0,1} 
	 * @param sol: Solutions number ?
	 * @throws IOException
	 */
	public void generateModel(String configFilePath, int sym, int sol) throws IOException{
		super.CallCSPGenrator(configFilePath, sym, sol);
		Solutions2Models();
	}
	
	public void Solutions2Models() {
		
		System.out.println("Model Builder is running");
		int ID=0;
		for(FoundSolution solution: foundSolutions) {
			ID++;
			try {
				generateDot(solution.getValues(),ID);
			} catch (IOException e) {
				
			}
		}
	}
		
	public void generateDot(ArrayList<Integer> values, int ID) throws IOException
	{
		ArrayList<Integer> vals= values;
		int variable=0;
		EPackage pack= reader.getModelPackage();
		List<EClass> cls= reader.getClasses();
		PrintWriter ecrivain;
		
		ArrayList<String> references= new ArrayList<String>();
				
		new File(root).mkdir();
		ecrivain =  new PrintWriter(new BufferedWriter(new FileWriter(root+"/"+this.modelFilePath+ID+".dot")));
		
		ecrivain.write("Graph g{ \n");
		
		String AttrDots="";
		
		int classDomBegin=0,classDomEnd=0;
		////////////////////////////////////////////////////////////
		//  Créer les objets instances de classe et leurs attribut
		////////////////////////////////////////////////////////////
		for(EClass c: cls)
		{
			String currentClassName = c.getName();
			
			/////////////////////////////////////
			// Instance of rootClass
			/////////////////////////////////////
			if(currentClassName.equals(root))		
			{
				//Attributes
				for(EAttribute a:reader.getAllAttributesFromClass(c))
				{
					if(a.getEType().getName().equals("EBoolean")) {
						boolean value= AttributeInstantiator.generateBoolean();
						AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
						
					}else if(a.getEType().getName().equals("EString")) {
						if(a.getName().toLowerCase().equals("name")) {
							String value = AttributeInstantiator.generateBasicName(currentClassName, 1);
							AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
						}else {
							String value = AttributeInstantiator.randomString();
							AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
						}
						
					}
					else if (a.getEType().getName().equals("EInt")) {
						int value = AttributeInstantiator.randomInt(1, 100);
						AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
					}
					else {
						//@TODO enumerations 
						EEnum enume= null;
						try{enume=(EEnum) a.getEType();}catch(Exception e){}
						EClass etype=null;
						try{etype=(EClass) a.getEType();}catch(Exception e){}				
						if(enume!=null)
						{
							AttrDots= AttrDots+" "+ a.getName()+"="+ enume.getEEnumLiteral(vals.get(variable)-1)+" \\n";
						}
						
					}
					//variable++;
				}	
				
				for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c)){
					int zz=ref.getUpperBound();
					if (zz==-1){	
						if(!ref.getEReferenceType().isAbstract())
							zz=referenceUpperBound;
						else
							zz=referenceUpperBound;
					}
					for(int z=1;z<=zz;z++){variable++;}
    			}
				
				//Create the shape for the current Object
				ecrivain.write("struct1 [shape=record,label=\"{"+c.getName().charAt(0)+"1:"+c.getName()+"|"+ AttrDots +"}\"]; \n");
			}
			else
			{
				
				//////////////////////////////////////////////
				// Creating the instances of other classes
				//////////////////////////////////////////////
				classDomBegin=  reader.domaineSum(reader.getClassIndex(c)-1)+1;
				classDomEnd=  reader.domaineSum(reader.getClassIndex(c)); 
				
				for(int instanceOID=classDomBegin;instanceOID<=classDomEnd;instanceOID++)
				{
					AttrDots="";
					//////////////////////////
					// Creating the attributes
					//////////////////////////
					for(EAttribute a:reader.getAllAttributesFromClass(c))
					{
						if(a.getEType().getName().equals("EBoolean")) {
							boolean value= AttributeInstantiator.generateBoolean();
							AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
							
						}else if(a.getEType().getName().equals("EString")) {
							if(a.getName().toLowerCase().equals("name")) {
								String value = AttributeInstantiator.generateBasicName(currentClassName, instanceOID);
								AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
							}else {
								String value = AttributeInstantiator.randomString();
								AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
							}
							
						}
						else if (a.getEType().getName().equals("EInt")) {
							int value = AttributeInstantiator.randomInt(1, 100);
							AttrDots= AttrDots+ " "+ a.getName()+"="+ value +" \\n";
						}
						else
						{
							//@TODO enumerations 
							EEnum enume= null;
							try{enume=(EEnum) a.getEType();}catch(Exception e){}
							EClass etype=null;
							try{etype=(EClass) a.getEType();}catch(Exception e){}				
							if(enume!=null){
								AttrDots= AttrDots+" "+ a.getName()+"="+ enume.getEEnumLiteral(vals.get(variable)-1)+" \\n";
							}							
						}
						//variable++;
					}
		    
					//////////////////////
					// Creating the links
					//////////////////////
					for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c)){
						int zz=ref.getUpperBound();
						if (zz==-1)
						{	//zz=5;
							if(ref.getEReferenceType().isAbstract())
								zz=referenceUpperBound;
							else
								zz=referenceUpperBound;
						}
						for(int z=1;z<=zz;z++)
						{
							int precedente= vals.get(variable);
							if(vals.get(variable)<=this.maxDomains)
							{
									if(ref.isContainment())
										ecrivain.write("struct"+instanceOID+" -- "+"struct"+vals.get(variable) +" [arrowtail=diamond,arrowhead=none,dir=both,label=\""+ref.getName()+"\"]   ;\n");
									else if(ref.getEOpposite() != null)
										ecrivain.write("struct"+instanceOID+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=both,label=\""+ref.getName()+"\"]   ;\n");
									else
										ecrivain.write("struct"+instanceOID+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=forward,label=\""+ref.getName()+"\"]   ;\n");
									
									references.add(instanceOID+"-"+vals.get(variable));								
							}
							variable++;
						}
					}
				
					//Creating the current shape
					ecrivain.write("struct"+instanceOID+" [shape=record,label=\"{"+c.getName().charAt(0)+instanceOID+":"+c.getName()+"|"+ AttrDots +"}\"];\n");
			
					//Adding a link between rootObject and the current Object
					ecrivain.write("struct1"+" -- "+"struct"+instanceOID +" [arrowtail=diamond,arrowhead=none,dir=both];\n");
					references.add("1-"+instanceOID);
				}
			}
		}
		ecrivain.write("} \n");
		ecrivain.close();
				
		/////////////////////////////////////////////////////
		// Call graphViz in order to generate 
		// an object diagram in pdf file
		/////////////////////////////////////////////////////
		String cmd = "dot -Tpdf "+root+"/"+this.modelFilePath+ID+".dot -o "+root+ "/"+ this.modelFilePath+ID+".pdf";
		
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			System.out.println("\t[OK] Model :"+root+"/"+this.modelFilePath+ID+".pdf was generated");
		}
		catch(Exception e){
			System.out.println("\\t[OK] MODEL "+root+"/"+this.modelFilePath+ID+".dot was generated");
		}
		
	}
}
