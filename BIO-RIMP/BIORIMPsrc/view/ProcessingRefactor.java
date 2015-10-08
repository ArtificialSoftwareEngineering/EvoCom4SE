/**
 * 
 */
package view;

import java.util.List;

import org.gicentre.utils.geom.Locatable;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import operators.RefOperMutation;
import optimization.GeneralizedImpactQuality;
import processing.core.PApplet;
import processing.core.PVector;
import space.RefactoringOperationSpace;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.hillclimbing.HillClimbing;
import unalcol.search.Goal;
import unalcol.search.space.Space;
import view.MyProcessingSketch.Dot;

/**
 * @author Daavid
 *
 */
public class ProcessingRefactor extends PApplet {
	
	MetaphorCode metaphor;
	double step = 0.001;    // Size of each step along the path
	
	Space<List<RefactoringOperation>> space;
	OptimizationFunction<List<RefactoringOperation>> function;
	Goal<List<RefactoringOperation>> goal;
	RefOperMutation variation;
	HillClimbing< List<RefactoringOperation> > search ;
	
	public void settings() {

		//First Step: Calculate Actual Metrics
		String userPath = System.getProperty("user.dir");
		String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\optimization\\src","-s", "     optimization      " };

		//Second Step: Create the structures for the prediction
		MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM ();
		init.main(args);
		metaphor = new MetaphorCode(init);

		//Third Step: Optimization 
		// Search Space definition
		int DIM = 7;
		space = new RefactoringOperationSpace( DIM , metaphor );

		// Optimization Function
		function = new GeneralizedImpactQuality(metaphor);		
		goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing   	

		 
        // Variation definition
        variation = new RefOperMutation( 0.5, metaphor );
             
        // Search method in RefactorSpace
        int MAXITERS = 1000;
        boolean neutral = true; // Accepts movements when having same function value
        search = new HillClimbing<List<RefactoringOperation>>( variation, neutral, MAXITERS );
                  


		size(1200, 600);

	}

	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "view.MyProcessingSketch" });
	}
}
