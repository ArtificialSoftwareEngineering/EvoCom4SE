package optimization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
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
	
	public List<QubitRefactor> code (List<RefactoringOperation> thing){
		QubitRefactor model = new QubitRefactor(true);
		List<QubitRefactor> coding = new ArrayList<QubitRefactor>();

		String  observation = new String();
		for(RefactoringOperation ref : thing){
			//extracting ref type
			switch(ref.getRefId()){
			case "pullUpField" : 
				if( Integer.toBinaryString(0).length() <= model.getREFACTOR() ){
					char[] temp = new char[model.getREFACTOR()];
					Arrays.fill(temp, '0');
					
					for(int i = model.getREFACTOR()-1; i>=0 ; i-- ){
						
					}
				}
					
				Integer.toBinaryString(0);
				break;
			case "moveMethod" :
				observation = Integer.toBinaryString(1);
				break;
			case "replaceMethodObject" : 
				observation = Integer.toBinaryString(2);
				break;
			case "replaceDelegationInheritance" : 
				observation = Integer.toBinaryString(3);
				break;
			case "moveField" : 
				observation = Integer.toBinaryString(4);
				break;
			case "extractMethod" : 
				observation = Integer.toBinaryString(5);
				break;
			case "pushDownMethod" : 
				observation = Integer.toBinaryString(6);
				break;
			case "replaceInheritanceDelegation" : 
				observation = Integer.toBinaryString(7);
				break;
			case "inlineMethod" : 
				observation = Integer.toBinaryString(8);
				break;
			case "pullUpMethod" : 
				observation = Integer.toBinaryString(9);
				break;
			case "pushDownField" : 
				observation = Integer.toBinaryString(10);
				break;
			case "extractClass" : 
				observation = Integer.toBinaryString(11);
				break;
			}//end case
			
			//extracting source classes
			int numberSrc = 0;
			if( !ref.getParams().get("src").isEmpty() ){
				for(RefactoringParameter rp : ref.getParams().get("src")){
					numberSrc++;
					for(Entry<Integer,TypeDeclaration> param : metaphor.getMapClass().entrySet()){
						if( param.getValue().equals(  rp.getCodeObj()  ) ){
							observation = observation + Integer.toBinaryString(param.getKey());
						}
					}
				}
			}//end if src
			
			//extracting fld
			if( !ref.getParams().get("fld").isEmpty() ){
				
			}
		}

		return coding;
	}
	
	private enum Refactoring{
		pullUpField, moveMethod, replaceMethodObject, replaceDelegationInheritance,
		moveField, extractMethod, pushDownMethod, replaceInheritanceDelegation, 
		inlineMethod, pullUpMethod, pushDownField, extractClass
	}

}
