package CSP2Model;

import java.util.ArrayList;

public class FoundSolution {

	private ArrayList<String> variables;
	private ArrayList<Integer> values;
	
	/**
	 * Each Object of type FoundSolution is found using the raw solution given by the solver 
	 * 
	 * @param rawSolverSolution: It is the raw solutions given by the solver
	 * 
	 * eg. solution  #8  Id_Compo_1_Bs_1=6 Id_Compo_1_Bs_2=7 Id_Compo_1_Bs_3=8 Id_Compo_1_Bs_4=9
	 * 
	 */
	public FoundSolution(String rawSolverSolution) {
		
		this.variables= new ArrayList<String>();
		this.values= new ArrayList<Integer>();
		
		String [] varvals = rawSolverSolution.split(" ");
		
		for (String val: varvals) {
			
			if(val.contains("=")) {
				String [] variablesValue= val.split("=");
				String variable=variablesValue[0];
				String value=variablesValue[1];
				
				this.variables.add(variable);
				this.values.add(Integer.parseInt(value));
			}
		}
	}

	public ArrayList<String> getVariables() {
		return variables;
	}

	public ArrayList<Integer> getValues() {
		return values;
	}
	
	
}
