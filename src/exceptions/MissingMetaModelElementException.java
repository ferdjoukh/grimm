package exceptions;

public class MissingMetaModelElementException extends Exception {
	
	public MissingMetaModelElementException(String msg, String metamodel) {
		super(msg + " was not found in meta-model: ["+ metamodel+"]");
	}
	

}
