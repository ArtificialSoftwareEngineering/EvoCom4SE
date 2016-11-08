package edu.wayne.cs.severe.redress2.entity.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.opers.RefactoringType;

/**
 * @author ojcchar
 * @version 1.0
 * @created 28-Mar-2014 17:27:28
 */
public class RefactoringOperation {

	private HashMap<String, List<RefactoringParameter>> params;
	private RefactoringType refType;
	private String refId;
	private List<RefactoringOperation> subRefs;
	//danaderp 1001 Field for feasible individual
	private boolean feasible;
	private ArrayList<Double> penalty;
	private boolean nonRepair = true;

	public boolean isNonRepair() {
		return nonRepair;
	}

	public void setNonRepair(boolean nonRepair) {
		this.nonRepair = nonRepair;
	}

	public RefactoringOperation(RefactoringType refType,

								HashMap<String, List<RefactoringParameter>> params, String refId,
								List<RefactoringOperation> subRefs) {
		this.refType = refType;
		this.params = params;
		this.refId = refId;
		this.subRefs = subRefs;
	}
	
	//danaderp 1001 Constructor for supporting feasible and penalty individuals
	
	public RefactoringOperation(RefactoringType refType,
			HashMap<String, List<RefactoringParameter>> params, String refId,
			List<RefactoringOperation> subRefs, boolean feasible, ArrayList<Double> penalty) {
		this.refType = refType;
		this.params = params;
		this.refId = refId;
		this.subRefs = subRefs;
		this.feasible = feasible;
		this.penalty = penalty;
	}

	public HashMap<String, List<RefactoringParameter>> getParams() {
        return params;
	}

	public RefactoringType getRefType() {
		return refType;
	}

	/**
	 * @return the subRefs
	 */
	public List<RefactoringOperation> getSubRefs() {
		return subRefs;
	}

	public String getRefId() {
		return refId;
	}
	
	//danaderp 1001 feasible getters 
	public boolean isFeasible() {
		return feasible;
	}


	@Override
	public String toString() {
		//Fixme convert to Json
		String JsonFormatReturn = 
				" \" " + refType.getAcronym() + " \":{ " 
				+ (params != null ? (params.toString()) : "") + " , "
				+ (subRefs != null ? ("{" + subRefs + "}") : "") + " , "
				+ "Penalties : " + penalty.size() + " } "
				+ "NonRepair : " + nonRepair + " } "
				; 
		/*
		return refType.getAcronym()
				+ (params != null ? (params.toString()) : "")
				+ (subRefs != null ? ("{" + subRefs + "}") : "")
				+ "feasible : " + feasible;*/
		return JsonFormatReturn;
	}
	
	//danaderp 1001 SetParams for transposition operator
	public void setParamsTrans(){
		List< RefactoringParameter > aux = new ArrayList< RefactoringParameter >();
		if( params.containsKey("tgt")
				&& !params.get("tgt").get(0).getObjState().equals( CodeObjState.NEW ) ){
			aux.addAll( params.get("tgt") );
			params.get("tgt").clear();
			params.get("tgt").addAll( params.get("src") );
			params.get("src").clear();
			params.get("src").addAll( aux );
		}
		
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(HashMap<String, List<RefactoringParameter>> params) {
		this.params = params;
	}

	/**
	 * @param subRefs the subRefs to set
	 */
	public void setSubRefs(List<RefactoringOperation> subRefs) {
		this.subRefs = subRefs;
	}

	public ArrayList<Double> getPenalty() {
		return penalty;
	}

	public void setPenalty(ArrayList<Double> penalty) {
		this.penalty = penalty;
	}
	//danaderp vers 1000
}// end RefactoringOperation