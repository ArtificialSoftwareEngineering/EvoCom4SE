/**
 * 
 */
package space;

import java.util.ArrayList;
import java.util.List;

import controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactorings;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import unalcol.random.integer.IntUniform;

/**
 * @author Daavid
 *
 */
public class CreateRefOper {


	public static List<RefactoringOperation> get(int n, MetaphorCode metaphor) {

		RefactoringReaderBIoRIMP reader = new RefactoringReaderBIoRIMP(
				metaphor.getSysTypeDcls(),
				metaphor.getLang(),
				metaphor.getBuilder());
		int mapRefactor;
		OBSERVRefactorings oper = new OBSERVRefactorings();
		List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();

		final int DECREASE = 0;
		IntUniform g = new IntUniform ( Refactoring.values().length - DECREASE);
		GeneratingRefactor randomRefactor = null;

		for(int i = 0; i < n; i++){
			mapRefactor = g.generate();
			switch(mapRefactor){
			case 0:
				randomRefactor = new GeneratingRefactorEC();
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
				randomRefactor = new GeneratingRefactorIM();
				break;
			case 7:
				randomRefactor = new GeneratingRefactorRID();
				break;
			case 8:
				randomRefactor = new GeneratingRefactorPDF();
				break;
			case 9:
				randomRefactor = new GeneratingRefactorPUF();
				break;
			case 10:
				randomRefactor = new GeneratingRefactorPDM();
				break;
			case 11:
				randomRefactor = new GeneratingRefactorPUM();
				
				break;
			//TODO: Quitar defaul y descomentar lineas del switch para activar todas
			default:
				randomRefactor = new GeneratingRefactorIM();
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
