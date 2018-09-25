package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;

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
		this.metamodel= metamodel;
		this.rootClass= rootClass;
		this.modelReader = new MetaModelReader(metamodel, rootClass, 2, 2);
		this.filePath= rootClass+"/"+filePath;
	}
	
	public void createConfigFile() throws MetaModelNotFoundException {
		
		new File(rootClass).mkdir();
	
		PrintWriter ecrivain;
		try {
			ecrivain = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
			
			ecrivain.write("%This is a configuration file for Grimm Tool \n");
			ecrivain.write("%Please do not change the ordering or the name of any element !\n");
			ecrivain.write("%Put a numerical value instead of 0, lower, upper, a and z \n");
			
			ecrivain.write("% \n");
			ecrivain.write("% \n");
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("% Number of instances for Classes \n");
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("%-------------------------------------------------------------\n");
					
			ArrayList<EClass> cls= new ArrayList<EClass>();
			cls= (ArrayList<EClass>) modelReader.getClasses();
			for(EClass c: cls)
			{
				String name= c.getName();
				if (name.compareTo(rootClass)!=0)
				ecrivain.write(name+"=0\n");
			}
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("%Domains of the features \n");
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("%-------------------------------------------------------------\n");
			ArrayList<EAttribute> attributes= new ArrayList<EAttribute>();
			for(EClass c: cls)
			{
				attributes= (ArrayList<EAttribute>) modelReader.getAllAttributesFromClass(c);
				for (EAttribute a: attributes)
				{
					String name= a.getName();
					ecrivain.write(c.getName()+"/"+name+"=lower..upper or a b c ... z\n");
				}
						
			}
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("%Some others \n");
			ecrivain.write("%-------------------------------------------------------------\n");
			ecrivain.write("%-------------------------------------------------------------\n");
			
			ecrivain.write("RefsBound=0\n");
			ecrivain.write("FeaturesBound=0\n");
					
			ecrivain.write("%-------------------------------------------------------------\n");
			
			
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
