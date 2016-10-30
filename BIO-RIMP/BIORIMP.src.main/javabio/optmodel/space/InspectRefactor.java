package javabio.optmodel.space;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.CodeObjState;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import javabio.optmodel.mappings.metaphor.MetaphorCode;

public final class InspectRefactor {
    public InspectRefactor() {

    }

    /**
     * @param value_mtd
     * @param sysType_src
     * @return
     */
    public static boolean inspectMethodNotConstructor(List<String> value_mtd,
                                                      TypeDeclaration sysType_src) {
        return !value_mtd.get(0).equals(sysType_src.getName());
    }

    /**
     * @param value_mtd
     * @param sysType_src
     * @return
     */
    public static boolean inspectOverrideParents(List<String> value_mtd, TypeDeclaration sysType_src) {
        boolean feasible = true;
        if (MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()) != null)
            if (!MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName())) {
                    if (MetaphorCode.getMethodsFromClass(clase) != null)
                        if (!MetaphorCode.getMethodsFromClass(clase).isEmpty()) {
                            for (String method : MetaphorCode.getMethodsFromClass(clase)) {
                                if (method.equals(value_mtd.get(0))) {
                                    feasible = false;
                                    break;
                                }
                            }
                        }
                }
            }
        return feasible;
    }

    /**
     * @param value_mtd
     * @param sysType_src
     * @param sysType_tgt
     * @return
     */
    public static boolean inspectHierarchyOverrideParents(List<String> value_mtd, TypeDeclaration sysType_src, TypeDeclaration sysType_tgt) {
        boolean feasible = true;
        if (MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()) != null)
            if (!MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName())) {
                    if (MetaphorCode.getMethodsFromClass(clase) != null)
                        if (!MetaphorCode.getMethodsFromClass(clase).isEmpty()) {
                            for (String method : MetaphorCode.getMethodsFromClass(clase)) {
                                if (method.equals(value_mtd.get(0)) || clase.equals(sysType_tgt)) {
                                    feasible = false;
                                    break;
                                }
                            }
                        }
                }
            }
        return feasible;
    }

    public static boolean inspectOverrideChildren(List<String> value_mtd, TypeDeclaration sysType_src) {
        boolean feasible = true;
        if (MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()) != null)
            if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration clase : MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName())) {
                    if (MetaphorCode.getMethodsFromClass(clase) != null)
                        if (!MetaphorCode.getMethodsFromClass(clase).isEmpty()) {
                            for (String method : MetaphorCode.getMethodsFromClass(clase)) {
                                if (method.equals(value_mtd.get(0))) {
                                    feasible = false;
                                    break;
                                }
                            }
                        }
                }
            }
        return feasible;
    }

    public static boolean inspectHierarchyOverrideChildren(List<String> value_mtd, TypeDeclaration sysType_src, TypeDeclaration sysType_tgt) {
        boolean feasible = true;
        if (MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()) != null)
            if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration clase : MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName())) {
                    if (MetaphorCode.getMethodsFromClass(clase) != null)
                        if (!MetaphorCode.getMethodsFromClass(clase).isEmpty()) {
                            for (String method : MetaphorCode.getMethodsFromClass(clase)) {
                                if (method.equals(value_mtd.get(0)) || clase.equals(sysType_tgt)) {
                                    feasible = false;
                                    break;
                                }
                            }
                        }
                }
            }
        return feasible;
    }

    //Feasibility feature reusable code
    public boolean insExtSrcClass(RefactoringOperation ref) {
        List<TypeDeclaration> src = new ArrayList<TypeDeclaration>();

        if (ref.getParams() != null) {
            if (ref.getParams().get("src") != null) {
                if (!ref.getParams().get("src").isEmpty()) {
                    for (RefactoringParameter param_src : ref.getParams().get("src")) {
                        //New class verification
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
        return true;
    }

}
