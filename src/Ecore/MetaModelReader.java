package Ecore;

import java.util.*;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.*;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import Ecore2CSP.ConfigFileReader;

public class MetaModelReader {

	/**
	 * @param args
	 */
	Resource resource;
	EPackage BasePackage;
	String rootClassName;
	ArrayList<Integer> sizes;
	ArrayList<Integer> sizesMin;
	ConfigFileReader cfr;
	
	public MetaModelReader(String metamodel,String rootClass){
		
		 Resource.Factory.Registry reg=Resource.Factory.Registry.INSTANCE;
		 Map<String,Object> m = reg.getExtensionToFactoryMap();
		 m.put("ecore",new XMIResourceFactoryImpl());
		 ResourceSet resourceSet=new ResourceSetImpl();
		 URI fileURI=URI.createFileURI(metamodel);
		 Resource resource=resourceSet.getResource(fileURI,true);
		
		this.resource=resource;
		
		EPackage c= (EPackage)  resource.getContents().get(0);
		
		this.BasePackage= c;
		this.rootClassName=rootClass;		
	}
	
	public MetaModelReader(String str,String racine,int lb,int ub){
		
		Resource.Factory.Registry reg=Resource.Factory.Registry.INSTANCE;
		Map<String,Object> m = reg.getExtensionToFactoryMap();
		m.put("ecore",new XMIResourceFactoryImpl());
		ResourceSet resourceSet=new ResourceSetImpl();
		URI fileURI=URI.createFileURI(str);
		Resource resource=resourceSet.getResource(fileURI,true);
				
		this.resource=resource;
				
		EPackage c= (EPackage)  resource.getContents().get(0);
				
		this.BasePackage= c;
		this.rootClassName=racine;
				
		sizeClassInit(lb,ub);
		sizeClassMinInit(1,lb);
	}
	
	public MetaModelReader(String str,String racine, ConfigFileReader cfr){
		
	this.cfr=cfr;
		
	Resource.Factory.Registry reg=Resource.Factory.Registry.INSTANCE;
	Map<String,Object> m = reg.getExtensionToFactoryMap();
	m.put("ecore",new XMIResourceFactoryImpl());
	ResourceSet resourceSet=new ResourceSetImpl();
	URI fileURI=URI.createFileURI(str);
	Resource resource=resourceSet.getResource(fileURI,true);
		
	this.resource=resource;
	
	EPackage c= (EPackage) resource.getContents().get(0);
		
	this.BasePackage= c;
	this.rootClassName=racine;
		
	sizeClassRead();
	sizeClassMinRead();
	}
	
	private void sizeClassMinRead() {
		
		System.out.println("set Class MIN sizes from Config file");
				
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		
		int i=0;
		
		for (EClass ec : cls){	
			sizes.add(i, 0);
			i++;
		}
		this.sizesMin=sizes;
	}

	private void sizeClassRead() {
		
		System.out.println("set Class sizes from Config file");
		
		List<EClass> cls= getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		
		int pos=0;
		
		for (EClass ec:cls){
			
			if(ec.getName().equals(rootClassName)) {
				sizes.add(pos, (Integer) 1);			    
			}else {
				String str =  cfr.getLineByStarting(ec.getName());
				sizes.add(pos, Integer.parseInt(str.substring(str.lastIndexOf("=")+1)));
			}
			pos++;		   
		}
		
		
		this.sizes=sizes;
	}

	public Resource getModelResource(){
		return this.resource;
	}
	
	public EPackage getModelPackage(){
		return this.BasePackage;
	}
	
	public List<EClass> getClasses()
	{
		ArrayList<EClass> cls= new ArrayList<EClass>();
		for( EClassifier cf :BasePackage.getEClassifiers())
		{
			if (cf instanceof EClass)
			{
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
	
	public List<EClass> getAbtractClasses()
	{
		ArrayList<EClass> cls= new ArrayList<EClass>();
		for( EClassifier cf :BasePackage.getEClassifiers())
		{
			if (cf instanceof EClass)
			{
					if (((EClass) cf).isAbstract())
						cls.add((EClass) cf);										
			}
		}
		
		
		return cls;
	}
	
	public int getClassIndex(EClass c)
	{
		int i=1;
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		for (EClass cc: cls)
		{
			if(cc==c)
				return i;
			else
				i++;
		}
		return -1;
	}
	
	public int getClassIndex(String c)
	{
		int i=1;
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		for (EClass cc: cls)
		{
			if(cc.getName().equals(c))
				return i;
			else
				i++;
		}
		return -1;
	}
		
	//Méthode initialisant les size(class)
	//Met 1 à la racine: size(racine)= 1
	private void sizeClassInit(int moy,int upperb)
	{
		
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		for (int i=0; i<=cls.size();i++)
		{
			//Générer un size entre 5 et 10
			int random = (int)(Math.random() * (upperb-moy)) + moy;
			sizes.add(i, random);
		}
		sizes.add(getClassIndex(rootClassName)-1,1);
	    this.sizes=sizes;
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
		this.sizesMin=sizes;
	}
	

	public ArrayList<Integer> getClassSize(){
		return this.sizes;
	}
	
	
	public ArrayList<Integer> getClassSizeMin(){
		return this.sizesMin;
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
