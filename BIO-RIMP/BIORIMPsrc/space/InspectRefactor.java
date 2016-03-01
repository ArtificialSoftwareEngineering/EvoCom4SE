package space;

import java.util.List;

import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import entity.MetaphorCode;

public final class InspectRefactor {
	public InspectRefactor(){
		
	}
	/**
	 * 
	 * @param value_mtd
	 * @param sysType_src
	 * @return
	 */
	public static boolean inspectMethodNotConstructor(List<String> value_mtd,
			TypeDeclaration sysType_src){
		if(value_mtd.get(0).equals(sysType_src.getName()))
			return false;
		else 
			return true;
	}
	
	/**
	 * 
	 * @param value_mtd
	 * @param sysType_src
	 * @return
	 */
	public static boolean inspectOverrideParents(List<String> value_mtd, TypeDeclaration sysType_src) {
		boolean feasible = true;
		if (MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()) != null)
			if (!MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName()).isEmpty()) {
				for (TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get(sysType_src.getQualifiedName())) {
					if (MetaphorCode.getMethodsFromClass(clase) != null)
						if (!MetaphorCode.getMethodsFromClass(clase).isEmpty()) {
							for (String method : MetaphorCode.getMethodsFromClass(clase)) {
								if (method.equals(value_mtd.get(0))) {
									feasible = false;
									break;
								}
							}
						}
				}
			}
		return feasible;
	}
	
	/**
	 * 
	 * @param value_mtd
	 * @param sysType_src
	 * @param sysType_tgt
	 * @return
	 */
	public static boolean inspectHierarchyOverrideParents(List<String> value_mtd, TypeDeclaration sysType_src, TypeDeclaration sysType_tgt){
		boolean feasible = true;
		if( MetaphorCode.getBuilder().getParentClasses().get( sysType_src.getQualifiedName()) != null)
			if( !MetaphorCode.getBuilder().getParentClasses().get( sysType_src.getQualifiedName()).isEmpty() ){
				for( TypeDeclaration clase : MetaphorCode.getBuilder().getParentClasses().get( sysType_src.getQualifiedName()) ){
					if ( MetaphorCode.getMethodsFromClass(clase) != null )
						if( !MetaphorCode.getMethodsFromClass(clase).isEmpty() ){
							for( String method : MetaphorCode.getMethodsFromClass(clase) ){
								if( method.equals( value_mtd.get(0) ) || clase.equals(sysType_tgt) ){
									feasible = false;
									break;
								}
							}
						}
				}
			}
		return feasible;
	}
}
