package test;

import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import operators.ClassTransposition;
import operators.RefOperMutation;
import operators.RefOperTransposition;
import operators.RefOperXOver;
import optimization.FitnessQualityDB;
import optimization.RefactorArrayPlainWrite;
import space.RefactoringOperationSpace;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.haea.HAEA;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.HaeaStep;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperatorsDescriptor;
import unalcol.evolution.haea.WriteHaeaStep;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.population.PopulationSolution;
import unalcol.search.population.PopulationSolutionDescriptors;
import unalcol.search.population.variation.ArityTwo;
import unalcol.search.population.variation.Operator;
import unalcol.search.selection.Tournament;
import unalcol.search.space.ArityOne;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.FileTracer;
import unalcol.tracer.Tracer;


public class MainHAEAFIX {
	
	//@Test
	public static void refactorHAEA( int iter, String systems ){
		//Tracking computational time
		long start = System.currentTimeMillis();
		
		//First Step: Calculate Actual Metrics
		//String userPath = System.getProperty("user.dir")+"/BIO-RIMP";
		String userPath = System.getProperty("user.dir");
		//String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\acra\\src","-s", "     acra      " };
		String[] args = { "-l", "Java", "-p", userPath + "/test_data/code/"+systems+"/src", "-s", "     "+systems+"      " };
	
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

		// Optimization Function
		OptimizationFunction<List<RefactoringOperation>> function = new FitnessQualityDB(metaphor, systems +"_HAEA_" + iter);		
		Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing   	

		// Variation definition
		//DoubleGenerator random = new SimplestSymmetricPowerLawGenerator(); // It can be set to Gaussian or other symmetric number generator (centered in zero)
		//PickComponents pick = new PermutationPick(DIM/2); // It can be set to null if the mutation operator is applied to every component of the solution array
		//AdaptMutationIntensity adapt = new OneFifthRule(500, 0.9); // It can be set to null if no mutation adaptation is required
		//IntensityMutation mutation = new IntensityMutation( 0.1, random, pick, adapt );
		RefOperMutation mutation = new RefOperMutation( 0.5, metaphor );
		ArityTwo< List<RefactoringOperation> > xover = new RefOperXOver();
		//ArityOne< List<RefactoringOperation> > transpositionRef = new RefOperTransposition();
		ArityOne< List<RefactoringOperation> > transposition = new ClassTransposition();

		// Search method
		int POPSIZE = 20;
		int MAXITERS = 80;
		@SuppressWarnings("unchecked")
		Operator< List<RefactoringOperation> >[] opers = (Operator< List<RefactoringOperation> >[])new Operator[3];
		opers[0] = mutation;
		opers[1] = xover;
		opers[2] = transposition;
		
		HaeaOperators< List<RefactoringOperation> > operators = new SimpleHaeaOperators< List<RefactoringOperation> >(opers);
		HAEA< List<RefactoringOperation> > search = new HAEA< List<RefactoringOperation> >(POPSIZE, operators, new Tournament< List<RefactoringOperation> >(4), MAXITERS );

		// Tracking the goal evaluations
		WriteDescriptors write_desc = new WriteDescriptors();
		RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
		List<RefactoringOperation> ref= new ArrayList<RefactoringOperation>();
		Write.set(ref , write);
		Write.set(HaeaStep.class, new WriteHaeaStep< List<RefactoringOperation> >());
		Descriptors.set(PopulationSolution.class, new PopulationSolutionDescriptors<List<RefactoringOperation>>());
		Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<List<RefactoringOperation>>());
		Write.set(HaeaOperators.class, write_desc);

		ConsoleTracer tracer = new ConsoleTracer(); 
		FileTracer filetracergoal = new FileTracer(systems +"_fileTracerCCODECGOAL_"+iter, '\n');
		//FileTracer filetraceralgo = new FileTracer("fileTracerCCODECALGO_"+iter, '\n');
		//FileTracer filetracerfunci = new FileTracer("fileTracerCCODEfunci_"+iter, '\n');
		Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
		Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
		Tracer.addTracer(goal, filetracergoal);  // Uncomment if you want to trace the function evaluations
		//Tracer.addTracer(search, filetraceralgo); // Uncomment if you want to trace the hill-climbing algorithm
		//Tracer.addTracer(function, filetracerfunci);


		// Apply the search method
		Solution< List<RefactoringOperation> > solution = search.apply(space, goal);
		
		long end = System.currentTimeMillis();
		System.out.println( solution.quality() + "=" + solution.value() );	
		escribirTextoArchivo(iter+"__" + solution.quality() + "=" + solution.value() );
		escribirTextoArchivo(iter + "_time_:_"+ (end - start) +"\n" );
		//The assertion verifies if the time taken is less than four hours in milliseconds
		//assertTrue( (end - start) < 1.44E+7 );
	}


	public static void main(String[] args){
		String systems = "acra";
		for(int i=0; i<30; i++){
			refactorHAEA( i , systems );
			MainHAEAvar.refactorVAR(i, systems);
			MainHillClimbing.refactorHill(i, systems);
			MainSimulatedAnnealing.refactorSimulated(i, systems);
		}
		 // Uncomment if testing real valued functions
		// binary(); // Uncomment if testing binary valued functions
		//binary2real(); // Uncomment if you want to try the multi-level search method

	}
	
	public static void escribirTextoArchivo( String texto ) {
		String systems = "acra";
		String ruta = systems+"_T_HAEAFIX_JAR.txt";
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
