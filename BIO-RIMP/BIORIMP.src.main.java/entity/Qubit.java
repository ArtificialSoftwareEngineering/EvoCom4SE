/**
 * 
 */
package entity;

import unalcol.random.util.RandBool;
import unalcol.types.collection.bitarray.BitArray;


/**
 * @author Daavid
 *
 */
public class Qubit {
	
	

	private int BITARRAYLENGTH;
	//private BitArray bit_one_non_act = null;
	//private BitArray bit_two_non_act = null;
	private BitArray non_act = null;
	private BitArray bit_act = null;
	private Boolean activeState = null; //check for bool
	
	
	
	//DDefine when  a bit is dominant
	
	public Qubit(boolean random, int BITARRAYLENGTH){
		this.BITARRAYLENGTH = BITARRAYLENGTH;
		//bit_one_non_act = new BitArray(BITARRAYLENGTH, random);
		//bit_two_non_act = new BitArray(BITARRAYLENGTH, random);
		bit_act = new BitArray(BITARRAYLENGTH, random);
		non_act = new BitArray(BITARRAYLENGTH*2, random); //double capacity
		RandBool g = new RandBool();
		activeState = g.next();
	}
	
	//Constructor for CODE level qubit
	
	public Qubit(boolean[] bits){
		this.BITARRAYLENGTH = bits.length;

		bit_act = new BitArray(bits);

		non_act = new BitArray(BITARRAYLENGTH*2, true); //double capacity
		activeState = false;
	}
	
	//Unstable Constructor
	public Qubit(BitArray qubitObserv, int startQubit, int startQuArray, int numQubit){
		
		BitArray qubitArray = qubitObserv.subBitArray(startQuArray * ((BITARRAYLENGTH/2) * numQubit), 
				(startQuArray * ((BITARRAYLENGTH/2) * numQubit)) + ((BITARRAYLENGTH/2) * numQubit) );
		
		bit_act = new BitArray( qubitArray.subBitArray(startQubit * (BITARRAYLENGTH/2) , 
				(startQubit * (BITARRAYLENGTH/2) ) + BITARRAYLENGTH/2 ) );
		
		non_act = new BitArray(BITARRAYLENGTH, true);
		activeState = false;
	}
	
	public BitArray getObservationQubit(){
		//how to create observation from bit:one and bit:two
		//Checking the state of the bits
		//One (true) means active state so it has not a superposition
		//Zero (false) means non active state so it has superposition 
		
		String obser = new String();
		
		if(activeState == false)
			obser = bit_act.toString();
		else 
			obser = non_act.toString();
		/*
		if(activeState.get(0)==false && activeState.get(1)==false){
			obser = bit_act.toString();
		}else{
			if(activeState.get(0)==true && activeState.get(1)==true){
				//obser = bit_one_non_act.toString() + bit_two_non_act.toString();
				obser = non_act.toString();
			}else{
				if(activeState.get(0)==false && activeState.get(1)==true){
					obser = bit_act.toString().substring(0, 1) + bit_two_non_act.toString();
				}
				else{
					if(activeState.get(0)==true && activeState.get(1)==false){
						obser = bit_act.toString().substring(1) +bit_one_non_act.toString();
					}
				}
			}
		}*/
		
		return new BitArray(obser);
	}

	public BitArray getBit_act() {
		return bit_act;
	}

	public void setBit_act(BitArray bit_act) {
		this.bit_act = bit_act;
	}

	public Boolean getActiveState() {
		return activeState;
	}

	public void setActiveState(Boolean activeState) {
		this.activeState = activeState;
	}

	public int getBITARRAYLENGTH() {
		return BITARRAYLENGTH;
	}

	public void setBITARRAYLENGTH(int bITARRAYLENGTH) {
		BITARRAYLENGTH = bITARRAYLENGTH;
	}
	
	public BitArray getNon_act() {
		return non_act;
	}

	public void setNon_act(BitArray non_act) {
		this.non_act = non_act;
	}

	
	
}
