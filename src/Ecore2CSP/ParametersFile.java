package Ecore2CSP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import exceptions.MetaModelNotFoundException;
import exceptions.OCLFileNotFoundException;

/**
* @author: Adel Ferdjoukh
* @email: ferdjoukh@gmail.com
*
*/

public class ParametersFile {
	private String filePath;
	private String metamodel;
	private String rootClass;
	private String oclFile;
	
	public ParametersFile(String filePath) {
		this.filePath= filePath;
	}
		
	public String getMetamodel() {
		return metamodel;
	}
	
	public void setMetamodel(String mm) {
		try {
			if(metamodelExists(mm)) {
				this.metamodel=mm;
			}
		} catch (MetaModelNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	public String getRootClass() {
		return rootClass;
	}

	public void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}

	public String getOclFile() {
		return oclFile;
	}

	public void setOclFile(String oclFile) {
		try {
			if(oclFileExists(oclFile)) {
				this.oclFile=oclFile;
			}
		} catch (OCLFileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * This method creates an empty parameters file to fill
	 *  
	 */
	public void createNewFile(){
		File file= new File(filePath);
		
		try {
			file.createNewFile();
			
			PrintWriter pw= new PrintWriter(file);
			pw.write("# This file contains all the generation parameters of GRIMM tool\n");
			pw.write("#\n");
			pw.write("# Fill the file with your own information\n");
			pw.write("#   + are mondatory\n");
			pw.write("#   - must be filled or removed\n");
			pw.write("#   (1) and (2) block must not appear at the same time\n");
			pw.write("#\n");
			
			pw.write("+meta-model =tests/test.ecore\n");
			pw.write("+rootClass =map\n");
			pw.write("-ocl file =tests/maps.ocl\n");
			
			pw.write("#(1)\n");
			pw.write("lowerBound for classes =2\n");
			pw.write("upperBound for classes =4\n");
			pw.write("upperBound for references =2\n");
			
			pw.write("#(2)\n");
			pw.write("configuration file =tests/test.grimm\n");
			
			pw.write("#\n");
			pw.write("#\n");
			pw.write("number of solutions =1\n");
			pw.write("#\n");
			pw.write("output format =xmi or dot\n");
			pw.write("CSP solver =abscon\n");
			
			pw.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method reads an existing parameters file
	 */
	public void readParamFile(){
		
		try {
			InputStream in= new FileInputStream(new File(filePath));
			InputStreamReader isr= new InputStreamReader(in);
			BufferedReader br= new BufferedReader(isr);
			
			String line;
			while( (line=br.readLine())!=null  ) {
				if(!line.startsWith("#")) {
					
					//Meta-model
					if(line.startsWith("+meta-model")) {
						String mm= line.substring(line.lastIndexOf("=")+1);
						setMetamodel(mm);
					}
					
					//RootClass
					if(line.startsWith("+rootClass")) {
						String root= line.substring(line.lastIndexOf("=")+1);
						setRootClass(root);
					}
					
					//OCL file
					if(line.startsWith("-ocl ")) {
						String ocl= line.substring(line.lastIndexOf("=")+1);
						setOclFile(ocl);
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean metamodelExists(String metamodel) throws MetaModelNotFoundException {
		File mm= new File(metamodel);
		
		if(mm.exists()) {
			return true;
		}else {
			throw new MetaModelNotFoundException(metamodel);
		}
	}
	
	public boolean oclFileExists(String oclFile) throws OCLFileNotFoundException{
		File ocl= new File(oclFile);
		
		if(ocl.exists()) {
			return true;
		}else {
			throw new OCLFileNotFoundException(oclFile);
		}
		
	}
}
