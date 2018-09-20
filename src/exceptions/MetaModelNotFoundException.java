package exceptions;

public class MetaModelNotFoundException extends Exception{
	
	public MetaModelNotFoundException(String msg) {
		super(msg+" meta-model file doest not exist");
	}

}
