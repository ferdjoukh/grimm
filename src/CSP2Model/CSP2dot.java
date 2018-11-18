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
	
	
	public String instantiateAttributes(EClass c, int OID) {
		
		String attributes="";
		String currentclassname = c.getName();
		
		for(EAttribute a:reader.getAllAttributesFromClass(c))
		{
			if(a.getEType().getName().equals("EBoolean")) {
				boolean value= AttributeInstantiator.generateBoolean();
				attributes= attributes+ " "+ a.getName()+"="+ value +" \\n";
				
			}else if(a.getEType().getName().equals("EString")) {
				if(a.getName().toLowerCase().equals("name")) {
					String value = AttributeInstantiator.generateBasicName(currentclassname, OID);
					attributes= attributes+ " "+ a.getName()+"="+ value +" \\n";
				}else {
					String value = AttributeInstantiator.randomString();
					attributes= attributes+ " "+ a.getName()+"="+ value +" \\n";
				}
				
			}
			else if (a.getEType().getName().equals("EInt")) {
				int value = AttributeInstantiator.randomInt(1, 100);
				attributes= attributes+ " "+ a.getName()+"="+ value +" \\n";
			}else if (a.getEType().eClass().getName().equals("EEnum")){
				
				EEnum enume= (EEnum) a.getEType();
				if (enume.getELiterals() != null) {
					int numberOfLiterals = enume.getELiterals().size();
					int value =  AttributeInstantiator.randomInt(0, numberOfLiterals);	
					attributes= attributes+" "+ a.getName()+"="+ enume.getELiterals().get(value)+" \\n";
				}										
			}else {
				System.out.println("\t[WARNING] Class "+ currentclassname+ " Attribute "+ a.getName() +"  type not supported: "+a.getEType().getName());
			}				
		}	
		return attributes;
	}
		
	public void generateDot(ArrayList<Integer> values, int ID) throws IOException
	{
		ArrayList<Integer> vals= values;
		
		//init variable to 1 in order to skip first variable
		int variable=1;
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
				int rootObjectOID=  reader.domaineSum(reader.getClassIndex(c)-1)+1;
				
				//Attributes
				AttrDots = instantiateAttributes(c, rootObjectOID);
				
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
				
				for(int OID=classDomBegin;OID<=classDomEnd;OID++)
				{
					AttrDots="";
					//////////////////////////
					// Creating the attributes
					//////////////////////////
					AttrDots = instantiateAttributes(c, OID);
					
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
						for(int z=1;z<=zz;z++){
							
							int precedente= vals.get(variable);
							if(vals.get(variable)<=this.maxDomains){
									if(ref.isContainment())
										ecrivain.write("struct"+OID+" -- "+"struct"+vals.get(variable) +" [arrowtail=diamond,arrowhead=none,dir=both,label=\""+ref.getName()+"\"]   ;\n");
									else if(ref.getEOpposite() != null)
										ecrivain.write("struct"+OID+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=both,label=\""+ref.getName()+"\"]   ;\n");
									else
										ecrivain.write("struct"+OID+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=forward,label=\""+ref.getName()+"\"]   ;\n");
									
									references.add(OID+"-"+vals.get(variable));								
							}
							variable++;
						}
					}
				
					//Creating the current shape
					ecrivain.write("struct"+OID+" [shape=record,label=\"{"+c.getName().charAt(0)+OID+":"+c.getName()+"|"+ AttrDots +"}\"];\n");
			
					//Adding a link between rootObject and the current Object
					ecrivain.write("struct1"+" -- "+"struct"+OID +" [arrowtail=diamond,arrowhead=none,dir=both];\n");
					references.add("1-"+OID);
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
