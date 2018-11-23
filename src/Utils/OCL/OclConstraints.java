package Utils.OCL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.cst.CSTNode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import Ecore.MetaModelReader;
import Ecore2CSP.XCSPgenerator;
import Utils.GrimmLogger;
import Utils.Utils;

public class OclConstraints {

	// Lien du fichier appartenant � une partie de Adel, � enlever lors de la fusion
	public static String XCSPFile = null;
	
	private Document _documentXCSP;
	private MetaModelReader _modelReader;
	private String _oclFilePath;
	private CSTNode _constraintsTree;
	private OclAnalyzer _analyzer;
	
	/**
	 * Cr�� un objet qui va lire le fichier OCL, le fichier Ecore et le fichier XCSP pour appliquer les contraintes.
	 * @param modeleReader Modele repr�sentant le fichier ecore
	 * @param oclFilePath Chemin vers le fichier OCL
	 * @param xcspDocument Chemin vers le fichier XCSP
	 */
	public OclConstraints(MetaModelReader modeleReader, String oclFilePath, Document xcspDocument) {
		this._documentXCSP = xcspDocument;
		_modelReader = modeleReader;
		_oclFilePath = oclFilePath;
		_constraintsTree = null;
	}

	/**
	 * Fonction qui r�cup�re les contraintes cr��es � partir du fichier OCL sous format XCSP.
	 * @return Les contraintes cr��es � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getConstraintsXCSP() throws FileNotFoundException, ParserException {
		if (_analyzer == null)
			constraintsToXCSP();

		return _analyzer.getConstraintsXCSP();
	}

	/**
	 * Fonction qui r�cup�re les pr�dicats cr��es � partir du fichier OCL sous format XCSP.
	 * @return Les pr�dicats cr��es � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getPredicatesXCSP() throws FileNotFoundException, ParserException {
		if (_analyzer == null)
			constraintsToXCSP();

		return _analyzer.getPredicatesXCSP();
	}
	
	/**
	 * Fonction qui r�cup�re les domaines cr��s � partir du fichier OCL sous format XCSP.
	 * @return Les domaines cr��s � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getDomainsXCSP() throws FileNotFoundException, ParserException {
		if (_analyzer == null)
			constraintsToXCSP();

		return _analyzer.getDomainsXCSP();
	}
	
	/**
	 * Fonction qui r�cup�re les variables cr��es � partir du fichier OCL sous format XCSP.
	 * @return Les variables cr��es � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getVariablesXCSP() throws FileNotFoundException, ParserException {
		if (_analyzer == null)
			constraintsToXCSP();

		return _analyzer.getVariablesXCSP();
	}


	/**
	 * Fonction qui r�cup�re l'arbre syntaxique du fichier OCL
	 * @return L'arbre syntaxique du fihier OCL
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public CSTNode getConstraintsTree() throws FileNotFoundException, ParserException {
		if (_constraintsTree == null)
			_constraintsTree = OclTools.loadConstraintsTree(_oclFilePath);

		return _constraintsTree;
	}

	/**
	 * Transforme l'arbre syntaxique obtenu depuis le fichier OCL en contraintes XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 * @see OclAnalyzer
	 */
	private void constraintsToXCSP() throws FileNotFoundException, ParserException {
		if (_constraintsTree == null)
			_constraintsTree = OclTools.loadConstraintsTree(_oclFilePath);

		_analyzer = new OclAnalyzer(this._modelReader, this._documentXCSP, _constraintsTree);
		_analyzer.getConstraintsXCSP();
	}
	
	public Document getResultDocumentXCSP() throws FileNotFoundException, ParserException {
		Element rootElement = _documentXCSP.getRootElement();
		
		rootElement.getChild("domains").addContent(getDomainsXCSP());
		rootElement.getChild("variables").addContent(getVariablesXCSP());
		rootElement.getChild("constraints").addContent(getConstraintsXCSP());
		rootElement.getChild("predicates").addContent(getPredicatesXCSP());
		
		//Met a jour le nombre d'occurance des domaines, variables, constraintes...
		rootElement.getChild("domains").setAttribute("nbDomains", 
				String.valueOf(Integer.valueOf(rootElement.getChild("domains").getAttributeValue("nbDomains")) + getDomainsXCSP().size()));
		rootElement.getChild("variables").setAttribute("nbVariables", 
				String.valueOf(Integer.valueOf(rootElement.getChild("variables").getAttributeValue("nbVariables")) + getVariablesXCSP().size()));
		rootElement.getChild("constraints").setAttribute("nbConstraints", 
				String.valueOf(Integer.valueOf(rootElement.getChild("constraints").getAttributeValue("nbConstraints")) + getConstraintsXCSP().size()));
		rootElement.getChild("predicates").setAttribute("nbPredicates", 
				String.valueOf(Integer.valueOf(rootElement.getChild("predicates").getAttributeValue("nbPredicates")) + getPredicatesXCSP().size()));
		
		return _documentXCSP;
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException, ParserException {
		GrimmLogger.getInstance().setLevel(Level.ALL);
		
//		String ecoreFile = "PetriNet/PetriNet.ecore";
//		String oclFile = "PetriNet/PetriNet.ocl";
//		String racine = "PetriNet";
//		XCSPFile = "PetriNet/PetriNet.xml";
				
		String ecoreFile = "Graph/GraphColoring.ecore";
		String oclFile = "Graph/GraphColoring.ocl";
		String racine = "Graph";
		XCSPFile = "Graph/" + racine + ".xml";

//		String ecoreFile = "FeatureModel/FeatureModel.ecore";
//		String oclFile = "FeatureModel/FeatureModel.ocl";
//		String racine = "RootFeature";
//		XCSPFile = "FeatureModel/FeatureModel.xml";

//		String ecoreFile = "ER/ER.ecore";
//		String oclFile = "ER/ER.ocl";
//		String racine = "Schema";
//		XCSPFile = "ER/ER.xml";

//		String ecoreFile = "Navigation/Navigation.ecore";
//		String oclFile = "Navigation/Navigation.ocl";
//		String racine = "Navigation";
//		XCSPFile = "Navigation/" + racine + ".xml";

		MetaModelReader modelReader = new MetaModelReader(ecoreFile, racine, 2, 2);
		
	    XCSPgenerator generation = new XCSPgenerator(modelReader, 4, 1);
		generation.generateXCSP(OclConstraints.XCSPFile);

		XMLOutputter outputXML = new XMLOutputter(Format.getPrettyFormat());
		try {
			OclConstraints oclCons = new OclConstraints(modelReader, oclFile, generation.getXCSPinstance());
			
			System.out.println("***** TREE *****\n");
			OclTools.scanNode(oclCons.getConstraintsTree());
			System.out.println("\n***** DOMAINS *****\n");
			for (Element elt : oclCons.getDomainsXCSP())
				System.out.println(outputXML.outputString(elt));
			System.out.println("\n***** VARIABLES *****\n");
			for (Element elt : oclCons.getVariablesXCSP())
				System.out.println(outputXML.outputString(elt));
			System.out.println("\n***** PREDICATES *****\n");
			for (Element elt : oclCons.getPredicatesXCSP())
				System.out.println(outputXML.outputString(elt));
			System.out.println("\n***** CONSTRAINTS *****\n");
			for (Element elt : oclCons.getConstraintsXCSP())
				System.out.println(outputXML.outputString(elt));
			
			Utils.saveXML(oclCons.getResultDocumentXCSP(), XCSPFile);
		} catch (FileNotFoundException | ParserException e) {
			e.printStackTrace();
		}

	}

}
