/**
 *
 */
package useless_test;

import unalcol.types.collection.bitarray.BitArray;

/**
 * @author Daavid
 */
public class testBitArray {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        BitArray testbit = new BitArray(5, false);
        System.out.println(testbit.toString());

        boolean[] testbool = new boolean[3];
        testbool[0] = false;
        testbool[1] = true;
        testbool[2] = true;

        BitArray testbit2 = new BitArray(testbool);
        System.out.println(testbit2.toString());

    }

}
