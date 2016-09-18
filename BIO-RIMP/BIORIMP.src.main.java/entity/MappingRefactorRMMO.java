/**
 *
 */
package entity;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import entity.MappingRefactor.Refactoring;
import unalcol.types.collection.bitarray.BitArray;

/**
 * @author Daavid
 */
public class MappingRefactorRMMO extends MappingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.replaceMethodObject;

    @Override
    public OBSERVRefactoring mappingRefactor(QubitRefactor genome) {
        boolean feasible = true;
        List<OBSERVRefParam> params = new ArrayList<OBSERVRefParam>();

        //Creating the OBSERVRefParam for the src class
        int numSrcObs = genome.getNumberGenome(genome.getGenSRC());
        TypeDeclaration sysType_src = MetaphorCode.getMapClass().get(numSrcObs %
                MetaphorCode.getMapClass().size());
        List<String> value_src = new ArrayList<String>();
        value_src.add(sysType_src.getQualifiedName());
        params.add(new OBSERVRefParam("src", value_src));

        //Creating the OBSERVRefParam for the mtd class
        List<String> value_mtd = new ArrayList<String>();
        String mtdName;
        if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
            int numMtdObs = genome.getNumberGenome(genome.getGenMTD());
            mtdName = (String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()[numMtdObs
                    % MetaphorCode.getMethodsFromClass(sysType_src).size()];
            value_mtd.add(mtdName);

            //verification of method not constructor
            if (value_mtd.get(0).equals(sysType_src.getName()))
                feasible = false;

            params.add(new OBSERVRefParam("mtd", value_mtd));
        } else {
            mtdName = "";
            value_mtd.add(mtdName);
            params.add(new OBSERVRefParam("mtd", value_mtd));
            feasible = false;
        }

        //Creating the OBSERVRefParam for the tgt
        //This Target Class is not inside metaphor
        List<String> value_tgt = new ArrayList<String>();
        value_tgt.add(sysType_src.getPack() + mtdName + "|N");
        params.add(new OBSERVRefParam("tgt", value_tgt));
        //Fixme
        //MetaphorCode.addClasstoHash(sysType_src.getPack(), mtdName + "|N");

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
