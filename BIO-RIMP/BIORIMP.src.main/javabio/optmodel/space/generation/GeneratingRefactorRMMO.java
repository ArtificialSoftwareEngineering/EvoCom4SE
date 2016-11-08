/**
 *
 */
package javabio.optmodel.space.generation;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.space.Refactoring;
import javabio.optmodel.space.feasibility.InspectRefactor;
import javabio.optmodel.space.generation.GeneratingRefactor;
import unalcol.random.integer.IntUniform;

/**
 * @author Daavid
 */
public class GeneratingRefactorRMMO extends GeneratingRefactor {

	/* (non-Javadoc)
     * @see entity.MappingRefactor#mappingRefactor(java.lang.String, unalcol.types.collection.bitarray.BitArray, entity.MetaphorCode)
	 */

    protected Refactoring type = Refactoring.replaceMethodObject;

    @Override
    public OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty) {
        boolean feasible;
        List<OBSERVRefParam> params;
        IntUniform g = new IntUniform(MetaphorCode.getMapClass().size());
        TypeDeclaration sysType_src;
        List<String> value_mtd;
        String mtdName = null;

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();

            //Creating the OBSERVRefParam for the src class
            sysType_src = MetaphorCode.getMapClass().get(g.generate());
            List<String> value_src = new ArrayList<String>();
            value_src.add(sysType_src.getQualifiedName());
            params.add(new OBSERVRefParam("src", value_src));

            //Creating the OBSERVRefParam for the mtd class
            value_mtd = new ArrayList<String>();

            if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
                IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());
                mtdName = (String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                        [numMtdObs.generate()];
                value_mtd.add(mtdName);

                //+Verification of method not constructor
                feasible = InspectRefactor.inspectMethodNotConstructor(value_mtd, sysType_src);
                if (feasible) {
                    //Override verification parents
                    feasible = InspectRefactor.inspectOverrideParents(value_mtd, sysType_src);
                    if (feasible) {
                        //Override verification children
                        feasible = InspectRefactor.inspectOverrideChildren(value_mtd, sysType_src);
                    }
                }

                params.add(new OBSERVRefParam("mtd", value_mtd));
            } else {
                feasible = false;
            }
        } while (!feasible);

        //Creating the OBSERVRefParam for the tgt
        //This Target Class is not inside metaphor
        List<String> value_tgt = new ArrayList<String>();
        value_tgt.add(sysType_src.getPack() + "TGT" + mtdName + "|N");
        params.add(new OBSERVRefParam("tgt", value_tgt));
        //MetaphorCode.addClasstoHash(sysType_src.getPack(), "TGT" + mtdName + "|N");

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
        List<String> value_mtd;
        String mtdName = null;

        do {
            feasible = true;
            params = new ArrayList<OBSERVRefParam>();

            //Creating the OBSERVRefParam for the src class
            //sysType_src =  code.getMapClass().get( g.generate() );
            if (ref.getParams() != null) {
                sysType_src = (TypeDeclaration) ref.getParams().get("src").get(0).getCodeObj();
            } else {
                sysType_src = MetaphorCode.getMapClass().get(g.generate());
            }

            List<String> value_src = new ArrayList<String>();
            value_src.add(sysType_src.getQualifiedName());
            params.add(new OBSERVRefParam("src", value_src));

            //Creating the OBSERVRefParam for the mtd class
            value_mtd = new ArrayList<String>();

            if (!MetaphorCode.getMethodsFromClass(sysType_src).isEmpty()) {
                IntUniform numMtdObs = new IntUniform(MetaphorCode.getMethodsFromClass(sysType_src).size());
                mtdName = (String) MetaphorCode.getMethodsFromClass(sysType_src).toArray()
                        [numMtdObs.generate()];
                value_mtd.add(mtdName);

                //verification of method not constructor
                if (value_mtd.get(0).equals(sysType_src.getName()))
                    feasible = false;

                if (feasible) {
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
                }


                params.add(new OBSERVRefParam("mtd", value_mtd));
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
            //This Target Class is not inside metaphor
            List<String> value_tgt = new ArrayList<String>();
            value_tgt.add(sysType_src.getPack() + "TGT" + mtdName + "|N");
            params.add(new OBSERVRefParam("tgt", value_tgt));
            //Fixme
            //MetaphorCode.addClasstoHash(sysType_src.getPack(), "TGT" + mtdName + "|N");
            refRepair = new OBSERVRefactoring(type.name(), params, feasible,ref.getPenalty());

        }
        return refRepair;
    }

}
