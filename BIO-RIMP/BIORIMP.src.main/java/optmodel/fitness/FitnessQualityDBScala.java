package java.optmodel.fitness;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import scala.FitnessScalaApply;
import unalcol.optimization.OptimizationFunction;

import java.util.List;

/**
 * Created by david on 27/10/16.
 */
public class FitnessQualityDBScala extends OptimizationFunction<List<RefactoringOperation>> {
    @Override
    public Double apply(List<RefactoringOperation> x) {
        FitnessScalaApply fit = new FitnessScalaApply();
        return fit.gBiasQualitySystemRatio(x);
    }
}
