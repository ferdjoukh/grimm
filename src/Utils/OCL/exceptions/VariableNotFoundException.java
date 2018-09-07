package Utils.OCL.exceptions;

@SuppressWarnings("serial")
public class VariableNotFoundException extends Exception {

	public VariableNotFoundException(String className, String attributeName) {
		super("Variable of class " + className + " with attribute " + attributeName + " not found");
	}
	
	public VariableNotFoundException(Integer idInstance, String attributeName) {
		super("Variable of ID " + idInstance + " with attribute " + attributeName + " not found");
	}
	
}
