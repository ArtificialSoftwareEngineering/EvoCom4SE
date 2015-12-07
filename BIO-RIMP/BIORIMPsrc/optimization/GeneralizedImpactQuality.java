/**
 * 
 */
package optimization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
public class GeneralizedImpactQuality extends OptimizationFunction<List<RefactoringOperation>> {

	MetaphorCode metaphor;
	LinkedHashMap<String, LinkedHashMap<String, Double>> prevMetrics;
	String file;
	
	public GeneralizedImpactQuality(MetaphorCode metaphor , String file) {
		this.metaphor = metaphor;
		this.file = file; 
		PreviMetrics();
	}

	@Override
	public Double apply(List<RefactoringOperation> x) {
		// TODO Auto-generated method stub
		double GQSm_ = 0;
		try {
			LinkedHashMap<String, LinkedHashMap<String, Double>> predictedMetrics = ActualMetrics( PredictingMetrics( x ) );
			//printFitness( predictedMetrics );

			//LinkedHashMap<String, Double> bias = TotalActualMetrics( predictedMetrics );
			//printFitness2(bias);
			//GQSm_ = GQSm(bias); //First calculate proneness per metric and then normalize
			
			GQSm_ = GQSproneness( predictedMetrics ); //First normalize and then calculate proneness
			
		} catch (ReadException | IOException | CompilUnitException | WritingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (x.size() == 1) {
			// GQSm();
		}
		return GQSm_;
	}

	// normalization and receives the weights
	// FIXME VECTOR DEFAULT INCOMPLETE
	private Double GQSm(LinkedHashMap<String, Double> bias) {
		Double min = Collections.min(bias.values());
		Double max = Collections.max(bias.values());
		double fitness = 0;
		double W[] = new double[ bias.size() ];
		double w = (double)1 / (double)bias.size();
		System.out.println("BIAS SIZE: {" + w +"}");
		for (Entry<String, Double> metric : bias.entrySet()) {
			
			fitness = fitness + (w *((metric.getValue() - min) / (max - min)));
			System.out.println("FITNESS: {" + fitness +"} | {"+((metric.getValue() - min) / (max - min))+"}");
		}
		System.out.println("FITNESS FINAL: {" + fitness +"}");

		return fitness;
	}	
	
	private Double GQSproneness( LinkedHashMap<String, LinkedHashMap<String, Double>> metricActualVector ){
		double generalQuality = 0.0;
		double denominator = 0.0;
		double numerator = 0.0;

		LinkedHashMap<String, Double> SUA_metric = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> SUA_prev_metric = new LinkedHashMap<String, Double>();

		for ( Entry<String, LinkedHashMap<String, Double>> clase : metricActualVector.entrySet() ) {
			//1. Adding predicting metrics
			for ( Entry<String, Double> metric : clase.getValue().entrySet() ) {
				// evaluate if the metric is repeat for summing
				if ( SUA_metric.containsKey( metric.getKey() ) ) {
					SUA_metric.replace( metric.getKey() , 
							SUA_metric.get( metric.getKey() ),
							SUA_metric.get( metric.getKey() ) + metric.getValue() );
				} else {
					SUA_metric.put( metric.getKey(), metric.getValue() );
				}
			}

			//2. Checking the class in prevMetrics
			if ( prevMetrics.containsKey( clase.getKey() ) ) {
				// Extracting prevMetrics
				for ( Entry<String, Double> metric : prevMetrics.get( clase.getKey() ).entrySet() ) {
					// Evaluate that the metric is impacted
					if ( metricActualVector.get(clase.getKey()).containsKey( metric.getKey() ) ) {
						// Evaluate if the metric is repeat for summing
						if (SUA_prev_metric.containsKey(metric.getKey())) {
							SUA_prev_metric.replace(metric.getKey(), 
									SUA_prev_metric.get(metric.getKey()),
									SUA_prev_metric.get(metric.getKey()) + metric.getValue());
						} else {
							SUA_prev_metric.put( metric.getKey(), metric.getValue() );
						}
					}
				}
			} else {
				//For new classes
				//Commented cause it is not necessary adding new classes metrics to the vector
				/*
				for ( Entry<String, Double> metric : clase.getValue().entrySet() ) {
					// evaluate if the metric is repeat for summing
					if ( SUA_prev_metric.containsKey( metric.getKey() ) ) {
						SUA_prev_metric.replace( metric.getKey(), 
												SUA_prev_metric.get(metric.getKey()),
												SUA_prev_metric.get(metric.getKey()) + metric.getValue());
					} else {
						SUA_prev_metric.put( metric.getKey(), metric.getValue() );
					}
				}*/
			}
		}//End Loop Clase
		
		Double min = Collections.min( SUA_metric.values() );
		Double max = Collections.max( SUA_metric.values() );
		
		Double minPrev = Collections.min( SUA_prev_metric.values() );
		Double maxPrev = Collections.max( SUA_prev_metric.values() );
		
		double W[] = new double[ SUA_metric.size() ];
		double w = (double)1 / (double)SUA_metric.size();

		for ( Entry<String, Double> metric : SUA_prev_metric.entrySet() ) {
			if ( SUA_metric.containsKey( metric.getKey() ) ) {
				//Accumulate the metrics
				numerator = numerator + (w *(( SUA_metric.get( metric.getKey() ) - min) / (max - min)));
				denominator = denominator + (w *((metric.getValue() - minPrev) / (maxPrev - minPrev)));			

			} else {
				System.out.println("Something is wrong with prev_metrics");
			}
		}
		System.out.println("Numerador: "+ numerator );
		System.out.println("Denominador: "+  denominator );
		generalQuality = numerator/denominator;
		System.out.println("Proneness[FITNESS]: "+  generalQuality  );
		
		escribirTextoArchivo( String.valueOf(generalQuality) + "\r\n");
		
		return generalQuality;

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

	private LinkedHashMap<String, Double> TotalActualMetrics(
			LinkedHashMap< String, LinkedHashMap<String, Double> > actualMetrics ) {

		LinkedHashMap<String, Double> SUA_metric = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> SUA_prev_metric = new LinkedHashMap<String, Double>();

		for ( Entry<String, LinkedHashMap<String, Double>> clase : actualMetrics.entrySet() ) {
			//1. Adding predicting metrics
			for ( Entry<String, Double> metric : clase.getValue().entrySet() ) {
				// evaluate if the metric is repeat for summing
				if ( SUA_metric.containsKey( metric.getKey() ) ) {
					SUA_metric.replace( metric.getKey() , 
										SUA_metric.get( metric.getKey() ),
										SUA_metric.get( metric.getKey() ) + metric.getValue() );
				} else {
					SUA_metric.put( metric.getKey(), metric.getValue() );
				}
			}

			//Checking the class in prevMetrics
			if ( prevMetrics.containsKey( clase.getKey() ) ) {
				// Extracting prevMetrics
				for ( Entry<String, Double> metric : prevMetrics.get( clase.getKey() ).entrySet() ) {
					// evaluate that the metric is impacted
					if ( actualMetrics.get(clase.getKey()).containsKey(metric.getKey()) ) {
						// evaluate if the metric is repeat for summing
						if (SUA_prev_metric.containsKey(metric.getKey())) {
							SUA_prev_metric.replace(metric.getKey(), 
													SUA_prev_metric.get(metric.getKey()),
													SUA_prev_metric.get(metric.getKey()) + metric.getValue());
						} else {
							SUA_prev_metric.put( metric.getKey(), metric.getValue() );
						}
					}
				}
			} else {
				//For new classes
				//Commented cause it is not necessary adding new classes metrics to the vector
				/*
				for ( Entry<String, Double> metric : clase.getValue().entrySet() ) {
					// evaluate if the metric is repeat for summing
					if ( SUA_prev_metric.containsKey( metric.getKey() ) ) {
						SUA_prev_metric.replace( metric.getKey(), 
												SUA_prev_metric.get(metric.getKey()),
												SUA_prev_metric.get(metric.getKey()) + metric.getValue());
					} else {
						SUA_prev_metric.put( metric.getKey(), metric.getValue() );
					}
				}*/
			}
		}//End Loop Clase

		// Figure out the bias by division of the accumulative sum
		/*for ( Entry<String, Double> metric : SUA_metric.entrySet() ) {
			if ( SUA_prev_metric.containsKey( metric.getKey() ) ) {
				SUA_metric.replace( metric.getKey(), 
									metric.getValue(),
									metric.getValue() / SUA_prev_metric.get( metric.getKey() ) );
			} else {
				System.out.println("Something is wrong with prev_metrics");
			}
		}*/

		for ( Entry<String, Double> metric : SUA_prev_metric.entrySet() ) {
			if ( SUA_metric.containsKey( metric.getKey() ) ) {
				System.out.println("["+ metric.getKey() +"]Numerador: "+ SUA_metric.get( metric.getKey() ) );
				System.out.println("Denominador: "+  metric.getValue() );
				//Verification of no zero in the denominator
				if( metric.getValue() == 0 )
					SUA_prev_metric.replace( metric.getKey() , 0.0000000000000001); //replace for avoiding NaN
				SUA_metric.replace( metric.getKey(), 
						SUA_metric.get( metric.getKey() ) ,
						SUA_metric.get( metric.getKey() ) /  metric.getValue() );

				System.out.println("Proneness: "+  SUA_metric.get( metric.getKey() )  );

			} else {
				System.out.println("Something is wrong with prev_metrics");
			}
		}

		return SUA_metric;
	}
	
	//Organized the prediction and reduce the data
	private LinkedHashMap<String, LinkedHashMap<String, Double>> ActualMetrics(
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> prediction ) {
		// Average of all the metrics per class
		LinkedHashMap<String, LinkedHashMap<String, Double>> SUA = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		LinkedHashMap<String, Double> SUA_metric = new LinkedHashMap<String, Double>();

		for (Entry<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> ref : prediction.entrySet()) {
			for (Entry<String, LinkedHashMap<String, Double>> clase : ref.getValue().entrySet()) {
				//Add metrics per class to SUA_metric 
				for (Entry<String, Double> metric : clase.getValue().entrySet()) {
					SUA_metric.put(metric.getKey(), metric.getValue());
				} 
				// Class Loop
				if ( !SUA.containsKey( clase.getKey() ) ) { //Evaluating if SUA contains the class
					SUA.put(clase.getKey(), SUA_metric);	//Adding to SUA if do not contains the class
				} else {
					//Metric Loop
					for ( Entry<String, Double> metric_ : clase.getValue().entrySet() ) {
						if ( SUA.get( clase.getKey() ).containsKey( metric_.getKey() ) ) { //If the SUA class contains already the metric
							// Deciding the maximum value
							if ( metric_.getValue() >= SUA.get(clase.getKey()).get(metric_.getKey()) ) {
								SUA.get(clase.getKey()).replace(metric_.getKey(),
										SUA.get(clase.getKey()).get(metric_.getKey()), metric_.getValue());
							} else {
								SUA.get(clase.getKey()).replace(metric_.getKey(),
										SUA.get(clase.getKey()).get(metric_.getKey()),
										SUA.get(clase.getKey()).get(metric_.getKey()));
							}
						} else {
							SUA.get( clase.getKey() ).put( metric_.getKey() , metric_.getValue() );
						}

					} // Metric Loop
				}
				SUA_metric = new LinkedHashMap<String, Double>();
			} // Clase Loop
		} // Ref Loop

		// for(int ref = 0; ref < prediction.size(); ref++){
		// for(int clase = 0; clase < prediction.)
		// }//Ref Loop

		return SUA;
	}
	
	//Redress is called here for prediction
	private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> PredictingMetrics(
			List<RefactoringOperation> operations)
					throws ReadException, IOException, CompilUnitException, WritingException {

		System.out.println("Predicting metrics");

		MetricCalculator calc = new MetricCalculator();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> predictMetrics = calc
				.predictMetrics(operations, metaphor.getMetrics(), prevMetrics);

		return predictMetrics;

	}

	private void printFitness(LinkedHashMap<String, LinkedHashMap<String, Double>> structureMetrics) {
		for (Entry<String, LinkedHashMap<String, Double>> clase : structureMetrics.entrySet()) {
			for (Entry<String, Double> metric_ : clase.getValue().entrySet()) {
				System.out.println("[Class: " + clase.getKey() + "] \t" + "[Metric: " + metric_.getKey() + "] \t"
						+ "[Value: " + metric_.getValue());
			}
		}
	}

	private void printFitness2(LinkedHashMap<String, Double> totalActualMetrics) {
		for (Entry<String, Double> metric_ : totalActualMetrics.entrySet()) {
			System.out.println("[Metric: " + metric_.getKey() + "] \t" + "[Value: " + metric_.getValue() + "] \t");
		}

	}

	public void escribirTextoArchivo( String texto ) {
		String ruta = file + "_TEST_FITNESS_JAR.txt";
		try(FileWriter fw=new FileWriter( ruta , true );
				FileReader fr=new FileReader( ruta )){
			//Escribimos en el fichero un String y un caracter 97 (a)
			fw.write( texto );
			//fw.write(97);
			//Guardamos los cambios del fichero
			fw.flush();
		}catch(IOException e){
			System.out.println("Error E/S: "+e);
		}

	}

	
}
