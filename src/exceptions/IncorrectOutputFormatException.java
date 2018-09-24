package exceptions;

public class IncorrectOutputFormatException extends Exception {

	public IncorrectOutputFormatException(String value) {
		super("Output format: "+value+ " is incorrect (choose xmi or dot)");
	}
}
