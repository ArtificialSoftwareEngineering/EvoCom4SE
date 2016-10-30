package javabio.optmodel.operators;

import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.space.CreateRefOper;
import javabio.optmodel.space.VarLengthOperRefSpace;
import unalcol.clone.*;
import unalcol.random.raw.RawGenerator;
import unalcol.search.space.ArityOne;

/**
 * <p>Title: AddGen</p>
 * <p>Description: The gene addition operator. Add a gene generated randomly</p>
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * @author Jonatan Gomez
 * @version 1.0
 */

public class RefOperAddGen extends ArityOne<List<RefactoringOperation>> {
    /**
     * If the added gene is added to the end of the genome or not (randomly added)
     */
    protected boolean append = true;

    protected int gene_size;
    protected int min_length;
    protected int max_length;
    //protected MetaphorCode metaphor;

    public RefOperAddGen(int gene_size, int min_length, int max_length) {
        this.gene_size = gene_size;
        this.min_length = min_length;
        this.max_length = max_length;
    }

    /**
     * Constructor: creates an addition gene operator that adds a gene according to
     * the variable _append
     *
     * @param append If the added gene is added to the end of the genome or not (randomly added)
     */
    public RefOperAddGen(int gene_size, int min_length, int max_length, boolean append) {
        this(gene_size, min_length, max_length);
        this.append = append;
    }

    /**
     * Add to the end of the given genome a new gene
     *
     * @param gen Genome to be modified
     * @return The added gene or a String
     */
    public List<RefactoringOperation> apply(List<RefactoringOperation> gen) {
        try {
            List<RefactoringOperation> genome = (List<RefactoringOperation>) Clone.create(gen);
            if (genome.size() < max_length) {
                CreateRefOper refOper = new CreateRefOper();
                List<RefactoringOperation> gene = refOper.get(gene_size);
                if (append) {
                    genome.addAll(gene);
                } else {
                    int k = RawGenerator.get(this).integer(genome.size());
                    if (k == genome.size()) {
                        genome.addAll(gene);
                    } else {
                        genome.addAll(k, gene);
                    }
                }
            }
            return genome;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Testing function
     */
    public static void main(String[] argv) {
        //Getting the Metaphor
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "java/optmodel/fitness      "};
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);

        System.out.println("*** Generating a genome of 10 genes randomly ***");

        //Creating the Space
        VarLengthOperRefSpace refactorSpace = new VarLengthOperRefSpace(5, 10);
        List<RefactoringOperation> genome = refactorSpace.get();
        System.out.println(genome.toString());

        System.out.println("*** Generating a Addition Gen operation with gen length of 3 ***");
        RefOperAddGen add = new RefOperAddGen(3, 5, 10, false);
        System.out.println("*** Applying the addition ***");
        List<RefactoringOperation> gene = add.apply(genome);

        System.out.println("*** Mutated genome ***");
        System.out.println(gene);
    }
}
