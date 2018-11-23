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
	
	@Test(expected = MissingInputValueException.class)
	public void incompleteCHRFile() throws Exception{
		ChromosomeReader chr = new ChromosomeReader("CHR/incomplete-Graph-211339-2211181.chr");
	}
	
	@Test
	public void createExistingCHR() throws Exception {
		ChromosomeReader chr = new ChromosomeReader("CHR/Graph-211339-2211181.chr");
		assertEquals("CHR/Graph-211339-2211181.chr", chr.getChrFile());
		assertEquals("Graph", chr.getRootClass());
		assertEquals("meta-models/ScaffoldGraph.ecore", chr.getMetamodel());
	}
	
	@Test
	public void validXMLFile() throws Exception{
		ChromosomeReader chr = new ChromosomeReader("CHR/Graph-211339-2211181.chr");
		chr.validateCHR();
	}
	
	@Test
	public void invalidXMLFile() throws Exception{
		ChromosomeReader chr = new ChromosomeReader("CHR/invalid-Graph-211339-2211181.chr");
		chr.validateCHR();
	}
	
}
