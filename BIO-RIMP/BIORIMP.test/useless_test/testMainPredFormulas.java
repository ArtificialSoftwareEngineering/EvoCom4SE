package useless_test;

import edu.wayne.cs.severe.redress2.main.MainPredFormulas;

public class testMainPredFormulas {

    public static void main(String[] argss) {
        // TODO Auto-generated method stub
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\evolution\\src", "-s", "     evolution      ",
                "-r", userPath + "\\test_data\\refs\\refsDummyAll.txt"};
        MainPredFormulas.main(args);
    }

}
