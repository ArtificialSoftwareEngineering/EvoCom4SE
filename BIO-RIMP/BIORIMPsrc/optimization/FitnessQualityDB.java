/**
 *
 */
package optimization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import edu.wayne.cs.severe.redress2.controller.MetricCalculator;
import edu.wayne.cs.severe.redress2.entity.*;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.exception.CompilUnitException;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import edu.wayne.cs.severe.redress2.exception.WritingException;
import edu.wayne.cs.severe.redress2.io.MetricsReader;
import entities.Register;
import entity.MetaphorCode;
import repositories.RegisterRepository;
import unalcol.clone.Clone;
import unalcol.optimization.OptimizationFunction;

/**
 * @author dnader
 */
public class FitnessQualityDB extends OptimizationFunction<List<RefactoringOperation>> {

    MetaphorCode metaphor;
    LinkedHashMap<String, LinkedHashMap<String, Double>> prevMetrics;
    String file;
    //Field for memoization
    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> predictMetrics = new
            LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>();
	LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> predictMetricsMemorizar = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>();
	LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> predictMetricsRecordar = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>();

	public void memorizar(RefactoringOperation operRef) {

		// Verificaciï¿½n de llaves
		String src = "";
		String tgt = "";
		String fld = "-1", mtd;
		if (operRef.getParams() != null) {
			// si es un extract class memoriza sub-refs
			String acronym = operRef.getRefType().getAcronym();
			if (acronym.equals("EC")) {
				// 1. Extracting src from subrefs
				if (operRef.getSubRefs().get(0).getParams().get("src") != null) {
					if (!operRef.getSubRefs().get(0).getParams().get("src").isEmpty()) {// valida
																						// si
																						// es
																						// vacï¿½o
						for (RefactoringParameter obj : operRef.getSubRefs().get(0).getParams().get("src")) {
							src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";
						}
						src = src.substring(0, src.length() - 1);

					}
				}

				// 2. Extracting fld from subrefs
				if (operRef.getSubRefs().get(0).getParams().get("fld") != null) {
					if (!operRef.getSubRefs().get(0).getParams().get("fld").isEmpty()) // valida
																						// si
																						// es
																						// vacï¿½o
						fld = ((AttributeDeclaration) operRef.getSubRefs().get(0).getParams().get("fld").get(0)
								.getCodeObj()).getObjName();
					else
						fld = "-1";
				} else {
					fld = "-1";
				}
				// 3. Extracting mtd from subrefs
				if (operRef.getSubRefs().get(1).getParams().get("mtd") != null) {
					if (!operRef.getSubRefs().get(1).getParams().get("mtd").isEmpty())
						mtd = ((MethodDeclaration) operRef.getSubRefs().get(1).getParams().get("mtd").get(0)
								.getCodeObj()).getObjName();
					else
						mtd = "-1";
				} else {
					mtd = "-1";
				}
			} else {
				if (acronym.equals("RMMO")) {
					// 1. Extracting src from subrefs
					if (operRef.getParams().get("src") != null) {
						if (!operRef.getParams().get("src").isEmpty()) {// valida
																		// si es
																		// vacï¿½o
							for (RefactoringParameter obj : operRef.getParams().get("src")) {
								src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";
							}
							src = src.substring(0, src.length() - 1);

						}
					}

					// 2. Extracting mtd from subrefs
					if (operRef.getParams().get("mtd") != null) {
						if (!operRef.getParams().get("mtd").isEmpty())
							mtd = ((MethodDeclaration) operRef.getParams().get("mtd").get(0).getCodeObj()).getObjName();
						else
							mtd = "-1";
					} else {
						mtd = "-1";
					}
				} else {
					if (operRef.getParams().get("src") != null) {
						if (!operRef.getParams().get("src").isEmpty()) {// valida
																		// si es
																		// vacï¿½o
							for (RefactoringParameter obj : operRef.getParams().get("src")) {
								src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";
							}
							src = src.substring(0, src.length() - 1);

						}
					}
					if (operRef.getParams().get("tgt") != null) {
						if (!operRef.getParams().get("tgt").isEmpty()) {// valida
																		// si es
																		// vacï¿½o
							for (RefactoringParameter obj : operRef.getParams().get("tgt")) {
								tgt += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";
							}
							tgt = tgt.substring(0, tgt.length() - 1);

						}
					}

					if (operRef.getParams().get("fld") != null) {
						if (!operRef.getParams().get("fld").isEmpty()) // valida
																		// si es
																		// vacï¿½o
							fld = ((AttributeDeclaration) operRef.getParams().get("fld").get(0).getCodeObj())
									.getObjName();
						else
							fld = "-1";
					} else {
						fld = "-1";
					}

					if (operRef.getParams().get("mtd") != null) {
						if (!operRef.getParams().get("mtd").isEmpty())
							mtd = ((MethodDeclaration) operRef.getParams().get("mtd").get(0).getCodeObj()).getObjName();
						else
							mtd = "-1";
					} else {
						mtd = "-1";
					}
				}
			}
			// Termina Extracción

			// Se escribe en el fichero la predicciï¿½n
			for (Entry<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> ref : predictMetricsMemorizar
					.entrySet()) {
				for (Entry<String, LinkedHashMap<String, Double>> clase : ref.getValue().entrySet()) {
					// Add metrics per class to SUA_metric
					for (Entry<String, Double> metric : clase.getValue().entrySet()) {

						String id_ref = ref.getKey();
						if (id_ref.contains("-"))
							id_ref = ref.getKey().substring(0, ref.getKey().indexOf("-"));
						if (!id_ref.equals(operRef.getRefType().getAcronym()))
							continue;
						double val = metric.getValue();

						Register register = new Register(id_ref, metric.getKey(), val, src, tgt, fld, mtd,
								clase.getKey());
						RegisterRepository repo = new RegisterRepository();
						repo.insertRegister(register);
					}
				}
			}

		}

		predictMetricsMemorizar = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>();
	}

    public boolean recordar(RefactoringOperation operRef) {
        boolean bandera = true;

        String clase = "";
        //Verificaciï¿½n de llaves
        String src = "";
        String tgt = "";
        String fld, mtd;
        String acronym = operRef.getRefType().getAcronym();
        if (operRef.getParams() != null) {
			// si es un extract class memoriza sub-refs
			if (acronym.equals("EC")) {
				//1.Extracting src from subrefs
				if (operRef.getSubRefs().get(0).getParams().get("src") != null) {
					if (!operRef.getSubRefs().get(0).getParams().get("src").isEmpty()) { // valida
																		// si es
																		// vacï¿½o
						for (RefactoringParameter obj : operRef.getSubRefs().get(0).getParams().get("src")) {
							src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";// 45,67
						}
						src = src.substring(0, src.length() - 1);
					}
				}
				//2.Extracting fld from subrefs
				if (operRef.getSubRefs().get(0).getParams().get("fld") != null) {
					if (!operRef.getSubRefs().get(0).getParams().get("fld").isEmpty())
						fld = ((AttributeDeclaration) operRef.getSubRefs().get(0).getParams().get("fld").get(0).getCodeObj()).getObjName();
					else
						fld = "-1";
				} else {
					fld = "-1";
				}
				
				//3.Extracting mtd from subrefs
				if (operRef.getSubRefs().get(1).getParams().get("mtd") != null) {
					if (!operRef.getSubRefs().get(1).getParams().get("mtd").isEmpty())
						mtd = ((MethodDeclaration) operRef.getSubRefs().get(1).getParams().get("mtd").get(0).getCodeObj()).getObjName();
					else
						mtd = "-1";
				} else {
					mtd = "-1";
				}

			} else {

				if (operRef.getParams().get("src") != null) {
					if (!operRef.getParams().get("src").isEmpty()) { // valida
																		// si es
																		// vacï¿½o
						for (RefactoringParameter obj : operRef.getParams().get("src")) {
							src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";// 45,67
						}
						src = src.substring(0, src.length() - 1);
					}
				}

				if (operRef.getParams().get("tgt") != null) {
					if (!operRef.getParams().get("tgt").isEmpty()) {
						for (RefactoringParameter obj : operRef.getParams().get("tgt")) {
							tgt += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";
						}
						tgt = tgt.substring(0, tgt.length() - 1);
					}
				}

				if (operRef.getParams().get("fld") != null) {
					if (!operRef.getParams().get("fld").isEmpty())
						fld = ((AttributeDeclaration) operRef.getParams().get("fld").get(0).getCodeObj()).getObjName();
					else
						fld = "-1";
				} else {
					fld = "-1";
				}

				if (operRef.getParams().get("mtd") != null) {
					if (!operRef.getParams().get("mtd").isEmpty())
						mtd = ((MethodDeclaration) operRef.getParams().get("mtd").get(0).getCodeObj()).getObjName();
					else
						mtd = "-1";
				} else {
					mtd = "-1";
				}
			}

            RegisterRepository repo = new RegisterRepository();
            List<Register> listMetric = new ArrayList<>();
            if (acronym.equals("EM") || acronym.equals("IM") || acronym.equals("RMMO")) {//-> Only matters src + mtd
                listMetric = repo.getRegistersByClass(acronym, src, "", mtd, "");
            } else if (acronym.equals("MF") || acronym.equals("PDF") || acronym.equals("PUF")) {//->Only matters src+tgt+fld
                listMetric = repo.getRegistersByClass(acronym, src, tgt, "", fld);
            } else if (acronym.equals("MM") || acronym.equals("PDM") || acronym.equals("PUM")) {//->Only matters src+mtd+tgt
                listMetric = repo.getRegistersByClass(acronym, src, tgt, mtd, "");
            } else if (acronym.equals("RDI") || acronym.equals("RID")) {//->Only matters src+tgt
                listMetric = repo.getRegistersByClass(acronym, src, tgt, "", "");
            } else if (acronym.equals("EC")) {//->Only matters src+fld+mtd
                listMetric = repo.getRegistersByClass(acronym, src, "", mtd, fld);
            }


            LinkedHashMap<String, Double> metricList = new LinkedHashMap<String, Double>();
            LinkedHashMap<String, LinkedHashMap<String, Double>> clasesList = new
                    LinkedHashMap<String, LinkedHashMap<String, Double>>();
            //clase = ((TypeDeclaration) operRef.getParams().get("src").get(0).getCodeObj()).getQualifiedName();

            bandera = !listMetric.isEmpty();
            if (bandera) {

                clase = listMetric.get(0).getClasss();
                for (Register reg : listMetric) {
                    if (!clase.equals(reg.getClasss())) {
                        clasesList.put(clase, metricList);
                        clase = reg.getClasss();
                        metricList = new LinkedHashMap<String, Double>();
                    }
                    metricList.put(reg.getMetric(), reg.getValue());

                }
                clasesList.put(clase, metricList);
                predictMetricsRecordar.put(operRef.getRefId(), clasesList);
            }


		} else { // if no params, no recall unless EC
			if (acronym.equals("EC")) {
				// 1.Extracting src from subrefs
				if (operRef.getSubRefs().get(0).getParams().get("src") != null) {
					if (!operRef.getSubRefs().get(0).getParams().get("src").isEmpty()) { // valida
						// si es
						// vacï¿½o
						for (RefactoringParameter obj : operRef.getSubRefs().get(0).getParams().get("src")) {
							src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";// 45,67
						}
						src = src.substring(0, src.length() - 1);
					}
				}
				// 2.Extracting fld from subrefs
				if (operRef.getSubRefs().get(0).getParams().get("fld") != null) {
					if (!operRef.getSubRefs().get(0).getParams().get("fld").isEmpty())
						fld = ((AttributeDeclaration) operRef.getSubRefs().get(0).getParams().get("fld").get(0)
								.getCodeObj()).getObjName();
					else
						fld = "-1";
				} else {
					fld = "-1";
				}

				// 3.Extracting mtd from subrefs
				if (operRef.getSubRefs().get(1).getParams().get("mtd") != null) {
					if (!operRef.getSubRefs().get(1).getParams().get("mtd").isEmpty())
						mtd = ((MethodDeclaration) operRef.getSubRefs().get(1).getParams().get("mtd").get(0)
								.getCodeObj()).getObjName();
					else
						mtd = "-1";
				} else {
					mtd = "-1";
				}

				RegisterRepository repo = new RegisterRepository();
				List<Register> listMetric = new ArrayList<>();

				listMetric = repo.getRegistersByClass(acronym, src, "", mtd, fld);

				LinkedHashMap<String, Double> metricList = new LinkedHashMap<String, Double>();
				LinkedHashMap<String, LinkedHashMap<String, Double>> clasesList = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
				// clase = ((TypeDeclaration)
				// operRef.getParams().get("src").get(0).getCodeObj()).getQualifiedName();

				bandera = !listMetric.isEmpty();
				if (bandera) {

					clase = listMetric.get(0).getClasss();
					for (Register reg : listMetric) {
						if (!clase.equals(reg.getClasss())) {
							clasesList.put(clase, metricList);
							clase = reg.getClasss();
							metricList = new LinkedHashMap<String, Double>();
						}
						metricList.put(reg.getMetric(), reg.getValue());

					}
					clasesList.put(clase, metricList);
					predictMetricsRecordar.put(operRef.getRefId(), clasesList);
				}

			} else {
				bandera = false;
			}
		}
        return bandera;

    }
    // End memoization

    public FitnessQualityDB(MetaphorCode metaphor, String file) {
        this.metaphor = metaphor;
        this.file = file;
        PreviMetrics();
    }

    @Override
    public Double apply(List<RefactoringOperation> x) {
        // TODO Auto-generated method stub
        double GQSm_ = 0;
        try {
            LinkedHashMap<String, LinkedHashMap<String, Double>> predictedMetrics = ActualMetrics(PredictingMetrics(x));
            //printFitness( predictedMetrics );

            //LinkedHashMap<String, Double> bias = TotalActualMetrics( predictedMetrics );
            //printFitness2(bias);
            //GQSm_ = GQSm(bias); //First calculate proneness per metric and then normalize

            GQSm_ = GQSproneness(predictedMetrics); //First normalize and then calculate proneness

        } catch (ReadException | IOException | CompilUnitException | WritingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (x.size() == 1) {
            // GQSm();
        }
        return GQSm_;
    }

    private Double GQSproneness(LinkedHashMap<String, LinkedHashMap<String, Double>> metricActualVector) {
        double generalQuality = 0.0;
        double denominator = 0.0;
        double numerator = 0.0;

        LinkedHashMap<String, Double> SUA_metric = new LinkedHashMap<String, Double>();
        LinkedHashMap<String, Double> SUA_prev_metric = new LinkedHashMap<String, Double>();

        for (Entry<String, LinkedHashMap<String, Double>> clase : metricActualVector.entrySet()) {
            //1. Adding predicting metrics
            for (Entry<String, Double> metric : clase.getValue().entrySet()) {
                // evaluate if the metric is repeat for summing
                if (SUA_metric.containsKey(metric.getKey())) {
                    SUA_metric.replace(metric.getKey(),
                            SUA_metric.get(metric.getKey()),
                            SUA_metric.get(metric.getKey()) + metric.getValue());
                } else {
                    SUA_metric.put(metric.getKey(), metric.getValue());
                }
            }

            //2. Checking the class in prevMetrics
            if (prevMetrics.containsKey(clase.getKey())) {
                // Extracting prevMetrics
                for (Entry<String, Double> metric : prevMetrics.get(clase.getKey()).entrySet()) {
                    // Evaluate that the metric is impacted
                    if (metricActualVector.get(clase.getKey()).containsKey(metric.getKey())) {
                        // Evaluate if the metric is repeat for summing
                        if (SUA_prev_metric.containsKey(metric.getKey())) {
                            SUA_prev_metric.replace(metric.getKey(),
                                    SUA_prev_metric.get(metric.getKey()),
                                    SUA_prev_metric.get(metric.getKey()) + metric.getValue());
                        } else {
                            SUA_prev_metric.put(metric.getKey(), metric.getValue());
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

        Double min = Collections.min(SUA_metric.values());
        Double max = Collections.max(SUA_metric.values());

        Double minPrev = Collections.min(SUA_prev_metric.values());
        Double maxPrev = Collections.max(SUA_prev_metric.values());

        double W[] = new double[SUA_metric.size()];
        double w = (double) 1 / (double) SUA_metric.size();

        for (Entry<String, Double> metric : SUA_prev_metric.entrySet()) {
            if (SUA_metric.containsKey(metric.getKey())) {
                //Accumulate the metrics
                numerator = numerator + (w * ((SUA_metric.get(metric.getKey()) - min) / (max - min)));
                denominator = denominator + (w * ((metric.getValue() - minPrev) / (maxPrev - minPrev)));

            } else {
                System.out.println("Something is wrong with prev_metrics");
            }
        }
        System.out.println("Numerador: " + numerator);
        System.out.println("Denominador: " + denominator);
        generalQuality = numerator / denominator;
        System.out.println("Proneness[FITNESS]: " + generalQuality);

        escribirTextoArchivo(String.valueOf(generalQuality) + "\r\n");

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

    //Organized the prediction and reduce the data
    private LinkedHashMap<String, LinkedHashMap<String, Double>> ActualMetrics(
            LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> prediction) {
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
                if (!SUA.containsKey(clase.getKey())) { //Evaluating if SUA contains the class
                    SUA.put(clase.getKey(), SUA_metric);    //Adding to SUA if do not contains the class
                } else {
                    //Metric Loop
                    for (Entry<String, Double> metric_ : clase.getValue().entrySet()) {
                        if (SUA.get(clase.getKey()).containsKey(metric_.getKey())) { //If the SUA class contains already the metric
                            // Deciding the maximum value
                            if (metric_.getValue() >= SUA.get(clase.getKey()).get(metric_.getKey())) {
                                SUA.get(clase.getKey()).replace(metric_.getKey(),
                                        SUA.get(clase.getKey()).get(metric_.getKey()), metric_.getValue());
                            } else {
                                SUA.get(clase.getKey()).replace(metric_.getKey(),
                                        SUA.get(clase.getKey()).get(metric_.getKey()),
                                        SUA.get(clase.getKey()).get(metric_.getKey()));
                            }
                        } else {
                            SUA.get(clase.getKey()).put(metric_.getKey(), metric_.getValue());
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

        List<RefactoringOperation> operationsClone;
        //(List<RefactoringOperation>)Clone.create(operations);
        for (RefactoringOperation operRef : operations) {


            if (recordar(operRef)) {
                System.out.println("Recalling metrics");
                predictMetrics.putAll((LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>)
                        Clone.create(predictMetricsRecordar));
                predictMetricsRecordar = new
                        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>();

            } else {

                System.out.println("Predicting metrics");
                operationsClone = new ArrayList<RefactoringOperation>();
                operationsClone.add(operRef);
                MetricCalculator calc = new MetricCalculator();
                //predictMetrics = calc.predictMetrics(operations, metaphor.getMetrics(), prevMetrics);
                //predictMetrics = calc.predictMetrics(operationsClone, metaphor.getMetrics(), prevMetrics);
                predictMetricsMemorizar.putAll(calc.predictMetrics(operationsClone, metaphor.getMetrics(), prevMetrics));
                predictMetrics.putAll((LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>)
                        Clone.create(predictMetricsMemorizar));
                //Memoriza en Archivo lo que se encuentra en la predicciï¿½n y vacï¿½s la estructura
                memorizar(operRef);
            }
        }

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

    //This function generates the tracking for the fitness
    //in all possible evaluations
    public void escribirTextoArchivo(String texto) {
        String ruta = file + "_TEST_FITNESS_JAR.txt";
        try (FileWriter fw = new FileWriter(ruta, true);
             FileReader fr = new FileReader(ruta)) {
            //Escribimos en el fichero un String y un caracter 97 (a)
            fw.write(texto);
            //fw.write(97);
            //Guardamos los cambios del fichero
            fw.flush();
        } catch (IOException e) {
            System.out.println("Error E/S: " + e);
        }

    }
/**
 * refactor, clase, metrica, valor, code{src[], trg[], metod, campo}
 * 01,01,PUF,6.7,S,a-z
 *
 * 01,02,PUF,98.6,T,
 * 01,03,PUF,5.6,S
 *
 * 01,01,PUF,4.5,T
 * 01,01,PUF,55,4,S
 *
 */

}
