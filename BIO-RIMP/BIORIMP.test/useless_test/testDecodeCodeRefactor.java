package useless_test;

import java.util.ArrayList;
import java.util.List;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import java.model.mappings.quantum.QubitRefactor;
import java.model.mappings.quantum.CodeDecodeRefactorList;
import unalcol.search.multilevel.CodeDecodeMap;

public class testDecodeCodeRefactor {

    public static void main(String[] argss) {
        // TODO Auto-generated method stub
        //Getting the Metaphor
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "java/model/fitness      "};


        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        //metaphor.setSysTypeDcls(init.getSysTypeDcls());
        //metaphor.setBuilder(init.getBuilder());
        //metaphor.setLang(init.getLang());
        //Creating the individual
        List<QubitRefactor> genome = new ArrayList<QubitRefactor>();
        List<RefactoringOperation> phe = new ArrayList<RefactoringOperation>();
        for (int i = 0; i < 10000; i++) {
            genome.add(new QubitRefactor(true, 4));
            System.out.println(i + " " +
                    genome.get(i).getGenObservation().toString());
        }
        //Processing EncodeDecode
        CodeDecodeMap<List<QubitRefactor>, List<RefactoringOperation>> map
                = new CodeDecodeRefactorList();

        phe = map.decode(genome);

        for (int i = 0; i < phe.size(); i++) {
            System.out.println(i + " " + phe.get(i).toString());
        }
    }

}
