package useless_test;

import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import operators.RefOperMutation;
import space.RefactoringOperationSpace;

public class TestRefactorRepair {

    public static void main(String[] argss) {
        // TODO Auto-generated method stub
        //Getting the Metaphor
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "     optimization      "};
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        System.out.println("*** Generating a genome of x genes randomly ***");

        //Creating the Space
        RefactoringOperationSpace refactorSpace = new RefactoringOperationSpace(10000);

        //Visualizing the get() Space
        List<RefactoringOperation> refactor = refactorSpace.get();
        if (refactor != null)
            for (RefactoringOperation refOper : refactor) {
                System.out.println("Random Refactor: " + refOper.toString());
            }


        //ListRefOperMutation mutation = new ListRefOperMutation(0.05);
        RefOperMutation mutation = new RefOperMutation(0.5);

        System.out.println("*** Applying the mutation ***");
        List<RefactoringOperation> mutated = mutation.apply(refactor);

        System.out.println("Mutated array ");
        if (mutated != null)
            for (RefactoringOperation refOper : mutated) {
                System.out.println("Mutated Refactor: " + refOper.toString());
            }
        int refss = 0;
        if (!refactorSpace.feasible(mutated)) {
            List<RefactoringOperation> mutatedN = refactorSpace.repair(mutated);
            System.out.println("Mutated array Repaired ");
            if (mutatedN != null)
                for (RefactoringOperation refOper : mutatedN) {
                    System.out.println("Repaired Refactor: " + refss + refOper.toString());
                    refss++;
                }
        }

    }

}
