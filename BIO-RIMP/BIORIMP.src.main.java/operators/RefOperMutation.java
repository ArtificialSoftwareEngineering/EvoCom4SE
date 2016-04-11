package operators;

import unalcol.random.integer.IntUniform;
import unalcol.random.util.*;
import unalcol.search.space.ArityOne;
import unalcol.types.collection.bitarray.BitArray;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.opers.*;
import edu.wayne.cs.severe.redress2.entity.refactoring.opers.RefactoringType;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import entity.QubitArray;
import entity.QubitRefactor;
import space.GeneratingRefactorEC;
import space.GeneratingRefactorEM;
import space.GeneratingRefactorIM;
import space.GeneratingRefactorMF;
import space.GeneratingRefactorMM;
import space.GeneratingRefactorPDF;
import space.GeneratingRefactorPDM;
import space.GeneratingRefactorPUF;
import space.GeneratingRefactorPUM;
import space.GeneratingRefactorRDI;
import space.GeneratingRefactorRID;
import space.GeneratingRefactorRMMO;
import space.Refactoring;
import space.RefactoringOperationSpace;
import unalcol.clone.*;

/**
 * <p>Title: Mutation</p>
 * <p>Description: The simple bit mutation operator</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * @author danaderp
 * @version 1.0
 */

public class RefOperMutation extends ArityOne< List<RefactoringOperation> > {
	/**
	 * Probability of mutating one single bit
	 */
	protected double bit_mutation_rate = 0.0;
	private MetaphorCode metaphor;

	/**
	 * Constructor: Creates a mutation with a mutation probability depending on the size of the genome
	 */
	public RefOperMutation() {}

	/**
	 * Constructor: Creates a mutation with the given mutation rate
	 * @param bit_mutation_rate Probability of mutating each single bit
	 */
	public RefOperMutation(double bit_mutation_rate, MetaphorCode metaphor) {
		this.bit_mutation_rate = bit_mutation_rate;
		this.metaphor = metaphor;
	}
	
	public RefOperMutation( MetaphorCode metaphor) {
		this.metaphor = metaphor;
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
			RefactoringOperation refOper;
			
			final int DECREASE = 5;
			IntUniform r = new IntUniform ( Refactoring.values().length - DECREASE);
			RefactoringType refType = null;

			for (int i = 0; i < genome.size(); i++) {
				if (g.next()) {	        	
					switch( r.generate() ){
					case 0:
						refType = new ExtractClass( metaphor.getSysTypeDcls(), metaphor.getLang(), metaphor.getBuilder() );
						break;
					case 1:
						refType = new MoveMethod( metaphor.getSysTypeDcls() , metaphor.getBuilder() );
						break;
					case 2:
						refType = new ReplaceMethodObject( metaphor.getSysTypeDcls(), metaphor.getLang(), metaphor.getBuilder() );
						break;
					case 3:
						refType = new ReplaceDelegationInheritance( metaphor.getSysTypeDcls() , metaphor.getBuilder() );
						break;
					case 4:
						refType = new MoveField( metaphor.getSysTypeDcls(), metaphor.getLang() );
						break;
					case 5:
						refType = new ExtractMethod( metaphor.getSysTypeDcls(), metaphor.getLang() );
						break;
					case 6:
						refType = new InlineMethod( metaphor.getSysTypeDcls(), metaphor.getLang() );
						break;
					case 7:
						refType = new ReplaceInheritanceDelegation( metaphor.getSysTypeDcls() , metaphor.getBuilder() );
						break;
					case 8:
						refType = new PushDownMethod( metaphor.getSysTypeDcls() , metaphor.getBuilder() );
						break;
					case 9:
						refType = new PullUpMethod( metaphor.getSysTypeDcls(), metaphor.getLang(), metaphor.getBuilder() );
						break;
					case 10:
						refType = new PushDownField( metaphor.getSysTypeDcls(), metaphor.getLang() );
						break;
					case 11:
						refType = new PullUpField( metaphor.getSysTypeDcls() );
						break;
					}//END CASE

					refOper = new RefactoringOperation( refType, genome.get(i).getParams(),  
							refType.getAcronym(), genome.get(i).getSubRefs(), genome.get(i).isFeasible() );
					genome.set( i , refOper );
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
		if(refactor != null)
			for( RefactoringOperation refOper : refactor ){
				System.out.println( "Random Refactor: "+ refOper.toString() );
			}


		//ListRefOperMutation mutation = new ListRefOperMutation(0.05);
		RefOperMutation mutation = new RefOperMutation( 0.5, metaphor );

		System.out.println("*** Applying the mutation ***");
		List<RefactoringOperation> mutated = mutation.apply( refactor );

		System.out.println("Mutated array ");
		if(mutated != null)
			for( RefactoringOperation refOper : mutated ){
				System.out.println( "Random Refactor: "+ refOper.toString() );
			}
	}

}
