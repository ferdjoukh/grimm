package Ecore2CSP;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import org.eclipse.emf.ecore.EClass;
import org.jdom2.Attribute;
import org.jdom2.Element;

import Ecore.MetaModelReader;

public class CSPconstraint {
	
	/**
	 * Create predicate for equality A=B
	 */
	public static Element equalityPredicate()
	{
		Element pre1= new Element("predicate");
		
		Element par1= new Element("parameters");
		par1.setText("int A int B");
		
		Element exp= new Element("expression");
		Element fct= new Element("functional");
		fct.setText("eq(A,B)");
		exp.addContent(fct);
		
		pre1.addContent(par1);
		pre1.addContent(exp);
		Attribute refe1= new Attribute("name", "equal");
		pre1.setAttribute(refe1);
		return pre1;
	}
	
	/**
	 * Create symmetries ordering constraints
	 * 
	 * @param variable1
	 * @param variable2
	 */
	public static Element equalityVarValConstraint(String variable, int value){
		Element cons = new Element("constraint");
		Element param= new Element("parameters");
		
		param.setText(variable+ " "+ value);
		cons.addContent(param);
		
		Attribute name= new Attribute("name", "value_of_"+variable);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", "2");
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", variable);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "equal");
		cons.setAttribute(refe);
		
		return cons;
	}

	
	/**
	 * Create predicate for symmetries constraints
	 */
	public static Element createOrderingPredicate()
	{
		Element pre1= new Element("predicate");
		
		Element par1= new Element("parameters");
		par1.setText("int v1 int v2");
		

		Element exp= new Element("expression");
		Element fct= new Element("functional");
		fct.setText("lt(v1,v2)");
		exp.addContent(fct);
		
		pre1.addContent(par1);
		pre1.addContent(exp);
		Attribute refe1= new Attribute("name", "inf");
		pre1.setAttribute(refe1);
		return pre1;
	}
	
	/**
	 * Create symmetries ordering constraints
	 * 
	 * @param variable1
	 * @param variable2
	 */
	public static Element createOrderingConstraint(String variable1, String variable2){
		Element cons = new Element("constraint");
		Element param= new Element("parameters");
		
		param.setText(variable2+" "+variable1);
		cons.addContent(param);
		
		Attribute name= new Attribute("name", "Cons"+variable1+variable2);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", "2");
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", variable1+" "+variable2);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "inf");
		cons.setAttribute(refe);
		
		return cons;
	}

	
	/**
	 * This method creates a GCC constraint. It is mainly used to process EOpossite Reference.
	 * Having diverse upperBound for this GCC helps to get a better diversity while instanciating 
	 * the variables that links between class instances.
	 * 
	 * UpperBound are then randomly generated
	 * 
	 * @param gccName
	 * @param variableArity
	 * @param valuesArity
	 * @param variables
	 * @param domain
	 * @param lower
	 * @param upper
	 */
	public static Element createGcc(String gccName,int variableArity, int valuesArity, String variables, String domain, int lower, int upper)
	{
		Element cons=new Element("constraint");
		
		String gccLowerBounds=" ",gccUpperBounds=" ";
		Random random= new SecureRandom();
		
		//Create random uniform upper bound
		for(int i=1;i<=valuesArity;i++)
		{
			gccLowerBounds = gccLowerBounds + lower+" ";
			int nextUpper = random.nextInt(upper-lower+1) + lower;
			gccUpperBounds = gccUpperBounds + nextUpper +" ";
		}
		
		//Set GCC body
		String body="[ "+ variables +"] [ "+ domain +"] ["+gccLowerBounds+"] ["+gccUpperBounds+"]";
		Element param= new Element("parameters");
		param.setText(body);
		cons.addContent(param);
		
		//Set GCC attributes
		Attribute name= new Attribute("name", gccName);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+variableArity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+variables);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:globalCardinality");
		cons.setAttribute(refe);
		
		//Add GCC to all constraints
		return cons;
	}
	
	public static Element GenClassConstraint(MetaModelReader reader)
	{
		ArrayList<EClass> listOfClasses= (ArrayList<EClass>) reader.getClasses();
		
		Element cons=new Element("constraint");
		
		int arity=reader.domaineSum(listOfClasses.size());		
		String vars= "";
		String vals= "",lb="",ub="";
		
		//Valeurs et bornes inf et sup
		int ddddd= reader.domaineSum(listOfClasses.size()) - reader.domaineSumMin(listOfClasses.size());
		vals= vals + "0 ";
		lb= lb+ "0 ";
		ub= ub+ ddddd+" ";
		for(int i=1; i<=reader.domaineSum(listOfClasses.size()); i++){
			vals= vals + i+" ";
			lb=lb+ "0 ";
			ub=ub+"1 ";
		}
		
		String body = "[ "+ vars +"] ["+ vals +"] ["+lb+"] ["+ub+"]";
		Element param= new Element("parameters");
		param.setText(body);
		cons.addContent(param);
		
		
		Attribute name= new Attribute("name", "GccClasses");
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+arity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:globalCardinality");
		cons.setAttribute(refe);
		
		return cons;
	}
	
	public static Element GenNoAllocConstraintPred(){
		Element pre1= new Element("predicate");
		Element par1= new Element("parameters");
		par1.setText("int A int B");
		Element exp= new Element("expression");
		Element fct= new Element("functional");
		fct.setText("or(neg(eq(A,0)),eq(B,0))");
		exp.addContent(fct);
		pre1.addContent(par1);
		pre1.addContent(exp);
		Attribute refe1= new Attribute("name", "implies");
		pre1.setAttribute(refe1);
		return pre1;
	}
	
	public static Element GenNoAllocConstraint(String i,String var){
		Element cons = new Element("constraint");
		Element param= new Element("parameters");
		param.setText(var);
		cons.addContent(param);
		Attribute name= new Attribute("name", "CNA"+i);
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", "2");
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", var);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "implies");
		cons.setAttribute(refe);
		return cons;	
	}
	
	public static Element Gccroot(int arity, int valsarity, String vars, String vals){
		Element cons=new Element("constraint");
		
		//Les parametres
		String lb="0 ",ub=arity+" ";
		vals= "0"+ vals;
		
		//Valeurs et bornes inf et sup
		for(int i=1;i<=valsarity;i++){
			lb=lb + "0 ";
			ub=ub +"1 ";
		}
		
		String pText="[ "+ vars +"] ["+ vals +"] ["+lb+"] ["+ub+"]";
		Element param= new Element("parameters");
		param.setText(pText);
		cons.addContent(param);
		
		Attribute name= new Attribute("name", "GccRoot");
		cons.setAttribute(name);
		Attribute Arity= new Attribute("arity", ""+valsarity);
		cons.setAttribute(Arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		cons.setAttribute(Scope);
		Attribute refe= new Attribute("reference", "global:globalCardinality");
		cons.setAttribute(refe);
		return cons;
	}
	
	public static Element GenAllDiffRoot(int arity,String vars)
	{
		Element cons=new Element("constraint");
		Attribute name= new Attribute("name", "AllDiffRoot");
		Attribute Arity= new Attribute("arity", ""+arity);
		Attribute Scope= new Attribute("scope", ""+vars);
		Attribute refe= new Attribute("reference", "global:allDifferent");
		cons.setAttribute(name);
		cons.setAttribute(Arity);
		cons.setAttribute(Scope);
		cons.setAttribute(refe);
	    
		Element param= new Element("parameters");
		String body="["+ vars +"]";
		param.setText(body);
		cons.addContent(param);
		
		return cons;
	}
}
