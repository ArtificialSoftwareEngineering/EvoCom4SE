/**
 * 
 */
package entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import edu.wayne.cs.severe.redress2.controller.HierarchyBuilder;
import edu.wayne.cs.severe.redress2.controller.MetricUtils;
import edu.wayne.cs.severe.redress2.controller.metric.CodeMetric;
import edu.wayne.cs.severe.redress2.entity.ProgLang;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entities.RefKey;
import entities.Register;
import repositories.RegisterRepository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Daavid
 *
 */
public final class MetaphorCode {
	
	private static HierarchyBuilder builder;
	private static List<TypeDeclaration> sysTypeDcls;
	private static HashMap<Integer,TypeDeclaration> mapClass=
			new HashMap<Integer,TypeDeclaration>();
	
	private static HashMap<Integer,TypeDeclaration> mapNewClass=
			new HashMap<Integer,TypeDeclaration>();
	
	private static ProgLang lang;
	private static ArrayList<CodeMetric> metrics;
	private static File systemPath;
	private static String sysName;
	
	private static int COUNTER = 0;
	
	public MetaphorCode(MainPredFormulasBIoRIPM init) {
		this.systemPath = init.getSystemPath();
		this.sysName = init.getSysName();
		this.sysTypeDcls = init.getSysTypeDcls();
		this.builder = init.getBuilder();
		this.lang = init.getLang();
		this.metrics = init.getMetrics();
		bitAssignerClass();
	}
	
	//Method for assigning a bit representation to each Class
	private void bitAssignerClass(){
		//BitArray array; 
		int i=0;
		for (TypeDeclaration typeDcl : sysTypeDcls) {
			//array = new BitArray(tamBitArray,false);
			//BitArrayConverter.setNumber(array, 0, tamBitArray, i++); //set number inside bitarray
			typeDcl.setId( i );
			this.mapClass.put(i++, typeDcl);
		}
	}
	
	//Method for adding a class into the HashMap
	public void addClasstoHash(String pack, String name){
		this.mapNewClass.put(COUNTER++, 
				new TypeDeclaration(pack,name));
	}
	
	//Get the complete list of Methods of a specific class
	public static LinkedHashSet<String> getMethodsFromClass(TypeDeclaration typeDcl) {
		LinkedHashSet<String> methods = new LinkedHashSet<String>();
		try {
			methods = MetricUtils.getMethods(typeDcl);
			
		} catch (Exception e) {
			System.out.println("Error for class (in Methaphor): " + typeDcl.getQualifiedName()
			+ " - " + e.getMessage());
			methods = null;
		}
		
		return methods;
	
	}
	
	//Get the complete list of Fields of a specific class
	public static HashSet<String> getFieldsFromClass(TypeDeclaration typeDcl) {
		HashSet<String> fields = new HashSet<String>();
		try {
			fields = MetricUtils.getFields(typeDcl);
			
		} catch (Exception e) {
			System.out.println("Error for class (in Methaphor): " + typeDcl.getQualifiedName()
			+ " - " + e.getMessage());
			fields = null;
		}
		
		return fields;
	
	}

	public static HierarchyBuilder getBuilder() {
		return builder;
	}

	public List<TypeDeclaration> getSysTypeDcls() {
		return sysTypeDcls;
	}

	public ProgLang getLang() {
		return lang;
	}

	public HashMap<Integer, TypeDeclaration> getMapClass() {
		return mapClass;
	}

	public void setMapClass(HashMap<Integer, TypeDeclaration> mapClass) {
		this.mapClass = mapClass;
	}

	public ArrayList<CodeMetric> getMetrics() {
		return metrics;
	}

	public File getSystemPath() {
		return systemPath;
	}

	public String getSysName() {
		return sysName;
	}
	
	public static LoadingCache< RefKey, List<Register> > RefactoringCache(){
		 //create a cache for refactorings based on their tgt,src,fld,mth and refid
	      LoadingCache<RefKey, List<Register> > refactoringCache = CacheBuilder.newBuilder()
		    .maximumSize(1000000) // maximum 1000000 records can be cached
		    .expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after 30 minutes of access
		    .build(
		    		new CacheLoader<RefKey, List<Register> >(){ // build the cacheloader
					@Override
					public List<Register> load(RefKey refKey) throws Exception {
						//make the expensive call
						RegisterRepository repo = new RegisterRepository();
						return repo.getRegistersByClass(refKey) ;
					} 
				    }
		    );
	      
	      return refactoringCache;
	      
	}
	
}
