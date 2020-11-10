package org.grimm.exception;

public class MetaModelNotFoundException extends Exception{
	
	public MetaModelNotFoundException(String filePath) {
		super("Meta-model file: ["+filePath+"] was not found");
	}

}
