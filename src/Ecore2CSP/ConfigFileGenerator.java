package Ecore2CSP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import Ecore.MetaModelReader;
import exceptions.MetaModelNotFoundException;

public class ConfigFileGenerator {

	
	private String metamodel;
	private String rootClass;
	private MetaModelReader modelReader;
	private String filePath;
	
	
	public ConfigFileGenerator(String mm, String rootClass)	{
		this.metamodel = mm;
		this.rootClass = rootClass;
		this.modelReader = new MetaModelReader(mm, rootClass, 2, 2);
		filePath= rootClass+"/"+rootClass+".grimm";
	}
	
	public ConfigFileGenerator(String filePath, String metamodel, String rootClass) {
		this.metamodel = metamodel;
		this.rootClass = rootClass;
		this.modelReader = new MetaModelReader(metamodel, rootClass, 2, 2);
		this.filePath = filePath;
	}
	
	public void createConfigFile() throws MetaModelNotFoundException {
		
		new File(rootClass).mkdir();
		PrintWriter ecrivain;
	
		try {
			ecrivain = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
			
			ecrivain.write("% Configuration file for grimm tool \n");
			ecrivain.write("%	Please specify detailed information on your models:\n");
			ecrivain.write("%		(1) precise number of class intances\n"
						 + "%		(2) domain for attributes\n"
					     + "%		(3) reference upper bound\n");
			ecrivain.write("%---------------------------------\n");
			ecrivain.write("% Number of instances for Classes\n");
			ecrivain.write("%---------------------------------\n");
					
			ArrayList<EClass> cls= new ArrayList<EClass>();
			cls= (ArrayList<EClass>) modelReader.getClasses();
			for(EClass c: cls)
			{
				String name= c.getName();
				if (name.compareTo(rootClass)!=0)
				ecrivain.write(name+"=0\n");
			}
			ecrivain.write("%---------------------------------\n");
			ecrivain.write("% Domains of the features\n");
			ecrivain.write("%---------------------------------\n");
			ecrivain.write("%	Strings: choose: random, name or give a list of values (space separated)\n");
			ecrivain.write("%	Integer: choose: 1..100, custom interval or  list of values (space separated)\n");
			ecrivain.write("%---------------------------------\n");
			ArrayList<EAttribute> attributes= new ArrayList<EAttribute>();
			ecrivain.write("% String\n");
			ecrivain.write("%--------\n");
			for(EClass c: cls){
				attributes= (ArrayList<EAttribute>) modelReader.getAllAttributesFromClass(c);
				for (EAttribute a: attributes){
					if(a.getEType().getName().equals("EString")) {
						String name= a.getName();
						if(name.toLowerCase().equals("name")) {
							ecrivain.write(c.getName()+"/"+name+"=name\n");
						}else {
							ecrivain.write(c.getName()+"/"+name+"=random\n");
						}
					}
				}								
			}
			ecrivain.write("%---------------------------------\n");
			ecrivain.write("% Integer\n");
			ecrivain.write("%---------\n");
			for(EClass c: cls){
				attributes= (ArrayList<EAttribute>) modelReader.getAllAttributesFromClass(c);
				for (EAttribute a: attributes){
					if(a.getEType().getName().equals("EInt")) {
						String name= a.getName();
						ecrivain.write(c.getName()+"/"+name+"=1..100\n");
					}
				}								
			}
			ecrivain.write("%---------------------------------\n");
			ecrivain.write("% References upper bound\n");
			ecrivain.write("%---------------------------------\n");
			ecrivain.write("RefsBound=3\n");
			
			ecrivain.close();			
			
		} catch (IOException e) {
			throw new MetaModelNotFoundException(metamodel);
		}		
	}

	public String getMetamodel() {
		return metamodel;
	}

	public String getRootClass() {
		return rootClass;
	}

	public MetaModelReader getModelReader() {
		return modelReader;
	}

	public String getFilePath() {
		return filePath;
	}
	
}
