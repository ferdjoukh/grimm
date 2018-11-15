package Ecore2CSP;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.omg.CORBA.portable.InputStream;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class ConfigFileReader {

	
	private String configFilePath;
	private int featureBound;
	private int referencesUB;
	private ArrayList<String> content;
	private ArrayList<String> classInstances;
	private ArrayList<String> attributesDomainsRaw;
	private Hashtable<String,ArrayList<String>> attributesDomains; 
	
	public ConfigFileReader(String configFilePath) {
		
		this.configFilePath = configFilePath;
		this.featureBound = 0;
		this.referencesUB = 0;
		this.content = new ArrayList<String>();
		this.classInstances = new ArrayList<String>();
		this.attributesDomainsRaw = new ArrayList<String>();
		this.attributesDomains = new Hashtable<String, ArrayList<String>>();
	}

	public void read(){
		
		try {
		
		File file= new File(configFilePath);
		FileInputStream fis= new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader reader = new BufferedReader(isr);
		String line;
		
		while((line = reader.readLine()) != null)
		{
			if (line.startsWith("RefsBound=")){
				
				this.referencesUB = Integer.parseInt(line.substring(line.lastIndexOf("=")+1));
			}else if (line.startsWith("FeaturesBound=")) {
				
				this.featureBound = Integer.parseInt(line.substring(line.lastIndexOf("=")+1));			
			}else if(!line.startsWith("%") && line.contains("=")){
				
				if(line.contains("/")) {
					this.attributesDomainsRaw.add(line);
				}else {
					this.classInstances.add(line);
				}
				
				//@ToDo: remove this later
				this.content.add(line);
			}
		}
		reader.close();
		}
		catch(IOException e) {
			System.out.println("\t[PROBLEM] ConfigFileReader met a fatal problem :(");
		}
	}

	/**
	 * This method returns the line corresponding to a given class 
	 * 
	 * @param start: class Name
	 * @return
	 */
	public String getClassLineByStartString(String start) {
		
		for(String line: classInstances) {
			if(line.startsWith(start)) {
				return line;
			}
		}
		return null;
	}
	
	private void setFeaturesDomains(){
		
		String cle;
		String valeur;
		
		for (String e: content)
		{
			cle=e.substring(0,e.lastIndexOf('='));
			valeur=e.substring(e.lastIndexOf('=')+1,e.length());
			//fD.put(cle, valeur);
		}	
	}
	
	/////////////////////////////////////////
	///  getters
	////////////////////////////////////////
	public ArrayList<String> getContent() {
		return content;
	}
	
	public int getFeatureBound() {
		return featureBound;
	}

	public int getRefsBound() {
		return referencesUB;
	}

	public ArrayList<String> getClassInstances() {
		return classInstances;
	}

	public ArrayList<String> getAttributesDomainsRaw() {
		return attributesDomainsRaw;
	}

	public Hashtable<String, ArrayList<String>> getAttributesDomains() {
		return attributesDomains;
	}
}
