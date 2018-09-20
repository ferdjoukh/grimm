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

/**
* @author: Adel Ferdjoukh
* @email: ferdjoukh@gmail.com
*
*/

public class ParametersFile {
	private String filePath;
	
	public ParametersFile(String filePath) {
		this.filePath= filePath;
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
			pw.write("# + are mondatory\n");
			pw.write("# Choose (1),(2) or (3) and remove the other lines\n");
			pw.write("#\n");
			
			pw.write("+meta-model =tests/test.ecore\n");
			pw.write("+rootClass =map\n");
			pw.write("ocl file =tests/maps.ocl\n");
			
			pw.write("#(1)\n");
			pw.write("quick mode\n");
			
			pw.write("#(2)\n");
			pw.write("lowerBound for classes =2\n");
			pw.write("upperBound for classes =4\n");
			pw.write("upperBound for references =2\n");
			
			pw.write("#(3)\n");
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
	
	public void readParamFile() {
		
		try {
			InputStream in= new FileInputStream(new File(filePath));
			InputStreamReader isr= new InputStreamReader(in);
			BufferedReader br= new BufferedReader(isr);
			
			String line;
			while( (line=br.readLine())!=null  ) {
				System.out.println(line);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
