package javabio.optmodel.fitness;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import scalabio.FitnessScalaApply;
import unalcol.optimization.OptimizationFunction;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        double generalQuality = fit.gBiasQualitySystemRatio(x);
        escribirTextoArchivo(String.valueOf(generalQuality) + "\r\n");
        return generalQuality;
    }

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

}
