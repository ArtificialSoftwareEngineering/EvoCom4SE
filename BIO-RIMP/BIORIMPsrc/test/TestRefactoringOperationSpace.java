/**
 * 
 */
package test;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
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
		RefactoringOperationSpace refactorSpace = new RefactoringOperationSpace( 5, metaphor );
		
		//Visualizing the get() Space
		for( RefactoringOperation refOper : refactorSpace.get() ){
			System.out.println( "Random Refactor: "+ refOper.toString() );
		}

	}

}
