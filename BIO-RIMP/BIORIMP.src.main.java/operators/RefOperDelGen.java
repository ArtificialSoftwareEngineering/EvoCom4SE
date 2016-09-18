package operators;

import unalcol.types.collection.bitarray.BitArray;

import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import space.VarLengthOperRefSpace;
import unalcol.clone.*;
import unalcol.random.raw.RawGenerator;
import unalcol.search.space.ArityOne;


/**
 * <p>Title: DelGen</p>
 * <p>Description: The gene deletion operator.  Deletes a gene in the genome</p>
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * @author Jonatan Gomez
 * @version 1.0
 */

public class RefOperDelGen extends ArityOne<List<RefactoringOperation>> {
    /**
     * If the last gene is going to be deleted or one randomly selected
     */
    protected boolean del_last_gene = true;

    protected int gene_size;
    protected int min_length;
    protected int max_length;


    public RefOperDelGen(int gene_size, int min_length, int max_length) {
        this.gene_size = gene_size;
        this.min_length = min_length;
        this.max_length = max_length;
    }


    /**
     * Constructor: create a deletion gene operator that deletes a gene of a genome
     *
     * @param gene_size     Number of bits defining a gene
     * @param min_length    Minimum number of genes in the chromosome
     * @param max_length    Maximun number of genes in the chromosome
     * @param del_last_gene Determines if the gene to be deleted is the last in
     *                      the genome or not. A true value indicates that the last gene is deleted.
     *                      A false value indiciates that a gene is randomly selected and deleted
     */
    public RefOperDelGen(int gene_size, int min_length, int max_length,
                         boolean del_last_gene) {
        this(gene_size, min_length, max_length);
        this.del_last_gene = del_last_gene;
    }

    /**
     * Delete from the given genome the last gene
     *
     * @param gen Genome to be modified
     */
    public List<RefactoringOperation> apply(List<RefactoringOperation> gen) {
        try {
            List<RefactoringOperation> genome = (List<RefactoringOperation>) Clone.create(gen);
            if (genome.size() > min_length + gene_size) {
                if (del_last_gene) {
                    for (int i = gen.size() - 1; i >= gen.size() - 1 - gene_size; i--)
                        genome.remove(i);
                } else {
                    int k = RawGenerator.get(this).integer(genome.size());
                    for (int i = k; i < k + gene_size && i < gen.size(); i++)
                        genome.remove(k);
                }
            }
            return genome;
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

        System.out.println("*** Generating a genome of 20 genes randomly ***");
        //Creating the Space
        VarLengthOperRefSpace refactorSpace = new VarLengthOperRefSpace(11, 20);
        List<RefactoringOperation> genome = refactorSpace.get();
        System.out.println(genome.toString());

        System.out.println("*** Generating a Deletion Gen operation with gen length of 10 ***");
        RefOperDelGen del = new RefOperDelGen(5, 5, 20, false);

        System.out.println("*** Applying the deletion ***");
        List<RefactoringOperation> gene = del.apply(genome);

        System.out.println("*** Mutated genome ***");
        System.out.println(gene);

    }
}
