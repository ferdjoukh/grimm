package Utils;


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
		ArrayList<Integer> vals= super.CallCSPGenrator(lb, ub, rb, sym, sol);
		
		///////////////////////////////////////////////////////////////
		//Reconstruire une solution
		/////////////////////////////////////////////////////////////
		if(vals!=null)
		{	
			if(vals.size()!=0)
			{
				System.out.println("Model builder is running...");
				generateDot(vals);
			}
		}
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
		
		ArrayList<Integer> vals=super.CallCSPGenrator(configFilePath, sym, sol);
		
		///////////////////////////////////////////////////////////////
		//Reconstruire une solution
		/////////////////////////////////////////////////////////////
		if(vals!=null)
		{	
			if(vals.size()!=0)
			{
				System.out.print("Model builder is running...");
				generateDot(vals);
			}
		}
	}
		
	public void generateDot(ArrayList<Integer> values) throws IOException
	{
		ArrayList<Integer> vals= values;
		int variable=0;
		EPackage pack= r.getModelPackage();
		List<EClass> cls= r.getClasses();
		PrintWriter ecrivain;
		
		ArrayList<String> references= new ArrayList<String>();
				
		new File(root).mkdir();
		ecrivain =  new PrintWriter(new BufferedWriter(new FileWriter(root+"/"+this.Model+".dot")));
		
		ecrivain.write("Graph g{ \n");
		
		String AttrDots="";
		
		int lb=0,ub=0;
		/////////////////////////////////////////////////////////
		//  Créer les objets instances de classe et leurs attribut;
		for(EClass c: cls)
		{
			//Instance de la racine
			if(c.getName().equals(root))		
			{
				//ses attributs
				for(EAttribute a:r.getAllAttributesFromClass(c))
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
				
				for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))
    			{
					int zz=ref.getUpperBound();
					if (zz==-1)
					{	//zz=5;
						if(!ref.getEReferenceType().isAbstract())
							zz=refB;
						else
							zz=refB;
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
				
				//Les instances des autres classes
				lb=  domaineSum(r.getClassIndex(c)-1)+1;
				ub=  domaineSum(r.getClassIndex(c)); 
				for(int i=lb;i<=ub;i++)
				{
					AttrDots="";
					
					//Pour une instance donnée
	//				System.out.println("struct"+i+" [shape=record,label=\"{"+c.getName()+i+"}\"];");
				
				
				//ses attributs
				for(EAttribute a:r.getAllAttributesFromClass(c))
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
				for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))
				{
					int zz=ref.getUpperBound();
					if (zz==-1)
					{	//zz=5;
						if(ref.getEReferenceType().isAbstract())
							zz=refB;
						else
							zz=refB;
					}
					for(int z=1;z<=zz;z++)
					{
						//ICI créer une variable pointeur du type dst
			//			System.out.println("struct"+i+" -- "+"struct"+vals.get(variable));
						int precedente= vals.get(variable);
						if(vals.get(variable)<=this.maxDomains)
						{
							if(!references.contains(i+"-"+vals.get(variable)))
							{
								
								//The Edge is different when the kind of the reference is.
								if(ref.isContainment())
									ecrivain.write("struct"+i+" -- "+"struct"+vals.get(variable) +" [arrowtail=diamond,arrowhead=none,dir=both,label=\""+ref.getName()+"\"]   ;\n");
								else if(ref.getEOpposite() != null)
									ecrivain.write("struct"+i+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=both,label=\""+ref.getName()+"\"]   ;\n");
								else
									ecrivain.write("struct"+i+" -- "+"struct"+vals.get(variable) +" [arrowhead=open,arrowtail=open,dir=forward,label=\""+ref.getName()+"\"]   ;\n");
								references.add(i+"-"+vals.get(variable));
							}
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
		
		System.out.println(" OK");
		
		////////////////////////////////////////////////
		/////////////////////////
		//////////
		////        Générer le pdf...
		
		String cmd = "dot -Tpdf "+root+"/"+this.Model+".dot -o "+root+ "/"+ this.Model+".pdf";
		
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			
			System.out.println("\tSuccess, A model found :D");
		    System.out.println("\t"+root+"/"+this.Model+".pdf is the generated model");
		  
		}
		catch(Exception e)
		{
			System.out.println("\tGraphViz Software is not installed. \n\t"+root+"/"+this.Model+".dot is the generated model");
		}
		
	}
}
