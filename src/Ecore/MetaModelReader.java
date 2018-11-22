package Ecore;

import java.util.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.*;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import Ecore2CSP.ConfigFileReader;

public class MetaModelReader {
	
	private Resource resource;
	private EPackage BasePackage;
	private String rootClassName;
	private ArrayList<Integer> sizeOfClasses;
	private ArrayList<Integer> minSizesOfClasses;
	private ConfigFileReader configFileReader;
	private String metamodelFilePath;
	private Hashtable<String,ArrayList<String>> attributesDomains; 
	
	public void loadRootPackage(String metamodel,String rootClass) {
		Resource.Factory.Registry reg=Resource.Factory.Registry.INSTANCE;
		Map<String,Object> m = reg.getExtensionToFactoryMap();
		m.put("ecore",new XMIResourceFactoryImpl());
		ResourceSet resourceSet=new ResourceSetImpl();
		URI fileURI=URI.createFileURI(metamodel);
		Resource resource=resourceSet.getResource(fileURI,true);
		EPackage rootpackage= (EPackage)  resource.getContents().get(0);
		
		this.metamodelFilePath = metamodel;
		this.rootClassName=rootClass;
		this.resource=resource;
		this.BasePackage= rootpackage;
		this.attributesDomains = new Hashtable<String, ArrayList<String>>();
	}
	
	public MetaModelReader(String metamodel,String rootClass){
		loadRootPackage(metamodel, rootClass);
	}
	
	public MetaModelReader(String metamodel,String rootClass, int lb, int ub){
		loadRootPackage(metamodel, rootClass);
		
		sizeClassMinInit(1,lb);
		sizeClassInit(lb,ub);
	}
	
	public MetaModelReader(String metamodel,String rootClass, ConfigFileReader cfr){
		loadRootPackage(metamodel, rootClass);	
		
		this.configFileReader=cfr;
		this.attributesDomains = cfr.getAttributesDomains();
		sizeClassMinRead();
		sizeClassRead();
	}
				
	public ConfigFileReader getConfigFileReader() {
		return configFileReader;
	}

	public String getMetamodel(){
		return metamodelFilePath;
	}

	public String getRootClass() {
		return rootClassName;
	}
	
	private void sizeClassMinRead() {
		
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		
		int i=0;
		
		for (EClass ec : cls){	
			if(ec.getName().equals(rootClassName)) {
				sizes.add(i,1);
			}else {
				sizes.add(i, 0);
			}
			i++;
		}
		this.minSizesOfClasses=sizes;
	}

	private void sizeClassRead() {
		
		List<EClass> cls= getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		Hashtable<String, Integer> classInstanceNb = configFileReader.getClassInstances(); 
		
		int pos=0;
		for (EClass ec:cls){
			sizes.add(0);
			pos++;
		}
		
		pos=0;
		for (EClass ec:cls){
			
			if(ec.getName().equals(rootClassName)) {
				sizes.set(pos, (Integer) 1);			    
			}else {
				int nbInstances = classInstanceNb.get(ec.getName());
				sizes.set(pos,nbInstances);
			}
			pos++;		   
		}
		this.sizeOfClasses=sizes;
	}

	public Resource getModelResource(){
		return this.resource;
	}
	
	public EPackage getModelPackage(){
		return this.BasePackage;
	}
	
	public Hashtable<String, ArrayList<String>> getAttributesDomains() {
		return attributesDomains;
	}

	/**
	 * This method read class sizes and returns the begin of the domain of each class
	 * 
	 * domain D = [a,b]
	 * 
	 * to get a: call the method with classID-1, then add 1
	 * to get b: call the method with classID
	 * 
	 * @param classID
	 * @return
	 */
	public int domaineSum(int classID){
		int end=0;
		if (classID <= 0)
			return 0;
		
		for(int i=0;i<=classID-1;i++){
			end+= sizeOfClasses.get(i);
		}
		return end;
	}
	
	public int domaineSumMin(int classID){
		int end=0;
		if (classID <= 0)
			return 0;
	
		for(int i=0;i<=classID-1;i++){
			end+= minSizesOfClasses.get(i);
		}
		return end;
	}
	
	public List<EClass> getClasses(){
		
		ArrayList<EClass> cls= new ArrayList<EClass>();
		for( EClassifier cf :BasePackage.getEClassifiers()){
			
			if (cf instanceof EClass){		
				if (!((EClass) cf).isAbstract())
					cls.add((EClass) cf);						
			}
		}
		return cls;
	}
	
	public EClass getClassByName(String className) {
		
		for(EClass eclass: getClasses()) {
			if (eclass.getName().equals(className)) {
				return eclass;
			}
		}
		return null;
	}
	
	public List<EClass> getAbtractClasses(){
		
		ArrayList<EClass> cls= new ArrayList<EClass>();
		for( EClassifier cf :BasePackage.getEClassifiers()){
			
			if (cf instanceof EClass){
					if (((EClass) cf).isAbstract())
						cls.add((EClass) cf);										
			}
		}
		return cls;
	}
	
	public int getClassIndex(EClass c){
		
		int i=1;
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		for (EClass cc: cls){
			
			if(cc==c) return i;
			else i++;
		}
		return -1;
	}
	
	public int getClassIndex(String c){
		
		int i=1;
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		for (EClass cc: cls){
			
			if(cc.getName().equals(c))	return i;
			else	i++;
		}
		return -1;
	}
		
	//Méthode initialisant les size(class)
	//Met 1 à la racine: size(racine)= 1
	private void sizeClassInit(int moy,int upperb){
		
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		for (int i=0; i<=cls.size();i++){			
			int random = (int)(Math.random() * (upperb-moy)) + moy;
			sizes.add(i, random);
		}
		sizes.add(getClassIndex(rootClassName)-1,1);
	    this.sizeOfClasses=sizes;
	}
	
	//Init les sizeMin d'une classe
	//Met 0 à la recine, MinSize(Racine)=1
	private void sizeClassMinInit(int lowerb,int moy)
	{
		
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		for (int i=0; i<=cls.size();i++)
		{
			//Générer un size entre 1 et 5
			int random = (int)(Math.random() * (moy-lowerb)) + lowerb;
			sizes.add(i, random);
		}
		sizes.add(getClassIndex(rootClassName)-1,1);
		this.minSizesOfClasses=sizes;
	}
	

	public ArrayList<Integer> getClassSize(){
		return this.sizeOfClasses;
	}
	
	
	public ArrayList<Integer> getClassSizeMin(){
		return this.minSizesOfClasses;
	}
	
	/**
	 * This method collects all the attributes of a Class
	 * It includes also the attributes of all the inheritance tree.
	 * 
	 * Unchangeable attributes are not considered
	 * 
	 * @param c
	 * @return
	 */
	public List<EAttribute> getAllAttributesFromClass(EClass c)
	{
		ArrayList<EAttribute> attributes= new ArrayList<EAttribute>();
		
		for(EAttribute a : c.getEAllAttributes()) {
			if(a.isChangeable()) {
				attributes.add(a);
			}
		}
		return attributes;
	}
	
	public List<EClass> getAllSubtypes(EClass c)
	{
		ArrayList<EClass> allClasses= new ArrayList<EClass>();
		for(EClass subClass: getClasses()){
			
			if(subClass.getEAllSuperTypes().contains(c))
			allClasses.add(subClass);
		}
		return allClasses;
	}
	
	/**
	 * This method collects all the references of a given class.
	 * It includes also the references of superClasses of c.
	 * 
	 * Unchangeable and containments are not considered
	 * 
	 * @param c
	 * @return
	 */
	public List<EReference> getAllReferencesFromClass(EClass c){
		ArrayList<EReference> references= new ArrayList<EReference>();
		for (EReference r: c.getEAllReferences()){
			if (r.isChangeable() && !r.isContainment()){
				references.add(r);
			}
		}
		return references;
    }
	
	/**
	 * This method collects the containment relation of a given class
	 * 
	 * unchangeable references are not considered
	 * 
	 * @param c
	 * @return
	 */
	public List<EReference> getAllContainmentFromClass(EClass c){
		ArrayList<EReference> containments= new ArrayList<EReference>();
		for (EReference r: c.getEAllReferences()){
			if (r.isChangeable() && r.isContainment()){
				containments.add(r);
			}
		}
		return containments;
	}
	
	/**
	 * This method is collecting the references of a class.
	 * If a reference has not an EOpposite then it is added.
	 * If it has then the reference with the smallest bound is added
	 * 
	 * @param c
	 * @return
	 */
	public List<EReference> getAllReferencesFromClasswithOpposite(EClass c)
	{
		ArrayList<EReference> references= new ArrayList<EReference>();
		for (EReference ref: getAllReferencesFromClass(c)){
			
			if(ref.getEOpposite()==null){
				references.add(ref);
			}
			else{
				
				int bound=ref.getUpperBound();
				int oppositeBound = ref.getEOpposite().getUpperBound();
				
				if (bound == -1) {
					bound=100;			
				}
				
				if (oppositeBound == -1) { 
					oppositeBound=100;
				}
				
				if(bound < oppositeBound){
					references.add(ref);
				}
				else if(oppositeBound==bound)
				{
					if(ref.getName().codePointCount(0, ref.getName().length()) < ref.getEOpposite().getName().codePointCount(0, ref.getEOpposite().getName().length()))
					{
						references.add(ref);
					}
				}
			}
		}
		return references;
    }
	
	public List<EClass> getConcreteSubTypes(EClass c) 
	{

			ArrayList<EClass> cls= new ArrayList<EClass>();
			for(EClass cc: getClasses())
			{
				if (!cc.isAbstract())
				{
				if(cc.getEAllSuperTypes().contains(c))	
				cls.add(cc);
				}
			}
			return cls;
	}

	public EPackage getBasePackage() {
		return BasePackage;
	}		
}
