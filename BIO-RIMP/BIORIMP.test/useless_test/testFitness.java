package useless_test;

import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.operators.RefOperMutation;
import javabio.optmodel.fitness.GeneralizedImpactQuality;
import javabio.optmodel.space.RefactoringOperationSpace;
import unalcol.optimization.OptimizationFunction;
import unalcol.search.space.Space;

public class testFitness {

    public static void main(String[] argss) {
        // TODO Auto-generated method stub
        //Getting the Metaphor
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "java/optmodel/fitness      "};


        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        //Creating the individual
        /*
        List<QubitRefactor> genome = new ArrayList<QubitRefactor>();
		List<RefactoringOperation> phe = new ArrayList<RefactoringOperation>();
		for(int i = 0; i < 20; i++){
			genome.add(new QubitRefactor(true,4));
			System.out.println(i+" "+
			genome.get(i).getGenObservation().toString());
		}
		//Processing EncodeDecode
		CodeDecodeMap<List<QubitRefactor>,List<RefactoringOperation>> map 
			= new CodeDecodeRefactorList(metaphor); 
		
		phe = map.decode(genome);
		
		for(int i = 0; i < phe.size(); i++){
			System.out.println(i+" "+ phe.get(i).toString());
		}
		*/
        // Search Space definition
        int DIM = 1000;
        Space<List<RefactoringOperation>> space = new RefactoringOperationSpace(DIM);

        List<RefactoringOperation> refactor = space.get();
        RefOperMutation mutation = new RefOperMutation(0.5);

        System.out.println("*** Applying the mutation ***");
        List<RefactoringOperation> mutated = mutation.apply(refactor);

        //Processing Fitness
        OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor, "OPTIMIZATION");
        System.out.println("FITNESS : [" + function.apply(refactor) + "]");

        //Processing Fitness
        //System.out.println("FITNESS MUTATED: ["+ function.apply( mutated ) +"]");

        List<RefactoringOperation> refactor_reipared = space.repair(mutated);
        //Processing Fitness
        System.out.println("FITNESS REPAIRED: [" + function.apply(refactor_reipared) + "]");
    }

}
