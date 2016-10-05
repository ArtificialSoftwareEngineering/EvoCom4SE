package java.storage.entities;

/**
 * Created by developer on 12/29/15.
 */
public class Register extends Entity {
    public static String COLUMN_REFACTOR = "refactor_id";
    public static String COLUMN_VALUE = "value";
    public static String COLUMN_SOURCES = "sources";
    public static String COLUMN_TARGETS = "targets";
    public static String COLUMN_METHOD = "method";
    public static String COLUMN_FIELD = "field";
    public static String COLUMN_METRIC = "metric";
    public static String COLUMN_CLASS = "class";


    private String refactor;
    private double value;
    private String sources;
    private String targets;


    private String method;
    private String field;
    private String metric;
    private String classs;


    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getTargets() {
        return targets;
    }

    public void setTargets(String targets) {
        this.targets = targets;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }


    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }


    public Register() {

    }

    public String getRefactor() {
        return refactor;
    }

    public void setRefactor(String refactor) {
        this.refactor = refactor;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getClasss() {
        return classs;
    }

    public void setClasss(String classs) {
        this.classs = classs;
    }

    public Register(String refactor, String metric, double value,
                    String sources, String targets, String field, String method, String classs) {
        super();
        this.refactor = refactor;
        this.metric = metric;
        this.value = value;
        this.sources = sources;
        this.targets = targets;
        this.method = method;
        this.field = field;
        this.classs = classs;

    }


}
