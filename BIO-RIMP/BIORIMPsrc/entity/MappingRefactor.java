package entity;

import java.util.Hashtable;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import unalcol.types.collection.bitarray.BitArray;

public abstract class MappingRefactor {
	
	private OBSERVRefactoring refactor;
	protected Refactoring type;
	
	public abstract OBSERVRefactoring mappingRefactor(
			QubitRefactor genome,
			MetaphorCode code);
	
	public abstract List<OBSERVRefParam> mappingParams();

	public OBSERVRefactoring getRefactor() {
		return refactor;
	}

	public void setRefactor(OBSERVRefactoring refactor) {
		this.refactor = refactor;
	}
	
	enum Refactoring{
		pullUpField, moveMethod, replaceMethodObject, replaceDelegationInheritance,
		moveField, extractMethod, pushDownMethod, replaceInheritanceDelegation, 
		inlineMethod, pullUpMethod, pushDownField, extractClass
	}


}
