/**
 * 
 */
package optimization;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

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
		double GQSm = 0;
		try {
			LinkedHashMap<String, LinkedHashMap<String, Double>> actualMetrics =
					ActualMetrics(PredictingMetrics(x));
			printFitness(actualMetrics);
			
			LinkedHashMap<String, Double> bias = TotalActualMetrics(actualMetrics);
			printFitness2(bias);
			
			GQSm = GQSm(bias);
			
		} catch (ReadException | IOException | CompilUnitException | WritingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(x.size() == 1){
			//GQSm();
		}
		return GQSm;
	}
	
	//normalization and recives the weights
	private Double GQSm(LinkedHashMap<String, Double> bias){
		Double min = Collections.min(bias.values());
		Double max = Collections.max(bias.values());
		double fitness = 0;
		for( Entry<String, Double> metric : bias.entrySet() ){
			fitness = fitness 
					+ ( (metric.getValue() - min) / (max - min) );
		}
		return fitness;
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
	
	private LinkedHashMap<String, Double> TotalActualMetrics
		(LinkedHashMap<String, LinkedHashMap<String, Double>> actualMetrics){
		
		LinkedHashMap<String, Double> SUA_metric = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> SUA_prev_metric = new LinkedHashMap<String, Double>();
		
		for(Entry<String, LinkedHashMap<String, Double>> clase : actualMetrics.entrySet()){
			for(Entry<String, Double> metric : clase.getValue().entrySet()){
				//evaluate if the metric is repeat for summing
				if(SUA_metric.containsKey(metric.getKey())){
					SUA_metric.replace(metric.getKey(), SUA_metric.get(metric.getKey()), 
							SUA_metric.get(metric.getKey()) + metric.getValue());
				}else{
					SUA_metric.put(metric.getKey(), metric.getValue());
				}
			}
			
			//checking the class in prevMetrics
			if( prevMetrics.containsKey(clase.getKey()) ){
				//extracting prev metrics
				for(Entry<String, Double> metric : prevMetrics.get(clase.getKey()).entrySet()){
					//evaluate that the metric is impacted
					if(actualMetrics.get(clase.getKey()).containsKey(metric.getKey())){
						//evaluate if the metric is repeat for summing
						if(SUA_prev_metric.containsKey(metric.getKey())){
							SUA_prev_metric.replace(metric.getKey(), SUA_prev_metric.get(metric.getKey()), 
									SUA_prev_metric.get(metric.getKey()) + metric.getValue());
						}else{
							SUA_prev_metric.put(metric.getKey(), metric.getValue());
						}
					}
				}
			}else{
				for(Entry<String, Double> metric : clase.getValue().entrySet()){
					//evaluate if the metric is repeat for summing
					if(SUA_prev_metric.containsKey(metric.getKey())){
						SUA_prev_metric.replace(metric.getKey(), SUA_prev_metric.get(metric.getKey()), 
								SUA_prev_metric.get(metric.getKey()) + metric.getValue());
					}else{
						SUA_prev_metric.put(metric.getKey(), metric.getValue());
					}
				}
			}
		}
		
		//Figure out the bias by division of the accumulative sum 
		
		for( Entry<String, Double> metric : SUA_metric.entrySet() ){
			if( SUA_prev_metric.containsKey(metric.getKey()) ){
				SUA_metric.replace(metric.getKey(), metric.getValue(), 
						metric.getValue() / SUA_prev_metric.get(metric.getKey()) );
			}else{
				System.out.println("Something is wrong with prev_metrics");
			}
		}
		
		return SUA_metric;
	}
	
	private LinkedHashMap<String, LinkedHashMap<String, Double>> ActualMetrics(
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> prediction){
		//Average of all the metrics per class
		LinkedHashMap<String, LinkedHashMap<String, Double>> SUA = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		LinkedHashMap<String, Double> SUA_metric = new LinkedHashMap<String, Double>();	
		
		for(Entry<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> ref : prediction.entrySet()){
			for(Entry<String, LinkedHashMap<String, Double>> clase : ref.getValue().entrySet()){
				for(Entry<String, Double> metric : clase.getValue().entrySet()){
					SUA_metric.put(metric.getKey(), metric.getValue());
				}//Metric Loop
				if(!SUA.containsKey(clase.getKey())){
					SUA.put(clase.getKey(), SUA_metric);
				}else{
					//averague
					for(Entry<String, Double> metric_ : clase.getValue().entrySet()){
						if(SUA.get(clase.getKey()).containsKey(metric_.getKey())){
							//deciding the major value 
							if(metric_.getValue() >= SUA.get(clase.getKey()).get(metric_.getKey())){
								SUA.get(clase.getKey()).replace(metric_.getKey(), SUA.get(clase.getKey()).get(metric_.getKey()),
										metric_.getValue());
							}else{
								SUA.get(clase.getKey()).replace(metric_.getKey(), SUA.get(clase.getKey()).get(metric_.getKey()),
										SUA.get(clase.getKey()).get(metric_.getKey()));
							}
						}else{
							SUA.get(clase.getKey()).put(metric_.getKey(), metric_.getValue());
						}
							
					}//Metric Loop
				}
				SUA_metric = new LinkedHashMap<String, Double>();
			}//Clase Loop
		}//Ref Loop
		
		//for(int ref = 0; ref < prediction.size(); ref++){
		//	for(int clase = 0; clase < prediction.)
		//}//Ref Loop
		
		return SUA;
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
	
	private void printFitness(LinkedHashMap<String, LinkedHashMap<String, Double>> structureMetrics){
		for(Entry<String,LinkedHashMap<String, Double>> clase : structureMetrics.entrySet()){
			for(Entry<String,Double> metric_ : clase.getValue().entrySet()){
				System.out.println("[Class: "+ clase.getKey() +"] \t"
									+"[Metric: "+ metric_.getKey()+"] \t"
									+"[Value: "+metric_.getValue());
			}
		}
	}
	
	private void printFitness2(LinkedHashMap<String, Double> totalActualMetrics){
			for(Entry<String,Double> metric_ : totalActualMetrics.entrySet()){
				System.out.println("[Metric: "+ metric_.getKey()+"] \t"
									+"[Value: "+metric_.getValue()+"] \t");
			}
		
	}

}
