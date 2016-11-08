package javabio.optmodel.space.generation;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.AttributeDeclaration;
import edu.wayne.cs.severe.redress2.entity.MethodDeclaration;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.space.Refactoring;
import javabio.storage.entities.Register;
import javabio.storage.repositories.RegisterRepository;

public abstract class GeneratingRefactor {

    private OBSERVRefactoring refactor;
    protected Refactoring type;

    protected double penaltyReGeneration = MetaphorCode.getPenaltyReGeneration();
    protected double penaltyRepair = MetaphorCode.getPenaltyRepair();

    public abstract OBSERVRefactoring generatingRefactor(ArrayList<Double> penalty);

    public abstract OBSERVRefactoring repairRefactor(RefactoringOperation ref,
                                                     int break_point);

    public OBSERVRefactoring getRefactor() {
        return refactor;
    }

    public void setRefactor(OBSERVRefactoring refactor) {
        this.refactor = refactor;
    }

}
