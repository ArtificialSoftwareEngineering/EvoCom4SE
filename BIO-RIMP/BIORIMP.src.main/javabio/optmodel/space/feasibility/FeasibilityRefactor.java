package javabio.optmodel.space.feasibility;

import edu.wayne.cs.severe.redress2.entity.AttributeDeclaration;
import edu.wayne.cs.severe.redress2.entity.MethodDeclaration;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.CodeObjState;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.storage.entities.Register;
import javabio.storage.repositories.RegisterRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 5/11/16.
 */
public class FeasibilityRefactor {

    public static boolean feasibleRefactorEC(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (feasibleRefactorbyRecalling(ref))
            return true;

        // Extracting the source class MF
        List<TypeDeclaration> src_MF = new ArrayList<TypeDeclaration>();
        //if( ref.getSubRefs().get(0).getParams().get("src") != null ){

        if (ref.getSubRefs() != null) {
            if (!ref.getSubRefs().get(0).getParams().get("src").isEmpty()) {
                for (RefactoringParameter param_src : ref.getSubRefs().get(0).getParams().get("src")) {
                    src_MF.add((TypeDeclaration) param_src.getCodeObj());
                }
            } else {
                return false;
            }


            //Extracting the source class MM
            List<TypeDeclaration> src_MM = new ArrayList<TypeDeclaration>();
            if (ref.getSubRefs().get(1).getParams().get("src") != null) {
                if (!ref.getSubRefs().get(1).getParams().get("src").isEmpty()) {
                    for (RefactoringParameter param_src : ref.getSubRefs().get(1).getParams().get("src")) {
                        src_MM.add((TypeDeclaration) param_src.getCodeObj());
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

            //Extracting the target class MF
            List<TypeDeclaration> tgt_MF = new ArrayList<TypeDeclaration>();
            if (ref.getSubRefs().get(0).getParams().get("tgt") != null) {
                if (!ref.getSubRefs().get(0).getParams().get("tgt").isEmpty()) {
                    for (RefactoringParameter param_tgt : ref.getSubRefs().get(0).getParams().get("tgt")) {
                        tgt_MF.add((TypeDeclaration) param_tgt.getCodeObj());
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

            //Extracting the target class MM
            List<TypeDeclaration> tgt_MM = new ArrayList<TypeDeclaration>();
            if (ref.getSubRefs().get(1).getParams().get("tgt") != null) {
                if (!ref.getSubRefs().get(1).getParams().get("tgt").isEmpty()) {
                    for (RefactoringParameter param_tgt : ref.getSubRefs().get(1).getParams().get("tgt")) {
                        tgt_MM.add((TypeDeclaration) param_tgt.getCodeObj());
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

            //Extracting field of source class
            List<AttributeDeclaration> fld = new ArrayList<AttributeDeclaration>();
            if (ref.getSubRefs().get(0).getParams().get("fld") != null) {
                if (!ref.getSubRefs().get(0).getParams().get("fld").isEmpty()) {
                    for (RefactoringParameter param_fld : ref.getSubRefs().get(0).getParams().get("fld")) {
                        fld.add((AttributeDeclaration) param_fld.getCodeObj());
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }


            //Extracting method of source class
            List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
            if (ref.getSubRefs().get(1).getParams().get("mtd") != null) {
                if (!ref.getSubRefs().get(1).getParams().get("mtd").isEmpty()) {
                    for (RefactoringParameter param_mtd : ref.getSubRefs().get(1).getParams().get("mtd")) {
                        mtd.add((MethodDeclaration) param_mtd.getCodeObj());
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }


            //Verification Field in Source Class
            for (TypeDeclaration src_class : src_MF) {
                for (AttributeDeclaration field : fld) {
                    if (MetaphorCode.getFieldsFromClass(src_class) != null)
                        if (!MetaphorCode.getFieldsFromClass(src_class).isEmpty())
                            for (String fiel : MetaphorCode.getFieldsFromClass(src_class)) {
                                if (field.getObjName().equals(fiel))
                                    feasible = false;    //check the logic is wrong!!
                            }
                    if (feasible)
                        return false;
                    else
                        feasible = true;
                }
            }

            //Verification Method in Source Class
            for (TypeDeclaration src_class : src_MM) {
                for (MethodDeclaration metodo : mtd) {
                    if (MetaphorCode.getMethodsFromClass(src_class) != null)
                        if (!MetaphorCode.getMethodsFromClass(src_class).isEmpty())
                            for (String method : MetaphorCode.getMethodsFromClass(src_class)) {
                                if (metodo.getObjName().equals(method))
                                    feasible = false;    //check the logic is wrong!!
                            }
                    if (feasible)
                        return false;
                    else
                        feasible = true;
                }
            }

            //verification of method not constructor
            for (TypeDeclaration src_class : src_MM) {
                for (MethodDeclaration metodo : mtd) {
                    if (src_class.getName().equals(metodo.getObjName()))
                        return false;
                }
            }

            for (TypeDeclaration src_class : src_MM) {
                for (MethodDeclaration metodo : mtd) {
                    //Override verification parents
                    if (MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()) != null)
                        if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                            for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                                if (MetaphorCode.getMethodsFromClass(clase_parent) != null)
                                    if (!MetaphorCode.getMethodsFromClass(clase_parent).isEmpty()) {
                                        for (String method : MetaphorCode.getMethodsFromClass(clase_parent)) {
                                            if (method.equals(metodo.getObjName())) {
                                                return false;
                                            }
                                        }
                                    }
                            }
                        }

                    //Override verification children
                    if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                        if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                            for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                                if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                    if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                        for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
                                            if (method.equals(metodo.getObjName())) {
                                                return false;
                                            }
                                        }
                                    }
                            }
                        }


                }//end for metodo
            }//enf for src_class

        } else {
            return false;
        }
        return feasible;
    }

    public static boolean feasibleRefactorEM(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        //0. Feasibility by Recalling
        //if( feasibleRefactorbyRecalling(ref) )
        //	return true;

        //1. Extracting the source class
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


        //2. Extracting method of source class
        List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
        if (ref.getParams().get("mtd") != null) {
            if (!ref.getParams().get("mtd").isEmpty()) {
                for (RefactoringParameter param_mtd : ref.getParams().get("mtd")) {
                    mtd.add((MethodDeclaration) param_mtd.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        //3. Verification Method in Source Class
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (MetaphorCode.getMethodsFromClass(src_class) != null)
                    if (!MetaphorCode.getMethodsFromClass(src_class).isEmpty())
                        for (String method : MetaphorCode.getMethodsFromClass(src_class)) {
                            if (metodo.getObjName().equals(method))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //4. verification of method not constructor
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (src_class.getName().equals(metodo.getObjName()))
                    return false;
            }
        }

        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                //5. Override verification parents
                if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                    for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                        if (MetaphorCode.getMethodsFromClass(clase_parent) != null)
                            if (!MetaphorCode.getMethodsFromClass(clase_parent).isEmpty()) {
                                for (String method : MetaphorCode.getMethodsFromClass(clase_parent)) {
                                    if (method.equals(metodo.getObjName())) {
                                        return false;
                                    }
                                }
                            }
                    }
                }

                //6. Override verification children
                if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
                                        if (method.equals(metodo.getObjName())) {
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

    public static boolean feasibleRefactorIM(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
            return true;

        //1. Extracting the source class
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


        //2. Extracting method of source class
        List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
        if (ref.getParams().get("mtd") != null) {
            if (!ref.getParams().get("mtd").isEmpty()) {
                for (RefactoringParameter param_mtd : ref.getParams().get("mtd")) {
                    mtd.add((MethodDeclaration) param_mtd.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        //3. Verification Method in Source Class
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (MetaphorCode.getMethodsFromClass(src_class) != null)
                    if (!MetaphorCode.getMethodsFromClass(src_class).isEmpty())
                        for (String method : MetaphorCode.getMethodsFromClass(src_class)) {
                            if (metodo.getObjName().equals(method))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //4. verification of method not constructor
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (src_class.getName().equals(metodo.getObjName()))
                    return false;
            }
        }

        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                //5. Override verification parents
                if (MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_parent) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_parent).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_parent)) {
                                        if (method.equals(metodo.getObjName())) {
                                            return false;
                                        }
                                    }
                                }
                        }
                    }

                //6. Override verification children
                if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
                                        if (method.equals(metodo.getObjName())) {
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

    public static boolean feasibleRefactorMF(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
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

        //Extracting field of source class
        List<AttributeDeclaration> fld = new ArrayList<AttributeDeclaration>();
        if (ref.getParams().get("fld") != null) {
            if (!ref.getParams().get("fld").isEmpty()) {
                for (RefactoringParameter param_fld : ref.getParams().get("fld")) {
                    fld.add((AttributeDeclaration) param_fld.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }


        //Verification Field in Source Class
        for (TypeDeclaration src_class : src) {
            for (AttributeDeclaration field : fld) {
                if (MetaphorCode.getFieldsFromClass(src_class) != null)
                    if (!MetaphorCode.getFieldsFromClass(src_class).isEmpty())
                        for (String fiel : MetaphorCode.getFieldsFromClass(src_class)) {
                            if (field.getObjName().equals(fiel))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        return feasible;
    }

    public static boolean feasibleRefactorMM(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        //if (feasibleRefactorbyRecalling(ref))
        //return true;

        //1. Extracting the source class
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

        //2. Extracting method of source class
        List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
        if (ref.getParams().get("mtd") != null) {
            if (!ref.getParams().get("mtd").isEmpty()) {
                for (RefactoringParameter param_mtd : ref.getParams().get("mtd")) {
                    mtd.add((MethodDeclaration) param_mtd.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }


        //3. Extracting the target class
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

        //4. Verification Method in Source Class
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (MetaphorCode.getMethodsFromClass(src_class) != null)
                    if (!MetaphorCode.getMethodsFromClass(src_class).isEmpty())
                        for (String method : MetaphorCode.getMethodsFromClass(src_class)) {
                            if (metodo.getObjName().equals(method))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //5. verification of method not constructor
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (src_class.getName().equals(metodo.getObjName()))
                    return false;
            }
        }

        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                //6. Override verification parents
                if (MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_parent) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_parent).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_parent)) {
                                        if (method.equals(metodo.getObjName())) {
                                            return false;
                                        }
                                    }
                                }
                        }
                    }

                //7. Override verification children
                if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
                                        if (method.equals(metodo.getObjName())) {
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

    public static boolean feasibleRefactorPDF(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
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

        //Extracting field of source class
        List<AttributeDeclaration> fld = new ArrayList<AttributeDeclaration>();
        if (ref.getParams().get("fld") != null) {
            if (!ref.getParams().get("fld").isEmpty()) {
                for (RefactoringParameter param_fld : ref.getParams().get("fld")) {
                    fld.add((AttributeDeclaration) param_fld.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }


        //Verification Field in Source Class
        for (TypeDeclaration src_class : src) {
            for (AttributeDeclaration field : fld) {
                if (MetaphorCode.getFieldsFromClass(src_class) != null)
                    if (!MetaphorCode.getFieldsFromClass(src_class).isEmpty())
                        for (String fiel : MetaphorCode.getFieldsFromClass(src_class)) {
                            if (field.getObjName().equals(fiel))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //Verification SRCSupClassTGT
        for (TypeDeclaration src_class : src) {
            if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration tgt_class : tgt) {
                    feasible = false;
                    for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {

                        if (clase_child.equals(tgt_class))
                            feasible = true;

                    }
                    if (!feasible)
                        return false;
                }
            } else {
                return false;
            }
        }

        return feasible;
    }

    public static boolean feasibleRefactorPDM(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
            return true;

        //1. Extracting the source class
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

        //2. Extracting method of source class
        List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
        if (ref.getParams().get("mtd") != null) {
            if (!ref.getParams().get("mtd").isEmpty()) {
                for (RefactoringParameter param_mtd : ref.getParams().get("mtd")) {
                    mtd.add((MethodDeclaration) param_mtd.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        //3. Extracting the target class
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


        //4. Verification Method in Source Class
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (MetaphorCode.getMethodsFromClass(src_class) != null)
                    if (!MetaphorCode.getMethodsFromClass(src_class).isEmpty())
                        for (String method : MetaphorCode.getMethodsFromClass(src_class)) {
                            if (metodo.getObjName().equals(method))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //5. verification of method not constructor
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (src_class.getName().equals(metodo.getObjName()))
                    return false;
            }
        }

        //6. Verification SRCsupClassTGT
        for (TypeDeclaration src_class : src) {
            if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration tgt_class : tgt) {
                    feasible = false;
                    for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {

                        if (clase_child.equals(tgt_class))
                            feasible = true;

                    }
                    if (!feasible)
                        return false;
                }
            } else {
                return false;
            }
        }

        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                //7. Override verification parents
                if (MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_parent) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_parent).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_parent)) {
                                        if (method.equals(metodo.getObjName())) {
                                            return false;
                                        }
                                    }
                                }
                        }
                    }

                //8. Override verification children
                if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
                                        if (method.equals(metodo.getObjName())) {
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

    public static boolean feasibleRefactorPUF(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
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

        //Extracting field of source class
        List<AttributeDeclaration> fld = new ArrayList<AttributeDeclaration>();
        if (ref.getParams().get("fld") != null) {
            if (!ref.getParams().get("fld").isEmpty()) {
                for (RefactoringParameter param_fld : ref.getParams().get("fld")) {
                    fld.add((AttributeDeclaration) param_fld.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }


        //Verification Field in Source Class
        for (TypeDeclaration src_class : src) {
            for (AttributeDeclaration field : fld) {
                if (MetaphorCode.getFieldsFromClass(src_class) != null)
                    if (!MetaphorCode.getFieldsFromClass(src_class).isEmpty())
                        for (String fiel : MetaphorCode.getFieldsFromClass(src_class)) {
                            if (field.getObjName().equals(fiel))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //Verification SRCsubClassTGT
        for (TypeDeclaration src_class : src) {
            if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration tgt_class : tgt) {
                    feasible = false;
                    for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                        if (clase_parent.equals(tgt_class))
                            feasible = true;
                    }
                    if (!feasible)
                        return false;
                }
            } else {
                return false;
            }
        }


        return feasible;
    }

    public static boolean feasibleRefactorPUM(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
            return true;

        //1. Extracting the target class
        List<TypeDeclaration> tgt = new ArrayList<TypeDeclaration>();
        if (ref.getParams() != null) {
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
        } else {
            return false;
        }


        //2. Extracting the source class
        List<TypeDeclaration> src = new ArrayList<TypeDeclaration>();
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

        //3. Extracting method of source class
        List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
        if (ref.getParams().get("mtd") != null) {
            if (!ref.getParams().get("mtd").isEmpty()) {
                for (RefactoringParameter param_mtd : ref.getParams().get("mtd")) {
                    mtd.add((MethodDeclaration) param_mtd.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }


        //4. Verification Method in Source Class
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (MetaphorCode.getMethodsFromClass(src_class) != null)
                    if (!MetaphorCode.getMethodsFromClass(src_class).isEmpty())
                        for (String method : MetaphorCode.getMethodsFromClass(src_class)) {
                            if (metodo.getObjName().equals(method))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //5. verification of method not constructor
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (src_class.getName().equals(metodo.getObjName()))
                    return false;
            }
        }

        //6. Verification SRCsubClassTGT
        for (TypeDeclaration src_class : src) {
            if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                for (TypeDeclaration tgt_class : tgt) {
                    feasible = false;
                    for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {

                        if (clase_parent.equals(tgt_class))
                            feasible = true;
                    }
                    if (!feasible)
                        return false;
                }
            } else {
                return false;
            }
        }

        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                //7. Override verification parents
                if (MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_parent) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_parent).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_parent)) {
                                        if (method.equals(metodo.getObjName())) {
                                            return false;
                                        }
                                    }
                                }
                        }
                    }

                //8. Override verification children
                if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
                                        if (method.equals(metodo.getObjName())) {
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

    public static boolean feasibleRefactorRDI(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
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

        //Hierarchy verification parents
        for (TypeDeclaration src_class : src) {
            if (MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()) != null)
                if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                    for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                        for (TypeDeclaration tgt_class : tgt) {
                            if (clase_parent.equals(tgt_class)) {
                                return false;
                            }
                        }

                    }
                }
        }

        //Hierarchy verification children
        for (TypeDeclaration src_class : src) {
            if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                    for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                        for (TypeDeclaration tgt_class : tgt) {
                            if (clase_child.equals(tgt_class)) {
                                return false;
                            }
                        }

                    }
                }
        }

        return feasible;
    }

    public static boolean feasibleRefactorRID(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
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

    public static boolean feasibleRefactorRMMO(RefactoringOperation ref) {
        // TODO Auto-generated method stub
        boolean feasible = true;

        // 0. Feasibility by Recalling
        if (FeasibilityRefactor.feasibleRefactorbyRecalling(ref))
            return true;

        // Extracting the source class
        List<TypeDeclaration> src = new ArrayList<TypeDeclaration>();
        if (ref.getParams() != null) {
            if (ref.getParams().get("src") != null) {
                if (!ref.getParams().get("src").isEmpty()) {
                    for (RefactoringParameter param_src : ref.getParams().get("src")) {
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
                    if (!param_tgt.getObjState().equals(CodeObjState.NEW))
                        return false;
                    tgt.add((TypeDeclaration) param_tgt.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        //Extracting method of source class
        List<MethodDeclaration> mtd = new ArrayList<MethodDeclaration>();
        if (ref.getParams().get("mtd") != null) {
            if (!ref.getParams().get("mtd").isEmpty()) {
                for (RefactoringParameter param_mtd : ref.getParams().get("mtd")) {
                    mtd.add((MethodDeclaration) param_mtd.getCodeObj());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        //Verification Method in Source Class
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (MetaphorCode.getMethodsFromClass(src_class) != null)
                    if (!MetaphorCode.getMethodsFromClass(src_class).isEmpty())
                        for (String method : MetaphorCode.getMethodsFromClass(src_class)) {
                            if (metodo.getObjName().equals(method))
                                feasible = false;    //check the logic is wrong!!
                        }
                if (feasible)
                    return false;
                else
                    feasible = true;
            }
        }

        //verification of method not constructor
        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                if (src_class.getName().equals(metodo.getObjName()))
                    return false;
            }
        }

        for (TypeDeclaration src_class : src) {
            for (MethodDeclaration metodo : mtd) {
                //Override verification parents
                if (MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_parent : MetaphorCode.getBuilder().getParentClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_parent) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_parent).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_parent)) {
                                        if (method.equals(metodo.getObjName())) {
                                            return false;
                                        }
                                    }
                                }
                        }
                    }

                //Override verification children
                if (MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_class.getQualifiedName())) {
                            if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
                                        if (method.equals(metodo.getObjName())) {
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

    //1000 danaderp dynamic feasiability
    //Return true if a refactoring has already in the data base
    public static boolean feasibleRefactorbyRecalling(RefactoringOperation operRef) {
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

            RegisterRepository repo = RegisterRepository.getInstance();
            List<Register> listMetric = new ArrayList<>();
            if (acronym.equals("EM") || acronym.equals("IM") || acronym.equals("RMMO")) {// ->
                // Only
                // matters
                // src
                // +
                // mtd
                listMetric = repo.getRegistersByClass(acronym, src, "", mtd, "", MetaphorCode.getSysName());
            } else if (acronym.equals("MF") || acronym.equals("PDF") || acronym.equals("PUF")) {// ->Only
                // matters
                // src+tgt+fld
                listMetric = repo.getRegistersByClass(acronym, src, tgt, "", fld, MetaphorCode.getSysName());
            } else if (acronym.equals("MM") || acronym.equals("PDM") || acronym.equals("PUM")) {// ->Only
                // matters
                // src+mtd+tgt
                listMetric = repo.getRegistersByClass(acronym, src, tgt, mtd, "", MetaphorCode.getSysName());
            } else if (acronym.equals("RDI") || acronym.equals("RID")) {// ->Only
                // matters
                // src+tgt
                listMetric = repo.getRegistersByClass(acronym, src, tgt, "", "", MetaphorCode.getSysName());
            } else if (acronym.equals("EC")) {// ->Only matters src+fld+mtd
                listMetric = repo.getRegistersByClass(acronym, src, "", mtd, fld, MetaphorCode.getSysName());
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

                    RegisterRepository repo = RegisterRepository.getInstance();
                    List<Register> listMetric = new ArrayList<>();

                    listMetric = repo.getRegistersByClass(acronym, src, "", mtd, fld, MetaphorCode.getSysName());
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
}
