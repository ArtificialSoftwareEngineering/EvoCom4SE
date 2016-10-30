/**
 *
 */
package javabio.optmodel.mappings.quantum;

import javabio.optmodel.operators.quantum.QubitMutation;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayConverter;

/**
 * @author Daavid
 */
public class QubitRefactor implements Cloneable {
    /**
     * Qubit array used to store the Qubits
     */

    private QubitArray[] data = null;
    private BitArray genObservation = null;


    /**
     * The number of Qubits in the Qubit array
     */
    private int n = 5;
    private int REFACTOR = 1;    //5 Qubits
    private int SRC = 3;        //6 Qubits
    private int FLD = 2;        //3 Qubits
    private int MTD = 2;        //3 Qubits
    private int TGT = 3;        //6 Qubits
    private int QUBITTAM;

    /**
     * Constructor: Creates a clone of the QubitRefactor given as argument
     *
     * @param source The QubitRefactor that will be cloned
     */
    public QubitRefactor(QubitRefactor source) {
        if (source.data != null) {
            n = source.n;
            data = new QubitArray[source.data.length];
            for (int i = 0; i < source.data.length; i++) {
                data[i] = source.data[i];
            }
        }
    }

    /**
     * Constructor: Creates a QubitRefactor of n QubitArrays, in a random way or with all bit in false according to the randomly argument
     *
     * @param n The size of the Qubit array
     */
    public QubitRefactor(boolean random, int QUBITTAM) {
        this.QUBITTAM = QUBITTAM;
        data = new QubitArray[n];
        data[0] = new QubitArray(REFACTOR, random, QUBITTAM);
        data[1] = new QubitArray(SRC, random, QUBITTAM);
        data[2] = new QubitArray(FLD, random, QUBITTAM);
        data[3] = new QubitArray(MTD, random, QUBITTAM);
        data[4] = new QubitArray(TGT, random, QUBITTAM);

    }

    //Constructor for CODE level QubitRefactor
    public QubitRefactor(String[] refactor, String[] src, String[] fld,
                         String[] mtd, String[] tgt, int QUBITTAM) {
        this.QUBITTAM = QUBITTAM;
        data = new QubitArray[n];

        if (refactor != null)
            data[0] = new QubitArray(REFACTOR, QUBITTAM, refactor);
        else
            data[0] = new QubitArray(REFACTOR, true, QUBITTAM);

        if (src != null)
            data[1] = new QubitArray(SRC, QUBITTAM, src);
        else
            data[1] = new QubitArray(SRC, true, QUBITTAM);

        if (fld != null)
            data[2] = new QubitArray(FLD, QUBITTAM, fld);
        else
            data[2] = new QubitArray(FLD, true, QUBITTAM);

        if (mtd != null)
            data[3] = new QubitArray(MTD, QUBITTAM, mtd);
        else
            data[3] = new QubitArray(MTD, true, QUBITTAM);

        if (tgt != null)
            data[4] = new QubitArray(TGT, QUBITTAM, tgt);
        else
            data[4] = new QubitArray(TGT, true, QUBITTAM);
    }


    public QubitRefactor(int n_, QubitRefactor source) {
        if (source.data != null) {
            n = source.n - n_;
            if (n < 0) {
                n = 0;
            } else {
                data = new QubitArray[source.data.length - n_];
                for (int i = 0, j = n_; i < source.data.length - n_; i++, j++) {
                    data[i] = source.data[j];
                }
            }
        }
    }

    public QubitRefactor(int start, int end, QubitRefactor source) {
        if (source.data != null) {
            if (end <= source.data.length) {
                n = end - start;
                if (n < 0) {
                    n = 0;
                } else {
                    data = new QubitArray[n];
                    for (int i = 0, j = start; j < end && j < source.data.length; i++, j++) {
                        data[i] = source.data[j];
                    }
                }
            } else {
                n = source.data.length - start;
                if (n < 0) {
                    n = 0;
                } else {
                    data = new QubitArray[n];
                    for (int i = 0, j = start; j < source.data.length; i++, j++) {
                        data[i] = source.data[j];
                    }
                }
            }
        }
    }


    /**
     * Gets the dimension of the bits array
     *
     * @return The dimension
     */
    public int dimension() {
        return n;
    }

    /**
     * Gets specific genes
     *
     * @return The genes
     */

    public QubitArray getGenRefactor() {
        return data[0];
    }

    public QubitArray getGenSRC() {
        return data[1];
    }

    public QubitArray getGenFLD() {
        return data[2];
    }

    public QubitArray getGenMTD() {
        return data[3];
    }

    public QubitArray getGenTGT() {
        return data[4];
    }

    public BitArray getGenObservation() {
        setGenObservation();
        return genObservation;
    }

    private void setGenObservation() {
        String source = new String();
        for (int i = 0; i < n; i++) {
            source = source + data[i].getGenObservation().toString();
        }
        this.genObservation = new BitArray(source);
    }

    //Get a sub gen observation from an index to the last item in the list
    public QubitRefactor getSubGen(int index) {
        return new QubitRefactor(index, this);
    }

    //
    public QubitRefactor getSubGen(int start, int end) {
        return new QubitRefactor(start, end, this);
    }

    /**
     * Returns the number of Qubits in the array
     *
     * @return The number of Qubits in the array
     */
    public int size() {
        return n;
    }

    /**
     * Returns the sub Qubit array of the Qubit array starting at the position start and the previous to the position end-1.
     * If the end position is greater than the last position of the array then the function will return only the last Qubits.
     * <p>  A = 1000111001</p>
     * <p>  A.subBitArray( 4, 7 ) = 111</p>
     * <p>  A.subArray( 0, 4 ) = 1000</p>
     * <p>  A.subArray( 7, 11 ) = 001</p>
     *
     * @param start The start position of the substring
     * @param end   The end position + 1 of the subarray
     * @return The sub bit array of the bit array starting at the position start and the previous to the position end-1.
     */
    public QubitRefactor subQubitArray(int start, int end) {
        QubitRefactor subArray = new QubitRefactor(start, end, this);
        return subArray;
    }

    /**
     * Returns a sub qubit array of the bit array starting from the position start until the end of the bit array.
     * <p>  A = 1000111001</p>
     * <p>  A.subBitArray( 4 ) = 111001</p>
     * <p>  A.subArray( 0 ) = 1000111001</p>
     * <p>  A.subArray( 10 ) = empty bit array</p>
     *
     * @param start The start position
     * @return A sub bit array of the bit array starting from the position start until the end of the bit array.
     */
    public QubitRefactor subQubitArray(int start) {
        QubitRefactor subArray = new QubitRefactor(start, this);
        return subArray;
    }


    public QubitArray[] getData() {
        return data;
    }

    /**
     * Returns the qubitRefactor value of a specific position
     *
     * @param i The bit index
     * @return The qubitRefactor value of a specific position
     */
    public QubitArray get(int i) {
        QubitArray qubit;
        if (i < data.length) qubit = data[i];
        else qubit = null;
        return qubit;
    }

    public void set(int i, QubitArray qubit) {
        if (i < data.length) data[i] = qubit;
    }


    public void setData(QubitArray[] data) {
        this.data = data;
    }


    public int getREFACTOR() {
        return REFACTOR;
    }


    public void setREFACTOR(int rEFACTOR) {
        REFACTOR = rEFACTOR;
    }


    public int getSRC() {
        return SRC;
    }


    public void setSRC(int sRC) {
        SRC = sRC;
    }


    public int getFLD() {
        return FLD;
    }


    public void setFLD(int fLD) {
        FLD = fLD;
    }


    public int getMTD() {
        return MTD;
    }


    public void setMTD(int mTD) {
        MTD = mTD;
    }


    public int getTGT() {
        return TGT;
    }


    public void setTGT(int tGT) {
        TGT = tGT;
    }


    /**
     * Utilizada para clonar un QubitRefactor, sin copiar su referencia
     *
     * @return The new BitArray
     */
    public Object clone() {
        return new QubitRefactor(this);
    }


    /**
     * Gets integer representation of the given genome
     *
     * @return The integer representation
     */
    public int getNumberGenome(QubitArray genome) {
        return BitArrayConverter.getNumber(
                genome.getGenObservation(), 0, genome.getGenObservation().size());
    }

    public int getNumberGenome(QubitArray genome, int start, int lenght) {
        return BitArrayConverter.getNumber(
                genome.getGenObservation(), start, lenght);
    }


    public void not(int i) {
        // TODO Auto-generated method stub
        QubitMutation variation = new QubitMutation();
        data[i] = variation.apply(data[i]);
    }

}
