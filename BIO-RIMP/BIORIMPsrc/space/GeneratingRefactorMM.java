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
public class GeneratingRefactorMM extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.moveMethod;
	
	@Override
	public OBSERVRefactoring generatingRefactor( MetaphorCode code) {
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
			
			//Creating the OBSERVRefParam for the mtd class
			List<String> value_mtd  = new ArrayList<String>();
			if(!code.getMethodsFromClass(sysType_src).isEmpty()){
				IntUniform numMtdObs = new IntUniform ( code.getMethodsFromClass(sysType_src).size() );
				
				value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()
						[ numMtdObs.generate() ]);
				
				//verification of method not constructor
				if(value_mtd.get(0).equals(sysType_src.getName()))
					feasible = false;
				
				params.add(new OBSERVRefParam("mtd", value_mtd));
			}else{
				feasible = false;
			}
		}while( !feasible );
		
		//Creating the OBSERVRefParam for the tgt
		List<String> value_tgt  = new ArrayList<String>();
		TypeDeclaration sysType_tgt = code.getMapClass().get( g.generate() );
		value_tgt.add( sysType_tgt.getQualifiedName());
		params.add(new OBSERVRefParam("tgt", value_tgt));
		
		
		return new OBSERVRefactoring(type.name(),params,feasible);

	}

}