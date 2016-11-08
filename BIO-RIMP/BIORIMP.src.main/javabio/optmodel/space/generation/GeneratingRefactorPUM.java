/**
 *
 */
package javabio.optmodel.space.generation;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.CodeObjState;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.space.Refactoring;
import javabio.optmodel.space.generation.GeneratingRefactor;
import unalcol.random.integer.IntUniform;

/**
 * @author Daavid
 */
public class GeneratingRefactorPUM extends GeneratingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.pullUpMethod;

    @Override
    public OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty) {
        // TODO Auto-generated method stub
        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());
        List<String> value_mtd = null;
        List<String> value_tgt;
        TypeDeclaration sysType_tgt;
        TypeDeclaration sysType_src;
        List<String> value_src;

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();

            //Creating the OBSERVRefParam for the tgt
            value_tgt = new ArrayList<String>();
            sysType_tgt = MetaphorCode.getMapClass().get(g.generate());
            value_tgt.add(sysType_tgt.getQualifiedName());

            //Creating the OBSERVRefParam for the src class
            value_src = new ArrayList<String>();

            //verification of SRCSubClassTGT
            if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty()) {
                List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
                IntUniform indexClass = new IntUniform(clases.size());
                sysType_src = clases.get(indexClass.generate()); //RandomlySelectedClass

                //Creating the OBSERVRefParam for the mtd class randomly
                value_mtd = new ArrayList<String>();

                if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
                    IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());
                    value_mtd.add((String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                            [numMtdObs.generate()]);

                    if (feasible) {
                        //-Verification of method not constructor
                        if (value_mtd.get(0).equals(sysType_src.getName())) {
                            feasible = false;
                        } else {
                            //Choosing other src(s) with the mtd
                            for (TypeDeclaration clase : clases) {
                                for (String method : MetaphorCode.getMethodsFromClass(clase)) {
                                    if (method.equals(value_mtd.get(0))) {
                                        value_src.add(clase.getQualifiedName());
                                    }
                                }
                            }

                            for (String src_type : value_src) {
                                //Override verification parents
                                if (MetaphorCode.getBuilder().getParentClasses().get(src_type) != null)
                                    if (!MetaphorCode.getBuilder().getParentClasses().get(src_type).isEmpty()) {
                                        for (TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get(src_type)) {
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
                                    if (MetaphorCode.getBuilder().getChildClasses().get(src_type) != null)
                                        if (!MetaphorCode.getBuilder().getChildClasses().get(src_type).isEmpty()) {
                                            for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_type)) {
                                                if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                                    if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                                        for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
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

                        }
                    }
                } else {
                    feasible = false;
                }

            } else {
                feasible = false;

            }

        } while (!feasible);

        params.add(new OBSERVRefParam("src", value_src));
        params.add(new OBSERVRefParam("mtd", value_mtd));
        params.add(new OBSERVRefParam("tgt", value_tgt));

        return new OBSERVRefactoring(type.name(), params, feasible, penalty);
    }

    @Override
    public OBSERVRefactoring repairRefactor(RefactoringOperation ref, int break_point) {
        // TODO Auto-generated method stub
        OBSERVRefactoring refRepair = null;
        int counter = 0;

        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());
        List<String> value_mtd = null;
        List<String> value_tgt;
        TypeDeclaration sysType_tgt = null;
        TypeDeclaration sysType_src;
        List<String> value_src;

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();

            //Creating the OBSERVRefParam for the tgt
            value_tgt = new ArrayList<String>();
            //sysType_tgt = code.getMapClass().get( g.generate() );
            //sysType_tgt = (TypeDeclaration) ref.getParams().get("tgt").get(0).getCodeObj();
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

            //Creating the OBSERVRefParam for the src class
            value_src = new ArrayList<String>();

            //verification of SRCSubClassTGT
            if (MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()) != null) {
                if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty()) {
                    List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
                    IntUniform indexClass = new IntUniform(clases.size());
                    sysType_src = clases.get(indexClass.generate()); //RandomlySelectedClass

                    //Creating the OBSERVRefParam for the mtd class randomly
                    value_mtd = new ArrayList<String>();

                    if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
                        IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());
                        value_mtd.add((String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                                [numMtdObs.generate()]);

                        if (feasible) {
                            //verification of method not constructor
                            if (value_mtd.get(0).equals(sysType_src.getName())) {
                                feasible = false;
                            } else {
                                //Choosing other src(s) with the mtd
                                for (TypeDeclaration clase : clases) {
                                    for (String method : MetaphorCode.getMethodsFromClass(clase)) {
                                        if (method.equals(value_mtd.get(0))) {
                                            value_src.add(clase.getQualifiedName());
                                        }
                                    }
                                }

                                for (String src_type : value_src) {
                                    //Override verification parents
                                    if (MetaphorCode.getBuilder().getParentClasses().get(src_type) != null)
                                        if (!MetaphorCode.getBuilder().getParentClasses().get(src_type).isEmpty()) {
                                            for (TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get(src_type)) {
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
                                        if (MetaphorCode.getBuilder().getChildClasses().get(src_type) != null)
                                            if (!MetaphorCode.getBuilder().getChildClasses().get(src_type).isEmpty()) {
                                                for (TypeDeclaration clase_child : MetaphorCode.getBuilder().getChildClasses().get(src_type)) {
                                                    if (MetaphorCode.getMethodsFromClass(clase_child) != null)
                                                        if (!MetaphorCode.getMethodsFromClass(clase_child).isEmpty()) {
                                                            for (String method : MetaphorCode.getMethodsFromClass(clase_child)) {
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

                            }
                        }
                    } else {
                        feasible = false;
                    }

                } else {
                    feasible = false;
                    break;
                }
            } else {
                feasible = false;
                break;
            }

            counter++;

            if (counter < break_point)
                break;

        } while (!feasible);

        if (!feasible || counter < break_point) {
            //Penalty
            ref.getPenalty().add(penaltyReGeneration);
            refRepair = generatingRefactor(ref.getPenalty());
        } else {
            //Penalty
            ref.getPenalty().add(penaltyRepair);
            params.add(new OBSERVRefParam("src", value_src));
            params.add(new OBSERVRefParam("mtd", value_mtd));
            params.add(new OBSERVRefParam("tgt", value_tgt));
            refRepair = new OBSERVRefactoring(type.name(), params, feasible,ref.getPenalty());
        }


        return refRepair;
    }

}
