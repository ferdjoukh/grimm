package Utils;

import java.security.SecureRandom;
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

}
