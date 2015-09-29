/**
 * 
 */
package space;

import java.util.ArrayList;
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
public class GeneratingRefactorIM extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

	protected Refactoring type = Refactoring.inlineMethod;

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
		}while( !feasible );//generating feasible individuals

		return new OBSERVRefactoring(type.name(),params,feasible);
	}

	@Override
	public boolean feasibleRefactor(RefactoringOperation ref, MetaphorCode code) {
		// TODO Auto-generated method stub
		boolean feasible = true;

		//1. Extracting the source class
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


		//2. Extracting method of source class
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

		//3. Verification Method in Source Class
		for(TypeDeclaration src_class : src){
			for(MethodDeclaration metodo : mtd){
				if ( code.getMethodsFromClass(src_class) != null )
					if( !code.getMethodsFromClass(src_class).isEmpty() )
						for(String method : code.getMethodsFromClass(src_class)){
							if(   metodo.getObjName().equals(  method  )  )
								feasible = false;	//check the logic is wrong!!
						}
				if( feasible )
					return false;
				else
					feasible = true;
			}			
		}

		//4. verification of method not constructor
		for(TypeDeclaration src_class : src){
			for(MethodDeclaration metodo : mtd){
				if(  src_class.getName().equals(  metodo.getObjName()  )  )
					return false;	
			}
		}

		for(TypeDeclaration src_class : src){
			for(MethodDeclaration metodo : mtd){
				//5. Override verification parents 
				if( !code.getBuilder().getParentClasses().get( src_class.getQualifiedName()).isEmpty() ){
					for( TypeDeclaration clase_parent : code.getBuilder().getParentClasses().get( src_class.getQualifiedName()) ){
						if ( code.getMethodsFromClass(clase_parent) != null )
							if( !code.getMethodsFromClass(clase_parent).isEmpty() ){
								for( String method : code.getMethodsFromClass(clase_parent) ){
									if( method.equals( metodo.getObjName() ) ){
										return false;	
									}
								}
							}
					}
				}

				//6. Override verification children
				if( !code.getBuilder().getChildClasses().get( src_class.getQualifiedName()).isEmpty() ){
					for( TypeDeclaration clase_child : code.getBuilder().getChildClasses().get( src_class.getQualifiedName()) ){
						if ( code.getMethodsFromClass(clase_child) != null )
							if( !code.getMethodsFromClass(clase_child).isEmpty() ){
								for( String method : code.getMethodsFromClass(clase_child) ){
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

	@Override
	public OBSERVRefactoring repairRefactor(RefactoringOperation ref, MetaphorCode code) {
		// TODO Auto-generated method stub
		boolean feasible;
		OBSERVRefactoring refRepair = null;
		List<OBSERVRefParam> params;
		List<String> value_src;
		List<String> value_mtd;
		List<TypeDeclaration> src;
		List<MethodDeclaration> mtd;
		IntUniform g = new IntUniform ( code.getMapClass().size() );
		TypeDeclaration sysType_src = null;

		do{
			feasible = true;
			params = new ArrayList<OBSERVRefParam>();

			//1. Extracting the source class
			src = new ArrayList<TypeDeclaration>();
			if( ref.getParams().get("src") != null ){
				if( !ref.getParams().get("src").isEmpty() ){
					value_src  = new ArrayList<String>();
					for(RefactoringParameter param_src : ref.getParams().get("src") ){
						src.add( (TypeDeclaration) param_src.getCodeObj() );
						value_src.add( param_src.getCodeObj().toString() );
					}
					params.add(new OBSERVRefParam("src", value_src));
				}else{
					//Creating the OBSERVRefParam for the src class
					sysType_src =  code.getMapClass().get( g.generate() );
					value_src  = new ArrayList<String>();
					value_src.add(sysType_src.getQualifiedName());
					params.add(new OBSERVRefParam("src", value_src));
				}
			}else{
				//Creating the OBSERVRefParam for the src class
				sysType_src =  code.getMapClass().get( g.generate() );
				value_src  = new ArrayList<String>();
				value_src.add(sysType_src.getQualifiedName());
				params.add(new OBSERVRefParam("src", value_src));
			}

			//2. Extracting method of source class
			mtd = new ArrayList<MethodDeclaration>();
			if( ref.getParams().get("mtd") != null ){
				if( !ref.getParams().get("mtd").isEmpty() ){
					value_mtd  = new ArrayList<String>();
					for(RefactoringParameter param_mtd : ref.getParams().get("mtd") ){
						mtd.add( (MethodDeclaration) param_mtd.getCodeObj() );
						value_mtd.add(param_mtd.getCodeObj().toString()) ;
					}
					params.add(new OBSERVRefParam("mtd", value_mtd));
				}else{
					//Creating the OBSERVRefParam for the mtd class
					value_mtd  = new ArrayList<String>();
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
				}
			}else{
				//Creating the OBSERVRefParam for the mtd class
				value_mtd  = new ArrayList<String>();
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
			}

			refRepair = new OBSERVRefactoring(type.name(),params,feasible);
		}while( !feasible );//generating feasible individuals

		//3. Verification Method in Source Class
		if( !src.isEmpty() )
			for(TypeDeclaration src_class : src){
				for(MethodDeclaration metodo : mtd){
					if ( code.getMethodsFromClass(src_class) != null )
						if( !code.getMethodsFromClass(src_class).isEmpty() )
							for(String method : code.getMethodsFromClass(src_class)){
								if(   metodo.getObjName().equals(  method  )  )
									feasible = false;	//check the logic is wrong!!
							}
					if( feasible )
						refRepair = generatingRefactor( code );
					else
						feasible = true;
				}			
			}

		//4. verification of method not constructor
		if( !src.isEmpty() && feasible)
			for(TypeDeclaration src_class : src){
				for(MethodDeclaration metodo : mtd){
					if(  src_class.getName().equals(  metodo.getObjName()  )  ){
						refRepair = generatingRefactor( code );
						feasible = false;
						break;
					}
				}
				if(!feasible)
					break;
			}

		if( !src.isEmpty() && feasible)
			for(TypeDeclaration src_class : src){
				for(MethodDeclaration metodo : mtd){
					//5. Override verification parents 
					if( !code.getBuilder().getParentClasses().get( src_class.getQualifiedName()).isEmpty() ){
						for( TypeDeclaration clase_parent : code.getBuilder().getParentClasses().get( src_class.getQualifiedName()) ){
							if ( code.getMethodsFromClass(clase_parent) != null )
								if( !code.getMethodsFromClass(clase_parent).isEmpty() ){
									for( String method : code.getMethodsFromClass(clase_parent) ){
										if( method.equals( metodo.getObjName() ) ){
											refRepair = generatingRefactor( code );
											feasible = false;
											break;
										}
									}
								}
							if(!feasible)
								break;
						}
					}

					//6. Override verification children
					if(!feasible)
						if( !code.getBuilder().getChildClasses().get( src_class.getQualifiedName()).isEmpty() ){
							for( TypeDeclaration clase_child : code.getBuilder().getChildClasses().get( src_class.getQualifiedName()) ){
								if ( code.getMethodsFromClass(clase_child) != null )
									if( !code.getMethodsFromClass(clase_child).isEmpty() ){
										for( String method : code.getMethodsFromClass(clase_child) ){
											if( method.equals( metodo.getObjName() ) ){
												refRepair = generatingRefactor( code );
												feasible = false;
												break;
											}
										}
									}
								if(!feasible)
									break;
							}
						}
				}//end for metodo
			}//enf for src_class		

		return refRepair;
	}

}
