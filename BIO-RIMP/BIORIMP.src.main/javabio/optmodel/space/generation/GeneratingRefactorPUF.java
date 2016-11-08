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
public class GeneratingRefactorPUF extends GeneratingRefactor {

    protected Refactoring type = Refactoring.pullUpField;


    @Override
    public OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty) {

        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());
        TypeDeclaration sysType_src = null;
        TypeDeclaration sysType_tgt;
        List<String> value_src;
        List<String> value_fld = null;
        List<String> value_tgt;


        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();
            //Creating the OBSERVRefParam for the tgt/super class
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

                //Creating the OBSERVRefParam for the fld field
                value_fld = new ArrayList<String>();
                if (!MetaphorCode.getFieldsFromClass(sysType_src).isEmpty()) {

                    IntUniform numFldObs = new IntUniform(MetaphorCode.getFieldsFromClass(sysType_src).size());
                    value_fld.add((String) MetaphorCode.getFieldsFromClass(sysType_src).toArray()
                            [numFldObs.generate()]);

                    //Choosing other src(s) with the fld
                    for (TypeDeclaration clase : clases) {
                        for (String field : MetaphorCode.getFieldsFromClass(clase)) {
                            if (field.equals(value_fld.get(0))) {
                                value_src.add(clase.getQualifiedName());
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
        params.add(new OBSERVRefParam("fld", value_fld));
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
        TypeDeclaration sysType_src = null;
        TypeDeclaration sysType_tgt = null;
        List<String> value_src;
        List<String> value_fld = null;
        List<String> value_tgt;


        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();
            //Creating the OBSERVRefParam for the tgt/super class
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

                    //Creating the OBSERVRefParam for the fld field
                    value_fld = new ArrayList<String>();
                    if (!MetaphorCode.getFieldsFromClass(sysType_src).isEmpty()) {

                        IntUniform numFldObs = new IntUniform(MetaphorCode.getFieldsFromClass(sysType_src).size());
                        value_fld.add((String) MetaphorCode.getFieldsFromClass(sysType_src).toArray()
                                [numFldObs.generate()]);

                        //Choosing other src(s) with the fld
                        for (TypeDeclaration clase : clases) {
                            for (String field : MetaphorCode.getFieldsFromClass(clase)) {
                                if (field.equals(value_fld.get(0))) {
                                    value_src.add(clase.getQualifiedName());
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
            params.add(new OBSERVRefParam("fld", value_fld));
            params.add(new OBSERVRefParam("tgt", value_tgt));
            refRepair = new OBSERVRefactoring(type.name(), params, feasible,ref.getPenalty());
        }


        return refRepair;
    }

}
