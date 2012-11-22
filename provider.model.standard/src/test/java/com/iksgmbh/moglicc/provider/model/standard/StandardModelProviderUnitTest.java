package com.iksgmbh.moglicc.provider.model.standard;

import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_INPUT_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_OUTPUT_FILES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MogliCodeCreator;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.infrastructure.MogliInfrastructure;
import com.iksgmbh.moglicc.plugin.PluginExecutable;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MandatoryMetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.OptionalMetaInfoValidator;
import com.iksgmbh.moglicc.test.StandardModelProviderTestParent;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter2;
import com.iksgmbh.utils.FileUtil;

public class StandardModelProviderUnitTest extends StandardModelProviderTestParent {
	
	final private StandardModelProviderStarter modelProvider = new StandardModelProviderStarter();
	
	@Before
	public void setup() {
		super.setup();
		modelProvider.setMogliInfrastructure(infrastructure);
		infrastructure.getPluginLogFile().delete();
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());
		infrastructure.getPluginInputDir().mkdirs();
	}
	
	protected void setModelFile(String filename) {
		final String source = getProjectTestResourcesDir() + "modelFiles/" + filename;
		FileUtil.copyBinaryFile(source, modelTextfile.getAbsolutePath());
		
		infrastructure = new MogliInfrastructure(
				createInfrastructureInitData(null, getPluginListForValidatorTest(), StandardModelProviderStarter.PLUGIN_ID));
		modelProvider.setMogliInfrastructure(infrastructure);
	}
	
	// **************************  Test Methods  *********************************
	
	@Test
	public void handlesMissingModelFile() throws MogliPluginException{

		try {
			// call functionality under test
			modelProvider.buildModel();
			fail("Expected exception not thrown!");
		} catch (MogliPluginException e) {
			// verify test result
			assertStringContains(e.getMessage(), TextConstants.TEXT_NO_MODELFILE_FOUND);
		}		
	}
	
	@Test
	public void handlesEmptyModelFile() throws MogliPluginException{
		// prepare test
		setModelFile("emptyModelFile.txt");
		
		try {
			// call functionality under test
			modelProvider.buildModel();
			fail("Expected exception not thrown!");
		} catch (MogliPluginException e) {
			// verify test result
			assertStringContains(e.getMessage(), "Unexpected empty file");
		}		
	}
	
	@Test
	public void buildsModel() throws MogliPluginException{
		// prepare test
		setModelFile("simpelModelFile.txt");
		
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
		FileUtil.createFileWithContent(propertiesFile, "modelfile=simpelModelFile.txt");
		
		// call functionality under test
		Model model = modelProvider.buildModel();
		
		// verify test result
		assertNotNull(model);
		assertEquals("Model name", "SimpelTestModel", model.getName());
	}
	
	@Test
	public void unpacksInputDefaultData() throws MogliPluginException {
		// prepare test
		final File pluginInputDir = new File(applicationRootDir + "/" + DIR_INPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		FileUtil.deleteDirWithContent(pluginInputDir);
		assertFileDoesNotExist(pluginInputDir);

		// call functionality under test
		modelProvider.unpackDefaultInputData();
		
		// verify test result
		assertFileExists(pluginInputDir);
		final File modelFile = new File(pluginInputDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_TEXTFILE);
		assertFileExists(modelFile);
		assertFileContainsEntry(modelFile, "model DefaultModel");
	}
	
	@Test
	public void createsStatisticsFileWithoutUnusedElements() throws MogliPluginException, IOException {
		// prepare test
		final File outputDir = new File(MogliCodeCreator.getApplicationRootDir() + "/" 
				                       + DIR_OUTPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		FileUtil.deleteDirWithContent(outputDir);
		assertFileDoesNotExist(outputDir);
		modelProvider.unpackDefaultInputData();
		infrastructure = new MogliInfrastructure(
				createInfrastructureInitData(null, getPluginListForStatisticsFileTest(), StandardModelProviderStarter.PLUGIN_ID));
		modelProvider.setMogliInfrastructure(infrastructure);
		
		// call functionality under test
		modelProvider.doYourJob();
		
		// verify test result
		final File statisticsFile = new File(outputDir, StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedStatisticsFile.txt");
		assertFileExists(statisticsFile);
		assertFileEquals(expectedFile, statisticsFile);
	}

	@Test
	public void createsStatisticsFileWithUnusedMetaInfoAndValidatorElements() throws MogliPluginException, IOException {
		// prepare test
		final File outputDir = new File(MogliCodeCreator.getApplicationRootDir() + "/" 
				                       + DIR_OUTPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		FileUtil.deleteDirWithContent(outputDir);
		assertFileDoesNotExist(outputDir);
		modelProvider.unpackDefaultInputData();
		setModelFile("MetaInfoForStatisticsFileTestWithUnusedElements.txt");
		final List<PluginExecutable> pluginList = getPluginListForStatisticsFileTest();
		pluginList.add(createGeneratorWithUnusedMetaInfoValidators());
		infrastructure = new MogliInfrastructure(
				createInfrastructureInitData(null, pluginList, StandardModelProviderStarter.PLUGIN_ID));
		modelProvider.setMogliInfrastructure(infrastructure);
		
		// call functionality under test
		modelProvider.doYourJob();
		
		// verify test result
		final File statisticsFile = new File(outputDir, StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedStatisticsFileWithUnusedElements.txt");
		assertFileExists(statisticsFile);
		assertFileEquals(expectedFile, statisticsFile);
	}

	
	private PluginExecutable createGeneratorWithUnusedMetaInfoValidators() {
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
	public void throwsExceptionFor_MissingMandatoryModel_MetaInfo() {
		// prepare test
		setModelFile("MetaInfoValidatorTest_MissingMandatoryModel_MetaInfo.txt");
		
		// call functionality under test
		try {
			modelProvider.doYourJob();
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", TextConstants.TEXT_METAINFO_VALIDATION_ERROR_OCCURRED
					                             + infrastructure.getPluginLogFile().getName(), e.getMessage());
			assertFileContainsEntry(infrastructure.getPluginLogFile(), 
					                "ERROR: MetaInfo 'modelMetaInfoMandatory' does not found for model 'MetaInfoValidatorTestModel'");
			assertFileContainsEntryNTimes(infrastructure.getPluginLogFile(), "ERROR: MetaInfo", 1);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionFor_MissingMandatoryClass_MetaInfo() {
		// prepare test
		setModelFile("MetaInfoValidatorTest_MissingMandatoryClass_MetaInfo.txt");
		
		// call functionality under test
		try {
			modelProvider.doYourJob();
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", TextConstants.TEXT_METAINFO_VALIDATION_ERROR_OCCURRED
					                             + infrastructure.getPluginLogFile().getName(), e.getMessage());
			assertFileContainsEntry(infrastructure.getPluginLogFile(), 
					                "ERROR: MetaInfo 'classMetaInfoMandatory' does not found for classDescriptor 'TestklasseA'");
			assertFileContainsEntryNTimes(infrastructure.getPluginLogFile(), "ERROR: MetaInfo", 1);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionFor_MissingMandatoryAttribute_MetaInfo() {
		// prepare test
		setModelFile("MetaInfoValidatorTest_MissingMandatoryAttribute_MetaInfo.txt");
		
		// call functionality under test
		try {
			modelProvider.doYourJob();
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", TextConstants.TEXT_METAINFO_VALIDATION_ERROR_OCCURRED
					                             + infrastructure.getPluginLogFile().getName(), e.getMessage());
			assertFileContainsEntry(infrastructure.getPluginLogFile(), 
					                "ERROR: MetaInfo 'attributeMetaInfoMandatory' does not found for attributeDescriptor 'A2'");
			assertFileContainsEntryNTimes(infrastructure.getPluginLogFile(), "ERROR: MetaInfo", 1);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void writesErrorLogLineForAllMissingMandatoryMetaInfos() {
		// prepare test
		setModelFile("MetaInfoValidatorTest_AllMissingMandatory_MetaInfo.txt");
		
		// call functionality under test
		try {
			modelProvider.doYourJob();
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", TextConstants.TEXT_METAINFO_VALIDATION_ERROR_OCCURRED
					                             + infrastructure.getPluginLogFile().getName(), e.getMessage());
			assertFileContainsEntryNTimes(infrastructure.getPluginLogFile(), "ERROR: MetaInfo", 6);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void validatesMetaInfosSuccessfully() throws MogliPluginException {
		// prepare test
		setModelFile("MetaInfoValidatorTest_Success.txt");
		
		// call functionality under test
		modelProvider.doYourJob();
	}

	private List<PluginExecutable> getPluginListForValidatorTest() {
		final List<PluginExecutable> toReturn = new ArrayList<PluginExecutable>();
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

	private List<PluginExecutable> getPluginListForStatisticsFileTest() {
		final List<PluginExecutable> toReturn = new ArrayList<PluginExecutable>();
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
		final Model model = modelProvider.getModel();
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
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), TextConstants.TEXT_MODEL_NOT_EXISTS);
			return;
		}
		fail("Expected exception not thrown!");
	}
}
