package exceptions;

public class UnknownParameterException extends Exception {

	public UnknownParameterException(String value) {
		super("Parameter: ["+value+"] is unknown");
	}
}
