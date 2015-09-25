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
public class GeneratingRefactorPUM extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.pullUpMethod;
	
	@Override
	public OBSERVRefactoring generatingRefactor( MetaphorCode code ) {
		// TODO Auto-generated method stub
		boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform ( code.getMapClass().size() );
        List<String> value_mtd = null ;
        List<String> value_tgt; 
        TypeDeclaration sysType_tgt; 
        TypeDeclaration sysType_src; 
        List<String> value_src;
        
        do{
        	feasible = true;
        	params = new ArrayList<OBSERVRefParam>();
        	
	        //Creating the OBSERVRefParam for the tgt
	      	value_tgt  = new ArrayList<String>();
	      	sysType_tgt = code.getMapClass().get( g.generate() );
	      	value_tgt.add( sysType_tgt.getQualifiedName());
	        
			//Creating the OBSERVRefParam for the src class
			value_src  = new ArrayList<String>();
			
			//verification of SRCSubClassTGT
			if(! code.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty() ){
				List<TypeDeclaration> clases = code.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
				IntUniform indexClass = new IntUniform ( clases.size() );
				sysType_src = clases.get( indexClass.generate() ); //RandomlySelectedClass
				
				//Creating the OBSERVRefParam for the mtd class randomly
				value_mtd  = new ArrayList<String>();
				
				if( !code.getMethodsFromClass(sysType_src).isEmpty() ){
					IntUniform numMtdObs = new IntUniform ( code.getMethodsFromClass(sysType_src).size() );
					value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()
							[ numMtdObs.generate()]);
					
					//Override verification
					if ( code.getMethodsFromClass(sysType_tgt) != null )
					if( !code.getMethodsFromClass(sysType_tgt).isEmpty() ){
						for ( String method : code.getMethodsFromClass( sysType_tgt ) ){
							if( method.equals( value_mtd.get(0) ) ){
								feasible = false;
								break;
							}
						}
					}
					
					if( feasible ){
						//verification of method not constructor
						if( value_mtd.get(0).equals( sysType_src.getName() ) ){
							feasible = false;
						}else{
							//Choosing other src(s) with the mtd
							for( TypeDeclaration clase : clases ){
								for( String method : code.getMethodsFromClass(clase) ){
									if( method.equals( value_mtd.get(0) ) ){
										value_src.add( clase.getQualifiedName() );
									}
								}
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
		params.add(new OBSERVRefParam("mtd", value_mtd));
		params.add(new OBSERVRefParam("tgt", value_tgt));
		
		return new OBSERVRefactoring(type.name(),params,feasible);
	}


}
