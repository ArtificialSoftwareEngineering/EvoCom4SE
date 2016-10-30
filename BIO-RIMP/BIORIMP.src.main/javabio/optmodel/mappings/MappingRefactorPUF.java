/**
 *
 */
package javabio.optmodel.mappings;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.mappings.quantum.QubitRefactor;

/**
 * @author Daavid
 */
public class MappingRefactorPUF extends MappingRefactor {

    protected Refactoring type = Refactoring.pullUpField;


    @Override
    public OBSERVRefactoring mappingRefactor(
            QubitRefactor genome) {

        boolean feasible = true;
        List<OBSERVRefParam> params = new ArrayList<OBSERVRefParam>();

        //Creating the OBSERVRefParam for the tgt
        int numTgtObs = genome.getNumberGenome(genome.getGenTGT());
        List<String> value_tgt = new ArrayList<String>();
        TypeDeclaration sysType_tgt = MetaphorCode.getMapClass().get(numTgtObs %
                MetaphorCode.getMapClass().size());
        value_tgt.add(sysType_tgt.getQualifiedName());


        //Creating the OBSERVRefParam for the src class
        TypeDeclaration sysType_src = null;
        List<String> value_src = new ArrayList<String>();

        int numSrcObs = 0;

        for (int i = 0; i < genome.getGenSRC().size(); i = i + genome.getSRC()) {
            numSrcObs = genome.getNumberGenome(genome.getGenSRC(), i, genome.getSRC());
            sysType_src = MetaphorCode.getMapClass().get(numSrcObs %
                    MetaphorCode.getMapClass().size());
            value_src.add(sysType_src.getQualifiedName());

            //verification of SRCSubClassTGT
            if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty()) {
                List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
                feasible = false;
                for (TypeDeclaration clase : clases) {
                    if (clase.getQualifiedName().equals(value_src.get(i))) {
                        feasible = true;
                        break;
                    }
                }
            } else {
                feasible = false;
            }

        }

        params.add(new OBSERVRefParam("src", value_src));

        //Creating the OBSERVRefParam for the fld field
        List<String> value_fld = new ArrayList<String>();
        if (!MetaphorCode.getFieldsFromClass(sysType_src).isEmpty()) {
            int numFldObs = genome.getNumberGenome(genome.getGenFLD());
            value_fld.add((String) MetaphorCode.getFieldsFromClass(sysType_src).toArray()[numFldObs
                    % MetaphorCode.getFieldsFromClass(sysType_src).size()]);
            params.add(new OBSERVRefParam("fld", value_fld));
        } else {
            value_fld.add("");
            params.add(new OBSERVRefParam("fld", value_fld));
            feasible = false;
        }


        params.add(new OBSERVRefParam("tgt", value_tgt));

        return new OBSERVRefactoring(type.name(), params, feasible);
    }

    @Override
    public List<OBSERVRefParam> mappingParams() {
        // TODO Auto-generated method stub
        return null;
    }

}
