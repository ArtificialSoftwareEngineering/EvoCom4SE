package operators;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import space.RefactoringOperationSpace;
import unalcol.clone.*;
import unalcol.random.raw.RawGenerator;
import unalcol.search.population.variation.ArityTwo;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.vector.*;

/**
 * <p>Title: XOver</p>
 * <p>Description: The simple point crossover operator (variable length)</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * @author Jonatan Gomez
 * @version 1.0
 */

public class RefOperXOver extends ArityTwo< List<RefactoringOperation> > {
    public RefOperXOver(){}

  /**
   * The crossover point of the last xover execution
   */
  protected int cross_over_point;

  /**
   * Apply the simple point crossover operation over the given genomes at the given
   * cross point
   * @param child1 The first parent
   * @param child2 The second parent
   * @param xoverPoint crossover point
   * @return The crossover point
   */
  public Vector< List<RefactoringOperation> > generates(List<RefactoringOperation> child1, List<RefactoringOperation> child2, int xoverPoint) {
	  List<RefactoringOperation> child1_1 = (List<RefactoringOperation>)Clone.create(child1);
	  List<RefactoringOperation> child2_1 = (List<RefactoringOperation>)Clone.create(child2);

      cross_over_point = xoverPoint;

      for(int i = xoverPoint ; i<child1.size() ; i++){
          child1_1.set( i, child2.get(i) );
          child2_1.set( i, child1.get(i) );              
      }
      
      Vector<List<RefactoringOperation>> v = new Vector<List<RefactoringOperation>>();
      v.add(child1_1);
      v.add(child2_1);
      return v;
  }

  /**
   * Apply the simple point crossover operation over the given genomes
   * @param child1 The first parent
   * @param child2 The second parent
   * @return The crossover point
   */
  @Override
  public Vector< List<RefactoringOperation> > apply( List<RefactoringOperation> child1, List<RefactoringOperation> child2 ){
	  return generates(child1, child2, RawGenerator.integer(this, Math.min(child1.size(), child2.size())));
  }
  /**
   * Testing function
   */
  public static void main(String[] argv){
	  //Getting the Metaphor
	  String userPath = System.getProperty("user.dir");
	  String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\optimization\\src","-s", "     optimization      " };        
	  MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM ();
	  init.main(args);
	  MetaphorCode metaphor = new MetaphorCode(init);

	  System.out.println("*** Generating a genome of 10 genes randomly ***");

	  //Creating the Space
	  RefactoringOperationSpace refactorSpace = new RefactoringOperationSpace( 10, metaphor );

	  //Visualizing the get() Space
	  List<RefactoringOperation> refactor = refactorSpace.get();  

	  System.out.println("*** Generating a genome of 20 genes randomly ***");
	  List<RefactoringOperation> parent1 = refactorSpace.get();  
	  System.out.println(parent1.toString());

	  System.out.println("*** Generating a genome of 20 genes randomly ***");
	  List<RefactoringOperation> parent2 = refactorSpace.get();  
	  System.out.println(parent2.toString());

	  RefOperXOver xover = new RefOperXOver();

	  System.out.println("*** Applying the croosover ***");
	  Vector< List<RefactoringOperation> > kids = xover.apply(parent1, parent2);

	  System.out.println("*** Child 1 ***");
	  System.out.println(kids.get(0).toString());
	  System.out.println("*** Child 2 ***");
	  System.out.println(kids.get(1).toString());

  }
}
