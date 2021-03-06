package org.grimm.gui;

public class Help {

	public static void printHelp() {

		System.out.println("GRIMM");
		
		System.out.println(" is a tool for automated model generation (meta-model instantiation)");
		
		System.out.println("");
		System.out.println("USAGE");
		System.out.println("");
		System.out.println("    java -jar grimm.jar [options]");
		
		System.out.println("");
		System.out.println("OPTIONS");
		
//		System.out.println("");
//		System.out.println(" ");
//		System.out.println("     ");
//	
		System.out.println("");
		System.out.println("    help (shortcut: h)");
		System.out.println("       print help page");
		
		
		System.out.println("");
		System.out.println("    parameter filePath.params (shortcut: p)");
		System.out.println("        Create a pre-filled parameter file (=filePath.params) for generation");
		System.out.println("         It contains general information on generation: ");
		System.out.println("          meta-model, OCL file, root class, output format, number of solutions, etc");
		
		
		System.out.println("");
		System.out.println("    config filePath.grimm metamodel.ecore rootClass (shortcut: c)");
		System.out.println("        create a configuration file (=filePath.grimm) for the given meta-model");
		System.out.println("         It contains specific information on a meta-model:");
		System.out.println("          number of instances for classes, custom domains for attributes, references UpperBound");
		
		System.out.println("");
		System.out.println("    generate filePath.params (shortcut: g)");
		System.out.println("         Generate models using the given parameters file");
	
		System.out.println("");
		System.out.println("    generate--chr filePath.params (shortcut: chr)");
		System.out.println("         Generate models and corresponding chromosomes using the given parameters file");
		
		System.out.println("");
		System.out.println("    validate chromosome.chr (shortcut: v)");
		System.out.println("         Checks the validity of a chromosome and generates a corresponding model");
	
		
		System.out.println("");
		System.out.println("QUICK START");
		
		System.out.println("");
		System.out.println("     a quick start example is included in the version of GRIMM you downloaded");
		System.out.println("");
		System.out.println("       java -jar grimm.jar g parameters-files/quick-tests/test1-quick-dot.params");
		System.out.println("       java -jar grimm.jar g parameters-files/quick-tests/test1-quick-xmi.params");
		System.out.println("");
		System.out.println("     The results of execution are stored in Compo folder (named after rootClass)");
		
		System.out.println("");
		System.out.println("CREDITS");
		System.out.println("    version : v6.3-d20122018 (December 20, 2018)");
		System.out.println("    author  : grimm is developped by Adel Ferdjoukh (ferdjoukh@gmail.com)");
		
	}
}
