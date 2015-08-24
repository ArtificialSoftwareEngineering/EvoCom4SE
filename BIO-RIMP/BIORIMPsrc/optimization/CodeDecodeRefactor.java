package optimization;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactorings;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import entity.MappingRefactor;
import entity.MappingRefactorEC;
import entity.MappingRefactorEM;
import entity.MappingRefactorIM;
import entity.MappingRefactorMF;
import entity.MappingRefactorMM;
import entity.MappingRefactorPDF;
import entity.MappingRefactorPDM;
import entity.MappingRefactorPUF;
import entity.MappingRefactorPUM;
import entity.MappingRefactorRDI;
import entity.MappingRefactorRID;
import entity.MappingRefactorRMMO;
import entity.MetaphorCode;
import entity.QubitArray;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayConverter;

public class CodeDecodeRefactor 
	extends CodeDecodeMap<QubitArray,List<RefactoringOperation>>{
	
	private MetaphorCode metaphor;
	private Hashtable<String,Integer> genetic_marker;

	
	public CodeDecodeRefactor(MetaphorCode metaphor) { 
		this.metaphor = metaphor;
		genetic_marker = new Hashtable<String,Integer>();
		genetic_marker.put("BASE",2); //Minimun amount of qubits for representing a refactor
		genetic_marker.put("SRC",6);  //Fixed amount of Qubits for representing a src class
		genetic_marker.put("TGT",6);  //Fixed amount of Qubits for representing a tgt class
		genetic_marker.put("FLD",3);  //Fixed amount of Qubits for representing a fld 
		genetic_marker.put("MTD",3);  //Fixed amount of Qubits for representing a mtd 
		genetic_marker.put("TAM",1);  //Fixed amount of Qubits for representing the # of variable chromosome
		genetic_marker.put("PUF_BASE",
				genetic_marker.get("BASE").intValue() +
				genetic_marker.get("TAM").intValue() +
				genetic_marker.get("FLD").intValue() +
				genetic_marker.get("TGT").intValue() 
				); 
	}
	
	public List<RefactoringOperation> decode(QubitArray genome) { 
		RefactoringReaderBIoRIMP reader = new RefactoringReaderBIoRIMP(
				metaphor.getSysTypeDcls(),
				metaphor.getLang(),
				metaphor.getBuilder());
		
		try {
			return reader.getRefactOperations(
					mappingRefactor(genome)
			);
		} catch (ReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Reading Error");
			return null;
		} 
	}
	
	private OBSERVRefactorings mappingRefactor(QubitArray genome){	
		int mapRefactor;
		int Q_TAM; //Qubit #3 for size of the variable parameter 
		int quanticReader = 0;//position of the reader in the QubitArray;
		boolean stillReading = true;
		OBSERVRefactorings oper = new OBSERVRefactorings();
		
		metaphor.bitAssignerClass(); //Building the hash map
		
		List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();
		
		MappingRefactor mappingRefactor=null;
		
		while(genome.size() > genetic_marker.get("BASE").intValue()  + genetic_marker.get("TAM").intValue() 
						&& stillReading){ //+tam in case of variable chromosome
		//for(int i=0;i<=observation.size();i=i+MIN){
			//SubIndex of the RefactoringType
			mapRefactor = BitArrayConverter.getNumber(
					genome.getSubGen(0,genetic_marker.get("BASE").intValue() ).getGenObservation(), 
					0, 
					genome.getSubGen(0,genetic_marker.get("BASE").intValue() ).getGenObservation().size()) 
					%	(Refactoring.values().length-1);
			//According to the RefactoringType is selected the params mapping
			switch(mapRefactor){
				case 0:
					mappingRefactor = new MappingRefactorPUF();
					Q_TAM = BitArrayConverter.getNumber(
							genome.get(genetic_marker.get("BASE").intValue() + genetic_marker.get("TAM").intValue() ).getObservationQubit(), 
							0, 
							genome.get(genetic_marker.get("BASE").intValue() + genetic_marker.get("TAM").intValue() ).getObservationQubit().size()); 
					quanticReader = genetic_marker.get("PUF_BASE").intValue() + 
							genetic_marker.get("SRC").intValue() * Q_TAM;
					if(genome.size() < quanticReader)
						stillReading = false;
					break;
				case 1:
					mappingRefactor = new MappingRefactorMM();
					break;
				case 2:
					mappingRefactor = new MappingRefactorRMMO();
					break;
				case 3:
					mappingRefactor = new MappingRefactorRDI();
					break;
				case 4:
					mappingRefactor = new MappingRefactorMF();
					break;
				case 5:
					mappingRefactor = new MappingRefactorEM();
					break;
				case 6:
					mappingRefactor = new MappingRefactorPDM();
					break;
				case 7:
					mappingRefactor = new MappingRefactorRID();
					break;
				case 8:
					mappingRefactor = new MappingRefactorIM();
					break;
				case 9:
					mappingRefactor = new MappingRefactorPUM();
					break;
				case 10:
					mappingRefactor = new MappingRefactorPDF();
					break;
				case 11:
					mappingRefactor = new MappingRefactorEC();
					break;
			}//END CASE
			
			if(stillReading){
				refactorings.add(mappingRefactor.mappingRefactor(genetic_marker,
				    		genome,
				    		metaphor
				    		));
				//Cutting the QubitArray
				genome = genome.subQubitArray(quanticReader);
			}
		}//END LOOP
		
		oper.setRefactorings(refactorings);
		return oper;
	}
	
	
	private enum Refactoring{
		pullUpField, moveMethod, replaceMethodObject, replaceDelegationInheritance,
		moveField, extractMethod, pushDownMethod, replaceInheritanceDelegation, 
		inlineMethod, pullUpMethod, pushDownField, extractClass
	}

}
