package edu.wayne.cs.severe.redress2.entity.refactoring;

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

	public RefactoringOperation(RefactoringType refType,
			HashMap<String, List<RefactoringParameter>> params, String refId,
			List<RefactoringOperation> subRefs) {
		this.refType = refType;
		this.params = params;
		this.refId = refId;
		this.subRefs = subRefs;
	}
	
	//danaderp 1001 Constructor for supporting feasible individuals
	
	public RefactoringOperation(RefactoringType refType,
			HashMap<String, List<RefactoringParameter>> params, String refId,
			List<RefactoringOperation> subRefs, boolean feasible) {
		this.refType = refType;
		this.params = params;
		this.refId = refId;
		this.subRefs = subRefs;
		this.feasible = feasible;
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
		return refType.getAcronym()
				+ (params != null ? (params.toString()) : "")
				+ (subRefs != null ? ("{" + subRefs + "}") : "")
				+ "feasible : " + feasible;
	}




	
	//danaderp vers 1000
	

}// end RefactoringOperation