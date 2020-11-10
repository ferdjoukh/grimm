package org.grimm.Ecore2CSP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

public class ConfigFileReader {

	
	private String configFilePath;
	private int featureBound;
	private int referencesUB;
	private ArrayList<String> content;
	private ArrayList<String> classInstancesRaw;
	private Hashtable<String,Integer> classInstances;
	private ArrayList<String> attributesDomainsRaw;
	private Hashtable<String,ArrayList<String>> attributesDomains; 
	
	public ConfigFileReader(String configFilePath) {
		
		this.configFilePath = configFilePath;
		this.featureBound = 0;
		this.referencesUB = 0;
		this.content = new ArrayList<String>();
		this.classInstancesRaw = new ArrayList<String>();
		this.classInstances = new Hashtable<String, Integer>();
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
					this.classInstancesRaw.add(line);
				}
				
				//@ToDo: remove this later
				this.content.add(line);
			}
		}
		reader.close();
		
		setClassInstances();
		setAttributesDomains();
		
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
		
		for(String line: classInstancesRaw) {
			if(line.startsWith(start)) {
				return line;
			}
		}
		return null;
	}
	
	private void setClassInstances(){
		
		for(String classinstance: classInstancesRaw) {
			String className = classinstance.substring(0,classinstance.indexOf("="));
			String valueS = classinstance.substring(classinstance.indexOf("=")+1);
			int value;
			try {
				value = Integer.parseInt(valueS);
				if(value < 0) {
					value = 0;
					System.out.println("\t[WARNING] number of instances <"+valueS+"> for class "+ className + " treated as 0");
				}
			}catch(NumberFormatException e) {
				value = 0; 
				System.out.println("\t[WARNING] number of instances <"+valueS+"> for class "+ className + " treated as 0");
			}
			
			classInstances.put(className, value);
		}
	}
	
	/**
	 * Key = ClassName/AttributeName
	 * 
	 * Element 0 of the ArrayList<String> = l if the domain is a list of values
	 * 									  = i if the domain is a min..max interval
	 * 
	 * Examples:
	 * 
	 * ArrayList<String> = [i,23,100]
	 * ArrayList<String> = [l,a,b,c,d,e]
	 * ArrayList<String> = [n] (if used for identification name-like)
	 * 	
	 */
	private void setAttributesDomains(){
		
		String attrName;
		String domainS;
		
		for (String attributedomain : attributesDomainsRaw){
			
			attrName = attributedomain.substring(0,attributedomain.lastIndexOf('='));
			domainS = attributedomain.substring(attributedomain.indexOf("=")+1);
			ArrayList<String> value = new ArrayList<String>();
			
			//if default value is set: random, name or 1..100 is found, the attribute is ignored
			if(!domainS.equals("random") && !domainS.equals("name") && !domainS.equals("1..100") ) {
			
				if(domainS.contains("..")) {
					value.add("i");
					String [] splits = domainS.split("\\.\\.");
					for(String str: splits) {
						value.add(str);
					}
				}else {
					value.add("l");
					String [] splits = domainS.split(" ");
					for(String str: splits) {
						value.add(str);
					}
				}
				
				attributesDomains.put(attrName, value);
				
			}else if (domainS.equals("name")) {
				value.add("n");
				
				attributesDomains.put(attrName, value);
			}
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
	
	public ArrayList<String> getAttributesDomainsRaw() {
		return attributesDomainsRaw;
	}

	public Hashtable<String, ArrayList<String>> getAttributesDomains() {
		return attributesDomains;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public int getReferencesUB() {
		return referencesUB;
	}

	public ArrayList<String> getClassInstancesRaw() {
		return classInstancesRaw;
	}

	public Hashtable<String, Integer> getClassInstances() {
		return classInstances;
	}
}
