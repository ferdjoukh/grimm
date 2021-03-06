package org.grimm.gui;

import java.io.File;
import java.io.IOException;

import org.grimm.CSP2Model.*;
import org.grimm.Ecore2CSP.*;
import org.grimm.exception.*;
import org.grimm.genetics.ChromosomeReader;

public class GrimmLauncher {

	public static void main(String[] args) throws IOException, CSPSolverNotFoundException {
		
		///////////////////////////////////////
		//Check is abscon solver is available
		///////////////////////////////////////
		try {
			isSolverExisting("abssol.jar");
		}
		catch(CSPSolverNotFoundException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		
		if(args.length==0) {
			Help.printHelp();
		}else {
			switch(args[0]) {
			
				case "h":
				case "help":{
					Help.printHelp();
				}
				break;
				
				case "p":
				case "parameter":{
					try {
						createParameterFile(args);
					}catch(Exception e) {
						System.out.println(e.getMessage());
					}
				}
				break;
				
				case "c":
				case "config":{
					try {
						createConfigFile(args);
					}catch(Exception e) {
						System.out.println(e.getMessage());
					}			
				}
				break;
				
				case "g":
				case "generate":{
					try {
						generateModels(args,false);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				break;
				
				case "chr":
				case "generate--chr":{
					try {
						generateModels(args,true);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				break;
				
				case "v":
				case "validate":{
					try {
						validateChromosome(args);
					}catch(Exception e) {
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
	 * This method is used to validate a given Chromosome (.chr file)
	 * 
	 * If the chromosome is valid then, generate a corresponding dot file. 
	 * 
	 * @param args: 0: option = v or validate
	 * 				1: chr file path
	 * @throws Exception 
	 */
	private static void validateChromosome(String[] args) throws Exception {
		
		if(args.length != 2) {
			throw new MissingGrimmParameterException("validation requires a chromosome file");
		}else {
			System.out.println("VALIDATION of CHROMOSOME");
			System.out.println("");
			
			ChromosomeReader chrReader = new ChromosomeReader(args[1]);
			chrReader.validateCHR();
		}
	}

	/**
	 * Creates a parameters file for grimm.
	 * 
	 * @param args
	 * @throws MissingGrimmParameterException
	 */
	private static void createParameterFile(String[] args) throws MissingGrimmParameterException {
		
		if(args.length != 2) {
			throw new MissingGrimmParameterException("creation of parameters file requires a filePath");
		}else {
			System.out.println("PARAMETERS files");
			System.out.println("");
			System.out.print("Creation of Parameters file: ["+args[1]+"] ... ");
			ParametersFile params= new ParametersFile(args[1]);
			params.createNewFile();
			System.out.println("DONE");
		}		
	}
		
	/**
	 * Creates a configuration File for a given meta-model and its root class
	 * 
	 * @param args
	 * @throws MissingGrimmParameterException
	 * @throws MetaModelNotFoundException 
	 * @throws IOException 
	 */
	private static void createConfigFile(String[] args) throws MissingGrimmParameterException, MetaModelNotFoundException, IOException {
		
		if(args.length != 4) {
			throw new MissingGrimmParameterException("creation of configuration file requires: (1) file path, (2) meta-model and (3) root class");
		}else{
			File metamodelFile= new File(args[2]);
			if(metamodelFile.exists()) {
				ConfigFileGenerator cfg= new ConfigFileGenerator(args[1], args[2], args[3]);
				System.out.println("CONFIGURATION file");
				System.out.println("");
				System.out.print("creation of Configuration file: ["+cfg.getFilePath()+"] ... ");
				cfg.createConfigFile();
				System.out.println("DONE");
			}else {
				throw new MetaModelNotFoundException(args[2]);
			}
		}
	}
	
	/**
	 * Generates models using grimm for a given parameters file
	 * 
	 * @param args
	 * @param chr: a boolean that says if the chromosome should be generated or not
	 * @throws Exception 
	 */
	private static void generateModels(String[] args, boolean chr) throws Exception {
		
		if(args.length != 2) {
			throw new MissingGrimmParameterException("generation of models requires a parameters file");
		}else {
			//read the params file
			System.out.println("GENERATION of MODELS");
			System.out.println("");
			
			ParametersFile params= new ParametersFile(args[1]);
			params.readParamFile();
			System.out.println(params.toString());
			
			System.out.println("");
			System.out.println("Start generation...");
			
			//Start generation
			ModelBuilder modelbuilder;
			
			//////////////////////////////////////////////////////
			//
			//input mode: quick (lb,ub,rb)
			//
			//////////////////////////////////////////////////////
			if(params.getInputMode().equals("quick")) {
				if(params.getOutputFormat().equals("xmi")) {
					
					modelbuilder= new CSP2XMI(params.getMetamodel(), params.getRootClass(),
							params.getRootClass()+"/"+params.getRootClass()+".xml", 
							params.getOclFile());
					
					modelbuilder.generateModel(params.getClassLowerBound(), params.getClassUpperBound(), 
							params.getReferenceUpperBound(), 1, params.getNumberOfSolutions());
					
				}else if (params.getOutputFormat().equals("dot")) {
					
					modelbuilder= new CSP2dot(params.getMetamodel(), params.getRootClass(),
							params.getRootClass()+"/"+params.getRootClass()+".xml", 
							params.getOclFile());
					
					modelbuilder.generateModel(params.getClassLowerBound(), params.getClassUpperBound(), 
							params.getReferenceUpperBound(), 1, params.getNumberOfSolutions());
				}
			
			//////////////////////////////////////////////////////
			//
			//input mode: config (detailed)
			//
			//////////////////////////////////////////////////////
			}else if (params.getInputMode().equals("config")) {
				if(params.getOutputFormat().equals("xmi")) {
					
					modelbuilder= new CSP2XMI(params.getMetamodel(), params.getRootClass(),
							params.getRootClass()+"/"+params.getRootClass()+".xml", 
							params.getOclFile());
					
					modelbuilder.generateModel(params.getConfFile(), 1, params.getNumberOfSolutions(),chr);
					
				}else if (params.getOutputFormat().equals("dot")) {
					
					modelbuilder= new CSP2dot(params.getMetamodel(), params.getRootClass(),
							params.getRootClass()+"/"+params.getRootClass()+".xml", 
							params.getOclFile());
					
					modelbuilder.generateModel(params.getConfFile(), 1, params.getNumberOfSolutions(),chr);
				}
			}
		}	
	}
	
	private static void isSolverExisting(String filePath) throws CSPSolverNotFoundException {
		File abscon= new File(filePath);
		if(!abscon.exists()) {
			throw new CSPSolverNotFoundException(filePath);
		}	
	}
	
	private static void incorrectParameter(String[] args) throws UnknownParameterException {
		UnknownParameterException e= new UnknownParameterException(args[0]);
		throw e;
	}

}
