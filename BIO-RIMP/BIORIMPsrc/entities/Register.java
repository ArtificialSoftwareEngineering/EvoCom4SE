package entities;

/**
 * Created by developer on 12/29/15.
 */
public class Register extends Entity {
    public static String COLUMN_REFACTOR = "refactor_id";
    public static String COLUMN_CODE = "code";
    public static String COLUMN_VALUE = "value";
    public static String COLUMN_METRIC = "metric";


    private String refactor;
    private String code;
    private double value;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    private String metric;

    public  Register (){

    }
    public String getRefactor() {
        return refactor;
    }

    public void setRefactor(String refactor) {
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

    public Register(String refactor, String code, String metric, double value){
        super();
        this.refactor = refactor;
        this.code = code;
        this.metric= metric;
        this. value = value;

    }


}
