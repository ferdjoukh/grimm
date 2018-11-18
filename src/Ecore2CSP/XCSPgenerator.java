package Ecore2CSP;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import Ecore.MetaModelReader;

public class XCSPgenerator {

	private Element instance= new Element("instance");
	private org.jdom2.Document XCSPinstance;
    private Element domains= new Element("domains");
    private Element variables= new Element("variables");
    private Element predicates= new Element("predicates");
    private Element relations= new Element("relations");
    private Element constraints= new Element("constraints");
	
    private MetaModelReader reader;
	private ArrayList<EClass> listOfClasses;
	private int numberOfVariables = 0;
	private int numberOfDomaines = 0;
	private int numberOfPredicates = 0;
	private int numberOfRelations = 0;
	private int numberOfConstraints = 0;
	private int referencesUB = 2;
	private int maxDomains = 0;
	private int symmetries = 1;
	
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
		this.listOfClasses= (ArrayList<EClass>) reader.getClasses();
		this.symmetries=sym;
	}
	
	public XCSPgenerator(MetaModelReader reader, int RefsBound, int sym){
		createRootElement(reader,sym);
		this.referencesUB=RefsBound;
	}
	
	public XCSPgenerator(MetaModelReader reader, ConfigFileReader cfr, int sym){
		createRootElement(reader,sym);
		this.referencesUB= cfr.getReferencesUB();	
	}
	
	/**
	 * This is the main method. It generates the CSP instance
	 * 
	 * @param file: filepath of CSP document (instance.xml)
	 * @return
	 */
	public void generateXCSP(String file){
		
		createFakeVariable();
		genenerateClassDomains();
		GenRefsDomainsJokers();
		GenVars();
		Element orderPredicate = CSPconstraint.createOrderingPredicate();
		predicates.addContent(orderPredicate);
		numberOfPredicates++;
		
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
	 * 	Create the fake variable
	 *  it is used to create an empty model when all config are 0  
	 */
	public void createFakeVariable() {
		
		Element variable= new Element("variable");
		Attribute name = new Attribute("name", "First");
		Attribute domain= new Attribute("domain", "First");
		variable.setAttribute(domain);
		variable.setAttribute(name);
		variables.addContent(variable);
		numberOfVariables++;
		
		//Create one domain	
		Element domainef= new Element("domain");
		Attribute nf=new Attribute("name", "First");
		domainef.setAttribute(nf);
		domainef.setText("-1");
		domains.addContent(domainef);
		numberOfDomaines++;
	}
	
	/**
	 *  Generate Class Domains
	 *   
	 */
	public void genenerateClassDomains(){
		
		for(EClass c: listOfClasses){	
			
			int domainStart=  reader.domaineSum(reader.getClassIndex(c)-1)+1;
			int domainEnd=  reader.domaineSum(reader.getClassIndex(c)); 
			int nb= domainEnd-domainStart+1;
			String body;
			
			if(domainStart == domainEnd) body = ""+domainStart;
			else body = domainStart + ".." + domainEnd;
			
			Element domain= new Element("domain");
			Attribute name=new Attribute("name", "D_"+c.getName());
			Attribute nbValues=new Attribute("nbValues", ""+nb);
			domain.setAttribute(name);
			domain.setAttribute(nbValues);
			domain.setText(body);
			domains.addContent(domain);
			
			maxDomains=domainEnd;			
		}
	}
	
	/**
	 *
	 *     Features Domains
	 */
	public void GenFeaturesDomains(int borne){
		int fid=0;
		int i=1;
		
		for(EClass c: listOfClasses){
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
			
				if(enume!=null){
					domainef.setText("1.."+enume.getELiterals().size());
				}
				else{
      			   domainef.setText(-borne+".."+borne); 
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
	public int GenJokers(){
		int max=0;
		for(EClass c:listOfClasses){
			for(EReference ref: reader.getAllReferencesFromClass(c)){
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
	public void GenRefsDomains(int FeatureBound){
		
		int i=1;
		for(EClass c: listOfClasses){
			
			int refi=0;
		    // Création des domaines pour Les références
		    for (EReference ref: reader.getAllReferencesFromClass(c)){
		    	
		    	refi++;
		    	if (!ref.getEReferenceType().isAbstract()){
		    		
					int zz=ref.getUpperBound();
					if (zz==-1)
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
				else{
					//Union des domaines
					List<EClass> ddd=reader.getConcreteSubTypes(ref.getEReferenceType());
					String v="";
					int vv=0;
					int vvv=0;
					for (EClass cst: ddd){
						int cindex=reader.getClassIndex(cst);
    					int lB=  reader.domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
    					int uB=  reader.domaineSum(cindex); //(i)*10;   // size(class)= 10;
    					v+= " "+ lB+ ".."+ uB;
            			vv+= uB-lB+1;           		           					
					}
   					
					Element domaine= new Element("domain");
        			numberOfDomaines++;
        			Attribute n=new Attribute("name", "DCR_"+refi+"_"+i);
        			domaine.setAttribute(n);
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
	public void GenRefsDomainsJokers(){			
		int max= GenJokers();
		int JSLB= maxDomains+1;
		int JSUB= JSLB+max;
		String jokerString= ""+ JSLB+ ".."+JSUB+"" ;
		int nbvJ= JSUB-JSLB;
		
		int i=1;
		for(EClass c: listOfClasses){
			int refi=0;
			// Création des domaines pour Les références + Jokers
			for (EReference ref: reader.getAllReferencesFromClasswithOpposite(c)){
				refi++;
				if (!ref.getEReferenceType().isAbstract()){
					int zz=ref.getUpperBound();
					if (zz==-1)	zz = referencesUB;

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
					
					domaine.setText(v);
					int vv= uB-lB+1;

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
				else{
					//Union des domaines
					List<EClass> ddd=reader.getConcreteSubTypes(ref.getEReferenceType());
					String v="";
					int vv=0;
					int vvv=0;
					for (EClass cst: ddd){
						int cindex=reader.getClassIndex(cst);
						int lB=  reader.domaineSum(cindex-1)+1; //(i-1)*10+1; // size(class)= 10;
						int uB=  reader.domaineSum(cindex); //(i)*10;   // size(class)= 10;
						v+= " "+ lB+ ".."+ uB;
						vv+= uB-lB+1;           		           					
					}

					Element domaine= new Element("domain");
					numberOfDomaines++;
					Attribute n=new Attribute("name", "DCRJ_"+refi+"_"+i);
					domaine.setAttribute(n);
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
					domains.addContent(Dom);
				}
			}
			i++;
		}
	}
		
	/**
	 *  This method generates the variables of the CSP:
	 * 		- variables of references
	 *      - variables of EOpposite references
	 */
	public void GenVars(){
		
		int i=1;
		for(EClass c: listOfClasses){
			int refi=0;
			   
			//Pour la Gcc Eopposite			   
			int arityGcc=0;
		    int valsArityGcc=0;
		    String varsGcc="";
		    String valsGcc="";
		    
			for(int j=reader.domaineSum(reader.getClassIndex(c)-1)+1;j<=reader.domaineSum(reader.getClassIndex(c));j++){	
				
				//Create attributes variables 
				//for (EAttribute a: reader.getAllAttributesFromClass(c)){
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
    					
    					for(int z=1;z<=zz;z++){
	    					Element variabler= new Element("variable");
	                    	numberOfVariables++;
	                    	
	                    	Attribute namer= new Attribute("name", "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z);
	                    	Attribute domr;

	                    	/////////////////////////////////////////////////////////////////
	                    	//Contrainte ordonne des vars des références: Breaking symmetries
	                    	/////////////////////////////////////////////////////////////////
	                    	if(symmetries==1){
	                    		if(z>1){
	                    			int ertf=z-1;
	                    			Element orderingConstraint= CSPconstraint.createOrderingConstraint("Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z, 
	                    					    "Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+ertf);
	                    			constraints.addContent(orderingConstraint);
	                    			numberOfConstraints++;
	                    		}
	                    	}
	                    
	                    	////////////////////////////////
	                    	// Doamines avec les jokers 
	                    	////////////////////////////////                 
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
	                    	
	                    	if(symmetries==1){
	                    		if(z>1){
	                    			int ertf=z-1;
	                    			Element orderingConstraint = CSPconstraint.createOrderingConstraint("Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+z, 
	                    					"Id_"+c.getName()+"_"+j+"_"+ref.getName()+"_"+ertf);
	                    			constraints.addContent(orderingConstraint);
	                    			numberOfConstraints++;
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
						Element gccconstraint = CSPconstraint.createGcc(ref.getName()+"_"+ref.getEOpposite().getName(),
								arityGcc,valsArityGcc,varsGcc,valsGcc,
								ref.getEOpposite().getLowerBound(),upperBound);
						numberOfConstraints++;
						constraints.addContent(gccconstraint);
					}
				}
			}
			
			i++;
		}
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
