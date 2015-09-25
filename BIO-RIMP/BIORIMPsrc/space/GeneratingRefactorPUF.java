/**
 * 
 */
package space;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MetaphorCode;
import unalcol.random.integer.IntUniform;
import unalcol.random.util.RandBool;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.collection.bitarray.BitArrayConverter;

/**
 * @author Daavid
 *
 */
public class GeneratingRefactorPUF extends GeneratingRefactor {
	
	protected Refactoring type = Refactoring.pullUpField;
	
	
	@Override
	public OBSERVRefactoring generatingRefactor(
			MetaphorCode code) {
		
		boolean feasible;
		List<OBSERVRefParam> params;
		IntUniform g = new IntUniform ( code.getMapClass().size() );
		TypeDeclaration sysType_src = null;
		TypeDeclaration sysType_tgt;
		List<String> value_src;
		List<String> value_fld = null;
		List<String> value_tgt ;
		
	
		do{
			feasible = true;
			params = new ArrayList<OBSERVRefParam>();
			//Creating the OBSERVRefParam for the tgt/super class
			value_tgt  = new ArrayList<String>();
			sysType_tgt = code.getMapClass().get( g.generate() );
			value_tgt.add( sysType_tgt.getQualifiedName() );
	
			//Creating the OBSERVRefParam for the src class
			value_src  = new ArrayList<String>();
			
			//verification of SRCSubClassTGT
			if(! code.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty() ){
				List<TypeDeclaration> clases = code.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
				IntUniform indexClass = new IntUniform ( clases.size() );
				sysType_src = clases.get( indexClass.generate() ); //RandomlySelectedClass
				
				//Creating the OBSERVRefParam for the fld field
				value_fld  = new ArrayList<String>();
				if( !code.getFieldsFromClass(sysType_src).isEmpty() ){
					
					IntUniform numFldObs = new IntUniform ( code.getFieldsFromClass(sysType_src).size() );
					value_fld.add((String) code.getFieldsFromClass(sysType_src).toArray()
							[ numFldObs.generate() ]);
					
					//Choosing other src(s) with the fld
					for(TypeDeclaration clase : clases){
						for(String field : code.getFieldsFromClass(clase)){
							if( field.equals(value_fld.get(0)) ){
								value_src.add(clase.getQualifiedName());
							}
						}
					}
					
				}else{
					feasible = false;
				}
				
			}else{
				feasible = false;
			}
		}while( !feasible );
		
		params.add(new OBSERVRefParam("src", value_src));
		params.add(new OBSERVRefParam("fld", value_fld));
		params.add(new OBSERVRefParam("tgt", value_tgt));
		
		return new OBSERVRefactoring(type.name(),params,feasible);
	}

}
