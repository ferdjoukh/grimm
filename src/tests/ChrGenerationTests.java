package tests;

import org.junit.Test;
import gui.GrimmLauncher;

public class ChrGenerationTests {

	//////////////////////////////
	//     Scaffoldgraph.ecore
	//////////////////////////////
	@Test
	public void newScaffold1() throws Exception{
		String [] args= {"chr","parameters-files/ScaffoldGraph/ScaffoldGraph1.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void validate() throws Exception{
		String [] args= {"v","ScaffoldGraph/ScaffoldGraph-111348-2012181.chr"};
		GrimmLauncher.main(args);
	}

}
