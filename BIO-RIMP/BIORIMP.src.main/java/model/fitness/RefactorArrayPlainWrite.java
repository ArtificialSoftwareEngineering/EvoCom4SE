package java.model.fitness;

import unalcol.io.*;

import java.io.*;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;


/**
 * <p>Title: DoubleArraySimplePersistent </p>
 * <p>Description: A double array persistent method that uses a given charater for separating the array values</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Kunsamu</p>
 *
 * @author Jonatan Gomez Perdomo Danaderp
 * @version 1.0
 */

public class RefactorArrayPlainWrite extends Write<List<RefactoringOperation>> {
    /**
     * Character used for separating the values in the array
     */
    protected char separator = ' ';

    protected boolean write_dimension = true;

    /**
     * Creates an integer array persistent method that uses an space for separatng the array values
     */
    public RefactorArrayPlainWrite() {
    }

    /**
     * Creates an integer array persistent method that uses an space for separatng the array values
     */
    public RefactorArrayPlainWrite(boolean write_dim) {
        write_dimension = write_dim;
    }

    /**
     * Creates a double array persistent method that uses the give charater for separating the array values
     *
     * @param separator Character used for separating the array values
     */
    public RefactorArrayPlainWrite(char separator) {
        this.separator = separator;
    }

    /**
     * Creates a double array persistent method that uses the give charater for separating the array values
     *
     * @param separator Character used for separating the array values
     */
    public RefactorArrayPlainWrite(char separator, boolean write_dim) {
        this.separator = separator;
        this.write_dimension = write_dim;
    }


    /**
     * Writes an array to the given writer (writes the size and the values) using the associated separator char
     *
     * @param obj array to write
     * @param out The writer object
     * @throws IOException IOException
     */
    public void write(List<RefactoringOperation> obj, Writer out) throws IOException {
        StringBuilder sb = new StringBuilder();
        //int n = obj.length;
        int n = obj.size();
        if (write_dimension) {
            sb.append(n);
        }
        if (n > 0) {
            if (write_dimension) sb.append(separator);
            //sb.append(obj[0]);
            sb.append(obj.get(0));
        }
        for (int i = 1; i < n; i++) {
            sb.append(separator);
            //sb.append(obj[i]);
            sb.append(obj.get(i));
        }
        out.write(sb.toString());
    }

}
