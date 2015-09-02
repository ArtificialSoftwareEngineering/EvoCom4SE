package test;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;
import entity.QubitRefactor;
import optimization.CodeDecodeRefactorList;
import optimization.GeneralizedImpactQuality;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.binary.testbed.Deceptive;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.types.collection.bitarray.BitArray;

public class testFitness {

	public static void main(String[] argss) {
		// TODO Auto-generated method stub
		//Getting the Metaphor
		String userPath = System.getProperty("user.dir");
        String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\optimization\\src","-s", "     optimization      " };
        
		
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM ();
        init.main(args);
        MetaphorCode metaphor = new MetaphorCode(init);
		//Creating the individual
		List<QubitRefactor> genome = new ArrayList<QubitRefactor>();
		List<RefactoringOperation> phe = new ArrayList<RefactoringOperation>();
		for(int i = 0; i < 20; i++){
			genome.add(new QubitRefactor(true));
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
		
		//Processing Fitness
		OptimizationFunction<List<RefactoringOperation>> function = new GeneralizedImpactQuality(metaphor);	
		function.apply(phe);
	}

}
