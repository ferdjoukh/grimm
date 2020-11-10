package org.grimm.utils.ocl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ocl.cst.BooleanLiteralExpCS;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.IntegerLiteralExpCS;
import org.eclipse.ocl.cst.OperationCallExpCS;
import org.eclipse.ocl.cst.VariableExpCS;
import org.jdom2.Document;
import org.jdom2.Element;

import org.grimm.Ecore.MetaModelReader;
import org.grimm.utils.GrimmLogger;

/**
 * Classe repr�sentant un ou plusieurs domaines. Elle permet de les manipuler depuis et vers le fichier XCSP.
 * Plusieurs domaines sont trouv�s lorsqu'il concerne l'attribut d'une classe abstraite : les domaines seront ceux des classes filles.
 */
public class OclDomain {

	private MetaModelReader _modelReader;
	private Document _xcspDocument;
	private String _className;
	private String _variableName;
	private ArrayList<ArrayList<Integer>> _domains;
	private ArrayList<Element> _domainElements;

	/**
	 * Cr�� un domaine qui sera initialis� depuis le document XCSP fourni.
	 * @param modelReader Modele repr�sentant le fichier ecore
	 * @param xcspDocument Document XCSP o� chercher le domaine
	 * @param className Nom de la classe concern�e
	 * @param variableName Nom de la variable concern�e
	 */
	public OclDomain(MetaModelReader modelReader, Document xcspDocument, String className, String variableName) {
		this._modelReader = modelReader;
		_xcspDocument = xcspDocument;
		_className = className;
		_variableName = variableName;
		readDomainsFromXCSP();
	}

	/**
	 * R�cup�re une liste de cha�nes de caract�res repr�sentant le domaine r�cup�r� au format XCSP :
	 * 		{@code 5..7 | 5 }
	 * @return Les domaines sous forme de liste de cha�nes de caract�res
	 */
	public List<String> getDomainsAsStringList() {
		return OclTools.getDomainsAsString(_domains);
	}

	/**
	 * R�cup�re les domaines sous forme de liste d'entiers.
	 * @return Les domaines sous forme de liste d'entiers.
	 */
	public ArrayList<ArrayList<Integer>> getDomainsAsIntegerList() {
		return _domains;
	}

	/**
	 * R�cup�re et stock les domaines depuis le document XCSP.
	 */
	private void readDomainsFromXCSP() {
		if(_domainElements == null)
			_domainElements = OclTools.getDomainsOf(_modelReader, _xcspDocument, _className, _variableName);
		
		_domains = OclTools.getElementDomainToArrayOfInteger(_domainElements);
	}

	/**
	 * Ecrit le domaine dans le document XCSP puis sauvegarde le fichier XCSP.
	 */
	private void writeDomainInXCSP() {
		for(int i = 0; i < this._domainElements.size(); i++){
			
			this._domainElements.get(i).setText(this.getDomainsAsStringList().get(i));
			this._domainElements.get(i).setAttribute("nbValues", String.valueOf(this._domains.get(i).size()));
		}
		
		//GenXCSP.saveXML(_xcspDocument, OclConstraints.XCSPFile);
	}

	/**
	 * Applique une op�ration sur le domaine courant.
	 * @param operationNode Noeud contenant l'op�ration � appliquer
	 */
	@SuppressWarnings("unchecked")
	public void applyOperationOnDomain(OperationCallExpCS operationNode) {
		ArrayList<ArrayList<Integer>> newDomains = (ArrayList<ArrayList<Integer>>) _domains.clone();

		for(int i = 0; i < this._domains.size(); i++) {

			newDomains.set(i, (ArrayList<Integer>) this._domains.get(i).clone());
			
			Iterator<Integer> valueIterator = _domains.get(i).iterator();
			while (valueIterator.hasNext()) {
				Integer currentValue = valueIterator.next();
				if (! isValueCheckedByConstraint(operationNode, currentValue)) {
					newDomains.get(i).remove(currentValue);
				}
			}
			
		}

		_domains = newDomains;
		writeDomainInXCSP();
	}

	/**
	 * V�rifie si une valeur est v�rifi�e par une contrainte.
	 * @param operationNode Noeud contenant l'op�ration
	 * @param value Valeur � v�rifier
	 * @return 	{@code true} si la valeur est v�rifi�e (donc appartient toujours au domaine), {@code false} sinon
	 */
	private boolean isValueCheckedByConstraint(OperationCallExpCS operationNode, Integer value) {
		OclOperation operation = OclTools.getNodeOperation(operationNode);
		if (operation.getType() == OclOperationType.Boolean) {
			return treatBooleanOperation(operationNode, operation, value);
		} else  {
			GrimmLogger.getInstance().severe("Op�ration non prise en charge : " + operation.toString());
			// TODO Traiter les autres cas possible (if, self, ...)
			return false;
		}
	}

	/**
	 * Traite une op�ration bool�enne et renvoie son r�sultat selon une valeur sp�cifique pour la variable de l'op�ration.
	 * @param operationNode Noeud contenant l'op�ration
	 * @param operation Type {@link OclOperation} de l'op�ration 
	 * @param value Valeur � tester de la variable concern�e
	 * @return Le r�sultat du teste
	 */
	private boolean treatBooleanOperation(OperationCallExpCS operationNode, OclOperation operation, Integer value) {
		boolean result = false;
		CSTNode[] arguments = OclTools.getArgumentsOfOperation(operationNode, operation);
		switch(operation) {
		case not:
			result = !getValueOfBooleanExpression(arguments[0], value);
			break;
		case and:
			result = getValueOfBooleanExpression(arguments[0], value) && getValueOfBooleanExpression(arguments[1], value);
			break;
		case or:
			result = getValueOfBooleanExpression(arguments[0], value) || getValueOfBooleanExpression(arguments[1], value);
			break;
		case xor:
			result = getValueOfBooleanExpression(arguments[0], value) ^ getValueOfBooleanExpression(arguments[1], value);
			break;
		case eq:
			result = getValueOfNumberExpression(arguments[0], value) == getValueOfNumberExpression(arguments[1], value);
			break;
		case ne:
			result = getValueOfNumberExpression(arguments[0], value) != getValueOfNumberExpression(arguments[1], value);
			break;
		case ge:
			result = getValueOfNumberExpression(arguments[0], value) >= getValueOfNumberExpression(arguments[1], value);
			break;
		case gt:
			result = getValueOfNumberExpression(arguments[0], value) > getValueOfNumberExpression(arguments[1], value);
			break;
		case le:
			result = getValueOfNumberExpression(arguments[0], value) <= getValueOfNumberExpression(arguments[1], value);
			break;
		case lt:
			result = getValueOfNumberExpression(arguments[0], value) < getValueOfNumberExpression(arguments[1], value);
			break;
		default:
			GrimmLogger.getInstance().severe("Op�ration bool�enne non prise en charge : " + operation.toString());
			break;
		}
		return result;
	}


	/**
	 * Traite une op�ration arith�mtique et renvoie son r�sultat selon une valeur sp�cifique pour la variable de l'op�ration.
	 * @param operationNode Noeud contenant l'op�ration
	 * @param operation Type {@link OclOperation} de l'op�ration 
	 * @param value Valeur � tester de la variable concern�e
	 * @return Le r�sultat du test
	 */
	private Integer treatNumberExpression(OperationCallExpCS operationNode, OclOperation operation, Integer value) {
		Integer result = null;
		CSTNode[] arguments = OclTools.getArgumentsOfOperation(operationNode, operation);
		switch(operation) {
		case neg:
			result = -getValueOfNumberExpression(arguments[0], value);
			break;
		case add:
			result = getValueOfNumberExpression(arguments[0], value) + getValueOfNumberExpression(arguments[1], value);
			break;
		case sub:
			result = getValueOfNumberExpression(arguments[0], value) - getValueOfNumberExpression(arguments[1], value);
			break;
		case mul:
			result = getValueOfNumberExpression(arguments[0], value) * getValueOfNumberExpression(arguments[1], value);
			break;
		case div:
			result = getValueOfNumberExpression(arguments[0], value) / getValueOfNumberExpression(arguments[1], value);
			break;
			//		case ifCond:
			//			result = getValueOfBooleanExpression(arguments[0], value) ? getValueOfNumberExpression(arguments[1], value) : getValueOfNumberExpression(arguments[2], value);
			//			break;
			// TODO Le if sera trait� ailleurs
		default:
			GrimmLogger.getInstance().severe("Op�ration arithm�tique non prise en charge : " + operation.toString());
			break;
		}
		return result;
	}

	/**
	 * R�cup�re la valeur d'une expression devant retourner un entier.
	 * @param numberExpressionNode Noeud contenant l'expression � traiter
	 * @param value Valeur � tester de la variable concern�e
	 * @return La valeur de l'expression ou {@code null} si ce n'est pas une expression arithm�tique
	 */
	private Integer getValueOfNumberExpression(CSTNode numberExpressionNode, Integer value) {
		if (numberExpressionNode instanceof OperationCallExpCS) {
			return treatNumberExpression((OperationCallExpCS) numberExpressionNode,
					OclTools.getNodeOperation((OperationCallExpCS) numberExpressionNode), value);
		} else if (numberExpressionNode instanceof IntegerLiteralExpCS) {
			return ((IntegerLiteralExpCS) numberExpressionNode).getIntegerSymbol();
		} else if(numberExpressionNode instanceof VariableExpCS){
			return value;
		} else {
			GrimmLogger.getInstance().severe("Not an integer expression : " + numberExpressionNode);
			return null;
		}
	}

	/**
	 * R�cup�re la valeur d'une expression devant retourner un bool�en.
	 * @param booleanExpressionNode Noeud contenant l'expression � traiter
	 * @param value Valeur � tester de la variable concern�e
	 * @return La valeur de l'expression
	 */
	private boolean getValueOfBooleanExpression(CSTNode booleanExpressionNode, Integer value) {
		if (booleanExpressionNode instanceof OperationCallExpCS) {
			return treatBooleanOperation((OperationCallExpCS) booleanExpressionNode,
					OclTools.getNodeOperation((OperationCallExpCS) booleanExpressionNode), value);
		} else if (booleanExpressionNode instanceof BooleanLiteralExpCS) {
			return ((BooleanLiteralExpCS) booleanExpressionNode).getBooleanSymbol();
		} else {
			GrimmLogger.getInstance().severe("Not a boolean expression : " + booleanExpressionNode);
			return false;
		}
	}
}
