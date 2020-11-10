package org.grimm.test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;

import org.grimm.CSP2Model.CSP2XMI;
import org.grimm.CSP2Model.ModelBuilder;
import org.junit.jupiter.api.Test;

public class TestFoundSolution {

	@Test
	public void testWithCompo1() {
		ModelBuilder modelbuilder= new CSP2XMI("","","","");
		BufferedReader bufferedreader= modelbuilder.executeAbsconSolver("tests/CSP/Compo1.xml", 5);
		modelbuilder.findAllSolutions(bufferedreader);
		assertEquals(5, modelbuilder.getFoundSolutions().size());
	}
}
