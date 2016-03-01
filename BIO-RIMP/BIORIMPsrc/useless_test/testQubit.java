/**
 * 
 */
package useless_test;

import entity.Qubit;
import entity.QubitArray;
import space.QubitSpace;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayConverter;

/**
 * @author Daavid
 *
 */
public class testQubit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		//Testing QubitArray 
		QubitArray array = new QubitArray(40,true);
		System.out.println(array.getGenObservation().toString());
		
		//Testing QubitSpace
		QubitArray arrayS = new QubitArray(5,true);
		QubitSpace space = new QubitSpace(10);
		System.out.println("isfeasible:"+space.feasibility(arrayS));
		QubitArray arrayS_ = space.repair(arrayS);
		System.out.println("isfeasible:"+space.feasibility(arrayS_));
		//Testing mapping bitToNumber
		/*
		BitArray testMap = new BitArray("1000");
		int map = BitArrayConverter.getNumber(testMap, 0, 4);
		System.out.println(map);*/
		
		/*
		//Testing mapping NumbertoBitArray
		BitArray testMap = new BitArray(20,false);
		int map = 30; 
		BitArrayConverter.setNumber(testMap, 0, 20, map);
		System.out.println(testMap.toString());
		// TODO Auto-generated method stub
		
		
		//Testing GrowingFunctionQubit
		//GrowingFunctionQubit grow = new GrowingFunctionQubit();
		//grow.get(array);
		
		do{
		Qubit testqubit = new Qubit(true);
		BitArray testObservation = testqubit.getObservationQubit();
		System.out.println(testObservation.toString());
		}while(true);
		
		*/
	}

}
