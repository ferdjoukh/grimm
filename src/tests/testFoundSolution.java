package tests;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import org.junit.Test;
import CSP2Model.CSP2XMI;
import CSP2Model.ModelBuilder;

public class testFoundSolution {

	@Test
	public void testWithCompo1() {
		ModelBuilder modelbuilder= new CSP2XMI("","","","");
		BufferedReader bufferedreader= modelbuilder.executeAbsconSolver("tests/CSP/Compo1.xml", 5);
		modelbuilder.findAllSolutions(bufferedreader);
		assertEquals(5, modelbuilder.getFoundSolutions().size());
	}
}
