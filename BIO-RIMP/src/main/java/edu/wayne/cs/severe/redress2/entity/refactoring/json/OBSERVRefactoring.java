package edu.wayne.cs.severe.redress2.entity.refactoring.json;

import java.util.ArrayList;
import java.util.List;

public class OBSERVRefactoring {

	private String type;
	private List<OBSERVRefParam> params;
	private List<OBSERVRefactoring> subRefs;
	private boolean feasible;
	
	
	
	public OBSERVRefactoring(String type, List<OBSERVRefParam> params, boolean feasible) {
		this.type = type;
		this.params = params;
		//this.subRefs = new ArrayList<OBSERVRefactoring> ();
		this.subRefs = null;
		this.feasible = feasible;
	}

	public OBSERVRefactoring(String type, List<OBSERVRefParam> params, List<OBSERVRefactoring> subRefs, boolean feasible) {
		this.type = type;
		this.params = params;
		this.subRefs = subRefs;
		this.feasible = feasible;
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


	@Override
	public String toString() {
		return "OBSERVRefactoring [type=" + type + ", params=" + params
				+ ", subRefs=" + subRefs + ", feasible=" + feasible +"]";
	}

}
