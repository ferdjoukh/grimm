package Utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used to instantiate attributes while generating models
 * 
 * @author Adel Ferdjoukh
 *
 */
public class AttributeInstantiator {
	
	public static boolean generateBoolean() {
		Random rand= new SecureRandom();
		return rand.nextBoolean();
	}
	
	public static String generateBasicName(String objectName, int OID) {
		return  objectName+OID;		
	}
	
	public static String chooseString(ArrayList<String> candidates, int OID){
		
		int size = candidates.size()-1;
		int index = (OID % size) + 1;
		return candidates.get(index)+OID;		
	}
	
	public static String randomString() {
		
		String res="";
		Random rand= new SecureRandom();
		int length= rand.nextInt(10-5)+5;
		
		for(int i=0; i<length;i++) {
			int a= rand.nextInt(90-65)+ 65;
			res=res+ (char) a;
		}
		return res;
	}
	
	public static int randomInt(int min, int max) {
		Random rand= new SecureRandom();
		return rand.nextInt(max-min) + min ;
	}
	
	public static int chooseInteger(ArrayList<String> customDomain) {
		
		int index = randomInt(1, customDomain.size());
		return Integer.parseInt(customDomain.get(index));		
	}

}
