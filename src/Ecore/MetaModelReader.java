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
	String racine;
	ArrayList<Integer> sizesMin;
	ArrayList<Integer> sizes;
	ConfigFileReader cfr;
	
	
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
		this.racine=racine;
		
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
		this.racine=racine;
		
		sizeClassRead();
		sizeClassMinRead();
	}
	
	private void sizeClassMinRead() {
		// TODO Auto-generated method stub
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		
		sizes.add(getClassIndex(racine)-1,1);
	    
		int i=1;
		
		for (EClass ec:cls)
		{	
			sizes.add(i, 1);
			i++;
		}
		this.sizesMin=sizes;
	}

	private void sizeClassRead() {
		// TODO Auto-generated method stub
		ArrayList<EClass> cls= (ArrayList<EClass>) getClasses();
		ArrayList<Integer> sizes= new ArrayList<Integer>(cls.size());
		ArrayList<String> content = cfr.getContent();
		
		System.out.println(cls.size());
		
		System.out.println(racine+" index="+getClassIndex(racine));
		
		sizes.add(getClassIndex(racine)-1,1);
	    
		int i=0;
		String str;
		
		for (EClass ec:cls)
		{	
			if(i!=0)
			{
				str = content.get(getClassIndex(ec)-2);
				//System.out.println("Classe dans content"+ec.getName()+ "  -  "+str);
				sizes.add(i, Integer.parseInt(str.substring(str.lastIndexOf("=")+1)));
		    }
			i++;
		   
		}
		this.sizes=sizes;
	}

	public Resource getModelResource()
	{
		return this.resource;
	}
	
	public EPackage getModelPackage()
	{
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
		return (Integer) null;
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
		System.out.println(c);
		return (Integer) null;
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
		sizes.add(getClassIndex(racine)-1,1);
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
		sizes.add(getClassIndex(racine)-1,1);
		this.sizesMin=sizes;
	}
	

	public ArrayList<Integer> getClassSize(){
		return this.sizes;
	}
	
	
	public ArrayList<Integer> getClassSizeMin(){
		return this.sizesMin;
	}
	
	public List<EAttribute> getAllAttributesFromClass(EClass c)
	{
		ArrayList<EAttribute> attr= new ArrayList<EAttribute>();
		attr.addAll(c.getEAllAttributes());
		return attr;
	}
	
	public List<EClass> getAllSubtypes(EClass c)
	{
		ArrayList<EClass> cls= new ArrayList<EClass>();
		for(EClass cc: getClasses())
		{
			if(cc.getEAllSuperTypes().contains(c))
			cls.add(cc);
		}
		return cls;
	}
	
	public List<EReference> getAllReferencesFromClass(EClass c)
	{
		ArrayList<EReference> refs= new ArrayList<EReference>();
		refs.addAll(c.getEAllReferences());
		/*for (EReference ref: c.getEAllReferences())
		{
			if (ref.getEReferenceType().isAbstract())
			{
				refs.remove(ref);
				
			  for (EClass e: getAllSubtypes((EClass) ref.getEType()) )
			  {
				  
				  EReference r= EcoreFactory.eINSTANCE.createEReference();
				  r.setContainment(ref.isContainer());
				  r.setEOpposite(ref.getEOpposite());
				  r.setLowerBound(ref.getLowerBound());
				  r.setUpperBound(ref.getUpperBound());
				  r.setName(ref.getName());
				  r.setEType(e);
				  refs.add(r);
			  }
			}
		}*/
		return refs;
    }
	
	public List<EReference> getAllReferencesFromClasswithOpposite(EClass c)
	{
		ArrayList<EReference> refs= new ArrayList<EReference>();
		//refs.addAll(c.getEAllReferences());
		for (EReference ref: c.getEAllReferences())
		{
			if(ref.getEOpposite()==null)
			{
				//Si elle n'a pas de EOpposite: l'ajouter
				refs.add(ref);
			}
			else
			{
				//Si elle a une EOpposite: Ajouter une des deux;
				int i=ref.getEOpposite().getUpperBound();
				if (i==-1) i=100;
				int j=ref.getUpperBound();
				if (j==-1) j=100;			
				if(j<i)
				{
					refs.add(ref);
				}
				else if(i==j)
				{
					if(ref.getName().codePointCount(0, ref.getName().length())< ref.getEOpposite().getName().codePointCount(0, ref.getEOpposite().getName().length()))
					{
						refs.add(ref);
					}
				}
			}
		}
		return refs;
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
