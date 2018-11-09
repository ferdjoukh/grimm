package Utils;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;

public class Utils {
	
	public static EObject searchInstanceByClass(ArrayList<ClassInstance> instances, String className) {
		
		EObject object=null;
		
		for(ClassInstance instance: instances){
			
			String currentObjectClassName =((DynamicEObjectImpl) instance.getObj()).eClass().getName();
			
			if(currentObjectClassName.equals(className))
				object=instance.getObj();
		}
		return object;
	}
	
	public static EObject searchInstanceByClass(ArrayList<ClassInstance> instances, EClass eClass) {
		
		ArrayList<EObject> objects = findAllinstancesOfClass(instances, eClass);
		
		if(objects.size() > 0) {
			int n= (int) (Math.random()* (objects.size()));
			System.out.println("num="+ n);
			return objects.get(n);
		}else {
			return null;
		}
	}

	public static ArrayList<EObject> findAllinstancesOfClass(ArrayList<ClassInstance> instances, EClass eClass){
		ArrayList<EObject> objects= new ArrayList<EObject>();
		
		for(ClassInstance instance: instances){
			
			EClass currentObjectClass = ((DynamicEObjectImpl) instance.getObj()).eClass();
			
			if(currentObjectClass.getEAllSuperTypes().contains(eClass)) {
				objects.add(instance.getObj());
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
