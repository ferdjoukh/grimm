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

}
