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
public class GeneratingRefactorMF extends GeneratingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor
     * (java.lang.String,
     * unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.moveField;

    @Override
    public OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty ) {
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

            //Creating the OBSERVRefParam for the fld field
            List<String> value_fld = new ArrayList<String>();
            if (!MetaphorCode.getFieldsFromClass(sysType_src).isEmpty()) {
                IntUniform numFldObs = new IntUniform(MetaphorCode.getFieldsFromClass(sysType_src).size());

                value_fld.add((String) MetaphorCode.getFieldsFromClass(sysType_src).toArray()
                        [numFldObs.generate()]);
                params.add(new OBSERVRefParam("fld", value_fld));
            } else {
                value_fld.add("");
                params.add(new OBSERVRefParam("fld", value_fld));
                feasible = false;
            }

        } while (!feasible);

        //Creating the OBSERVRefParam for the tgt

        List<String> value_tgt = new ArrayList<String>();
        TypeDeclaration sysType_tgt = MetaphorCode.getMapClass().get(g.generate());
        value_tgt.add(sysType_tgt.getQualifiedName());
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
        TypeDeclaration sysType_src;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();

            //Creating the OBSERVRefParam for the src class
            //sysType_src = code.getMapClass().get( g.generate() );
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
            //Creating the OBSERVRefParam for the tgt
            List<String> value_tgt = new ArrayList<String>();
            //TypeDeclaration sysType_tgt = code.getMapClass().get( g.generate() );
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
            refRepair = new OBSERVRefactoring(type.name(), params, feasible, ref.getPenalty());
        }

        return refRepair;
    }

}