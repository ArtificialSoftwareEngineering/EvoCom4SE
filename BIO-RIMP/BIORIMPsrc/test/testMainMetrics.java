package test;

import edu.wayne.cs.severe.redress2.main.MainMetrics;

public class testMainMetrics {

	public static void main(String[] argsS) {
		//First Test bith evolution library
		// TODO Auto-generated method stub
		String userPath = System.getProperty("user.dir");
        String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\evolutionaryagent\\src","-s", "     evolutionaryagent      " };
        MainMetrics.main(args);
	}

}
