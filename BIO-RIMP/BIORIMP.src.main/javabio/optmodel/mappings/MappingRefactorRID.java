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
public class MappingRefactorRID extends MappingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.replaceInheritanceDelegation;

    @Override
    public OBSERVRefactoring mappingRefactor(QubitRefactor genome) {
        // TODO Auto-generated method stub
        boolean feasible = true;
        List<OBSERVRefParam> params = new ArrayList<OBSERVRefParam>();

        //Creating the OBSERVRefParam for the src class
        int numSrcObs = genome.getNumberGenome(genome.getGenSRC());
        TypeDeclaration sysType_src = MetaphorCode.getMapClass().get(numSrcObs %
                MetaphorCode.getMapClass().size());
        List<String> value_src = new ArrayList<String>();
        value_src.add(sysType_src.getQualifiedName());
        params.add(new OBSERVRefParam("src", value_src));

        //Creating the OBSERVRefParam for the tgt
        int numTgtObs = genome.getNumberGenome(genome.getGenTGT());
        List<String> value_tgt = new ArrayList<String>();
        TypeDeclaration sysType_tgt = MetaphorCode.getMapClass().get(numTgtObs %
                MetaphorCode.getMapClass().size());
        value_tgt.add(sysType_tgt.getQualifiedName());
        params.add(new OBSERVRefParam("tgt", value_tgt));

        //verification of SRCSubClassTGT
        if (!MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName()).isEmpty()) {
            List<TypeDeclaration> clases = MetaphorCode.getBuilder().getChildClasses().get(sysType_tgt.getQualifiedName());
            feasible = false;
            for (TypeDeclaration clase : clases) {
                if (clase.getQualifiedName().equals(value_src.get(0))) {
                    feasible = true;
                    break;
                }
            }
        } else {
            feasible = false;
        }


        return new OBSERVRefactoring(type.name(), params, feasible, new ArrayList<Double>());
    }

    /* (non-Javadoc)
     * @see entity.MappingRefactor#mappingParams()
     */
    @Override
    public List<OBSERVRefParam> mappingParams() {
        // TODO Auto-generated method stub
        return null;
    }

}
