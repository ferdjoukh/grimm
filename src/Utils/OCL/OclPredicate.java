package Utils.OCL;

import java.util.List;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.FeatureCallExpCS;
import org.eclipse.ocl.cst.OperationCallExpCS;
import org.jdom2.Element;

public class OclPredicate {

	// Noms des noeuds XML contenant une contrainte ou un pr�dicat
	private static final String CONSTRAINT_ELEMENT_NAME = "constraint";
	private static final String PREDICATE_ELEMENT_NAME = "predicate";

	private static final String PATTERN_STRING_INSERTION = "####";
	
	private static int PREDICATE_COUNTER = 0;

	private String _name;
	private OperationCallExpCS _operationNode;
	private int _arity;
	private Element _predicateElement;
	private Element _parametersElement;
	private Element _expressionElement;

	public OclPredicate(String name, OperationCallExpCS operationNode) {
		_name = name;
		_operationNode = operationNode;
		_arity = 0;
	}

	public OclPredicate(OperationCallExpCS operationNode) {
		_name = PREDICATE_ELEMENT_NAME + "_" + PREDICATE_COUNTER;
		PREDICATE_COUNTER++;
		_operationNode = operationNode;
		_arity = 0;
	}

	public Element getPredicateXCSP() {
		if(_predicateElement == null)
			generatePredicateXCSP();
		return _predicateElement;
	}

	public Element getConstraintXCSP(String ... parameters) {
		if (parameters.length != _arity) {
			//System.err.println("Le nombre de param�tres donn�es (" + parameters.length + ") � " + _name + 
			//		" ne correspond pas au nombre attendu (" + _arity + "). Param�tres donn�s : " + Arrays.toString(parameters));
			return null;
		} else {
			Element toReturn = new Element(CONSTRAINT_ELEMENT_NAME);

			String constraintName = "Const";
			for(String p : parameters)
				constraintName += p;
			toReturn.setAttribute("name", constraintName);

			toReturn.setAttribute("arity", String.valueOf(_arity));

			String parametersString = "";
			for(String p : parameters)
				parametersString += p + " ";
			parametersString.trim();
			toReturn.setAttribute("scope", OclTools.getScopeFromParameters(parametersString));

			toReturn.setAttribute("reference", _name);

			Element parametersElement = new Element("parameters");
			parametersElement.setText(parametersString);
			toReturn.addContent(parametersElement);

			return toReturn;
		}
	}

	public Element getConstraintXCSP(List<String> parameters) {
		String[] params = new String[parameters.size()];
		for(int i = 0; i < parameters.size(); i++)
			params[i] = parameters.get(i);
		return getConstraintXCSP(params);
	}

	private void generatePredicateXCSP() {
		_predicateElement = new Element(PREDICATE_ELEMENT_NAME);
		_predicateElement.setAttribute("name", _name);

		_parametersElement = new Element("parameters");
		_predicateElement.addContent(_parametersElement);
		_parametersElement.setText(PATTERN_STRING_INSERTION);

		Element tmpExpressionElement = new Element("expression");
		_expressionElement = new Element("functional");
		tmpExpressionElement.addContent(_expressionElement);
		_predicateElement.addContent(tmpExpressionElement);
		_expressionElement.setText(PATTERN_STRING_INSERTION);

		treatOperationCallExpCS(_operationNode);
		
		addInExpression("");
		addInParameters("");
	}

	private void treatOperationCallExpCS(OperationCallExpCS node) {
		OclOperation operation = OclTools.getNodeOperation(node);
		CSTNode[] arguments = OclTools.getArgumentsOfOperation(node, operation);

		if (OclTools.isNavigation(arguments[0])) {
			if (OclTools.isNavigation(arguments[1])) {
				treatOperationNavNav(node, arguments);
			} else if (OclTools.isVariable(arguments[1])) {
				treatOperationNavVar(node, arguments);
			} else {
				treatOperationNavCste(node, arguments);
			}
		} else if (OclTools.isVariable(arguments[0])) {
			if (OclTools.isNavigation(arguments[1])) {
				treatOperationNavVar(node, arguments);
			} else if (OclTools.isVariable(arguments[1])) {
				treatOperationVarVar(node, arguments);
			}
		} else {
			if (OclTools.isNavigation(arguments[1])) {
				treatOperationNavCste(node, arguments);
			}
		}
	}

	private void treatNavigation(List<String> featurePath) {
		for (int i = 0; i < featurePath.size() - 1; i++) {
			if (! featurePath.get(i).equals("self")) {
				addInParameters(" int X" + _arity + " int Cste" + (_arity + 1) + PATTERN_STRING_INSERTION);
				addInExpression("and((eq(X" + _arity + ", Cste" + (_arity + 1) + ")), " + PATTERN_STRING_INSERTION + ")");
				_arity += 2;
			}
		}
	}

	private void treatOperation(String operationToString, String ... variables) {
		for (String var : variables) {
			addInParameters(" int " + var + PATTERN_STRING_INSERTION);
			_arity ++;
		}

		addInExpression("(" + operationToString.trim() + ")" + PATTERN_STRING_INSERTION);
	}

	private void treatOperationNavNav(OperationCallExpCS node, CSTNode[] arguments) {
		treatNavigation(OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) arguments[0]));
		treatNavigation(OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) arguments[1]));

		String operationAsString = OclTools.getOperationNodeAsOperationString(node, false);
		treatOperation(operationAsString, OclTools.getVariablesNamesInOperation(operationAsString));
	}

	private void treatOperationNavVar(OperationCallExpCS node, CSTNode[] arguments) {
		CSTNode featureNode;
		if(OclTools.isNavigation(arguments[0])) {
			featureNode = arguments[0];
		} else {
			featureNode = arguments[1];
		}

		treatNavigation(OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) featureNode));

		String operationAsString = OclTools.getOperationNodeAsOperationString(node, false);
		treatOperation(operationAsString, OclTools.getVariablesNamesInOperation(operationAsString));
	}

	private void treatOperationNavCste(OperationCallExpCS node, CSTNode[] arguments) {
		CSTNode featureNode;
		if(OclTools.isNavigation(arguments[0])) {
			featureNode = arguments[0];
		} else {
			featureNode = arguments[1];
		}

		List<String> featurePath = OclTools.getPathFromFeatureCallExpCS((FeatureCallExpCS) featureNode);
		treatNavigation(featurePath);

		treatOperation(OclTools.getOperationNodeAsOperationString(node, false), featurePath.get(featurePath.size() - 1));
	}

	private void treatOperationVarVar(OperationCallExpCS node, CSTNode[] arguments) {
		String operationAsString = OclTools.getOperationNodeAsOperationString(node, false);
		treatOperation(operationAsString, OclTools.getVariablesNamesInOperation(operationAsString));
	}

	public int getArity() {
		return _arity;
	}

	public static Element createConstraintElementOfGlobalAllDiff(String constraintName, List<String> parameters) {
		String parametersListAsString = OclTools.parametersAsStringSeparatedBySpace(parameters);

		Element constraintElement = new Element(CONSTRAINT_ELEMENT_NAME);
		constraintElement.setAttribute("name", constraintName);
		constraintElement.setAttribute("arity", String.valueOf(parameters.size()));
		constraintElement.setAttribute("scope", OclTools.getScopeFromParameters(parametersListAsString));
		constraintElement.setAttribute("reference", "global:allDifferent");

		Element parametersElement = new Element("parameters");
		parametersElement.addContent("[ " + parametersListAsString + " ]");
		constraintElement.addContent(parametersElement);

		return constraintElement;
	}

	public static Element createConstraintElementOfGlobalElement(String constraintName, String variableName, String indexDomainName, String ... array) {
		Element constraintElement = new Element(CONSTRAINT_ELEMENT_NAME);

		constraintElement.setAttribute("name", constraintName);
		constraintElement.setAttribute("arity", String.valueOf(2 + array.length));
		String arrayAsString = OclTools.parametersAsStringSeparatedBySpace(array);
		constraintElement.setAttribute("scope", OclTools.getScopeFromParameters(OclTools.parametersAsStringSeparatedBySpace(variableName, indexDomainName) + " " + arrayAsString));
		constraintElement.setAttribute("reference", "global:Element");

		Element parametersElement = new Element("parameters");
		parametersElement.setText(indexDomainName + " [ " + arrayAsString + " ] " + variableName);
		constraintElement.addContent(parametersElement);

		return constraintElement;
	}
	
	private void addInParameters(String toAdd) {
		String parameters = _parametersElement.getText();
		parameters = parameters.replaceAll(PATTERN_STRING_INSERTION, toAdd);
		_parametersElement.setText(parameters);
	}
	
	private void addInExpression(String toAdd) {
		String expression = _expressionElement.getText();
		expression = expression.replaceAll(PATTERN_STRING_INSERTION, toAdd);
		_expressionElement.setText(expression);
	}

}
