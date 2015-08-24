/**
 * 
 */
package entity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MappingRefactor.Refactoring;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayConverter;

/**
 * @author Daavid
 *
 */
public class MappingRefactorPUF extends MappingRefactor {
	
	private Refactoring type = Refactoring.pullUpField;
	
	
	@Override
	public OBSERVRefactoring mappingRefactor(Hashtable<String,Integer> genetic_marker, 
			QubitArray genome,
			MetaphorCode code) {
		
		QubitArray reader_genome = genome;
		
		List<OBSERVRefParam> params = new ArrayList<OBSERVRefParam>();
		
		//Creating the OBSERVRefParam for the src class
		//This number represent a position for obtaining a class in builder
		int numSrcObs = 0;
		int Q_TAM = BitArrayConverter.getNumber(
				reader_genome.get(genetic_marker.get("BASE").intValue() + genetic_marker.get("TAM").intValue() ).getObservationQubit(), 
				0, 
				reader_genome.get(genetic_marker.get("BASE").intValue() + genetic_marker.get("TAM").intValue() ).getObservationQubit().size()); 
		int window = 0;
		for(int i=0; i < Q_TAM; i++){
			numSrcObs = BitArrayConverter.getNumber(genome, 0, genome.size());
			List<String> value_src  = new ArrayList<String>();
			TypeDeclaration sysType_src = code.getMapClass().get(numSrcObs % 
					code.getMapClass().size());
			value_src.add(sysType_src.getQualifiedName());
			params.add(new OBSERVRefParam("src", value_src));
		}
		//Creating the OBSERVRefParam for the fld field
		//The first half of the code is fld value
		int numFldObs = BitArrayConverter.getNumber(genome, 0, genome.size()/2);
		List<String> value_fld  = new ArrayList<String>();
		value_fld.add((String) code.getFieldsFromClass(sysType_src).toArray()[numFldObs
		  % code.getFieldsFromClass(sysType_src).size()]);
		params.add(new OBSERVRefParam("fld", value_fld));
		
		//Creating the OBSERVRefParam for the tgt
		//The first half of the code is tgt value
		int numTgtObs = BitArrayConverter.getNumber(genome, genome.size()/2, genome.size()/2);
		List<String> value_tgt  = new ArrayList<String>();
		TypeDeclaration sysType_tgt = code.getMapClass().get(numTgtObs % 
				code.getMapClass().size());
		value_tgt.add( sysType_tgt.getQualifiedName());
		params.add(new OBSERVRefParam("tgt", value_tgt));
		
		
		return new OBSERVRefactoring(type.name(),params);
	}

	@Override
	public List<OBSERVRefParam> mappingParams() {
		// TODO Auto-generated method stub
		return null;
	}

}
