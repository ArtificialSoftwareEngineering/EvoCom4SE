package main;

import edu.wayne.cs.severe.redress2.main.MainMetrics;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import java.model.mappings.metaphor.MetaphorCode;

/**
 * Created by Alberto on 6/20/2015.
 */

public class MainBIoRIMP {

    public static void main(String[] argss) {

        //First Step: Calculate Actual Metrics
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "java/model/fitness      "};
        MainMetrics.main(args);

        //Second Step: Create the structures for the prediction

        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        //processor.processSytem();

        //Third Step: Optimization 
        /*
        // Reflection
        ServiceProvider provider = ReflectUtil.getProvider("services\\");

        // Search Space 
        int QUBITARRAYLENGTH = 900;
        QubitArray array = new QubitArray(QUBITARRAYLENGTH,true);
        InstanceService ikey = new QubitArrayInstance();
        provider.register(ikey);
        //FIXME Bitarray
        provider.setDefault_service(InstanceService.class,QubitArray.class,ikey);

        // Phenotype Space 
        List<RefactoringOperation> operations = new ArrayList<RefactoringOperation>();
        
        // Solution space
        Solution<List<RefactoringOperation>> solution 
        			= new Individual <QubitArray,List<RefactoringOperation>>(array, operations);
        
        GrowingFunction<QubitArray,List<RefactoringOperation>> grow 
        					= new GrowingFunctionQubit(metaphor);
        
        SolutionInstance skey = new IndividualInstance( grow );
        provider.setDefault_service(InstanceService.class,Solution.class,skey);

        // Initial population
        int POPSIZE = 100;
        provider.register(skey);
        Vector<Solution<BitArray>> pop = InstanceProvider.get(solution, POPSIZE);

        // Function being optimized
        OptimizationFunction function =  new Deceptive();
        // Evaluating the fitness of the initial population
        Solution.evaluate((Vector)pop, function);

        ArityTwo xover = new XOver();
        ArityOne mutation = new Mutation();
        ArityOne transposition = new Transposition();
               
        // Genetic operators
        Operator[] opers = new Operator[]{mutation, transposition, xover};            
        HaeaOperators haeaOperators = new SimpleHaeaOperators(opers);

        // Extra parent selection mechanism
        Selection selection = new Tournament(4);

        // Genetic Algorithm Transformation
        Transformation transformation = new HAEA(haeaOperators, grow, selection );

        // Evolution generations
        int MAXITER = 100;
        Predicate condition = new ForLoopCondition(MAXITER);

        // Evolutionary algorithm (is a population optimizer)
        PopulationOptimizer ea = new IterativePopulationOptimizer(condition,
                transformation, pop);

        boolean tracing = true;
        if( tracing){
            // A console set tracer
            Tracer tracer = new ConsoleTracer(ea);
            // Adding the tracer collection to the given population optimizer (evolutionary algorithm)
            provider.register(tracer);
            tracer = new FileTracer(ea, "haea.txt", true);
            provider.register(tracer);
        }

        // running the population optimizer (the evolutionary algorithm)
        pop = (Vector<Solution<BitArray>>)ea.apply(function);
        System.out.println( pop.get(0).get() );
        
        TracerProvider.close(ea);
		*/
    }

}
