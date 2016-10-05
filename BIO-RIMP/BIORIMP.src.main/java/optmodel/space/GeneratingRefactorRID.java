/**
 *
 */
package java.optmodel.space;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.CodeObjState;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import java.optmodel.mappings.metaphor.MetaphorCode;
import unalcol.random.integer.IntUniform;

/**
 * @author Daavid
 */
public class GeneratingRefactorRID extends GeneratingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.replaceInheritanceDelegation;

    @Override
    public OBSERVRefactoring generatingRefactor( ) {
        // TODO Auto-generated method stub
        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();
            //Creating the OBSERVRefParam for the src class

            TypeDeclaration sysType_src = MetaphorCode.getMapClass().get(g.generate());
            List<String> value_src = new ArrayList<String>();
            value_src.add(sysType_src.getQualifiedName());
            params.add(new OBSERVRefParam("src", value_src));

            //Creating the OBSERVRefParam for the tgt
            List<String> value_tgt = new ArrayList<String>();
            TypeDeclaration sysType_tgt = MetaphorCode.getMapClass().get(g.generate());
            value_tgt.add(sysType_tgt.getQualifiedName());
            params.add(new OBSERVRefParam("tgt", value_tgt));

            //Verification of equality
            if (sysType_src.equals(sysType_tgt))
                feasible = false;

            if (feasible) {
                //verification of SRCSubClassTGT
                if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty()) {
                    List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
                    feasible = false;
                    for (TypeDeclaration clase : clases) {
                        if (clase.equals(sysType_src)) {
                            feasible = true;
                            break;
                        }
                    }
                } else {
                    feasible = false;
                }
            }
        } while (!feasible);

        return new OBSERVRefactoring(type.name(), params, feasible);
    }

    @Override
    public boolean feasibleRefactor(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (feasibleRefactorbyRecalling(ref))
            return true;

        //Extracting the source class
        List<TypeDeclaration> src = new ArrayList<TypeDeclaration>();
        if (ref.getParams() != null) {
            if (ref.getParams().get("src") != null) {
                if (!ref.getParams().get("src").isEmpty()) {
                    for (RefactoringParameter param_src : ref.getParams().get("src")) {
                        //New class verification in src class
                        if (param_src.getObjState().equals(CodeObjState.NEW))
                            return false;
                        src.add((TypeDeclaration) param_src.getCodeObj());
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        //Extracting the target class
        List<TypeDeclaration> tgt = new ArrayList<TypeDeclaration>();
        if (ref.getParams().get("tgt") != null) {
            if (!ref.getParams().get("tgt").isEmpty()) {
                for (RefactoringParameter param_tgt : ref.getParams().get("tgt")) {
                    //New class verification in tgt class
                    if (param_tgt.getObjState().equals(CodeObjState.NEW))
                        return false;
                    tgt.add((TypeDeclaration) param_tgt.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        //Verification of equality
        for (TypeDeclaration src_class : src) {
            for (TypeDeclaration tgt_class : tgt) {
                if (src_class.getName().equals(tgt_class.getName()))
                    return false;
            }
        }

        //verification of SRCSubClassTGT
        for (TypeDeclaration tgt_class : tgt) {
            if (MetaphorCode.getBuilder().getChildClasses().get(tgt_class.getQualifiedName()) != null) {
                if (!MetaphorCode.getBuilder().getChildClasses().get(tgt_class.getQualifiedName()).isEmpty()) {
                    for (TypeDeclaration src_class : src) {
                        feasible = false;
                        for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(tgt_class.getQualifiedName())) {
                            if (clase_child.equals(src_class)) {
                                feasible = true;
                            }
                        }
                        if (!feasible)
                            return false;
                    }
                }
            } else {
                return false;
            }
        }

        return feasible;
    }

    @Override
    public OBSERVRefactoring repairRefactor(RefactoringOperation ref, int break_point) {
        // TODO Auto-generated method stub
        OBSERVRefactoring refRepair = null;
        int counter = 0;
        TypeDeclaration sysType_src;
        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();
            //Creating the OBSERVRefParam for the src class

            //TypeDeclaration sysType_src =  code.getMapClass().get( g.generate() );
            if (ref.getParams() != null) {
                //New class verification in src class
                if (ref.getParams().get("src").get(0).getObjState().equals(CodeObjState.NEW))
                    sysType_src = MetaphorCode.getMapClass().get(g.generate());
                else
                    sysType_src = (TypeDeclaration) ref.getParams().get("src").get(0).getCodeObj(); //Assumes the first src class of a set of classes
            } else {
                sysType_src = MetaphorCode.getMapClass().get(g.generate());
            }

            List<String> value_src = new ArrayList<String>();
            value_src.add(sysType_src.getQualifiedName());
            params.add(new OBSERVRefParam("src", value_src));

            //Creating the OBSERVRefParam for the tgt
            List<String> value_tgt = new ArrayList<String>();
            //TypeDeclaration sysType_tgt = code.getMapClass().get( g.generate() );
            //TypeDeclaration sysType_tgt = (TypeDeclaration) ref.getParams().get("tgt").get(0).getCodeObj();
            TypeDeclaration sysType_tgt = null;
            if (ref.getParams() != null) {
                if (ref.getParams().get("tgt") != null) {
                    if (!ref.getParams().get("tgt").isEmpty()) {
                        //New class verification in tgt class
                        if (ref.getParams().get("tgt").get(0).getObjState().equals(CodeObjState.NEW))
                            sysType_tgt = MetaphorCode.getMapClass().get(g.generate());
                        else
                            sysType_tgt = (TypeDeclaration) ref.getParams().get("tgt").get(0).getCodeObj(); //Assumes the first tgt class of a set of classes
                    } else {
                        sysType_tgt = MetaphorCode.getMapClass().get(g.generate());
                    }
                } else {
                    sysType_tgt = MetaphorCode.getMapClass().get(g.generate());
                }
            } else {
                sysType_tgt = MetaphorCode.getMapClass().get(g.generate());
            }

            value_tgt.add(sysType_tgt.getQualifiedName());
            params.add(new OBSERVRefParam("tgt", value_tgt));

            //Verification of equality
            if (sysType_src.equals(sysType_tgt))
                feasible = false;

            if (feasible) {
                //verification of SRCSubClassTGT
                if (MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()) != null) {
                    if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty()) {
                        List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
                        feasible = false;
                        for (TypeDeclaration clase : clases) {
                            if (clase.equals(sysType_src)) {
                                feasible = true;
                                break;
                            }
                        }
                    } else {
                        feasible = false;
                    }
                } else {
                    feasible = false;
                }
            }
            counter++;

            if (counter < break_point)
                break;
        } while (!feasible);

        if (!feasible || counter < break_point)
            refRepair = generatingRefactor();
        else
            refRepair = new OBSERVRefactoring(type.name(), params, feasible);

        return refRepair;
    }
}
