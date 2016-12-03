package javabio.optmodel.space;

import java.util.ArrayList;
import java.util.List;

import javabio.controller.RefactoringReaderBIoRIMP;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactoring;
import edu.wayne.cs.severe.redress2.entity.refactoring.json.OBSERVRefactorings;
import edu.wayne.cs.severe.redress2.exception.ReadException;
import javabio.optmodel.mappings.metaphor.MetaphorCode;
import javabio.optmodel.space.feasibility.FeasibilityRefactor;
import javabio.optmodel.space.generation.*;
import unalcol.clone.Clone;
import unalcol.random.integer.IntUniform;
import unalcol.random.raw.RawGenerator;
import unalcol.search.space.Space;

public class VarLengthOperRefSpace extends Space<List<RefactoringOperation>> {
    protected int minLength;
    protected int maxVarGenes;
    protected int gene_size;

    public VarLengthOperRefSpace(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxVarGenes = maxLength - minLength;
        this.gene_size = 1;
    }

    public VarLengthOperRefSpace(int minLength, int maxLength,
                                 int gene_size) {
        this.minLength = minLength;
        this.gene_size = gene_size;
        this.maxVarGenes = (maxLength - minLength) / gene_size;
    }

    @Override
    public boolean feasible(List<RefactoringOperation> x) {
        boolean feasible = false;
        String mapRefactor;

        for (RefactoringOperation refOp : x) {
            mapRefactor = refOp.getRefType().getAcronym();
            switch (mapRefactor) {
                case "PUF":
                    feasible = FeasibilityRefactor.feasibleRefactorPUF(refOp);
                    break;
                case "MM":
                    feasible = FeasibilityRefactor.feasibleRefactorMM(refOp);
                    break;
                case "RMMO":
                    feasible = FeasibilityRefactor.feasibleRefactorRMMO(refOp);
                    break;
                case "RDI":
                    feasible = FeasibilityRefactor.feasibleRefactorRDI(refOp);
                    break;
                case "MF":
                    feasible = FeasibilityRefactor.feasibleRefactorMF(refOp);
                    break;
                case "EM":
                    feasible = FeasibilityRefactor.feasibleRefactorEM(refOp);
                    break;
                case "PDM":
                    feasible = FeasibilityRefactor.feasibleRefactorPDM(refOp);
                    break;
                case "RID":
                    feasible = FeasibilityRefactor.feasibleRefactorRID(refOp);
                    break;
                case "IM":
                    feasible = FeasibilityRefactor.feasibleRefactorIM(refOp);
                    break;
                case "PUM":
                    feasible = FeasibilityRefactor.feasibleRefactorPUM(refOp);
                    break;
                case "PDF":
                    feasible = FeasibilityRefactor.feasibleRefactorPDF(refOp);
                    break;
                case "EC":
                    feasible = FeasibilityRefactor.feasibleRefactorEC(refOp);
                    break;
            }//END CASE

            if (!feasible) {
                System.out.println("Wrong Feasible Refactor (IN FEASIBLE): " + refOp.toString());
                break;
            }
        }
        return minLength <= x.size() && x.size() <= minLength + maxVarGenes * gene_size && feasible;
    }

    @Override
    public double feasibility(List<RefactoringOperation> x) {
        return feasible(x) ? 1 : 0;
    }

    @Override
    public List<RefactoringOperation> repair(List<RefactoringOperation> x) {
        int maxLength = minLength + maxVarGenes * gene_size;
        OBSERVRefactorings oper = new OBSERVRefactorings();
        List<OBSERVRefactoring> refactorings = new ArrayList<OBSERVRefactoring>();
        String mapRefactor;
        GeneratingRefactor specificRefactor = null;
        boolean feasible = false;
        int break_point = 10;

        List<RefactoringOperation> clon;
        List<RefactoringOperation> repaired = new ArrayList<RefactoringOperation>();

        if (x.size() > maxLength) {
            //x = x.subBitArray(0,maxLength);
            clon = new ArrayList<RefactoringOperation>();
            for (int i = 0; i < maxLength; i++) {
                clon.add(x.get(i));
            }
        } else {
            if (x.size() < minLength) {
                //x = new BitArray(minLength, true);
                clon = createRefOper(minLength);
                for (int i = x.size(); i < minLength; i++) {
                    x.add(clon.get(i));
                }
            }
            for (int i = 0; i < minLength; i++) {
                x.set(i, x.get(i));
            }
            clon = (List<RefactoringOperation>) Clone.create(x);
        }

        for (RefactoringOperation refOp : clon) {
            mapRefactor = refOp.getRefType().getAcronym();

            switch (mapRefactor) {
                case "PUF":
                    feasible = FeasibilityRefactor.feasibleRefactorPUF(refOp);
                    break;
                case "MM":
                    feasible = FeasibilityRefactor.feasibleRefactorMM(refOp);
                    break;
                case "RMMO":
                    feasible = FeasibilityRefactor.feasibleRefactorRMMO(refOp);
                    break;
                case "RDI":
                    feasible = FeasibilityRefactor.feasibleRefactorRDI(refOp);
                    break;
                case "MF":
                    feasible = FeasibilityRefactor.feasibleRefactorMF(refOp);
                    break;
                case "EM":
                    feasible = FeasibilityRefactor.feasibleRefactorEM(refOp);
                    break;
                case "PDM":
                    feasible = FeasibilityRefactor.feasibleRefactorPDM(refOp);
                    break;
                case "RID":
                    feasible = FeasibilityRefactor.feasibleRefactorRID(refOp);
                    break;
                case "IM":
                    feasible = FeasibilityRefactor.feasibleRefactorIM(refOp);
                    break;
                case "PUM":
                    feasible = FeasibilityRefactor.feasibleRefactorPUM(refOp);
                    break;
                case "PDF":
                    feasible = FeasibilityRefactor.feasibleRefactorPDF(refOp);
                    break;
                case "EC":
                    feasible = FeasibilityRefactor.feasibleRefactorEC(refOp);
                    break;
            }//END CASE

            if (!feasible) {
                //FIXME reapair must be static
                switch (mapRefactor) {
                    case "PUF":
                        specificRefactor = new GeneratingRefactorPUF();
                        break;
                    case "MM":
                        specificRefactor = new GeneratingRefactorMM();
                        break;
                    case "RMMO":
                        specificRefactor = new GeneratingRefactorRMMO();
                        break;
                    case "RDI":
                        specificRefactor = new GeneratingRefactorRDI();
                        break;
                    case "MF":
                        specificRefactor = new GeneratingRefactorMF();
                        break;
                    case "EM":
                        specificRefactor = new GeneratingRefactorEM();
                        break;
                    case "PDM":
                        specificRefactor = new GeneratingRefactorPDM();
                        break;
                    case "RID":
                        specificRefactor = new GeneratingRefactorRID();
                        break;
                    case "IM":
                        specificRefactor = new GeneratingRefactorIM();
                        break;
                    case "PUM":
                        specificRefactor = new GeneratingRefactorPUM();
                        break;
                    case "PDF":
                        specificRefactor = new GeneratingRefactorPDF();
                        break;
                    case "EC":
                        specificRefactor = new GeneratingRefactorEC();
                        break;
                    default:
                        specificRefactor = new GeneratingRefactorEC();
                }//END CASE
                refactorings.add(specificRefactor.repairRefactor(refOp, break_point));
            } else {
                repaired.add(refOp);
            }
        }

        oper.setRefactorings(refactorings);

        try {
            repaired.addAll(MetaphorCode.getRefactorReader().getRefactOperations(oper));

        } catch (ReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Reading Error in Repair");
            return null;
        }

        return repaired;
    }

    @Override
    public List<RefactoringOperation> get() {
        //return (maxVarGenes>0)?new BitArray(minLength+RawGenerator.integer(this, maxVarGenes*gene_size), true):new BitArray(minLength, true);
        return (maxVarGenes > 0) ? createRefOper(minLength + RawGenerator.integer(this, maxVarGenes * gene_size)) : createRefOper(minLength);

    }

    public List<RefactoringOperation> createRefOper(int n) {
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

            //System.out.println( "Refactor [ " + Refactoring.values()[mapRefactor] + "]");
            refactorings.add(randomRefactor.generatingRefactor( new ArrayList<Double>() ));

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