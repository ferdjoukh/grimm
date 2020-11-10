package org.grimm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.grimm.exception.ConfigurationFileNotFoundException;
import org.grimm.exception.MissingInputValueException;
import org.grimm.genetics.ChromosomeReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChromosomeReaderTest {

	@Test
	public void createNonExistingCHR() throws Exception {
		
		Assertions.assertThrows(ConfigurationFileNotFoundException.class, () -> {
			ChromosomeReader chr = new ChromosomeReader("fake");
		});
	}
	
	@Test
	public void incompleteCHRFile() throws Exception{
		
		Assertions.assertThrows(MissingInputValueException.class, () -> {
			ChromosomeReader chr = new ChromosomeReader("CHR/incomplete-Graph-211339-2211181.chr");
		});	
	}
	
	@Test
	public void createExistingCHR() throws Exception {
		Assertions.assertThrows(MissingInputValueException.class, () -> {
			ChromosomeReader chr = new ChromosomeReader("CHR/Graph-211339-2211181.chr");
			assertEquals("CHR/Graph-211339-2211181.chr", chr.getChrFile());
			assertEquals("Graph", chr.getRootClass());
			assertEquals("meta-models/ScaffoldGraph.ecore", chr.getMetamodel());
		});
	}
	
	@Test
	public void validXMLFile() throws Exception{
		Assertions.assertThrows(MissingInputValueException.class, () -> {
			ChromosomeReader chr = new ChromosomeReader("CHR/Graph-211339-2211181.chr");
			chr.validateCHR();
		});
	}
	
	@Test
	public void invalidXMLFile() throws Exception{
		Assertions.assertThrows(MissingInputValueException.class, () -> {
			ChromosomeReader chr = new ChromosomeReader("CHR/Graph-211339-2211181.chr");
			chr.validateCHR();
		});
	}
	
}
