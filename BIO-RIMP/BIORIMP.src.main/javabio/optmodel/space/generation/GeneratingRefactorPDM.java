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
import javabio.optmodel.space.feasibility.InspectRefactor;
import javabio.optmodel.space.generation.GeneratingRefactor;
import unalcol.random.integer.IntUniform;
import unalcol.random.util.RandBool;

/**
 * @author Daavid
 */
public class GeneratingRefactorPDM extends GeneratingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.pushDownMethod;

    @Override
    public OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty) {
        // TODO Auto-generated method stub
        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());
        TypeDeclaration sysType_src;
        List<String> value_tgt;
        List<String> value_src;
        List<String> value_mtd;

        do {
            do {
                feasible = true;
                params = new ArrayList<OBSERVRefParam>();

                //Creating the OBSERVRefParam for the src class/super class
                sysType_src = MetaphorCode.getMapClass().get(g.generate());
                value_src = new ArrayList<String>();
                value_src.add(sysType_src.getQualifiedName());

                //Creating the OBSERVRefParam for the mtd class
                value_mtd = new ArrayList<String>();
                if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {

                    IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());

                    value_mtd.add((String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                            [numMtdObs.generate()]);

                    //Override verification parents
                    feasible = InspectRefactor.inspectOverrideParents(value_mtd, sysType_src);

                    if (feasible) {
                        //Override verification children
                        feasible = InspectRefactor.inspectOverrideChildren(value_mtd, sysType_src);
                    }

                    if (feasible) {
                        //+Verification of method not constructor
                        feasible = InspectRefactor.inspectMethodNotConstructor(value_mtd, sysType_src);
                    }

                } else {
                    feasible = false;
                }
            } while (!feasible);

            //Creating the OBSERVRefParam for the tgt class/child classes
            value_tgt = new ArrayList<String>();

            //Verification of SRCSupClassTGT
            //Retriving all child classes and choosing randomly
            if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName());
                RandBool gC = new RandBool();
                do {
                    for (TypeDeclaration clase : clases) {
                        if (gC.next()) {
                            value_tgt.add(clase.getQualifiedName());
                        }
                    }
                } while (value_tgt.isEmpty());
            } else {
                feasible = false;
            }

        } while (!feasible);//Checking Subclasses for SRC selected

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
        TypeDeclaration sysType_src;
        List<String> value_tgt = null;
        List<String> value_src;
        List<String> value_mtd;

        do {
            do {
                feasible = true;
                params = new ArrayList<OBSERVRefParam>();

                //Creating the OBSERVRefParam for the src class/super class
                //sysType_src =  code.getMapClass().get( g.generate() );
                if (ref.getParams() != null) {
                    //New class verification in src class
                    if (ref.getParams().get("src").get(0).getObjState().equals(CodeObjState.NEW))
                        sysType_src = MetaphorCode.getMapClass().get(g.generate());
                    else
                        sysType_src = (TypeDeclaration) ref.getParams().get("src").get(0).getCodeObj(); //Assumes the first src class of a set of classes
                } else {
                    sysType_src = MetaphorCode.getMapClass().get(g.generate());
                }
                value_src = new ArrayList<String>();
                value_src.add(sysType_src.getQualifiedName());

                //Creating the OBSERVRefParam for the mtd class
                value_mtd = new ArrayList<String>();
                if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {

                    IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());

                    value_mtd.add((String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                            [numMtdObs.generate()]);

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

                    if (feasible) {
                        //verification of method not constructor
                        if (value_mtd.get(0).equals(sysType_src.getName()))
                            feasible = false;
                    }

                } else {
                    feasible = false;
                    //break;
                }

                counter++;

                if (counter < break_point)
                    break;

            } while (!feasible);

            if (counter < break_point) {
                break;

            } else {
                //Creating the OBSERVRefParam for the tgt class/child classes
                value_tgt = new ArrayList<String>();

                //Verification of SRCSupClassTGT
                //Retriving all child classes and choosing randomly
                if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                    List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName());
                    RandBool gC = new RandBool();
                    do {
                        for (TypeDeclaration clase : clases) {
                            if (gC.next()) {
                                value_tgt.add(clase.getQualifiedName());
                            }
                        }
                    } while (value_tgt.isEmpty());
                } else {
                    feasible = false;
                    //break;
                }
            }


        } while (!feasible);//Checking Subclasses for SRC selected

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
