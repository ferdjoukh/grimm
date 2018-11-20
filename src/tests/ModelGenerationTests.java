package tests;

import org.junit.Test;
import gui.GrimmLauncher;

public class ModelGenerationTests {

	//////////////////////////////
	//      maps.ecore
	//////////////////////////////
	@Test
	public void mapCustomAttributesDot() throws Exception{
		String [] args= {"g","parameters-files/maps-custom-attributes-dot.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void mapCustomAttributesXmiDot() throws Exception{
		String [] args= {"g","parameters-files/maps-custom-attributes-xmi.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void mapNoPublicSpacesXmi() throws Exception{
		//Testing size 0 for some classes
		String [] args= {"g","parameters-files/maps-noPublicSpaces.params"};
		GrimmLauncher.main(args);
	}
	
	//////////////////////////////
	//     Scaffoldgraph.ecore
	//////////////////////////////
	@Test
	public void scaffoldConfigDot() throws Exception{
		String [] args= {"g","parameters-files/scaffold-config-dot.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void scaffoldConfigXmi() throws Exception{
		String [] args= {"g","parameters-files/scaffold-config-xmi.params"};
		GrimmLauncher.main(args);
	}
	
	
	/////////////////////
	//  Ecore.ecore
	////////////////////
	@Test
	public void Ecore4ATL1() throws Exception{
		String [] args= {"g","parameters-files/ecore4ATL1.params"};
		GrimmLauncher.main(args);
	}
	
	//Old tests
	@Test
	public void Ecore1() throws Exception{
		String [] args= {"g","tests/generation/ecore1.params"};
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
