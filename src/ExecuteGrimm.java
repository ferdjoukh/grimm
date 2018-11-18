import java.io.File;
import java.io.IOException;
import CSP2Model.CSP2XMI;
import CSP2Model.CSP2dot;
import CSP2Model.ModelBuilder;
import Ecore2CSP.ConfigFileGenerator;
import exceptions.MetaModelNotFoundException;

public class ExecuteGrimm {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MetaModelNotFoundException 
	 */
	public static void main(String[] args) throws IOException, MetaModelNotFoundException {
		// TODO Auto-generated method stub
      		
		String mm = "";
		String root = "";
		int lb=0;
		int ub=0;
		int rb=0;
		String confFilePath= "";
		String oclFilePath="";		
		int sym= 0;
		int sol=1;
		int Nsol=1;
		int somme=0;
		
		char xmiOrDot='0';		
		
		/////////////////////////////////////////////////
		////////////////////////////////////////////////
		//////////////////////////////
		/////////////////////////////      
		//
		//Put help if no parameters
		//
		///////////////////////////
		//////////////////////////
		/////////////////////////
		
		if(args.length==0)
		{
			
			System.out.println("Grimm is a model generation tool !");
			
			System.out.println("\n\nGeneration of a configuration file:");
			System.out.println("\tjava -jar grimm.jar -mm=metaModel.ecore -root=class");
			
			System.out.println("\n\nMondatory Parameters:");
			System.out.println("\t-mm=mm.ecore: File path of chosen meta-model.");
			System.out.println("\t-root=rootClass: Root class of chosen meta-model.");
			
			
			System.out.println("\n\nMondatory Parameters for model generation:");
			System.out.println("\t-lb=x, x is instances per class lower bound.");
			System.out.println("\t-ub=x, x is instances per class upper bound.");
			System.out.println("\t-rb=x, x is references upper bound.");
			System.out.println("\tOr:");
			System.out.println("\t-cfg=x, x is the modified configuration file (mm.grimm).");
			
			System.out.println("\n\nMondatory Parameters of file format for generated models:");
			System.out.println("");
			System.out.println("\t-xmi, to generate a model in xmi format.");
			System.out.println("\tOr:");
			System.out.println("\t-dot, to generate a model in dot format.");
			
						
			
			System.out.println("\n\nOptional Parameters for model generation:");
			System.out.println("\t-sym={0,1}, To break or not Symmetries (default =0).");
			System.out.println("\t-ocl=x, x is ocl constraints file path.");
			System.out.println("\t-sol=x, generate the xth solution (default =1).");
			System.out.println("\t-Nsol=x, generate x solutions (default =1).");
			System.out.println("\t-distance={h,..}, choose the distance function (h=hamming, ...).");
			
			
			
			System.out.println("\n\nExamples:");
			System.out.println("\tjava -jar grimm.jar -mm=test.ecore -root=Compo");
			System.out.println("\tjava -jar grimm.jar -mm=test.ecore -root=Compo -lb=2 -ub=2 -rb=4 -xmi");
			System.out.println("\tjava -jar grimm.jar -mm=test.ecore -root=Compo -cfg=test.grimm -dot");
			System.out.println("\tjava -jar grimm.jar -mm=test.ecore -root=Compo -cfg=test.grimm -sym=1 -ocl=test.ocl -xmi");
			System.out.println("\tjava -jar grimm.jar -mm=test.ecore -root=Compo -lb=2 -ub=2 -rb=4 -sym=1 -ocl=test.ocl -dot");
			
			System.out.println("");
			
			
			
		//	CSP2dot  c2d=new CSP2dot("model/maps.ecore","map","map/map.xml","map/map.dot");
		//   c2d.CallCSPGenrator(2,2,4,1);
		//   c2d.CallCSPGenrator(2,3,4,0);
		}
		else
		{		
			
			/*
			 * 
			 * Treating the input parameters
			 *        -mm=1
			 *        -rootClass=2
			 *        -lb=5
			 *        -ub=7
			 *        -rb=11
			 *        
			 *        -configFile=3
			 */
			String start;
			System.out.println("Grimm model generation Tool is now lunched");
			
			
			for( int i=0; i<args.length;i++)
			{
				//System.out.println("param "+i+" = "+args[i]);
				start= args[i].substring(0, args[i].lastIndexOf('=')+1);
				if(start.equals(""))
					start=args[i];
				
				switch(start)
				{
				case "-mm=": {
								mm= args[i].substring(4);
						//		System.out.println("mm ="+mm);
								somme = somme + 1;
							}
						break;
				
				case "-root=": {
										root= args[i].substring(6);
						//				System.out.println("RootClass ="+rootClass);
										somme = somme + 2;
										new File(root).mkdir();										
								   }
						break;		
	
				case "-lb=": {
						//		System.out.println("Lower bound found ="+args[i].substring(4));
								lb= Integer.parseInt(args[i].substring(4));
								somme = somme + 5;
							}
						break;
						
				case "-ub=": {
						//		System.out.println("Upper bound found ="+args[i].substring(4));
								ub=	Integer.parseInt(args[i].substring(4));
								somme = somme + 7;
							}
						break;
						
				case "-rb=": {
						//		System.out.println("Ref bound found ="+args[i].substring(4));
								rb=	Integer.parseInt(args[i].substring(4));
								somme = somme + 11;
							}
						break;		
				
				case "-sym=": {
								sym=	Integer.parseInt(args[i].substring(5));
						//		System.out.println("Symmetries ? ="+sym);
							 }
						break;
				
				case "-cfg=": {
										confFilePath=	args[i].substring(5);
					//					System.out.println("configFilePath ="+confFilePath);
										somme = somme + 3;
	 			 					}
						break;			
		
				case "-ocl=": {
									oclFilePath=	args[i].substring(9);
					//				System.out.println("oclFilePath ="+oclFilePath);			
								 }
						break;			
		 
				case "-sol=": {
								
								sol=	Integer.parseInt(args[i].substring(5));
								if (sol<=0)
									sol=1;
								System.out.println("Solution number ="+sol);								
				 			 }
						break;
				
				case "-Nsol=": {
								Nsol=	Integer.parseInt(args[i].substring(6));
					//			System.out.println("How many Solutions ="+ Nsol);		
	 			 			 }
						break;	
				
				case "-xmi": {
				    xmiOrDot= 'x';
				}
				break;
				
				case "-dot": {
				    xmiOrDot= 'd';
				}
				break;
			
						
				default: System.out.println("Invalid Parameter: "+args[i]+" !!");	
				}
			}
			
			
			///////////////////////////////// /////
			// What to do now ? A constructor for each set of possible parameters
			// After testing a produced String
			//////////////////////////////// ////
			ModelBuilder mb;
			
			
			switch (somme)
			{
			    case 3: {//Generate the config File
			    	System.out.print("Generation of a configuration file for meta-model "+mm +"...");
			    	ConfigFileGenerator gcf= new ConfigFileGenerator(mm,root);
			    	gcf.createConfigFile();
			    	
			    	
			    }
			    break;
			    
				case 6: {//somme is equal to 6: Generate with a configFile
					System.out.println("\tMeta-model: "+mm+", configuration File: "+ confFilePath);
			    	
					if(xmiOrDot=='0')
					{
						System.out.println("\tError: You must choose either to generate -xmi files ot -dot files !");
						return;
					}
					if(xmiOrDot=='x')
					{
						mb=new CSP2XMI(mm,root,root+"/"+root+".xml", oclFilePath);
						mb.generateModel(confFilePath, sym, Nsol);
					   return;	
					}
					
					if(xmiOrDot=='d')
					{
						mb=new CSP2dot(mm,root,root+"/"+root+".xml", oclFilePath);
						mb.generateModel(confFilePath, sym, Nsol);
						return;
					}
					
					
				} 
				break;
				case 26: {//somme is equal to 26: Generate with the usual parameters
					System.out.println("\tMeta-model: "+mm+", prameters: -lb="+ lb + " -ub="+ ub+ " -rb="+ rb);
			    	
			    	
					
					if(xmiOrDot=='0')
					{
						System.out.println("\tError: You must choose either to generate -xmi files ot -dot files !");
						return;
					}
					if(xmiOrDot=='x')
					{
					   mb= new CSP2XMI(mm,root,root+"/"+root+".xml", oclFilePath);
					   mb.generateModel(lb, ub, rb, sym, sol);
					   return;	
					}
					
					if(xmiOrDot=='d')
					{
						mb=new CSP2dot(mm,root,root+"/"+root+".xml", oclFilePath);
						mb.generateModel(lb, ub, rb, sym, sol);
						return;
					}
					
				}
				break;
				
				default: {
					      System.out.println("\tInvalid Combination of prameters");
					      System.out.println("\n\tTry execution whitout parameters for help");
				}
			
			}
			
		}	 
	}

}
