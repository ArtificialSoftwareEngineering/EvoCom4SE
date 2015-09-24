/**
 * 
 */
package space;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MappingRefactor.Refactoring;
import entity.MetaphorCode;
import unalcol.random.integer.IntUniform;
import unalcol.types.collection.bitarray.BitArray;

/**
 * @author Daavid
 *
 */
public class GeneratingRefactorPDF extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.pushDownField;
	
	@Override
	public OBSERVRefactoring generatingRefactor( MetaphorCode code ) {
		// TODO Auto-generated method stub
		boolean feasible;
		List<OBSERVRefParam> params;
		IntUniform g = new IntUniform ( code.getMapClass().size() );
		TypeDeclaration sysType_src;
		
		do{
			feasible = true;
			params = new ArrayList<OBSERVRefParam>();
			
			//Creating the OBSERVRefParam for the src class
			sysType_src =  code.getMapClass().get( g.generate() );
			List<String> value_src  = new ArrayList<String>();
			value_src.add(sysType_src.getQualifiedName());
			params.add(new OBSERVRefParam("src", value_src));
			
			//Creating the OBSERVRefParam for the fld field
			List<String> value_fld  = new ArrayList<String>();
			if(!code.getFieldsFromClass(sysType_src).isEmpty()){
				IntUniform numFldObs = new IntUniform ( code.getFieldsFromClass(sysType_src).size() );
				value_fld.add((String) code.getFieldsFromClass(sysType_src).toArray()
						[ numFldObs.generate() ]);
				params.add(new OBSERVRefParam("fld", value_fld));
			}else{
				feasible = false;
			}
		}while( !feasible );
		
		//Creating the OBSERVRefParam for the tgt class
		TypeDeclaration sysType_tgt = null;
		List<String> value_tgt  = new ArrayList<String>();
		int numTgtObs = 0;
		
		for(int i = 0; i < genome.getGenTGT().size(); i = i+genome.getTGT()){
			numTgtObs = genome.getNumberGenome(genome.getGenTGT(), i, genome.getTGT());
			sysType_tgt = code.getMapClass().get(numTgtObs % 
					code.getMapClass().size());
			value_tgt.add(sysType_tgt.getQualifiedName());
			
			//verification of SRCSupClassTGT
			if(! code.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty() ){
				List<TypeDeclaration> clases = code.getBuilder().getChildClasses().get(sysType_src.getQualifiedName());
				feasible = false;
				for(TypeDeclaration clase : clases){
					if(clase.getQualifiedName().equals(value_tgt.get(i))){
						feasible = true;
						break;
					}
				}
			}else{
				feasible = false;
			}
		}
		
		
		params.add(new OBSERVRefParam("tgt", value_tgt));
		
		return new OBSERVRefactoring(type.name(),params,feasible);
	}
}
