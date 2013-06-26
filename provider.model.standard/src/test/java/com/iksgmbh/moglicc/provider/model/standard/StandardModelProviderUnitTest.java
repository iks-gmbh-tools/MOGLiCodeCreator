package com.iksgmbh.moglicc.provider.model.standard;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_OUTPUT_FILES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.MandatoryMetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.OptionalMetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.test.StandardModelProviderTestParent;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter2;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class StandardModelProviderUnitTest extends StandardModelProviderTestParent {

	final private StandardModelProviderStarter modelProvider = new StandardModelProviderStarter();

	@Before
	public void setup() {
		super.setup();
		modelProvider.setMOGLiInfrastructure(infrastructure);
		infrastructure.getPluginLogFile().delete();
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());
		infrastructure.getPluginInputDir().mkdirs();
		final File propertiesFile = new File(infrastructure.getPluginInputDir(), StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE );
		try {
			FileUtil.createNewFileWithContent(propertiesFile, "");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void setModelFile(String filename) {
		final String source = getProjectTestResourcesDir() + "modelFiles/" + filename;
		FileUtil.copyBinaryFile(source, modelTextfile.getAbsolutePath());

		infrastructure = new MOGLiInfrastructure(
				createInfrastructureInitData(null, getPluginListForValidatorTest(), StandardModelProviderStarter.PLUGIN_ID));
		modelProvider.setMOGLiInfrastructure(infrastructure);
	}

	// **************************  Test Methods  *********************************

	@Test
	public void handlesMissingModelFile() throws MOGLiPluginException {
		try {
			// call functionality under test
			modelProvider.buildModel();
			fail("Expected exception not thrown!");
		} catch (MOGLiPluginException e) {
			// verify test result
			assertStringContains(e.getMessage(), TextConstants.TEXT_NO_MODELFILE_FOUND);
		}
	}

	@Test
	public void handlesEmptyModelFile() throws MOGLiPluginException{
		// prepare test
		setModelFile("emptyModelFile.txt");

		try {
			// call functionality under test
			modelProvider.buildModel();
			fail("Expected exception not thrown!");
		} catch (MOGLiPluginException e) {
			// verify test result
			assertStringContains(e.getMessage(), "Unexpected empty file");
		}
	}

	@Test
	public void buildsModel() throws MOGLiPluginException{
		// prepare test
		setModelFile("simpelModelFile.txt");
		modelProvider.readPluginProperties();

		// call functionality under test
		Model model = modelProvider.buildModel();

		// verify test result
		assertNotNull(model);
		assertEquals("Unexpected number of classes!", 2, model.getClassDescriptorList().size());
		assertStringEquals("Unexpected class name!", "TestklasseA", model.getClassDescriptorList().get(0).getSimpleName());
	}

	@Test
	public void buildsModelFromFileDefinedInPluginPropertiesFile() throws Exception{
		// prepare test
		setModelFile("MetaInfoValidatorTest_Success.txt");
		final File modelfile = new File(getProjectTestResourcesDir(), "modelFiles/simpelModelFile.txt");
		FileUtil.copyTextFile(modelfile, infrastructure.getPluginInputDir().getAbsolutePath());
		final File propertiesFile = new File(infrastructure.getPluginInputDir(), StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE );
		FileUtil.createNewFileWithContent(propertiesFile, "modelfile=simpelModelFile.txt");
		modelProvider.readPluginProperties();

		// call functionality under test
		Model model = modelProvider.buildModel();

		// verify test result
		assertNotNull(model);
		assertEquals("Model name", "SimpelTestModel", model.getName());
	}

	@Test
	public void unpacksInputDefaultData() throws MOGLiPluginException {
		// prepare test
		final File pluginInputDir = new File(applicationRootDir + "/" + DIR_INPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		FileUtil.deleteDirWithContent(pluginInputDir);
		assertFileDoesNotExist(pluginInputDir);

		// call functionality under test
		modelProvider.unpackDefaultInputData();

		// verify test result
		assertFileExists(pluginInputDir);
		final File modelFile = new File(pluginInputDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		assertFileExists(modelFile);
		assertFileContainsEntry(modelFile, "model MOGLiCC_JavaBeanModel");
	}

	@Test
	public void createsStatisticsFileWithoutUnusedElements() throws MOGLiPluginException, IOException {
		// prepare test
		final File outputDir = new File(MOGLiCodeCreator.getApplicationRootDir() + "/"
				                       + DIR_OUTPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		FileUtil.deleteDirWithContent(outputDir);
		assertFileDoesNotExist(outputDir);
		setModelFile("MetaInfoForStatisticsFileTestWithoutUnusedElements.txt");
		infrastructure = new MOGLiInfrastructure(
				createInfrastructureInitData(null, getPluginListForStatisticsFileTest(), StandardModelProviderStarter.PLUGIN_ID));
		modelProvider.setMOGLiInfrastructure(infrastructure);

		// call functionality under test
		modelProvider.doYourJob();

		// verify test result
		final File statisticsFile = new File(outputDir, StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedStatisticsFile.txt");
		assertFileExists(statisticsFile);
		assertFileEquals(expectedFile, statisticsFile);
	}

	@Test
	public void createsStatisticsFileWithUnusedMetaInfoAndValidatorElements() throws MOGLiPluginException, IOException {
		// prepare test
		final File outputDir = new File(MOGLiCodeCreator.getApplicationRootDir() + "/"
				                       + DIR_OUTPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		FileUtil.deleteDirWithContent(outputDir);
		assertFileDoesNotExist(outputDir);
		modelProvider.unpackDefaultInputData();
		setModelFile("MetaInfoForStatisticsFileTestWithUnusedElements.txt");
		final List<MOGLiPlugin> pluginList = getPluginListForStatisticsFileTest();
		pluginList.add(createGeneratorWithUnusedMetaInfoValidators());
		infrastructure = new MOGLiInfrastructure(
				createInfrastructureInitData(null, pluginList, StandardModelProviderStarter.PLUGIN_ID));
		modelProvider.setMOGLiInfrastructure(infrastructure);

		// call functionality under test
		modelProvider.doYourJob();

		// verify test result
		final File statisticsFile = new File(outputDir, StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedStatisticsFileWithUnusedElements.txt");
		assertFileExists(statisticsFile);
		assertFileEquals(expectedFile, statisticsFile);
	}


	private MOGLiPlugin createGeneratorWithUnusedMetaInfoValidators() {
		final DummyGeneratorStarter2 dummyGeneratorStarter2 = new DummyGeneratorStarter2();
		final List<MetaInfoValidator> metaInfoValidatorList = new ArrayList<MetaInfoValidator>();
		dummyGeneratorStarter2.setMetaInfoValidatorList(metaInfoValidatorList);

		OptionalMetaInfoValidator optionalMetaInfoValidator = new OptionalMetaInfoValidator("xyz", HierarchyLevel.Model);
		optionalMetaInfoValidator.setVendorPluginId("DummyGenerator3");
		metaInfoValidatorList.add(optionalMetaInfoValidator);

		optionalMetaInfoValidator = new OptionalMetaInfoValidator("xyz", HierarchyLevel.Class);
		optionalMetaInfoValidator.setVendorPluginId("DummyGenerator3");
		metaInfoValidatorList.add(optionalMetaInfoValidator);

		optionalMetaInfoValidator = new OptionalMetaInfoValidator("xyz", HierarchyLevel.Attribute);
		optionalMetaInfoValidator.setVendorPluginId("DummyGenerator3");
		metaInfoValidatorList.add(optionalMetaInfoValidator);
		return dummyGeneratorStarter2;
	}

	@Test
	public void writesWarningLogLineForAllMissingMandatoryMetaInfos() throws Exception {
		// prepare test
		setModelFile("MetaInfoValidatorTest_SixMissingMandatory_MetaInfo.txt");

		// call functionality under test
		modelProvider.doYourJob();

		// verify test result
		final String fileContent = FileUtil.getFileContent(modelProvider.getMOGLiInfrastructure().getPluginLogFile());
		assertStringContains(fileContent, "Warning: Mandatory metaInfo 'modelMetaInfoMandatory' was not found for model 'MetaInfoValidatorTestModel'");
		assertStringContains(fileContent, "Warning: Mandatory metaInfo 'classMetaInfoMandatory' was not found for class descriptor 'TestklasseA'");
		assertStringContains(fileContent, "Warning: Mandatory metaInfo 'classMetaInfoMandatory' was not found for class descriptor 'TestklasseB'");
		assertStringContains(fileContent, "Warning: Mandatory metaInfo 'attributeMetaInfoMandatory' was not found for attribute descriptor 'A1'");
		assertStringContains(fileContent, "Warning: Mandatory metaInfo 'attributeMetaInfoMandatory' was not found for attribute descriptor 'A2'");
		assertStringContains(fileContent, "Warning: Mandatory metaInfo 'attributeMetaInfoMandatory' was not found for attribute descriptor 'B1'");
	}

	@Test
	public void validatesMetaInfosSuccessfully() throws MOGLiPluginException {
		// prepare test
		setModelFile("MetaInfoValidatorTest_Success.txt");

		// call functionality under test
		modelProvider.doYourJob();
	}

	private List<MOGLiPlugin> getPluginListForValidatorTest() {
		final List<MOGLiPlugin> toReturn = new ArrayList<MOGLiPlugin>();
		final DummyGeneratorStarter dummyGeneratorStarter = new DummyGeneratorStarter();
		final List<MetaInfoValidator> metaInfoValidatorList = new ArrayList<MetaInfoValidator>();
		dummyGeneratorStarter.setMetaInfoValidatorList(metaInfoValidatorList);
		toReturn.add(dummyGeneratorStarter);

		MandatoryMetaInfoValidator metaInfoValidator = new MandatoryMetaInfoValidator("modelMetaInfoMandatory",
				                                                                      HierarchyLevel.Model);
		metaInfoValidatorList.add(metaInfoValidator);

		metaInfoValidator = new MandatoryMetaInfoValidator("classMetaInfoMandatory", HierarchyLevel.Class);
		metaInfoValidatorList.add(metaInfoValidator);

		metaInfoValidator = new MandatoryMetaInfoValidator("attributeMetaInfoMandatory", HierarchyLevel.Attribute);
		metaInfoValidatorList.add(metaInfoValidator);

		return toReturn;
	}

	private List<MOGLiPlugin> getPluginListForStatisticsFileTest() {
		final List<MOGLiPlugin> toReturn = new ArrayList<MOGLiPlugin>();
		final DummyGeneratorStarter dummyGeneratorStarter = new DummyGeneratorStarter();
		final List<MetaInfoValidator> metaInfoValidatorList = new ArrayList<MetaInfoValidator>();
		dummyGeneratorStarter.setMetaInfoValidatorList(metaInfoValidatorList);
		toReturn.add(dummyGeneratorStarter);

		OptionalMetaInfoValidator optionalMetaInfoValidator = new OptionalMetaInfoValidator("implements", HierarchyLevel.Class);
		optionalMetaInfoValidator.setVendorPluginId("DummyGenerator1");
		metaInfoValidatorList.add(optionalMetaInfoValidator);

		optionalMetaInfoValidator = new OptionalMetaInfoValidator("extends", HierarchyLevel.Class);
		optionalMetaInfoValidator.setVendorPluginId("DummyGenerator1");
		metaInfoValidatorList.add(optionalMetaInfoValidator);

		MandatoryMetaInfoValidator mandatoryMetaInfoValidator = new MandatoryMetaInfoValidator("JavaType", HierarchyLevel.Attribute);
		mandatoryMetaInfoValidator.setVendorPluginId("DummyGenerator2");
		metaInfoValidatorList.add(mandatoryMetaInfoValidator);

		return toReturn;
	}

	@Test
	public void readsModelFileNameFromPropertiesFile() throws Exception {
		// prepare test
		final File testModel1File = new File(infrastructure.getPluginInputDir(), "TestModel1.txt");
		FileUtil.createNewFileWithContent(testModel1File, "model TestModel1" + FileUtil.getSystemLineSeparator() +
                                                         "class com.iksgmbh.moglicc.demo.Person1");
		final File testModel2File = new File(infrastructure.getPluginInputDir(), "TestModel2.txt");
		FileUtil.createNewFileWithContent(testModel2File, "model TestModel2" + FileUtil.getSystemLineSeparator() +
		"class com.iksgmbh.moglicc.demo.Person2");
		final File testPropertiesFile = new File(infrastructure.getPluginInputDir(),
				                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		FileUtil.createNewFileWithContent(testPropertiesFile, "modelfile=TestModel2.txt");
		assertChildrenNumberInDirectory(infrastructure.getPluginInputDir(), 3);

		// call functionality under test
		modelProvider.doYourJob();

		// verify test result
		final Model model = modelProvider.getModel(null);
		assertStringEquals("model name", "TestModel2", model.getName());
	}

	@Test
	public void throwsExceptionIfPropertiesFileNotFound() throws Exception {
		// prepare test
		final File testPropertiesFile = new File(infrastructure.getPluginInputDir(),
                StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		FileUtil.createNewFileWithContent(testPropertiesFile, "modelfile=TestModel3.txt");

		// call functionality under test
		try {
			modelProvider.doYourJob();
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), TextConstants.TEXT_MODEL_NOT_EXISTS);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void readsUmlauteFromModelFile() throws MOGLiPluginException {
		// prepare test
		final File testPropertiesFile = new File(infrastructure.getPluginInputDir(),
                StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		testPropertiesFile.delete();
		assertFileDoesNotExist(testPropertiesFile);
		final File testModelFile = new File(infrastructure.getPluginInputDir(), "Umlauts.txt");
		MOGLiFileUtil.createNewFileWithContent(testModelFile, "ßüäöÜÄÖ");
		assertFileExists(testModelFile);
		modelProvider.setModelFile(testModelFile);

		// call functionality under test
		final String actualFileContent = modelProvider.readModelFileContent().get(0);

		// verify test result
		assertStringEquals("file content", "ßüäöÜÄÖ", actualFileContent);

	}

	@Test
	public void readsMetaInfoValueContainingDoubleQuotesUsingCustomizedBraceSymbol() throws Exception {
		// prepare test
		final String testBraceSymbol = "++";
		final String testMetaInfoName = "metainfoWithDoubleQuote";
		final String testMetaInfoValue = "double quote \"containing\" value";
		final File testModelFile = new File(infrastructure.getPluginInputDir(), "TestModel.txt");
		FileUtil.createNewFileWithContent(testModelFile, "model TestModel" + FileUtil.getSystemLineSeparator() +
                                                         "metainfo " + testMetaInfoName + " " +
                                                         testBraceSymbol + testMetaInfoValue + testBraceSymbol +
                                                         FileUtil.getSystemLineSeparator() +
                                                         "class com.iksgmbh.moglicc.demo.Person1");

		final File testPropertiesFile = new File(infrastructure.getPluginInputDir(),
				                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		FileUtil.createNewFileWithContent(testPropertiesFile, "modelfile=TestModel.txt"
				                                              + FileUtil.getSystemLineSeparator()
				                                              + TextConstants.BRACE_SYMBOL_PROPERTY
				                                              + "=" + testBraceSymbol);
		assertChildrenNumberInDirectory(infrastructure.getPluginInputDir(), 2);

		// call functionality under test
		modelProvider.doYourJob();

		// verify test result
		final Model model = modelProvider.getModel(null);
		assertStringEquals("model name", testMetaInfoValue, model.getMetaInfoValueFor(testMetaInfoName));
	}

	@Test
	public void createsLogEntryWhenCallingGetModelOnAInvalidModelFile() throws Exception {
		// prepare test
		infrastructure = new MOGLiInfrastructure(createInfrastructureInitData(
				null,
				getPluginListWithDummyGeneratorContainingMandatoryValidatorForNotExistingMetaInfo(),
				StandardModelProviderStarter.PLUGIN_ID));
		modelProvider.setMOGLiInfrastructure(infrastructure);

		final File testModelFile = new File(infrastructure.getPluginInputDir(), "TestModel.txt");
		FileUtil.createNewFileWithContent(testModelFile, "model TestModel" + FileUtil.getSystemLineSeparator() +
                                                         "class com.iksgmbh.moglicc.demo.Person1");
		final File testPropertiesFile = new File(infrastructure.getPluginInputDir(),
				                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		FileUtil.createNewFileWithContent(testPropertiesFile, "modelfile=TestModel.txt");
		assertChildrenNumberInDirectory(infrastructure.getPluginInputDir(), 2);
		final File logFile = modelProvider.getMOGLiInfrastructure().getPluginLogFile();
		final String expected = "Model breaks 1 MetaInfoValidator settings!";
		assertFileDoesNotContainEntry(logFile, expected);

		// call functionality under test
		try {
			modelProvider.doYourJob();
			modelProvider.getModel(null);
		} catch (Exception e) {
			// ignore it
		}
		
		// verify test result
		assertFileContainsEntry(logFile, expected);
	}

	private List<MOGLiPlugin> getPluginListWithDummyGeneratorContainingMandatoryValidatorForNotExistingMetaInfo() {
		final List<MOGLiPlugin> toReturn = new ArrayList<MOGLiPlugin>();
		final DummyGeneratorStarter dummyGeneratorStarter = new DummyGeneratorStarter();
		final List<MetaInfoValidator> metaInfoValidatorList = new ArrayList<MetaInfoValidator>();
		dummyGeneratorStarter.setMetaInfoValidatorList(metaInfoValidatorList);
		toReturn.add(dummyGeneratorStarter);

		final MandatoryMetaInfoValidator mandatoryMetaInfoValidator = new MandatoryMetaInfoValidator("NotExistingMetaInfo", HierarchyLevel.Model);
		mandatoryMetaInfoValidator.setVendorPluginId("DummyGenerator");
		metaInfoValidatorList.add(mandatoryMetaInfoValidator);

		return toReturn;
	}

	@Test
	public void readsModelFileWithoutAnyClassDefinition() throws Exception {
		// prepare test
		final File testModelFile = new File(infrastructure.getPluginInputDir(), "TestModel.txt");
		FileUtil.createNewFileWithContent(testModelFile, "model TestModel");
		final File testPropertiesFile = new File(infrastructure.getPluginInputDir(),
				                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		FileUtil.createNewFileWithContent(testPropertiesFile, "modelfile=TestModel.txt");

		// call functionality under test
		modelProvider.doYourJob();

		// verify test result -> no Exception 
	}

}
