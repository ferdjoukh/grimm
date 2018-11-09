package tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import exceptions.CSPSolverNotFoundException;
import gui.GrimmLauncher;

public class EcoreMMUnchangeableValuesTests {

	@Test
	public void generationEcore1() throws Exception{
		String [] args= {"g","tests/generation/ecore1.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void generateMaps() throws Exception{
		String [] args= {"g","tests/generation/maps1.params"};
		//GrimmLauncher.main(args);
	}
}
