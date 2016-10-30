package javabio.optmodel.fitness;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import scalabio.FitnessScalaApply;
import unalcol.optimization.OptimizationFunction;

import java.util.List;

/**
 * Created by david on 27/10/16.
 */


public class FitnessQualityDBScala extends OptimizationFunction<List<RefactoringOperation>> {

    String file;

    public FitnessQualityDBScala(String file){
        this.file = file;
    }

    @Override
    public Double apply(List<RefactoringOperation> x) {
        FitnessScalaApply fit = new FitnessScalaApply();
        return fit.gBiasQualitySystemRatio(x);
    }
}
