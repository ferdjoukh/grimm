package gui;

public class Help {

	public static void printHelp() {

		System.out.println("GRIMM");
		
		System.out.println(" is a tool for automated model generation (meta-model instantiation)");
		
		System.out.println("");
		System.out.println("    java -jar grimm.jar [options]");
		
		System.out.println("");
		System.out.println("OPTIONS");
		
//		System.out.println("");
//		System.out.println(" ");
//		System.out.println("     ");
//	
		System.out.println("");
		System.out.println(" help (shortcut: h)");
		System.out.println("     print help page");
		
		
		System.out.println("");
		System.out.println(" parameter filePath (shortcut: p)");
		System.out.println("     Create a pre-filled parameter file (=filePath.params) for generation");
		System.out.println("     It contains general information on generation: ");
		System.out.println("       meta-model, OCL file, root class, output format, number of solutions, etc");
		
		
		System.out.println("");
		System.out.println(" config filePath metamodel.ecore rootClass (shortcut: c)");
		System.out.println("     create a configuration file (=filePath.grimm) for the given meta-model");
		System.out.println("     It contains specific information on a meta-model:");
		System.out.println("       number of instances for classes, domains for attributes probability distributions for diversity, etc");
		
		System.out.println("");
		System.out.println(" generate filePath.params (shortcut: g)");
		System.out.println("     Generate models using the given parameters file");
	
		
		System.out.println("");
		System.out.println("QUICK START");
		
		System.out.println("");
		System.out.println(" a quick start example is included in the version of GRIMM you downloaded");
		System.out.println("     java -jar grimm.jar g test.params");
		
		System.out.println("");
		System.out.println("EXAMPLES");
		System.out.println("");
		
	}
}
