package javabio.view;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import javabio.optmodel.fitness.FitnessQualityDBScala;
import javabio.optmodel.fitness.RefactorArrayPlainWrite;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.operators.RefOperClassTransposition;
import javabio.optmodel.operators.RefOperMutation;
import javabio.optmodel.operators.RefOperXOver;
import javabio.optmodel.space.RefactoringOperationSpace;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.haea.*;
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.HORIZONTAL;

/**
 * Created by david on 9/12/16.
 */
public class RefGenerator {

    /*
    Configuration
     */
    private static String systems = "xerces";
    private static String userPath = System.getProperty("user.dir");
    private static String[] args = {"-l", "Java", "-p", userPath + "/test_data/code/" + systems + "/src", "-s", "     " + systems + "      "};

    /**
     * Ref Size Configuration
     */
    private JSlider refsizeSlider ;

    /**
     * Population Size Configuration
     */
    private JSlider popSize;

    private JComboBox algoComboBox;

    /**
     * Mutation Rate Configuration
     */
    private JTextField mutationTextField;

    /**
     * Iteration Size Configuration
     */
    private JSlider iterationSlider ;

    private JButton testButton;

    private JComboBox sua;

    private JPanel refGenPanel;

    public static void main(String[] args) {

        JFrame refGeneratorFrame = new JFrame("RefGenerator");
        refGeneratorFrame.setContentPane(new RefGenerator().refGenPanel);
        refGeneratorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        refGeneratorFrame.pack();
        refGeneratorFrame.setVisible(true);
    }


    public RefGenerator() {
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Would you like to proceed with experiment?","Warning",dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION){
                    algoComboBox.getSelectedItem().toString();
                    switch (algoComboBox.getSelectedIndex()){
                        case 1: HAEArefactor( iterationSlider.getValue(), sua.getSelectedItem().toString() );
                                break;
                        case 2: HAEArefactor(iterationSlider.getValue(), sua.getSelectedItem().toString() );
                            break;
                        case 3: HAEArefactor(iterationSlider.getValue(), sua.getSelectedItem().toString() );
                            break;
                        case 4: HAEArefactor(iterationSlider.getValue(), sua.getSelectedItem().toString() );
                            break;
                    }
                }
            }
        });
    }

    private void HAEArefactor(int iter, String systems) {
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

    private void escribirTextoArchivo(String texto) {
        //String algo = "_HILL_";
        String ruta = systems + algoComboBox.getSelectedItem().toString() + "_T_TEST_LOG_JAR.txt";
        try (FileWriter fw = new FileWriter(ruta, true);
             FileReader fr = new FileReader(ruta)) {
            fw.write(texto);
            //Guardamos los cambios del fichero
            fw.flush();
        } catch (IOException e) {
            System.out.println("Error E/S: " + e);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
