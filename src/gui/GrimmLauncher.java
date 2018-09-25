package gui;

import exceptions.MissingGrimmParameterException;
import exceptions.UnknownParameterException;

public class GrimmLauncher {

	public static void main(String[] args) {
		
		if(args.length==0) {
			Help.printHelp();
		}else {
			switch(args[0]) {
			
				case "help":{
					Help.printHelp();
				}
				break;
				
				case "h":{
					Help.printHelp();
				}
				break;
				
				case "parameter":{
					try {
						createParameterFile(args);
					}catch(MissingGrimmParameterException e) {
						System.out.println(e.getMessage());
					}
				}
				break;
				
				case "p":{
					try {
						createParameterFile(args);
					}catch(MissingGrimmParameterException e) {
						System.out.println(e.getMessage());
					}					
				}
				break;
				
				case "config":{
					try {
						createConfigFile(args);
					}catch(MissingGrimmParameterException e) {
						System.out.println(e.getMessage());
					}
				}
				break;
				
				case "c":{
					try {
						createConfigFile(args);
					}catch(MissingGrimmParameterException e) {
						System.out.println(e.getMessage());
					}			
				}
				break;
				
				case "generate":{
					try {
						generateModels(args);
					}catch(MissingGrimmParameterException e) {
						System.out.println(e.getMessage());
					}
				}
				break;
				
				case "g":{
					try {
						generateModels(args);
					}catch(MissingGrimmParameterException e) {
						System.out.println(e.getMessage());
					}
				}
				break;
				
				default:{
					try {
						incorrectParameter(args);
					}
					catch (UnknownParameterException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}

	}

	/**
	 * Creates a parameters file for grimm.
	 * 
	 * @param args
	 * @throws MissingGrimmParameterException
	 */
	private static void createParameterFile(String[] args) throws MissingGrimmParameterException {
		
		if(args.length<2) {
			throw new MissingGrimmParameterException("creation of parameters file requires a filePath");
		}		
	}
		
	/**
	 * Creates a configuration File for a given meta-model and its root class
	 * 
	 * @param args
	 * @throws MissingGrimmParameterException
	 */
	private static void createConfigFile(String[] args) throws MissingGrimmParameterException {
		
		if(args.length<3) {
			throw new MissingGrimmParameterException("creation of configuration file requires a meta-model and a root class");
		}
	}
	
	/**
	 * Generates models using Grimm for a given parameters file
	 * 
	 * @param args
	 * @throws MissingGrimmParameterException
	 */
	private static void generateModels(String[] args) throws MissingGrimmParameterException {
		
		if(args.length<2) {
			throw new MissingGrimmParameterException("generation of models requires a parameters file");
		}	
	}
	
	private static void incorrectParameter(String[] args) throws UnknownParameterException {
		UnknownParameterException e= new UnknownParameterException(args[0]);
		throw e;
	}

}
