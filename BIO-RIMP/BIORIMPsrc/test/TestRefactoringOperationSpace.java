/**
 * 
 */
package test;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import java.util.List;
import space.RefactoringOperationSpace;

/**
 * @author dnader
 *
 */
public class TestRefactoringOperationSpace {

	/**
	 * @param args
	 */
	public static void main(String[] argss) {
		//Getting the Metaphor
		String userPath = System.getProperty("user.dir");
		String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\optimization\\src","-s", "     optimization      " };        
		MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM ();
		init.main(args);
		MetaphorCode metaphor = new MetaphorCode(init);
		
		//Creating the Space
		RefactoringOperationSpace refactorSpace = new RefactoringOperationSpace( 1000, metaphor );
		
		//Visualizing the get() Space
		List<RefactoringOperation> refactorSpaceG = refactorSpace.get();
		
		if(refactorSpaceG != null)
		for( RefactoringOperation refOper : refactorSpaceG ){
			System.out.println( "Random Refactor: "+ refOper.toString() );
		}
		
		//Visualizing feasible individual

		System.out.println( "Feasible Refactor: " + "["+ refactorSpace.feasible(refactorSpaceG) +"]" );	

	}

}
