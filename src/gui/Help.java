package gui;

public class Help {

	public static void printHelp() {

		System.out.println("GRIMM");
		
		System.out.println(" is a tool for automated model generation (meta-model instantiation)");
		
		System.out.println("HELP");
		
		System.out.println("");
		System.out.println(" ");
		System.out.println("     ");
	
		System.out.println("");
		System.out.println(" help (shortcut: h)");
		System.out.println("     print help page");
		
		
		System.out.println("");
		System.out.println(" parameter filePath (shortcut: p)");
		System.out.println("     create a pre-filled parameter file (=filePath.params) for generation");
		
		
		System.out.println("");
		System.out.println(" config filePath metamodel.ecore rootClass (shortcut: c)");
		System.out.println("     create a configuration file (=filePath.grimm) for the given meta-model");
		
		System.out.println("");
		System.out.println(" generate filePath.params (shortcut: g)");
		System.out.println("     Generate models using the given parameters file");
	
		
		System.out.println("");
		System.out.println("QUICK START");
		
		System.out.println("");
		System.out.println(" a quick start example is included in the version of GRIMM you downloaded");
		System.out.println("");
		System.out.println(" write: java -jar grimm.jar g test.params");
		
		System.out.println("");
		System.out.println("EXAMPLES");
		System.out.println("");
		
	}
}
