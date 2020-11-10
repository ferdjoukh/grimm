package org.grimm.exception;

public class PositiveIntegerInputException extends Exception {

	public PositiveIntegerInputException(String value, String forwhat) {
		super("Value: ["+value+"] for ["+ forwhat +"] is not a positive integer");
	}
	
}
