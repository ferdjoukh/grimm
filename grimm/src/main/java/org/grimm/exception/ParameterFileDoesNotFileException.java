package org.grimm.exception;

public class ParameterFileDoesNotFileException extends Exception {

	public ParameterFileDoesNotFileException(String file) {
		super("Parameter file: ["+file+"] does not exist");
	}
}
