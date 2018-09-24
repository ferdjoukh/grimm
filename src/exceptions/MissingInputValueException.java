package exceptions;

public class MissingInputValueException extends Exception {

	public MissingInputValueException(String name) {
		super("Value for: ["+name+"] is mandatory and is missing");
	}
}
