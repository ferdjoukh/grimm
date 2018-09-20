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

public class ConfigFileGenerator {

	
	private String mm;
	private String rootClass;
	private MetaModelReader modelReader;
	
	
	public ConfigFileGenerator(String mm, String rootClass)
	{
		this.mm = mm;
		this.rootClass = rootClass;
		modelReader = new MetaModelReader(mm, rootClass, 2, 2);
	}
	
	public void generate() throws IOException
	{
		String filePath= rootClass+".grimm";
		new File(rootClass).mkdir();
	
		PrintWriter ecrivain =  new PrintWriter(new BufferedWriter(new FileWriter(rootClass+"/"+filePath)));
		
		ecrivain.write("%This is a configuration file for Grimm Tool \n");
		ecrivain.write("%Please do not change the ordering or the name of any element !\n");
		ecrivain.write("%Put a numerical value instead of 0, lower, upper, a and z \n");
		
		ecrivain.write("% \n");
		ecrivain.write("% \n");
		ecrivain.write("%-------------------------------------------------------------\n");
		ecrivain.write("%Number of instances for Classes \n");
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
		
		System.out.println(" OK");
		System.out.println("\tConfiguration file: \""+rootClass+"/"+ filePath + "\" was generated !");
		
	}
}
