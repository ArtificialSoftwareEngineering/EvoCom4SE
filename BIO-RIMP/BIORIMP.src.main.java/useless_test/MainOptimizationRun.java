package useless_test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import operators.ClassTransposition;
import operators.RefOperAddGen;
import operators.RefOperDelGen;
import operators.RefOperJoin;
import operators.RefOperMutation;
import operators.RefOperTransposition;
import operators.RefOperXOver;
import optimization.GeneralizedImpactQuality;
import optimization.RefactorArrayPlainWrite;
import space.RefactoringOperationSpace;
import space.VarLengthOperRefSpace;
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
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.Transposition;
import unalcol.optimization.binary.XOver;
import unalcol.optimization.binary.testbed.Deceptive;
import unalcol.optimization.hillclimbing.HillClimbing;
import unalcol.optimization.integer.IntHyperCube;
import unalcol.optimization.integer.MutationIntArray;
import unalcol.optimization.integer.XOverIntArray;
import unalcol.optimization.integer.testbed.QueenFitness;
import unalcol.optimization.real.BinaryToRealVector;
import unalcol.optimization.real.HyperCube;
import unalcol.optimization.real.mutation.AdaptMutationIntensity;
import unalcol.optimization.real.mutation.IntensityMutation;
import unalcol.optimization.real.mutation.OneFifthRule;
import unalcol.optimization.real.mutation.PermutationPick;
import unalcol.optimization.real.mutation.PickComponents;
import unalcol.optimization.real.testbed.Rastrigin;
import unalcol.optimization.real.xover.LinearXOver;
import unalcol.optimization.simulatedannealing.SimulatedAnnealing;
import unalcol.random.real.DoubleGenerator;
import unalcol.random.real.SimplestSymmetricPowerLawGenerator;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.SolutionDescriptors;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.search.multilevel.MultiLevelSearch;
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
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.integer.array.IntArray;
import unalcol.types.integer.array.IntArrayPlainWrite;
import unalcol.types.real.array.DoubleArray;
import unalcol.types.real.array.DoubleArrayPlainWrite;

public class MainOptimizationRun {

    public static void haeaFix(MetaphorCode metaphor, String system) {

        // processor.processSytem();

        // Third Step: Optimization
        // Search Space definition
        int DIM = 7;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor,
                "HAEAFIX_" + system);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing,
        // remove
        // the
        // parameter
        // false
        // if
        // minimizing

        // Variation definition
        // DoubleGenerator random = new SimplestSymmetricPowerLawGenerator(); //
        // It can be set to Gaussian or other symmetric number generator
        // (centered in zero)
        // PickComponents pick = new PermutationPick(DIM/2); // It can be set to
        // null if the mutation operator is applied to every component of the
        // solution array
        // AdaptMutationIntensity adapt = new OneFifthRule(500, 0.9); // It can
        // be set to null if no mutation adaptation is required
        // IntensityMutation mutation = new IntensityMutation( 0.1, random,
        // pick, adapt );
        RefOperMutation mutation = new RefOperMutation(0.5);
        ArityTwo<List<RefactoringOperation>> xover = new RefOperXOver();
        ArityOne<List<RefactoringOperation>> transpositionRef = new RefOperTransposition();
        ArityOne<List<RefactoringOperation>> transposition = new ClassTransposition();

        // Search method
        int POPSIZE = 50;
        int MAXITERS = 10000;
        @SuppressWarnings("unchecked")
        Operator<List<RefactoringOperation>>[] opers = (Operator<List<RefactoringOperation>>[]) new Operator[3];
        opers[0] = mutation;
        opers[1] = xover;
        opers[2] = transposition;

        HaeaOperators<List<RefactoringOperation>> operators = new SimpleHaeaOperators<List<RefactoringOperation>>(
                opers);
        HAEA<List<RefactoringOperation>> search = new HAEA<List<RefactoringOperation>>(POPSIZE, operators,
                new Tournament<List<RefactoringOperation>>(4), MAXITERS);

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
        Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
        // function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the
        // hill-climbing algorithm

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        escribirTextoArchivo(solution.quality() + "=" + solution.value(), "HAEAFIX_" + system);
    }

    public static void haeaVar(MetaphorCode metaphor, String system) {

        // Search Space definition
        VarLengthOperRefSpace space = new VarLengthOperRefSpace(5, 20);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor,
                "HAEAVAR_" + system);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing,
        // remove
        // the
        // parameter
        // false
        // if
        // minimizing

        // Variation definition
        // DoubleGenerator random = new SimplestSymmetricPowerLawGenerator(); //
        // It can be set to Gaussian or other symmetric number generator
        // (centered in zero)
        // PickComponents pick = new PermutationPick(DIM/2); // It can be set to
        // null if the mutation operator is applied to every component of the
        // solution array
        // AdaptMutationIntensity adapt = new OneFifthRule(500, 0.9); // It can
        // be set to null if no mutation adaptation is required
        // IntensityMutation mutation = new IntensityMutation( 0.1, random,
        // pick, adapt );
        RefOperAddGen add = new RefOperAddGen(2, 5, 10);
        RefOperDelGen del = new RefOperDelGen(2, 5, 20);
        ArityTwo<List<RefactoringOperation>> xover = new RefOperJoin();

        // Search method
        int POPSIZE = 50;
        int MAXITERS = 10000;
        @SuppressWarnings("unchecked")
        Operator<List<RefactoringOperation>>[] opers = (Operator<List<RefactoringOperation>>[]) new Operator[3];
        opers[0] = add;
        opers[1] = del;
        opers[2] = xover;

        HaeaOperators<List<RefactoringOperation>> operators = new SimpleHaeaOperators<List<RefactoringOperation>>(
                opers);
        HAEA<List<RefactoringOperation>> search = new HAEA<List<RefactoringOperation>>(POPSIZE, operators,
                new Tournament<List<RefactoringOperation>>(4), MAXITERS);

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
        Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
        // function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the
        // hill-climbing algorithm

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        escribirTextoArchivo(solution.quality() + "=" + solution.value(), "HAEAVAR_" + system);

    }

    public static void hillClimbing(MetaphorCode metaphor, String system) {

        // Third Step: Optimization
        // Search Space definition
        int DIM = 7;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor, "HILLCLIMBING_" + system);
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
        int MAXITERS = 100000;
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

        escribirTextoArchivo("QUALITY_:" + solution.quality() + "=" + "VALUE_:" + solution.value(), "HILLCLIMBING_" + system);
    }

    public static void simulated(MetaphorCode metaphor, String system) {

        // Search Space definition
        int DIM = 7;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        // Variation definition
        RefOperMutation variation = new RefOperMutation(0.5);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor, "SIMULATED_" + system);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing   	


        // Search method
        int MAXITERS = 100000;
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
        Tracer.addTracer(goal, tracer);

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        escribirTextoArchivo(solution.quality() + "=" + solution.value(), "SIMULATED_" + system);

    }

    public static void main(String[] arg) {

        String[] systems = {"ccodec", "jhotdraw", "evolution", "acra"};
        for (int i = 0; i < systems.length; i++) {
            // First Step: Calculate Actual Metrics
            String userPath = System.getProperty("user.dir");
            // String[] args = { "-l", "Java", "-p",
            // userPath+"\\test_data\\code\\acra\\src","-s", " acra " };
            String[] args = {"-l", "Java", "-p", userPath + "/test_data/code/" + systems[i] + "/src", "-s",
                    "     " + systems[i] + "      "};
            // MainMetrics.main(args);
            // Second Step: Create the structures for the prediction
            MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
            init.main(args);
            // Third Step: Optimization
            haeaFix(new MetaphorCode(init), systems[i]);
            haeaVar(new MetaphorCode(init), systems[i]);
            hillClimbing(new MetaphorCode(init), systems[i]);
            simulated(new MetaphorCode(init), systems[i]);

        }

        System.out.println(" ***********************SUCCESFULL ********************");

    }

    public static void escribirTextoArchivo(String texto, String file) {
        String ruta = file + "_TEST_FITNESS_JAR.txt";
        try (FileWriter fw = new FileWriter(ruta, true); FileReader fr = new FileReader(ruta)) {
            // Escribimos en el fichero un String y un caracter 97 (a)
            fw.write(texto);
            // fw.write(97);
            // Guardamos los cambios del fichero
            fw.flush();
        } catch (IOException e) {
            System.out.println("Error E/S: " + e);
        }

    }
}
