/**
 *
 */
package entity;

import java.util.ArrayList;
import java.util.Arrays;

import unalcol.optimization.binary.BitMutation;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.integer.IntUtil;

/**
 * @author Daavid
 */
public class QubitArray implements Cloneable {
    /**
     * Qubit array used to store the Qubits
     */
    //private Qubit[] data = null;
    private ArrayList<Qubit> data;
    private BitArray genObservation = null;
    //concatenate all observations from Qubit

    /**
     * The number of Qubits in the Qubit array
     */
    private int n = 0;


    /**
     * Constructor: Creates a clone of the Qubit array given as argument
     *
     * @param source The Qubit array that will be cloned
     */
    public QubitArray(QubitArray source) {
        if (source.data != null) {
            n = source.n;
            //data = new Qubit[source.data.length];
            data = new ArrayList<Qubit>();
            //for (int i = 0; i < source.data.length; i++) {
            for (int i = 0; i < source.data.size(); i++) {
                data.add(source.data.get(i));
                //data[i] = source.data[i];
            }
        }
    }


    public QubitArray(int n_, QubitArray source) {
        if (source.data != null) {
            n = source.n - n_;
            if (n < 0) {
                n = 0;
            } else {
                //data = new Qubit[source.data.length - n_];
                data = new ArrayList<Qubit>();
                //for (int i = 0, j = n_; i < source.data.length - n_; i++,j++) {
                for (int i = 0, j = n_; i < source.data.size() - n_; i++, j++) {
                    //data[i] = source.data[j];
                    data.add(source.data.get(j));
                }
            }
        }
    }

    public QubitArray(int start, int end, QubitArray source) {
        if (source.data != null) {
            //if(end <= source.data.length){
            if (end <= source.data.size()) {
                n = end - start;
                if (n < 0) {
                    n = 0;
                } else {
                    data = new ArrayList<Qubit>();
                    //data = new Qubit[n];
                    //for (int i = 0, j = start; j < end && j < source.data.length; i++,j++) {
                    for (int i = 0, j = start; j < end && j < source.data.size(); i++, j++) {
                        data.add(source.data.get(j));
                        //data[i] = source.data[j];
                    }
                }
            } else {
                //n = source.data.length - start;
                n = source.data.size() - start;
                if (n < 0) {
                    n = 0;
                } else {
                    //data = new Qubit[n];
                    data = new ArrayList<Qubit>();
                    for (int i = 0, j = start; j < source.data.size(); i++, j++) {
                        //for (int i = 0, j = start; j < source.data.length;i++,j++) {
                        data.add(source.data.get(j));
                        //data[i] = source.data[j];
                    }
                }
            }
        }
    }

    /**
     * Constructor: Creates a bit array of n bits, in a random way or with all bit in false according to the randomly argument
     *
     * @param n The size of the bit array
     */
    public QubitArray(int n, boolean random, int qubitam) {
        this.n = n;
        data = new ArrayList<Qubit>();
        //data = new Qubit[n];

        for (int i = 0; i < n; i++) {
            data.add(new Qubit(random, qubitam));
            //data[i] = new Qubit(random);
        }

    }


    //Constructor for CODE level QubitArray
    //must be cut by qubittam bits is the complete observation
    public QubitArray(int numOfQubits, int qubitam, String[] observation) {

        data = new ArrayList<Qubit>();

        for (int i = 0; i < observation.length; i++) {

            if (observation[i].length() <= numOfQubits * qubitam) {
                boolean[] strand = new boolean[numOfQubits * qubitam];
                Arrays.fill(strand, false);
                for (int j = observation[i].length() - 1, k = strand.length - 1; j >= 0; j--, k--) {
                    if (observation[i].charAt(j) == '0')
                        strand[k] = false;
                    else
                        strand[k] = true;
                }

                for (int m = 0; m < numOfQubits; m++) {
                    boolean[] bits = new boolean[qubitam];
                    for (int n = 0; n < bits.length; n++) {
                        bits[n] = strand[n + (qubitam * m)];
                    }
                    data.add(new Qubit(bits));
                }

            } else {
                System.out.println("ERROR. Creating QUBITARRAY for OutofBounds");
                data.add(new Qubit(true, qubitam));
            }
        }

        this.n = data.size();
    }

    /**
     * Gets the dimension of the bits array
     *
     * @return The dimension
     */
    public int dimension() {
        return n;
    }

    public BitArray getGenObservation() {
        setGenObservation();
        return genObservation;
    }

    private void setGenObservation() {
        String source = new String();
        for (int i = 0; i < n; i++) {
            //source = source + data[i].getObservationQubit().toString();
            source = source + data.get(i).getObservationQubit().toString();
        }
        this.genObservation = new BitArray(source);
    }

    //Get a sub gen observation from an index to the last item in the list
    public QubitArray getSubGen(int index) {
        return new QubitArray(index, this);
    }

    //
    public QubitArray getSubGen(int start, int end) {
        return new QubitArray(start, end, this);
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
    public QubitArray subQubitArray(int start, int end) {
        QubitArray subArray = new QubitArray(start, end, this);
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
    public QubitArray subQubitArray(int start) {
        QubitArray subArray = new QubitArray(start, this);
        return subArray;
    }


    public ArrayList<Qubit> getData() {
        return data;
    }

    /**
     * Returns the qubit value of a specific position
     *
     * @param i The bit index
     * @return The qubit value of a specific position
     */
    public Qubit get(int i) {
        Qubit qubit;
        if (i < data.size()) qubit = data.get(i);
            //if(i<data.length) qubit = data[i];
        else qubit = null;
        return qubit;
    }

    //public void set(int i, Qubit qubit){
    //	if(i<data.length) data[i] = qubit;
    //}

    public void set(Qubit qubit) {
        data.add(qubit);
    }

    public void add(int i, Qubit qubit) {
        data.add(i, qubit);
    }

    //public void setData(Qubit[] data) {
    //	this.data = data;
    //}

    public void setData(ArrayList<Qubit> data) {
        this.data = data;
    }

    /**
     * Flips the Qubit given
     * <p> A = 10011011</p>
     * <p> A.not( 3 ) = 10001011</p>
     * <p> A.not( 5 ) = 10011111</p>
     *
     * @param bit apply not
     */
    public void not(int i) {
        BitMutation variation = new BitMutation();
        //Flips on active bits
        get(i).setBit_act(variation.apply(get(i).getBit_act()));
        //Flips on non-active bits
        get(i).setNon_act(variation.apply(get(i).getNon_act()));
    }

    /**
     * Flips the Qubit given in
     * specific bit
     * <p> A = 10011011</p>
     * <p> A.not( 3 ) = 10001011</p>
     * <p> A.not( 5 ) = 10011111</p>
     *
     * @param bit apply not
     */
    public void not_bit(int i) {
        //Flips on active bits
        get(i).getBit_act().not(i % get(i).getBit_act().size());
        //Flips on non-active bits
        get(i).getNon_act().not(i % get(i).getNon_act().size());
        //get(i).getBit_one_non_act().not(i % get(i).getBITARRAYLENGTH());
        //get(i).getBit_two_non_act().not(i % get(i).getBITARRAYLENGTH());
    }

    /**
     * Flips the Qubit state given
     * <p> A = 10011011</p>
     * <p> A.not( 3 ) = 10001011</p>
     * <p> A.not( 5 ) = 10011111</p>
     *
     * @param bit apply not
     */
    public void not_state(int i) {
        //Flips on state
        if (!get(i).getActiveState() ) {
            get(i).setActiveState(true);
        } else {
            get(i).setActiveState(false);
        }
    }

    /**
     * Utilizada para clonar un BitArray, sin copiar su referencia
     *
     * @return The new BitArray
     */
    public Object clone() {
        return new QubitArray(this);
    }


}
