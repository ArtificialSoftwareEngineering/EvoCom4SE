package space;

import java.util.Hashtable;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MetaphorCode;
import unalcol.types.collection.bitarray.BitArray;

public abstract class GeneratingRefactor {
	
	private OBSERVRefactoring refactor;
	protected Refactoring type;
	
	public abstract OBSERVRefactoring generatingRefactor( MetaphorCode code );
	
	public abstract boolean feasibleRefactor( RefactoringOperation ref, MetaphorCode code );

	public OBSERVRefactoring getRefactor() {
		return refactor;
	}

	public void setRefactor(OBSERVRefactoring refactor) {
		this.refactor = refactor;
	}

}
