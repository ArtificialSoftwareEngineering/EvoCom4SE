/**
 * 
 */
package space;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.AttributeDeclaration;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
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


	@Override
	public boolean feasibleRefactor(RefactoringOperation ref, MetaphorCode code) {
		// TODO Auto-generated method stub
		boolean feasible = true;

		//Extracting the source class
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

		//Extracting the target class
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

		//Extracting field of source class
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


		//Verification Field in Source Class
		for(TypeDeclaration src_class : src){
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

		//Verification SRCsubClassTGT
		for(TypeDeclaration src_class : src){
			if( !code.getBuilder().getParentClasses().get( src_class.getQualifiedName()).isEmpty() ){
				for(TypeDeclaration tgt_class : tgt){
					feasible = false;
					for( TypeDeclaration clase_parent : code.getBuilder().getParentClasses().get( src_class.getQualifiedName()) ){
						if( clase_parent.equals(tgt_class) ) 
							feasible = true;
					}
					if( !feasible )
						return false;
				}
			}else{
				return false;
			}
		}


		return feasible;
	}


	@Override
	public OBSERVRefactoring repairRefactor(RefactoringOperation ref, MetaphorCode code) {
		// TODO Auto-generated method stub
		OBSERVRefactoring refRepair = null;
		int counter = 0;

		boolean feasible;
		List<OBSERVRefParam> params;
		//IntUniform g = new IntUniform ( code.getMapClass().size() );
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
			//sysType_tgt = code.getMapClass().get( g.generate() );
			sysType_tgt = (TypeDeclaration) ref.getParams().get("tgt").get(0).getCodeObj();
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

			counter++;

			if(!feasible && counter > 10)
				break;

		}while( !feasible );

		params.add(new OBSERVRefParam("src", value_src));
		params.add(new OBSERVRefParam("fld", value_fld));
		params.add(new OBSERVRefParam("tgt", value_tgt));

		refRepair = new OBSERVRefactoring(type.name(),params,feasible);

		if(!feasible && counter > 10)
			refRepair = generatingRefactor( code );

		return refRepair;
	}

}
