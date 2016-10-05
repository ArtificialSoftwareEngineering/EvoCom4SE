/**
 *
 */
package java.model.mappings;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import java.model.mappings.metaphor.MetaphorCode;
import java.model.mappings.quantum.QubitRefactor;

/**
 * @author Daavid
 */
public class MappingRefactorPUM extends MappingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.pullUpMethod;

    @Override
    public OBSERVRefactoring mappingRefactor(QubitRefactor genome) {
        // TODO Auto-generated method stub
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

        //Creating the OBSERVRefParam for the mtd class with the LAST sysType_src
        List<String> value_mtd = new ArrayList<String>();
        if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
            int numMtdObs = genome.getNumberGenome(genome.getGenMTD());

            value_mtd.add((String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()[numMtdObs
                    % MetaphorCode.getMethodsFromClass(sysType_src).size()]);

            //verification of method not constructor
            if (value_mtd.get(0).equals(sysType_src.getName()))
                feasible = false;

            params.add(new OBSERVRefParam("mtd", value_mtd));
        } else {
            value_mtd.add("");
            params.add(new OBSERVRefParam("mtd", value_mtd));
            feasible = false;
        }

        params.add(new OBSERVRefParam("tgt", value_tgt));

        return new OBSERVRefactoring(type.name(), params, feasible);
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
