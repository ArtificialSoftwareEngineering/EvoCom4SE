/**
 * 
 */
package entity;

import unalcol.types.collection.bitarray.BitArray;


/**
 * @author Daavid
 *
 */
public class Qubit {
	
	
	private int BITARRAYLENGTH = 2;
	private BitArray bit_one_non_act = null;
	private BitArray bit_two_non_act = null;
	private BitArray bit_act = null;
	private BitArray activeState = null;
	
	
	
	//DDefine when  a bit is dominant
	
	public Qubit(boolean random){
		bit_one_non_act = new BitArray(BITARRAYLENGTH, random);
		bit_two_non_act = new BitArray(BITARRAYLENGTH, random);
		bit_act = new BitArray(BITARRAYLENGTH, random);
		activeState = new BitArray(BITARRAYLENGTH, random);
	}
	
	public BitArray getObservationQubit(){
		//how to create observation from bit:one and bit:two
		//Checking the state of the bits
		//One (true) means active state so it has not a superposition
		//Zero (false) means non active state so it has superposition 
		
		String obser = new String();
		if(activeState.get(0)==false && activeState.get(1)==false){
			obser = bit_act.toString();
		}else{
			if(activeState.get(0)==true && activeState.get(1)==true){
				obser = bit_one_non_act.toString() + bit_two_non_act.toString();
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
		}
		
		
		return new BitArray(obser);
	}

	public BitArray getBit_one_non_act() {
		return bit_one_non_act;
	}

	public void setBit_one_non_act(BitArray bit_one_non_act) {
		this.bit_one_non_act = bit_one_non_act;
	}

	public BitArray getBit_two_non_act() {
		return bit_two_non_act;
	}

	public void setBit_two_non_act(BitArray bit_two_non_act) {
		this.bit_two_non_act = bit_two_non_act;
	}

	public BitArray getBit_act() {
		return bit_act;
	}

	public void setBit_act(BitArray bit_act) {
		this.bit_act = bit_act;
	}

	public BitArray getActiveState() {
		return activeState;
	}

	public void setActiveState(BitArray activeState) {
		this.activeState = activeState;
	}

	public int getBITARRAYLENGTH() {
		return BITARRAYLENGTH;
	}

	public void setBITARRAYLENGTH(int bITARRAYLENGTH) {
		BITARRAYLENGTH = bITARRAYLENGTH;
	}

	
	
}
