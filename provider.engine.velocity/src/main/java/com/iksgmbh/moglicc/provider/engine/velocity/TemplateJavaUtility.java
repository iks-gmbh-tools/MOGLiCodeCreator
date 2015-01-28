package com.iksgmbh.moglicc.provider.engine.velocity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

public class TemplateJavaUtility {

	private static HashSet<String> PRIMITIVE_TYPE = new HashSet<String>();
	static {
		PRIMITIVE_TYPE.add("boolean");
		PRIMITIVE_TYPE.add("int");
		PRIMITIVE_TYPE.add("long");
		PRIMITIVE_TYPE.add("float");
		PRIMITIVE_TYPE.add("double");
		PRIMITIVE_TYPE.add("char");
		PRIMITIVE_TYPE.add("byte");
	}

	public static boolean isPrimitiveType(String clazz) {
		return PRIMITIVE_TYPE.contains(clazz);
	}

	private static Set<String> PRIMITIVE_TYPE_WRAPPERS = new HashSet<String>();
	static {
		PRIMITIVE_TYPE_WRAPPERS.add("Boolean");
		PRIMITIVE_TYPE_WRAPPERS.add("Integer");
		PRIMITIVE_TYPE_WRAPPERS.add("Long");
		PRIMITIVE_TYPE_WRAPPERS.add("Float");
		PRIMITIVE_TYPE_WRAPPERS.add("Double");
		PRIMITIVE_TYPE_WRAPPERS.add("Character");
		PRIMITIVE_TYPE_WRAPPERS.add("Byte");
	}
	
	private static Set<String> PRIMITIVE_TYPE_WRAPPERS_FULLY_QUALIFIED = new HashSet<String>();
	static {
		PRIMITIVE_TYPE_WRAPPERS.add("java.lang.Boolean");
		PRIMITIVE_TYPE_WRAPPERS.add("java.lang.Integer");
		PRIMITIVE_TYPE_WRAPPERS.add("java.lang.Long");
		PRIMITIVE_TYPE_WRAPPERS.add("java.lang.Float");
		PRIMITIVE_TYPE_WRAPPERS.add("java.lang.Double");
		PRIMITIVE_TYPE_WRAPPERS.add("java.lang.Character");
		PRIMITIVE_TYPE_WRAPPERS.add("java.lang.Byte");
	}
	

	public static boolean isPrimitiveTypeWrapper(String clazz) {
		return PRIMITIVE_TYPE_WRAPPERS.contains(clazz)
				|| PRIMITIVE_TYPE_WRAPPERS_FULLY_QUALIFIED.contains(clazz);
	}

	// These classes are relevant for cloning purpose because they need not to be cloned.
	private static Set<String> IMMUTTABLE_CLASSES = new HashSet<String>();
	static {
		IMMUTTABLE_CLASSES.addAll(PRIMITIVE_TYPE);
		IMMUTTABLE_CLASSES.addAll(PRIMITIVE_TYPE_WRAPPERS);
		IMMUTTABLE_CLASSES.add("String");
		IMMUTTABLE_CLASSES.add("BigDecimal");
	}

	public static boolean isImmutableClass(String clazz) {
		return IMMUTTABLE_CLASSES.contains(clazz);
	}
	

	/**
	 * Removes package information from fully qualified class name: <code>org.joda.time.DateTime</code> -> <code>DateTime</code>
	 * @param clazz
	 * @return simple name of class
	 */
	public static String getSimpleClassName(final String clazz) {
		Validate.notNull(clazz, "Argument 'clazz' is null.");

		int indexOfLastDot = clazz.lastIndexOf('.');
		return (indexOfLastDot < 0) ? clazz : clazz.substring(indexOfLastDot + 1);
	}
	
	public static List<String> getSimpleClassName(final List<String> classes) {
		Validate.notNull(classes, "Argument 'classes' is null.");
		final List<String> toReturn = new ArrayList<String>();
		for (final String clazz : classes) {
			toReturn.add(getSimpleClassName(clazz));
		}
		return toReturn;
	}
	
	public static List<String> searchForImportClasses(final ClassDescriptor classDescriptor) {
		final HashSet<String> importClasses = new HashSet<String>();
		final List<MetaInfo> allMetaInfos = classDescriptor.getAllMetaInfos();
		
		for (final MetaInfo metaInfo : allMetaInfos) {
			checkTypeOfValue(importClasses, metaInfo.getValue());
		}
		
		final List<String> toReturn = new ArrayList<String>(importClasses); 
		Collections.sort(toReturn);
		return toReturn;
	}


	private static void checkTypeOfValue(final HashSet<String> importClasses, final String className) {
		if (isJavaMetaTypeGeneric(className)) {
			final int beginIndex = className.indexOf("<");
			final int endIndex = className.lastIndexOf(">");
			String val = className.substring(beginIndex+1, endIndex);
			checkTypeOfValue(importClasses, val);
			val = className.substring(0, beginIndex);
			checkTypeOfValue(importClasses, val);
		} else if (isJavaMetaTypeArray(className)) {
			importClasses.add("java.util.Arrays");
			final int beginIndex = className.indexOf("[");
			String val = className.substring(0,beginIndex);		
			checkTypeOfValue(importClasses, val);
		} else if (ClassNameData.isFullyQualifiedClassnameValid(className)) {
			importClasses.add(className);
		} else if (ClassNameData.isSimpleClassNameKnown(className)) {
			importClasses.add(ClassNameData.makeNameFullyQualifiedNameIfsimple(className));
		}
	}

	/*-----------------------------------------------------------------------*\
	 * Import Statement                                                      *
	\*-----------------------------------------------------------------------*/

	private Set<String> cachedImportStatements = new HashSet<String>(); // avoid multiple import statements

	/**
	 * Adds fully qualilified class names if necesarry (Date -> java.util.Date)
	 * @param clazz
	 * @return Import statement for clazz. For types like "Boolean" no import statement is generated and 
	 *         an empty String returned. For types like "Date", the package "java.util" is added automatically.
	 */
	public String importStatement(String clazz) {
		Validate.notNull(clazz, "Argument 'clazz' is null.");

		if (clazz.equals(getSimpleClassName(clazz))) {
			// primitive types
			return "";
		}
		
		String importStatement = "import " + clazz + ";";
		
		if (cachedImportStatements.contains(importStatement)) {
			return ""; // avoid dublicates
		}
		cachedImportStatements.add(importStatement);

		return importStatement + "\n";
	}
	
	public static boolean isFullyQualifiedClassName(final String s) {
		try {
			new ClassNameData(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String getArrayElementType(final String javaType) {
		if (! isJavaMetaTypeArray(javaType)) {
			return "Error: '" + javaType + "' is no Array type!";
		}
		final int pos1 = javaType.indexOf("[");
		if (pos1 > -1) {
			return javaType.substring(0 ,pos1);
		}
		return javaType;
	}
	
	public static boolean isJavaMetaTypePrimitive(final String javaType) {
		if (PRIMITIVE_TYPE.contains(javaType)) {
			return true;
		}
		return false;
	}
	
	public static boolean isJavaMetaTypeGeneric(final String javaType) {
		if (javaType.contains("<") && javaType.endsWith(">")) {
			return true;
		}
		return false;
	}
	
	public static String getCollectionMetaType(final String javaType) {
		if (! isJavaMetaTypeCollection(javaType)) {
			return "Error: '" + javaType + "' is no Collection type!";
		}
		final int pos1 = javaType.indexOf("<");
		if (pos1 > -1) {
			return javaType.substring(0 ,pos1);
		}
		return javaType;
	}
	
	public static String getCollectionElementType(final String javaType) {
		final int pos1 = javaType.indexOf("<");
		final int pos2 = javaType.lastIndexOf(">");
		if (pos1 > -1 && pos2 > pos1 ) {
			return javaType.substring(pos1+1, pos2);
		}
		return "Error in method getCollectionElementType with argument '" + javaType + "'";
	}

	public static boolean isJavaMetaTypeArray(final String javaType) {
		if (javaType.contains("[") && javaType.endsWith("]")) {
			return true;
		}
		return false;
	}

	public static boolean isJavaMetaTypeCollection(String javaType) {
		int pos = javaType.indexOf("<");
		if (pos > -1) {
			javaType = javaType.substring(0, pos);
		}

		try {
			final ClassNameData cnd = new ClassNameData(javaType);
			final Class<?> name = Class.forName(cnd.getFullyQualifiedClassname());
			return hasInterface(name, Collection.class.getName());
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * 
	 * @param cl
	 * @param interfaceToSearch 
	 * @return returns true if cl is of type interfaceSimpleNameToSearch
	 */
	public static boolean hasInterface(final Class<?> cl, final String interfaceToSearch) {
		final Class<?>[] interfaces = cl.getInterfaces();
		for (final Class<?> interf : interfaces) {			
			if (interf.getName().equals(interfaceToSearch)) {
				return true;
			}
			final boolean hasInterface = hasInterface(interf, interfaceToSearch);
			if (hasInterface) return true;
		}
		return false;
	}

}
