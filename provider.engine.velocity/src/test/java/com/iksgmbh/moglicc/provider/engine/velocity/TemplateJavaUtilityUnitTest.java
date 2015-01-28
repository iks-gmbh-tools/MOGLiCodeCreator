package com.iksgmbh.moglicc.provider.engine.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;
import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.test.model.AttributeDescriptorDummy;
import com.iksgmbh.moglicc.test.model.ClassDescriptorDummy;
import com.iksgmbh.moglicc.test.model.MetaInfoDummy;

public class TemplateJavaUtilityUnitTest extends VelocityEngineProviderTestParent {

	@Test
	public void findsJavaLangTypes() {
		// prepare test
		final MetaInfo metaInfo = new MetaInfoDummy(MetaInfo.HierarchyLevel.Class, "Integer", "extends");
		final AttributeDescriptor attributeDescriptor = new AttributeDescriptorDummy("Adresse", "Double");
		final ClassDescriptor classDescriptor = new ClassDescriptorDummy("Person", attributeDescriptor, metaInfo);
		
		// call functionality under test
		final List<String> searchForClassNames = TemplateJavaUtility.searchForImportClasses(classDescriptor);
		
		// verify test result
		assertEquals("number of class names", 2, searchForClassNames.size());
		assertTrue(searchForClassNames.contains("java.lang.Integer"));
		assertTrue(searchForClassNames.contains("java.lang.Double"));
	}
	
	@Test
	public void findsBigDecimal() {
		// prepare test
		final MetaInfo metaInfo = new MetaInfoDummy(MetaInfo.HierarchyLevel.Class, "java.lang.Object", "extends");
		final AttributeDescriptor attributeDescriptor = new AttributeDescriptorDummy("Adresse", "java.math.BigDecimal");
		final ClassDescriptor classDescriptor = new ClassDescriptorDummy("Person", attributeDescriptor, metaInfo);
		
		// call functionality under test
		final List<String> searchForClassNames = TemplateJavaUtility.searchForImportClasses(classDescriptor);
		
		// verify test result
		assertEquals("number of class names", 2, searchForClassNames.size());
		assertTrue(searchForClassNames.contains("java.lang.Object"));
		assertTrue(searchForClassNames.contains("java.math.BigDecimal"));
	}
	
	@Test
	public void findsSimpleQualifiedDomainTypeInMetaInfos() {
		// prepare test
		final MetaInfo metaInfo = new MetaInfoDummy(MetaInfo.HierarchyLevel.Class, "java.lang.Object", "extends");
		final AttributeDescriptor attributeDescriptor = new AttributeDescriptorDummy("Adresse", "Address");
		final ClassDescriptor classDescriptor = new ClassDescriptorDummy("Person", attributeDescriptor, metaInfo);
		new ClassNameData("de.test.Address");
		
		// call functionality under test
		final List<String> searchForClassNames = TemplateJavaUtility.searchForImportClasses(classDescriptor);
		
		// verify test result
		assertEquals("number of class names", 2, searchForClassNames.size());
		assertTrue(searchForClassNames.contains("java.lang.Object"));
		assertTrue(searchForClassNames.contains("de.test.Address"));
	}
	
	@Test
	public void findsFullyQualifiedDomainTypeInMetaInfos() {
		// prepare test
		final MetaInfo metaInfo = new MetaInfoDummy(MetaInfo.HierarchyLevel.Class, "java.lang.Object", "extends");
		final AttributeDescriptor attributeDescriptor = new AttributeDescriptorDummy("Adresse", "de.test.Address");
		final ClassDescriptor classDescriptor = new ClassDescriptorDummy("Person", attributeDescriptor, metaInfo);
		
		// call functionality under test
		final List<String> searchForClassNames = TemplateJavaUtility.searchForImportClasses(classDescriptor);
		
		// verify test result
		assertEquals("number of class names", 2, searchForClassNames.size());
		assertTrue(searchForClassNames.contains("java.lang.Object"));
		assertTrue(searchForClassNames.contains("de.test.Address"));
	}
	
	@Test
	public void findsDateTimeInMetaInfos() {
		// prepare test
		final MetaInfo metaInfo = new MetaInfoDummy(MetaInfo.HierarchyLevel.Class, "java.lang.Object", "extends");
		final AttributeDescriptor attributeDescriptor = new AttributeDescriptorDummy("StartDate", "org.joda.time.DateTime");
		final ClassDescriptor classDescriptor = new ClassDescriptorDummy("ClassName", attributeDescriptor, metaInfo);
		
		// call functionality under test
		final List<String> searchForClassNames = TemplateJavaUtility.searchForImportClasses(classDescriptor);
		
		// verify test result
		assertEquals("number of class names", 2, searchForClassNames.size());
		assertTrue(searchForClassNames.contains("org.joda.time.DateTime"));
	}
	
	@Test
	public void findsArraysInMetaInfos() {
		// prepare test
		final MetaInfo metaInfo = new MetaInfoDummy(MetaInfo.HierarchyLevel.Class, "java.lang.Object", "extends");
		final AttributeDescriptor attributeDescriptor = new AttributeDescriptorDummy("NamingList", "de.test.NamingList[]");
		final ClassDescriptor classDescriptor = new ClassDescriptorDummy("ClassName", attributeDescriptor, metaInfo);
		
		// call functionality under test
		final List<String> searchForClassNames = TemplateJavaUtility.searchForImportClasses(classDescriptor);
		
		// verify test result
		assertEquals("number of class names", 3, searchForClassNames.size());
		assertTrue(searchForClassNames.contains("java.util.Arrays"));
		assertTrue(searchForClassNames.contains("de.test.NamingList"));
	}
	
	@Test
	public void findsHashSetInMetaInfos() {
		// prepare test
		final MetaInfo metaInfo = new MetaInfoDummy(MetaInfo.HierarchyLevel.Class, "java.lang.Object", "extends");
		final AttributeDescriptor attributeDescriptor = new AttributeDescriptorDummy("NamingList", "java.util.HashSet<de.test.NamingList>");
		final ClassDescriptor classDescriptor = new ClassDescriptorDummy("ClassName", attributeDescriptor, metaInfo);
		
		// call functionality under test
		final List<String> searchForClassNames = TemplateJavaUtility.searchForImportClasses(classDescriptor);
		
		// verify test result
		assertEquals("number of class names", 3, searchForClassNames.size());
		assertTrue(searchForClassNames.contains("java.util.HashSet"));
		assertTrue(searchForClassNames.contains("de.test.NamingList"));
	}
		
	@Test
	public void cutsPackageFromFullyQualifiedClassName() {
		String simpleClassName = TemplateJavaUtility.getSimpleClassName("java.util.List<?>");
		assertStringEquals("simpleClassName", "List<?>", simpleClassName);
		
		simpleClassName = TemplateJavaUtility.getSimpleClassName("String");
		assertStringEquals("simpleClassName", "String", simpleClassName);

		simpleClassName = TemplateJavaUtility.getSimpleClassName("boolean[][]");
		assertStringEquals("simpleClassName", "boolean[][]", simpleClassName);

		simpleClassName = TemplateJavaUtility.getSimpleClassName("int");
		assertStringEquals("simpleClassName", "int", simpleClassName);

	}
	
	@Test
	public void recognizesCollectionTypes() {
		assertFalse("collection type wrongly detected", TemplateJavaUtility.isJavaMetaTypeCollection("java.lang.Object"));
		assertTrue("collection type not detected", TemplateJavaUtility.isJavaMetaTypeCollection("java.util.List<?>"));
		assertTrue("collection type not detected", TemplateJavaUtility.isJavaMetaTypeCollection("List"));
		assertTrue("collection type not detected", TemplateJavaUtility.isJavaMetaTypeCollection("java.util.ArrayList<String>"));
		assertTrue("collection type not detected", TemplateJavaUtility.isJavaMetaTypeCollection("java.util.HashSet"));
	}

	@Test
	public void getsArrayElementType() {
		assertEquals("collectionMetaType", "Error: 'java.lang.Object' is no Array type!", TemplateJavaUtility.getArrayElementType("java.lang.Object"));
		assertEquals("collectionMetaType", "String", TemplateJavaUtility.getArrayElementType("String[]"));
	}

	@Test
	public void getsCollectionMetaType() {
		assertEquals("collectionMetaType", "Error: 'java.lang.Object' is no Collection type!", TemplateJavaUtility.getCollectionMetaType("java.lang.Object"));
		assertEquals("collectionMetaType", "java.util.ArrayList", TemplateJavaUtility.getCollectionMetaType("java.util.ArrayList<String>"));
	}

	@Test
	public void getsCollectionElementType() {
		assertEquals("collectionMetaType", "Error in method getCollectionElementType with argument 'java.lang.Object'", TemplateJavaUtility.getCollectionElementType("java.lang.Object"));
		assertEquals("collectionMetaType", "String", TemplateJavaUtility.getCollectionElementType("java.util.ArrayList<String>"));
	}
	
}
