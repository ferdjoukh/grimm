package Utils;
import org.eclipse.emf.ecore.EObject;


public class ClassInstance {

	int Id;
	EObject obj;
	
	public ClassInstance(int Id, EObject o){
		this.Id=Id;
		this.obj=o;
	}
}
