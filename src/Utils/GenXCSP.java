package Utils;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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

public class GenXCSP {

	static MetaModelReader r;
	static Element instance= new Element("instance");
	private static org.jdom2.Document XCSPinstance;
    
	static Element domains= new Element("domains");
	static Element variables= new Element("variables");
	static Element predicates= new Element("predicates");
	static Element relations= new Element("relations");
	static Element constraints= new Element("constraints");
	
	ArrayList<EClass> lesClasses;
	ArrayList<Integer> sizes;
	ArrayList<Integer> sizesMin;
	int nbVars=0;
	int nbDoms=0;
	int nbPre=0;
	int nbRel=0;
	int nbCons=0;
	int gccvalsarity=0;
	int Alldiffnames=0;
	int FeatureBound;
	int RefsBound;
	int maxDomains=0;
	int Symmetries=0;
	
	ConfigFileReader cfr=null;
	Hashtable<String,String> featuresDomains=null;
	
	String vars="",racine,gccvals="";
	String alldiffnames="";
	
	
	public GenXCSP(String modelFile,String racine,MetaModelReader re,int FeatureBound,int RefsBound, int sym)
	{
		this.r=re;
		Document document= new Document(instance);
		setXCSPinstance(document);
		Element rac1= new Element("presentation");
		Attribute a1= new Attribute("name", "?");
		Attribute a2= new Attribute("maxConstraintArity", "2");
		Attribute a3= new Attribute("format", "XCSP 2.0");
		Attribute a4= new Attribute("type", "WCSP");
		rac1.setAttribute(a1);
		rac1.setAttribute(a2);
		rac1.setAttribute(a3);
	//	rac1.setAttribute(a4);	
		instance.addContent(rac1);
		
		//Construire les variables
	    lesClasses= (ArrayList<EClass>) r.getClasses();
				
		//Les size des classes
		sizes= (ArrayList<Integer>) r.getClassSize();
		sizesMin= (ArrayList<Integer>) r.getClassSizeMin();
		
		this.racine=racine;
		this.FeatureBound=FeatureBound;
		this.RefsBound=RefsBound;
		this.Symmetries=sym;
	}
	
	public GenXCSP(String modelFile,String racine,MetaModelReader re, ConfigFileReader cfr, int sym)
	{
		this.r=re;
		setXCSPinstance(new Document(instance));
		Element rac1= new Element("presentation");
		Attribute a1= new Attribute("name", "?");
		Attribute a2= new Attribute("maxConstraintArity", "2");
		Attribute a3= new Attribute("format", "XCSP 2.0");
		Attribute a4= new Attribute("type", "WCSP");
		rac1.setAttribute(a1);
		rac1.setAttribute(a2);
		rac1.setAttribute(a3);
	    //rac1.setAttribute(a4);	
		instance.addContent(rac1);
		
		//Construire les variables
	    lesClasses= (ArrayList<EClass>) r.getClasses();
				
		//Les size des classes
		sizes= (ArrayList<Integer>) r.getClassSize();
		sizesMin= (ArrayList<Integer>) r.getClassSizeMin();
		
		
		//Read the ConfigFile to get the FeatureBound et RefBound
		
		this.racine=racine;
		this.FeatureBound= cfr.getFeatureBound();
		//System.out.println("FeaturesB="+this.FeatureBound);
		this.RefsBound= cfr.getRefsBound();
		//System.out.println("RefsB="+this.RefsBound);
		this.Symmetries=sym;
		
		this.cfr=cfr;
		featuresDomains=cfr.getfeaturesDomains();
		
	}
	
	public int getMaxDomains()
	{
		return maxDomains;
	}
	
	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////
	///////////////////////////          Sum computation methods 
	///////////////////////
	///////////////////
	///////////////
	////////////
	
	
	public int domaineSum(int k)
	{
		int s=0;
		if (k==0)
			return 0;
		for(int i=0;i<=k-1;i++)
		{
			s+= sizes.get(i);
		}
		return s;
	}
	
	public int domaineSumMin(int k)
	{
		int s=0;
		if (k==0)
			return 0;
		for(int i=0;i<=k-1;i++)
		{
			s+= sizesMin.get(i);
		}
		return s;
	}
	
	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////           Generate Class Domains
	///////////////////////////          
	///////////////////////
	///////////////////
	///////////////
	////////////
	/****
	 *    Generate Class Domains
	 *   
	 */
	public void GenDomains()
	{
		
		int i=1;
		for(EClass c: lesClasses)
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
			nbDoms++;
			n=new Attribute("name", "DC"+i);
		//	n=new Attribute("name", "DC_"+c.getName()); ////Nom de classe = name, DC_name
			domaine.setAttribute(n);
			
			lB=  domaineSum(r.getClassIndex(c)-1)+1;
			uB= domaineSum(r.getClassIndex(c)-1) + sizesMin.get(r.getClassIndex(c)-1); 
			
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
			nbDoms++;
			n=new Attribute("name", "DC"+i+"_2");
		//	n=new Attribute("name", "DC_"+c.getName()+"_2");
			
			
			domaine.setAttribute(n);
			
			lB=  domaineSum(r.getClassIndex(c)-1)+ sizesMin.get(r.getClassIndex(c)-1)+1; 					
			uB=  domaineSum(r.getClassIndex(c)); 
			
			if (lB-1==uB)
			{
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
			nbDoms++;
		//	n=new Attribute("name", "DC"+i+"_2");
			n=new Attribute("name", "DC_"+c.getName());
			
			domaine.setAttribute(n);
			
			
			lB=  domaineSum(r.getClassIndex(c)-1)+1;
			uB=  domaineSum(r.getClassIndex(c)); 
				
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
	
	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////
	///////////////////////////          Generate Feature Domains 
	///////////////////////
	///////////////////
	///////////////
	////////////
	/****
	 *
	 *     Features Domains
	 */
	public void GenFeaturesDomains(int borne)
	{
		int fid=0;
		int i=1;
		
		for(EClass c: lesClasses)
		{
			fid=0;
			for (EAttribute a: r.getAllAttributesFromClass(c))
			{
				fid++;
				Element domainef= new Element("domain");
				nbDoms++;
				Attribute nf=new Attribute("name", "DF"+i+"_"+fid);
				domainef.setAttribute(nf);
			
				EEnum enume = null;
				try{enume= (EEnum) a.getEType();}catch(Exception e){}
			
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
					domainef.setText(featuresDomains.get(c.getName()+"/"+a.getName()));
				}	
				int vf= borne+borne+1;
				Attribute n2f=new Attribute("nbValues", ""+vf);
				domainef.setAttribute(n2f);
			
				domains.addContent(domainef);
			
			}
			i++;
		}
	}
	
	
	/////////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////
	//////////////////////////////////////
	////////////////////////////////           Generate Jokers interval
	////////////////////////////
	//////////////////////
	/////////////////
	///////////
	
	public int GenJokers()
	{
		int max=0;
		for(EClass c:lesClasses)
		{
			for(EReference ref: r.getAllReferencesFromClass(c))
			{
				int ub=ref.getUpperBound();
				if (ub==-1)
					ub=RefsBound;
				int lb=ref.getLowerBound();
				int inter= ub-lb;
				if (inter>max) 
				    max=inter;
			}
		}
		return max;
	}
		
	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////
	///////////////////////////          Generate Reference Domains 
	///////////////////////
	///////////////////
	///////////////
	////////////
	/****
	 * 
	 *      Generate References Domains      
	 * 
	 */
	public void GenRefsDomains(int FeatureBound)
	{
		int i=1;
		for(EClass c: lesClasses)
		{
			
			int refi=0;
		    // Création des domaines pour Les références
		    for (EReference ref: r.getAllReferencesFromClass(c))
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
					int cindex=r.getClassIndex(dst);
                	    					
					Element domaine= new Element("domain");
        			nbDoms++;
        			Attribute n=new Attribute("name", "DCR_"+refi+"_"+i);
        			domaine.setAttribute(n);
        			
        			int lB=  domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
        			int uB=  domaineSum(cindex); //(i)*10;   // size(class)= 10;
        			
        			
        			String vn= "0" + " "+ lB+ ".."+ uB;
        			String v= " "+ lB+ ".."+ uB;
        			
        			
        			if(c.getName().equals(racine)) 
        				{
	        				for(int ii=lB;ii<=uB;ii++)
	            			{
	            				gccvals+= " "+ii;
	            			}
        					//gccvals+= v;
        					gccvalsarity+= uB-lB+1;
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
					List<EClass> ddd=r.getConcreteSubTypes(ref.getEReferenceType());
					String v="",gcc1 = "";
					int vv=0;
					int vvv=0;
					for (EClass cst: ddd)
					{
						EClass dst= ref.getEReferenceType();
    					int cindex=r.getClassIndex(cst);
    					
    					int lB=  domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
    					int uB=  domaineSum(cindex); //(i)*10;   // size(class)= 10;
    					
    					for(int ii=lB;ii<=uB;ii++)
    					{
    						gcc1+=" "+ii;
    					}
    					
            			v+= " "+ lB+ ".."+ uB;
            			vv+= uB-lB+1;           		           					
					}
   					
					Element domaine= new Element("domain");
        			nbDoms++;
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
        			
        			if(c.getName().equals(racine)) 
    				{
    					gccvals= gcc1;
    					gccvalsarity+= vv;
    				}
        		//	Dom.setText(v);	   			//Virer ça pour mettre le 0
        			
        			
        			
        			domains.addContent(Dom);
        		
				}
				
			}
			i++;
		}
	}
	

///////////////////////////////////////////////////
///////////////////////////////////////////////
///////////////////////////////////////////
///////////////////////////////////////
///////////////////////////////////
///////////////////////////////
///////////////////////////          Generate Reference Domains + Jokers ******* 
///////////////////////
///////////////////
///////////////
////////////
/****
* 
*      Generate References Domains      
* 
*/
	public void GenRefsDomainsJokers(int FeatureBound)
		{
			
			int max= GenJokers();
			int JSLB= maxDomains+1;
			int JSUB= JSLB+max;
			String jokerString= ""+ JSLB+ ".."+JSUB+"" ;
			int nbvJ= JSUB-JSLB;
			
			int i=1;
			for(EClass c: lesClasses)
				{

					int refi=0;
					// Création des domaines pour Les références + Jokers
					for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))
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
									int cindex=r.getClassIndex(dst);

									Element domaine= new Element("domain");
									nbDoms++;
									Attribute n=new Attribute("name", "DCRJ_"+refi+"_"+i);
									domaine.setAttribute(n);

									int lB=  domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
									int uB=  domaineSum(cindex); //(i)*10;   // size(class)= 10;


									String vn= ""+ lB+ ".."+ uB+ " "+jokerString;
									String v= " "+ lB+ ".."+ uB;


									if(c.getName().equals(racine)) 
									{
										for(int ii=lB;ii<=uB;ii++)
											{
												gccvals+= " "+ii;
											}
										//gccvals+= v;
										gccvalsarity+= uB-lB+1;
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
								List<EClass> ddd=r.getConcreteSubTypes(ref.getEReferenceType());
								String v="",gcc1 = "";
								int vv=0;
								int vvv=0;
								for (EClass cst: ddd)
									{
										EClass dst= ref.getEReferenceType();
										int cindex=r.getClassIndex(cst);

										int lB=  domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
										int uB=  domaineSum(cindex); //(i)*10;   // size(class)= 10;

										for(int ii=lB;ii<=uB;ii++)
											{
												gcc1+=" "+ii;
											}

										v+= " "+ lB+ ".."+ uB;
										vv+= uB-lB+1;           		           					
									}

								Element domaine= new Element("domain");
								nbDoms++;
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

								if(c.getName().equals(racine)) 
									{
										gccvals= gcc1;
										gccvalsarity+= vv;
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
		for(EClass c: lesClasses)
		{
			int refi=0;
			  //Variables pour une racine (Pour la gcc)
			   int gccarity=0;
			   String gccvars="";
			
			   
			 //Pour la Gcc Eopposite			   
			    int arityGcc=0;
		    	int valsArityGcc=0;
		    	String varsGcc="";
		    	String valsGcc="";
			   
			
			for(int j=domaineSum(r.getClassIndex(c)-1)+1;j<=domaineSum(r.getClassIndex(c));j++) 	
			{	
				//************************************            
				//Les variables Des Attributs 
				int fid=0;
            	for (EAttribute a: r.getAllAttributesFromClass(c))
            	{
				
				fid++;
				Element variablef= new Element("variable");
            	nbVars++;
            	Attribute namef= new Attribute("name", "F_"+c.getName()+"_"+j+"_"+a.getName());
            	Attribute domf= new Attribute("domain", "DF"+i+"_"+fid);
            	
            	  ///////////////////////////////////
            	 //OCL Réseaux de Petri
            	//////////////////////////////////
            	if(c.getName().equals("Place") || c.getName().equals("Transition"))
            	{
            		if(a.getName().equals("name"))
            		{
            			alldiffnames+= " F_"+c.getName()+"_"+j+"_"+a.getName();
            			Alldiffnames++;
            		}
            	}
            	////////////////////////////////////
            	////////////////////////////////////
            	///////////////////////////////////
            	
            	variablef.setAttribute(domf);
            	variablef.setAttribute(namef);
            	variables.addContent(variablef);     		
            	}
            	
            	//******************************
            	//Les références
    		    refi=0;
    		    for (EReference ref: r.getAllReferencesFromClasswithOpposite(c))
    			{
    		    	refi++;
    		    	if (!ref.getEReferenceType().isAbstract())
    				{
    					int zz=ref.getUpperBound();
    					if (zz==-1)
    						zz=RefsBound;
    					
    					EClass dst= ref.getEReferenceType();
    					int cindex=r.getClassIndex(dst);
                    	
    					String VarPrec="";
    					
    					for(int z=1;z<=zz;z++)
    					{
    					Element variabler= new Element("variable");
                    	nbVars++;
                    	
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
                    	if(Symmetries==1)
                    	{
                    		if(z>1)
                    		{
                    			int ertf=z-1;
                    			GenConstOrd("Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z, "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+ertf);
                    		}
                    	}
                    
                    	////////////////////////
                    	// Domaines avec 0
                    	//////////////////////////////////////////             	
                    	/*  	
                		if (z<ref.getLowerBound())
                    	{
                    		domr= new Attribute("domain", "DCR_"+refi+"_"+i);
                    	}
                    	else
                    	{
                    		domr= new Attribute("domain", "DCR2_"+refi+"_"+i);
                   
                   		}
                    	 */  		
                    	////////////////////////// 
                    	// Doamines avec les jokers 
                    	//////////////                 
                   		if (z<=ref.getLowerBound())                                        
                        	{
                        		domr= new Attribute("domain", "DCRJ_"+refi+"_"+i);       
                        	}
                        	else                                                         
                        	{                                                            
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
    					List<EClass> ddd=r.getConcreteSubTypes(ref.getEReferenceType());
    					for (EClass cst: ddd)
    					{
    						EClass dst= ref.getEReferenceType();
        					int cindex=r.getClassIndex(cst);                        	
    					}   
    					int zz=ref.getUpperBound();
    					if (zz==-1)
    						//zz=5;
    						//zz=FeatureBound/2;
						    zz=RefsBound;
    						
    					for(int z=1;z<=zz;z++)
    					{
    					Element variabler= new Element("variable");
                    	nbVars++;
                    	Attribute namer= new Attribute("name", "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z);                  	                    	
                    	Attribute domr;
                    	
                    	//*************
                    	gccarity++;
                    	gccvars+= " "+ "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z;
                    	gccvals+= "";             
                    
                    	////////////////////////////////////////////////////////////////////
                    	//Contrainte ordonne des vars des références: Break symmetries
                    	////////////////////////////////////////////////////////////////////
                    	if(Symmetries==1)
                    	{
                    		if(z>1)             
                    		{
                    			int ertf=z-1;
                    			GenConstOrd("Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z, "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+ertf);
                    		}
                    	}
                  	
                    	
              /*    	if (z<=ref.getLowerBound())
                    	{
                    		domr= new Attribute("domain", "DCR_"+refi+"_"+i);
                    	}    
                    	else
                    	{
                    		domr= new Attribute("domain", "DCR2_"+refi+"_"+i);	
                    		//*************************************************************
                        	//Contrainte de non allocation de la reference
               //     		if(j>sizesMin.get(r.getClassIndex(c)-1))
               //     		GenNoAllocConstraint(""+i+refi+z,"Id_"+c.getName()+"_"+j+ " Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z);
                        	//*************************************************************
                        	
                    	}*/
                   	
                   	if (z<=ref.getLowerBound())
                    	{
                    		domr= new Attribute("domain", "DCRJ_"+refi+"_"+i);
                    	}    
                    	else
                    	{
                    		domr= new Attribute("domain", "DCRJ2_"+refi+"_"+i);	
                    	}
                    	variabler.setAttribute(domr);
                    	variabler.setAttribute(namer);
                    	variables.addContent(variabler);
                    	
                    	
                    	//System.out.println("Class= "+c.getName()+ " index= "+ i +" ref= "+ref.getName()+" refii= " + refi + " dom= "+domr);
        		    	
    			}
    				
    					
    		}
    		    	
    		    	
				
    	}
  }
			
			//////////////////////////////////////////////////////////////
			///////////    Gestion des references EOpposite
			//////////////////////////////////////////////////////////////

			for(EReference ref: r.getAllReferencesFromClasswithOpposite(c))
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
						int cindex=r.getClassIndex(dst);
						int lB=  domaineSum(cindex-1)+1; 
						int uB=  domaineSum(cindex);
										
						//Remplir vals
						for(int biz=lB;biz<=uB;biz++)
							valsGcc= valsGcc+biz+" ";
						valsArityGcc=uB-lB+1;


						//Les vars et arityVars de la Gcc
							int zz=ref.getUpperBound();
							if (zz==-1)
								zz=RefsBound;

							EClass dst2= ref.getEReferenceType();
							int cindex2=r.getClassIndex(dst);

							String VarPrec="";

							for(int j=domaineSum(r.getClassIndex(c)-1)+1;j<=domaineSum(r.getClassIndex(c));j++)   //10= size(c);
							{
								for(int z=1;z<=zz;z++)
								{
									arityGcc++;
									varsGcc+= "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z+" ";	 
								}
							}
					

							int upperBound = ref.getEOpposite().getUpperBound();
							
							if(upperBound ==-1) 
								upperBound = RefsBound;
							

						//Créer la Gcc 	
						createGcc(ref.getName()+"_"+ref.getEOpposite().getName(),arityGcc,valsArityGcc,varsGcc,valsGcc,ref.getEOpposite().getLowerBound(),upperBound);
					}
				}
			}
			
			i++;
		}
	}
	

	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////
	///////////////////////////          Generate Class Instances GCC Constaint 
	///////////////////////
	///////////////////
	///////////////
	////////////
	/******
       *  
	   *       Generate Classes Gcc Constraint
	   */
	
	public void GenClassConstraint()
	{
		Element cons=new Element("constraint");
		nbCons++;
		int arity=domaineSum(lesClasses.size());		
		
		//Les parametres
		//String vars= "";
		String vals= "",lb="",ub="";
		
		
		//Valeurs et bornes inf et sup
		int ddddd= domaineSum(lesClasses.size())-domaineSumMin(lesClasses.size());
		vals= vals + "0 ";
		lb= lb+ "0 ";
		ub= ub+ ddddd+" ";
		for(int i=1;i<=domaineSum(lesClasses.size());i++)
		{
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

	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////
	///////////////////////////          Class Instances vs Refernces 
	///////////////////////           No Allocation Constraint 
 	///////////////////           And Predicate Method
	///////////////
	////////////
	/*********
	 * 
	 *   Class Instances References No Allocation Constraints 
	 * 
	 */

	public void GenNoAllocConstraintPred()
	{
		//La relation d'implication
		Element pre1= new Element("predicate");
		nbPre++;
		
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
	
	public void GenNoAllocConstraint(String i,String var)
	{
		//Les contraintes
	    		
		Element cons = new Element("constraint");
		nbCons++;
		
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
	
	
	////////////////////////////////////////////////////
	///////////////////////////////////////////////
	//////////////////////////////////////////
	//////////////////////////////////////
	//////////////////////////////               Contrainte Gcc sur les références de la racine
	/////////////////////////
	////////////////////
	////////////////
	///////////
	/**
	* 
	*    Generating a Gcc Constraint on root references' instances 
	* 
	*/

	
	public void Gccroot(int arity,int valsarity,String vars,String vals)
	{
		Element cons=new Element("constraint");
		nbCons++;
		
		//Les parametres
		//String vars= "";
		String lb="0 ",ub=arity+" ";
		
	    vals= "0"+ vals;
		
	    //System.out.println("arity vals="+valsarity);
	    
		//Valeurs et bornes inf et sup
		for(int i=1;i<=valsarity;i++)
		{
			lb=lb + "0 ";
			ub=ub +"1 ";
		}
		
        
		String pText="[ "+ vars +"] ["+ vals +"] ["+lb+"] ["+ub+"]";
		Element param= new Element("parameters");
		param.setText(pText);
		cons.addContent(param);
		
		//Les attributs
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
	* 
	*    Generating a Gcc Constraint on root references' instances 
	* 
	*/	
	public void createGcc(String nom,int arity,int valsarity,String vars,String vals, int lower, int upper)
	{
		Element cons=new Element("constraint");
		nbCons++;
		
		//Les parametres
		//String vars= "";
		String lb=" ",ub=" ";
		
	    vals= ""+ vals;
		
	   // System.out.println("arity vals="+valsarity);
	    
		//Valeurs et bornes inf et sup
		for(int i=1;i<=valsarity;i++)
		{
			lb=lb + lower+" ";
			ub=ub +upper+" ";
		}
		
        
		String pText="[ "+ vars +"] [ "+ vals +"] ["+lb+"] ["+ub+"]";
		Element param= new Element("parameters");
		param.setText(pText);
		cons.addContent(param);
		
		//Les attributs
		Attribute name= new Attribute("name", nom);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+arity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:globalCardinality");
		cons.setAttribute(refe);
		
		constraints.addContent(cons);
		

	}
	
	public void GenAllDiffRoot(int arity,String vars)
	{
		Element cons=new Element("constraint");
		nbCons++;
		
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
		
	//	constraints.addContent(cons);
	
	}
	
	//OCL RDP

	public void GenAllDiffnames(int arity,String vars)
	{
		Element cons=new Element("constraint");
		nbCons++;
		
		//Les parametres	
        
		String pText="["+ vars +"]";
		Element param= new Element("parameters");
		param.setText(pText);
		cons.addContent(param);
		
		//Les attributs
		Attribute name= new Attribute("name", "OCLAllDiffNames");
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+arity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:allDifferent");
		cons.setAttribute(refe);
		
		constraints.addContent(cons);
	
	}

	//Contrainte OCL : Ocltype
	public void GenOCLtypePred()
	{
		//La relation d'implication
		Element pre1= new Element("relation");
		nbRel++;
		
		String tuples="";
		int nbtup=0;
		
		for (EClass c : lesClasses)
		{
			if(c.getName().equals("Arc"))
			{
				int pi= r.getClassIndex("Place");
				int ti= r.getClassIndex("Transition");
				
				int a=  domaineSum(pi-1)+1;
				int b= domaineSum(pi-1) + sizesMin.get(pi-1); 
				
				int cc=  domaineSum(ti-1)+1;
				int d= domaineSum(ti-1) + sizesMin.get(ti-1); 
		        
				int i=a,j=b;
				
				for(i=a;i<=b;i++)
				{
					for(j=cc;j<=d;j++)
					{
						tuples= tuples+ " ("+i+","+j+")";
						nbtup++;
					}
				}
				
				
				for(i=cc;i<=d;i++)
				{
					for(j=a;j<=b;j++)
					{
						tuples+= " ("+i+","+j+")";
						nbtup++;
					}
				}
			}
		}
		pre1.setText(tuples);
		Attribute refe1= new Attribute("name", "ocltypeRelation");
		Attribute refe2= new Attribute("nbTuples", ""+nbtup);
		Attribute refe3= new Attribute("sementics", "supports");
		pre1.setAttribute(refe1);
		pre1.setAttribute(refe2);
		pre1.setAttribute(refe3);
		relations.addContent(pre1);
	}

	public void GenOcltypeConstraint()
	{
		//Les contraintes OCl type de la classe Arc
		for (EClass c : lesClasses)
		{
			if(c.getName().equals("Arc"))
			{
				int pi= r.getClassIndex("Place");
				int ti= r.getClassIndex("Transition");
				
				int a=  domaineSum(pi-1)+1;
				int b= domaineSum(pi-1) + sizesMin.get(pi-1); 
				
				int cc=  domaineSum(ti-1)+1;
				int d= domaineSum(ti-1) + sizesMin.get(ti-1); 
				
				
				//Création des contraintes
				for(int j=1;j<=sizes.get(r.getClassIndex(c)-1);j++)
				{
					Element cons = new Element("constraint");
					nbCons++;
								
					Attribute name= new Attribute("name", "ocltypeCon"+j);
					cons.setAttribute(name);
					Attribute Scope= new Attribute("scope", "Id_Arc_"+j+"_source_1"+" Id_Arc_"+j+"_target_1 ");
					cons.setAttribute(Scope);
					Attribute refe= new Attribute("reference", "ocltypeRelation");
					cons.setAttribute(refe);
					
					constraints.addContent(cons);
				}
			}
		}
	    		
		
	}
	
	///////////////////////////////////////////////////
	//////////////////////////////////////////////
	//////////////////////////////////////////
	/////////////////////////////////
	////////////////////////////               Ordonner les varialbes des références
	//////////////////////
	//////////////
	///////

	public void GenPredOrd()
	{
		//La relation d'implication
				Element pre1= new Element("predicate");
				nbPre++;
				
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
	
	public void GenConstOrd(String v1,String v2)
	{
		Element cons = new Element("constraint");
		nbCons++;
		
		Element param= new Element("parameters");
		param.setText(v2+" "+v1);
		
		cons.addContent(param);
		
		Attribute name= new Attribute("name", "Cons"+v1+v2);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", "2");
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", v1+" "+v2);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "inf");
		cons.setAttribute(refe);
		
		constraints.addContent(cons);
	
	}
	
	
	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////
	///////////////////////////          Generate CSP XML Document Method 
	///////////////////////
	///////////////////
	///////////////
	////////////
	/*****
	 * 
	 * Generate Meta-model CSP XML code  
	 * 
	 */
	
	public Document GenerateXCSP(String file)
	{
		//Construire les dommaines des classes
		GenDomains();
	
		
		//Construire les domaines des features des classes
		GenFeaturesDomains(FeatureBound);//FeatureBound);
		
		//Construire les domaines des références
	//	GenRefsDomains(RefsBound);
		GenRefsDomainsJokers(RefsBound);
		
		//Construire les variables des classes et de leurs Features et de leurs Références
		GenVars();
							
        //Construire la contrainte Gcc sur les classes
  //  	GenClassConstraint();
		
		//Construire la contrainte de non allocation des References si l'instance = 0
//		GenNoAllocConstraintPred();
	
    	//La contrainte qui ordonne les var des références
    	GenPredOrd();
    	
//    	if(racine.equals("PetriNet"))
//    	{
//    		//Contrainte OCL All diff des Rdp
//    		GenAllDiffnames(Alldiffnames, alldiffnames);
//    	
//    		//Contrainte OCL: Ocltype des Rdp
//    //		GenOCLtypePred();
//    //		GenOcltypeConstraint();
//    	}
    
		//Nombre de variables, de domaines, ... etc
		Attribute nbvars= new Attribute("nbVariables", ""+nbVars);
		Attribute nbdom= new Attribute("nbDomains", ""+nbDoms);
		Attribute nbpre= new Attribute("nbPredicates", ""+nbPre);
		Attribute nbrel= new Attribute("nbRelationss", ""+nbRel);
		Attribute nbcons= new Attribute("nbConstraints", ""+nbCons);
		domains.setAttribute(nbdom);
		variables.setAttribute(nbvars);
		predicates.setAttribute(nbpre);
		relations.setAttribute(nbrel);
		constraints.setAttribute(nbcons);
		
		//Ajout des Variables, des domaines, des contraintes au XML
		instance.addContent(domains);
		instance.addContent(variables);
//		instance.addContent(relations);
		instance.addContent(predicates);
		instance.addContent(constraints);
		
		//Sauver le XML
		saveXML(getXCSPinstance(), file);
		return getXCSPinstance();
	}
	
	
	
	///////////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////
	///////////////////////////////////
	///////////////////////////////
	///////////////////////////          Manipulate XML (SAVING, READING) 
	///////////////////////
	///////////////////
	///////////////
	////////////

	public void saveXML(Document XCSP,String file)
	{
		
		 try
		   {
		      //On utilise ici un affichage classique avec getPrettyFormat()
		      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		      //Remarquez qu'il suffit simplement de créer une instance de FileOutputStream
		      //avec en argument le nom du fichier pour effectuer la sérialisation.
		      sortie.output(XCSP, new FileOutputStream(file));
		   }
		   catch (java.io.IOException e){}
		
	}
	
	public void PrintIt()
	{
		try
		   {
		      //On utilise ici un affichage classique avec getPrettyFormat()
		      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		      sortie.output(getXCSPinstance(), System.out);
		   }
		   catch (java.io.IOException e){}
	}

	public static org.jdom2.Document getXCSPinstance() {
		return XCSPinstance;
	}

	public static void setXCSPinstance(org.jdom2.Document xCSPinstance) {
		XCSPinstance = xCSPinstance;
	}
	
	
	
	
}
