package space;

import java.util.ArrayList;
import java.util.List;

import controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactorings;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import entity.MetaphorCode;
import unalcol.clone.Clone;
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
		boolean feasible = false;
		String mapRefactor;
		GeneratingRefactor specificRefactor = null;

		for(RefactoringOperation refOp : x){
			mapRefactor = refOp.getRefType().getAcronym();	
			switch(mapRefactor){
			case "PUF":
				specificRefactor = new GeneratingRefactorPUF();
				break;
			case "MM":
				specificRefactor = new GeneratingRefactorMM();
				break;
			case "RMMO":
				specificRefactor = new GeneratingRefactorRMMO();
				break;
			case "RDI":
				specificRefactor = new GeneratingRefactorRDI();
				break;
			case "MF":
				specificRefactor = new GeneratingRefactorMF();
				break;
			case "EM":
				specificRefactor = new GeneratingRefactorEM();
				break;
			case "PDM":
				specificRefactor = new GeneratingRefactorPDM();
				break;
			case "RID":
				specificRefactor = new GeneratingRefactorRID();
				break;
			case "IM":
				specificRefactor = new GeneratingRefactorIM();
				break;
			case "PUM":
				specificRefactor = new GeneratingRefactorPUM();
				break;
			case "PDF":
				specificRefactor = new GeneratingRefactorPDF();
				break;
			case "EC":
				specificRefactor = new GeneratingRefactorEC();
				break;
			}//END CASE

			feasible = specificRefactor.feasibleRefactor( refOp, metaphor );
			if( !feasible ){
				System.out.println( "Wrong Feasible Refactor (IN FEASIBLE): " + refOp.toString() );
				break;
			}
		}
		return x.size() <= n && feasible;
	}

	@Override
	public double feasibility(List<RefactoringOperation> x) {
		return feasible(x)?1:0;
	}

	@Override
	public List<RefactoringOperation> repair(List<RefactoringOperation> x) {
		OBSERVRefactorings oper = new OBSERVRefactorings();
		List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();
		String mapRefactor;
		GeneratingRefactor specificRefactor = null;
		boolean feasible = false;

		List<RefactoringOperation> clon;
		List<RefactoringOperation> repaired = new ArrayList<RefactoringOperation>();

		if(x.size() > n){
			clon = new ArrayList<RefactoringOperation>();
			for(int i = 0; i < n; i++){
				clon.add( x.get(i) );
				repaired.add( x.get(i) );
			}
		}else{
			clon = (List<RefactoringOperation>) Clone.create( x );
		}


		for( RefactoringOperation refOp : clon ){
			mapRefactor = refOp.getRefType().getAcronym();	

			switch(mapRefactor){
			case "PUF":
				specificRefactor = new GeneratingRefactorPUF();
				break;
			case "MM":
				specificRefactor = new GeneratingRefactorMM();
				break;
			case "RMMO":
				specificRefactor = new GeneratingRefactorRMMO();
				break;
			case "RDI":
				specificRefactor = new GeneratingRefactorRDI();
				break;
			case "MF":
				specificRefactor = new GeneratingRefactorMF();
				break;
			case "EM":
				specificRefactor = new GeneratingRefactorEM();
				break;
			case "PDM":
				specificRefactor = new GeneratingRefactorPDM();
				break;
			case "RID":
				specificRefactor = new GeneratingRefactorRID();
				break;
			case "IM":
				specificRefactor = new GeneratingRefactorIM();
				break;
			case "PUM":
				specificRefactor = new GeneratingRefactorPUM();
				break;
			case "PDF":
				specificRefactor = new GeneratingRefactorPDF();
				break;
			case "EC":
				specificRefactor = new GeneratingRefactorEC();
				break;
			}//END CASE

			feasible = specificRefactor.feasibleRefactor( refOp, metaphor );

			if( !feasible ){
				refactorings.add( specificRefactor.repairRefactor( refOp, metaphor ) );

			}else{
				repaired.add( refOp );
			}
		}

		oper.setRefactorings(refactorings);
		RefactoringReaderBIoRIMP reader = new RefactoringReaderBIoRIMP(
				metaphor.getSysTypeDcls(),
				metaphor.getLang(),
				metaphor.getBuilder());

		try {
			repaired.addAll( reader.getRefactOperations( oper ) );
		} catch (ReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println( "Reading Error in Repair" );
			return null;
		}
		return repaired ;
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
