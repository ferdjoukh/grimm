package tests;

import java.io.IOException;

import org.junit.Test;

import exceptions.CSPSolverNotFoundException;
import gui.GrimmLauncher;

public class ModelGenerationTests {

	
	@Test
	public void mapCustomAttributesDot() throws Exception{
		String [] args= {"g","parameters-files/maps-custom-attributes-dot.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void Ecore1() throws Exception{
		String [] args= {"g","tests/generation/ecore1.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void generationEcore4ATL() throws Exception{
		String [] args= {"g","tests/generation/ecore4ATL.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void generationEcore4ATLconf1() throws Exception{
		String [] args= {"g","tests/generation/ecore4ATL-conf.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void ecore_conf_use_domains() throws Exception{
		String [] args= {"g","tests/generation/ecore4ATL-conf-user-domains.params"};
		GrimmLauncher.main(args);
	}

	@Test
	public void generationScaffold() throws Exception{
		String [] args= {"g","tests/generation/scaffold1.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void generateMaps() throws Exception{
		String [] args= {"g","tests/generation/maps2.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void mapsDot() throws Exception{
		String [] args= {"g","tests/generation/maps-dot.params"};
		GrimmLauncher.main(args);
	}
}
