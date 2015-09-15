package optimization;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import entity.Qubit;
import entity.QubitArray;
import unalcol.search.space.Space;

public class RefactoringOperationSpace extends Space<List<RefactoringOperation>> {
	protected int n;
	
	public RefactoringOperationSpace( int n ){
		this.n = n; 
	}

	@Override
	public boolean feasible(List<RefactoringOperation> x) {
		return x.size()==n;
	}

	@Override
	public double feasibility(List<RefactoringOperation> x) {
		return feasible(x)?1:0;
	}

	@Override
	public List<RefactoringOperation> repair(List<RefactoringOperation> x) {
		
		/*if( x.size() != n ){
			if(x.size()>n){
				x = x.subQubitArray(0,n);
			}else{
				//x = new QubitArray(n, true);
				for( int i=0; i<n;i++)
					x.set(new Qubit(true));
			}
		}*/
		return x;
	}

	@Override
	public List<RefactoringOperation> get() {
		//return new QubitArray(n, true);
		
		return new ArrayList<RefactoringOperation>() ;
	}
}
