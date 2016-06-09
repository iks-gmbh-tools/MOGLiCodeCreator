/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc.provider.engine.velocity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
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

	// These classes are relevant for cloning purpose because they need not to be cloned.
	private static Set<String> IMMUTTABLE_CLASSES = new HashSet<String>();
	static {
		IMMUTTABLE_CLASSES.addAll(PRIMITIVE_TYPE);
		IMMUTTABLE_CLASSES.addAll(PRIMITIVE_TYPE_WRAPPERS);
		IMMUTTABLE_CLASSES.add("String");
		IMMUTTABLE_CLASSES.add("BigDecimal");
	}
	
	/**
	 * Checks a Integer to be null or not.
	 * @param s
	 * @return true if not null
	 */
	public boolean isIntegerValueSet(Integer i) {
		return i != null;
	}

	/**
	 * Checks a Boolean to be null or not.
	 * @param s
	 * @return true if not null
	 */
	public boolean isBooleanValueSet(Boolean b) {
		return b != null;
	}

	/**
	 * Checks whether a String represents a java primitiv type.
	 * @param type
	 * @return true e.g. for long, but false for Long
	 */
	public static boolean isPrimitiveType(String type) {
		return PRIMITIVE_TYPE.contains(type);
	}
	
	/**
	 * Checks whether a String represents a simple name of a wrapper class of a java primitiv type.
	 * @param clazz
	 * @return true e.g. for Long or java.lang.Long
	 */
	public static boolean isPrimitiveTypeWrapper(String clazz) {
		return PRIMITIVE_TYPE_WRAPPERS.contains(clazz)
				|| PRIMITIVE_TYPE_WRAPPERS_FULLY_QUALIFIED.contains(clazz);
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

	/**
	 * Converts a list of classes into a list of class names.
	 * @param classes
	 * @return list of simple names of classes
	 */
	public static List<String> getSimpleClassName(final List<String> classes) {
		Validate.notNull(classes, "Argument 'classes' is null.");
		final List<String> toReturn = new ArrayList<String>();
		for (final String clazz : classes) {
			toReturn.add(getSimpleClassName(clazz));
		}
		return toReturn;
	}
	
	/**
	 * Reads all metainfo elements of a classDescriptor to gets all class names relevant for import statements.
	 * @param classDescriptor
	 * @return list of class names
	 */
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

	/**
	 * Checks whether a string representation of a class is fully qualified.
	 * @param classname
	 * @return true if fully qualified
	 */
	public static boolean isFullyQualifiedClassName(final String classname) {
		try {
			new ClassNameData(classname);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Determines the type of the elements of an array.
	 * @param javaType
	 * @return string representation of a class, e.g. String for String[]
	 */
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

	/**
	 * Checks a String representation of a javaType to be primitive.
	 * @param javaType
	 * @return true if primitive
	 */
	public static boolean isJavaMetaTypePrimitive(final String javaType) {
		if (PRIMITIVE_TYPE.contains(javaType)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks a String representation of a javaType to be generic.
	 * @param javaType
	 * @return true e.g. for List<String>, but false for String
	 */
	public static boolean isJavaMetaTypeGeneric(final String javaType) {
		if (javaType.contains("<") && javaType.endsWith(">")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines the type of a collection.
	 * @param javaType
	 * @return true e.g. List for List<String>
	 */
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
	
	/**
	 * Determines the type of the elements of a collection.
	 * @param javaType
	 * @return true e.g. String for List<String>
	 */
	public static String getCollectionElementType(final String javaType) {
		final int pos1 = javaType.indexOf("<");
		final int pos2 = javaType.lastIndexOf(">");
		if (pos1 > -1 && pos2 > pos1 ) {
			return javaType.substring(pos1+1, pos2);
		}
		return "Error in method getCollectionElementType with argument '" + javaType + "'";
	}

	/**
	 * Checks a String representation of a javaType to be an array.
	 * @param javaType
	 * @return true e.g. for String[]
	 */
	public static boolean isJavaMetaTypeArray(final String javaType) {
		if (javaType.contains("[") && javaType.endsWith("]")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks a String representation of a javaType to be a collection.
	 * @param javaType
	 * @return true e.g. for List<String>
	 */
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
	 * Checks by reflection whether a clazz has the interface interfaceToSearch. 
	 * @param clazz
	 * @param interfaceToSearch 
	 * @return returns true if clazz is of type interfaceSimpleNameToSearch
	 */
	public static boolean hasInterface(final Class<?> clazz, final String interfaceToSearch) {
		final Class<?>[] interfaces = clazz.getInterfaces();
		for (final Class<?> interf : interfaces) {			
			if (interf.getName().equals(interfaceToSearch)) {
				return true;
			}
			final boolean hasInterface = hasInterface(interf, interfaceToSearch);
			if (hasInterface) return true;
		}
		return false;
	}

	/**
	 * A method called from a template file to intentionally throw an exception of a defined error message.
	 * @param errorMessage
	 * @throws MOGLiPluginException
	 */
	public static void throwMOGLiCCException(final String errorMessage) throws MOGLiPluginException {
		throw new MOGLiPluginException(errorMessage);
	}

}