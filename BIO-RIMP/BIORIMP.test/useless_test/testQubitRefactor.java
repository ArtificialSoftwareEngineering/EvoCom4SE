package useless_test;

import javabio.optmodel.mappings.quantum.QubitRefactor;

public class testQubitRefactor {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //Random QubitRefactor
        QubitRefactor refactor_1 = new QubitRefactor(true, 4);
        System.out.println("Qubits: " + refactor_1.getGenObservation().toString());
        System.out.println("Qubits Refactor: " + refactor_1.getGenRefactor().getGenObservation().toString());
        System.out.println("Qubits SRC: " + refactor_1.getGenSRC().getGenObservation().toString());
        System.out.println("Qubits FLD: " + refactor_1.getGenFLD().getGenObservation().toString());
        System.out.println("Qubits MTD: " + refactor_1.getGenMTD().getGenObservation().toString());
        System.out.println("Qubits TGT: " + refactor_1.getGenTGT().getGenObservation().toString());

        //Random QubitRefactor
        QubitRefactor refactor_2 = new QubitRefactor(false, 4);
        System.out.println("Qubits: " + refactor_2.getGenObservation().toString());
        System.out.println("Qubits Refactor: " + refactor_2.getGenRefactor().getGenObservation().toString());
        System.out.println("Qubits SRC: " + refactor_2.getGenSRC().getGenObservation().toString());
        System.out.println("Qubits FLD: " + refactor_2.getGenFLD().getGenObservation().toString());
        System.out.println("Qubits MTD: " + refactor_2.getGenMTD().getGenObservation().toString());
        System.out.println("Qubits TGT: " + refactor_2.getGenTGT().getGenObservation().toString());
    }

}
