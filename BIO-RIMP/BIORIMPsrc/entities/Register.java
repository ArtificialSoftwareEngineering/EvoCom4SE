package entities;

/**
 * Created by developer on 12/29/15.
 */
public class Register extends Entity {
    public static String COLUMN_REFACTOR = "refactor_id";
    public static String COLUMN_CODE = "code";
    public static String COLUMN_VALUE = "value";


    private int refactor;
    private String code;
    private double value;

    public  Register (){

    }
    public int getRefactor() {
        return refactor;
    }

    public void setRefactor(int refactor) {
        this.refactor = refactor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Register(int refactor, String code, double value){
        super();
        this.refactor = refactor;
        this.code = code;

        this. value = value;

    }


}
