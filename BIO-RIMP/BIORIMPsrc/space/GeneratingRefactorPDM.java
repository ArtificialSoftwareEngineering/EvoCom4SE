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
public class GeneratingRefactorPDM extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.pushDownMethod;
	
	@Override
	public OBSERVRefactoring generatingRefactor( MetaphorCode code ) {
		// TODO Auto-generated method stub
		boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform ( code.getMapClass().size() );
        TypeDeclaration sysType_src;
        TypeDeclaration sysType_src_parent;
        List<String> value_tgt; 
        List<String> value_src;
        List<String> value_mtd;
        
        do{
	        do{
	        	feasible = true;
	        	params = new ArrayList<OBSERVRefParam>();
	        	
				//Creating the OBSERVRefParam for the src class/super class
				sysType_src =  code.getMapClass().get( g.generate() );
				value_src  = new ArrayList<String>();
				value_src.add( sysType_src.getQualifiedName() );
				
				//Creating the OBSERVRefParam for the mtd class
				value_mtd  = new ArrayList<String>();
				if( !code.getMethodsFromClass(sysType_src).isEmpty() ){
					
					IntUniform numMtdObs = new IntUniform ( code.getMethodsFromClass(sysType_src).size() );
					
					value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()
							[ numMtdObs.generate() ]);
					
					//Override verification
					if(! code.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()).isEmpty() ){
						for( TypeDeclaration clase : code.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()) ){
							for( String method : code.getMethodsFromClass(clase) ){
								if( method.equals( value_mtd.get(0) ) ){
									feasible = false;
									break;
								}
							}
						}
					}
					
					if( feasible ){
						//verification of method not constructor
						if(value_mtd.get(0).equals(sysType_src.getName()))
							feasible = false;
					}
					
				}else{
					feasible = false;
				}
	        }while( !feasible );
	        
			//Creating the OBSERVRefParam for the tgt class/child classes
			value_tgt  = new ArrayList<String>();
			
			//Verification of SRCSupClassTGT
			//Retriving all child classes and choosing randomly
			if(! code.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty() ){
				List<TypeDeclaration> clases = code.getBuilder().getChildClasses().get(sysType_src.getQualifiedName());
				RandBool gC = new RandBool();
				for(TypeDeclaration clase : clases){
					if( gC.next() ){
						value_tgt.add(clase.getQualifiedName());
					}
				}
				
			}else{
				feasible = false;
			}

        }while( !feasible );//Checking Subclasses for SRC selected
        
        params.add(new OBSERVRefParam("src", value_src));
        params.add(new OBSERVRefParam("mtd", value_mtd));
        params.add(new OBSERVRefParam("tgt", value_tgt));
		return new OBSERVRefactoring(type.name(),params,feasible);
	}

}
