/**
 *
 */
package space;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import entity.MetaphorCode;
import unalcol.random.integer.IntUniform;

/**
 * @author Daavid
 */
public class RepairRefactor {

    /**
     * @param args
     */
    public static List<String> repairExtractSRC(MetaphorCode code) {

        //Creating the OBSERVRefParam for the src class
        IntUniform g = new IntUniform(code.getMapClass().size());
        TypeDeclaration sysType_src = code.getMapClass().get(g.generate());
        List<String> value_src = new ArrayList<String>();
        value_src.add(sysType_src.getQualifiedName());

        return value_src;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
