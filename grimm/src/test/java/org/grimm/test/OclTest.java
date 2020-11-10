//package org.grimm.test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.io.FileNotFoundException;
//import java.util.LinkedList;
//
//import org.eclipse.ocl.ParserException;
//import org.grimm.Ecore.MetaModelReader;
//import org.grimm.Ecore2CSP.XCSPgenerator;
//import org.grimm.utils.ocl.OclConstraints;
//import org.grimm.utils.ocl.OclDomain;
//import org.junit.jupiter.api.Test;
//
//public class OclTest{
//
//	String ecoreFile = "model/Simple.ecore";
//	String racine = "Root";
//	
//	MetaModelReader modelReader;
//	XCSPgenerator generation;
//	
//
//	protected void setUp() throws Exception{
//		
//		OclConstraints.XCSPFile = "model/" + racine + ".xml";
//
//		modelReader = new MetaModelReader(ecoreFile, racine, 2, 2);
//	    generation = new XCSPgenerator(modelReader, 4, 1);
//		generation.generateXCSP(OclConstraints.XCSPFile,1);
//	}
//	
////	@Override
////	protected void tearDown() throws Exception {
////		generation.setXCSPinstance(null);
////		generation = null;
////		modelReader = null;
////	}
//	
//	@Test
//	public void testOclConstraintInvEQ() {
//		// age = 0
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_eq.ocl", "Student", "age", "0");
//	}
//
//	@Test
//	public void testOclConstraintInvNE() {
//		// age <>= 0
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_ne.ocl", "Student", "age", "-12..-1 1..12");
//	}
//
//	@Test
//	public void testOclConstraintInvGE() {
//		// age >= 0
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_ge.ocl", "Student", "age", "0..12");
//	}
//
//	@Test
//	public void testOclConstraintInvGT() {
//		// age > 5
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_gt.ocl", "Student", "age", "6..12");
//	}
//
//	@Test
//	public void testOclConstraintInvLE() {
//		// age <= 5
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_le.ocl", "Student", "age", "-12..5");
//	}
//	
//	@Test
//	public void testOclConstraintInvLT() {
//		// age < 2
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_lt.ocl", "Student", "age", "-12..1");
//	}
//	
//	@Test
//	public void testOclConstraintInvNOT() {
//		// not(age = 0)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_not.ocl", "Student", "age", "-12..-1 1..12");
//	}
//
//	@Test
//	public void testOclConstraintInvAND() {
//		// (age < -6) and (age > 6)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_and.ocl", "Student", "age", "-5..5");
//	}
//
//	@Test
//	public void testOclConstraintInvOR() {
//		// (age < 0) or (age > 0)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_or.ocl", "Student", "age", "-12..-1 1..12");
//	}
//
//	@Test
//	public void testOclConstraintInvXOR() {
//		// (age < 5) xor (age > 2)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_xor.ocl", "Student", "age", "-12..2 5..12");
//	}
//
//	@Test
//	public void testOclConstraintInvNEG() {
//		// age = -5
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_neg.ocl", "Student", "age", "-5");
//	}
//
//	@Test
//	public void testOclConstraintInvADD() {
//		// age < (5 + 5)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_add.ocl", "Student", "age", "-12..9");
//	}
//
//	@Test
//	public void testOclConstraintInvSUB() {
//		// age < (5 - 5)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_sub.ocl", "Student", "age", "-12..-1");
//	}
//
//	@Test
//	public void testOclConstraintInvMUL() {
//		// age < (2 * 3)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_mul.ocl", "Student", "age", "-12..5");
//	}
//
//	@Test
//	public void testOclConstraintInvDIV() {
//		// age < (5 / 2)
//		doTheTest("model/Simple.ecore", "ocl/Test/inv_div.ocl", "Student", "age", "-12..1");
//	}
//
//	private void doTheTest(String ecoreFilePath, String oclFilePath, String className, String attributeName, String theoreticalDomain) {
//		try {
//			
//			setUp();
//			OclConstraints oclCons = new OclConstraints(modelReader, oclFilePath, generation.getXCSPinstance());
//			oclCons.getConstraintsXCSP();
//			OclDomain oclDomain = new OclDomain(modelReader, generation.getXCSPinstance(), className, attributeName);
//
//			assertEquals(theoreticalDomain, oclDomain.getDomainsAsStringList());
//		} catch (FileNotFoundException | ParserException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
//	
//	@Test
//	public void testOclConstraintGetDomainAsList(){
//		
//		
//		
//		LinkedList<Integer> resExpected = new LinkedList<>();
//		for(int i = -12; i < 6; i++)
//			resExpected.add(i);
//		
//		try {
//			setUp();
//			
//			OclConstraints oclCons = new OclConstraints(null, "ocl/Test/inv_le.ocl", generation.getXCSPinstance());
//				
//			oclCons.getConstraintsXCSP();
//			OclDomain oclDomain = new OclDomain(null, generation.getXCSPinstance(), "Student", "age");
//
//			assertEquals(resExpected, oclDomain.getDomainsAsIntegerList());
//		} catch (FileNotFoundException | ParserException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testOclConstraintGetDomainAsString(){
//		
//		
//		String resExpected = "-12..5";
//		
//		try {
//			setUp();
//			
//			OclConstraints oclCons = new OclConstraints(null, "ocl/Test/inv_le.ocl", generation.getXCSPinstance());
//			
//			oclCons.getConstraintsXCSP();
//			OclDomain oclDomain = new OclDomain(null, generation.getXCSPinstance(), "Student", "age");
//
//			assertEquals(resExpected, oclDomain.getDomainsAsStringList());
//		} catch (FileNotFoundException | ParserException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//}
