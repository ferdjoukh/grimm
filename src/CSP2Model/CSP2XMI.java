package CSP2Model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import Utils.ClassInstance;
import Utils.Utils;

public class CSP2XMI extends ModelBuilder{

	/**
	 * 
	 * @param ModelFile: .ecore file
	 * @param root: root class name 
	 * @param InstanceFile: CSP instance file .xml
	 * @param Model: Result model file path .xmi
	 * @param oclFilePath: ocl file path .ocl or empty string.
	 */
	public CSP2XMI(String ModelFile, String root,String InstanceFile,String oclFilePath){
			
		super(ModelFile, root, InstanceFile, oclFilePath);
	}
	
	/***
	 * 
	 * @param lb: lower bound for class instances
	 * @param ub: upper bound for class instances
	 * @param rb: bound for unbounded references
	 * @param sym: break or not symmetries  0,1 ?
	 * @param sol: #solution to find 
	 * @throws IOException 
	 */
	public void generateModel(int lb,int ub,int rb,int sym, int sol) throws IOException
	{
		super.CallCSPGenrator(lb, ub, rb, sym, sol);	
		Solutions2Models();
	}
	
	/***
	 * 
	 * @param configFilePath: path for a configuration file
	 * @param sym: break or not symmetries
	 * @param sol: solution number
	 * @throws IOException
	 */
	public void generateModel(String configFilePath, int sym, int numberOfSolutions) throws IOException
	{
		super.CallCSPGenrator(configFilePath, sym, numberOfSolutions);
		Solutions2Models();

	}
	
	public void Solutions2Models() {
		System.out.println("Model Builder is running...");
		
		int ID=0;
		
		System.out.println("step1");
		
		for(FoundSolution solution: foundSolutions) {
			ID++;
			
			System.out.println("step2");
			
			EObject object= FindModel(solution.getValues());
			
			System.out.println("step3");
			
			ValidModel(object,ID);
			
			System.out.println("step4");
		}
	}
	
	public EObject FindModel(ArrayList<Integer> values)
	{	
		ArrayList<Integer> vals= values;
		int variable=0;
		EPackage pack= super.r.getModelPackage();
		List<EClass> cls= super.r.getClasses();
	//	ArrayList<Integer> sizes= r.getClassSize();
		EObject rootObject = null;
		int lb=0,ub=0;
		
		//Les instances de classes
		ArrayList<ClassInstance> mesInst= new ArrayList<ClassInstance>(); 
		ArrayList<ClassInstance> mesInstLiees= new ArrayList<ClassInstance>(); 
		
		
		//Premier passage: Construire les instances
		for(EClass c: cls)
		{
			if(c.getName().equals(root))		
			{
				//Create instance of rootClass
				rootObject= pack.getEFactoryInstance().create(c);
				
				//ses attributs
				for(EAttribute a:super.r.getAllAttributesFromClass(c))
				{
					System.out.println(a.getName());
					
					if(a.getEType().getName()=="EString")
						rootObject.eSet(a, r.getBasePackage().getName()+"_"+vals.get(variable).toString());
				    else if (a.getEType().getName()=="EInt")
					    rootObject.eSet(a, vals.get(variable));	
				    else
					{
						//C'est une Enumération !!!
				    	EEnum enume= null;
						try{enume=(EEnum) a.getEType();}catch(Exception e){}
						EClass etype=null;
						try{etype=(EClass) a.getEType();}catch(Exception e){}				
						if(enume!=null)
						{
							rootObject.eSet(a, enume.getEEnumLiteral(vals.get(variable)-1));
						}
						if(etype!=null)
							System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
					}
					variable++;
				}
				
				//ses liens
				//for (EReference ref: r.getAllReferencesFromClass(c))
				for (EReference ref: super.r.getAllReferencesFromClasswithOpposite(c))	    		
				{
						int zz=ref.getUpperBound();
    					if (zz==-1)
    					{	//zz=5;
    						if(!ref.getEReferenceType().isAbstract())
    							zz=refB;
    						else
    							zz=refB;
    					}
    					EClass dst= ref.getEReferenceType();
    					ArrayList<EObject> rr= new ArrayList<EObject>(); 
    					for(int z=1;z<=zz;z++)
    					{
    						//ICI créer une variable pointeur du type dst
    						variable++;
    					}
    			}
			}
			else
			{
				//Les autres classes
			
				lb=  domaineSum(r.getClassIndex(c)-1)+1;
				ub=  domaineSum(r.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{
					//int variablei=0;
					EObject i;
					i= pack.getEFactoryInstance().create(c);
					//ses attributs
					for(EAttribute a:super.r.getAllAttributesFromClass(c))
					{
						
						if(a.getEType().getName()=="EString"){
							i.eSet(a, c.getName()+"_"+j+"_"+a.getName()+"_"+ vals.get(variable).toString());
					    }
						else if (a.getEType().getName()=="EInt"){
							i.eSet(a, vals.get(variable));
					    }
						else{
							//C'est une Enumération !!!
							EEnum enume= null;
							try{enume=(EEnum) a.getEType();} catch(Exception e){}
							EClass etype=null;
							try{etype=(EClass) a.getEType();} catch(Exception e){}				
							if(enume!=null)
							{
								i.eSet(a, enume.getEEnumLiteral(vals.get(variable)-1));
							}
							if(etype!=null)
								System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
						}
							variable++;
					}
					
					//Ses références
					for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))
		    		{
						int zz=ref.getUpperBound();
	    				if (zz==-1)
	    				{	
	    					if(ref.getEReferenceType().isAbstract())
	    						zz=refB;
	    					else
	    						zz=refB;
	    				}
	    				
	    				for(int z=1;z<=zz;z++) variable++;
	    					
					}
					mesInst.add(new ClassInstance(j, i));
				}
			}
		} 
		//Fin du premier passage
		///////////////////////////////////
		//////////
		//Deuxième passage: Construire les pointeurs des références
		variable=0;		 
		for(EClass c: cls)
		{
			int vari=0;
			if(c.getName().equals(root))
			{
				vari= variable;
				//ses attributs
				for(EAttribute a:r.getAllAttributesFromClass(c)) variable++;		
				//ses liens
				for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))	
				{
					int zz=ref.getUpperBound();
		    	    if (zz==-1){	
						if(ref.getEReferenceType().isAbstract()) zz=refB;
						else zz=refB;
					}
		   			
		    	    for(int z=1;z<=zz;z++) variable++;
		    	}
				
			}
			else
			{
				//Les autres classes
				lb=  domaineSum(r.getClassIndex(c)-1)+1;
				ub=  domaineSum(r.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{
					EObject i;
					i=Utils.searchIns(mesInst, j);	
					//ses attributs
					for(EAttribute a:r.getAllAttributesFromClass(c)) variable++;
					//Ses références
					for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))	
					{
					    int refUpperBound=ref.getUpperBound();
			    		if (refUpperBound==-1) {
	    					if(ref.getEReferenceType().isAbstract()) refUpperBound=refB;
	    					else refUpperBound=refB;
	    				}
			    		EClass dst= ref.getEReferenceType();
			    		List<EObject> objectsToLink= new ArrayList<EObject>(); 
			    			
			    		if(refUpperBound==1){
			    			if(vals.get(variable)!=0)
			    			{
			    				try{
			    					i.eSet(ref, Utils.searchIns(mesInst, vals.get(variable)));
			    				}catch(Exception e){}
			    			}
			    				variable++;
			    		}
			    		else{
			    			int z=0;
			    			for(z=1;z<=refUpperBound;z++)
			    			{
			    				if(vals.get(variable)!=0)
			    				{
			    					objectsToLink.add(Utils.searchIns(mesInst, vals.get(variable)));
			    					//System.out.println("je suis passé par la pour "+j+ " j'ai ajouté: "+ vals.get(variable));
			    				}		
			    				variable++;
			    			}
			    			//System.out.println("ref: " +ref.getName()+" z= "+z+" --- "+objectsToLink.toString());
			    			try{
			    				i.eSet(ref, objectsToLink);
		    				}catch(Exception e){}
			    			
			    		}						
			    	}
					mesInstLiees.add(new ClassInstance(j, i));						
				}					
			}
		}
		//Fin du 2ème passage
		
		///////////////////////////////////////////////////////////
		//////////////////////////////////
		/////////////////
		//3ème passage : construire les relations de compartimentage
		variable=0;	
		for(EClass c: cls)
		{
			//Instance de la racine
			if(c.getName().equals(root))
			{
				//ses attributs
				for(EAttribute a:super.r.getAllAttributesFromClass(c)) variable++;
				
				//ses liens
				for (EReference ref: super.r.getAllReferencesFromClass(c))	
		    	{
					System.out.println(ref.getName());
					int refUpperBound=ref.getUpperBound();
					if (refUpperBound==-1){	
						if(ref.getEReferenceType().isAbstract()) refUpperBound=refB;
						else refUpperBound=refB;
					}
		   			List<EObject> objectstoCompose= new ArrayList<EObject>(); 
		    		
		   			if(refUpperBound==1){
		   				if(vals.get(variable)!=0) {
		   					try {
		   						
		   						String targetedClassName=ref.getEType().getName();
		   						EObject target = Utils.searchInstanceByClass(mesInst, targetedClassName);
		   						
		   						System.out.println(" "+targetedClassName);
		   						System.out.println(" "+target);
		   						
		   						if(target!=null) {
		   							rootObject.eSet(ref, target);
		   						}
		   					}
		   					catch(Exception e) {
		   						System.out.println("Class:"+c.getName()+" Ref:"+ref.getName()+ " 1 component add error !");
		   					}
		   				}
		   				variable++;
		   			}	
		   			else{
		   				for(int z=1;z<=refUpperBound;z++) variable++;
		   				
		   				//Add the appropriate instances for each reference of rootClass
		   				for(ClassInstance clInst: mesInst)
		   				{
		   					EObject object= clInst.getObj();
		   					String cl =((DynamicEObjectImpl) object).eClass().getName();
		   					
		   					if(cl.equals(ref.getEType().getName())){
		   						objectstoCompose.add(object);
		   					}
		   				}
		   				try{
		   					rootObject.eSet(ref, objectstoCompose);
		   				}
		   				catch(Exception e){
		   					System.out.println("Class:"+c.getName()+" Ref:"+ref.getName()+ " n component add error !");
		   				}
		    		}
		    	}
			}
			else
			{
				//Les autres classes
				lb=  domaineSum(r.getClassIndex(c)-1)+1;
				ub=  domaineSum(r.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{	
						//ses attributs
						for(EAttribute a:r.getAllAttributesFromClass(c)) variable++;
						//Ses références
						for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))
			    		{
						    int zz=ref.getUpperBound();
			    			if (zz==-1){
	    						if(ref.getEReferenceType().isAbstract()) zz=refB;
	    						else zz=refB;
	    					}
			    			for(int z=1;z<=zz;z++) variable++;
			    		}						
			    	}
			}
		}
		//Fin du 3ème passage
		
		System.out.println("3eme passage");
	    
		return rootObject;
	}
	
	 public void ValidModel(EObject o, int ID)
	 {
		 new File(root).mkdir();
		 ResourceSet resourceSet=new ResourceSetImpl();
		 //Resource resource= r.getModelResource();
		 EPackage pack= r.getModelPackage();
		 Resource resource;
		 try
		 {
			 resourceSet = new ResourceSetImpl();
			 resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",new XMIResourceFactoryImpl());
			 URI uri=URI.createURI(root+"/"+this.Model+ID+".xmi");
			 resource=resourceSet.createResource(uri);
			 resource.getContents().add(o);
			 Map<String,Boolean> opts= new HashMap<String,Boolean>();
			 //Important to get an XMI readable in EMF
			 opts.put(XMLResourceImpl.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			 resource.save(opts); 
			 System.out.println(" Model: "+root+"/"+this.Model+ID+".xmi was generated");
		 }
		 catch(Exception e){
			 System.out.println(" Problem when building the model");
		 }
	 }
}