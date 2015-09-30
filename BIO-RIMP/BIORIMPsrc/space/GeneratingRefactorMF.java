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
public class GeneratingRefactorMF extends GeneratingRefactor {

	/* (non-Javadoc)
	 * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

	protected Refactoring type = Refactoring.moveField;

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
			sysType_src = code.getMapClass().get( g.generate() );
			List<String> value_src  = new ArrayList<String>();
			value_src.add(sysType_src.getQualifiedName());
			params.add(new OBSERVRefParam("src", value_src));

			//Creating the OBSERVRefParam for the fld field
			List<String> value_fld  = new ArrayList<String>();
			if(!code.getFieldsFromClass(sysType_src).isEmpty()){
				IntUniform numFldObs = new IntUniform ( code.getFieldsFromClass(sysType_src).size() );

				value_fld.add((String) code.getFieldsFromClass(sysType_src).toArray()
						[numFldObs.generate() ]);
				params.add(new OBSERVRefParam("fld", value_fld));
			}else{
				value_fld.add("");
				params.add(new OBSERVRefParam("fld", value_fld ));
				feasible = false;
			}

		}while( !feasible );

		//Creating the OBSERVRefParam for the tgt

		List<String> value_tgt  = new ArrayList<String>();
		TypeDeclaration sysType_tgt = code.getMapClass().get( g.generate() );
		value_tgt.add( sysType_tgt.getQualifiedName() );
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


		return feasible;
	}

	@Override
	public OBSERVRefactoring repairRefactor(RefactoringOperation ref, MetaphorCode code) {
		// TODO Auto-generated method stub
		OBSERVRefactoring refRepair = null;
		int counter = 0;

		boolean feasible;
		List<OBSERVRefParam> params;		
		TypeDeclaration sysType_src;
		//IntUniform g = new IntUniform ( code.getMapClass().size() );

		do{
			feasible = true;
			params = new ArrayList<OBSERVRefParam>();	

			//Creating the OBSERVRefParam for the src class
			//sysType_src = code.getMapClass().get( g.generate() );
			sysType_src = (TypeDeclaration) ref.getParams().get("src").get(0).getCodeObj();
			List<String> value_src  = new ArrayList<String>();
			value_src.add(sysType_src.getQualifiedName());
			params.add(new OBSERVRefParam("src", value_src));

			//Creating the OBSERVRefParam for the fld field
			List<String> value_fld  = new ArrayList<String>();
			if(!code.getFieldsFromClass(sysType_src).isEmpty()){
				IntUniform numFldObs = new IntUniform ( code.getFieldsFromClass(sysType_src).size() );

				value_fld.add((String) code.getFieldsFromClass(sysType_src).toArray()
						[numFldObs.generate() ]);
				params.add(new OBSERVRefParam("fld", value_fld));
			}else{
				value_fld.add("");
				params.add(new OBSERVRefParam("fld", value_fld ));
				feasible = false;
			}

			counter++;

			if(!feasible && counter > 10)
				break;

		}while( !feasible );

		//Creating the OBSERVRefParam for the tgt

		List<String> value_tgt  = new ArrayList<String>();
		//TypeDeclaration sysType_tgt = code.getMapClass().get( g.generate() );
		TypeDeclaration sysType_tgt = (TypeDeclaration) ref.getParams().get("tgt").get(0).getCodeObj();
		value_tgt.add( sysType_tgt.getQualifiedName() );
		params.add(new OBSERVRefParam("tgt", value_tgt));

		refRepair = new OBSERVRefactoring(type.name(),params,feasible);

		if(!feasible && counter > 10)
			refRepair = generatingRefactor( code );

		return refRepair;
	}

}
