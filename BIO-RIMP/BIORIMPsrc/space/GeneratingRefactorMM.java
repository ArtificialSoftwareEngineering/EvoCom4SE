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
		List<String> value_mtd;
		List<String> value_src;
		List<String> value_tgt;
		
		do{
			do{
				feasible = true;
				params = new ArrayList<OBSERVRefParam>();
				
				//Creating the OBSERVRefParam for the src class
				sysType_src =  code.getMapClass().get( g.generate() );
				value_src  = new ArrayList<String>();
				value_src.add(sysType_src.getQualifiedName());
				
				
				//Creating the OBSERVRefParam for the mtd class
				value_mtd  = new ArrayList<String>();
				if( !code.getMethodsFromClass(sysType_src).isEmpty() ){
					IntUniform numMtdObs = new IntUniform ( code.getMethodsFromClass(sysType_src).size() );
					
					value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()
							[ numMtdObs.generate() ]);

					//verification of method not constructor
					if(value_mtd.get(0).equals(sysType_src.getName()))
						feasible = false;
	
				}else{
					feasible = false;
				}
			}while( !feasible );
			
			//Creating the OBSERVRefParam for the tgt
			value_tgt  = new ArrayList<String>();
			TypeDeclaration sysType_tgt = code.getMapClass().get( g.generate() );
			value_tgt.add( sysType_tgt.getQualifiedName());
			
			
			//Override and hierarchy verification parents 
			if( !code.getBuilder().getParentClasses().get( sysType_src.getQualifiedName()).isEmpty() ){
				for( TypeDeclaration clase : code.getBuilder().getParentClasses().get( sysType_src.getQualifiedName()) ){
					if ( code.getMethodsFromClass(clase) != null )
					if( !code.getMethodsFromClass(clase).isEmpty() ){
						for( String method : code.getMethodsFromClass(clase) ){
							if( method.equals( value_mtd.get(0) ) || clase.equals(sysType_tgt) ){
								feasible = false;
								break;
							}
						}
					}
				}
			}
			
			if(feasible){
				//Override and hierarchy verification children
				if( !code.getBuilder().getChildClasses().get( sysType_src.getQualifiedName()).isEmpty() ){
					for( TypeDeclaration clase : code.getBuilder().getChildClasses().get( sysType_src.getQualifiedName()) ){
						if ( code.getMethodsFromClass(clase) != null )
						if( !code.getMethodsFromClass(clase).isEmpty() ){
							for( String method : code.getMethodsFromClass(clase) ){
								if( method.equals( value_mtd.get(0) ) || clase.equals(sysType_tgt) ){
									feasible = false;
									break;
								}
							}
						}
					}
				}
			}
		
		}while( !feasible );
		
		params.add(new OBSERVRefParam("src", value_src));
		params.add(new OBSERVRefParam("mtd", value_mtd));
		params.add(new OBSERVRefParam("tgt", value_tgt));
		
		return new OBSERVRefactoring(type.name(),params,feasible);

	}

}