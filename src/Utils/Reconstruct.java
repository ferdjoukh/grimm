package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import Ecore.MetaModelReader;
import Ecore2CSP.XCSPgenerator;

public class Reconstruct {

	MetaModelReader r;
	String ModelFile;
	String racine;
	String InstanceFile;
	String Model;
	int refB;
	ArrayList<Integer> sizes;
	ArrayList<Integer> sizesMin;
	
	public Reconstruct(String ModelFile, String racine,String InstanceFile,String Model){
		
		this.ModelFile=ModelFile;
		this.racine=racine;
		this.InstanceFile=InstanceFile;
		this.Model=Model;		
	}
	
	public void CallCSPGenrator(int lb,int ub,int rb,int sym)
	{
		r= new MetaModelReader(ModelFile, racine,lb,ub);
		this.refB=rb;
		
		this.sizes=r.getClassSize();
		this.sizesMin=r.getClassSizeMin();
		
		
		long debut; double duree;
		debut=System.nanoTime();
				
		//Sa racine est Petrinet
	    XCSPgenerator generation= new XCSPgenerator(r,rb,sym);
		generation.generateXCSP(InstanceFile);
				
		duree=(System.nanoTime()-debut)/1000000;
		System.out.println("Le temps de génération du XCSP = [ "+ duree+ " Millisecondes ]\n");
				
		///////////////////////////////////////////////////////////////
		//Ececuter le solveur
		////////////////////////////////////////////////////////////
		BufferedReader r;
		r=Execute(InstanceFile,"mac.xml");
		ArrayList<Integer> vals= new ArrayList<Integer>();
		vals=RValues(r);
		int vv=0;
		/*for (int i:vals)
		{
			System.out.println(i);
			vv++;
		}*/
		
		
		///////////////////////////////////////////////////////////////
		//Reconstruire une solution
		/////////////////////////////////////////////////////////////
		if(vals.size()!=0)
		{
		EObject object= FindModel(vals);
		ValidModel(Model,object);
		}
	
		
	}
	
	public BufferedReader Execute(String Instancefile,String conFile){

		int nbsol=1;
	//	String cmd = "java -jar abscon109.jar " +conFile+" 1 XSax " + Instancefile;
		String cmd = "java -jar abssol.jar " + Instancefile;// +" -s="+ nbsol;

		Process p = null;
			try {
				p = Runtime.getRuntime().exec(cmd);
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				return reader;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
	        return null;
	}
	
	public ArrayList<Integer> RValues(BufferedReader reader){
		ArrayList<Integer> vals= new ArrayList<Integer>();
		String line;
		String du = null;
		int found=0;
		
		try {
			while((line = reader.readLine()) != null) {
				System.out.println(line);
				if(line.startsWith("s SATISFIABLE")) {	        
			    	
			    	found=1;
			    }
			    else  if(line.startsWith("v ")){
			    	int i=2;int varl=0;
			    	while(i<line.length())
			    	{
			    		varl=line.indexOf(" ", i);
			    		vals.add(Integer.parseInt((line.substring(i, varl))));
			    		i= varl+1;
			    	}
			    } 
			    else if(line.startsWith("   totalWckTime"))
			    {
			    	int kk=line.indexOf("CpuTime=");
			    	du="Durée="+line.subSequence(kk+8, line.length())+" CPUtime]";
			    }
			}
			if(found==0)
				System.out.println("Le modèle n'a pu être satisfait !!");
			else
			{
				System.out.print("Success !! Un modèle valide a été trouvé par le solveur !! [ ");
				System.out.println(du);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    	
		}
		return vals;
	}
	
	public EObject FindModel(ArrayList<Integer> values)
	{
		ArrayList<Integer> vals= values;
		int variable=0;
		EPackage pack= r.getModelPackage();
		List<EClass> cls= r.getClasses();
	//	ArrayList<Integer> sizes= r.getClassSize();
		EObject o = null;
		int lb=0,ub=0;
		
		//Les instances de classes
		ArrayList<ClassInstance> mesInst= new ArrayList<ClassInstance>(); 
		ArrayList<ClassInstance> mesInstLiees= new ArrayList<ClassInstance>(); 
		
		
		//Premier passage: Construire les instances
		for(EClass c: cls)
		{
			if(sizes.get(r.getClassIndex(c)-1)==1)
			{
				//Instance de la racine
				o= pack.getEFactoryInstance().create(c);
			//	variable++;
				
				//ses attributs
				for(EAttribute a:r.getAllAttributesFromClass(c))
				{
					if(a.getEType().getName()=="EString")
						o.eSet(a, r.getBasePackage().getName()+"_"+vals.get(variable).toString());
				    else if (a.getEType().getName()=="EInt")
					    o.eSet(a, vals.get(variable));	
				    else
					{
						//C'est une Enumération !!!
				    	EEnum enume= null;
						try{enume=(EEnum) a.getEType();}catch(Exception e){}
						EClass etype=null;
						try{etype=(EClass) a.getEType();}catch(Exception e){}				
						if(enume!=null)
						{
							o.eSet(a, enume.getEEnumLiteral(vals.get(variable)-1));
						}
						if(etype!=null)
							System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
					}
					variable++;
				}
				
				//ses liens
				for (EReference ref: r.getAllReferencesFromClass(c))
    			{
						int zz=ref.getUpperBound();
    					if (zz==-1)
    					{	//zz=5;
    						if(!ref.getEReferenceType().isAbstract())
    							zz=refB/2;
    						else
    							zz=refB/2;
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
			
				lb=  r.domaineSum(r.getClassIndex(c)-1)+1;
				ub=  r.domaineSum(r.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{
					//int variablei=0;
					EObject i;
					System.out.println("à l'entrée var="+variable);
					if(vals.get(variable)!=0)
					{
						i= pack.getEFactoryInstance().create(c);
						System.out.println("instance "+ j+" de "+ c.getName()+" "+i.toString());
					//  variable++;
						//ses attributs
						for(EAttribute a:r.getAllAttributesFromClass(c))
						{
							if(a.getEType().getName()=="EString")
								{
								i.eSet(a, c.getName()+"_"+j+"_"+a.getName()+"_"+ vals.get(variable).toString());
							    System.out.println("Attribut "+a.getName()+ " de instance "+ j+" de "+ c.getName()+" "+i.toString());
								}
							else if (a.getEType().getName()=="EInt")
								{
								i.eSet(a, vals.get(variable));
							    System.out.println("="+vals.get(variable)+" Attribut "+a.getName()+ " de instance "+ j+" de "+ c.getName()+" "+i.toString());
								}
							else
							{
								//C'est une Enumération !!!
								EEnum enume= null;
								try{enume=(EEnum) a.getEType();}catch(Exception e){}
								EClass etype=null;
								try{etype=(EClass) a.getEType();}catch(Exception e){}				
								if(enume!=null)
								{
									i.eSet(a, enume.getEEnumLiteral(vals.get(variable)-1));
								}
								if(etype!=null)
									System.out.println("Attention: L'attribut "+a.getName()+ " de la classe "+c.getName()+ " est de type objet("+a.getEType().getName()+") doit être remplacé par une référence !!");
							}
							variable++;
						}
						System.out.println("ap feature var="+variable);
				    
						//Ses références
						//ses liens
						for (EReference ref: r.getAllReferencesFromClass(c))
						{
							int zz=ref.getUpperBound();
	    					if (zz==-1)
	    					{	//zz=5;
	    						if(ref.getEReferenceType().isAbstract())
	    							zz=refB/2;
	    						else
	    							zz=refB/2;
	    					}
	    					for(int z=1;z<=zz;z++)
	    					{
	    						//ICI créer une variable pointeur du type dst
	    						variable++;
	    					}
						}
						mesInst.add(new ClassInstance(j, i));
						System.out.println("après refs var="+variable);
					}
					else //Incrémente sans rien créer car Oid=0 
					{
				//		variable++;
						//ses attributs
						for(EAttribute a:r.getAllAttributesFromClass(c))
						{
							variable++;
						}
						//Ses références
						//ses liens
						for (EReference ref: r.getAllReferencesFromClass(c))
		    			{
							    int zz=ref.getUpperBound();
		    					if (zz==-1) 
		    					{	//zz=5;
		    						if(ref.getEReferenceType().isAbstract())
		    							zz=refB;
		    						else
		    							zz=refB/2;
		    					}	
		    					for(int z=1;z<=zz;z++)variable++;
		    			}
					    
					}
				}
			}
		} //Fin du premier passage
		
		variable=0;
	  //  System.out.println("MesInsts size="+mesInst.size());
		
		//Deuxième passage: Construire les pointeurs des références
		for(EClass c: cls)
		{
			int vari=0;
			if(sizes.get(r.getClassIndex(c)-1)==1)
			{
				//Instance de la racine
				//o= pack.getEFactoryInstance().create(c);
				vari= variable;
		//		variable++;
						
				//ses attributs
				for(EAttribute a:r.getAllAttributesFromClass(c)) variable++;
						
				//ses liens
				for (EReference ref: r.getAllReferencesFromClass(c))
		    	{
					int zz=ref.getUpperBound();
		    	    if (zz==-1) 
		    	    {	//zz=5;
						if(ref.getEReferenceType().isAbstract())
							zz=refB;
						else
							zz=refB/2;
					}
		   			
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
				lb=  r.domaineSum(r.getClassIndex(c)-1)+1;
				ub=  r.domaineSum(r.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{
					EObject i;
			//		if(vals.get(variable)!=0)
			//		{
						i=Utils.searchIns(mesInst, j);
						
		//				variable++;
						//ses attributs
						for(EAttribute a:r.getAllAttributesFromClass(c))
							variable++;
						
						//Ses références
						//ses liens
						for (EReference ref: r.getAllReferencesFromClass(c))
			    		{
						    int zz=ref.getUpperBound();
			    			if (zz==-1) 
			    			{	//zz=5;
	    						if(ref.getEReferenceType().isAbstract())
	    							zz=refB/2;
	    						else
	    							zz=refB/2;
	    					}
			    			EClass dst= ref.getEReferenceType();
			    			List<EObject> rr= new ArrayList<EObject>(); 
			    			
			    			if(zz==1)
			    			{
			    				if(vals.get(variable)!=0)
			    				{
			    					try{
			    					i.eSet(ref, Utils.searchIns(mesInst, vals.get(variable)));
			    					}catch(Exception e){}
			    				}
			    				variable++;
			    			}
			    			else
			    			{
			    				for(int z=1;z<=zz;z++)
			    				{
			    					//ICI créer une variable pointeur d'un type ?? (l'un des sous types la)
			    					
			    					if(vals.get(variable)!=0)
			    					{
			    						rr.add(Utils.searchIns(mesInst, vals.get(variable)));
			    					}
			    					
			    					variable++;
			    				}
			   				i.eSet(ref, rr);
			    			}						
			    		}
			//			Utils.replace(mesInst, vari, i);
						mesInstLiees.add(new ClassInstance(j, i));
						
			/*		}
					else //Incrémente sans rien créer car Oid=0 
					{
			//			variable++;
						//ses attributs
						for(EAttribute a:r.getAllAttributesFromClass(c))variable++;
						//Ses références
						for (EReference ref: r.getAllReferencesFromClass(c))
			    		{
						    int zz=ref.getUpperBound();
			    			if (zz==-1) 
			    			{	//zz=5;
	    						if(ref.getEReferenceType().isAbstract())
	    							zz=refB/2;
	    						else
	    							zz=refB/2;
	    					}
			    			for(int z=1;z<=zz;z++)variable++;
			    		}
					}*/
				}
			}
		}
		/////////////////////////////////////////////////////////////
		//////////////////////////////////
		//////////////////
		//Fin du 2ème passage
		
		variable=0;	
		///////////////////////////////////////////////////////////
		//////////////////////////////////
		/////////////////
		//3ème passage : construire les relations de compartimentage
		for(EClass c: cls)
		{
			
			if(sizes.get(r.getClassIndex(c)-1)==1)
			{
				//Instance de la racine
				//o= pack.getEFactoryInstance().create(c);
				
		//		variable++;
						
				//ses attributs
				for(EAttribute a:r.getAllAttributesFromClass(c)) variable++;
						
				//ses liens
				for (EReference ref: r.getAllReferencesFromClass(c))
		    	{
					int zz=ref.getUpperBound();
		    	    if (zz==-1)	
		    	    {	//zz=5;
						if(ref.getEReferenceType().isAbstract())
							zz=refB/2;
						else
							zz=refB/2;
					}
		   			List<EObject> rr= new ArrayList<EObject>(); 
		    		
		   			if(zz==1)
		   			{
		   				if(vals.get(variable)!=0)
	   						o.eSet(ref, Utils.searchIns(mesInstLiees, vals.get(variable)));
		   				variable++;
		   			}	
		   			else
		   			{
		   				for(int z=1;z<=zz;z++)
		   				{
		   					//ICI créer une variable pointeur de type dst
		   					if(vals.get(variable)!=0)
		   					{
		   						try{
		   						EObject oo= Utils.searchIns(mesInst, vals.get(variable));
		   						if(oo!=null)
		   						rr.add(oo);
		   						}catch(Exception e){System.out.println("Erreur de collecte");}	
		   				       }
		   			        
		   					variable++;
		   				}
		   				try{
		  //    			System.out.println("rr"+ rr.toString());
		   					o.eSet(ref, rr);
		    //	     		o.eSet(ref, Utils.returnAll(mesInstLiees));
		   				}catch(Exception e){System.out.println("Classe:"+c.getName()+" Référence:"+ref.getName()+ "   Erreur d'ajout");}
		    		}
		    	}
			}
			else
			{
				//Les autres classes
				lb=  r.domaineSum(r.getClassIndex(c)-1)+1;
				ub=  r.domaineSum(r.getClassIndex(c)); 
				
				for(int j=lb;j<=ub;j++)
				{	
			//		variable++;
						//ses attributs
						for(EAttribute a:r.getAllAttributesFromClass(c))variable++;
						
						//Ses références
						//ses liens
						for (EReference ref: r.getAllReferencesFromClass(c))
			    		{
						    int zz=ref.getUpperBound();
			    			if (zz==-1) 
			    			{	//zz=5;
	    						if(ref.getEReferenceType().isAbstract())
	    							zz=refB/2;
	    						else
	    							zz=refB/2;
	    					}
			    				for(int z=1;z<=zz;z++)
			    					variable++;
			    		}						
			    		
					}
			}
		}
		////////////////////////////////////////////////////////////
		////////////////////////////////
		////////////////
		//Fin du 3ème passage
	    
		return o;
		
	}
	
	 public void ValidModel(String s,EObject o)
	 {
		 ResourceSet resourceSet=new ResourceSetImpl();
		 Resource resource= r.getModelResource();
		 EPackage pack= r.getModelPackage();
		 
		 try
		 {
			 resource.load(null);
			 resourceSet = new ResourceSetImpl();
			 resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
			 put("xmi",new XMIResourceFactoryImpl());
			 URI uri=URI.createURI(s);
			 resource=resourceSet.createResource(uri);
			 resource.getContents().add(o);
			 resource.save(null); 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();	 
			 System.out.println("\nLe modèle n'a pu être correctement construit !");
		 }
 }
}