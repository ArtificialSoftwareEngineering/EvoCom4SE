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
import entity.QubitRefactor;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayConverter;

public class CodeDecodeRefactorList 
	extends CodeDecodeMap<List<QubitRefactor>,List<RefactoringOperation>>{
	
	private MetaphorCode metaphor;

	
	public CodeDecodeRefactorList(MetaphorCode metaphor) { 
		this.metaphor = metaphor;
	}
	
	public List<RefactoringOperation> decode(List<QubitRefactor> genome) { 
		RefactoringReaderBIoRIMP reader = new RefactoringReaderBIoRIMP(
				metaphor.getSysTypeDcls(),
				metaphor.getLang(),
				metaphor.getBuilder());
		
		try {
			return reader.getRefactOperations(
					mappingRefactoring(genome)
			);
		} catch (ReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Reading Error");
			return null;
		} 
	}
	
	private OBSERVRefactorings mappingRefactoring(List<QubitRefactor> genome){	
		int mapRefactor;
		
		OBSERVRefactorings oper = new OBSERVRefactorings();
		
		//metaphor.bitAssignerClass(); //Building the hash map
		
		List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();
		
		MappingRefactor mappingRefactor=null;
		
		for(int i = 0; i < genome.size(); i++){
			//SubIndex of the RefactoringType
			int getnumber = genome.get(i).getNumberGenome(genome.get(i).getGenRefactor());
			mapRefactor = getnumber  %	(Refactoring.values().length);
			//According to the RefactoringType is selected the params mapping
			switch(mapRefactor){
				case 0:
					mappingRefactor = new MappingRefactorPUF();
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
			
			refactorings.add(mappingRefactor.mappingRefactor(genome.get(i), metaphor));
			
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
