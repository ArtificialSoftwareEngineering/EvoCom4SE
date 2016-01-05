package repositories;

import entities.Register;

/**
 * Created by developer on 12/30/15.
 */
public class TestMain {


    public static void main(String args []){
        Register r = new Register(2,"123254",5.6);
        RegisterRepository repo = new RegisterRepository();
        Register r2 = repo.getRegister(2, "123254");
        System.out.println(r2.getValue());
        repo.insertRegister(r);
        for(Register reg: repo.fetchAll()){
            System.out.println(reg.getCode());
        }
    }
}