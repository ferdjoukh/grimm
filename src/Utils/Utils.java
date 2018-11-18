package Utils;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;

public class Utils {
	
	public static ClassInstance searchInstanceByClass(ArrayList<ClassInstance> instances, String className) {
		
		for(ClassInstance instance: instances){
			
			String currentObjectClassName =((DynamicEObjectImpl) instance.getObj()).eClass().getName();
			
			if(currentObjectClassName.equals(className))
				return instance;
		}
		return null;
	}
	
	public static ClassInstance searchInstanceByClass(ArrayList<ClassInstance> instances, EClass eClass) {
		
		ArrayList<ClassInstance> objects = findAllinstancesOfClass(instances, eClass);
		
		if(objects.size() > 0) {
			int n= (int) (Math.random()* (objects.size()));
			//System.out.println("num="+ n);
			return objects.get(n);
		}else {
			return null;
		}
	}
	
	/**
	 * Search for instance that was not used
	 * 
	 * @param instances
	 * @param eClass
	 * @param used
	 * @return
	 */
	public static ClassInstance searchInstanceByClass(ArrayList<ClassInstance> instances, EClass eClass, ArrayList<Integer> used) {
		
		ArrayList<ClassInstance> objects = findAllinstancesOfClass(instances, eClass);
		int i=objects.size();
		
		if(objects.size() > 0) {
			for(int cpt=0; cpt< objects.size();cpt++) {
			
				int n= (int) (Math.random()* (objects.size()));
				
				ClassInstance instance = objects.get(n); 
				
				if(!used.contains(instance.getId())) {
					return objects.get(n);
				}
			}
		}
		return null;
	}

	public static ArrayList<ClassInstance> findAllinstancesOfClass(ArrayList<ClassInstance> instances, EClass eClass){
		ArrayList<ClassInstance> objects= new ArrayList<ClassInstance>();
		
		for(ClassInstance instance: instances){
			
			EClass currentObjectClass = ((DynamicEObjectImpl) instance.getObj()).eClass();
			
			if(currentObjectClass.equals(eClass) || currentObjectClass.getEAllSuperTypes().contains(eClass)) {
				objects.add(instance);
			}
		}
		
		return objects;
	}
	
	public static EObject searchIns(ArrayList<ClassInstance> a,int oid)
	{
		for(ClassInstance c: a)
		{
			if(c.Id==oid)
				return c.obj;
		}
		return null;
	}
	
	public static void replace(ArrayList<ClassInstance> a,int oid, EObject o)
	{

		for(ClassInstance c: a)
		{
			if(c.Id==oid)
				c.obj=o;
		}
	}
	
	public static ArrayList<EObject> returnAll(ArrayList<ClassInstance> a)
	{
		ArrayList<EObject> cc=new ArrayList<EObject>();
		for(ClassInstance c: a)
		{
			cc.add(c.obj);
		}
		return cc;
	}	
}
