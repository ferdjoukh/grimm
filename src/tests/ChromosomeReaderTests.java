package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import genetic.*;
import exceptions.*;

public class ChromosomeReaderTests {

	@Test(expected = ConfigurationFileNotFoundException.class)
	public void createNonExistingCHR() throws Exception {
		ChromosomeReader chr = new ChromosomeReader("fake");
	}
	
	@Test
	public void createExistingCHR() throws Exception {
		ChromosomeReader chr = new ChromosomeReader("CHR/Graph-211339-2211181.chr");
		assertEquals("CHR/Graph-211339-2211181.chr", chr.getChrFile());
		assertEquals("Graph", chr.getRootClass());
		assertEquals("meta-models/ScaffoldGraph.ecore", chr.getMetamodel());
	}

}
