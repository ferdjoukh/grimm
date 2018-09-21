package exceptions;

public class MissingInputValueException extends Exception {

	public MissingInputValueException(String name) {
		super(name+" is a mandatory information and is missing");
	}
}
