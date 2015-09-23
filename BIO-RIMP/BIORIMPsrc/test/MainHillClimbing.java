package test;

import java.util.ArrayList;
import java.util.List;

import controller.GQSPred;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainMetrics;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import entity.QubitArray;
import entity.QubitRefactor;
import optimization.CodeDecodeRefactorList;
import optimization.GeneralizedImpactQuality;
import optimization.ListRefOperMutation;
import optimization.QubitMutation;
import optimization.RefOperMutation;
import optimization.RefactorArrayPlainWrite;
import space.QubitSpace;
import space.RefactoringOperationSpace;
import unalcol.algorithm.iterative.ForLoopCondition;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.haea.HAEA;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.io.Write;
import unalcol.math.logic.Predicate;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.testbed.Deceptive;
import unalcol.optimization.hillclimbing.HillClimbing;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.SolutionDescriptors;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.search.multilevel.MultiLevelSearch;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.FileTracer;
import unalcol.tracer.Tracer;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayInstance;
import unalcol.types.collection.vector.Vector;
import unalcol.types.real.array.DoubleArrayPlainWrite;

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
        
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM ();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);
        
     	
        //processor.processSytem();
        
        //Third Step: Optimization 
        
        // Search Space definition
        int DIM = 12;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace( DIM );
        
        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor);		
        //Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function, false); // maximizing, remove the parameter false if minimizing   	
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing   	
        
        // CodeDecodeMap
        CodeDecodeMap<List<QubitRefactor>,List<RefactoringOperation>> map 
			= new CodeDecodeRefactorList(metaphor); 
        
        
        // Variation definition in QubitRefactorSpace
        ListRefOperMutation variation = new ListRefOperMutation();
             
          	
        // Search method in QubitRefactorSpace
        int MAXITERS = 10000;
        boolean neutral = true; // Accepts movements when having same function value
        //HillClimbing<BitArray> search = new HillClimbing<BitArray>( variation, neutral, MAXITERS );
        HillClimbing<List<QubitRefactor>> Qubit_search = new HillClimbing<List<QubitRefactor>>( variation, neutral, MAXITERS );
             
        // The multilevel search method (moves in the qubitrefactor space, but computes fitness in the refactoring space)
        MultiLevelSearch<List<QubitRefactor>, List<RefactoringOperation> > search = new MultiLevelSearch<>(Qubit_search, map);
        

        // Tracking the goal evaluations
        SolutionDescriptors<List<RefactoringOperation>> desc = new SolutionDescriptors<List<RefactoringOperation>>();
        Descriptors.set(Solution.class, desc);
        RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
        List<RefactoringOperation> ref= new ArrayList<RefactoringOperation>();
        Write.set(ref , write);
        
        ConsoleTracer tracer = new ConsoleTracer();       
        //Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
             
        // Apply the search method
        Solution< List<RefactoringOperation> > solution = search.apply(space, goal);
        
        System.out.println( solution.quality() + "=" + solution.value());	
        
		
	}

}
