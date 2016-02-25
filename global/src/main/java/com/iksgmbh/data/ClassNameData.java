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
package com.iksgmbh.data;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.iksgmbh.utils.StringUtil;

public class ClassNameData {

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
	
	private static Map<String, String> KNOWN_CLASSES = new HashMap<String, String>();
	static { // these classes can be instantiated by their simple name
		KNOWN_CLASSES.put("BigDecimal", "java.math.BigDecimal");
		KNOWN_CLASSES.put("DateTime", "org.joda.time.DateTime");
		KNOWN_CLASSES.put("List", "java.util.List");
		KNOWN_CLASSES.put("HashSet", "java.util.HashSet");
		KNOWN_CLASSES.put("String", "java.lang.String");
		KNOWN_CLASSES.put("Boolean", "java.lang.Boolean");
		KNOWN_CLASSES.put("Character", "java.lang.Character");
		KNOWN_CLASSES.put("Byte", "java.lang.Byte");
		KNOWN_CLASSES.put("Integer", "java.lang.Integer");
		KNOWN_CLASSES.put("Long", "java.lang.Long");
		KNOWN_CLASSES.put("Float", "java.lang.Float");
		KNOWN_CLASSES.put("Double", "java.lang.Double");
	}
	
	private String simpleClassName;
	private String packageName;
	private String fullyQualifiedClassname;
	
	
	public ClassNameData(String classname) {
		classname = classname.trim();
		classname = makeNameFullyQualifiedNameIfsimple(classname);
		
		if (! isFullyQualifiedClassnameValid(classname)) {
			packageName = "";
			simpleClassName = classname;
			fullyQualifiedClassname = classname;
			KNOWN_CLASSES.put(simpleClassName, fullyQualifiedClassname);  // dynamically growing knowledge
		}
		else
		{
			final int pos = classname.lastIndexOf('.');
			packageName = classname.substring(0, pos);
			simpleClassName = classname.substring(pos + 1);
			fullyQualifiedClassname = classname;
			KNOWN_CLASSES.put(simpleClassName, fullyQualifiedClassname);  // dynamically growing knowledge
		}
	}

	public String getSimpleClassName() {
		return simpleClassName;
	}

	public String getFullyQualifiedClassname() {
		return fullyQualifiedClassname;
	}

	public String getPackageName() {
		return packageName;
	}

	
	@Override
	public String toString() {
		return getFullyQualifiedClassname();
	}
	

	/**
	 * searchs for FullyQualifiedClassname in the list of KNOWN_CLASSES
	 * @param simpleName
	 * @return fullyQualifiedClassname
	 */
	public static String makeNameFullyQualifiedNameIfsimple(String simpleName) {
		final String fullyQualifiedClassname = KNOWN_CLASSES.get(simpleName);
		if (fullyQualifiedClassname == null) {
			return simpleName;
		}
		return fullyQualifiedClassname;
	}

	public String getSubdirPackageHierarchy() {
		return getPackageName().replace('.', '/');
	}	

	
	/**
	 * Checks whether simple classname is a known class.
	 * Used to detect domain types.
	 * @param classname
	 * @return false for unknown unqualified classes AND primitive types
	 */
	public static boolean isSimpleClassNameKnown(final String simpleClassname) {
		if (PRIMITIVE_TYPE.contains(simpleClassname))
		{
			return false;
		}
		
		return KNOWN_CLASSES.get(simpleClassname) != null;
	}
	
	/**
	 * Checks whether classname is valid in Java
	 * @param classname
	 * @return true or false
	 */
	public static boolean isFullyQualifiedClassnameValid(final String classname) {
	      if (classname == null) return false;
	      if (classname.trim().length() == 0) return false;
	      if (classname.endsWith(".")) return false;
	      if (classname.indexOf(".") == -1) return false;
	      
	      final String[] parts = classname.split("[\\.]");
	      if (parts.length == 0) return false;
	      String packageName = "";
	      for (int i = 0; i < parts.length; i++) {
	          
	    	  final CharacterIterator iter = new StringCharacterIterator(parts[i]);
	          // Check first character (there should at least be one character for each part) ...
	          char c = iter.first();
	          if (c == CharacterIterator.DONE) return false;
	          if (!Character.isJavaIdentifierStart(c) && !Character.isIdentifierIgnorable(c)) return false;
	          c = iter.next();
	          // Check the remaining characters, if there are any ...
	          while (c != CharacterIterator.DONE) {
	              if (!Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c)) return false;
	              c = iter.next();
	          }
	          
	          if (i < parts.length - 1) {
	    	      if (StringUtil.startsWithUpperCase(parts[i])) {
	    	    	  return false;
	    	      }
	    	      packageName += "." + parts[i];
	          }
	      }
	      
	      packageName = packageName.substring(1);
	      final String simpleClassName = parts[parts.length-1];
	      if (StringUtil.startsWithLowerCase(simpleClassName)) {
	    	  return false;
	      }
	      
	      return true;
	  }

}