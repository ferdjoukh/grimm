package exceptions;

public class InputValueIsNotAnIntegerException extends Exception {

	public InputValueIsNotAnIntegerException(String value, String forwhat) {
		super("String value "+value+" for "+ forwhat +" is not an integer value");
	}
	
}
