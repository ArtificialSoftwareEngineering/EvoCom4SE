/**
 * 
 */
package space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.AttributeDeclaration;
import edu.wayne.cs.severe.redress2.entity.MethodDeclaration;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MetaphorCode;
import unalcol.random.integer.IntUniform;
import unalcol.types.collection.bitarray.BitArray;

/**
 * @author Daavid
 *
 */



public class GeneratingRefactorEM extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */
	
	protected Refactoring type = Refactoring.extractMethod;
	
	@Override
	public OBSERVRefactoring generatingRefactor( MetaphorCode code ) {
		// TODO Auto-generated method stub
		boolean feasible;
		List<OBSERVRefParam> params;
		TypeDeclaration sysType_src;
		IntUniform g = new IntUniform ( code.getMapClass().size() );
	
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
			if( !code.getMethodsFromClass(sysType_src).isEmpty() ){
				IntUniform numMtdObs = new IntUniform ( code.getMethodsFromClass(sysType_src).size() );
							
				value_mtd.add((String) code.getMethodsFromClass(sysType_src).toArray()
						[ numMtdObs.generate() ]);
				
				//verification of method not constructor
				if(value_mtd.get(0).equals(sysType_src.getName()))
					feasible = false;
				
				if(feasible){
					//Override verification parents 
					if( !code.getBuilder().getParentClasses().get( sysType_src.getQualifiedName()).isEmpty() ){
						for( TypeDeclaration clase : code.getBuilder().getParentClasses().get( sysType_src.getQualifiedName()) ){
							if ( code.getMethodsFromClass(clase) != null )
							if( !code.getMethodsFromClass(clase).isEmpty() ){
								for( String method : code.getMethodsFromClass(clase) ){
									if( method.equals( value_mtd.get(0) ) ){
										feasible = false;
										break;
									}
								}
							}
						}
					}
					if(feasible){
						//Override verification children
						if( !code.getBuilder().getChildClasses().get( sysType_src.getQualifiedName()).isEmpty() ){
							for( TypeDeclaration clase : code.getBuilder().getChildClasses().get( sysType_src.getQualifiedName()) ){
								if ( code.getMethodsFromClass(clase) != null )
								if( !code.getMethodsFromClass(clase).isEmpty() ){
									for( String method : code.getMethodsFromClass(clase) ){
										if( method.equals( value_mtd.get(0) ) ){
											feasible = false;
											break;
										}
									}
								}
							}
						}
					}
				}
				
				params.add(new OBSERVRefParam("mtd", value_mtd));
			}else{
				feasible = false;
			}
		}while( !feasible ); //Generating only feasible individuals
		
		return new OBSERVRefactoring(type.name(),params,feasible);
	}

	@Override
	public boolean feasibleRefactor(RefactoringOperation ref, MetaphorCode code) {
		// TODO Auto-generated method stub
		boolean feasible = true;
		
		List<TypeDeclaration> src = new ArrayList<TypeDeclaration>();
		if( ref.getParams().get("src") != null ){
			if( !ref.getParams().get("src").isEmpty() ){
				for(RefactoringParameter param_src : ref.getParams().get("src") ){
					src.add( (TypeDeclaration) param_src.getCodeObj() );
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
		
		List<TypeDeclaration> tgt = new ArrayList<TypeDeclaration>();
		if( ref.getParams().get("tgt") != null ){
			if( !ref.getParams().get("tgt").isEmpty() ){
				for(RefactoringParameter param_tgt : ref.getParams().get("tgt") ){
					tgt.add( (TypeDeclaration) param_tgt.getCodeObj() );
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
		
        List<AttributeDeclaration> fld = new ArrayList<AttributeDeclaration>();
		if( ref.getParams().get("fld") != null ){
			if( !ref.getParams().get("fld").isEmpty() ){
				for(RefactoringParameter param_fld : ref.getParams().get("fld") ){
					fld.add( (AttributeDeclaration) param_fld.getCodeObj() );
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	
		
		List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
		if( ref.getParams().get("mtd") != null ){
			if( !ref.getParams().get("mtd").isEmpty() ){
				for(RefactoringParameter param_mtd : ref.getParams().get("mtd") ){
					mtd.add( (MethodDeclaration) param_mtd.getCodeObj() );
				}
			}else{
				return false;
			}
		}else{
			return false;
		}

	    //verification of method not constructor
		for(TypeDeclaration src_class : src){
			for(MethodDeclaration metodo : mtd){
				if(  src_class.getName().equals(  metodo.getObjName()  )  )
						return false;	
			}
		}
		
		for(TypeDeclaration src_class : src){
			for(MethodDeclaration metodo : mtd){
				//Override verification parents 
				if( !code.getBuilder().getParentClasses().get( src_class.getQualifiedName()).isEmpty() ){
					for( TypeDeclaration clase : code.getBuilder().getParentClasses().get( src_class.getQualifiedName()) ){
						if ( code.getMethodsFromClass(clase) != null )
						if( !code.getMethodsFromClass(clase).isEmpty() ){
							for( String method : code.getMethodsFromClass(clase) ){
								if( method.equals( metodo.getObjName() ) ){
									return false;	
								}
							}
						}
					}
				}
				
				//Override verification children
				if( !code.getBuilder().getChildClasses().get( src_class.getQualifiedName()).isEmpty() ){
					for( TypeDeclaration clase : code.getBuilder().getChildClasses().get( src_class.getQualifiedName()) ){
						if ( code.getMethodsFromClass(clase) != null )
						if( !code.getMethodsFromClass(clase).isEmpty() ){
							for( String method : code.getMethodsFromClass(clase) ){
								if( method.equals( metodo.getObjName() ) ){
									return false;	
								}
							}
						}
					}
				}
			}//end for metodo
		}//enf for src_class
		
		return feasible;
	}

}
