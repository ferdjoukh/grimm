package Utils.OCL;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.DotOrArrowEnum;
import org.eclipse.ocl.cst.FeatureCallExpCS;
import org.eclipse.ocl.cst.IntegerLiteralExpCS;
import org.eclipse.ocl.cst.OperationCallExpCS;
import org.eclipse.ocl.cst.PathNameCS;
import org.eclipse.ocl.cst.SimpleNameCS;
import org.eclipse.ocl.cst.SimpleTypeEnum;
import org.eclipse.ocl.cst.VariableExpCS;
import org.eclipse.ocl.cst.impl.FeatureCallExpCSImpl;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.ecore.parser.OCLAnalyzer;
import org.jdom2.Document;
import org.jdom2.Element;

import Ecore.MetaModelReader;
import Utils.OCL.exceptions.VariableNotFoundException;

public class OclTools {

	/**
	 * R�cup�re l'arbre syntaxique correspondant au fichier OCL fournit.
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 * @return CSTNode L'arbre syntaxique correspondant au fichier OCL fournit
	 * @see OCLAnalyzer
	 */
	public static CSTNode loadConstraintsTree(String oclFilePath) throws FileNotFoundException, ParserException {
		InputStream in = new FileInputStream(oclFilePath);
		OCLInput document = new OCLInput(in);

		Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env = EcoreEnvironmentFactory.INSTANCE.createEnvironment();
		OCLAnalyzer a = new OCLAnalyzer(env, document.getContentAsString());
		return a.parseConcreteSyntax();
	}

	/**
	 * Analyse un noeud de type {@link PathNameCS}
	 * @param node Noeud de type {@link PathNameCS} � analyser
	 * @return <code>String</code> contenant le chemin sp�cifi� dans le noeud
	 */
	public static String getPathNameFromNode(PathNameCS node) {
		String toReturn = "";
		for (EObject subNode : node.eContents()) {
			if (subNode instanceof SimpleNameCS) {
				toReturn += ((SimpleNameCS) subNode).getValue() + ".";
			}
		}
		return toReturn.substring(0, toReturn.length() - 1);
	}

	/**
	 * R�cup�re la liste de toutes les variables sp�cifi�es dans un noeud et ses fils.
	 * @param node Noeud � parcourir � la recherche de variables
	 * @return Liste des variables pr�sentes dans le noeud et ses fils
	 */
	public static List<String> getVariablesInNodeAndHisSubNodesOfType(CSTNode node, SimpleTypeEnum ... types) {
		List<String> toReturn = new ArrayList<String>();

		for (EObject subNode : node.eContents()) {
			if (subNode instanceof VariableExpCS) {
				SimpleNameCS variableSimpleNameNode = (SimpleNameCS) subNode.eContents().get(0);
				if (Arrays.binarySearch(types, variableSimpleNameNode.getType()) >= 0) {
					String variableName = variableSimpleNameNode.getValue();
					//if (! toReturn.contains(variableName))
					toReturn.add(variableName);
				}
			} else if (subNode instanceof CSTNode) {
				for (String varName : getVariablesInNodeAndHisSubNodesOfType((CSTNode) subNode, types)) {
					//if (! toReturn.contains(varName))
					toReturn.add(varName);
				}
			}
		}

		return toReturn;
	}

	/**
	 * R�cup�re un tableau contenant les arguments d'une op�ration.
	 * @param operationNode Noeud contenant l'op�ration et ses arguments
	 * @param operation Type {@link OclOperation} de l'op�ration 
	 * @return Un tableau contenant les arguments de l'op�ration
	 */
	public static CSTNode[] getArgumentsOfOperation(OperationCallExpCS operationNode, OclOperation operation) {
		CSTNode[] toReturn = new CSTNode[operation.getArity()];

		int tabCounter = 0;
		for (EObject subNode : operationNode.eContents()) {
			if (subNode instanceof CSTNode) {
				if (subNode instanceof SimpleNameCS) {
					if (! ((SimpleNameCS) subNode).getValue().equals(operation.getOclSymbol())) {
						toReturn[tabCounter] = (CSTNode) subNode;
						tabCounter++;
					}
				} else {
					toReturn[tabCounter] = (CSTNode) subNode;
					tabCounter++;
				}
			}
		}

		return toReturn;
	}

	public static String[] getVariablesNamesInOperation(OperationCallExpCS node) {
		return getVariablesNamesInOperation(OclTools.getOperationNodeAsOperationString(node, false));
	}

	public static String[] getVariablesNamesInOperation(String operationAsString) {
		String[] toReturn = new String[2];

		if (operationAsString.endsWith(")")) {
			Scanner scanner = new Scanner(operationAsString);
			scanner.useDelimiter("\\(");
			scanner.next();
			scanner.useDelimiter(",");
			toReturn[0] = scanner.next();
			toReturn[0] = toReturn[0].substring(1).trim();
			scanner.useDelimiter("\\)");
			toReturn[1] = scanner.next();
			toReturn[1] = toReturn[1].substring(1).trim();
			scanner.close();
		} else {
			Scanner scanner = new Scanner(operationAsString);
			toReturn[0] = scanner.next();
			scanner.next();
			toReturn[1] = scanner.next();
			scanner.close();
		}

		return toReturn;
	}

	/**
	 * R�cup�re le type {@link OclOperation} de l'op�ration pr�sente dans un noeud de type {@link CSTNode}.
	 * @param operationNode Noeud contenant l'op�ration
	 * @return Le type {@link OclOperation} de l'op�ration ou {@code null} si elle n'a pas �t� trouv�e
	 */
	public static OclOperation getNodeOperation(OperationCallExpCS operationNode) {
		for (EObject subNode : operationNode.eContents()) {
			if (subNode instanceof SimpleNameCS) {
				SimpleTypeEnum keyword = ((SimpleNameCS) subNode).getType();
				if ((keyword == SimpleTypeEnum.KEYWORD_LITERAL && operationNode.getAccessor() == DotOrArrowEnum.NONE_LITERAL) ||
						(keyword == SimpleTypeEnum.IDENTIFIER_LITERAL && operationNode.getAccessor() == DotOrArrowEnum.DOT_LITERAL)) {
					return OclOperation.getOperationFromOCLSymbol(((SimpleNameCS) subNode).getValue(),
							operationNode.eContents().size() - 1);
				}
			}
		}
		//GrimmLogger.getInstance().severe("No operation found in : " + OclTools.toStringNode(operationNode));
		return null;
	}

	/**
	 * Retourne les classes filles d'une classe.
	 * @param modelReader ModelReader contenant le mod�le Ecore o� chercher les classes
	 * @param className nom de la classe m�re
	 * @return les classes filles ou {@code null} si la classe m�re n'a pas �t� trouv�e ou qu'elle n'a pas de classe fille
	 */
	public static ArrayList<EClass> getSubClasses(MetaModelReader modelReader, String className) {
		ArrayList<EClass> allClass = (ArrayList<EClass>) modelReader.getClasses();
		ArrayList<EClass> allAbtractClass = (ArrayList<EClass>) modelReader.getAbtractClasses();

		ArrayList<EClass> toReturn = new ArrayList<EClass>();
		for (EClass currentAbstractClass : allAbtractClass) {
			if(currentAbstractClass.getName().equals(className)) {
				for (EClass eClass: allClass) {
					if(currentAbstractClass.isSuperTypeOf(eClass)){
						toReturn.add(eClass);
					}
				}
				return toReturn;
			}
		}

		return null;
	}

	/**
	 * Retourne le nom des classes filles d'une classe.
	 * @param modelReader ModelReader contenant le mod�le Ecore o� chercher les classes
	 * @param className nom de la classe m�re
	 * @return le nom des classes filles ou {@code null} si la classe m�re n'a pas �t� trouv�e ou qu'elle n'a pas de classe fille
	 */
	public static List<String> getSubClassesNames(MetaModelReader modelReader, String className){
		ArrayList<String> classNameResult = new ArrayList<>();
		ArrayList<EClass> subClasses = getSubClasses(modelReader, className);

		if(subClasses == null)
			return null;

		for (EClass eClass: subClasses) {
			classNameResult.add(eClass.getName());
		}
		return classNameResult;
	}


	/**
	 * R�cup�re le type d'un attribut.
	 * @param modelReader ModelReader contenant le mod�le Ecore o� chercher les classes
	 * @param className Nom de la classe contenant l'attribut
	 * @param attributName Nom de l'attribut
	 * @return Le type de l'attribut souhait� ou {@code null} s'il n'a pas �t� trouv�
	 */
	public static String getTypeOfAttribute(MetaModelReader modelReader, String className, String attributeName) {
		EClass classToTreat = getClassFromName(modelReader, className);
		if(classToTreat == null)
			classToTreat = getAbstractClassFromName(modelReader, className);

		if(classToTreat == null)
			//GrimmLogger.getInstance().severe("Class " + className + " not found.");

		for (EReference curreAttribute : modelReader.getAllReferencesFromClass(classToTreat)) {
			if(curreAttribute.getName().equals(attributeName)){
				return curreAttribute.getEType().getName();
			}
		}
		//GrimmLogger.getInstance().severe("Attribute " + className + ":" + attributeName + " not found.");
		return null;
	}

	private static EClass getClassFromName(MetaModelReader modelReader, String className) {
		for (EClass currentClass : modelReader.getClasses()) {
			if(currentClass.getName().equals(className))
				return currentClass;
		}
		return null;
	}

	private static EClass getAbstractClassFromName(MetaModelReader modelReader, String className) {
		for (EClass currentClass : modelReader.getAbtractClasses()) {
			if(currentClass.getName().equals(className))
				return currentClass;
		}
		return null;
	}

	// R�cup�re la liste des identifiants des instances pr�sentes dans un attribut de type collection
	public static int getInstanceId(String instanceVariableName) {
		Integer toReturn = null;
		String toParse = instanceVariableName.replace("_", " ");

		Scanner scan = new Scanner(toParse);
		boolean idFound = false;
		while(!idFound && scan.hasNext()) {
			if (scan.hasNextInt()) {
				toReturn = scan.nextInt();
				idFound = true;
			} else {
				scan.next();
			}
		}
		scan.close();

		return toReturn;
	}

	public static ArrayList<Element> getDomainsOfClass(MetaModelReader modelReader, Document xcspDocument, String className) {
		ArrayList<Element> toReturn = new ArrayList<>();

		List<String> subClassesNames = getSubClassesNames(modelReader, className);
		if (subClassesNames != null) {	
			for (String subClassName : subClassesNames) {
				toReturn.add(OclTools.getDomainElementWithName(xcspDocument, "DC_" + subClassName));
			}
		} else {
			toReturn.add(OclTools.getDomainElementWithName(xcspDocument, "DC_" + className));
		}

		return toReturn;
	}

	public static Element getDomainElementWithName(Document xcspDocument, String domainName) {
		Element root = xcspDocument.getRootElement();

		List<Element> domaineName = root.getChildren("domains").get(0).getChildren("domain");
		for (Element element : domaineName) {
			if(element.getAttributeValue("name").equals(domainName)) {
				return element;
			}
		}

		//GrimmLogger.getInstance().warning("Domain " + domainName + " not found.");
		return null;
	}

	public static ArrayList<Integer> domainStringToArray(String stringDomain) {
		ArrayList<Integer> domainResult = new ArrayList<>();

		Scanner domainScanner = new Scanner(stringDomain);
		while(domainScanner.hasNext()) {
			String parameter = domainScanner.next();

			// On va traiter les diff�rents cas possibles
			if(parameter.contains("..")) { // Si c'est un intervalle
				// On va r�cup�rer les valeurs des bornes
				parameter = parameter.replace("..", " ");

				Scanner intervalScanner = new Scanner(parameter);
				int lowerBound = intervalScanner.nextInt();
				int upperBound = intervalScanner.nextInt();

				// Puis on ajoute toutes les valeurs comprises � l'int�rieur de cette borne
				for (int i = lowerBound; i <= upperBound; i++) {
					domainResult.add(i);
				}
				intervalScanner.close();
			} else { // Sinon, on trouve une valeur seule
				domainResult.add(Integer.valueOf(parameter));
			}
		}

		domainScanner.close();

		return domainResult;
	}

	/**
	 * R�cup�re une cha�ne de caract�res repr�sentant le domaine r�cup�r� au format XCSP :
	 * 		{@code 5..7 | 5 }
	 * @return Le domaine sous forme de cha�ne de caract�res
	 */
	public static ArrayList<String> getDomainAsStringFromElementList(ArrayList<Element> domains) {
		return getDomainsAsString(getElementDomainToArrayOfInteger(domains));
	}

	/**
	 * R�cup�re une cha�ne de caract�res repr�sentant le domaine r�cup�r� au format XCSP :
	 * 		{@code 5..7 | 5 }
	 * @return Le domaine sous forme de cha�ne de caract�res
	 */
	public static ArrayList<String> getDomainsAsString(ArrayList<ArrayList<Integer>> domains) {
		ArrayList<String> result = new ArrayList<>();

		for (int indexDomain = 0 ; indexDomain < domains.size(); indexDomain++) {
			ArrayList<Integer> currentDomain = domains.get(indexDomain);
			result.add(OclTools.getDomainAsString(currentDomain));
		}

		return result;
	}

	public static String getDomainAsString(ArrayList<Integer> domainArray) {
		String currentResult = "";

		for(int i = 0; i < domainArray.size(); i++) {
			currentResult += domainArray.get(i);
			if(domainArray.size() > i+1 && domainArray.get(i + 1) == domainArray.get(i) + 1){
				currentResult += "..";
				while(domainArray.size() > i+1 && domainArray.get(i + 1) == domainArray.get(i) + 1)
					i++;
				currentResult += domainArray.get(i);
			}
			currentResult += " ";
		}

		return currentResult.trim();
	}

	/**
	 * Transforme une liste d'Element en liste d'Array d'Integer (ATTENTION les elements pass� en parametre doivent etre des elements de domain)
	 * @param elementsDomains
	 * @return
	 */
	public static ArrayList<ArrayList<Integer>> getElementDomainToArrayOfInteger(ArrayList<Element> elementsDomains){
		ArrayList<ArrayList<Integer>> domainsResult = new ArrayList<>();

		for (int indexDomain = 0; indexDomain < elementsDomains.size(); indexDomain++) {

			String currentStringDomain = elementsDomains.get(indexDomain).getValue();

			domainsResult.add(
					domainStringToArray(currentStringDomain));
		}

		return domainsResult;
	}

	/**
	 * Renvoi les elements correspondant au domaine recherch� sous forme de liste d'entier.
	 */
	public static ArrayList<ArrayList<Integer>> getDomainsArrayOf(MetaModelReader modelReader, Document xcspDocument, String className, String attributeName){
		return getElementDomainToArrayOfInteger(getDomainsOf(modelReader, xcspDocument, className, attributeName));
	}

	/**
	 * R�cup�re les �l�ments XML correspondant au domaine recherch�.
	 * @param modelReader ModelReader contenant le mod�le
	 * @param xcspDocument Document XCSP o� chercher le domaine
	 * @param className Nom de la classe
	 * @param attributeName Nom de l'attribut
	 * @return Les �l�ments XML correspondant au domaine recherch�
	 */
	public static ArrayList<Element> getDomainsOf(MetaModelReader modelReader, Document xcspDocument, String className, String attributeName) {
		ArrayList<Element> domainsResult = new ArrayList<>();

		List<String> allClassName = OclTools.getSubClassesNames(modelReader, className);
		if (allClassName == null) {
			allClassName = new ArrayList<String>();
			allClassName.add(className);
		}

		Element root = xcspDocument.getRootElement();

		List<Element> domains = root.getChildren("domains").get(0).getChildren("domain");
		List<Element> varaibles = root.getChildren("variables").get(0).getChildren("variable");

		// On parcours les variables � la recherche de celle souhait�e
		for (Element currentVariable : varaibles) {
			String variableName = currentVariable.getAttributeValue("name");
			String variableDomain = currentVariable.getAttributeValue("domain");

			// Si cette variable est une recherch�e
			for (String currentClassName : allClassName) {

				if(variableName.contains(attributeName) && variableName.contains(currentClassName)) {

					// Je parcours les domaines pour trouver celui de la variable
					for (Element currentDomain : domains) {
						if(currentDomain.getAttributeValue("name").equals(variableDomain) && !domainsResult.contains(currentDomain)) {
							domainsResult.add(currentDomain);
						}
					}
				}

			}
		}

		//if(domainsResult.isEmpty())
			//GrimmLogger.getInstance().severe("No domain found for attribute " + className + ":" + attributeName);

		return domainsResult;
	}

	/**
	 * Parcours et affiche le contenu d'un noeud.
	 * @param node Noeud � parcourir
	 */
	public static void scanNode(CSTNode node) {
		scanNode(node, 0);
	}

	/**
	 * Parcours et affiche le contenu d'un noeud.
	 * @param node Noeud � parcourir
	 * @param level Niveau, � titre indicatif, du noeud dans l'arbre (sert pour la r�cursion)
	 */
	private static void scanNode(CSTNode node, int level) {
		String indentation = "";
		for (int i = 0; i < level; i++)
			indentation += " ";
		System.out.println(indentation + level + " : " + node);
		for (EObject subNode : node.eContents()) {
			if (subNode instanceof CSTNode) {
				scanNode((CSTNode) subNode, level + 1);
			}
		}
	}

	/**
	 * Retourne une description courte d'un noeud sous la forme :
	 * 		<i>Nom_Classe_Noeud</i> [<i>Premier_Mot</i> .. <i>Dernier_Mot</i>] |
	 * 		<i>Nom_Classe_Noeud</i> [<i>Mot_Unique</i>]
	 * @param node Noeud � d�crire
	 * @return Description courte du noeud
	 */
	public static String toStringNode(CSTNode node) {
		String toReturn;
		

		toReturn = node.getClass().getSimpleName() + " [";

		if (node.getStartToken() == node.getEndToken())
			toReturn += node.getStartToken();
		else
			toReturn += node.getStartToken() + " .. " + node.getEndToken();
		toReturn += "]";

		return toReturn;
	}

	/**
	 * R�cup�re le type d'une instance d'identifiant donn� depuis le fichier XCSP.
	 * @param xcspDocument Document XCSP
	 * @param idInstance Identifiant de l'instance
	 * @return Le type de l'instance d'identifiant donn� ou null sinon
	 */
	public static String getTypeOfInstanceID(Document xcspDocument, Integer idInstance) {
		Element root = xcspDocument.getRootElement();
		List<Element> variables = root.getChildren("variables").get(0).getChildren("variable");

		for (Element currentVariable : variables) {
			if(currentVariable.getAttributeValue("name").contains(String.valueOf(idInstance))){

				String currentVariableName = currentVariable.getAttributeValue("name").replaceAll("_", " ");

				Scanner scanner = new Scanner(currentVariableName);
				if(scanner.hasNext()){
					scanner.next();
					if(scanner.hasNext()){
						String result = scanner.next();
						scanner.close();
						if(result == null || result == "")
							//GrimmLogger.getInstance().severe("Error while parsing " + currentVariableName);
						return result;
					}
				}
				scanner.close();
				//GrimmLogger.getInstance().severe("Impossible to parse " + currentVariableName);
			}
		}

		//GrimmLogger.getInstance().severe("Impossible to find instance with ID " + idInstance);
		return null;
	}

	/**
	 * Retourne la liste des variables correspondant au couple classe/attribut :  {@code className}/{@code attributeName}
	 * @param className Nom de la classe de la variable
	 * @param attributeName Nom de l'attribut repr�sent� par la variable
	 * @return La liste des variables correspondant
	 */
	public static List<String> getVariablesNamesOf(Document documentXCSP, String className, String attributeName) {
		List<String> toReturn = new ArrayList<String>();

		Element root = documentXCSP.getRootElement();
		List<Element> variables = root.getChildren("variables").get(0).getChildren("variable");
		for (Element currentVariable: variables) {
			String variableName = currentVariable.getAttributeValue("name");
			if(variableName.contains(className) && variableName.contains(attributeName)){
				toReturn.add(variableName);
			}
		}

		return toReturn;
	}

	/**
	 * Retourne les variables correspondantes au couple ID d'instance/attribut :  {@code instanceId}/{@code attributeName}
	 * @param instanceId ID des instances des variables
	 * @param attributeName Nom de l'attribut repr�sent� par les variables
	 * @return Les variables correspondantes ou {@code null} si aucune n'a pas �t� trouv�e
	 * @throws VariableNotFoundException Si aucune variable n'a �t� trouv�e
	 */
	public static List<String> getVariable(Document documentXCSP, Integer instanceId, String attributeName) throws VariableNotFoundException {
		List<String> toReturn = new ArrayList<String>();

		Element root = documentXCSP.getRootElement();
		List<Element> variables = root.getChildren("variables").get(0).getChildren("variable");
		for (Element currentVariable: variables) {
			String variableName = currentVariable.getAttributeValue("name");
			if(OclTools.getInstanceId(variableName) == instanceId && variableName.contains(attributeName)){
				toReturn.add(variableName);
			}
		}

		if (toReturn.isEmpty())
			throw new VariableNotFoundException(instanceId, attributeName);
		else
			return toReturn;
	}

	public static boolean isOclTypeComparaisonOperationNode(OperationCallExpCS node) {
		OclOperation operation = OclTools.getNodeOperation(node);
		CSTNode[] arguments = OclTools.getArgumentsOfOperation(node, operation);

		CSTNode firstArg = arguments[0];
		CSTNode lastArg = arguments[arguments.length - 1];
		if ((firstArg instanceof OperationCallExpCS) && (lastArg instanceof OperationCallExpCS)) {
			if (OclTools.getNodeOperation((OperationCallExpCS) firstArg) == OclOperation.oclType &&
					OclTools.getNodeOperation((OperationCallExpCS) lastArg) == OclOperation.oclType) {
				return true;
			}
		}

		return false;
	}

	public static Element createDomainElement(String name, ArrayList<Integer> values) {
		Element toReturn = new Element("domain");

		toReturn.setAttribute("name", name);
		toReturn.setAttribute("nbValues", String.valueOf(values.size()));

		toReturn.setText(OclTools.getDomainAsString(values));

		return toReturn;
	}

	public static Element createVariableElement(String domainName, String variableName) {
		Element toReturn = new Element("variable");

		toReturn.setAttribute("domain", domainName);
		toReturn.setAttribute("name", variableName);

		return toReturn;
	}

	public static String parametersAsStringSeparatedBySpace(String ... parameters) {
		String parametersListAsString = "";
		for(String p : parameters)
			parametersListAsString += p + " ";
		return parametersListAsString.trim();
	}

	public static String parametersAsStringSeparatedBySpace(List<String> parameters) {
		String parametersListAsString = "";
		for(String p : parameters)
			parametersListAsString += p + " ";
		return parametersListAsString.trim();
	}

	public static <T extends CSTNode> int countSubNodesOfType(CSTNode node, Class<T> nodeTypeToCount) {
		int toReturn = 0;
		if (nodeTypeToCount.equals(node.getClass()))
			toReturn++;

		for(EObject subNode : node.eContents()) {
			if (subNode instanceof CSTNode) {
				toReturn += countSubNodesOfType((CSTNode) subNode, nodeTypeToCount);
			}
		}

		return toReturn;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Recherche dans TOUS les sous-noeuds d'un arbre (jusqu'aux feuilles) les noeuds d'un type donn�. 
	 * @param node Noeud � parcourir
	 * @param nodeTypeToGet Type des noeuds � r�cup�rer
	 * @return La liste des noeuds d'un type donn�
	 */
	public static <T extends CSTNode> List<T> getSubNodesOfType(CSTNode node, Class<T> nodeTypeToGet) {
		List<T> toReturn = new ArrayList<T>();
		if (nodeTypeToGet.equals(node.getClass()))
			toReturn.add((T) node);

		for(EObject subNode : node.eContents()) {
			if (subNode instanceof CSTNode) {
				toReturn.addAll(getSubNodesOfType((CSTNode) subNode, nodeTypeToGet));
			}
		}

		return toReturn;
	}

	public static List<String> getPathFromFeatureCallExpCS(FeatureCallExpCS node) {
		List<String> toReturn = new ArrayList<String>();

		for(EObject subNode : node.eContents()) {
			if (subNode instanceof FeatureCallExpCS) {
				toReturn.addAll(getPathFromFeatureCallExpCS((FeatureCallExpCS) subNode));
			} else if (subNode instanceof SimpleNameCS) {
				toReturn.add(((SimpleNameCS) subNode).getValue());
			} else if (subNode instanceof VariableExpCS) {
				for(EObject subNodeVar : subNode.eContents()) {
					if (subNodeVar instanceof SimpleNameCS) {
						toReturn.add(((SimpleNameCS) subNodeVar).getValue());
					}
				}
			}
		}

		return toReturn;
	}

	public static EClass getClassWithName(MetaModelReader modelReader,
			String concernedClassName) {
		List<EClass> concernedClasses = modelReader.getClasses();

		if(concernedClasses.isEmpty())
			concernedClasses = modelReader.getAbtractClasses();

		for (EClass eClass : concernedClasses) {
			if(eClass.getName().equals(concernedClassName))
				return eClass;
		}	
		//System.err.println("class not found for name " + concernedClassName);
		return null;
	}

	public static String getOperationNodeAsOperationString(OperationCallExpCS node, boolean withNavigations) {
		String[] operation = new String[3];
		for(int i = 0; i < operation.length; i++) {
			operation[i] = "";
		}
		
		boolean isInfixOperation = false;
		
		for(int iNodes = 0; iNodes < node.eContents().size(); iNodes++) {
			EObject subNode = node.eContents().get(iNodes);
			if (subNode instanceof FeatureCallExpCS) {
				if (withNavigations) {
					for(String s : getPathFromFeatureCallExpCS((FeatureCallExpCS) subNode)) {
						operation[iNodes] += s + ".";
					}
					operation[iNodes] = operation[iNodes].substring(0, operation[iNodes].length() - 1);
				} else {
					List<String> featurePath = getPathFromFeatureCallExpCS((FeatureCallExpCS) subNode);
					operation[iNodes] += featurePath.get(featurePath.size() - 1);
				}
			} else if (subNode instanceof SimpleNameCS) {
				if (((SimpleNameCS) subNode).getType() == SimpleTypeEnum.KEYWORD_LITERAL) {
					isInfixOperation = true;
					OclOperation operationOcl = OclOperation.getOperationFromOCLSymbol(((SimpleNameCS) subNode).getValue(), 2);
					if (operationOcl == null) {
						operation[iNodes] = ((SimpleNameCS) subNode).getValue();
						isInfixOperation = false;
					} else {
						operation[iNodes] = operationOcl.name();
					}
				} else {
					operation[iNodes] = ((SimpleNameCS) subNode).getValue();
				}
			} else if (subNode instanceof VariableExpCS) {
				for(EObject subNodeVar : subNode.eContents()) {
					if (subNodeVar instanceof SimpleNameCS) {
						operation[iNodes] += ((SimpleNameCS) subNodeVar).getValue();
					}
				}
			} else if (subNode instanceof IntegerLiteralExpCS) {
				operation[iNodes] += ((IntegerLiteralExpCS) subNode).getIntegerSymbol();
			}
		}
		
		if (operation[0].equals(operation[2])) {
			operation[0] += 0;
			operation[2] += 1;
		}
		
		if (isInfixOperation)
			return operation[1] + "(" + operation[0] + ", " + operation[2] + ")";
		else
			return operation[0] + " " + operation[1] + " " + operation[2];
	}

	public static <T> List<List<T>> getCombinationOfLists(List<List<T>> lists) {
		List<List<T>> toReturn = new ArrayList<List<T>>();

		if (lists.size() > 1) {
			List<List<T>> otherCombinations = getCombinationOfLists(lists.subList(1, lists.size()));
			for(T elt : lists.get(0)) {
				for(List<T> combination : otherCombinations) {
					List<T> newCombination = new ArrayList<T>();
					newCombination.add(elt);
					newCombination.addAll(combination);
					toReturn.add(newCombination);
				}
			}
		} else {
			for(T elt : lists.get(0)) {
				toReturn.add(new ArrayList<T>(Arrays.asList(elt)));
			}
		}

		return toReturn;
	}

	public static <T> List<List<T>> getCombinationOfLists(List<List<T>> lists, List<T> listToAdd) {
		List<List<T>> toReturn = new ArrayList<List<T>>();

		for(List<T> list : lists) {
			List<T> newList = new ArrayList<T>();
			newList.addAll(list);
			newList.add(listToAdd.get(0));
			toReturn.add(newList);
		}

		if (listToAdd.size() > 1) {
			List<T> newListToAdd = new ArrayList<T>(listToAdd);
			newListToAdd.remove(0);
			toReturn.addAll(getCombinationOfLists(lists, newListToAdd));
		}

		//		if (lists.size() > 1) {
		//			List<List<T>> otherCombinations = getCombinationOfLists(lists.subList(1, lists.size()));
		//			for(T elt : lists.get(0)) {
		//				for(List<T> combination : otherCombinations) {
		//					List<T> newCombination = new ArrayList<T>();
		//					newCombination.add(elt);
		//					newCombination.addAll(combination);
		//					toReturn.add(newCombination);
		//				}
		//			}
		//		} else {
		//			for(T elt : lists.get(0)) {
		//				toReturn.add(new ArrayList<T>(Arrays.asList(elt)));
		//			}
		//		}

		return toReturn;
	}

	public static String getScopeFromParameters(String parameters) {
		String toReturn = "";

		Scanner scan = new Scanner(parameters);
		while(scan.hasNext()) {
			if(scan.hasNextInt())
				scan.nextInt();
			else
				toReturn += scan.next() + " ";
		}
		scan.close();

		return toReturn.trim();
	}

	/**
	 * Retourne les identifiants des instances d'un type donn�.
	 * @param documentXCSP Document XCSP
	 * @param type Type de l'instance
	 * @return Les identifiants des instances trouv�es
	 */
	public static List<Integer> getInstancesIdsOfType(Document documentXCSP, String type) {
		List<Integer> toReturn = new ArrayList<Integer>();

		Element root = documentXCSP.getRootElement();
		List<Element> variables = root.getChildren("variables").get(0).getChildren("variable");
		for (Element currentVariable: variables) {
			String variableName = currentVariable.getAttributeValue("name");
			if(variableName.contains("Id_" + type)) {
				Integer idVariable = getInstanceId(variableName);
				if (! toReturn.contains(idVariable))
					toReturn.add(idVariable);
			}
		}

		return toReturn;
	}

	public static boolean isNavigation(CSTNode node) {
		if (node instanceof FeatureCallExpCSImpl) {
			List<String> path = OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) node);
			if(path.get(0).equals("self") && path.size() == 2)
				return false;
			else
				return true;
		} else {
			return false;
		}
	}

	public static boolean isVariable(CSTNode node) {
		return (node instanceof VariableExpCS) || (! isNavigation(node) && node instanceof FeatureCallExpCSImpl);
	}
}
