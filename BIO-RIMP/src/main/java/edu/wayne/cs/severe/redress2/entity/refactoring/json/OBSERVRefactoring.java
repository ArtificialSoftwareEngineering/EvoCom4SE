package edu.wayne.cs.severe.redress2.entity.refactoring.json;

import java.util.ArrayList;
import java.util.List;

public class OBSERVRefactoring {

	private String type;
	private List<OBSERVRefParam> params;
	private List<OBSERVRefactoring> subRefs;
	private boolean feasible;
	private ArrayList<Double> penalty;
	
	
	public OBSERVRefactoring(String type,
							 List<OBSERVRefParam> params,
							 boolean feasible,
							 ArrayList<Double> penalty
	) {
		this.type = type;
		this.params = params;
		//this.subRefs = new ArrayList<OBSERVRefactoring> ();
		this.subRefs = null;
		this.feasible = feasible;
		this.penalty = penalty;
	}

	public OBSERVRefactoring(String type, List<OBSERVRefParam> params,
							 List<OBSERVRefactoring> subRefs,
							 boolean feasible, ArrayList<Double> penalty) {
		this.type = type;
		this.params = params;
		this.subRefs = subRefs;
		this.feasible = feasible;
		this.penalty = penalty;
	}

	public String getType() {
		return type;
	}

	public List<OBSERVRefParam> getParams() {
		return params;
	}

	/**
	 * @return the subRefs
	 */
	public List<OBSERVRefactoring> getSubRefs() {
		return subRefs;
	}
	
	public void setType(String type){
		this.type=type;
	}
	
	public void setParams(List<OBSERVRefParam> params){
		this.params=params;
	}
	
	public void setSubRefs(List<OBSERVRefactoring> subRefs){
		this.subRefs=subRefs;
	}
	
	
	public boolean isFeasible() {
		return feasible;
	}

	public ArrayList<Double> getPenalty() {
		return penalty;
	}

	public void setPenalty(ArrayList<Double> penalty) {
		this.penalty = penalty;
	}

	@Override
	public String toString() {
		return "OBSERVRefactoring [type=" + type + ", params=" + params
				+ ", subRefs=" + subRefs + ", feasible=" + feasible +"]";
	}

}
