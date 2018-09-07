package Utils;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class Utils {

	static EObject searchIns(ArrayList<ClassInstance> a,int oid)
	{
		for(ClassInstance c: a)
		{
			if(c.Id==oid)
				return c.obj;
		}
		return null;
	}
	
	static void replace(ArrayList<ClassInstance> a,int oid, EObject o)
	{

		for(ClassInstance c: a)
		{
			if(c.Id==oid)
				c.obj=o;
		}
	}
	
	static ArrayList<EObject> returnAll(ArrayList<ClassInstance> a)
	{
		ArrayList<EObject> cc=new ArrayList<EObject>();
		for(ClassInstance c: a)
		{
			cc.add(c.obj);
		}
		return cc;
	}
}
