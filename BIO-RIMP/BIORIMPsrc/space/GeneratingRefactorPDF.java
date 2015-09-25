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
import unalcol.random.util.RandBool;
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
			List<String> value_tgt  = new ArrayList<String>();
			
			//Verification of SRCSupClassTGT
			//Retriving all child classes and choosing randomly
			if(! code.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty() ){
				List<TypeDeclaration> clases = code.getBuilder().getChildClasses().get(sysType_src.getQualifiedName());
				RandBool gC = new RandBool();
				do{
					for(TypeDeclaration clase : clases){
						if( gC.next() ){
							value_tgt.add(clase.getQualifiedName());
						}
					}
				}while( value_tgt.isEmpty() );
				params.add(new OBSERVRefParam("tgt", value_tgt));
			}else{
				feasible = false;
			}
			
		}while( !feasible );//Checking Subclasses for SRC selected
		
		return new OBSERVRefactoring(type.name(),params,feasible);
	}
}
