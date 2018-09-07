package Utils.OCL;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.ClassifierContextDeclCS;
import org.eclipse.ocl.cst.DefCS;
import org.eclipse.ocl.cst.FeatureCallExpCS;
import org.eclipse.ocl.cst.InvCS;
import org.eclipse.ocl.cst.IteratorExpCS;
import org.eclipse.ocl.cst.OperationCallExpCS;
import org.eclipse.ocl.cst.PackageDeclarationCS;
import org.eclipse.ocl.cst.PathNameCS;
import org.eclipse.ocl.cst.SimpleNameCS;
import org.eclipse.ocl.cst.SimpleTypeEnum;
import org.jdom2.Document;
import org.jdom2.Element;

import Utils.GrimmLogger;
import Utils.ModelReader;
import Utils.OCL.exceptions.VariableNotFoundException;

public class OclAnalyzer {

	private ModelReader modelReader;
	private Document documentXCSP;
	private CSTNode _node;
	private List<Element> _constraintsElements;
	private List<Element> _predicatesElements;
	private List<Element> _variablesElements;
	private List<Element> _domainsElements;

	/**
	 * String contenant le package sp�cifi� par le code OCL {@code package ...}
	 */
	private String _currentPackage;

	/**
	 * Cr�� un objet qui va modifier un fichier XCSP afin d'y ajouter les contraintes OCL.
	 * @param modeleReader Modele repr�sentant le fichier ecore
	 * @param documentXCSP Document XCSP � modifier
	 * @param node Noeud contenant l'arbre syntaxique du fichier OCL
	 */
	public OclAnalyzer(ModelReader modeleReader, Document documentXCSP, CSTNode node) {
		this.modelReader = modeleReader;
		this.documentXCSP = documentXCSP;
		_node = node;

	}

	/**
	 * Fonction qui r�cup�re les contraintes cr��es � partir du fichier OCL sous format XCSP.
	 * @return Les contraintes cr��es � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getConstraintsXCSP() throws FileNotFoundException, ParserException {
		if (_constraintsElements == null)
			startAnalayze();

		return _constraintsElements;
	}

	/**
	 * Fonction qui r�cup�re les pr�dicats cr��es � partir du fichier OCL sous format XCSP.
	 * @return Les pr�dicats cr��es � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getPredicatesXCSP() throws FileNotFoundException, ParserException {
		if (_predicatesElements == null)
			startAnalayze();

		return _predicatesElements;
	}

	/**
	 * Fonction qui r�cup�re les domaines cr��s � partir du fichier OCL sous format XCSP.
	 * @return Les domaines cr��s � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getDomainsXCSP() throws FileNotFoundException, ParserException {
		if (_domainsElements == null)
			startAnalayze();

		return _domainsElements;
	}

	/**
	 * Fonction qui r�cup�re les variables cr��es � partir du fichier OCL sous format XCSP.
	 * @return Les variables cr��es � partir du fichier OCL sous format XCSP
	 * @throws FileNotFoundException Lorsque le chemin vers le fichier OCL est incorrect
	 * @throws ParserException Lorsque la syntaxe OCL est incorrecte
	 */
	public List<Element> getVariablesXCSP() throws FileNotFoundException, ParserException {
		if (_variablesElements == null)
			startAnalayze();

		return _variablesElements;
	}

	private void startAnalayze() {
		_constraintsElements = new ArrayList<Element>();
		_predicatesElements = new ArrayList<Element>();
		_variablesElements = new ArrayList<Element>();
		_domainsElements = new ArrayList<Element>();
		analyzeAndTreat(_node);
	}

	/**
	 * Va analyser et traiter un noeud de l'arbre syntaxique du fichier OCL.
	 * @param node Noeud de l'arbre syntaxique du fichier OCL
	 */
	private void analyzeAndTreat(CSTNode node) {
		//GrimmLogger.getInstance().fine("Analyzing " + OclTools.toStringNode(node));
		if (node instanceof PackageDeclarationCS)
			treatPackageDeclarationCS((PackageDeclarationCS) node);
		else if (node instanceof ClassifierContextDeclCS)
			treatClassifierContextDeclCS((ClassifierContextDeclCS) node);
		//else
			//GrimmLogger.getInstance().severe("[analyzeAndTreat] " + OclTools.toStringNode(node) + " not supported.");
	}

	/**
	 * Va analyser et traiter un noeud de type {@link PackageDeclarationCS} de l'arbre syntaxique du fichier OCL.
	 * @param node Noeud de type {@link PackageDeclarationCS} de l'arbre syntaxique du fichier OCL
	 */
	private void treatPackageDeclarationCS(PackageDeclarationCS node) {
		//GrimmLogger.getInstance().fine("Analyzing " + OclTools.toStringNode(node));
		for (EObject subNode : node.eContents()) {
			if (subNode instanceof PathNameCS) {
				_currentPackage = OclTools.getPathNameFromNode((PathNameCS) subNode);
			}
		}
		for (EObject subNode : node.eContents()) {
			if (subNode instanceof CSTNode) {
				if (! (subNode instanceof PathNameCS))
					analyzeAndTreat((CSTNode) subNode);
			}
		}
	}

	/**
	 * Va analyser et traiter un noeud de type {@link ClassifierContextDeclCS} de l'arbre syntaxique du fichier OCL.
	 * @param node Noeud de type {@link ClassifierContextDeclCS} de l'arbre syntaxique du fichier OCL
	 */
	private void treatClassifierContextDeclCS(ClassifierContextDeclCS node) {
		//GrimmLogger.getInstance().fine("Analyzing " + OclTools.toStringNode(node));
		String currentClassName = "";
		for (EObject subNode : node.eContents()) {
			if (subNode instanceof PathNameCS) {
				currentClassName = OclTools.getPathNameFromNode((PathNameCS) subNode);
			} else if (subNode instanceof InvCS) {
				treatInvCS((InvCS) subNode, currentClassName);
			} else if (subNode instanceof DefCS) {
				treatDefCS((DefCS) subNode, currentClassName);
			}
		}
	}

	/**
	 * Traite un noeud contenant une contrainte de type invariant.
	 * Grammaire :
	 * 		{@literal <InvCS> :=
	 * 			"inv:" <SPecificationCS> |
	 * 			"inv" <UnrestrictedName> ":" <SPecificationCS> |
	 * 			"inv" <UnrestrictedName> "(" <SPecificationCS> ") :" <SPecificationCS>}
	 * @param node Noeud de type {@link InvCS} 
	 * @param concernedClassName Classe concern�e par la contrainte
	 */
	private void treatInvCS(InvCS node, String concernedClassName) {
		//GrimmLogger.getInstance().fine("Analyzing " + OclTools.toStringNode(node));
		for (EObject subNode : node.eContents()) {
			// TODO Traiter le nom de la contrainte
			// TODO Traiter le message de violation (SpecificationCS entre parenth�ses)
			if (subNode instanceof OperationCallExpCS) {
				treatOperationCallExpCS((OperationCallExpCS) subNode, concernedClassName);
			} else if (subNode instanceof IteratorExpCS) {
				treatIteratorExpCS((IteratorExpCS) subNode, concernedClassName);
			}
		}
	}

	// TODO
	private void treatDefCS(DefCS node, String concernedClassName) {
		//GrimmLogger.getInstance().fine("Analyzing " + OclTools.toStringNode(node));
		// TODO
	}

	/**
	 * Traite une op�ration. L'op�ration peut �tre infix�e ou pr�fix�e.
	 * @param node Noeud contenant l'op�ration
	 * @param concernedClassName Nom de la classe concern�e par la contrainte
	 * @see OclDomain
	 */
	private void treatOperationCallExpCS(OperationCallExpCS node, String concernedClassName) {
		//GrimmLogger.getInstance().fine("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		OclOperation operation = OclTools.getNodeOperation(node);
		CSTNode[] arguments = OclTools.getArgumentsOfOperation(node, operation);

		if (OclTools.isNavigation(arguments[0])) {
			if (OclTools.isNavigation(arguments[1])) {
				treatOperationNavNav(node, concernedClassName);
			} else if (OclTools.isVariable(arguments[1])) {
				treatOperationNavVar(node, concernedClassName);
			} else {
				treatOperationNavCste(node, concernedClassName);
			}
		} else if (OclTools.isVariable(arguments[0])) {
			if (OclTools.isNavigation(arguments[1])) {
				treatOperationNavVar(node, concernedClassName);
			} else if (OclTools.isVariable(arguments[1])) {
				treatOperationVarVar(node, concernedClassName);
			} else {
				treatOperationVarCste(node, concernedClassName);
			}
		} else {
			if (OclTools.isNavigation(arguments[1])) {
				treatOperationNavCste(node, concernedClassName);
			} else if (OclTools.isVariable(arguments[1])) {
				treatOperationVarCste(node, concernedClassName);
			} else {
				//GrimmLogger.getInstance().warning("An operation with two constants as arguments is impossible.");
			}
		}

		//		List<String> attributeNames = OclTools.getVariablesInNodeAndHisSubNodesOfType(node, SimpleTypeEnum.IDENTIFIER_LITERAL, SimpleTypeEnum.SELF_LITERAL);
		//		System.out.println(attributeNames);
		//		if(attributeNames.size() == 1) {
		//			if (OclTools.countSubNodesOfType(node, FeatureCallExpCSImpl.class) != 0) {
		//				treatOperationNavCste(node, concernedClassName);
		//				
		//			} else {
		//				treatOperationVarCste(node, concernedClassName);
		//			}
		//		} else {
		//			treatOperationVarVar(node, concernedClassName);
		//		}
	}

	/**
	 * Traite une it�ration sur une collection.
	 * @param node Noeud contenant l'it�ration
	 * @param concernedClassName Nom de la classe concern�e par la contrainte
	 * @see OclPredicate
	 */
	private void treatIteratorExpCS(IteratorExpCS node, String concernedClassName) {
		//GrimmLogger.getInstance().fine("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		List<String> variables = OclTools.getVariablesInNodeAndHisSubNodesOfType(node, SimpleTypeEnum.IDENTIFIER_LITERAL);

		// On va tester le cas o� l'it�rateur n�cessite un traitement de type AllDiff
		// Conditions n�cessaires : 3 variables (la collection + 2) et un op�rateur de diff�rence
		if (variables.size() > 2) {
			if (variables.size() == 3) {
				boolean isAllDiff = false;
				for (EObject obj : node.eContents()) {
					if (obj instanceof OperationCallExpCS) {
						OperationCallExpCS operationNode = (OperationCallExpCS) obj;
						OclOperation operation = OclTools.getNodeOperation(operationNode);
						if (operation == OclOperation.ne) {
							// Dans ce cas, c'est un AllDiff
							isAllDiff = true;
							CSTNode[] diffOperationArguments = OclTools.getArgumentsOfOperation(operationNode, operation);
							List<String> argumentsNames = new ArrayList<String>();
							for(CSTNode argumentNode : diffOperationArguments) {
								if (argumentNode instanceof FeatureCallExpCS) {
									argumentsNames.add(((SimpleNameCS)argumentNode.eContents().get(argumentNode.eContents().size() - 1)).getValue());
								} else {
									GrimmLogger.getInstance().severe("Type de variable non pris en charge : " + OclTools.toStringNode(argumentNode));
								}
							}

							// On r�cup�re les domaines de la collection qui contiennent les identifiants des instances qui sont dans la collections
							ArrayList<ArrayList<Integer>> domains = OclTools.getDomainsArrayOf(modelReader, documentXCSP, concernedClassName, variables.get(0));
							List<String> xcspVariablesNames = new ArrayList<String>();
							for (List<Integer> domain : domains) {
								for(Integer idInstance : domain) {
									for(String argument : argumentsNames) {
										String instanceTypeName = OclTools.getTypeOfInstanceID(documentXCSP, idInstance);
										if (instanceTypeName != null) {
											List<String> variablesNames = OclTools.getVariablesNamesOf(documentXCSP, instanceTypeName, argument);
											for (String variableName : variablesNames) {
												if (! xcspVariablesNames.contains(variableName)) {
													xcspVariablesNames.add(variableName);
												}
											}
										} else {
											//GrimmLogger.getInstance().warning("Instance with ID " + idInstance + " not found : instance ignored in collection " + concernedClassName + ":" + variables.get(0));
										}
									}
								}
							}

							_constraintsElements.add(OclPredicate.createConstraintElementOfGlobalAllDiff("AllDiff" + _constraintsElements.size(), xcspVariablesNames));
						}
					}
				}
				if (! isAllDiff) {
					//GrimmLogger.getInstance().severe("Les it�rateurs � 2 variables ne sont pas pris en charge (sauf les cas AllDiff).");
				}
			} else {
				//GrimmLogger.getInstance().severe("Les it�rateurs � plus de 2 variables ne sont pas pris en charge (sauf les cas AllDiff).");
			}
		} else {
			// TODO It�rateur � une variable
			//GrimmLogger.getInstance().severe("Les it�rateurs � 1 variable ne sont pas pris en charge.");
		}
	}

	private void treatOclTypeComparaisonNode(OperationCallExpCS node, String concernedClassName, List<String> attributeNames) {
		//GrimmLogger.getInstance().finest("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		// On r�cup�re les types des variables
		List<String> variablesTypes = new ArrayList<String>();
		for (String attributeName : attributeNames) {
			String toAdd = OclTools.getTypeOfAttribute(modelReader, concernedClassName, attributeName);
			if(! variablesTypes.contains(toAdd))
				variablesTypes.add(toAdd);
		}

		// On r�cup�re les domaines concern�s par ces types (DC_Type)
		List<Element> concernedDomainsElements = new ArrayList<Element>();
		for (String variableType : variablesTypes) {
			concernedDomainsElements.addAll(OclTools.getDomainsOfClass(modelReader, documentXCSP, variableType));
		}

		// On va cr�er les variables V ayant comme domaine les DC_Type
		List<Element> variablesV = new ArrayList<Element>();
		for (Element domainDC: concernedDomainsElements) {
			String domainName = domainDC.getAttributeValue("name");
			variablesV.add(OclTools.createVariableElement(domainName, domainName.replace("DC_", "V_")));
		}
		_variablesElements.addAll(variablesV);

		// On r�cup�re les noms des variables XCSP concern�es
		ArrayList<String> variablesXcspNames = new ArrayList<>();
		for (String currentAttributeName : attributeNames) {
			variablesXcspNames.addAll(OclTools.getVariablesNamesOf(documentXCSP, concernedClassName, currentAttributeName));
		}

		// On va cr�er les variables "I"
		// D'abord leur domaine (commun)
		ArrayList<Integer> domainIValues = new ArrayList<Integer>();
		for(int j = 0; j < variablesV.size(); j++)
			domainIValues.add(j);
		String domainIName = "DI_" + domainIValues.get(0) + "_" + domainIValues.get(domainIValues.size() - 1);
		Element domainI = OclTools.createDomainElement(domainIName, domainIValues);
		_domainsElements.add(domainI);

		ArrayList<Element> variablesI = new ArrayList<>();
		for(int i = 0; i < variablesXcspNames.size(); i++) {
			variablesI.add(OclTools.createVariableElement(domainIName, domainIName.replace("DI_", "I_") + "_" + i));
		}
		_variablesElements.addAll(variablesI);

		// On associe des variablesI � chaque instance pour des raisons pratiques (construction des contraintes Element et Alldiff)
		HashMap<Integer, HashMap<String, String>> associationInstanceVariablesI = new HashMap<Integer, HashMap<String,String>>();
		Iterator<Element> iteratorVariablesI = variablesI.iterator();
		Iterator<String> iteratorVariablesXcspNames = variablesXcspNames.iterator();
		while(iteratorVariablesXcspNames.hasNext()) {
			String currentVarXcspName = iteratorVariablesXcspNames.next();
			Integer idVarXcsp = OclTools.getInstanceId(currentVarXcspName);
			if (associationInstanceVariablesI.get(idVarXcsp) == null) {
				HashMap<String, String> associatedVariablesI = new HashMap<String, String>();
				associatedVariablesI.put(currentVarXcspName, iteratorVariablesI.next().getAttributeValue("name"));
				associationInstanceVariablesI.put(idVarXcsp, associatedVariablesI);
			} else {
				associationInstanceVariablesI.get(idVarXcsp).put(currentVarXcspName, iteratorVariablesI.next().getAttributeValue("name"));
			}
		}

		// Cr�er les contraintes de type Element
		// On cr�� la chaine de caract�re contenant la suite des noms des variables V
		String[] parametersText = new String[variablesV.size()];
		int tabCounter = 0;
		for (Element element : variablesV) {
			parametersText[tabCounter] = element.getAttributeValue("name");
			tabCounter++;
		}

		for(Integer idVarXcsp : associationInstanceVariablesI.keySet()) {
			for (String varXcspName : associationInstanceVariablesI.get(idVarXcsp).keySet()) {
				String indexDomainName = associationInstanceVariablesI.get(idVarXcsp).get(varXcspName);
				_constraintsElements.add(OclPredicate.createConstraintElementOfGlobalElement("ConsElement" + varXcspName + indexDomainName,
						varXcspName, indexDomainName, parametersText));
			}
		}

		// On cr�� les contraintes AllDiff
		for(Integer idVarXcsp : associationInstanceVariablesI.keySet()) {
			List<String> parametersAllDiff = new ArrayList<>();
			for (String varXcspName : associationInstanceVariablesI.get(idVarXcsp).keySet()) {
				parametersAllDiff.add(associationInstanceVariablesI.get(idVarXcsp).get(varXcspName));
			}
			String constraintName = "AllDiffOclType" + OclTools.parametersAsStringSeparatedBySpace(parametersAllDiff).replace(" ", "");
			_constraintsElements.add(OclPredicate.createConstraintElementOfGlobalAllDiff(constraintName, parametersAllDiff));
		}
	}

	private void treatOperationVarCste(OperationCallExpCS node, String concernedClassName) {
		GrimmLogger.getInstance().finest("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		List<String> attributeNames = OclTools.getVariablesInNodeAndHisSubNodesOfType(node, SimpleTypeEnum.IDENTIFIER_LITERAL);
		OclDomain domain = new OclDomain(this.modelReader, documentXCSP, concernedClassName, attributeNames.get(0));
		domain.applyOperationOnDomain(node);
	}

	private void treatOperationVarVar(OperationCallExpCS node, String concernedClassName) {
		GrimmLogger.getInstance().finest("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		String[] attributeNames = OclTools.getVariablesNamesInOperation(node);
		OclPredicate predicate = new OclPredicate(node);
		_predicatesElements.add(predicate.getPredicateXCSP());

		List<List<String>> variablesXcspNames = new ArrayList<List<String>>();
		boolean haveSelfAttribute = false;
		for(String att : attributeNames) {
			if (att.equals("self")) {
				haveSelfAttribute = true;
			} else {
				List<String> toAdd = new ArrayList<String>();
				toAdd.addAll(OclTools.getVariablesNamesOf(documentXCSP, concernedClassName, att));
				variablesXcspNames.add(toAdd);
			}
		}

		for(List<String> combination : OclTools.getCombinationOfLists(variablesXcspNames)) {
			if (haveSelfAttribute) {
				List<String> newCombination = new ArrayList<String>(combination);
				int selfPos = 0;
				while (! attributeNames[selfPos].equals("self")) {
					selfPos++;
				}
				newCombination.add(selfPos, String.valueOf(OclTools.getInstanceId(combination.get(0))));
				_constraintsElements.add(predicate.getConstraintXCSP(newCombination));
			} else {
				_constraintsElements.add(predicate.getConstraintXCSP(combination));
			}
		}
	}

	private void treatOperationNavCste(OperationCallExpCS node, String concernedClassName) {
		GrimmLogger.getInstance().finest("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		OclPredicate predicate = new OclPredicate(node);
		_predicatesElements.add(predicate.getPredicateXCSP());

		CSTNode navNode = null;
		for(EObject subNode : node.eContents()) {
			if (subNode instanceof CSTNode) {
				if (OclTools.isNavigation((CSTNode) subNode)) {
					navNode = (CSTNode) subNode;
				}
			}
		}

		// On traite la navigation
		List<String> featurePath = OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) navNode);
		featurePath.remove("self"); // Self est inutile ici

		List<List<Integer>> domainsFromNavigation = new ArrayList<List<Integer>>();
		String currentClass = concernedClassName;
		domainsFromNavigation.add(OclTools.getInstancesIdsOfType(documentXCSP, currentClass));
		for(int i = 0; i < featurePath.size() - 1; i++) { // On ne parcours pas le dernier attribut
			String attribute = featurePath.get(i);
			domainsFromNavigation.addAll(OclTools.getDomainsArrayOf(modelReader, documentXCSP, currentClass, attribute));
			currentClass = OclTools.getTypeOfAttribute(modelReader, currentClass, attribute);
		}

		List<List<Integer>> combinationsList = OclTools.getCombinationOfLists(domainsFromNavigation);
		for(List<Integer> combination : combinationsList) {
			// Une combinaison est un ensemble d'identifiants d'instances qui sont dans le m�me ordre que le chemin de navigation
			// Si une combinaison d�bute et termine par le m�me identifiant d'instance, on l'ignore
			if (! combination.get(0).equals(combination.get(combination.size() - 1))) {
				try {
					// On parcours les identifiants de la combinaison
					Iterator<String> attributes = featurePath.iterator();
					List<List<String>> parametersList = new ArrayList<List<String>>();
					parametersList.add(new ArrayList<String>());
					for(Integer instanceId : combination) {
						String attribute = attributes.next();
						parametersList = OclTools.getCombinationOfLists(parametersList,
								OclTools.getVariable(documentXCSP, instanceId, attribute));
					}

					// On ajoute les identifiants de chaque variable avant les variables elles-m�mes
					List<List<String>> newParametersList = new ArrayList<List<String>>();
					for(List<String> parameters : parametersList) {
						List<String> newParameters = new ArrayList<String>();
							for(String parameter : parameters) {
								newParameters.add(String.valueOf(OclTools.getInstanceId(parameter)));
								newParameters.add(parameter);
						}
							newParameters.remove(0);
							newParametersList.add(newParameters);
					}
					parametersList = newParametersList;
					
					for (List<String> parameters : parametersList) {
						_constraintsElements.add(predicate.getConstraintXCSP(parameters));
					}
				} catch (VariableNotFoundException e) {
					GrimmLogger.getInstance().warning((e.getMessage() + " => combination ignored"));
				}
			}
			
		}
	}

	private void treatOperationNavVar(OperationCallExpCS node, String concernedClassName) {
		GrimmLogger.getInstance().finest("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		OclPredicate predicate = new OclPredicate(node);
		_predicatesElements.add(predicate.getPredicateXCSP());

		CSTNode varNode = null, navNode = null;
		for(EObject subNode : node.eContents()) {
			if (subNode instanceof CSTNode) {
				if (OclTools.isNavigation((CSTNode) subNode)) {
					navNode = (CSTNode) subNode;
				} else {
					varNode = (CSTNode) subNode;
				}
			}
		}

		// On traite la navigation
		List<String> featurePath = OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) navNode);
		featurePath.remove("self"); // Self est inutile ici

		List<List<Integer>> domainsFromNavigation = new ArrayList<List<Integer>>();
		String currentClass = concernedClassName;
		domainsFromNavigation.add(OclTools.getInstancesIdsOfType(documentXCSP, currentClass));
		for(int i = 0; i < featurePath.size() - 1; i++) { // On ne parcours pas le dernier attribut
			String attribute = featurePath.get(i);
			domainsFromNavigation.addAll(OclTools.getDomainsArrayOf(modelReader, documentXCSP, currentClass, attribute));
			currentClass = OclTools.getTypeOfAttribute(modelReader, currentClass, attribute);
		}

		List<List<Integer>> combinationsList = OclTools.getCombinationOfLists(domainsFromNavigation);
		for(List<Integer> combination : combinationsList) {
			// Une combinaison est un ensemble d'identifiants d'instances qui sont dans le m�me ordre que le chemin de navigation
			// Si une combinaison d�bute et termine par le m�me identifiant d'instance, on l'ignore
			if (! combination.get(0).equals(combination.get(combination.size() - 1))) {
				try {
					// On parcours les identifiants de la combinaison
					Iterator<String> attributes = featurePath.iterator();
					List<List<String>> parametersList = new ArrayList<List<String>>();
					parametersList.add(new ArrayList<String>());
					for(Integer instanceId : combination) {
						String attribute = attributes.next();
						parametersList = OclTools.getCombinationOfLists(parametersList,
								OclTools.getVariable(documentXCSP, instanceId, attribute));
					}

					// On ajoute les identifiants de chaque variable avant les variables elles-m�mes
					List<List<String>> newParametersList = new ArrayList<List<String>>();
					for(List<String> parameters : parametersList) {
						List<String> newParameters = new ArrayList<String>();
							for(String parameter : parameters) {
								newParameters.add(String.valueOf(OclTools.getInstanceId(parameter)));
								newParameters.add(parameter);
						}
							newParameters.remove(0);
							newParametersList.add(newParameters);
					}
					parametersList = newParametersList;

					// On ajoute la variable aux param�tres
					String varParam;
					if (varNode instanceof FeatureCallExpCS) {
						List<String> varFeaturePath = OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) varNode);
						varParam = varFeaturePath.get(varFeaturePath.size() - 1);
					} else {
						List<String> varsInNode = OclTools.getVariablesInNodeAndHisSubNodesOfType(varNode, SimpleTypeEnum.IDENTIFIER_LITERAL);
						varParam = varsInNode.get(0);
					}
					parametersList = OclTools.getCombinationOfLists(parametersList,
							OclTools.getVariable(documentXCSP, combination.get(0), varParam));

					for (List<String> parameters : parametersList) {
						_constraintsElements.add(predicate.getConstraintXCSP(parameters));
					}
				} catch (VariableNotFoundException e) {
					GrimmLogger.getInstance().warning((e.getMessage() + " => combination ignored"));
				}
			}
		}
	}

	private void treatOperationNavNav(OperationCallExpCS node, String concernedClassName) {
		GrimmLogger.getInstance().finest("Analyzing " + OclTools.toStringNode(node) + " in context " + concernedClassName);

		if (OclTools.isOclTypeComparaisonOperationNode(node)) {
			treatOclTypeComparaisonNode(node, concernedClassName,
					OclTools.getVariablesInNodeAndHisSubNodesOfType(node, SimpleTypeEnum.IDENTIFIER_LITERAL, SimpleTypeEnum.SELF_LITERAL));
		} else {
			// TODO
		}
	}

}
