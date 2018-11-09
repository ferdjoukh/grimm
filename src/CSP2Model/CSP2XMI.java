package CSP2Model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
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

	ArrayList<ClassInstance> allCreatedEObjects= new ArrayList<ClassInstance>(); 
	
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
		
		if(foundSolutions.size()==0) {
			System.out.println("\t0 solutions were found :(");
			return;
		}
		
		int ID=0;
		
		for(FoundSolution solution: foundSolutions) {
			ID++;
			
			EObject object= FindModel(solution.getValues());
			System.out.println(object);
			ValidModel(object,ID);
		}
	}
	
	public EObject FindModel(ArrayList<Integer> values)
	{	
		ArrayList<Integer> vals= values;
		int variable=0;
		EPackage pack= super.reader.getModelPackage();
		List<EClass> cls= super.reader.getClasses();
		EObject rootObject = null;
		int lb=0,ub=0;
			
		ArrayList<ClassInstance> mesInstLiees= new ArrayList<ClassInstance>(); 
		
		/////////////////////////////////////////////
		// STEP 1 : Create all EObjects
		////////////////////////////////////////////
		for(EClass c: cls)
		{
			if(c.getName().equals(root))		
			{
				//Create instance of rootClass
				rootObject= pack.getEFactoryInstance().create(c);
				
				/////////////////////////////////////
				//   Attributes of rootClass
				////////////////////////////////////
				for(EAttribute a:super.reader.getAllAttributesFromClass(c))
				{
					if(a.isChangeable()) {
						
						if(a.getEType().getName().equals("EString")) {
							rootObject.eSet(a, reader.getBasePackage().getName()+"_"+vals.get(variable).toString());							
						}
					    else if (a.getEType().getName().equals("EInt"))
						    rootObject.eSet(a, vals.get(variable));	
					    else
						{
							//Enumeration type, boolean
					    	EEnum enume= null;
							try{enume=(EEnum) a.getEType();} catch(Exception e){}
							EClass etype=null;
							try{etype=(EClass) a.getEType();} catch(Exception e){}				
							if(enume!=null)
							{
								rootObject.eSet(a, enume.getEEnumLiteral(vals.get(variable)-1));
							}
							if(etype!=null)
								System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
						}
					}
					variable++;
				}
				
				/////////////////////////////////////////////////////
				// At this step, skip links of rootClass instance
				/////////////////////////////////////////////////////
				for (EReference ref: super.reader.getAllReferencesFromClasswithOpposite(c))	    		
				{
					int zz=ref.getUpperBound();
					if (zz==-1){	
						if(!ref.getEReferenceType().isAbstract())
							zz=refB;
						else
							zz=refB;
					}
					EClass dst= ref.getEReferenceType();
					ArrayList<EObject> rr= new ArrayList<EObject>(); 
					for(int z=1;z<=zz;z++)
					{
						variable++;
					}
    			}
			}
			else
			{
				////////////////////////////////////////////
				// Create instances of other classes
				////////////////////////////////////////////
				lb=  domaineSum(reader.getClassIndex(c)-1)+1;
				ub=  domaineSum(reader.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{
					///////////////////////////////////////
					// Create the EObject
					///////////////////////////////////////
					EObject createdObject;
					createdObject= pack.getEFactoryInstance().create(c);
					
					
					///////////////////////////////////////
					// Set values for attributes
					///////////////////////////////////////
					for(EAttribute a:super.reader.getAllAttributesFromClass(c))
					{
						if(a.isChangeable()) {
						    //System.out.println("UnChange: "+a.getName());
						
							if(a.getEType().getName().equals("EString")){
								createdObject.eSet(a, c.getName()+"_"+j+"_"+a.getName()+"_"+ vals.get(variable).toString());
						    }
							else if (a.getEType().getName().equals("EInt")){
								createdObject.eSet(a, vals.get(variable));
						    }
							else{
								// Enumeration
								EEnum enume= null;
								try{enume=(EEnum) a.getEType();} catch(Exception e){}
								EClass etype=null;
								try{etype=(EClass) a.getEType();} catch(Exception e){}				
								if(enume!=null)
								{
									createdObject.eSet(a, enume.getEEnumLiteral(vals.get(variable)-1));
								}
								if(etype!=null)
									System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
							}
						}
						variable++;
					}
					
					////////////////////////////////////////////////////
					// Skip the links of current EObject
					////////////////////////////////////////////////////
					for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c))
		    		{
						int zz=ref.getUpperBound();
	    				if (zz==-1)
	    				{	
	    					if(ref.getEReferenceType().isAbstract())
	    						zz=refB;
	    					else
	    						zz=refB;
	    				}
	    				for(int z=1;z<=zz;z++) {variable++;}
	    			}
					
					//Add current EObject to list of all instances 
					allCreatedEObjects.add(new ClassInstance(j, createdObject));
				}
			}
		} 
		
		///////////////////////////////////////////
		// STEP 2: Create pointers for references
		///////////////////////////////////////////
		variable=0;		 
		for(EClass currentClass: cls)
		{
			int vari=0;
			if(currentClass.getName().equals(root))
			{
				vari= variable;
				
				//Skip attributes of rootClass
				for(EAttribute a:reader.getAllAttributesFromClass(currentClass)) variable++;		
				
				//Skip references of rootClass
				for (EReference ref: reader.getAllReferencesFromClasswithOpposite(currentClass))	
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
				////////////////////////////////////////
				// Create links of other classes
				////////////////////////////////////////
				lb=  domaineSum(reader.getClassIndex(currentClass)-1)+1;
				ub=  domaineSum(reader.getClassIndex(currentClass)); 
				
				for(int j=lb;j<=ub;j++)
				{
					EObject currentEObject = Utils.searchIns(allCreatedEObjects, j);
					
					//Skip all attributes
					for(EAttribute a:reader.getAllAttributesFromClass(currentClass)) variable++;
					
					////////////////////////////////////////////////
					// Make sure that the reference is changeable
					////////////////////////////////////////////////
					for (EReference ref: reader.getAllReferencesFromClasswithOpposite(currentClass))	
					{
						if(ref.isChangeable()) {
							
							int refUpperBound=ref.getUpperBound();
				    		
							if (refUpperBound==-1) {
		    					if(ref.getEReferenceType().isAbstract()) refUpperBound=refB;
		    					else refUpperBound=refB;
		    				}
							
				    		EClass targetClass= ref.getEReferenceType();
				    		List<EObject> objectsToLink= new ArrayList<EObject>(); 
				    		
				    		if(refUpperBound==1){
				    			if(vals.get(variable)!=0)
				    			{
				    				try{
				    						EObject targetEObject= Utils.searchInstanceByClass(allCreatedEObjects, targetClass);
				    						
				    						if(targetEObject != null) {
				    							
				    							EClass targetEObjectClass= ((DynamicEObjectImpl) targetEObject).eClass();
						    					
				    							if(targetEObjectClass.getEAllSuperTypes().contains(targetClass)) {
				    								currentEObject.eSet(ref, targetEObject);
				    								System.out.println("1 LINK: " + currentClass.getName()+ " >> "+ ref.getName() +" " );
				    							}
				    						}				    										    				
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
				    					//EObject targetEObject= Utils.searchIns(allCreatedEObjects, vals.get(variable));
				    					
				    					EObject targetEObject= Utils.searchInstanceByClass(allCreatedEObjects, targetClass);
				    					
				    					if(targetEObject!=null) {
				    						EClass targetEObjectClass= ((DynamicEObjectImpl) targetEObject).eClass();
					    					
				    						if(targetEObjectClass.getEAllSuperTypes().contains(targetClass)) {
				    							objectsToLink.add(targetEObject);
				    							System.out.println("1 n LINK: " + currentClass.getName()+ " >> "+ ref.getName() +" " );
				    						}
				    					}				    									    				
				    				}	
				    				variable++;
				    			}
				    			
				    			try{
				    				if(!ref.isContainment()) {
				    					currentEObject.eSet(ref, objectsToLink);
				    					//System.out.println(currentClass.getName()+ " >> "+ ref.getName() +" "+ objectsToLink.size());
				    				}
			    				}catch(Exception e){}
				    			
				    		}						
						}						
					}
					mesInstLiees.add(new ClassInstance(j, currentEObject));						
				}					
			}
		}
		
		//////////////////////////////////////////////
		// STEP 3: containment relations of rootClass
		//////////////////////////////////////////////
		
		variable=0;	
		for(EClass c: cls)
		{
			if(c.getName().equals(root))
			{
				//Skip attributes
				for(EAttribute a:super.reader.getAllAttributesFromClass(c)) variable++;
				
				/////////////////////////////////////////////////
				// Links of rootClass
				/////////////////////////////////////////////////
				for (EReference ref: super.reader.getAllReferencesFromClass(c))	
		    	{
					if(!ref.isChangeable()) {
						//System.out.println("[ROOT] Unchange "+ref.getName());
					}else {
						
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
			   						EObject target = Utils.searchInstanceByClass(allCreatedEObjects, targetedClassName);
			   						
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
			   				for(ClassInstance clInst: allCreatedEObjects)
			   				{
			   					EObject object= clInst.getObj();
			   					EClass classOfObject= ((DynamicEObjectImpl) object).eClass();
			   					String cl =classOfObject.getName();
			   					
			   					if(classOfObject.getEAllSuperTypes().contains(ref.getEType())) {
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
			}
			else
			{
				/////////////////////////////////
				// Other classes
				/////////////////////////////////
				lb=  domaineSum(reader.getClassIndex(c)-1)+1;
				ub=  domaineSum(reader.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{	
						//ses attributs
						for(EAttribute a:reader.getAllAttributesFromClass(c)) variable++;
						//Ses références
						for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c))
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
		//End of STEP 3
		
		System.out.println(" EObject built with success");
		
		return rootObject;
	}
	
	 public void ValidModel(EObject o, int ID)
	 {

		 new File(root).mkdir();
		 ResourceSet resourceSet=new ResourceSetImpl();
		 Resource resource= reader.getModelResource();
		 EPackage pack= reader.getModelPackage();
		 //Resource resource;
		 try
		 {
			 resourceSet = new ResourceSetImpl();
			 resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",new XMIResourceFactoryImpl());
			 URI uri=URI.createURI(root+"/"+this.Model+ID+".xmi");
			 resource=resourceSet.createResource(uri);
			 
			 EList<EObject> ff=resource.getContents();
			 
			 ff.clear();
			 ff.add(o);
			 
			 Map<String,Boolean> opts= new HashMap<String,Boolean>();
			 opts.put(XMLResourceImpl.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			 
			 resource.save(opts); 
			 System.out.println(" Model: "+root+"/"+this.Model+ID+".xmi was generated");
		 }
		 catch(Exception e){
			 e.printStackTrace();
			 System.out.println(" Problems when saving the xmi file");
		 }
	 }
}