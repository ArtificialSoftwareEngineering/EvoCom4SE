package space;

import java.util.ArrayList;
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
	protected boolean feasibleRefactorbyRecalling(RefactoringOperation operRef) {
		boolean bandera = true;
		// Verificaci�n de llaves
		String src = "";
		String tgt = "";
		String fld, mtd;
		String acronym = operRef.getRefType().getAcronym();
		if (operRef.getParams() != null) {
			// si es un extract class memoriza sub-refs
			if (acronym.equals("EC")) {
				if (operRef.getSubRefs() != null) {
					// 1.Extracting src from subrefs
					if (operRef.getSubRefs().get(0).getParams().get("src") != null) {
						if (!operRef.getSubRefs().get(0).getParams().get("src").isEmpty()) { // valida
							// si es
							// vac�o
							for (RefactoringParameter obj : operRef.getSubRefs().get(0).getParams().get("src")) {
								src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";// 45,67
							}
							src = src.substring(0, src.length() - 1);
						}
					}
					// 2.Extracting fld from subrefs
					if (operRef.getSubRefs().get(0).getParams().get("fld") != null) {
						if (!operRef.getSubRefs().get(0).getParams().get("fld").isEmpty())
							fld = ((AttributeDeclaration) operRef.getSubRefs().get(0).getParams().get("fld").get(0)
									.getCodeObj()).getObjName();
						else
							fld = "-1";
					} else {
						fld = "-1";
					}

					// 3.Extracting mtd from subrefs
					if (operRef.getSubRefs().get(1).getParams().get("mtd") != null) {
						if (!operRef.getSubRefs().get(1).getParams().get("mtd").isEmpty())
							mtd = ((MethodDeclaration) operRef.getSubRefs().get(1).getParams().get("mtd").get(0)
									.getCodeObj()).getObjName();
						else
							mtd = "-1";
					} else {
						mtd = "-1";
					}
				} else {
					return false;
				}

			} else {

				if (operRef.getParams().get("src") != null) {
					if (!operRef.getParams().get("src").isEmpty()) { // valida
																		// si es
																		// vac�o
						for (RefactoringParameter obj : operRef.getParams().get("src")) {
							src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";// 45,67
						}
						src = src.substring(0, src.length() - 1);
					}
				}

				if (operRef.getParams().get("tgt") != null) {
					if (!operRef.getParams().get("tgt").isEmpty()) {
						for (RefactoringParameter obj : operRef.getParams().get("tgt")) {
							tgt += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";
						}
						tgt = tgt.substring(0, tgt.length() - 1);
					}
				}

				if (operRef.getParams().get("fld") != null) {
					if (!operRef.getParams().get("fld").isEmpty())
						fld = ((AttributeDeclaration) operRef.getParams().get("fld").get(0).getCodeObj()).getObjName();
					else
						fld = "-1";
				} else {
					fld = "-1";
				}

				if (operRef.getParams().get("mtd") != null) {
					if (!operRef.getParams().get("mtd").isEmpty())
						mtd = ((MethodDeclaration) operRef.getParams().get("mtd").get(0).getCodeObj()).getObjName();
					else
						mtd = "-1";
				} else {
					mtd = "-1";
				}
			}

			RegisterRepository repo = new RegisterRepository();
			List<Register> listMetric = new ArrayList<>();
			if (acronym.equals("EM") || acronym.equals("IM") || acronym.equals("RMMO")) {// ->
																							// Only
																							// matters
																							// src
																							// +
																							// mtd
			listMetric = repo.getRegistersByClass(acronym, src, "", mtd, "");
			} else if (acronym.equals("MF") || acronym.equals("PDF") || acronym.equals("PUF")) {// ->Only
																								// matters
																								// src+tgt+fld
				listMetric = repo.getRegistersByClass(acronym, src, tgt, "", fld);
			} else if (acronym.equals("MM") || acronym.equals("PDM") || acronym.equals("PUM")) {// ->Only
																								// matters
																								// src+mtd+tgt
				listMetric = repo.getRegistersByClass(acronym, src, tgt, mtd, "");
			} else if (acronym.equals("RDI") || acronym.equals("RID")) {// ->Only
																		// matters
																		// src+tgt
				listMetric = repo.getRegistersByClass(acronym, src, tgt, "", "");
			} else if (acronym.equals("EC")) {// ->Only matters src+fld+mtd
				listMetric = repo.getRegistersByClass(acronym, src, "", mtd, fld);
			}

			bandera = !listMetric.isEmpty();

		} else { // if no params, no recall unless EC
			if (acronym.equals("EC")) {
				if (operRef.getSubRefs() != null) {
					// 1.Extracting src from subrefs
					if (operRef.getSubRefs().get(0).getParams().get("src") != null) {
						if (!operRef.getSubRefs().get(0).getParams().get("src").isEmpty()) { // valida
							// si es
							// vac�o
							for (RefactoringParameter obj : operRef.getSubRefs().get(0).getParams().get("src")) {
								src += ((TypeDeclaration) obj.getCodeObj()).getId() + ",";// 45,67
							}
							src = src.substring(0, src.length() - 1);
						}
					}
					// 2.Extracting fld from subrefs
					if (operRef.getSubRefs().get(0).getParams().get("fld") != null) {
						if (!operRef.getSubRefs().get(0).getParams().get("fld").isEmpty())
							fld = ((AttributeDeclaration) operRef.getSubRefs().get(0).getParams().get("fld").get(0)
									.getCodeObj()).getObjName();
						else
							fld = "-1";
					} else {
						fld = "-1";
					}

					// 3.Extracting mtd from subrefs
					if (operRef.getSubRefs().get(1).getParams().get("mtd") != null) {
						if (!operRef.getSubRefs().get(1).getParams().get("mtd").isEmpty())
							mtd = ((MethodDeclaration) operRef.getSubRefs().get(1).getParams().get("mtd").get(0)
									.getCodeObj()).getObjName();
						else
							mtd = "-1";
					} else {
						mtd = "-1";
					}

					RegisterRepository repo = new RegisterRepository();
					List<Register> listMetric = new ArrayList<>();

					listMetric = repo.getRegistersByClass(acronym, src, "", mtd, fld);
					bandera = !listMetric.isEmpty();
				} else {
					bandera = false;
				}

			} else {
				bandera = false;
			}
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
