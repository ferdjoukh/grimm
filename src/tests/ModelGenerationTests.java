package tests;

import org.junit.Test;
import gui.GrimmLauncher;

public class ModelGenerationTests {

	//////////////////////////////
	//      maps.ecore
	//////////////////////////////
	@Test
	public void mapCustomAttributesDot() throws Exception{
		String [] args= {"g","parameters-files/maps/maps-custom-attributes-dot.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void mapCustomAttributesXmiDot() throws Exception{
		String [] args= {"g","parameters-files/maps/maps-custom-attributes-xmi.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void mapNoPublicSpacesXmi() throws Exception{
		//Testing size 0 for some classes
		String [] args= {"g","parameters-files/maps/maps-noPublicSpaces.params"};
		GrimmLauncher.main(args);
	}
	
	//////////////////////////////
	//     Scaffoldgraph.ecore
	//////////////////////////////
	@Test
	public void scaffoldConfigDot() throws Exception{
		String [] args= {"g","parameters-files/ScaffoldGraph/scaffold-config-dot.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void scaffoldConfigXmi() throws Exception{
		String [] args= {"g","parameters-files/ScaffoldGraph/scaffold-config-xmi.params"};
		GrimmLauncher.main(args);
	}
	
	
	////////////////////////////
	//  Ecore.ecore
	////////////////////////////
	@Test
	public void EcoreEmpty() throws Exception{
		String [] args= {"g","parameters-files/Ecore/1ecore-emptyEPackage-xmi.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void EcoreQuick() throws Exception{
		String [] args= {"g","parameters-files/Ecore/2ecore-quick-xmi.params"};
		GrimmLauncher.main(args);
	}
	
	@Test
	public void Ecore4ATL1() throws Exception{
		String [] args= {"g","parameters-files/Ecore/3ecore4ATL-xmi1.params"};
		GrimmLauncher.main(args);
	}
}
