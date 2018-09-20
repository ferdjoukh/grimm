package exceptions;

public class OCLFileNotFoundException extends Exception{

	public OCLFileNotFoundException(String filePath) {
		super("OCL file "+filePath+" does not exist");
	}
}
