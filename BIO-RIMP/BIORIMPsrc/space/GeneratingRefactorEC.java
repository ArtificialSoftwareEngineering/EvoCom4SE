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
public class GeneratingRefactorEC extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

	protected Refactoring type = Refactoring.extractClass;

	@Override
	public OBSERVRefactoring generatingRefactor( MetaphorCode code ) {
		// TODO Auto-generated method stub
		return mappingRefactorMFMM(code, "TgtClassEC" );
	}

	public OBSERVRefactoring mappingRefactorMFMM( MetaphorCode code, String newClass ) {
		// TODO Auto-generated method stub


		boolean feasible;
		List<OBSERVRefactoring> subRefs = new ArrayList<OBSERVRefactoring>();
		List<OBSERVRefParam> paramsMF;	
		List<OBSERVRefParam> paramsMM;
		TypeDeclaration sysType_src;
		IntUniform g = new IntUniform ( code.getMapClass().size() );

		do{
			feasible = true;
			paramsMF = new ArrayList<OBSERVRefParam>();	
			paramsMM = new ArrayList<OBSERVRefParam>();

			//Creating the OBSERVRefParam for the src class

			sysType_src = code.getMapClass().get( g.generate()  );
			List<String> value_src  = new ArrayList<String>();
			value_src.add(sysType_src.getQualifiedName());
			paramsMF.add(new OBSERVRefParam("src", value_src));
			paramsMM.add(new OBSERVRefParam("src", value_src));

			//Creating the OBSERVRefParam for the fld field
			List<String> value_fld  = new ArrayList<String>();
			if(!code.getFieldsFromClass(sysType_src).isEmpty()){
				IntUniform numFldObs = new IntUniform ( code.getFieldsFromClass(sysType_src).size() );

				String fldName = (String) code.getFieldsFromClass(sysType_src).toArray()
						[ numFldObs.generate() ];
				value_fld.add(fldName);
				paramsMF.add(new OBSERVRefParam("fld", value_fld));
			}else{
				feasible = false; 
			}

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
								if( !code.getMethodsFromClass(clase).isEmpty()  ){
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


				paramsMM.add(new OBSERVRefParam("mtd", value_mtd));
			}else{
				feasible = false; 
			}

		}while(!feasible); //Generate Just feasible individuals

		//Creating the OBSERVRefParam for the tgt
		//This Target Class is not inside metaphor
		List<String> value_tgt  = new ArrayList<String>();
		value_tgt.add( sysType_src.getPack() + newClass + "|N");
		paramsMF.add(new OBSERVRefParam("tgt", value_tgt));
		paramsMM.add(new OBSERVRefParam("tgt", value_tgt));
		code.addClasstoHash(sysType_src.getPack(), newClass + "|N");

		subRefs.add( new OBSERVRefactoring(Refactoring.moveField.name(),paramsMF, feasible ) );
		subRefs.add( new OBSERVRefactoring(Refactoring.moveMethod.name(),paramsMM, feasible ) );

		return new OBSERVRefactoring(type.name(), null ,subRefs,feasible);
	}

	@Override
	public boolean feasibleRefactor(RefactoringOperation ref, MetaphorCode code ) {
		// TODO Auto-generated method stub
		boolean feasible = true;

		//Extracting the source class MF
		List<TypeDeclaration> src_MF = new ArrayList<TypeDeclaration>();
		//if( ref.getSubRefs().get(0).getParams().get("src") != null ){

		if( ref.getSubRefs() != null ){
			if( !ref.getSubRefs().get(0).getParams().get("src").isEmpty() ){
				for(RefactoringParameter param_src : ref.getSubRefs().get(0).getParams().get("src") ){
					src_MF.add( (TypeDeclaration) param_src.getCodeObj() );
				}
			}else{
				return false;
			}


			//Extracting the source class MM
			List<TypeDeclaration> src_MM = new ArrayList<TypeDeclaration>();
			if( ref.getSubRefs().get(1).getParams().get("src") != null ){
				if( !ref.getSubRefs().get(1).getParams().get("src").isEmpty() ){
					for(RefactoringParameter param_src : ref.getSubRefs().get(1).getParams().get("src") ){
						src_MM.add( (TypeDeclaration) param_src.getCodeObj() );
					}
				}else{
					return false;
				}
			}else{
				return false;
			}		

			//Extracting the target class MF
			List<TypeDeclaration> tgt_MF = new ArrayList<TypeDeclaration>();
			if( ref.getSubRefs().get(0).getParams().get("tgt") != null ){
				if( !ref.getSubRefs().get(0).getParams().get("tgt").isEmpty() ){
					for(RefactoringParameter param_tgt : ref.getSubRefs().get(0).getParams().get("tgt") ){
						tgt_MF.add( (TypeDeclaration) param_tgt.getCodeObj() );
					}
				}else{
					return false;
				}
			}else{
				return false;
			}

			//Extracting the target class MM
			List<TypeDeclaration> tgt_MM = new ArrayList<TypeDeclaration>();
			if( ref.getSubRefs().get(1).getParams().get("tgt") != null ){
				if( !ref.getSubRefs().get(1).getParams().get("tgt").isEmpty() ){
					for(RefactoringParameter param_tgt : ref.getSubRefs().get(1).getParams().get("tgt") ){
						tgt_MM.add( (TypeDeclaration) param_tgt.getCodeObj() );
					}
				}else{
					return false;
				}
			}else{
				return false;
			}		

			//Extracting field of source class
			List<AttributeDeclaration> fld = new ArrayList<AttributeDeclaration>();
			if( ref.getSubRefs().get(0).getParams().get("fld") != null ){
				if( !ref.getSubRefs().get(0).getParams().get("fld").isEmpty() ){
					for(RefactoringParameter param_fld : ref.getSubRefs().get(0).getParams().get("fld") ){
						fld.add( (AttributeDeclaration) param_fld.getCodeObj() );
					}
				}else{
					return false;
				}
			}else{
				return false;
			}


			//Extracting method of source class
			List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
			if( ref.getSubRefs().get(1).getParams().get("mtd") != null ){
				if( !ref.getSubRefs().get(1).getParams().get("mtd").isEmpty() ){
					for(RefactoringParameter param_mtd : ref.getSubRefs().get(1).getParams().get("mtd") ){
						mtd.add( (MethodDeclaration) param_mtd.getCodeObj() );
					}
				}else{
					return false;
				}
			}else{
				return false;
			}



			//Verification Field in Source Class
			for(TypeDeclaration src_class : src_MF){
				for(AttributeDeclaration field : fld){
					if ( code.getFieldsFromClass(src_class) != null )
						if( !code.getFieldsFromClass(src_class).isEmpty() )
							for(String fiel : code.getFieldsFromClass(src_class)){
								if(   field.getObjName().equals(  fiel  )  )
									feasible = false;	//check the logic is wrong!!
							}
					if( feasible )
						return false;
					else
						feasible = true;
				}			
			}		

			//Verification Method in Source Class
			for(TypeDeclaration src_class : src_MM){
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

			//verification of method not constructor
			for(TypeDeclaration src_class : src_MM){
				for(MethodDeclaration metodo : mtd){
					if(  src_class.getName().equals(  metodo.getObjName()  )  )
						return false;	
				}
			}

			for(TypeDeclaration src_class : src_MM){
				for(MethodDeclaration metodo : mtd){
					//Override verification parents 
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

					//Override verification children
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

		}else{
			return false;
		}
		return feasible;
	}

	@Override
	public OBSERVRefactoring repairRefactor(RefactoringOperation ref, MetaphorCode code, int break_point) {
		// TODO Auto-generated method stub
		OBSERVRefactoring refRepair = null;
		int counter = 0;
		String newClass = "TgtClassEC";

		boolean feasible;
		List<OBSERVRefactoring> subRefs = new ArrayList<OBSERVRefactoring>();
		List<OBSERVRefParam> paramsMF;	
		List<OBSERVRefParam> paramsMM;
		TypeDeclaration sysType_src;
		IntUniform g = new IntUniform ( code.getMapClass().size() );

		do{
			feasible = true;
			paramsMF = new ArrayList<OBSERVRefParam>();	
			paramsMM = new ArrayList<OBSERVRefParam>();

			//Creating the OBSERVRefParam for the src class

			//sysType_src = code.getMapClass().get( g.generate()  );
			if( ref.getSubRefs()  != null ){
				if( !ref.getSubRefs().get(0).getParams().get("src").isEmpty() ){

					sysType_src = (TypeDeclaration) ref.getSubRefs().get(0).getParams().get("src").get(0).getCodeObj();
				}else{
					if( !ref.getParams().get("src").isEmpty() )
						sysType_src = (TypeDeclaration) ref.getParams().get("src").get(0).getCodeObj();
					else 
						sysType_src = code.getMapClass().get( g.generate()  );
				}

			}else{
				sysType_src = code.getMapClass().get( g.generate()  );
			}

			List<String> value_src  = new ArrayList<String>();
			value_src.add(sysType_src.getQualifiedName());
			paramsMF.add(new OBSERVRefParam("src", value_src));
			paramsMM.add(new OBSERVRefParam("src", value_src));

			//Creating the OBSERVRefParam for the fld field
			List<String> value_fld  = new ArrayList<String>();
			if(!code.getFieldsFromClass(sysType_src).isEmpty()){
				IntUniform numFldObs = new IntUniform ( code.getFieldsFromClass(sysType_src).size() );

				String fldName = (String) code.getFieldsFromClass(sysType_src).toArray()
						[ numFldObs.generate() ];
				value_fld.add(fldName);
				paramsMF.add(new OBSERVRefParam("fld", value_fld));
			}else{
				feasible = false; 
			}

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
								if( !code.getMethodsFromClass(clase).isEmpty()  ){
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


				paramsMM.add(new OBSERVRefParam("mtd", value_mtd));
			}else{
				feasible = false;
				break;
			}

			counter++;

			//if(!feasible && counter < break_point )
			if( counter < break_point )
				break;

		}while(!feasible); //Generate Just feasible individuals

		if( !feasible || counter < break_point ){
			refRepair = generatingRefactor( code );
		}else {

			//Creating the OBSERVRefParam for the tgt
			//This Target Class is not inside metaphor
			List<String> value_tgt  = new ArrayList<String>();
			value_tgt.add( sysType_src.getPack() + newClass + "|N");
			paramsMF.add(new OBSERVRefParam("tgt", value_tgt));
			paramsMM.add(new OBSERVRefParam("tgt", value_tgt));
			code.addClasstoHash(sysType_src.getPack(), newClass + "|N");

			subRefs.add( new OBSERVRefactoring(Refactoring.moveField.name(),paramsMF, feasible ) );
			subRefs.add( new OBSERVRefactoring(Refactoring.moveMethod.name(),paramsMM, feasible ) );

			refRepair = new OBSERVRefactoring(type.name(), null ,subRefs,feasible);
		}


		return refRepair;
	}

}
