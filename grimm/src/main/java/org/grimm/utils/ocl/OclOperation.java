package org.grimm.utils.ocl;

/**
 * Enum�re les op�rations disponibles en OCL et en XCSP afin de faciliter leur transformation et leur traitement
 */
public enum OclOperation {
	/* BOOLEAN */
	eq(OclOperationType.Boolean, "=", 2),
	ne(OclOperationType.Boolean, "<>", 2),
	ge(OclOperationType.Boolean, ">=", 2),
	gt(OclOperationType.Boolean, ">", 2),
	le(OclOperationType.Boolean, "<=", 2),
	lt(OclOperationType.Boolean, "<", 2),

	not(OclOperationType.Boolean, "not", 1),
	and(OclOperationType.Boolean, "and", 2),
	or(OclOperationType.Boolean, "or", 2),
	xor(OclOperationType.Boolean, "xor", 2),
	//iff(OclOperationType.Boolean, "todo", 2),

	/* NUMBER */
	neg(OclOperationType.Number, "-", 1),
	add(OclOperationType.Number, "+", 2),
	sub(OclOperationType.Number, "-", 2),
	mul(OclOperationType.Number, "*", 2),
	div(OclOperationType.Number, "/", 2),
	
	/* COLLECTION */
	forAll(OclOperationType.Collection, "forAll", 1),
	
	/* OTHER */
	oclType(OclOperationType.Other, "oclType", 1)
	//ifCond(OclOperationType.Number, "if", 3)
	;

	private OclOperationType _type;
	private int _arity;
	private String _oclSymbol;

	/**
	 * Cr�� une op�ration OCL et XCSP
	 * @param type Type de l'op�ration {@link OclOperationType}
	 * @param oclSymbol Symbole de l'op�ration. Par exemple, pour l'addition le symbole sera "+"
	 * @param arity Arit� de l'op�ration. Correspond au nombre de param�tres
	 */
	private OclOperation(OclOperationType type, String oclSymbol, int arity) {
		_type = type;
		_arity = arity;
		_oclSymbol = oclSymbol;
	}

	/**
	 * R�cup�re le type de l'op�ration. 
	 * @return Le type de l'op�ration
	 * @see OclOperationType
	 */
	public OclOperationType getType() {
		return _type;
	}
	
	/**
	 * R�cup�re l'arit� de l'op�ration qui correspond � son nombre de param�tres requis.
	 * @return l'arit� de l'op�ration
	 */
	public int getArity() {
		return _arity;
	}
	
	/**
	 * R�cup�re le symbole correspondant � l'op�ration. Par exemple, pour l'addition le symbole sera "+".
	 * @return Le symbole correspondant � l'op�ration
	 */
	public String getOclSymbol() {
		return _oclSymbol;
	}

	/**
	 * Fonction qui r�cup�re une op�ration OCL � partir d'un symbole et d'une arit� particuli�re.
	 * @param oclSymbol Symbole de l'op�ration
	 * @param arity Arit� de l'op�ration
	 * @return L'op�ration OCL correspondante
	 */
	public static OclOperation getOperationFromOCLSymbol(String oclSymbol, int arity) {
		for(OclOperation op : OclOperation.values()) {
			if (op.getOclSymbol().equals(oclSymbol) && op.getArity() == arity)
				return op;
		}
		//System.err.println("Operation '" + oclSymbol + "' non prise en charge");
		return null;
	}
}
