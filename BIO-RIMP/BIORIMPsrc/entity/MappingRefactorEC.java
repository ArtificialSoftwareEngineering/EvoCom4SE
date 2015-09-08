/**
 * 
 */
package entity;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MappingRefactor.Refactoring;
import unalcol.types.collection.bitarray.BitArray;

/**
 * @author Daavid
 *
 */
public class MappingRefactorEC extends MappingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.extractClass;
	private boolean feasible = true;
	@Override
	public OBSERVRefactoring mappingRefactor(QubitRefactor genome, MetaphorCode code) {
		// TODO Auto-generated method stub
		
		List<OBSERVRefactoring> subRefs = new ArrayList<OBSERVRefactoring>();
		subRefs.add(mappingRefactorMF(genome, code, "TgtClassEC"));
		subRefs.add(mappingRefactorMM(genome, code, "TgtClassEC"));
		
		return new OBSERVRefactoring(type.name(),null,subRefs,feasible);
	}
	
	public OBSERVRefactoring mappingRefactorMF(QubitRefactor genome, MetaphorCode code, String newClass) {
		// TODO Auto-generated method stub
		
		List<OBSERVRefParam> params = new ArrayList<OBSERVRefParam>();		

		
		//Creating the OBSERVRefParam for the src class
		int	numSrcObs = genome.getNumberGenome(genome.getGenSRC());
		TypeDeclaration sysType_src = code.getMapClass().get(numSrcObs % 
					code.getMapClass().size());
		List<String> value_src  = new ArrayList<String>();
		value_src.add(sysType_src.getQualifiedName());
		params.add(new OBSERVRefParam("src", value_src));
		
		//Creating the OBSERVRefParam for the fld field
		List<String> value_fld  = new ArrayList<String>();
		if(!code.getFieldsFromClass(sysType_src).isEmpty()){
			int numFldObs = genome.getNumberGenome(genome.getGenFLD());
			
			String fldName = (String) code.getFieldsFromClass(sysType_src).toArray()[numFldObs
			               % code.getFieldsFromClass(sysType_src).size()];
			value_fld.add(fldName);
			params.add(new OBSERVRefParam("fld", value_fld));
		}else{
			value_fld.add("");
			params.add(new OBSERVRefParam("fld", value_fld ));
			feasible = false; 
		}
		
		//Creating the OBSERVRefParam for the tgt
		//This Target Class is not inside metaphor
		List<String> value_tgt  = new ArrayList<String>();
		value_tgt.add( sysType_src.getPack() + newClass + "|N");
		params.add(new OBSERVRefParam("tgt", value_tgt));
		code.addClasstoHash(sysType_src.getPack(), newClass + "|N");
		
		return new OBSERVRefactoring(Refactoring.moveField.name(),params, feasible );
	}
	
	public OBSERVRefactoring mappingRefactorMM(QubitRefactor genome, MetaphorCode code,String newClass) {
		// TODO Auto-generated method stub
		List<OBSERVRefParam> params = new ArrayList<OBSERVRefParam>();
		
		//Creating the OBSERVRefParam for the src class
		int numSrcObs = genome.getNumberGenome(genome.getGenSRC());
		TypeDeclaration sysType_src =  code.getMapClass().get(numSrcObs % 
				code.getMapClass().size());
		List<String> value_src  = new ArrayList<String>();
		value_src.add(sysType_src.getQualifiedName());
		params.add(new OBSERVRefParam("src", value_src));
		
		//Creating the OBSERVRefParam for the mtd class
		List<String> value_mtd  = new ArrayList<String>();
		if(!code.getMethodsFromClass(sysType_src).isEmpty()){
			int numMtdObs = genome.getNumberGenome(genome.getGenMTD());
			
			value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()[numMtdObs
			     		  % code.getMethodsFromClass(sysType_src).size()]);
			
			//verification of method not constructor
			if(value_mtd.get(0).equals(sysType_src.getName()))
				feasible = false;
			
			params.add(new OBSERVRefParam("mtd", value_mtd));
		}else{
			value_mtd.add("");
			params.add(new OBSERVRefParam("mtd", value_mtd));
			feasible = false; 
			
		}
		//Creating the OBSERVRefParam for the tgt
		//This Target Class is not inside metaphor
		List<String> value_tgt  = new ArrayList<String>();
		value_tgt.add( sysType_src.getPack() + newClass + "|N");
		params.add(new OBSERVRefParam("tgt", value_tgt));
		code.addClasstoHash(sysType_src.getPack(), newClass + "|N");
		
		
		return new OBSERVRefactoring(Refactoring.moveMethod.name(),params,feasible);

	}

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingParams()
	 */
	@Override
	public List<OBSERVRefParam> mappingParams() {
		// TODO Auto-generated method stub
		return null;
	}

}
