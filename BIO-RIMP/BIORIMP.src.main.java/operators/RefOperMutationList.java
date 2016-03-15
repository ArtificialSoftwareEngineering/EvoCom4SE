package operators;

import unalcol.random.util.*;
import unalcol.search.space.ArityOne;
import unalcol.types.collection.bitarray.BitArray;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import entity.QubitArray;
import entity.QubitRefactor;
import unalcol.clone.*;

/**
 * <p>Title: Mutation</p>
 * <p>Description: The simple bit mutation operator</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * @author danaderp
 * @version 1.0
 */

public class RefOperMutationList extends ArityOne<List<RefactoringOperation>> {
  /**
   * Probability of mutating one single bit
   */
  protected double bit_mutation_rate = 0.0;

  /**
   * Constructor: Creates a mutation with a mutation probability depending on the size of the genome
   */
  public RefOperMutationList() {}

  /**
   * Constructor: Creates a mutation with the given mutation rate
   * @param bit_mutation_rate Probability of mutating each single bit
   */
  public RefOperMutationList(double bit_mutation_rate) {
    this.bit_mutation_rate = bit_mutation_rate;
  }

  /**
   * Flips a bit in the given genome
   * @param gen Genome to be modified
   * @return Number of mutated bits
   */

  @Override
  public List<RefactoringOperation> apply(List<RefactoringOperation> x) {
	  try{
	      List<RefactoringOperation> genome = (List<RefactoringOperation>) Clone.create( x );
	      double rate = 1.0 - ((bit_mutation_rate == 0.0)?1.0/genome.size():bit_mutation_rate);
	      RandBool g = new RandBool(rate);
	      RefOperMutationList variation = new RefOperMutationList();
	      for (int i = 0; i < genome.size(); i++) {
	        if (g.next()) {
	          //genome.not(i);
	          //genome.set(i, variation.apply(genome.get(i))); Should be replaced here
	        }
	      }
	      return genome;
	    }catch( Exception e ){ 
	        e.printStackTrace();
	        System.err.println("[Mutation]"+e.getMessage()); }
	    return null;
  }

 /**
  * Testing function
  */
  public static void main(String[] argv){
    System.out.println("*** Generating a genome of 21 genes randomly ***");
    List<QubitRefactor> genome = new ArrayList<QubitRefactor>();
    		//new QubitRefactor(true);
    for(int i = 0; i <= 21; i++){
    	genome.add(i,new QubitRefactor(true,4));
    	System.out.println("["+i+"] : "+genome.get(i).getGenObservation().toString());
    }
   

    //ListRefOperMutation mutation = new ListRefOperMutation(0.05);
    RefOperMutationList mutation = new RefOperMutationList(0.5);

    System.out.println("*** Applying the mutation ***");
    //List<QubitRefactor> mutated = mutation.apply(genome);
    System.out.println("Mutated array ");
    
    for(int i = 0; i <= 21; i++){
    	//System.out.println("["+i+"] : "+mutated.get(i).getGenObservation().toString());
    }
  }




}
