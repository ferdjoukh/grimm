package org.grimm.exception;

public class MissingGrimmParameterException extends Exception {
	public MissingGrimmParameterException(String msg) {
		super("Missing parameter for grimm: "+msg);
	}
}
