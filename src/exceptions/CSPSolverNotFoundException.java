package exceptions;

public class CSPSolverNotFoundException extends Exception {
	
	public CSPSolverNotFoundException(String name) {
		super("CSP solver: ["+name+"] was not found in the current folder");
	}
}
