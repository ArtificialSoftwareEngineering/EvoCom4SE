package space;

import java.util.ArrayList;
import java.util.List;

import controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactorings;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import entity.MetaphorCode;
import unalcol.random.integer.IntUniform;
import unalcol.search.space.Space;

public class RefactoringOperationSpace extends Space<List<RefactoringOperation>> {
	protected int n = 1;
	protected MetaphorCode metaphor;
	
	public RefactoringOperationSpace( MetaphorCode metaphor ){
		this.metaphor = metaphor;
	};
	
	public RefactoringOperationSpace( int n, MetaphorCode metaphor ){
		this.n = n; 
		this.metaphor = metaphor;
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
		RefactoringReaderBIoRIMP reader = new RefactoringReaderBIoRIMP(
				metaphor.getSysTypeDcls(),
				metaphor.getLang(),
				metaphor.getBuilder());
		int mapRefactor;
		OBSERVRefactorings oper = new OBSERVRefactorings();
		List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();
		
		IntUniform g = new IntUniform ( Refactoring.values().length );
		GeneratingRefactor randomRefactor = null;
		
		for(int i = 0; i < n; i++){
			mapRefactor = g.generate();
			switch(mapRefactor){
			case 0:
				randomRefactor = new GeneratingRefactorPUF();
				break;
			case 1:
				randomRefactor = new GeneratingRefactorMM();
				break;
			case 2:
				randomRefactor = new GeneratingRefactorRMMO();
				break;
			case 3:
				randomRefactor = new GeneratingRefactorRDI();
				break;
			case 4:
				randomRefactor = new GeneratingRefactorMF();
				break;
			case 5:
				randomRefactor = new GeneratingRefactorEM();
				break;
			case 6:
				randomRefactor = new GeneratingRefactorPDM();
				break;
			case 7:
				randomRefactor = new GeneratingRefactorRID();
				break;
			case 8:
				randomRefactor = new GeneratingRefactorIM();
				break;
			case 9:
				randomRefactor = new GeneratingRefactorPUM();
				break;
			case 10:
				randomRefactor = new GeneratingRefactorPDF();
				break;
			case 11:
				randomRefactor = new GeneratingRefactorEC();
				break;
			}//END CASE
			
			//System.out.println( "Refactor [ " + Refactoring.values()[mapRefactor] + "]");
			refactorings.add( randomRefactor.generatingRefactor( metaphor ) );
			
		}
		
		oper.setRefactorings(refactorings);
		try {
			return reader.getRefactOperations( oper	);
		} catch (ReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println( "Reading Error" );
			return null;
		} 
	}
}
