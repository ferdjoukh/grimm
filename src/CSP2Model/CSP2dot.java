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
	public void generateModel(int lb, int ub, int rb, int sym, int sol) throws IOException 
	{
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
	public void generateModel(String configFilePath, int sym, int sol) throws IOException
	{
		super.CallCSPGenrator(configFilePath, sym, sol);
		Solutions2Models();
	}
	
	public void Solutions2Models() {
		System.out.println("Model Builder is running...");
		
		int ID=0;
		
		for(FoundSolution solution: foundSolutions) {
			ID++;
			try {
				generateDot(solution.getValues(),ID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		
		int lb=0,ub=0;
		////////////////////////////////////////////////////////////
		//  Créer les objets instances de classe et leurs attribut
		////////////////////////////////////////////////////////////
		for(EClass c: cls)
		{
			//Instance de la racine
			if(c.getName().equals(root))		
			{
				//ses attributs
				for(EAttribute a:reader.getAllAttributesFromClass(c))
				{
					if(a.getEType().getName()=="EString")
						AttrDots= AttrDots+ " "+ a.getName()+"="+ a.getName()+vals.get(variable).toString()+" \\n";
					else if (a.getEType().getName()=="EInt")
						AttrDots= AttrDots+ " "+ a.getName()+"="+ vals.get(variable).toString()+" \\n";
					else
					{
						//C'est une Enumération !!!
						EEnum enume= null;
						try{enume=(EEnum) a.getEType();}catch(Exception e){}
						EClass etype=null;
						try{etype=(EClass) a.getEType();}catch(Exception e){}				
						if(enume!=null)
						{
							AttrDots= AttrDots+" "+ a.getName()+"="+ enume.getEEnumLiteral(vals.get(variable)-1)+" \\n";
						}
						if(etype!=null)
							System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
					}
					variable++;
				}	
				
				for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c))
    			{
					int zz=ref.getUpperBound();
					if (zz==-1)
					{	//zz=5;
						if(!ref.getEReferenceType().isAbstract())
							zz=referenceUpperBound;
						else
							zz=referenceUpperBound;
					}
					for(int z=1;z<=zz;z++)
					{
						//Créer les relations vers les autres instances de classes
						if(vals.get(variable)<=this.maxDomains)
						{
							if(!references.contains("1-"+vals.get(variable)))
							{
								//ecrivain.write("struct1"+" -- "+"struct"+vals.get(variable) +" [arrowtail=diamond,arrowhead=none,dir=both,label=\""+ref.getName()+"\"]   ;\n");
								//references.add("1-"+vals.get(variable));
							}
						}
						
						variable++;
						
					}
    			}
				
	//			System.out.println("struct1 [shape=record,label=\"{"+c.getName()+"|"+ AttrDots +"}\"];");
				ecrivain.write("struct1 [shape=record,label=\"{"+c.getName().charAt(0)+"1:"+c.getName()+"|"+ AttrDots +"}\"]; \n");
			}
			else
			{
				
				System.out.println(c.getName());
				
				//Les instances des autres classes
				lb=  domaineSum(reader.getClassIndex(c)-1)+1;
				ub=  domaineSum(reader.getClassIndex(c)); 
				for(int i=lb;i<=ub;i++)
				{
					AttrDots="";
					
					//Pour une instance donnée
	//				System.out.println("struct"+i+" [shape=record,label=\"{"+c.getName()+i+"}\"];");
				
				
				//ses attributs
				for(EAttribute a:reader.getAllAttributesFromClass(c))
				{
					//System.out.println("var="+variable);
					if(a.getEType().getName()=="EString")
						AttrDots= AttrDots+ " "+ a.getName()+"="+ a.getName()+vals.get(variable).toString()+" \\n";
					else if (a.getEType().getName()=="EInt")
						AttrDots= AttrDots+ " "+ a.getName()+"="+ vals.get(variable).toString()+" \\n";
					else
					{
						//C'est une Enumération !!!
						EEnum enume= null;
						try{enume=(EEnum) a.getEType();}catch(Exception e){}
						EClass etype=null;
						try{etype=(EClass) a.getEType();}catch(Exception e){}				
						if(enume!=null)
						{
							AttrDots= AttrDots+" "+ a.getName()+"="+ enume.getEEnumLiteral(vals.get(variable)-1)+" \\n";
						}
						if(etype!=null)
							System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
					}
					variable++;
				}
		    
				//Ses références
				//ses liens
				for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c))
				{
					System.out.println(" "+ref.getName());
					
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
						//ICI créer une variable pointeur du type dst
			//			System.out.println("struct"+i+" -- "+"struct"+vals.get(variable));
						int precedente= vals.get(variable);
						if(vals.get(variable)<=this.maxDomains)
						{
							//if(!references.contains(i+"-"+vals.get(variable)))
							//{
								
								//The Edge is different when the kind of the reference is.
								if(ref.isContainment())
									ecrivain.write("struct"+i+" -- "+"struct"+vals.get(variable) +" [arrowtail=diamond,arrowhead=none,dir=both,label=\""+ref.getName()+"\"]   ;\n");
								else if(ref.getEOpposite() != null)
									ecrivain.write("struct"+i+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=both,label=\""+ref.getName()+"\"]   ;\n");
								else
									ecrivain.write("struct"+i+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=forward,label=\""+ref.getName()+"\"]   ;\n");
								references.add(i+"-"+vals.get(variable));
							//}
						}
						variable++;
					}
				}
				
				//Pour une instance donnée
			//	System.out.println("struct"+i+" [shape=record,label=\"{"+c.getName()+i+"|"+ AttrDots +"}\"];");
				
                ecrivain.write("struct"+i+" [shape=record,label=\"{"+c.getName().charAt(0)+i+":"+c.getName()+"|"+ AttrDots +"}\"];\n");
			
                ecrivain.write("struct1"+" -- "+"struct"+i +" [arrowtail=diamond,arrowhead=none,dir=both];\n");
                references.add("1-"+i);
				
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
			
			System.out.println("  model :"+root+"/"+this.modelFilePath+ID+".pdf was generated");
		  
		}
		catch(Exception e)
		{
			System.out.println(" model :"+root+"/"+this.modelFilePath+ID+".dot was generated");
		}
		
	}
}
