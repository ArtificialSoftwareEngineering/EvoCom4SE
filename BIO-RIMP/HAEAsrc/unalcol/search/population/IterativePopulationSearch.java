package unalcol.search.population;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import unalcol.descriptors.Descriptors;
import unalcol.math.logic.Predicate;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.space.Space;
import unalcol.tracer.Tracer;

public class IterativePopulationSearch<T> extends PopulationSearch<T> {
    protected Predicate<PopulationSolution<T>> terminationCondition;
    protected PopulationSearch<T> step;

    public IterativePopulationSearch(int n, PopulationSearch<T> step,
                                     Predicate<PopulationSolution<T>> tC) {
        super(n);
        terminationCondition = tC;
        this.step = step;
    }

    public void init() {
        terminationCondition.init();
    }

    @Override
    public Solution<T> apply(Space<T> space, Goal<T> goal) {
        init();
        PopulationSolution<T> x = new PopulationSolution<T>(space.get(n), goal);
        Tracer.trace(this, Descriptors.obtain(x), step);
        while (terminationCondition.evaluate(x)) {
            x = apply(x, space, goal);
            Tracer.trace(this, Descriptors.obtain(x), step);
            //Danaderp
            //for(double sol: x.quality)
            //escribirTextoArchivo( sol + "\r\n" );
            //Danaderp
        }
        return x.pick();
    }

    @Override
    public PopulationSolution<T> apply(PopulationSolution<T> pop,
                                       Space<T> space, Goal<T> goal) {
        return step.apply(pop, space, goal);
    }

    public void escribirTextoArchivo(String texto) {
        String ruta = "F_TEST_CCODEC_JAR.txt";
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
