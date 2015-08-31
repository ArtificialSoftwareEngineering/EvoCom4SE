package test;

import java.util.ArrayList;
import java.util.List;

import controller.GQSPred;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainMetrics;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import entity.QubitArray;
import optimization.QubitMutation;
import optimization.QubitSpace;
import optimization.RefactoringOperationSpace;
import unalcol.algorithm.iterative.ForLoopCondition;
import unalcol.evolution.haea.HAEA;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.math.logic.Predicate;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.testbed.Deceptive;
import unalcol.optimization.hillclimbing.HillClimbing;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.FileTracer;
import unalcol.tracer.Tracer;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayInstance;
import unalcol.types.collection.vector.Vector;

/**
 * Created by Alberto on 6/20/2015.
 */

public class MainHillClimbing {

	public static void main(String[] argss) {
		
		//First Step: Calculate Actual Metrics
		String userPath = System.getProperty("user.dir");
        String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\optimization\\src","-s", "     optimization      " };
        //MainMetrics.main(args);
        
        //Second Step: Create the structures for the prediction
        MetaphorCode metaphor = new MetaphorCode();
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM ();
        init.main(args);
        metaphor.setSysTypeDcls(init.getSysTypeDcls());
        metaphor.setBuilder(init.getBuilder());
        metaphor.setLang(init.getLang());
        
     	
        //processor.processSytem();
        
        //Third Step: Optimization 
        
        // Search Space definition
        int DIM = 120;
        //Space<QubitArray> space = new QubitSpace( DIM );    
        Space<RefactoringOperation > space = new RefactoringOperationSpace( DIM );
        
        // Variation definition
        QubitMutation variation = new QubitMutation();
             
        // Optimization Function
        OptimizationFunction<BitArray> function = new Deceptive();		
        Goal<BitArray> goal = new OptimizationGoal<BitArray>(function, false); // maximizing, remove the parameter false if minimizing   	
         	
             // Search method
             int MAXITERS = 10000;
             boolean neutral = true; // Accepts movements when having same function value
             //HillClimbing<BitArray> search = new HillClimbing<BitArray>( variation, neutral, MAXITERS );

             // Tracking the goal evaluations
             ConsoleTracer tracer = new ConsoleTracer();       
//           Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
             //Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
             
             // Apply the search method
             //Solution<BitArray> solution = search.apply(space, goal);
             
             //System.out.println( solution.quality() + "=" + solution.value());	
        
		
	}

}
