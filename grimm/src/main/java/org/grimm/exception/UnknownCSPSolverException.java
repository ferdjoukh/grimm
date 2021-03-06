package org.grimm.exception;

public class UnknownCSPSolverException extends Exception {

	public UnknownCSPSolverException(String solver) {
		super("CSP solver: ["+solver+ "] is unknown (choose abscon)");
	}
}
