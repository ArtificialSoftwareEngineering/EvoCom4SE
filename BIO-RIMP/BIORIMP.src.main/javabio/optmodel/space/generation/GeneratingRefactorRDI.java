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
public class GeneratingRefactorRDI extends GeneratingRefactor {

    /* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
     */
    protected Refactoring type = Refactoring.replaceDelegationInheritance;

    @Override
    public OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty ) {
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
                //Hierarchy verification parents
                if (MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName())) {
                            if (clase.equals(sysType_tgt)) {
                                feasible = false;
                                break;
                            }

                        }
                    }

                if (feasible) {
                    //Hierarchy verification children
                    if (MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()) != null)
                        if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                            for (TypeDeclaration clase : MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName())) {
                                if (clase.equals(sysType_tgt)) {
                                    feasible = false;
                                    break;
                                }
                            }
                        }
                }
            }

        } while (!feasible);
        return new OBSERVRefactoring(type.name(), params, feasible,penalty);
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
                //Hierarchy verification parents
                if (MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()) != null)
                    if (!MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                        for (TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName())) {
                            if (clase.equals(sysType_tgt)) {
                                feasible = false;
                                break;
                            }

                        }
                    }

                if (feasible) {
                    //Hierarchy verification children
                    if (MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()) != null)
                        if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
                            for (TypeDeclaration clase : MetaphorCode.getBuilder().getChildClasses().get(sysType_src.getQualifiedName())) {
                                if (clase.equals(sysType_tgt)) {
                                    feasible = false;
                                    break;
                                }
                            }
                        }
                }
            }

            counter++;

            if (counter < break_point)
                break;

        } while (!feasible);

        if (!feasible || counter < break_point){
            //Penalty
            ref.getPenalty().add(penaltyReGeneration);
            refRepair = generatingRefactor(ref.getPenalty());
        }
        else{
            //Penalty
            ref.getPenalty().add(penaltyRepair);
            refRepair = new OBSERVRefactoring(type.name(), params, feasible, ref.getPenalty());
        }


        return refRepair;
    }

}
