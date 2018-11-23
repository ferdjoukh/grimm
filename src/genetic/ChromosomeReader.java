package genetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.input.DOMBuilder;

import CSP2Model.CSP2dot;
import Ecore2CSP.CSPconstraint;

import org.jdom2.Document;
import org.jdom2.Element;

import exceptions.ConfigurationFileNotFoundException;
import exceptions.MissingInputValueException;
import Utils.*;

public class ChromosomeReader {

	private String chrFile;
	private String fileName;
	private String valuesLine;
	private Integer [] values;
	private String xcspFile;
	private String grimmFile;
	private String metamodel;
	private String rootClass;
	
	
	public ChromosomeReader(String chrFile) throws Exception {
		setChrFile(chrFile);
		readChromosome();
	}
	
	public void readChromosome() throws Exception {
		
		File file= new File(chrFile);
		InputStream in= new FileInputStream(file);
		InputStreamReader isr= new InputStreamReader(in);
		BufferedReader br= new BufferedReader(isr);
		
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		while( (line=br.readLine())!=null  ) {
			lines.add(line);
		}
		
		if(lines.size() == 5) {
			this.valuesLine= lines.get(0);
			this.values = line2values();
			this.xcspFile= lines.get(1);
			this.grimmFile= lines.get(2);
			this.metamodel= lines.get(3);
			this.rootClass= lines.get(4);
		}else {
			throw new MissingInputValueException("chromosome");
		}
	}
	
	public void validateCHR() {
		
		System.out.println("Loading xcp file");
		
		Document document = loadXMLDocument();
		Element instance = document.getRootElement();
		
		System.out.println("\t[OK] XML file loaded");
		
		System.out.println("Loading XCSP file");
		
		//Add equal predicate
		Element equalPredicate = CSPconstraint.equalityPredicate();
		instance.getChild("predicates").addContent(equalPredicate);
		
		//Create all Equal constraints
		List<Element> allVariables = instance.getChild("variables").getChildren();
		int i=0;
		for(Element variable : allVariables) {
			String name = variable.getAttributeValue("name");
			Element equalConstraint;
			if(name.equals("First")) {
				equalConstraint = CSPconstraint.equalityVarValConstraint(name, -1);
			}else {
				equalConstraint = CSPconstraint.equalityVarValConstraint(name, values[i]);
			}
			instance.getChild("predicates").addContent(equalConstraint);
			i++;
		}
		
		//Save the new XML file
		Utils.saveXML(document, this.fileName+"-extended.xml");
		
		System.out.println("\t [OK] validation constraints added");
		
		//Check the validity
		CSP2dot csp2dot = new CSP2dot(metamodel, rootClass, this.fileName+"-extended.xml", "", fileName+"-VALIDATED-");
		csp2dot.initMetaModelReader(grimmFile);
		
		System.out.println("CSP Solver is running");
		BufferedReader bufferedreader;
		bufferedreader=csp2dot.executeAbsconSolver(this.fileName+"-extended.xml", 1);
		csp2dot.findAllSolutions(bufferedreader);
		if(csp2dot.getFoundSolutions().size() > 0) {
			csp2dot.Solutions2Models(false);
		}
	}
	
	/**
	 * This method returns the document element of an XML files (xcsp instance)
	 * @return
	 */
	public Document loadXMLDocument() {
		
		Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
	        org.w3c.dom.Document w3cDocument = documentBuilder.parse(this.xcspFile);
	        document = new DOMBuilder().build(w3cDocument);
	        return document;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}				
	}
	
	private Integer [] line2values() {
		
		String [] values = valuesLine.split(" "); 
		Integer [] vals = new Integer[values.length]; 
		int i=0;
		for (String s: values) {
			vals[i] = Integer.parseInt(s);
			i++;
		}
		return vals;
	}

	private void setChrFile(String chrFile) throws ConfigurationFileNotFoundException {
		try {
			if (chrFileExists(chrFile)) {
				this.chrFile = chrFile;
				this.fileName = chrFile.split("\\.")[0];				
			}
		}catch(ConfigurationFileNotFoundException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	private boolean chrFileExists(String chrFile) throws ConfigurationFileNotFoundException{
		File config= new File(chrFile);
		
		if(config.exists()) {
			return true;
		}else {
			throw new ConfigurationFileNotFoundException(chrFile);
		}
	}

	public String getChrFile() {
		return chrFile;
	}

	public String getValuesLine() {
		return valuesLine;
	}

	public Integer [] getValues() {
		return values;
	}

	public String getXcspFile() {
		return xcspFile;
	}

	public String getGrimmpFile() {
		return grimmFile;
	}

	public String getMetamodel() {
		return metamodel;
	}

	public String getRootClass() {
		return rootClass;
	}
}
