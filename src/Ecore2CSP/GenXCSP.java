package Ecore2CSP;
import java.io.*;
import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import Ecore.MetaModelReader;

public class GenXCSP {

	private Element instance= new Element("instance");
	private org.jdom2.Document XCSPinstance;
    private Element domains= new Element("domains");
    private Element variables= new Element("variables");
    private Element predicates= new Element("predicates");
    private Element relations= new Element("relations");
    private Element constraints= new Element("constraints");
	
	private MetaModelReader reader;
	private ConfigFileReader cfr;
	private String rootClass;
	private String metamodel;
	private ArrayList<EClass> listOfClasses;
	private ArrayList<Integer> sizesOfClasses;
	private ArrayList<Integer> minSizesOfClasses;
	private int numberOfVariables = 0;
	private int numberOfDomaines = 0;
	private int numberOfPredicates = 0;
	private int numberOfRelations = 0;
	private int numberOfConstraints = 0;
	private int featuresBound = 10;
	private int referencesUB = 2;
	private int maxDomains = 0;
	private int symmetries = 1;
	
	private String vars="";
	private int gccValuesArity = 0;
	private int alldiffNames = 0;
	private String gccvals = "";
	private String alldiffnames = "";
	
	public void createRootElement(MetaModelReader reader, int sym) {
		Document document= new Document(instance);
		setXCSPinstance(document);
		Element rac1= new Element("presentation");
		Attribute a1= new Attribute("name", "?");
		Attribute a2= new Attribute("maxConstraintArity", "2");
		Attribute a3= new Attribute("format", "XCSP 2.0");
		rac1.setAttribute(a1);
		rac1.setAttribute(a2);
		rac1.setAttribute(a3);
		
		this.instance.addContent(rac1);
		this.reader=reader;
		this.rootClass=reader.getRootClass();
		this.metamodel=reader.getMetamodel();
		this.listOfClasses= (ArrayList<EClass>) reader.getClasses();
		this.sizesOfClasses= reader.getClassSize();
		this.minSizesOfClasses= reader.getClassSizeMin();
		this.symmetries=sym;
	}
	
	public GenXCSP(MetaModelReader reader, int RefsBound, int sym){
		createRootElement(reader,sym);
		this.referencesUB=RefsBound;
	}
	
	public GenXCSP(MetaModelReader reader, ConfigFileReader cfr, int sym){
		createRootElement(reader,sym);
		this.cfr=cfr;
		this.referencesUB= cfr.getReferencesUB();	
	}
	
	/**
	 * This is the main method. It generates the CSP instance
	 * 
	 * @param file: filepath of CSP document (instance.xml)
	 * @return
	 */
	public void GenerateXCSP(String file){
		
		GenDomains();
		//GenFeaturesDomains(featuresBound);
		GenRefsDomainsJokers(referencesUB);
		GenVars();
		GenPredOrd();
    	
		Attribute nbvars= new Attribute("nbVariables", ""+numberOfVariables);
		Attribute nbdom= new Attribute("nbDomains", ""+numberOfDomaines);
		Attribute nbpre= new Attribute("nbPredicates", ""+numberOfPredicates );
		Attribute nbrel= new Attribute("nbRelationss", ""+numberOfRelations);
		Attribute nbcons= new Attribute("nbConstraints", ""+numberOfConstraints);
		domains.setAttribute(nbdom);
		variables.setAttribute(nbvars);
		predicates.setAttribute(nbpre);
		relations.setAttribute(nbrel);
		constraints.setAttribute(nbcons);
		instance.addContent(domains);
		instance.addContent(variables);
		instance.addContent(predicates);
		instance.addContent(constraints);
		
		saveXML(getXCSPinstance(), file);
	}
	
	/**
	 *  Generate Class Domains
	 *   
	 */
	public void GenDomains()
	{
		
		int i=1;
		for(EClass c: listOfClasses)
		{
			//***************************************
			//Construire son domaine
			//***************************************
			Element domaine;
			Attribute n,n2;
			int lB,uB,vv;
			String v,v1;
			
			//*****************************************************
			//Domaine 1..min
			//*****************************************************
			domaine= new Element("domain");
			numberOfDomaines++;
			n=new Attribute("name", "DC"+i);
			domaine.setAttribute(n);
			
			lB=  reader.domaineSum(reader.getClassIndex(c)-1)+1;
			uB=  reader.domaineSum(reader.getClassIndex(c)-1) + minSizesOfClasses.get(reader.getClassIndex(c)-1); 
			
			if(lB==uB)
			v= ""+lB;
			else
			v= lB+ ".."+ uB;
			
			domaine.setText(v);
			vv= uB-lB+1; //Plus la valuer 0 de non allocation
			v1= ""+vv+"";
			n2=new Attribute("nbValues", v1);
			domaine.setAttribute(n2);
			domains.addContent(domaine);
			
			//*****************************************************
			//Domaine min+1..max
			//****************************************************
			domaine= new Element("domain");
			numberOfDomaines++;
			n=new Attribute("name", "DC"+i+"_2");
		//	n=new Attribute("name", "DC_"+c.getName()+"_2");
			
			
			domaine.setAttribute(n);
			
			lB=  reader.domaineSum(reader.getClassIndex(c)-1)+ minSizesOfClasses.get(reader.getClassIndex(c)-1)+1; 					
			uB=  reader.domaineSum(reader.getClassIndex(c)); 
			
			if (lB-1==uB){
				v= "0 "+uB;
				vv= uB+1;
			}
			else
			{   
				v= "0" + " "+ lB+ ".."+ uB;
				vv= uB-lB+1+1; //Plus la valuer 0 de non allocation
			}
			domaine.setText(v);
			v1= ""+vv+"";
			n2=new Attribute("nbValues", v1);
			domaine.setAttribute(n2);
			domains.addContent(domaine);
			
			
			i++;
			maxDomains=uB;
			
			////////////////////////////////////
			//  Domaine d'une classe 
			////////////////////////////////////
			
			domaine= new Element("domain");
			numberOfDomaines++;
		//	n=new Attribute("name", "DC"+i+"_2");
			n=new Attribute("name", "DC_"+c.getName());
			
			domaine.setAttribute(n);
			
			
			lB=  reader.domaineSum(reader.getClassIndex(c)-1)+1;
			uB=  reader.domaineSum(reader.getClassIndex(c)); 
				
			if (lB==uB)
			{
				v= ""+uB;
				vv= uB;
			}
			else
			{   
				v= lB+ ".."+ uB;
				vv= uB-lB+1; //Plus la valuer 0 de non allocation
			}
			domaine.setText(v);
			v1= ""+vv+"";
			n2=new Attribute("nbValues", v1);
			domaine.setAttribute(n2);
			domains.addContent(domaine);
			
		}
		
		
	}
	
	/**
	 *
	 *     Features Domains
	 */
	public void GenFeaturesDomains(int borne)
	{
		int fid=0;
		int i=1;
		
		for(EClass c: listOfClasses)
		{
			fid=0;
			for (EAttribute a: reader.getAllAttributesFromClass(c))
			{
				fid++;
				Element domainef= new Element("domain");
				numberOfDomaines++;
				Attribute nf=new Attribute("name", "DF"+i+"_"+fid);
				domainef.setAttribute(nf);
			
				EEnum enume = null;
				try{enume= (EEnum) a.getEType();} catch(Exception e){}
			
				if(enume!=null)
				{
					domainef.setText("1.."+enume.getELiterals().size());
				}
				else
				{
      			   domainef.setText(-borne+".."+borne); 
				}
				
				if(cfr!=null)
				{
					//domainef.setText(featuresDomains.get(c.getName()+"/"+a.getName()));
				}	
				int vf= borne+borne+1;
				Attribute n2f=new Attribute("nbValues", ""+vf);
				domainef.setAttribute(n2f);
			
				domains.addContent(domainef);
			
			}
			i++;
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int GenJokers()
	{
		int max=0;
		for(EClass c:listOfClasses)
		{
			for(EReference ref: reader.getAllReferencesFromClass(c))
			{
				int ub=ref.getUpperBound();
				if (ub==-1)
					ub=referencesUB;
				int lb=ref.getLowerBound();
				int inter= ub-lb;
				if (inter>max) 
				    max=inter;
			}
		}
		return max;
	}
		
	/**
	 *      Generate References Domains      
	 * 
	 */
	public void GenRefsDomains(int FeatureBound)
	{
		int i=1;
		for(EClass c: listOfClasses)
		{
			
			int refi=0;
		    // Création des domaines pour Les références
		    for (EReference ref: reader.getAllReferencesFromClass(c))
			{
		    	refi++;
		    	if (!ref.getEReferenceType().isAbstract())
				{
					int zz=ref.getUpperBound();
					if (zz==-1)
						//zz=5;
					    zz=FeatureBound;

                	//Domaine de la réf
					EClass dst= ref.getEReferenceType();
					int cindex=reader.getClassIndex(dst);
                	    					
					Element domaine= new Element("domain");
        			numberOfDomaines++;
        			Attribute n=new Attribute("name", "DCR_"+refi+"_"+i);
        			domaine.setAttribute(n);
        			
        			int lB=  reader.domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
        			int uB=  reader.domaineSum(cindex); //(i)*10;   // size(class)= 10;
        			
        			
        			String vn= "0" + " "+ lB+ ".."+ uB;
        			String v= " "+ lB+ ".."+ uB;
        			
        			
        			if(c.getName().equals(rootClass)) 
        				{
	        				for(int ii=lB;ii<=uB;ii++)
	            			{
	            				gccvals+= " "+ii;
	            			}
        					//gccvals+= v;
        					gccValuesArity+= uB-lB+1;
        				}
       
        			domaine.setText(v);
        			int vv= uB-lB+1; //Plus la valuer 0 de non allocation
        			
        			Attribute n2=new Attribute("nbValues", ""+vv);
        			domaine.setAttribute(n2);
        			domains.addContent(domaine);
        			
        			Element Dom= new Element("domain");   			
        			n=new Attribute("name", "DCR2_"+refi+"_"+i);
        			Dom.setAttribute(n);
        			int vvv= vv+1;
        			n2=new Attribute("nbValues", ""+vvv);
        			Dom.setAttribute(n2);
        			Dom.setText(vn);	   			
        			domains.addContent(Dom);
        		
						
				}
				else
				{
					//Union des domaines
					List<EClass> ddd=reader.getConcreteSubTypes(ref.getEReferenceType());
					String v="",gcc1 = "";
					int vv=0;
					int vvv=0;
					for (EClass cst: ddd)
					{
						EClass dst= ref.getEReferenceType();
    					int cindex=reader.getClassIndex(cst);
    					
    					int lB=  reader.domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
    					int uB=  reader.domaineSum(cindex); //(i)*10;   // size(class)= 10;
    					
    					for(int ii=lB;ii<=uB;ii++)
    					{
    						gcc1+=" "+ii;
    					}
    					
            			v+= " "+ lB+ ".."+ uB;
            			vv+= uB-lB+1;           		           					
					}
   					
					Element domaine= new Element("domain");
        			numberOfDomaines++;
        			Attribute n=new Attribute("name", "DCR_"+refi+"_"+i);
        			domaine.setAttribute(n);
        				
        			//String vn= " "+ lB+ ".."+ uB;
        			domaine.setText(v);
        			
        			Attribute n2=new Attribute("nbValues", ""+vv);
        			domaine.setAttribute(n2);
        			domains.addContent(domaine);
        			
        			Element Dom= new Element("domain");   			
        			n=new Attribute("name", "DCR2_"+refi+"_"+i);
        			Dom.setAttribute(n);
        			vvv=vv+1;
        			n2=new Attribute("nbValues", ""+vvv);
        			Dom.setAttribute(n2);
        			Dom.setText("0" +v);	   			
        			
        			if(c.getName().equals(rootClass)) 
    				{
    					gccvals= gcc1;
    					gccValuesArity+= vv;
    				}
        		//	Dom.setText(v);	   			//Virer ça pour mettre le 0
        			
        			
        			
        			domains.addContent(Dom);
        		
				}
				
			}
			i++;
		}
	}
	


	/**
	*      Generate References Domains      
	* 
	*/
	public void GenRefsDomainsJokers(int FeatureBound){			
			int max= GenJokers();
			int JSLB= maxDomains+1;
			int JSUB= JSLB+max;
			String jokerString= ""+ JSLB+ ".."+JSUB+"" ;
			int nbvJ= JSUB-JSLB;
			
			int i=1;
			for(EClass c: listOfClasses)
				{

					int refi=0;
					// Création des domaines pour Les références + Jokers
					for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c))
						{
							refi++;
							if (!ref.getEReferenceType().isAbstract())
								{
									int zz=ref.getUpperBound();
									if (zz==-1)
										//zz=5;
										zz=FeatureBound;

									//Domaine de la réf
									EClass dst= ref.getEReferenceType();
									int cindex=reader.getClassIndex(dst);

									Element domaine= new Element("domain");
									numberOfDomaines++;
									Attribute n=new Attribute("name", "DCRJ_"+refi+"_"+i);
									domaine.setAttribute(n);

									int lB=  reader.domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
									int uB=  reader.domaineSum(cindex); //(i)*10;   // size(class)= 10;


									String vn= ""+ lB+ ".."+ uB+ " "+jokerString;
									String v= " "+ lB+ ".."+ uB;


									if(c.getName().equals(rootClass)) 
									{
										for(int ii=lB;ii<=uB;ii++)
											{
												gccvals+= " "+ii;
											}
										//gccvals+= v;
										gccValuesArity+= uB-lB+1;
									}
									//*********			
									//  			String vn=v;         //Virer ça pour mettre le "0"
									//*********			
									domaine.setText(v);
									int vv= uB-lB+1; //Plus la valuer 0 de non allocation

									Attribute n2=new Attribute("nbValues", ""+vv);
									domaine.setAttribute(n2);
									domains.addContent(domaine);

									Element Dom= new Element("domain");   			
									n=new Attribute("name", "DCRJ2_"+refi+"_"+i);
									Dom.setAttribute(n);
									int vvv= vv+nbvJ;
									n2=new Attribute("nbValues", ""+vvv);
									Dom.setAttribute(n2);
									Dom.setText(vn);	   			
									domains.addContent(Dom);


								}
							else
								{
								//Union des domaines
								List<EClass> ddd=reader.getConcreteSubTypes(ref.getEReferenceType());
								String v="",gcc1 = "";
								int vv=0;
								int vvv=0;
								for (EClass cst: ddd)
									{
										EClass dst= ref.getEReferenceType();
										int cindex=reader.getClassIndex(cst);

										int lB=  reader.domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
										int uB=  reader.domaineSum(cindex); //(i)*10;   // size(class)= 10;

										for(int ii=lB;ii<=uB;ii++)
											{
												gcc1+=" "+ii;
											}

										v+= " "+ lB+ ".."+ uB;
										vv+= uB-lB+1;           		           					
									}

								Element domaine= new Element("domain");
								numberOfDomaines++;
								Attribute n=new Attribute("name", "DCRJ_"+refi+"_"+i);
								domaine.setAttribute(n);

								//String vn= " "+ lB+ ".."+ uB;
								domaine.setText(v);

								Attribute n2=new Attribute("nbValues", ""+vv);
								domaine.setAttribute(n2);
								domains.addContent(domaine);

								Element Dom= new Element("domain");   			
								n=new Attribute("name", "DCRJ2_"+refi+"_"+i);
								Dom.setAttribute(n);
								vvv=vv+nbvJ;
								n2=new Attribute("nbValues", ""+vvv);
								Dom.setAttribute(n2);
								Dom.setText(v + " "+ jokerString);	   			

								if(c.getName().equals(rootClass)) 
									{
										gccvals= gcc1;
										gccValuesArity+= vv;
									}
								//	Dom.setText(v);	   			//Virer ça pour mettre le 0



								domains.addContent(Dom);

								//System.out.println("Class="+ c.getName()+ " i= "+ i+ " ref= "+ ref.getName()+ " refi= "+refi+ " dom= "+n+ " domT= "+v); 
								
								}

						}
					i++;
				}
		}

	
	
	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////         Generete
	///////////////////////////////////         Class CSP Variables              
	///////////////////////////////        Feature Variables  
	///////////////////////////        References Variables
	///////////////////////         Class Instances vs References Constraints (No allocation)
	///////////////////
	///////////////
	////////////
	/****
	 * 
	 *     Generate Class CSP Variables
	 *            And Features Variables
	 *                And References Variables
	 */
	
	public void GenVars()
	{
		int i=1;
		for(EClass c: listOfClasses){
			int refi=0;
			int gccarity=0;
			String gccvars="";
			   
			//Pour la Gcc Eopposite			   
			int arityGcc=0;
		    int valsArityGcc=0;
		    String varsGcc="";
		    String valsGcc="";
		    
			for(int j=reader.domaineSum(reader.getClassIndex(c)-1)+1;j<=reader.domaineSum(reader.getClassIndex(c));j++){	
				
				//Create attributes variables 
				int fid=0;
//            	for (EAttribute a: reader.getAllAttributesFromClass(c)){
//					fid++;
//					Element variablef= new Element("variable");
//	            	numberOfVariables++;
//	            	Attribute namef= new Attribute("name", "F_"+c.getName()+"_"+j+"_"+a.getName());
//	            	Attribute domf= new Attribute("domain", "DF"+i+"_"+fid);
//	            	variablef.setAttribute(domf);
//	            	variablef.setAttribute(namef);
//	            	variables.addContent(variablef);     		
//            	}
            	
            	//Create variables for references
            	refi=0;
    		    for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c)){
    		    	refi++;
    		    	if (!ref.getEReferenceType().isAbstract()){
    					int zz=ref.getUpperBound();
    					if (zz==-1)
    						zz=referencesUB;
    					
    					EClass dst= ref.getEReferenceType();
    					int cindex=reader.getClassIndex(dst);
                    	
    					String VarPrec="";
    					
    					for(int z=1;z<=zz;z++){
	    					Element variabler= new Element("variable");
	                    	numberOfVariables++;
	                    	
	                    	Attribute namer= new Attribute("name", "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z);
	                    	Attribute domr;
	                    	VarPrec="Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z;
	                    	//*************
	                    	gccarity++;
	                    	gccvars+= " "+ "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z;
	                    	gccvals+= "";             
	                    	
	                    	/////////////////////////////////////////////////////////////////
	                    	//Contrainte ordonne des vars des références: Breaking symmetries
	                    	/////////////////////////////////////////////////////////////////
	                    	if(symmetries==1){
	                    		if(z>1){
	                    			int ertf=z-1;
	                    			GenConstOrd("Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z, "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+ertf);
	                    		}
	                    	}
	                    
	                    	////////////////////////// 
	                    	// Doamines avec les jokers 
	                    	//////////////                 
	                   		if (z<=ref.getLowerBound()){
	                        	domr= new Attribute("domain", "DCRJ_"+refi+"_"+i);       
	                        }else{                                                            
	                        	domr= new Attribute("domain", "DCRJ2_"+refi+"_"+i);       
	                        }	
	                    		
	                    	variabler.setAttribute(domr);
	                    	variabler.setAttribute(namer);
	                    	variables.addContent(variabler);
    					}
    					
    				}
    				else
    				{
    					//Création des variables ---- Union de domaines
    					List<EClass> ddd=reader.getConcreteSubTypes(ref.getEReferenceType());
    					for (EClass cst: ddd){
    						EClass dst= ref.getEReferenceType();
        					int cindex=reader.getClassIndex(cst);                        	
    					}   
    					int zz=ref.getUpperBound();
    					if (zz==-1)
    						//zz=5;
    						//zz=FeatureBound/2;
						    zz=referencesUB;
    						
    					for(int z=1;z<=zz;z++)
    					{
	    					Element variabler= new Element("variable");
	                    	numberOfVariables++;
	                    	Attribute namer= new Attribute("name", "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z);                  	                    	
	                    	Attribute domr;
	                    	
	                    	gccarity++;
	                    	gccvars+= " "+ "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z;
	                    	gccvals+= "";             
	                    
	                    	if(symmetries==1){
	                    		if(z>1){
	                    			int ertf=z-1;
	                    			GenConstOrd("Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z, "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+ertf);
	                    		}
	                    	}
	
	                   		if (z<=ref.getLowerBound()){
	                    		domr= new Attribute("domain", "DCRJ_"+refi+"_"+i);
	                    	}else{
	                    		domr= new Attribute("domain", "DCRJ2_"+refi+"_"+i);	
	                    	}
	                    	variabler.setAttribute(domr);
	                    	variabler.setAttribute(namer);
	                    	variables.addContent(variabler);
	                    }
    				}
    		    }
			}
			
			//////////////////////////////////////////////////////////////
			///////////    Gestion des references EOpposite
			//////////////////////////////////////////////////////////////

			for(EReference ref: reader.getAllReferencesFromClasswithOpposite(c))
			{
				//Créer la Gcc Eopposite
				if(ref.getEOpposite()!=null)
				{
					//if(ref.getEOpposite().getUpperBound()!=-1)
					//{
						valsGcc="";
						arityGcc=0;
						varsGcc="";
							
						//Les vals et arityVal de la Gcc
						if (!ref.getEReferenceType().isAbstract())
						{
						
						
						EClass dst= ref.getEReferenceType();
						//System.out.println("Class="+dst.getName());
						int cindex=reader.getClassIndex(dst);
						int lB= reader.domaineSum(cindex-1)+1; 
						int uB= reader.domaineSum(cindex);
										
						//Remplir vals
						for(int biz=lB;biz<=uB;biz++)
							valsGcc= valsGcc+biz+" ";
						valsArityGcc=uB-lB+1;


						//Les vars et arityVars de la Gcc
							int zz=ref.getUpperBound();
							if (zz==-1)
								zz=referencesUB;

							EClass dst2= ref.getEReferenceType();
							int cindex2=reader.getClassIndex(dst);

							String VarPrec="";

							for(int j=reader.domaineSum(reader.getClassIndex(c)-1)+1;j<=reader.domaineSum(reader.getClassIndex(c));j++)   //10= size(c);
							{
								for(int z=1;z<=zz;z++)
								{
									arityGcc++;
									varsGcc+= "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z+" ";	 
								}
							}
					

							int upperBound = ref.getEOpposite().getUpperBound();
							
							if(upperBound ==-1) 
								upperBound = referencesUB;
							

						//Créer la Gcc 	
						createGcc(ref.getName()+"_"+ref.getEOpposite().getName(),arityGcc,valsArityGcc,varsGcc,valsGcc,ref.getEOpposite().getLowerBound(),upperBound);
					}
				}
			}
			
			i++;
		}
	}
	
	/** 
	 * Generate Classes Gcc Constraint
	 */
	public void GenClassConstraint()
	{
		Element cons=new Element("constraint");
		numberOfConstraints++;
		int arity=reader.domaineSum(listOfClasses.size());		
		
		//Les parametres
		//String vars= "";
		String vals= "",lb="",ub="";
		
		//Valeurs et bornes inf et sup
		int ddddd= reader.domaineSum(listOfClasses.size()) - reader.domaineSumMin(listOfClasses.size());
		vals= vals + "0 ";
		lb= lb+ "0 ";
		ub= ub+ ddddd+" ";
		for(int i=1; i<=reader.domaineSum(listOfClasses.size()); i++){
			vals= vals + i+" ";
			lb=lb+ "0 ";
			ub=ub+"1 ";
		}
		
		String pText="[ "+ vars +"] ["+ vals +"] ["+lb+"] ["+ub+"]";
		Element param= new Element("parameters");
		param.setText(pText);
		cons.addContent(param);
		
		//Les attributs
		Attribute name= new Attribute("name", "GccClasses");
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+arity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:globalCardinality");
		cons.setAttribute(refe);
		
		constraints.addContent(cons);
		
	}

	/** 
	 *   Class Instances References No Allocation Constraints 
	 */
	public void GenNoAllocConstraintPred(){
		Element pre1= new Element("predicate");
		numberOfPredicates ++;
		Element par1= new Element("parameters");
		par1.setText("int A int B");
		Element exp= new Element("expression");
		Element fct= new Element("functional");
		fct.setText("or(neg(eq(A,0)),eq(B,0))");
		exp.addContent(fct);
		pre1.addContent(par1);
		pre1.addContent(exp);
		Attribute refe1= new Attribute("name", "implies");
		pre1.setAttribute(refe1);
		predicates.addContent(pre1);
	}
	
	public void GenNoAllocConstraint(String i,String var){
		Element cons = new Element("constraint");
		numberOfConstraints++;
		Element param= new Element("parameters");
		param.setText(var);
		cons.addContent(param);
		Attribute name= new Attribute("name", "CNA"+i);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", "2");
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", var);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "implies");
		cons.setAttribute(refe);
		constraints.addContent(cons);	
	}
	
	/**
	*   Generating a Gcc Constraint on root references' instances 
	*/
	public void Gccroot(int arity,int valsarity,String vars,String vals){
		Element cons=new Element("constraint");
		numberOfConstraints++;
		
		//Les parametres
		String lb="0 ",ub=arity+" ";
		vals= "0"+ vals;
		
		//Valeurs et bornes inf et sup
		for(int i=1;i<=valsarity;i++){
			lb=lb + "0 ";
			ub=ub +"1 ";
		}
		
		String pText="[ "+ vars +"] ["+ vals +"] ["+lb+"] ["+ub+"]";
		Element param= new Element("parameters");
		param.setText(pText);
		cons.addContent(param);
		
		Attribute name= new Attribute("name", "GccRoot");
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+valsarity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:globalCardinality");
		cons.setAttribute(refe);
		constraints.addContent(cons);
	}
	
	/**
	 * This method creates a GCC constraint. It is mainly used to process EOpossite Reference.
	 * Having diverse upperBound for this GCC helps to get a better diversity while instanciating 
	 * the variables that links between class instances.
	 * 
	 * UpperBound are then randomly generated
	 * 
	 * @param gccName
	 * @param variableArity
	 * @param valuesArity
	 * @param variables
	 * @param domain
	 * @param lower
	 * @param upper
	 */
	public void createGcc(String gccName,int variableArity, int valuesArity, String variables, String domain, int lower, int upper)
	{
		Element cons=new Element("constraint");
		numberOfConstraints++;
		
		String gccLowerBounds=" ",gccUpperBounds=" ";
		Random random= new SecureRandom();
		
		//Create random uniform upper bound
		for(int i=1;i<=valuesArity;i++)
		{
			gccLowerBounds = gccLowerBounds + lower+" ";
			int nextUpper = random.nextInt(upper-lower+1) + lower;
			gccUpperBounds = gccUpperBounds + nextUpper +" ";
		}
		
		//Set GCC body
		String body="[ "+ variables +"] [ "+ domain +"] ["+gccLowerBounds+"] ["+gccUpperBounds+"]";
		Element param= new Element("parameters");
		param.setText(body);
		cons.addContent(param);
		
		//Set GCC attributes
		Attribute name= new Attribute("name", gccName);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+variableArity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+variables);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:globalCardinality");
		cons.setAttribute(refe);
		
		//Add GCC to all constraints
		constraints.addContent(cons);
	}
	
	public void GenAllDiffRoot(int arity,String vars)
	{
		Element cons=new Element("constraint");
		numberOfConstraints++;
		
		//Les parametres	
        
		String pText="["+ vars +"]";
		Element param= new Element("parameters");
		param.setText(pText);
		cons.addContent(param);
		
		//Les attributs
		Attribute name= new Attribute("name", "AllDiffRoot");
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+arity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:allDifferent");
		cons.setAttribute(refe);
	    constraints.addContent(cons);
	}
	
	/**
	 * Create predicate for symmetries constraints
	 */
	public void GenPredOrd()
	{
		Element pre1= new Element("predicate");
		numberOfPredicates ++;
		
		Element par1= new Element("parameters");
		par1.setText("int v1 int v2");
		

		Element exp= new Element("expression");
		Element fct= new Element("functional");
		fct.setText("lt(v1,v2)");
		exp.addContent(fct);
		
		pre1.addContent(par1);
		pre1.addContent(exp);
		Attribute refe1= new Attribute("name", "inf");
		pre1.setAttribute(refe1);
		predicates.addContent(pre1);
	}
	
	/**
	 * Create symmetries ordering constraints
	 * 
	 * @param variable1
	 * @param variable2
	 */
	public void GenConstOrd(String variable1, String variable2){
		numberOfConstraints++;
		Element cons = new Element("constraint");
		Element param= new Element("parameters");
		
		param.setText(variable2+" "+variable1);
		cons.addContent(param);
		
		Attribute name= new Attribute("name", "Cons"+variable1+variable2);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", "2");
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", variable1+" "+variable2);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "inf");
		cons.setAttribute(refe);
		
		constraints.addContent(cons);
	}
	
	/**
	 * This method saves the xcsp document an an XML file
	 * @param XCSP
	 * @param file
	 */
	public void saveXML(Document XCSP,String file){
		try{
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		    sortie.output(XCSP, new FileOutputStream(file));
		}
		catch (Exception e){
			System.out.println("\t[PROBLEM] impossible to save XCSP instance file");
		}
	}
	
	public int getMaxDomains()
	{
		return maxDomains;
	}
	
	public org.jdom2.Document getXCSPinstance() {
		return XCSPinstance;
	}

	public void setXCSPinstance(org.jdom2.Document xCSPinstance){
		XCSPinstance = xCSPinstance;
	}	
}
