package com.iksgmbh.moglicc.provider.model.standard.excel;

import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.RENAME_ATTRIBUTE_METAINFO_OCCURRENCE;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.RENAME_ATTRIBUTE_NAME_OCCURRENCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.test.ExcelStandardModelProviderTestParent;
import com.iksgmbh.utils.FileUtil;

public class ExcelStandardModelProviderStarterUnitTest extends ExcelStandardModelProviderTestParent
{	
	private ExcelStandardModelProviderStarter excelStandardModelProvider;

	@Before
	public void setup()
	{
		super.setup();
		createPropertiesFileWith("");
		excelStandardModelProvider = new ExcelStandardModelProviderStarter();
		excelStandardModelProvider.setInfrastructure(infrastructure);
		assertFileExists(new File(PROJECT_ROOT_DIR + "src/test/resources/" + TESTFILENAME));
		FileUtil.copyBinaryFile(PROJECT_ROOT_DIR + "src/test/resources/" + TESTFILENAME, 
				                excelStandardModelProvider.getInfrastructure().getPluginInputDir().getAbsolutePath());
	}

	@Test
	public void throwsExceptionForUnkownModel() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX  
					                       + "Unkown model <TestModel>. You can make it known in the '" 
			                               + ExcelStandardModelProviderStarter.PLUGIN_PROPERTIES_FILE 
			                               + "' of the ExcelStandardModelProvider.",
					e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForUndefinedExcelFile() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		createPropertiesFileWith("TestModel.sheetNumber=1");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX  
                                         + "No ExcelFilename defined in '"  
                                         + ExcelStandardModelProviderStarter.PLUGIN_PROPERTIES_FILE 
			                             + "'.", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForNonexistingExcelFile() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		createPropertiesFileWith("TestModel.filename=test.xls" + System.getProperty("line.separator") + "TestModel.sheetNumber=1"
				+ System.getProperty("line.separator") + "TestModel.sheetNumber=2");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertStringContains(e.getMessage(), "Defined ExcelFile");
			assertStringContains(e.getMessage(), "does not exist.");
		}
	}

	@Test
	public void throwsExceptionForNonexistingSheetNumber() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=4#1:1");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertStringContains(e.getMessage(), "Invalid Excel Data:");
			assertStringContains(e.getMessage(), "Please correct defined matrix <4#1:1>.");
		}
	}

	@Test
	public void throwsExceptionForNonexistingSheetName() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=nonExistent#1:1");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertStringContains(e.getMessage(), "Invalid Excel Data:");
			assertStringContains(e.getMessage(), "Please correct defined matrix <nonExistent#1:1>.");
		}
	}
	
	@Test
	public void throwsExceptionForMissingCellCoordinates() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=SecondSheet");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +  
                                          "Invalid Excel Data. Expected something like " + 
			                               ExcelStandardModelProviderStarter.MATRIX_PATTERN + ". " +
					                      "Please correct defined matrix <SecondSheet>.", e.getMessage());
		}
	}
	
	@Test
	public void throwsExceptionForMissingCellCoordinate() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=SecondSheet#1");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +  
                                          "Invalid Excel Data. Expected something like " + 
			                               ExcelStandardModelProviderStarter.MATRIX_PATTERN + ". " +
					                      "Please correct defined matrix <SecondSheet#1>.", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForInvalidCoordinates() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=SecondSheet#a:b");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +  
                                           "Invalid Excel Data. Expected something like " + 
			                               ExcelStandardModelProviderStarter.MATRIX_PATTERN + ". " +
					                      "Please correct defined matrix <SecondSheet#a:b>.", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForMissingDataMatrix() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=SecondSheet#100:100");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +  
                                          "No matrix data found for matrix <SecondSheet#100:100>", e.getMessage());
		}
	}
	
	@Test
	public void throwsExceptionForMissingDataRows() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=SecondSheet#2:6");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +  
                                          "No data row found for matrix <SecondSheet#2:6>", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForMissingDataCols() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename="  + filename + System.getProperty("line.separator")
				                 + "TestModel.matrix.test=SecondSheet#4:3");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +  
                                          "No data column found for matrix <SecondSheet#4:3>", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForDoubleAttributeName() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                "TestModel.matrix.class1=1#2:10" + System.getProperty("line.separator") );
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", "Problem with model read from ExcelStandardModelProvider: " +
					                      "Name of Attribute <Box> in class <class1> is not unique in its matrix!", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForDoubleMetaInfoName() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                "TestModel.matrix.class1=1#2:17" + System.getProperty("line.separator") );
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", "Problem with model read from ExcelStandardModelProvider: " +
					                      "Name of MetaInfo <Form> of class <class1> " +
					                      "is not unique in its matrix!", e.getMessage());
		}
	}
	
	@Test
	public void allowsDoubleMetaInfoNameIfConfigured() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                "TestModel.matrix.class1=1#2:17" + System.getProperty("line.separator")  + 
				                "TestModel" + ExcelStandardModelProviderStarter.ALLOW_METAINFO_DOUBLES_IDENTIFIER + "=truE");
		excelStandardModelProvider.doYourJob();

		// act
		final Model model = excelStandardModelProvider.getModel("", buildUpModel);
		
		// assert
		final List<String> result = model.getClassDescriptor("class1").getAttributeDescriptorList().get(0).getAllMetaInfoValuesFor("Form");
		assertEquals("result", 2, result.size());
	}
	

	//@Test
	public void buildsProviderReport() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                "TestModel.matrix.class1=1#1:1" + System.getProperty("line.separator") + 
				                "TestModel.matrix.class3=2#2:3");
		excelStandardModelProvider.doYourJob();

		// act
		excelStandardModelProvider.getModel("", buildUpModel);

		// assert
		assertEquals("model name", "TestModel", excelStandardModelProvider.getProviderReport());
	}
	
	@Test
	public void buildsModelFromExcelFile() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                "TestModel.matrix.class1=1#1:1" + System.getProperty("line.separator") + 
				                "TestModel.matrix.class3=2#2:3");
		excelStandardModelProvider.doYourJob();

		// act
		final Model model = excelStandardModelProvider.getModel("", buildUpModel);

		// assert
		assertEquals("model name", "TestModel", model.getName());
		assertEquals("number of classes", 2, model.getClassDescriptorList().size());
		
		ClassDescriptor classDescriptor = model.getClassDescriptorList().get(0);
		assertEquals("classname", "class1", classDescriptor.getSimpleName());
		assertEquals("Number of attributes in class <" + classDescriptor.getSimpleName() + ">", 
				     3, classDescriptor.getAttributeDescriptorList().size());
		assertEquals("Name of attribute 1 in class <" + classDescriptor.getSimpleName() + ">", 
				     "Box", classDescriptor.getAttributeDescriptorList().get(0).getName());
		assertEquals("Number of metainfos in the attributes in class <" + classDescriptor.getSimpleName() + ">", 
			     4, classDescriptor.getAttributeDescriptorList().get(0).getAllMetaInfos().size());
		assertEquals("Name of metainfo 1 in attribute 1 in class <" + classDescriptor.getSimpleName() + ">", 
			         "Length", classDescriptor.getAttributeDescriptorList().get(0).getAllMetaInfos().get(0).getName());
		assertEquals("Value of metainfo 1 in attribute 1 in class <" + classDescriptor.getSimpleName() + ">", 
		             "1", classDescriptor.getAttributeDescriptorList().get(0).getAllMetaInfos().get(0).getValue());

		assertEquals("Value of last metainfo in last attribute in class <" + classDescriptor.getSimpleName() + ">", 
	             "Last cell of matrix 1", classDescriptor.getAttributeDescriptorList().get(2).getAllMetaInfos().get(3).getValue());
	}
	
	@Test
	public void buildsModelFromExcelFileAndRenamesMetaInfo() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                 "TestModel.matrix.class1=1#2:17" + System.getProperty("line.separator") +
				                 "TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Form.1=InputForm"+ System.getProperty("line.separator") +
    							 "TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Form.2=OutputForm");
		excelStandardModelProvider.doYourJob();
		
		// act
		final Model model = excelStandardModelProvider.getModel("", buildUpModel);
		
		// assert
		assertEquals("attribute metainfo", "InputForm", model.getClassDescriptorList().get(0)
				                                             .getAttributeDescriptorList().get(0)
				                                             .getAllMetaInfos().get(1).getName());
		assertEquals("attribute metainfo", "OutputForm", model.getClassDescriptorList().get(0)
											                 .getAttributeDescriptorList().get(0)
												             .getAllMetaInfos().get(2).getName());		
	}
	
	@Test
	public void buildsModelFromExcelFileAndRenamesAttributes() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                 "TestModel.matrix.class1=1#2:17" + System.getProperty("line.separator") +
				                 "TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Form.1=InputForm"+ System.getProperty("line.separator") +
    							 "TestModel" + RENAME_ATTRIBUTE_NAME_OCCURRENCE + "Box.1=BoxRenamed");
		excelStandardModelProvider.doYourJob();
		
		// act
		final Model model = excelStandardModelProvider.getModel("", buildUpModel);
		
		// assert
		assertEquals("attribute name", "BoxRenamed", model.getClassDescriptorList().get(0)
				                                             .getAttributeDescriptorList().get(0)
				                                             .getName());	
	}

	@Test
	public void throwsExceptionForInvalidRenamingSetting_missingOccurrence() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                 "TestModel.matrix.class1=1#2:17" + System.getProperty("line.separator") +
    							 "TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Form2=OutputForm");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +   
                         "Invalid renaming setting:" + System.getProperty("line.separator") + 
					     "Expected something like '<modelName>" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "<oldname>.<numberOfOccurrence>=<newName>'" + 
					     System.getProperty("line.separator") + 
					     "Actual: TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Form2=OutputForm", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForInvalidRenamingSetting_OccurrenceNoInteger() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                 "TestModel.matrix.class1=1#2:17" + System.getProperty("line.separator") +
    							 "TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Form.2a=OutputForm");
		excelStandardModelProvider.doYourJob();

		// act
		try {
			excelStandardModelProvider.getModel("", buildUpModel);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) {
			// assert
			assertEquals("Error message", ExcelStandardModelProviderStarter.ERROR_PREFIX +  
                         "Invalid renaming setting:" + System.getProperty("line.separator") + 
					     "TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Form.2a=OutputForm" + 
					     System.getProperty("line.separator") + 
					     "Number of occurrence is no Integer value.", e.getMessage());
		}
	}
	
	@Test
	public void buildsModelFromExcelFileAndRenamesAttributesAndMetaInfosWithCharsToIgnore() throws Exception
	{
		// arrange
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		final String filename = TESTFILENAME;
		createPropertiesFileWith("TestModel.filename=" + filename + System.getProperty("line.separator") +  
				                 "TestModel.matrix.class1=3#1:1" + System.getProperty("line.separator") +
				                 "TestModel" + RENAME_ATTRIBUTE_METAINFO_OCCURRENCE + "Abbr1.2=Abbr. 2"+ System.getProperty("line.separator") +
    							 "TestModel" + RENAME_ATTRIBUTE_NAME_OCCURRENCE + "dataset1.2=dataset 2.");
		excelStandardModelProvider.doYourJob();
		
		// act
		final Model model = excelStandardModelProvider.getModel("", buildUpModel);
		
		// assert
		assertEquals("attribute name", "dataset 2.", model.getClassDescriptorList().get(0)
				                                             .getAttributeDescriptorList().get(1)
				                                             .getName());	
		assertEquals("metainfo name", "Abbr. 2", model.getClassDescriptorList().get(0)
                                                      .getAttributeDescriptorList().get(1)
                                                      .getMetaInfosWithNameStartingWith("Abbr. 2").get(0).getName());	
	}
	
	@Test
	public void replacesVariablePlaceholderInPropertyKeys() throws Exception
	{
		// arrange
		Properties pluginProperties = excelStandardModelProvider.readPluginProperties();
		pluginProperties.put("<<toBeReplacedKey>>", "value");
		final HashMap<String, String> variables = new HashMap<String, String>();
		variables.put("<<toBeReplacedKey>>", "replacedKey");
		
		// act
		final Properties replacedProperties = excelStandardModelProvider.doVariableReplacements(variables);
		
		// assert
		assertEquals("value of replaced property key", "value", replacedProperties.get("replacedKey"));
		
	}
	
}
