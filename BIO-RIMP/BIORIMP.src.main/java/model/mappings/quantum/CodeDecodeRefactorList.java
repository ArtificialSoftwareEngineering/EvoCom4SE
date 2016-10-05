package java.model.mappings.quantum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import java.controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactorings;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import java.model.mappings.MappingRefactor;
import java.model.mappings.MappingRefactorEC;
import java.model.mappings.MappingRefactorEM;
import java.model.mappings.MappingRefactorIM;
import java.model.mappings.MappingRefactorMF;
import java.model.mappings.MappingRefactorMM;
import java.model.mappings.MappingRefactorPDF;
import java.model.mappings.MappingRefactorPDM;
import java.model.mappings.MappingRefactorPUF;
import java.model.mappings.MappingRefactorPUM;
import java.model.mappings.MappingRefactorRDI;
import java.model.mappings.MappingRefactorRID;
import java.model.mappings.MappingRefactorRMMO;
import java.model.mappings.metaphor.MetaphorCode;
import unalcol.search.multilevel.CodeDecodeMap;

public class CodeDecodeRefactorList
        extends CodeDecodeMap<List<QubitRefactor>, List<RefactoringOperation>> {


    public CodeDecodeRefactorList( ) {

    }

    public List<RefactoringOperation> decode(List<QubitRefactor> genome) {
        RefactoringReaderBIoRIMP reader = new RefactoringReaderBIoRIMP();

        try {
            return reader.getRefactOperations(
                    mappingRefactoring(genome)
            );
        } catch (ReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Reading Error");
            return null;
        }
    }

    private OBSERVRefactorings mappingRefactoring(List<QubitRefactor> genome) {
        int mapRefactor;

        OBSERVRefactorings oper = new OBSERVRefactorings();

        //metaphor.bitAssignerClass(); //Building the hash map

        List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();

        MappingRefactor mappingRefactor = null;

        for (int i = 0; i < genome.size(); i++) {
            //SubIndex of the RefactoringType
            int getnumber = genome.get(i).getNumberGenome(genome.get(i).getGenRefactor());
            mapRefactor = getnumber % (Refactoring.values().length);
            //According to the RefactoringType is selected the params mapping
            switch (mapRefactor) {
                case 0:
                    mappingRefactor = new MappingRefactorPUF();
                    break;
                case 1:
                    mappingRefactor = new MappingRefactorMM();
                    break;
                case 2:
                    mappingRefactor = new MappingRefactorRMMO();
                    break;
                case 3:
                    mappingRefactor = new MappingRefactorRDI();
                    break;
                case 4:
                    mappingRefactor = new MappingRefactorMF();
                    break;
                case 5:
                    mappingRefactor = new MappingRefactorEM();
                    break;
                case 6:
                    mappingRefactor = new MappingRefactorPDM();
                    break;
                case 7:
                    mappingRefactor = new MappingRefactorRID();
                    break;
                case 8:
                    mappingRefactor = new MappingRefactorIM();
                    break;
                case 9:
                    mappingRefactor = new MappingRefactorPUM();
                    break;
                case 10:
                    mappingRefactor = new MappingRefactorPDF();
                    break;
                case 11:
                    mappingRefactor = new MappingRefactorEC();
                    break;
            }//END CASE

            refactorings.add(mappingRefactor.mappingRefactor(genome.get(i)));

        }//END LOOP

        oper.setRefactorings(refactorings);
        return oper;
    }

    //coding inside genome
    public List<QubitRefactor> code(List<RefactoringOperation> thing) {

        int QUBITTAM = 4; //could be generated in the constructor
        List<QubitRefactor> coding = new ArrayList<QubitRefactor>();

        String[] refactor = new String[1];
        for (RefactoringOperation ref : thing) {
            String acronym = ref.getRefType().getAcronym();
            //extracting ref type
            switch (acronym) {
                case "PUF":
                    refactor[0] = Integer.toBinaryString(0);
                    break;
                case "MM":
                    refactor[0] = Integer.toBinaryString(1);
                    break;
                case "RMMO":
                    refactor[0] = Integer.toBinaryString(2);
                    break;
                case "RDI":
                    refactor[0] = Integer.toBinaryString(3);
                    break;
                case "MF":
                    refactor[0] = Integer.toBinaryString(4);
                    break;
                case "EM":
                    refactor[0] = Integer.toBinaryString(5);
                    break;
                case "PDM":
                    refactor[0] = Integer.toBinaryString(6);
                    break;
                case "RID":
                    refactor[0] = Integer.toBinaryString(7);
                    break;
                case "IM":
                    refactor[0] = Integer.toBinaryString(8);
                    break;
                case "PUM":
                    refactor[0] = Integer.toBinaryString(9);
                    break;
                case "PDF":
                    refactor[0] = Integer.toBinaryString(10);
                    break;
                case "EC":
                    refactor[0] = Integer.toBinaryString(11);
                    break;
            }//end case extracting ref


            String[] src = null;
            String[] fld = null;
            String[] mtd = null;
            String[] tgt = null;

            int numberSrc = 0;
            int numberfld = 0;
            int numbermtd = 0;
            int numbertgt = 0;

            int hashSetfld = 0;
            int hashSetmtd = 0;

            //extracting source classes and its dependencies
            if (ref.getParams().get("src") != null) {
                for (RefactoringParameter rp : ref.getParams().get("src")) {
                    for (Entry<Integer, TypeDeclaration> clase : MetaphorCode.getMapClass().entrySet()) {
                        if (clase.getValue().equals(rp.getCodeObj())) {
                            src = new String[ref.getParams().get("src").size()];
                            src[numberSrc] = Integer.toBinaryString(clase.getKey());

                            //extracting fields of the source classes
                            if (ref.getParams().get("fld") != null) {
                                for (RefactoringParameter rp_fld : ref.getParams().get("fld")) {

                                    for (String field : MetaphorCode.getFieldsFromClass(clase.getValue())) {

                                        if (rp_fld.getCodeObj().toString().contains(field)) {
                                            //if( field.equals(  rp_fld.getCodeObj()  ) ){
                                            fld = new String[ref.getParams().get("fld").size()];
                                            fld[numberfld] = Integer.toBinaryString(hashSetfld);
                                        }
                                        hashSetfld++;
                                    }
                                    hashSetfld = 0;
                                    numberfld++;
                                }
                            }//end if fld

                            //extracting methods of the source classes
                            if (ref.getParams().get("mtd") != null) {
                                for (RefactoringParameter rp_mtd : ref.getParams().get("mtd")) {

                                    for (String method : MetaphorCode.getMethodsFromClass(clase.getValue())) {

                                        if (rp_mtd.getCodeObj().toString().contains(method)) {
                                            //if( method.equals(  rp_mtd.getCodeObj()  ) ){
                                            mtd = new String[ref.getParams().get("mtd").size()];
                                            mtd[numbermtd] = Integer.toBinaryString(hashSetmtd);
                                        }
                                        hashSetmtd++;
                                    }
                                    hashSetmtd = 0;
                                    numbermtd++;
                                }
                            }//end if mtd


                        }
                    }
                    numbermtd = 0;
                    numberfld = 0;
                    numberSrc++;
                }
            }//end if src

            //extracting target classes
            if (ref.getParams().get("tgt") != null) {
                for (RefactoringParameter rp : ref.getParams().get("tgt")) {
                    for (Entry<Integer, TypeDeclaration> clase : MetaphorCode.getMapClass().entrySet()) {
                        if (clase.getValue().equals(rp.getCodeObj())) {
                            tgt = new String[ref.getParams().get("tgt").size()];
                            tgt[numbertgt] = Integer.toBinaryString(clase.getKey());
                        }
                    }
                    numbertgt++;
                }
            }//end if tgt

            coding.add(new QubitRefactor(refactor, src, fld, mtd, tgt, QUBITTAM));
        }//for refactoring operation


        return coding;
    }

    private enum Refactoring {
        pullUpField, moveMethod, replaceMethodObject, replaceDelegationInheritance,
        moveField, extractMethod, pushDownMethod, replaceInheritanceDelegation,
        inlineMethod, pullUpMethod, pushDownField, extractClass
    }

}
