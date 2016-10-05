package test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import java.model.mappings.metaphor.MetaphorCode;
import java.model.operators.RefOperMutation;
import java.model.fitness.FitnessQualityDB;
import java.model.fitness.RefactorArrayPlainWrite;
import java.model.space.RefactoringOperationSpace;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.simulatedannealing.SimulatedAnnealing;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.SolutionDescriptors;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.FileTracer;
import unalcol.tracer.Tracer;


public class MainSimulatedAnnealing {

    public static void refactorSimulated(int iter, String systems) {
        //Tracking computational time
        long start = System.currentTimeMillis();

        //First Step: Calculate Actual Metrics
        String userPath = System.getProperty("user.dir");
        //String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\acra\\src","-s", "     acra      " };
        String[] args = {"-l", "Java", "-p", userPath + "/test_data/code/" + systems + "/src", "-s", "     " + systems + "      "};
        //Second Step: Create the structures for the prediction
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        //processor.processSytem();

        //Third Step: Optimization

        // Search Space definition
        int DIM = 7;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        // Variation definition
        RefOperMutation variation = new RefOperMutation(0.5);

        // Optimization Function
        OptimizationFunction<List<RefactoringOperation>> function = new FitnessQualityDB(metaphor, systems + "_SIMULATED_" + iter);
        Goal<List<RefactoringOperation>> goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing   	


        // Search method
        int MAXITERS = 2000;
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
        FileTracer filetracergoal = new FileTracer(systems + "_fileTracerCCODECGOAL_" + iter, '\n');
        Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
        Tracer.addTracer(goal, filetracergoal);

        // Apply the search method
        Solution<List<RefactoringOperation>> solution = search.apply(space, goal);

        long end = System.currentTimeMillis();
        System.out.println(solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "__" + solution.quality() + "=" + solution.value());
        escribirTextoArchivo(iter + "_time_:_" + (end - start) + "\n");
    }


    public static void main(String[] args) {
        String systems = "acra";
        for (int i = 0; i < 30; i++) {
            refactorSimulated(i, systems);
        }
    }

    public static void escribirTextoArchivo(String texto) {
        String systems = "acra";
        String ruta = systems + "_T_SIMULATED_JAR.txt";
        try (FileWriter fw = new FileWriter(ruta, true);
             FileReader fr = new FileReader(ruta)) {
            //Escribimos en el fichero un String y un caracter 97 (a)
            fw.write(texto);
            //fw.write(97);
            //Guardamos los cambios del fichero
            fw.flush();
        } catch (IOException e) {
            System.out.println("Error E/S: " + e);
        }

    }

}