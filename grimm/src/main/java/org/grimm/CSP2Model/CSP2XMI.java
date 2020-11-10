package org.grimm.CSP2Model;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import org.grimm.utils.AttributeInstantiator;
import org.grimm.utils.ClassInstance;
import org.grimm.utils.Utils;

public class CSP2XMI extends ModelBuilder{
	
	private EObject rootObject = null;
	private ArrayList<Integer> containedOIDs = new ArrayList<Integer>();
	
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
	
	public CSP2XMI(String ModelFile, String racine,String InstanceFile, String oclFilePath, String modelFile) {
		super(ModelFile, racine,InstanceFile,oclFilePath, modelFile);
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
	 * @throws Exception 
	 */
	public void generateModel(String configFilePath, int sym, int numberOfSolutions, boolean chr) throws Exception{
		super.CallCSPGenrator(configFilePath, sym, numberOfSolutions);
		Solutions2Models(chr);
	}
	
	public void Solutions2Models(boolean chr) throws Exception {
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
			
			if(chr) {
				if (this.configfilereader != null) {
					CSP2CHR(solution.getValues(), this.modelFilePath+ID);
				}
				else {
					System.out.println("\t[Problem] cannot generate chr for quick parameters mode, use config file !");
				}
			}
		}
	}
	
	public EObject instantiateAttributes(EObject currentobject, EClass currentclass, int OID) {
		
		String currentclassname = currentclass.getName();
		
		for(EAttribute a: reader.getAllAttributesFromClass(currentclass))
		{
			if(a.isChangeable()) {
				
				if(a.getEType().getName().equals("EBoolean")) {
					Boolean bool = AttributeInstantiator.generateBoolean();
					currentobject.eSet(a, bool);
				
				}else if(a.getEType().getName().equals("EString")) {
					if(a.getName().toLowerCase().equals("name")) {
						String value = ""+OID;
						if(!reader.getAttributesDomains().containsKey(currentclassname+"/"+a.getName())) {
							
							value = AttributeInstantiator.generateBasicName(currentclassname, OID);							
						}else {
							
							ArrayList<String> customDomain = reader.getAttributesDomains().
									get(currentclassname+"/"+a.getName());
							value= AttributeInstantiator.chooseString(customDomain,OID);
						}
						currentobject.eSet(a, value);
					}
					else {
						String value = "";
						if(!reader.getAttributesDomains().containsKey(currentclassname+"/"+a.getName())) {							
							value= AttributeInstantiator.randomString();							
						}else {
							ArrayList<String> customDomain = reader.getAttributesDomains().
									get(currentclassname+"/"+a.getName());
							if(customDomain.get(0).equals("n")) {
									value = AttributeInstantiator.generateBasicName(currentclassname, OID);
							}else {
								value= AttributeInstantiator.chooseString(customDomain,OID);
							}
						}
						currentobject.eSet(a, value);
					}
			    }else if (a.getEType().getName().equals("EInt")) {
			    	int value=0;
			    	if(!reader.getAttributesDomains().containsKey(currentclassname+"/"+a.getName())) {						
			    		
			    		value =  AttributeInstantiator.randomInt(0,100);			    		
			    	}else {
			    		
			    		ArrayList<String> customDomain = reader.getAttributesDomains().get(currentclassname+"/"+a.getName());
			    		
			    		//Check if the custom domain is in interval(=i) or a list(=l) for EInt attribute
			    		if(customDomain.get(0).equals("i")) {
			    			
			    			int begin = Integer.parseInt(customDomain.get(1));
			    			int end = Integer.parseInt(customDomain.get(2));
			    			value = AttributeInstantiator.randomInt(begin, end);
			    		}else if(customDomain.get(0).equals("l")) {
			    		
			    			value = AttributeInstantiator.chooseInteger(customDomain);
			    		}else if(customDomain.get(0).equals("n")) {
			    			value = OID;
			    		}
			    	}
			    	currentobject.eSet(a, value);
				}
				else if (a.getEType().eClass().getName().equals("EEnum")){
									
					EEnum enume= (EEnum) a.getEType();
					if (enume.getELiterals() != null) {
						int numberOfLiterals = enume.getELiterals().size();
						int value =  AttributeInstantiator.randomInt(0, numberOfLiterals);	
						currentobject.eSet(a, enume.getELiterals().get(value));
					}										
				}else {
					System.out.println("\t[WARNING] Class "+ currentclassname+ " Attribute "+ a.getName() +"  type not supported: "+a.getEType().getName());
				}
			}			
		}
		return currentobject;
	} 
	
	public void createEObjects(ArrayList<Integer> solutionValues) {
		
		EPackage rootPackage= super.reader.getModelPackage();
		List<EClass> cls= super.reader.getClasses();
		
		for(EClass c: cls)
		{
			String currentClassName= c.getName();
			if(currentClassName.equals(root))		
			{
				//Create instance of rootClass
				rootObject= rootPackage.getEFactoryInstance().create(c);
				
				int rootObjectOID=  reader.domaineSum(reader.getClassIndex(c)-1)+1;
				
				/////////////////////////////////////
				//   Attributes of rootClass
				////////////////////////////////////
				rootObject = instantiateAttributes(rootObject, c, rootObjectOID);				
			}
			else
			{
				////////////////////////////////////////////
				// Create instances of other classes
				////////////////////////////////////////////
				int classDomainBegin=  reader.domaineSum(reader.getClassIndex(c)-1)+1;
				int classDomainEnd=  reader.domaineSum(reader.getClassIndex(c)); 
								
				for(int OID=classDomainBegin;OID<=classDomainEnd;OID++){
					///////////////////////////////////////
					// Create the EObject
					///////////////////////////////////////
					EObject createdObject;
					createdObject= rootPackage.getEFactoryInstance().create(c);
					
					///////////////////////////////////////
					// Set values for attributes
					///////////////////////////////////////
					createdObject = instantiateAttributes(createdObject, c, OID);
					
					//Add current EObject to list of all instances 
					allCreatedEObjects.add(new ClassInstance(OID, createdObject));
				}
			}
		} 
	}
	
	public void createReferenceLinks(ArrayList<Integer> solutionValues) {
		//init currentVar to 1 in order to skip first variable
		int currentVar=1;
		
		List<EClass> cls= super.reader.getClasses();
		ArrayList<ClassInstance> linkedInstances= new ArrayList<ClassInstance>(); 
		
		for(EClass currentClass: cls){
			if(currentClass.getName().equals(root)){
				
				//Skip references of rootClass
				for (EReference ref: reader.getAllReferencesFromClasswithOpposite(currentClass)){
					
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
				int classDomBegin=  reader.domaineSum(reader.getClassIndex(currentClass)-1)+1;
				int classDomEnd=  reader.domaineSum(reader.getClassIndex(currentClass)); 
				
				for(int objectOID=classDomBegin;objectOID<=classDomEnd;objectOID++)
				{
					EObject currentEObject = Utils.searchIns(allCreatedEObjects, objectOID);
					
					for (EReference ref: reader.getAllReferencesFromClasswithOpposite(currentClass))	
					{
						int refUpperBound=ref.getUpperBound();
			    		
						if (refUpperBound==-1) {
	    					if(ref.getEReferenceType().isAbstract()) refUpperBound=referenceUpperBound;
	    					else refUpperBound=referenceUpperBound;
	    				}
						
			    		EClass targetClass= ref.getEReferenceType();
			    		List<EObject> objectsToLink= new ArrayList<EObject>(); 
			    		
			    		if(ref.getUpperBound()==1){
			    			if(solutionValues.get(currentVar)!=0)
			    			{
			    				try{
		    						ClassInstance targetInstance = Utils.searchInstanceByClass(allCreatedEObjects, targetClass);
			    					
		    						if(targetInstance != null) {
		    							
		    							EObject targetEObject= targetInstance.getObj();
				    					int targetOID = targetInstance.getId();
			    						EClass targetEObjectClass= ((DynamicEObjectImpl) targetEObject).eClass();
				    					
		    							if(containedOIDs.contains(targetOID) && containedOIDs.contains(objectOID)) {
			    							if(targetEObjectClass.equals(targetClass) ||
	 		    									targetEObjectClass.getEAllSuperTypes().contains(targetClass)) {
			    								currentEObject.eSet(ref, targetEObject);
			    								
			    							}
		    							}
		    						}				    										    				
			    				}catch(Exception e){}
			    			}
			    			currentVar++;
			    		}
			    		else{
			    			int z=0;
			    			for(z=1;z<=refUpperBound;z++){
			    				
			    				if(solutionValues.get(currentVar)!=0){
			    					
			    					ClassInstance targetInstance = Utils.searchInstanceByClass(allCreatedEObjects, targetClass);
			    					
			    					if(targetInstance!=null) {
			    					
			    						EObject targetEObject= targetInstance.getObj();
				    					int targetOID = targetInstance.getId();
			    						EClass targetEObjectClass= ((DynamicEObjectImpl) targetEObject).eClass();
				    					
			    						if(containedOIDs.contains(targetOID) && containedOIDs.contains(objectOID)) {
				    						if(targetEObjectClass.equals(targetClass) ||
				    								targetEObjectClass.getEAllSuperTypes().contains(targetClass)) {
				    							objectsToLink.add(targetEObject);
				    							
				    						}
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
				
				int oidStart=  reader.domaineSum(reader.getClassIndex(currentClass)-1)+1;
				int oidEnd=  reader.domaineSum(reader.getClassIndex(currentClass)); 
				
				
				for(int currentOID=oidStart; currentOID<=oidEnd; currentOID++){
					
					EObject currentEObject = Utils.searchIns(allCreatedEObjects, currentOID);
					
					
					for(EReference ref: reader.getAllContainmentFromClass(currentClass)) {
						
						int minLinks= ref.getLowerBound();
						int maxLinks= ref.getUpperBound();
						if(maxLinks == -1) {
							maxLinks = referenceUpperBound;
						}
						
						Random random= new SecureRandom();
						int actualLinks = 0;
						if(minLinks == maxLinks) {
							actualLinks = maxLinks;
						}else {
							actualLinks =  random.nextInt(maxLinks-minLinks) + minLinks;
						}
						
						
						EClass targetClass= ref.getEReferenceType();
						ArrayList<ClassInstance> candidatesObject= Utils.findAllinstancesOfClass(allCreatedEObjects, targetClass);
						
						
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
		
		
		containedOIDs.addAll(oidUsedInContainment);
	}
	
	public EObject CSP2XMIBuild(ArrayList<Integer> solutionValues){
		
		allCreatedEObjects.clear();
		containedOIDs.clear();
		
		/////////////////////////////////////////////
		// STEP 1 : Create all EObjects
		////////////////////////////////////////////
		createEObjects(solutionValues);
				
		//////////////////////////////////////////////
		// STEP 4 : containment relations of rootClass
		//////////////////////////////////////////////
		EClass rootClass = reader.getClassByName(root);
		
		for(EReference ref: reader.getAllContainmentFromClass(rootClass)) {
			
			int refUpperBound=ref.getUpperBound();
			if (refUpperBound==-1){	
				refUpperBound=referenceUpperBound;				
			}
			
			if(ref.getUpperBound() == 1){
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

		/////////////////////////////////////////////////////////////
		// STEP 3 : containment relations of non-root Classes
		/////////////////////////////////////////////////////////////
		createNonRootCompositions();
		
		///////////////////////////////////////////
		// STEP 2: Create pointers for references
		///////////////////////////////////////////
		createReferenceLinks(solutionValues);
		
		
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
		for(ClassInstance clInst: allCreatedEObjects){
			
			EObject object= clInst.getObj();
			int containedOID = clInst.getId();
			EClass classOfObject= ((DynamicEObjectImpl) object).eClass();
			
			if(classOfObject.equals(containment.getEType()) ||
				classOfObject.getEAllSuperTypes().contains(containment.getEType())) {
				objectstoCompose.add(object);
				containedOIDs.add(containedOID);
			}
		}
		
		try{
			container.eSet(containment, objectstoCompose);			
		}
		catch(Exception e){
			System.out.println("Containment Error: "+containment.getName());
		}
		
		return container;
	}
		
	/**
	 * This method creates an XMI file from a given EObject
	 * @param rootObject
	 * @param ID
	 */
	public void generateXMIfile(EObject rootObject, int ID){

		 new File(root).mkdir();
		 ResourceSet resourceSet=new ResourceSetImpl();
		 Resource resource= reader.getModelResource();
		 
		 try{
			 resourceSet = new ResourceSetImpl();
			 resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",new XMIResourceFactoryImpl());
			 URI uri=URI.createURI(this.modelFilePath+ID+".xmi");
			 resource=resourceSet.createResource(uri);
			 
			 resource.getContents().add(rootObject);
			 
			 Map<String,Boolean> opts= new HashMap<String,Boolean>();
			 opts.put(XMLResourceImpl.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			 
			 resource.save(opts); 
			 System.out.println("\t[OK] Model: "+this.modelFilePath+ID+".xmi was generated");
		 }
		 catch(Exception e){
			 e.printStackTrace();
			 System.out.println("[PROBLEM] xmi file could not be saved :(");
		 }
	 }
}