package tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import exceptions.CSPSolverNotFoundException;
import gui.GrimmLauncher;

public class EcoreMMUnchangeableValuesTests {

	@Test
	public void generationEcore4ATL() throws Exception{
		String [] args= {"g","tests/generation/ecore4ATL.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void generationEcore4ATLconf() throws Exception{
		String [] args= {"g","tests/generation/ecore4ATL-conf.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void generationScaffold() throws Exception{
		String [] args= {"g","tests/generation/scaffold1.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void generateMaps() throws Exception{
		String [] args= {"g","tests/generation/maps1.params"};
		GrimmLauncher.main(args);
	}
}
