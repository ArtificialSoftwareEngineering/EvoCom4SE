/**
 * 
 */
package optimization;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import edu.wayne.cs.severe.redress2.controller.MetricCalculator;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.exception.CompilUnitException;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import edu.wayne.cs.severe.redress2.exception.WritingException;
import edu.wayne.cs.severe.redress2.io.MetricsReader;
import entity.MetaphorCode;
import unalcol.optimization.OptimizationFunction;

/**
 * @author dnader
 *
 */
public class GeneralizedImpactQuality extends OptimizationFunction<List<RefactoringOperation>>{
	
	MetaphorCode metaphor;
	LinkedHashMap<String, LinkedHashMap<String, Double>> prevMetrics;
	
	
	
	public GeneralizedImpactQuality(MetaphorCode metaphor){
		this.metaphor = metaphor;
		PreviMetrics();
	}
	
	@Override
	public Double apply(List<RefactoringOperation> x) {
		// TODO Auto-generated method stub
		try {

			LinkedHashMap<String, LinkedHashMap<String, Double>> actualMetrics =
					ActualMetrics(PredictingMetrics(x));
		} catch (ReadException | IOException | CompilUnitException | WritingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(x.size() == 1){
			//GQSm();
		}
		return null;
	}
	
	private Double GQSm(RefactoringOperation delta){
		for(int j = 0; j < metaphor.getMetrics().size(); j++){
			for(int alfa = 0; alfa < metaphor.getSysTypeDcls().size(); alfa++){
				
			}
		}//Loop of metrics
		return null;
	}
	
	private void PreviMetrics() {
		System.out.println("Reading previous metrics");
		MetricsReader metReader = new MetricsReader(metaphor.getSystemPath(), metaphor.getSysName());
		try {
			prevMetrics = metReader.readMetrics();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private LinkedHashMap<String, LinkedHashMap<String, Double>> ActualMetrics(
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> prediction){
		return null;
	}
	
	private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> 
		PredictingMetrics(List<RefactoringOperation> operations)
			throws ReadException, IOException,
			CompilUnitException, WritingException{

		System.out.println("Predicting metrics");
		
		MetricCalculator calc = new MetricCalculator();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> predictMetrics = calc
		.predictMetrics(operations, metaphor.getMetrics(), prevMetrics);
		
		return predictMetrics;
		
	}

}
