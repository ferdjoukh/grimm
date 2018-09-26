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

public class ConfigFileReader {

	
	private int FeatureBound=0;
	private int RefBound=0;
	private String configFilePath;
	private ArrayList<String> content;
	private Hashtable<String,String> featuresDomaines; 
	
	
	public ConfigFileReader(String configFilePath) {
		// TODO Auto-generated constructor stub
		this.configFilePath = configFilePath;
	}

	@SuppressWarnings("deprecation")
	public void read() throws IOException {
		
		ArrayList<String> content= new ArrayList<String>();
		
		File z= new File(configFilePath);
		FileInputStream zz= new FileInputStream(z);
		BufferedInputStream zzz= new BufferedInputStream(zz);
		
		DataInputStream reader = new DataInputStream(zzz);
		
		String line="";
		
		while(reader.available()!=0)
		{
			line= reader.readLine();
			if (line.startsWith("RefsBound="))
			{
				this.RefBound= Integer.parseInt(line.substring(line.lastIndexOf("=")+1));
			}
			else if (line.startsWith("FeaturesBound="))
			{
				this.FeatureBound= Integer.parseInt(line.substring(line.lastIndexOf("=")+1));
			}
			else if(!line.startsWith("%"))
			{
				content.add(line);
				//System.out.println(line);
			}
		}
		
		this.content = content;
		
		reader.close();
		zzz.close();
		zz.close();
		
	}

	public ArrayList<String> getContent() {
		return content;
	}

	public int getFeatureBound() {
		// TODO Auto-generated method stub
		return FeatureBound;
	}

	public int getRefsBound() {
		// TODO Auto-generated method stub
		return RefBound;
	}
	
	public Hashtable<String,String> getfeaturesDomains()
	{
		
		Hashtable<String,String> fD= new Hashtable<String, String>(); 
		ArrayList<String> content= this.content;
		String cle;
		String valeur;
		
		for (String e: content)
		{
			//System.out.println("ligne="+e);
			
			cle=e.substring(0,e.lastIndexOf('='));
			valeur=e.substring(e.lastIndexOf('=')+1,e.length());
			//System.out.println("Cle="+cle+ " Valeur="+valeur);
			fD.put(cle, valeur);
		}
		
		return fD;
		
	}

}
