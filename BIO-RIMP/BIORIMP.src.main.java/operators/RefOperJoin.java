package operators;

import unalcol.search.population.variation.ArityTwo;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.vector.Vector;

import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import space.VarLengthOperRefSpace;
import unalcol.clone.*;

/**
 * <p>Title: Join</p>
 * <p>Description: Joins two chromosomes</p>
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * @author Jonatan Gomez
 * @version 1.0
 */

public class RefOperJoin extends ArityTwo<List<RefactoringOperation>> {

    /**
     * Apply the simple point crossover operation over the given genomes
     *
     * @param c1 The first parent
     * @param c2 The second parent
     */
    public Vector<List<RefactoringOperation>> apply(List<RefactoringOperation> c1, List<RefactoringOperation> c2) {
        try {
            List<RefactoringOperation> genome = (List<RefactoringOperation>) Clone.create(c1);
            genome.addAll(c2);
            Vector<List<RefactoringOperation>> v = new Vector<List<RefactoringOperation>>();
            v.add(genome);
            return v;
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * Testing function
     */
    public static void main(String[] argv) {
        //Getting the Metaphor
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "     optimization      "};
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        VarLengthOperRefSpace refactorSpace = new VarLengthOperRefSpace(11, 20);

        System.out.println("*** Generating a genome of 11-20 genes randomly ***");
        List<RefactoringOperation> parent1 = refactorSpace.get();
        System.out.println(parent1.toString());

        System.out.println("*** Generating a genome of 11-20 genes randomly ***");
        List<RefactoringOperation> parent2 = refactorSpace.get();
        System.out.println(parent2.toString());

        RefOperJoin xover = new RefOperJoin();

        System.out.println("*** Applying the croosover ***");
        List<RefactoringOperation> child = xover.apply(parent1, parent2).get(0);
        System.out.println("New Individual " + child);

    }
}
