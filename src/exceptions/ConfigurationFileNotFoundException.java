package exceptions;

public class ConfigurationFileNotFoundException extends Exception {
	
	public ConfigurationFileNotFoundException(String filePath) {
		super("Configuration file "+filePath+" does not exist :(");
	}

}
