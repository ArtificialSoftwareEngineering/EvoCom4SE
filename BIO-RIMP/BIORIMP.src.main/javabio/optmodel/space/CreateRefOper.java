/**
 *
 */
package javabio.optmodel.space;

import java.util.ArrayList;
import java.util.List;

import javabio.controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactorings;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.space.generation.*;
import unalcol.random.integer.IntUniform;

/**
 * @author Daavid
 */
public class CreateRefOper {


    public static List<RefactoringOperation> get(int n) {

        int mapRefactor;
        OBSERVRefactorings oper = new OBSERVRefactorings();
        List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();

        final int DECREASE = MetaphorCode.getDECREASE();
        IntUniform g = new IntUniform(Refactoring.values().length - DECREASE);
        GeneratingRefactor randomRefactor = null;

        for (int i = 0; i < n; i++) {
            mapRefactor = g.generate();
            switch (mapRefactor) {
                case 0:
                    randomRefactor = new GeneratingRefactorEC();
                    break;
                case 1:
                    randomRefactor = new GeneratingRefactorMM();
                    break;
                case 2:
                    randomRefactor = new GeneratingRefactorRMMO();
                    break;
                case 3:
                    randomRefactor = new GeneratingRefactorRDI();
                    break;
                case 4:
                    randomRefactor = new GeneratingRefactorMF();
                    break;
                case 5:
                    randomRefactor = new GeneratingRefactorEM();
                    break;
                case 6:
                    randomRefactor = new GeneratingRefactorIM();
                    break;
                case 7:
                    randomRefactor = new GeneratingRefactorRID();
                    break;
                case 8:
                    randomRefactor = new GeneratingRefactorPDF();
                    break;
                case 9:
                    randomRefactor = new GeneratingRefactorPUF();
                    break;
                case 10:
                    randomRefactor = new GeneratingRefactorPDM();
                    break;
                case 11:
                    randomRefactor = new GeneratingRefactorPUM();
                    break;
                //TODO: Quitar defaul y descomentar lineas del switch para activar todas
                default:
                    randomRefactor = new GeneratingRefactorIM();
            }//END CASE

            refactorings.add(randomRefactor.generatingRefactor(new ArrayList<>()));

        }

        oper.setRefactorings(refactorings);
        try {
            return MetaphorCode.getRefactorReader().getRefactOperations(oper);
        } catch (ReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Reading Error");
            return null;
        }
    }
}
