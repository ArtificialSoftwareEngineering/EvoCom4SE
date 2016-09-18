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
import edu.wayne.cs.severe.redress2.entity.refactoring.CodeObjState;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MetaphorCode;
import unalcol.clone.Clone;
import unalcol.random.integer.IntUniform;
import unalcol.types.collection.bitarray.BitArray;

/**
 * @author Daavid
 */


public class GeneratingRefactorEM extends GeneratingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.extractMethod;

    @Override
    public OBSERVRefactoring generatingRefactor( ) {
        // TODO Auto-generated method stub
        boolean feasible;
        List<OBSERVRefParam> params;
        TypeDeclaration sysType_src;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();

            //Creating the OBSERVRefParam for the src class
            sysType_src = MetaphorCode.getMapClass().get(g.generate());
            List<String> value_src = new ArrayList<String>();
            value_src.add(sysType_src.getQualifiedName());
            params.add(new OBSERVRefParam("src", value_src));

            //Creating the OBSERVRefParam for the mtd class
            List<String> value_mtd = new ArrayList<String>();
            if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
                IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());

                value_mtd.add((String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                        [numMtdObs.generate()]);

                //+Verification of method not constructor
                feasible = InspectRefactor.inspectMethodNotConstructor(value_mtd, sysType_src);

                if (feasible) {
                    //Override verification parents
                    feasible = InspectRefactor.inspectOverrideParents(value_mtd, sysType_src);
                    if (feasible) {
                        //Override verification children
                        feasible = InspectRefactor.inspectOverrideChildren(value_mtd, sysType_src);
                    }
                }

                params.add(new OBSERVRefParam("mtd", value_mtd));
            } else {
                feasible = false;
            }
        } while (!feasible); //Generating only feasible individuals

        return new OBSERVRefactoring(type.name(), params, feasible);
    }

    @Override
    public boolean feasibleRefactor(RefactoringOperation ref) {
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

    @Override
    public OBSERVRefactoring repairRefactor(RefactoringOperation ref, int break_point) {
        // TODO Auto-generated method stub
        OBSERVRefactoring refRepair = null;
        int counter = 0;
        boolean feasible;
        List<OBSERVRefParam> params;
        TypeDeclaration sysType_src;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());


        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();

            //Creating the OBSERVRefParam for the src class
            //sysType_src =  code.getMapClass().get( g.generate() );
            if (ref.getParams() != null) {
                //New class verification
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

            //Creating the OBSERVRefParam for the mtd class
            List<String> value_mtd = new ArrayList<String>();
            if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
                IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());

                value_mtd.add((String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                        [numMtdObs.generate()]);

                //verification of method not constructor
                if (value_mtd.get(0).equals(sysType_src.getName()))
                    feasible = false;

                if (feasible) {
                    //Override verification parents
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
                    if (feasible) {
                        //Override verification children
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
                    }
                }

                params.add(new OBSERVRefParam("mtd", value_mtd));
            } else {
                feasible = false;
                break;
            }

            counter++;

            if (counter < break_point)
                break;

        } while (!feasible); //Generating only feasible individuals

        if (!feasible || counter < break_point) {
            refRepair = generatingRefactor();
        } else {
            refRepair = new OBSERVRefactoring(type.name(), params, feasible);
        }


        return refRepair;
    }


}
