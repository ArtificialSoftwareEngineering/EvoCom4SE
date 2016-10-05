package java.optmodel.mappings;

import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefParam;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import java.optmodel.mappings.quantum.QubitRefactor;

public abstract class MappingRefactor {

    private OBSERVRefactoring refactor;
    protected Refactoring type;

    public abstract OBSERVRefactoring mappingRefactor(
            QubitRefactor genome);

    public abstract List<OBSERVRefParam> mappingParams();

    public OBSERVRefactoring getRefactor() {
        return refactor;
    }

    public void setRefactor(OBSERVRefactoring refactor) {
        this.refactor = refactor;
    }

    enum Refactoring {
        pullUpField, moveMethod, replaceMethodObject, replaceDelegationInheritance,
        moveField, extractMethod, pushDownMethod, replaceInheritanceDelegation,
        inlineMethod, pullUpMethod, pushDownField, extractClass
    }


}
