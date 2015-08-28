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
public class MappingRefactorPDM extends MappingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.pushDownMethod;
	
	@Override
	public OBSERVRefactoring mappingRefactor(QubitRefactor genome, MetaphorCode code) {
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
		int numMtdObs = genome.getNumberGenome(genome.getGenMTD());
		List<String> value_mtd  = new ArrayList<String>();
		value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()[numMtdObs
				     	 % code.getMethodsFromClass(sysType_src).size()]);
		params.add(new OBSERVRefParam("mtd", value_mtd));
		
		
		//Creating the OBSERVRefParam for the tgt class
		TypeDeclaration sysType_tgt = null;
		List<String> value_tgt  = new ArrayList<String>();
		
		int numTgtObs = 0;
		for(int i = 0; i < genome.getGenTGT().size(); i = i+genome.getTGT()){
			numTgtObs = genome.getNumberGenome(genome.getGenTGT(), i, genome.getTGT());
			sysType_tgt = code.getMapClass().get(numTgtObs % 
					code.getMapClass().size());
			value_tgt.add(sysType_tgt.getQualifiedName());
			
		}
		params.add(new OBSERVRefParam("tgt", value_tgt));
		
		return new OBSERVRefactoring(type.name(),params);
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
