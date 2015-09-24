/**
 * 
 */
package space;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MetaphorCode;
import unalcol.random.integer.IntUniform;
import unalcol.types.collection.bitarray.BitArray;

/**
 * @author Daavid
 *
 */
public class GeneratingRefactorEC extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.extractClass;
	private boolean feasible = true;
	@Override
	public OBSERVRefactoring generatingRefactor( MetaphorCode code ) {
		// TODO Auto-generated method stub
		return mappingRefactorMFMM(code, "TgtClassEC" );
	}
	
	public OBSERVRefactoring mappingRefactorMFMM( MetaphorCode code, String newClass ) {
		// TODO Auto-generated method stub
		
		List<OBSERVRefactoring> subRefs = new ArrayList<OBSERVRefactoring>();
		List<OBSERVRefParam> paramsMF = new ArrayList<OBSERVRefParam>();	
		List<OBSERVRefParam> paramsMM = new ArrayList<OBSERVRefParam>();
		TypeDeclaration sysType_src;
		
		do{
			//Creating the OBSERVRefParam for the src class
			IntUniform g = new IntUniform ( code.getMapClass().size() );
			sysType_src = code.getMapClass().get( g.generate()  );
			List<String> value_src  = new ArrayList<String>();
			value_src.add(sysType_src.getQualifiedName());
			paramsMF.add(new OBSERVRefParam("src", value_src));
			paramsMM.add(new OBSERVRefParam("src", value_src));
			
			//Creating the OBSERVRefParam for the fld field
			List<String> value_fld  = new ArrayList<String>();
			if(!code.getFieldsFromClass(sysType_src).isEmpty()){
				IntUniform numFldObs = new IntUniform ( code.getFieldsFromClass(sysType_src).size() );
						
				String fldName = (String) code.getFieldsFromClass(sysType_src).toArray()
						 [ numFldObs.generate() ];
				value_fld.add(fldName);
				paramsMF.add(new OBSERVRefParam("fld", value_fld));
			}else{
				
				feasible = false; 
			}
			
			//Creating the OBSERVRefParam for the mtd class
			List<String> value_mtd  = new ArrayList<String>();
			if(!code.getMethodsFromClass(sysType_src).isEmpty()){
				IntUniform numMtdObs = new IntUniform ( code.getMethodsFromClass(sysType_src).size() );
					
				value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()
						[ numMtdObs.generate() ]);
				
				//verification of method not constructor
				if(value_mtd.get(0).equals(sysType_src.getName()))
					feasible = false;
				
				paramsMM.add(new OBSERVRefParam("mtd", value_mtd));
			}else{
				feasible = false; 
			}
			
		}while(!feasible); //Generate Just feasible individuals
		
		//Creating the OBSERVRefParam for the tgt
		//This Target Class is not inside metaphor
		List<String> value_tgt  = new ArrayList<String>();
		value_tgt.add( sysType_src.getPack() + newClass + "|N");
		paramsMF.add(new OBSERVRefParam("tgt", value_tgt));
		paramsMM.add(new OBSERVRefParam("tgt", value_tgt));
		code.addClasstoHash(sysType_src.getPack(), newClass + "|N");
		
		subRefs.add( new OBSERVRefactoring(Refactoring.moveField.name(),paramsMF, feasible ) );
		subRefs.add( new OBSERVRefactoring(Refactoring.moveMethod.name(),paramsMM, feasible ) );
		
		return new OBSERVRefactoring(type.name(),null,subRefs,feasible);
	}
	

}
