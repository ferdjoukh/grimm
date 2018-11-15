package CSP2Model;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import Utils.AttributeInstantiator;
import Utils.ClassInstance;
import Utils.Utils;

public class CSP2XMI extends ModelBuilder{
	
	private EObject rootObject = null;
	
	/**
	 * 
	 * @param ModelFile: .ecore file
	 * @param root: root class name 
	 * @param InstanceFile: CSP instance file .xml
	 * @param Model: Result model file path .xmi
	 * @param oclFilePath: ocl file path .ocl or empty string.
	 */
	public CSP2XMI(String ModelFile, String root,String CSPInstanceFile,String oclFilePath){
			
		super(ModelFile, root, CSPInstanceFile, oclFilePath);
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
			System.out.println(" 0 solutions were found :(");
			return;
		}
		
		int ID=0;
		
		for(FoundSolution solution: foundSolutions) {
			ID++;
			
			EObject object= CSP2XMIBuild(solution.getValues());
			generateXMIfile(object,ID);
		}
	}
	
	public void createEObjects(ArrayList<Integer> solutionValues) {
		
		int currentVar = 0;
		
		EPackage rootPackage= super.reader.getModelPackage();
		List<EClass> cls= super.reader.getClasses();
		
		for(EClass c: cls)
		{
			String currentClassName= c.getName();
			if(currentClassName.equals(root))		
			{
				//Create instance of rootClass
				rootObject= rootPackage.getEFactoryInstance().create(c);
				
				/////////////////////////////////////
				//   Attributes of rootClass
				////////////////////////////////////
				for(EAttribute a:super.reader.getAllAttributesFromClass(c))
				{
					if(a.isChangeable()) {
						
						if(a.getEType().getName().equals("EBoolean")) {
							Boolean bool = AttributeInstantiator.generateBoolean();
							rootObject.eSet(a, bool);
						
						}else if(a.getEType().getName().equals("EString")) {
							if(a.getName().toLowerCase().equals("name")) {
								String nameValue = AttributeInstantiator.generateBasicName(currentClassName, 1);
								rootObject.eSet(a, nameValue);
							}
							else {
								String value= AttributeInstantiator.randomString();
								rootObject.eSet(a, value);
							}
						
						}else if (a.getEType().getName().equals("EInt")) {
							int value =  AttributeInstantiator.randomInt(0,100);
							rootObject.eSet(a, value);	
						} else{
							//Enumeration type
					    	EEnum enume= null;
							try{enume=(EEnum) a.getEType();} catch(Exception e){}
							EClass etype=null;
							try{etype=(EClass) a.getEType();} catch(Exception e){}				
							if(enume!=null)
							{
								rootObject.eSet(a, enume.getEEnumLiteral(solutionValues.get(currentVar)-1));
							}
							if(etype!=null)
								System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
						}
					}
					currentVar++;
				}
				
				/////////////////////////////////////////////////////
				// At this step, skip links of rootClass instance
				/////////////////////////////////////////////////////
				for (EReference ref: super.reader.getAllReferencesFromClasswithOpposite(c))	    		
				{
					int zz=ref.getUpperBound();
					if (zz==-1){	
						if(!ref.getEReferenceType().isAbstract())
							zz=referenceUpperBound;
						else
							zz=referenceUpperBound;
					}
					EClass dst= ref.getEReferenceType();
					ArrayList<EObject> rr= new ArrayList<EObject>(); 
					for(int z=1;z<=zz;z++)
					{
						currentVar++;
					}
    			}
			}
			else
			{
				////////////////////////////////////////////
				// Create instances of other classes
				////////////////////////////////////////////
				int classDomainBegin=  domaineSum(reader.getClassIndex(c)-1)+1;
				int classDomainEnd=  domaineSum(reader.getClassIndex(c)); 
				
				for(int currentInstance=classDomainBegin;currentInstance<=classDomainEnd;currentInstance++)
				{
					///////////////////////////////////////
					// Create the EObject
					///////////////////////////////////////
					EObject createdObject;
					createdObject= rootPackage.getEFactoryInstance().create(c);
					
					
					///////////////////////////////////////
					// Set values for attributes
					///////////////////////////////////////
					for(EAttribute a:super.reader.getAllAttributesFromClass(c))
					{
						if(a.isChangeable()) {
						    
							if(a.getEType().getName().equals("EBoolean")) {
								Boolean bool = AttributeInstantiator.generateBoolean();
								createdObject.eSet(a, bool);
							
							}else if(a.getEType().getName().equals("EString")) {
								if(a.getName().toLowerCase().equals("name")) {
									String nameValue = AttributeInstantiator.generateBasicName(currentClassName, currentInstance);
									createdObject.eSet(a, nameValue);
								}
								else {
									String value= AttributeInstantiator.randomString();
									createdObject.eSet(a, value);
								}
						    }else if (a.getEType().getName().equals("EInt")) {
								int value =  AttributeInstantiator.randomInt(0,100);
								createdObject.eSet(a, value);	
							}
							else{
								// Enumeration
								EEnum enume= null;
								try{enume=(EEnum) a.getEType();} catch(Exception e){}
								EClass etype=null;
								try{etype=(EClass) a.getEType();} catch(Exception e){}				
								if(enume!=null)
								{
									createdObject.eSet(a, enume.getEEnumLiteral(solutionValues.get(currentVar)-1));
								}
								if(etype!=null)
									System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
							}
						}
						currentVar++;
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
	    						zz=referenceUpperBound;
	    					else
	    						zz=referenceUpperBound;
	    				}
	    				for(int z=1;z<=zz;z++) {currentVar++;}
	    			}
					
					//Add current EObject to list of all instances 
					allCreatedEObjects.add(new ClassInstance(currentInstance, createdObject));
				}
			}
		} 
	}
	
	public void createReferenceLinks(ArrayList<Integer> solutionValues) {
		int currentVar=0;
		List<EClass> cls= super.reader.getClasses();
		ArrayList<ClassInstance> linkedInstances= new ArrayList<ClassInstance>(); 
		
		for(EClass currentClass: cls)
		{
			if(currentClass.getName().equals(root))
			{
				//Skip attributes of rootClass
				for(EAttribute a:reader.getAllAttributesFromClass(currentClass)) currentVar++;		
				
				//Skip references of rootClass
				for (EReference ref: reader.getAllReferencesFromClasswithOpposite(currentClass))	
				{
					int zz=ref.getUpperBound();
		    	    if (zz==-1){	
						if(ref.getEReferenceType().isAbstract()) zz=referenceUpperBound;
						else zz=referenceUpperBound;
					}
		   			for(int z=1;z<=zz;z++) {currentVar++;}
		    	}
			}
			else
			{
				////////////////////////////////////////
				// Create links of other classes
				////////////////////////////////////////
				int classDomBegin=  domaineSum(reader.getClassIndex(currentClass)-1)+1;
				int classDomEnd=  domaineSum(reader.getClassIndex(currentClass)); 
				
				for(int objectOID=classDomBegin;objectOID<=classDomEnd;objectOID++)
				{
					EObject currentEObject = Utils.searchIns(allCreatedEObjects, objectOID);
					
					//Skip attributes
					for(EAttribute a:reader.getAllAttributesFromClass(currentClass)) currentVar++;
					
					for (EReference ref: reader.getAllReferencesFromClasswithOpposite(currentClass))	
					{
						int refUpperBound=ref.getUpperBound();
			    		
						if (refUpperBound==-1) {
	    					if(ref.getEReferenceType().isAbstract()) refUpperBound=referenceUpperBound;
	    					else refUpperBound=referenceUpperBound;
	    				}
						
			    		EClass targetClass= ref.getEReferenceType();
			    		List<EObject> objectsToLink= new ArrayList<EObject>(); 
			    		
			    		if(refUpperBound==1){
			    			if(solutionValues.get(currentVar)!=0)
			    			{
			    				try{
			    						EObject targetEObject= Utils.searchInstanceByClass(allCreatedEObjects, targetClass).getObj();
			    						
			    						if(targetEObject != null) {
			    							
			    							EClass targetEObjectClass= ((DynamicEObjectImpl) targetEObject).eClass();
					    					
			    							if(targetEObjectClass.getEAllSuperTypes().contains(targetClass)) {
			    								currentEObject.eSet(ref, targetEObject);				    								
			    							}
			    						}				    										    				
			    				}catch(Exception e){}
			    			}
			    			currentVar++;
			    		}
			    		else{
			    			int z=0;
			    			for(z=1;z<=refUpperBound;z++)
			    			{
			    				if(solutionValues.get(currentVar)!=0)
			    				{
			    					EObject targetEObject= Utils.searchInstanceByClass(allCreatedEObjects, targetClass).getObj();
			    					
			    					if(targetEObject!=null) {
			    						EClass targetEObjectClass= ((DynamicEObjectImpl) targetEObject).eClass();
				    					
			    						if(targetEObjectClass.getEAllSuperTypes().contains(targetClass)) {
			    							objectsToLink.add(targetEObject);				    							
			    						}
			    					}				    									    				
			    				}	
			    				currentVar++;
			    			}
			    			
			    			try{
			    				if(!ref.isContainment()) {
			    					currentEObject.eSet(ref, objectsToLink);				    					
			    				}
		    				}catch(Exception e){}
			    			
			    		}						
												
					}
					linkedInstances.add(new ClassInstance(objectOID, currentEObject));						
				}					
			}
		}
	}
	
	public void createNonRootCompositions() {
		ArrayList<Integer> oidUsedInContainment= new ArrayList<Integer>();
		
		for(EClass currentClass: reader.getClasses()) {
			
			if(!currentClass.getName().equals(root)) {
				
				int oidStart=  domaineSum(reader.getClassIndex(currentClass)-1)+1;
				int oidEnd=  domaineSum(reader.getClassIndex(currentClass)); 
				
				for(int currentOID=oidStart; currentOID<=oidEnd; currentOID++){
					
					EObject currentEObject = Utils.searchIns(allCreatedEObjects, currentOID);
					
					for(EReference ref: reader.getAllContainmentFromClass(currentClass)) {
						
						int minLinks= ref.getLowerBound();
						int maxLinks= ref.getUpperBound();
						if(maxLinks == -1) {
							maxLinks = referenceUpperBound;
						}
						
						Random random= new SecureRandom();
						int actualLinks =  random.nextInt(maxLinks-minLinks) + minLinks;
						
						EClass targetClass= ref.getEReferenceType();
						ArrayList<ClassInstance> candidatesObject= Utils.findAllinstancesOfClass(allCreatedEObjects, targetClass);
						
						System.out.println("  "+currentClass.getName());
						System.out.println("    "+ ref.getName());
						System.out.println("       "+targetClass.getName()+" "+ candidatesObject.size());
						System.out.println("       ["+ minLinks +" < "+ actualLinks + " < " + maxLinks +"]");
					
						//Do something only if actualLinks > 0
						if( actualLinks > 0) {
							if(ref.getUpperBound() == 1) {
								ClassInstance instance = Utils.searchInstanceByClass(candidatesObject, targetClass, oidUsedInContainment);
								
								if(instance != null) {
									try{
										currentEObject.eSet(ref, instance.getObj());
										oidUsedInContainment.add(instance.getId());
									}catch (Exception e) {
										System.out.println("PROBLEM COMPO class:"+currentClass.getName()+" ref"+ ref.getName());
									}
								}
							}else {
								List<EObject> objectstoCompose= new ArrayList<EObject>();
								
								for(int i=1; i<= actualLinks; i++) {
									ClassInstance instance = Utils.searchInstanceByClass(candidatesObject, targetClass, oidUsedInContainment);									
									if(instance != null) {
										objectstoCompose.add(instance.getObj());
										oidUsedInContainment.add(instance.getId());
									}
								}
								
								try {
									currentEObject.eSet(ref, objectstoCompose);
								}catch (Exception e) {
									 System.out.println("PROBLEM COMPO class:"+currentClass.getName()+" ref"+ ref.getName());
								}
							}
						}
					}
				}
			}
		}
	}
	
	public EObject CSP2XMIBuild(ArrayList<Integer> solutionValues)
	{	
		/////////////////////////////////////////////
		// STEP 1 : Create all EObjects
		////////////////////////////////////////////
		createEObjects(solutionValues);
		
		///////////////////////////////////////////
		// STEP 2: Create pointers for references
		///////////////////////////////////////////
		createReferenceLinks(solutionValues);
		
		/////////////////////////////////////////////////////////////
		// STEP 3 : containment relations of non-root Classes
		/////////////////////////////////////////////////////////////
		createNonRootCompositions();
		
		//////////////////////////////////////////////
		// STEP 4 : containment relations of rootClass
		//////////////////////////////////////////////
		EClass rootClass = reader.getClassByName(root);
		
		for(EReference ref: reader.getAllContainmentFromClass(rootClass)) {
			
			int refUpperBound=ref.getUpperBound();
			if (refUpperBound==-1){	
				refUpperBound=referenceUpperBound;				
			}
			
			if(refUpperBound==1){
   				try {
   						String targetedClassName=ref.getEType().getName();
   						EObject target = Utils.searchInstanceByClass(allCreatedEObjects, targetedClassName).getObj();
   						
   						if(target!=null) {
   							rootObject.eSet(ref, target);			   										   											   							
   						}
   					}
   					catch(Exception e) {
   						System.out.println("Class:"+rootClass.getName()+" Ref:"+ref.getName()+ " 1 component add error !");
   					}   				
   			}else {
   				rootObject = setRootContainment(rootObject, ref);
   			}	
		}
		
		System.out.println("\t[OK] Model EObject built with success");
		return rootObject;
	}
	
	/**
	 * This method will set the containments between rootClass and EObject for given reference
	 * 
	 * @param container
	 * @param containment
	 */
	public EObject setRootContainment(EObject container, EReference containment) {
		
		List<EObject> objectstoCompose= new ArrayList<EObject>(); 
		
		//Add the appropriate instances for each reference of rootClass
		for(ClassInstance clInst: allCreatedEObjects)
		{
			EObject object= clInst.getObj();
			EClass classOfObject= ((DynamicEObjectImpl) object).eClass();
			
			if(classOfObject.getEAllSuperTypes().contains(containment.getEType())) {
				objectstoCompose.add(object);
			}
		}
		
		try{
			container.eSet(containment, objectstoCompose);
		}
		catch(Exception e){
			System.out.println("Containment Error: "+containment.getName());
		}
		finally {
			return container;
		}
		
	}
		
	/**
	 * This method creates an XMI file from a given EObject
	 * @param rootObject
	 * @param ID
	 */
	public void generateXMIfile(EObject rootObject, int ID)
	 {

		 new File(root).mkdir();
		 ResourceSet resourceSet=new ResourceSetImpl();
		 Resource resource= reader.getModelResource();
		 
		 try
		 {
			 resourceSet = new ResourceSetImpl();
			 resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",new XMIResourceFactoryImpl());
			 URI uri=URI.createURI(root+"/"+this.modelFilePath+ID+".xmi");
			 resource=resourceSet.createResource(uri);
			 
			 resource.getContents().add(rootObject);
			 
			 Map<String,Boolean> opts= new HashMap<String,Boolean>();
			 opts.put(XMLResourceImpl.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			 
			 resource.save(opts); 
			 System.out.println("\t[OK] Model: "+root+"/"+this.modelFilePath+ID+".xmi was generated");
		 }
		 catch(Exception e){
			 e.printStackTrace();
			 System.out.println("[PROBLEM] xmi file could not be saved :(");
		 }
	 }
}