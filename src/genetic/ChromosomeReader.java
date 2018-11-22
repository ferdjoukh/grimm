package genetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import exceptions.ConfigurationFileNotFoundException;

public class ChromosomeReader {

	private String chrFile;
	private String valuesLine;
	private Integer [] values;
	private String xcspFile;
	private String grimmpFile;
	private String metamodel;
	private String rootClass;
	
	
	public ChromosomeReader(String chrFile) throws Exception {
		setChrFile(chrFile);
		readChromosome();
	}
	
	public void readChromosome() throws Exception {
		
		File file= new File(chrFile);
		
		InputStream in= new FileInputStream(file);
		InputStreamReader isr= new InputStreamReader(in);
		BufferedReader br= new BufferedReader(isr);
		
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		while( (line=br.readLine())!=null  ) {
			lines.add(line);
		}
		
		this.valuesLine= lines.get(0);
		this.values = line2values();
		this.xcspFile= lines.get(1);
		this.grimmpFile= lines.get(2);
		this.metamodel= lines.get(3);
		this.rootClass= lines.get(4);		
	}
	
	private Integer [] line2values() {
		
		String [] values = valuesLine.split(" "); 
		
		Integer [] vals = new Integer[values.length]; 
	
		int i=0;
		for (String s: values) {
			vals[i] = Integer.parseInt(s);
			i++;
		}
	
		return vals;
	}

	private void setChrFile(String chrFile) throws ConfigurationFileNotFoundException {
		try {
			if (chrFileExists(chrFile)) {
				this.chrFile = chrFile;
			}
		}catch(ConfigurationFileNotFoundException e) {
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	private boolean chrFileExists(String chrFile) throws ConfigurationFileNotFoundException{
		File config= new File(chrFile);
		
		if(config.exists()) {
			return true;
		}else {
			throw new ConfigurationFileNotFoundException(chrFile);
		}
	}

	public String getChrFile() {
		return chrFile;
	}

	public String getValuesLine() {
		return valuesLine;
	}

	public Integer [] getValues() {
		return values;
	}

	public String getXcspFile() {
		return xcspFile;
	}

	public String getGrimmpFile() {
		return grimmpFile;
	}

	public String getMetamodel() {
		return metamodel;
	}

	public String getRootClass() {
		return rootClass;
	}
}
