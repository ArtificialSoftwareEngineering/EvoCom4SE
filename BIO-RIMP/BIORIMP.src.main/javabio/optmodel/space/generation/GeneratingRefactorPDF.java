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
import unalcol.random.util.RandBool;

/**
 * @author Daavid
 */
public class GeneratingRefactorPDF extends GeneratingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.pushDownField;

    @Override
    public OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty) {
        // TODO Auto-generated method stub
        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());
        TypeDeclaration sysType_src;

        do {
            do {
                feasible = true;
                params = new ArrayList<OBSERVRefParam>();

                //Creating the OBSERVRefParam for the src class
                sysType_src = MetaphorCode.getMapClass().get(g.generate());
                List<String> value_src = new ArrayList<String>();
                value_src.add(sysType_src.getQualifiedName());
                params.add(new OBSERVRefParam("src", value_src));

                //Creating the OBSERVRefParam for the fld field
                List<String> value_fld = new ArrayList<String>();
                if (!MetaphorCode.getFieldsFromClass(sysType_src).isEmpty()) {
                    IntUniform numFldObs = new IntUniform(MetaphorCode.getFieldsFromClass(sysType_src).size());
                    value_fld.add((String) MetaphorCode.getFieldsFromClass(sysType_src).toArray()
                            [numFldObs.generate()]);
                    params.add(new OBSERVRefParam("fld", value_fld));
                } else {
                    feasible = false;
                }
            } while (!feasible);

            //Creating the OBSERVRefParam for the tgt class
            List<String> value_tgt = new ArrayList<String>();

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
                params.add(new OBSERVRefParam("tgt", value_tgt));
            } else {
                feasible = false;
            }

        } while (!feasible);//Checking Subclasses for SRC selected

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

        do {
            do {
                feasible = true;
                params = new ArrayList<OBSERVRefParam>();

                //Creating the OBSERVRefParam for the src class
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

                List<String> value_src = new ArrayList<String>();
                value_src.add(sysType_src.getQualifiedName());
                params.add(new OBSERVRefParam("src", value_src));

                //Creating the OBSERVRefParam for the fld field
                List<String> value_fld = new ArrayList<String>();
                if (!MetaphorCode.getFieldsFromClass(sysType_src).isEmpty()) {
                    IntUniform numFldObs = new IntUniform(MetaphorCode.getFieldsFromClass(sysType_src).size());
                    value_fld.add((String) MetaphorCode.getFieldsFromClass(sysType_src).toArray()
                            [numFldObs.generate()]);
                    params.add(new OBSERVRefParam("fld", value_fld));
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
                //Creating the OBSERVRefParam for the tgt class
                List<String> value_tgt = new ArrayList<String>();

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
                    params.add(new OBSERVRefParam("tgt", value_tgt));
                } else {
                    feasible = false;
                    break;
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
            refRepair = new OBSERVRefactoring(type.name(), params, feasible, ref.getPenalty());
        }

        return refRepair;
    }
}
