package space;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.AttributeDeclaration;
import edu.wayne.cs.severe.redress2.entity.MethodDeclaration;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entities.Register;
import entity.MetaphorCode;
import repositories.RegisterRepository;
import unalcol.types.collection.bitarray.BitArray;

public abstract class GeneratingRefactor {
	
	private OBSERVRefactoring refactor;
	protected Refactoring type;
	
	public abstract OBSERVRefactoring generatingRefactor( MetaphorCode code );
	
	public abstract boolean feasibleRefactor( RefactoringOperation ref, MetaphorCode code );
	
	public abstract OBSERVRefactoring repairRefactor ( RefactoringOperation  ref, MetaphorCode code, int break_point);

	//1000 danaderp dynamic feasiability
	//Return true if a refactoring has already in the data base
	protected boolean feasibleRefactorbyRecalling (RefactoringOperation operRef){
		boolean bandera = true;
		//Verificacion de llaves
		String src="";
		String tgt="";
		String fld, mtd;
		if(operRef.getParams() != null ){
			//si es un extract class memoriza sub-refs
			if( !(operRef.getRefType().getAcronym().equals("EC") || operRef.getRefType().getAcronym().equals("RMMO" ))) {
				if(operRef.getParams().get("src") != null ) {
					if( !operRef.getParams().get("src").isEmpty()  ){ //valida si es vac�o
						for (RefactoringParameter obj : operRef.getParams().get("src")) {
							src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";// 45,67
						}
						src= src.substring(0,src.length()-1);
					}
				}

				if( operRef.getParams().get("tgt") != null ) {
					if( !operRef.getParams().get("tgt").isEmpty() ){
						for (RefactoringParameter obj : operRef.getParams().get("tgt")) {
							tgt += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";
						}
						tgt = tgt.substring(0, tgt.length() - 1);
					}
				}

				if(operRef.getParams().get("fld") != null ){
					if( !operRef.getParams().get("fld").isEmpty() )
						fld = ((AttributeDeclaration) operRef.getParams().get("fld").get(0).getCodeObj()).getObjName();
					else
						fld = "-1";
				}else{
					fld = "-1";
				}

				if(operRef.getParams().get("mtd") != null ){
					if( !operRef.getParams().get("mtd").isEmpty() )
						mtd = ((MethodDeclaration) operRef.getParams().get("mtd").get(0).getCodeObj()).getObjName();
					else 
						mtd = "-1";
				}else{ mtd = "-1";}

				RegisterRepository repo = new RegisterRepository();
				List<Register> listMetric =  repo.getRegistersByClass(operRef.getRefType().getAcronym(), src,tgt,mtd,fld);

				bandera = !listMetric.isEmpty();
			}else{//Si es  EC no guarda
				bandera = false;
			}
		}else{//if no params, no recall
			bandera = false;
		}
		return bandera;
	}
	//1000
	
	public OBSERVRefactoring getRefactor() {
		return refactor;
	}

	public void setRefactor(OBSERVRefactoring refactor) {
		this.refactor = refactor;
	}

}
