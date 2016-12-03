package finaltest;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import edu.wayne.cs.severe.redress2.main.MainMetrics;
import javabio.optmodel.fitness.FitnessQualityDBScala;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.operators.RefOperClassTransposition;
import javabio.optmodel.operators.RefOperAddGen;
import javabio.optmodel.operators.RefOperDelGen;
import javabio.optmodel.operators.RefOperJoin;
import javabio.optmodel.operators.RefOperMutation;
import javabio.optmodel.operators.RefOperXOver;
import javabio.optmodel.fitness.FitnessQualityDB;
import javabio.optmodel.fitness.RefactorArrayPlainWrite;
import javabio.optmodel.space.RefactoringOperationSpace;
import javabio.optmodel.space.VarLengthOperRefSpace;
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
import unalcol.optimization.hillclimbing.HillClimbing;
import unalcol.optimization.simulatedannealing.SimulatedAnnealing;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.SolutionDescriptors;
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

public class MainOptimization {

    private static String algo;
    private static String systems = "jfreechart";
    private static String userPath = System.getProperty("user.dir");
    private static String[] args = {"-l", "Java", "-p", userPath + "/test_data/code/" + systems + "/src", "-s", "     " + systems + "      "};


    public static void main(String[] args) {
        //String systems = "dataset01";
        //measureMetrics( systems );


//        for (int i = 0; i < 30; i++) {
//            algo = "_HILL_";
//            HILLrefactor(i, systems);
//        }
//
//
//		for(int i=0; i<30; i++){
//			algo = "_SIMULATED_";
//			SIMULATEDrefactor(i , systems );
//			}

		for(int i=22; i<30; i++){
			algo = "_HAEA_";
			HAEArefactor( i , systems );
			}


        //for(int i=0; i<30; i++)
        //HAEAVARrefactor( 2 , systems );

    }

    private static void measureMetrics(String systems) {
        // First Step: Calculate Actual Metrics
        MainMetrics init = new MainMetrics();
        init.main(args);

    }

    private static void HAEArefactor(int iter, String systems) {
        //Tracking computational time
        long start = System.currentTimeMillis();

        //First Step: Calculate Actual Metrics

        //Second Step: Create the structures for the prediction
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        //Third Step: Optimization
        // Search Space definition
        int DIM = 5;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new FitnessQualityDBScala( systems + "_HAEA_" + iter);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing

        // Variation definition
        RefOperMutation mutation = new RefOperMutation(0.5);
        ArityTwo<List<RefactoringOperation>> xover = new RefOperXOver();
        ArityOne<List<RefactoringOperation>> transposition = new RefOperClassTransposition();

        // Search method
        final int POPSIZE = 80;
        final int MAXITERS = 100;
        @SuppressWarnings("unchecked")
        Operator<List<RefactoringOperation>>[] opers = (Operator<List<RefactoringOperation>>[]) new Operator[3];
        opers[0] = mutation;
        opers[1] = xover;
        opers[2] = transposition;

        HaeaOperators<List<RefactoringOperation>> operators = new SimpleHaeaOperators<List<RefactoringOperation>>(opers);
        HAEA<List<RefactoringOperation>> search = new HAEA<List<RefactoringOperation>>(POPSIZE, operators, new Tournament<List<RefactoringOperation>>(4), MAXITERS);

        // Tracking the goal evaluations
        WriteDescriptors write_desc = new WriteDescriptors();
        RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
        List<RefactoringOperation> ref = new ArrayList<RefactoringOperation>();
        Write.set(ref, write);
        Write.set(HaeaStep.class, new WriteHaeaStep<List<RefactoringOperation>>());
        Descriptors.set(PopulationSolution.class, new PopulationSolutionDescriptors<List<RefactoringOperation>>());
        Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<List<RefactoringOperation>>());
        Write.set(HaeaOperators.class, write_desc);

        ConsoleTracer tracer = new ConsoleTracer();
        FileTracer filetracergoal = new FileTracer(systems + "_HAEA_fileTracerCCODECGOAL_" + iter, '\n');
        Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
        Tracer.addTracer(goal, filetracergoal);  // Uncomment if you want to trace the function evaluations

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        long end = System.currentTimeMillis();
        System.out.println(solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "__" + solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "_time_:_" + (end - start) + "\n");
    }

    public static void HAEAVARrefactor(int iter, String systems) {
        //Tracking computational time
        long start = System.currentTimeMillis();

        //First Step: Calculate Actual Metrics

        //Second Step: Create the structures for the prediction
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        final MetaphorCode metaphor = new MetaphorCode(init);

        //Third Step: Optimization
        // Search Space definition
        VarLengthOperRefSpace space = new VarLengthOperRefSpace(3, 7);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new FitnessQualityDBScala( systems + "_HAEAVAR_" + iter);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing

        // Variation definition
        RefOperAddGen add = new RefOperAddGen(1, 3, 7);
        RefOperDelGen del = new RefOperDelGen(1, 3, 7);
        ArityTwo<List<RefactoringOperation>> xover = new RefOperJoin();

        // Search method
        int POPSIZE = 50;
        final int MAXITERS = 200;
        @SuppressWarnings("unchecked")
        Operator<List<RefactoringOperation>>[] opers = (Operator<List<RefactoringOperation>>[]) new Operator[3];
        opers[0] = add;
        opers[1] = del;
        opers[2] = xover;

        HaeaOperators<List<RefactoringOperation>> operators = new SimpleHaeaOperators<List<RefactoringOperation>>(opers);
        HAEA<List<RefactoringOperation>> search = new HAEA<List<RefactoringOperation>>(POPSIZE, operators, new Tournament<List<RefactoringOperation>>(4), MAXITERS);

        // Tracking the goal evaluations
        WriteDescriptors write_desc = new WriteDescriptors();
        RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
        List<RefactoringOperation> ref = new ArrayList<RefactoringOperation>();
        Write.set(ref, write);
        Write.set(HaeaStep.class, new WriteHaeaStep<List<RefactoringOperation>>());
        Descriptors.set(PopulationSolution.class, new PopulationSolutionDescriptors<List<RefactoringOperation>>());
        Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<List<RefactoringOperation>>());
        Write.set(HaeaOperators.class, write_desc);

        ConsoleTracer tracer = new ConsoleTracer();
        FileTracer filetracergoal = new FileTracer(systems + "_HAEAVAR_fileTracerCCODECGOAL_" + iter, '\n');
        Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
        Tracer.addTracer(goal, filetracergoal);  // Uncomment if you want to trace the function evaluations

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        long end = System.currentTimeMillis();
        System.out.println(solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "__" + solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "_time_:_" + (end - start) + "\n");
    }

    public static void HILLrefactor(int iter, String systems) {
        //Tracking computational time
        long start = System.currentTimeMillis();

        // First Step: Calculate Actual Metrics

        // Second Step: Create the structures for the prediction
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        // Third Step: Optimization
        // Search Space definition
        final int DIM = 5;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        // Optimization Function
        // OptimizationFunction<List<RefactoringOperation>> function = new
        // GeneralizedImpactQuality(metaphor,"HILLCLIMBING");
        OptimizationFunction<List<RefactoringOperation>> function = new FitnessQualityDBScala( systems + "_HILLCLIMBING_" + iter);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing,
        // remove
        // the
        // parameter
        // false
        // if
        // minimizing

        // Variation definition
        RefOperMutation variation = new RefOperMutation(0.5);

        // Search method in RefactorSpace
        final int MAXITERS = 10000;
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
        FileTracer filetracergoal = new FileTracer(systems + "_hill_fileTracerCCODECGOAL_" + iter, '\n');
        Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
        Tracer.addTracer(goal, filetracergoal);  // Uncomment if you want to trace the function evaluations

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        long end = System.currentTimeMillis();
        System.out.println(solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "__" + solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "_time_:_" + (end - start) + "\n");

    }

    public static void SIMULATEDrefactor(int iter, String systems) {
        //Tracking computational time
        long start = System.currentTimeMillis();

        //First Step: Calculate Actual Metrics

        //Second Step: Create the structures for the prediction
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        //Third Step: Optimization

        // Search Space definition
        final int DIM = 5;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        // Variation definition
        RefOperMutation variation = new RefOperMutation(0.5);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new FitnessQualityDBScala( systems + "_SIMULATED_" + iter);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing   	


        // Search method
        final int MAXITERS = 10000;
        SimulatedAnnealing<List<RefactoringOperation>> search = new SimulatedAnnealing<List<RefactoringOperation>>(variation, MAXITERS);


        // Tracking the goal evaluations
        SolutionDescriptors<List<RefactoringOperation>> desc = new SolutionDescriptors<List<RefactoringOperation>>();
        Descriptors.set(Solution.class, desc);
        RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
        List<RefactoringOperation> ref = new ArrayList<RefactoringOperation>();
        Write.set(ref, write);
        WriteDescriptors w_desc = new WriteDescriptors();
        Write.set(Solution.class, w_desc);

        ConsoleTracer tracer = new ConsoleTracer();
        FileTracer filetracergoal = new FileTracer(systems + "_simulated_fileTracerCCODECGOAL_" + iter, '\n');
        Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
        Tracer.addTracer(goal, filetracergoal);  // Uncomment if you want to trace the function evaluations

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        long end = System.currentTimeMillis();
        System.out.println(solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "__" + solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "_time_:_" + (end - start) + "\n");
    }

    public static void escribirTextoArchivo(String texto) {
        //String algo = "_HILL_";
        String ruta = systems + algo + "_T_TEST_LOG_JAR.txt";
        try (FileWriter fw = new FileWriter(ruta, true);
             FileReader fr = new FileReader(ruta)) {
            fw.write(texto);
            //Guardamos los cambios del fichero
            fw.flush();
        } catch (IOException e) {
            System.out.println("Error E/S: " + e);
        }

    }
}
