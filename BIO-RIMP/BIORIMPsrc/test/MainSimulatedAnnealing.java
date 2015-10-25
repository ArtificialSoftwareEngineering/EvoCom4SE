package test;
import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import operators.RefOperMutation;
import optimization.GeneralizedImpactQuality;
import optimization.RefactorArrayPlainWrite;
import space.RefactoringOperationSpace;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.testbed.MaxOnes;
import unalcol.optimization.integer.IntHyperCube;
import unalcol.optimization.integer.MutationIntArray;
import unalcol.optimization.integer.testbed.QueenFitness;
import unalcol.optimization.real.BinaryToRealVector;
import unalcol.optimization.real.HyperCube;
import unalcol.optimization.real.mutation.AdaptMutationIntensity;
import unalcol.optimization.real.mutation.IntensityMutation;
import unalcol.optimization.real.mutation.OneFifthRule;
import unalcol.optimization.real.mutation.PermutationPick;
import unalcol.optimization.real.mutation.PickComponents;
import unalcol.optimization.real.testbed.Rastrigin;
import unalcol.optimization.simulatedannealing.SimulatedAnnealing;
import unalcol.random.real.DoubleGenerator;
import unalcol.random.real.SimplestSymmetricPowerLawGenerator;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.SolutionDescriptors;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.search.multilevel.MultiLevelSearch;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.integer.array.IntArray;
import unalcol.types.integer.array.IntArrayPlainWrite;
import unalcol.types.real.array.DoubleArray;
import unalcol.types.real.array.DoubleArrayPlainWrite;


public class MainSimulatedAnnealing {
	
	public static void refactor(){

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
        int DIM = 7;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace( DIM , metaphor );

        // Variation definition
        RefOperMutation variation = new RefOperMutation( 0.5, metaphor );

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor);		
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing   	
        
    	
        // Search method
        int MAXITERS = 30;
        SimulatedAnnealing< List<RefactoringOperation> > search = new SimulatedAnnealing< List<RefactoringOperation> >(variation, MAXITERS);


        // Tracking the goal evaluations
        SolutionDescriptors<List<RefactoringOperation>> desc = new SolutionDescriptors<List<RefactoringOperation>>();
        Descriptors.set(Solution.class, desc);
        RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
        List<RefactoringOperation> ref= new ArrayList<RefactoringOperation>();
        Write.set(ref , write);
        WriteDescriptors w_desc = new WriteDescriptors();
        Write.set(Solution.class, w_desc);
        
        ConsoleTracer tracer = new ConsoleTracer();       
        Tracer.addTracer(goal,tracer);
        
        // Apply the search method
        Solution< List<RefactoringOperation> > solution = search.apply(space, goal);
        
        System.out.println( solution.quality()+ "=" + solution.value() );		
	}
    
	public static void binary(){
		// Search Space definition
		int DIM = 100;
    	Space<BitArray> space = new BinarySpace( DIM );
    	
    	// Variation definition
    	BitMutation variation = new BitMutation();
        
    	// Optimization Function
    	OptimizationFunction<BitArray> function = new MaxOnes();		
        Goal<BitArray> goal = new OptimizationGoal<BitArray>(function, false); // maximizing, remove the parameter false if minimizing   	
    	
        // Search method
        int MAXITERS = 10000;
        SimulatedAnnealing<BitArray> search = new SimulatedAnnealing<BitArray>(variation, MAXITERS);

        // Tracking the goal evaluations
        ConsoleTracer tracer = new ConsoleTracer();       
        Tracer.addTracer(goal,tracer);
        
        // Apply the search method
        Solution<BitArray> solution = search.apply(space, goal);
        
        System.out.println( solution.quality() + "=" + solution.value());		
	}    

    
	
    public static void main(String[] args){
    	refactor(); // Uncomment if testing real valued functions
 
    }

}
