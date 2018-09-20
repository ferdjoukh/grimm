package exceptions;

public class MetaModelNotFoundException extends Exception{
	
	public MetaModelNotFoundException(String filePath) {
		super("Meta-model "+filePath+" doest not exist");
	}

}
