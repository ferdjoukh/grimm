package org.grimm.utils;
import org.eclipse.emf.ecore.EObject;


public class ClassInstance {

	int Id;
	EObject obj;
	
	public ClassInstance(int Id, EObject o){
		this.Id=Id;
		this.obj=o;
	}

	public int getId() {
		return Id;
	}

	public EObject getObj() {
		return obj;
	}	
}