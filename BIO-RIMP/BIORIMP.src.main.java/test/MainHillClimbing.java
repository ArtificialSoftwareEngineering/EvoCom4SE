package test;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controller.GQSPred;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainMetrics;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import entity.QubitArray;
import entity.QubitRefactor;
import operators.RefOperMutation;
import optimization.CodeDecodeRefactorList;
import optimization.FitnessQualityDB;
import optimization.GeneralizedImpactQuality;
import optimization.ListRefOperMutation;

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
		String systems = "acra";
		for(int i=0; i<30; i++){
			refactorHill( i , systems );
		}
		
	}
	
	public static void refactorHill(int iter , String systems){
		//Tracking computational time
		long start = System.currentTimeMillis();
		
		// First Step: Calculate Actual Metrics
		String userPath = System.getProperty("user.dir");
		// String[] args = { "-l", "Java", "-p",
		// userPath+"\\test_data\\code\\evolutionaryagent\\src","-s", "
		// evolutionaryagent " };
		String[] args = { "-l", "Java", "-p", userPath + "/test_data/code/"+systems+"/src", "-s", "     "+systems+"      " };

		// MainMetrics.main(args);

		// Second Step: Create the structures for the prediction
		MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
		init.main(args);
		MetaphorCode metaphor = new MetaphorCode(init);

		// Third Step: Optimization
		// Search Space definition
		int DIM = 7;
		Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM, metaphor);

		// Optimization Function
		// OptimizationFunction<List<RefactoringOperation>> function = new
		// GeneralizedImpactQuality(metaphor,"HILLCLIMBING");
		OptimizationFunction<List<RefactoringOperation>> function = new FitnessQualityDB(metaphor, systems+"_HILLCLIMBING_"+ iter);
		Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing,
																											// remove
																											// the
																											// parameter
																											// false
																											// if
																											// minimizing

		// Variation definition
		RefOperMutation variation = new RefOperMutation(0.5, metaphor);

		// Search method in RefactorSpace
		int MAXITERS = 2000;
		boolean neutral = true; // Accepts movements when having same function
								// value
		HillClimbing<List<RefactoringOperation>> search = new HillClimbing<List<RefactoringOperation>>(variation,
				neutral, MAXITERS);

		// Tracking the goal evaluations
		SolutionDescriptors<List<RefactoringOperation>> desc = new SolutionDescriptors<List<RefactoringOperation>>();
		Descriptors.set(Solution.class, desc);
		RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
		List<RefactoringOperation> ref = new ArrayList<RefactoringOperation>();
		Write.set(ref, write);
		WriteDescriptors w_desc = new WriteDescriptors();
		Write.set(Solution.class, w_desc);

		ConsoleTracer tracer = new ConsoleTracer();
		// Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
		// function evaluations
		Tracer.addTracer(search, tracer); // Uncomment if you want to trace the
											// hill-climbing algorithm

		// Apply the search method
		Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

		long end = System.currentTimeMillis();
		System.out.println( solution.quality() + "=" + solution.value() );	
		escribirTextoArchivo(iter+"__" + solution.quality() + "=" + solution.value() );
		escribirTextoArchivo(iter + "_time_:_"+ (end - start) +"\n" );

	}
	
	public static void escribirTextoArchivo( String texto ) {
		String systems = "acra";
		String ruta = systems+"_T_HILL.txt";
		try(FileWriter fw=new FileWriter( ruta , true );
				FileReader fr=new FileReader( ruta )){
			//Escribimos en el fichero un String y un caracter 97 (a)
			fw.write( texto );
			//fw.write(97);
			//Guardamos los cambios del fichero
			fw.flush();
		}catch(IOException e){
			System.out.println("Error E/S: "+e);
		}

	}
	
}
