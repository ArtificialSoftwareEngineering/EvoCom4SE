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
public class MappingRefactorMF extends MappingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.moveField;

    @Override
    public OBSERVRefactoring mappingRefactor(QubitRefactor genome) {
        // TODO Auto-generated method stub
        boolean feasible = true;
        List<OBSERVRefParam> params = new ArrayList<OBSERVRefParam>();
        int numSrcObs;
        TypeDeclaration sysType_src;

        //do{
        //Creating the OBSERVRefParam for the src class
        numSrcObs = genome.getNumberGenome(genome.getGenSRC());
        sysType_src = MetaphorCode.getMapClass().get(numSrcObs %
                MetaphorCode.getMapClass().size());
        List<String> value_src = new ArrayList<String>();
        value_src.add(sysType_src.getQualifiedName());
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

        //}while(code.getFieldsFromClass(sysType_src).isEmpty());

        //Creating the OBSERVRefParam for the tgt
        int numTgtObs = genome.getNumberGenome(genome.getGenTGT());
        List<String> value_tgt = new ArrayList<String>();
        TypeDeclaration sysType_tgt = MetaphorCode.getMapClass().get(numTgtObs %
                MetaphorCode.getMapClass().size());
        value_tgt.add(sysType_tgt.getQualifiedName());
        params.add(new OBSERVRefParam("tgt", value_tgt));

        return new OBSERVRefactoring(type.name(), params, feasible, new ArrayList<>());
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
