package com.iksgmbh.moglicc.provider.model.standard.parser;

import static com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants.ATTRIBUTE_IDENTIFIER;
import static com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants.CLASS_IDENTIFIER;
import static com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants.META_INFO_IDENTIFIER;
import static com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants.MODEL_IDENTIFIER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpAttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.exceptions.ModelParserException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;
import com.iksgmbh.moglicc.test.StandardModelProviderTestParent;

public class ModelParserUnitTest extends StandardModelProviderTestParent {
	
	public static final String TEST_MODEL_NAME = "TestModel A";
	
	private ModelParser modelParser;
	
	@Before
	public void setup() {
		modelParser = new ModelParser();
	}
	
	private List<String> getFileContentForTestModelWithTwoClasses(final String... lines) {
		return getFileContent(
				"# test comment", 
				MODEL_IDENTIFIER + " " + TEST_MODEL_NAME, 
                "class de.test.Person", 	
                "metainfo extends java.lang.Object", 
                "attribute FirstName",
                "metainfo JavaType int",
                "attribute LastName",
                "metainfo JavaType String",
                "# test comment",
                "class de.test.Address", 	
                "attribute Street",
                "metainfo JavaType String",
                "# test comment");
	}
	
	@Test
	public void throwsExceptionIfModelIdentificationNotFound() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MetaModelConstants.CLASS_IDENTIFIER + " de.Person");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.MODEL_IDENTIFIER_NOT_FOUND);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForMissingModelName() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER);
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.MISSING_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionIfTwoModelIdentificationsFound() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " Test", 
				                                              "class de.Test", 
				                                              MODEL_IDENTIFIER + " Test");
		fileContentAsList.add("Model Test");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.DUPLICATE_MODEL_IDENTIFIER);
			return;
		}
		fail("Expected exception not thrown!");
	}

	
	@Test
	public void throwsExceptionForMissingClassName() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " Test", 
				                                              CLASS_IDENTIFIER,
				                                              CLASS_IDENTIFIER + " de.Test");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.MISSING_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForClassNameWithAdditionalInfo() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " Test", 
				                                              CLASS_IDENTIFIER + " de.Test additional Information");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.INVALID_INFORMATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForAttributeNameWithAdditionalInfo() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " Test", 
				                                              CLASS_IDENTIFIER + " de.Test",
        													  ATTRIBUTE_IDENTIFIER + " ID additional Information");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.INVALID_INFORMATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForUnrelatedMetaInfo() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(META_INFO_IDENTIFIER + " MetaInfoName value  ");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.UNRELATED_METAINFO);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForMetaInfoWithoutName() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
															  META_INFO_IDENTIFIER);
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.MISSING_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForTwoIdenticalClassNames() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
				                                              CLASS_IDENTIFIER + " de.test.ClassName",
				                                              CLASS_IDENTIFIER + " de.test.ClassName");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.DUPLICATE_CLASS_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForTwoIdenticalAttributeNames() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
				                                              CLASS_IDENTIFIER + " de.test.ClassName",
				                                              ATTRIBUTE_IDENTIFIER + " AttributTestName",
				                                              ATTRIBUTE_IDENTIFIER + " AttributTestName");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.DUPLICATE_ATTRIBUTE_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForMetaInfoWithoutValue() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
															  META_INFO_IDENTIFIER + " MetaInfoName");
		
		// call functionality under test
		try {
			modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			assertStringContains(e.getParserErrors(), TextConstants.MISSING_VALUE);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void parsesMetaInfoNotFound() throws ModelParserException {
		// prepare test
		final String metaInfoName = "MetaInfoNameTest";
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
				CLASS_IDENTIFIER + " de.test.ClassNameTest1");
		
		// call functionality under test
		final BuildUpModel buildUpModel = (BuildUpModel) modelParser.parse(fileContentAsList);
		
		// verify test result
		assertStringEquals("metaInfoValue", MetaInfoSupport.META_INFO_NOT_FOUND.replaceFirst("#", metaInfoName), buildUpModel.getMetaInfoValueFor(metaInfoName));
	}	

	@Test
	public void parsesMetaInfoPluginListForModelSpaceSepatated() throws ModelParserException {
		// prepare test
		final String metaInfoName = "MetaInfoNameTest";
		final String metaInfoValue  = "MetaInfoTestValue";
		final String metaInfoValuePlugin  = "MetaInfoTestPlugin";
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
															  META_INFO_IDENTIFIER + " "  + metaInfoName 
															  + " " + metaInfoValue 
															  + " additional Information " 
															  + metaInfoValuePlugin, 
															  CLASS_IDENTIFIER + " de.test.ClassNameTest");
		
		// call functionality under test
		final BuildUpModel buildUpModel = (BuildUpModel) modelParser.parse(fileContentAsList);
		
		// verify test result
		assertStringEquals("metaInfoValue", metaInfoValue, buildUpModel.getMetaInfoValueFor(metaInfoName));
		final BuildUpMetaInfo buildUpMetaInfo = (BuildUpMetaInfo) buildUpModel.getMetaInfoList().get(0);
		assertEquals("Hierarchy Level", MetaInfo.HierarchyLevel.Model, buildUpMetaInfo.getHierarchyLevel());

	}	
	
	@Test
	public void parsesMetaInfoPluginListWithOneElementForClass() throws ModelParserException {
		// prepare test
		final String metaInfoName = "MetaInfoNameTest";
		final String metaInfoValue  = "MetaInfoTestValue";
		final String metaInfoValuePlugin  = "MetaInfoTestPlugin";
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
				                                              CLASS_IDENTIFIER + " de.test.ClassNameTest1",
															  META_INFO_IDENTIFIER + " " + metaInfoName 
															  + " " + metaInfoValue + " "  
															  + metaInfoValuePlugin, 
															  CLASS_IDENTIFIER + " de.test.ClassNameTest2");
		
		// call functionality under test
		final BuildUpModel buildUpModel = (BuildUpModel) modelParser.parse(fileContentAsList);
		
		// verify test result
		final BuildUpClassDescriptor buildUpClassDescriptor = (BuildUpClassDescriptor) buildUpModel.getClassDescriptorList().get(0);
		final BuildUpMetaInfo buildUpMetaInfo = (BuildUpMetaInfo) buildUpClassDescriptor.getMetaInfoList().get(0);
		assertEquals("Hierarchy Level", MetaInfo.HierarchyLevel.Class, buildUpMetaInfo.getHierarchyLevel());
	}	
	
	@Test
	public void parsesMetaInfoPluginListForAttributeCommaSepatated() throws ModelParserException {
		// prepare test
		final String metaInfoName = "MetaInfoNameTest";
		final String metaInfoValue  = "MetaInfoTestValue";
		final String metaInfoValuePlugin  = "MetaInfoTestPlugin";
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
				                                              CLASS_IDENTIFIER + " de.test.ClassNameTest1",
				                                              ATTRIBUTE_IDENTIFIER + " attribute",
															  META_INFO_IDENTIFIER + " " + metaInfoName 
															  + " " + metaInfoValue + " "  
															  + metaInfoValuePlugin + ", TestPluginId", 
															  CLASS_IDENTIFIER + " de.test.ClassNameTest2");
		
		// call functionality under test
		final BuildUpModel buildUpModel = (BuildUpModel) modelParser.parse(fileContentAsList);
		
		// verify test result
		final BuildUpAttributeDescriptor buildUpAttributeDescriptor = (BuildUpAttributeDescriptor) buildUpModel
		                                       .getClassDescriptorList().get(0).getAttributeDescriptorList().get(0);
		BuildUpMetaInfo buildUpMetaInfo = (BuildUpMetaInfo) buildUpAttributeDescriptor.getMetaInfoList().get(0);
		assertEquals("Hierarchy Level", MetaInfo.HierarchyLevel.Attribute, buildUpMetaInfo.getHierarchyLevel());
	}	
	
	@Test
	public void parsesTwoMetaInfoElementsWithIdenticalNamesWithoutPlugins() throws ModelParserException {
		// prepare test
		final String metaInfoName = "MetaInfoNameTest";
		final String metaInfoValue1  = "MetaInfoTestValue1";
		final String metaInfoValue2  = "MetaInfoTestValue2";
		final List<String> fileContentAsList = getFileContent(MODEL_IDENTIFIER + " ModelName",
				                                              CLASS_IDENTIFIER + " de.test.ClassNameTest1",
				                                              ATTRIBUTE_IDENTIFIER + " attribute",
															  META_INFO_IDENTIFIER + " " + metaInfoName 
															  + " " + metaInfoValue1,  
															  META_INFO_IDENTIFIER + " " + metaInfoName 
															  + " " + metaInfoValue2);
		
		// call functionality under test
		final BuildUpModel buildUpModel = (BuildUpModel) modelParser.parse(fileContentAsList);
		
		// verify test result
		final BuildUpAttributeDescriptor buildUpAttributeDescriptor = (BuildUpAttributeDescriptor) buildUpModel
		                                       .getClassDescriptorList().get(0).getAttributeDescriptorList().get(0);
		final List<String> values = buildUpAttributeDescriptor.getAllMetaInfoValuesFor(metaInfoName);
		assertEquals("MetaInfo number", 2,  values.size());
		assertStringEquals("first MetaInfo value", metaInfoValue1, values.get(0));
		assertStringEquals("second MetaInfo value", metaInfoValue2, values.get(1));
	}	
	
	@Test
	public void parseModelWithTwoClassAndCommentLines() {
		// prepare test
		final List<String> fileContentAsList = getFileContentForTestModelWithTwoClasses();
		
		// call functionality under test
		Model model = null;
		
		try {
			model = modelParser.parse(fileContentAsList);
		} catch (ModelParserException e) {
			fail(e.getParserErrors());
		}
		
		// verify test result
		assertNotNull(model);
		assertEquals("model name", TEST_MODEL_NAME, model.getName());
		assertEquals("Number of class descriptions in model file", 2, model.getSize());
		assertEquals("class name", "Person", model.getClassDescriptorList().get(0).getSimpleName());
		assertEquals("Value for MetaInfo 'extends'", "java.lang.Object", model.getClassDescriptorList().get(0).getMetaInfoValueFor("extends"));
		assertEquals("Attribute number", 2, model.getClassDescriptorList().get(0).getAttributeDescriptorList().size());
		assertEquals("Attribute name", "FirstName", model.getClassDescriptorList().get(0).getAttributeDescriptorList().get(0).getName());
		assertEquals("Attribute name", "LastName", model.getClassDescriptorList().get(0).getAttributeDescriptorList().get(1).getName());
		assertEquals("class name", "Address", model.getClassDescriptorList().get(1).getSimpleName());
		assertEquals("Attribute number", 1, model.getClassDescriptorList().get(1).getAttributeDescriptorList().size());
		assertEquals("Attribute name", "Street", model.getClassDescriptorList().get(1).getAttributeDescriptorList().get(0).getName());
	}
	
	@Test
	public void returnsClassDescriptorForClassName() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContentForTestModelWithTwoClasses();
		final Model model = modelParser.parse(fileContentAsList);
		
		// call functionality under test
		final ClassDescriptor classDescriptor1 = model.getClassDescriptor("unkown class");
		final ClassDescriptor classDescriptor2 = model.getClassDescriptor("de.test.Person");
		final ClassDescriptor classDescriptor3 = model.getClassDescriptor("Person");
		
		// verify test result
		assertNull(classDescriptor1);
		assertNotNull(classDescriptor2);
		assertNotNull(classDescriptor3);
	}
	
	@Test
	public void returnsAttributeForAttributeName() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContentForTestModelWithTwoClasses();
		final Model model = modelParser.parse(fileContentAsList);
		final ClassDescriptor classDescriptor = model.getClassDescriptor("de.test.Person");	
		
		// call functionality under test	
		AttributeDescriptor attributeDescriptor = classDescriptor.getAttributeDescriptor("FirstName");
		
		// verify test result
		assertNotNull(attributeDescriptor);
		assertEquals("Attribute name", "FirstName", attributeDescriptor.getName());
		assertEquals("Metainfo", "int", attributeDescriptor.getMetaInfoValueFor("JavaType"));
	}	

	@Test
	public void usesVariablesThatAreDefinedAtTheEndOfTheModelFile() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = new ArrayList<String>();
		fileContentAsList.add("model test");
		fileContentAsList.add("metainfo targetdir <<dir>>");
		fileContentAsList.add("class aName");
		fileContentAsList.add("variable dir directory");
		
		// call functionality under test	
		final Model model = modelParser.parse(fileContentAsList);
		
		// verify test result
		assertNotNull(model);
		assertEquals("Metainfo", "directory", model.getMetaInfoValueFor("targetdir"));
	}	

	@Test
	public void handlesBracesProblemForMoreThanOneVariableReplacementsWithBracesPerLine() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = new ArrayList<String>();
		fileContentAsList.add("model test");
		fileContentAsList.add("metainfo displaytext \" <<text1>><<text2>> kurzer <<text3>> \"");
		fileContentAsList.add("class aName");
		fileContentAsList.add("variable text1 \" Das \"");
		fileContentAsList.add("variable text3 \" ein Text. \"");
		fileContentAsList.add("variable text2 \" ist \"");
		
		// call functionality under test	
		final Model model = modelParser.parse(fileContentAsList);
		
		// verify test result
		assertNotNull(model);
		assertEquals("Metainfo", "  Das  ist  kurzer  ein Text.  ", model.getMetaInfoValueFor("displaytext"));
	}	
	
}