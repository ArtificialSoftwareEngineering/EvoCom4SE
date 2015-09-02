package edu.wayne.cs.severe.redress2.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;

import edu.wayne.cs.severe.redress2.controller.HierarchyBuilder;
import edu.wayne.cs.severe.redress2.controller.metric.CodeMetric;
import edu.wayne.cs.severe.redress2.controller.processor.PredFormulasProcessorBIoRIPM;
import edu.wayne.cs.severe.redress2.entity.ProgLang;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.utils.ArgsParser;
import edu.wayne.cs.severe.redress2.utils.ArgsPredParser;

/**
 * @author danaderp
 * @version 1.0
 * @created 03-Agosto-2015 12:28:47
 */
public class MainPredFormulasBIoRIPM {
	
	/**
	 * 
	 * @param args
	 */
	private static List<TypeDeclaration> sysTypeDcls;
	private static HierarchyBuilder builder;
	private static ProgLang lang;
	private static ArrayList<CodeMetric> metrics;
	private static File systemPath;
	private static String sysName;
	
	public void main(String[] args) {

		try {

			// parse the arguments ArgsPredParser suppresed
			ArgsParser parser = new ArgsParser();
			try {
				parser.processArgs(args);
			} catch (ParseException e) {
				System.out.println(e.getMessage());
				parser.printHelp();
				return;
			}

			// process the system (create the metrics and builder in the constructor)
			PredFormulasProcessorBIoRIPM processor = new PredFormulasProcessorBIoRIPM(
					parser.getSysPath(), parser.getSysName(), parser.getLang());
			//Getting the builder and the typesdcls
			builder = processor.getBuilder();
			sysTypeDcls = processor.getSysTypeDcls();
			lang = processor.getLang();
			metrics = processor.getMetrics();
			systemPath = parser.getSysPath();
			sysName = parser.getSysName();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public HierarchyBuilder getBuilder() {
		return builder;
	}
	
	public List<TypeDeclaration> getSysTypeDcls(){
		return  sysTypeDcls;
	}
	public ProgLang getLang() {
		return lang;
	}
	public ArrayList<CodeMetric> getMetrics() {
		return metrics;
	}
	public void setMetrics(ArrayList<CodeMetric> metrics) {
		MainPredFormulasBIoRIPM.metrics = metrics;
	}
	public File getSystemPath() {
		return systemPath;
	}
	public void setSystemPath(File systemPath) {
		MainPredFormulasBIoRIPM.systemPath = systemPath;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		MainPredFormulasBIoRIPM.sysName = sysName;
	}
	
	

}// end MainPredFormulas