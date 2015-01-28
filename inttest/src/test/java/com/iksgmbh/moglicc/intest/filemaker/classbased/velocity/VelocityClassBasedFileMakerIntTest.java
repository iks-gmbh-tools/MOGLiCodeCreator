package com.iksgmbh.moglicc.intest.filemaker.classbased.velocity;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityClassBasedFileMakerIntTest extends IntTestParent {

	@Test
	public void createsNothingWhenModelFileContainsNoClassDefinition() throws MOGLiPluginException {
		// prepare test
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model MOGLiCC_JavaBeanModel");
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiJavaBean");
		assertFileDoesNotExist(file);
		final String generationReport = velocityClassBasedFileMakerStarter.getGeneratorReport();
		System.out.println(generationReport);
		assertStringEquals("generationReport", "9 input artefact(s) found. No classes in model. Nothing to do.", generationReport);
	}

	@Test
	public void createsJavaBeanMiscJavaFile() throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiJavaBean/Misc.java");
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedMisc.java");
		assertFileEquals(expectedFile, file);
	}
	
	@Test
	public void createsJavaBeanMiscFactoryJavaFile() throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiJavaBeanFactory/MiscFactory.java");
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedMiscFactory.java");
		assertFileEquals(expectedFile, file);
	}	

	@Test
	public void createsArtefactOnlyIfModelIsValid() throws Exception {
		// prepare test
		final String artefactName = "TestArtefact";
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File artefactTemplateDir = new File(velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir(),
				artefactName);
		artefactTemplateDir.mkdirs();
		final File testTemplate = new File(artefactTemplateDir, "Main.tpl");
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" + FileUtil.getSystemLineSeparator() +
				                                     "@NameOfValidModel na" + FileUtil.getSystemLineSeparator() +
				                                     "${classDescriptor.simpleName}");

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final File artefactTargetDir = new File(velocityClassBasedFileMakerStarter.getInfrastructure().getPluginOutputDir(),
				artefactName);
		assertFileDoesNotExist(artefactTargetDir);


		// prepare follow up test
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" +
				FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel MOGLiCC_JavaBeanModel" + FileUtil.getSystemLineSeparator() +
                "${classDescriptor.simpleName}");

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		assertFileExists(artefactTargetDir);
	}

	@Test
	public void createsResultFileWithUmlauts() throws MOGLiPluginException, IOException {
		// prepare test
		final File defaultModelFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
                                               StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		defaultModelFile.delete();
		assertFileDoesNotExist(defaultModelFile);
		final File testPropertiesFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
                                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		testPropertiesFile.delete();
		assertFileDoesNotExist(testPropertiesFile);
		final File testModelFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
				                            "Umlauts.txt");
		MOGLiFileUtil.createNewFileWithContent(testModelFile, "model DemoModel" + FileUtil.getSystemLineSeparator() +
				                                              "metainfo umlauts ßüäöÜÄÖ" + FileUtil.getSystemLineSeparator() +
                                                              "class de.Test");
		MOGLiFileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=Umlauts.txt");

		assertFileExists(testModelFile);

		File inputDir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
		FileUtil.deleteDirWithContent(inputDir);
		assertFileDoesNotExist(inputDir);
		inputDir = new File(inputDir, "Test");
		inputDir.mkdirs();
		final File templateFile = new File(inputDir, "Umlauts.tpl");
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
				                                             "@TargetFileName Umlauts.txt" + FileUtil.getSystemLineSeparator() +
				                                             "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
				                                             "ßüäöÜÄÖ $model.getMetaInfoValueFor(\"umlauts\")");
		assertFileExists(templateFile);

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final File resultFile = new File(applicationRootDir, "example/Umlauts.txt");
		assertFileExists(resultFile);
		final String actualFileContent = FileUtil.getFileContent(resultFile);
		assertStringEquals("file content", "ßüäöÜÄÖ ßüäöÜÄÖ", actualFileContent);
	}

	@Test
	public void usesStandardOutputEncodingFormatIfNotDefinedInMainTemplate() throws Exception {
		// prepare test
		final File templateFile = prepareOutputEncodingFormatTest();
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName Umlauts.txt" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example");

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		assertStringEquals("OutputEncodingFormat", "UTF-8", velocityClassBasedFileMakerStarter.getEncodingHelper().getEncoding());
	}

	protected File prepareOutputEncodingFormatTest() throws MOGLiPluginException {
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File generatorPluginInputDir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
		FileUtil.deleteDirWithContent(generatorPluginInputDir);
		final File targetDir = new File(generatorPluginInputDir, "myNewArtefact");
		targetDir.mkdirs();
		final File templateFile = new File(targetDir, "main.tpl");
		return templateFile;
	}

	@Test
	public void usesStandardOutputEncodingFormatIfValueInMainTemplateIsNotValid() throws Exception {
		// prepare test
		final File templateFile = prepareOutputEncodingFormatTest();
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName Umlauts.txt" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                "@OutputEncodingFormat bubu");

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		assertStringEquals("OutputEncodingFormat", "UTF-8", velocityClassBasedFileMakerStarter.getEncodingHelper().getEncoding());
	}

	@Test
	public void readsOutputEncodingFormatFromMainTemplate() throws Exception {
		// prepare test
		final File templateFile = prepareOutputEncodingFormatTest();
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName Umlauts.txt" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                "@OutputEncodingFormat UTF-16");

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		assertStringEquals("OutputEncodingFormat", "UTF-16", velocityClassBasedFileMakerStarter.getEncodingHelper().getEncoding());
	}

	@Test
	public void readMetaInfoValidationFileContainingNonASCIIChars() throws Exception {
		// prepare test
		final File testValidationFile = getTestFile("MetaInfoWithNonASCIIChars.validation");
		final File f = new File(velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir(), MetaInfoValidationUtil.FILENAME_VALIDATION);
		FileUtil.copyBinaryFile(testValidationFile, f);

		// call functionality under test
		standardModelProviderStarter.doYourJob();

		// verified if no exception was thrown
	}

	@Test
	public void skipsTargetFileCreationWhenConfiguredSoInTemplateProperties() throws MOGLiPluginException, IOException {
		final String modelName = "SkipTestModel";

		final String modelFileContent = "model " + modelName + FileUtil.getSystemLineSeparator() +
                "class de.Test1" + FileUtil.getSystemLineSeparator() +
                "  metainfo nonPersistent true" + FileUtil.getSystemLineSeparator() +
                "class de.Test2"; // this class is NOT skipped

		final String templateFileContent = "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                                           "@TargetFileName ${classDescriptor.simpleName}.java" + FileUtil.getSystemLineSeparator() +
                                           "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                                           "@SkipGeneration $classDescriptor.doesHaveAnyMetaInfosWithName(\"nonPersistent\")";

		executeSkipGenerationTest(modelName, modelFileContent, templateFileContent);
	}

	@Test
	public void skipsTargetFileCreationWhenConfiguredSoInTemplatePropertiesUsingNotConstruction() throws MOGLiPluginException, IOException {
		final String modelName = "SkipFileTestModel";

		final String modelFileContent = "model " + modelName + FileUtil.getSystemLineSeparator() +
                "class de.Test1" + FileUtil.getSystemLineSeparator() +
                "  metainfo databaseRelevant false" + FileUtil.getSystemLineSeparator() +
                "class de.Test2"+ FileUtil.getSystemLineSeparator() +
                "  metainfo databaseRelevant true"; // this class is NOT skipped

		final String templateFileContent = "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                                           "@TargetFileName ${classDescriptor.simpleName}.java" + FileUtil.getSystemLineSeparator() +
                                           "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                                           "@SkipGeneration NOT $classDescriptor.getMetaInfoValueFor(\"databaseRelevant\")";

		executeSkipGenerationTest(modelName, modelFileContent, templateFileContent);
	}

	private void executeSkipGenerationTest(final String modelName, final String modelFileContent,
			                          final String templateFileContent) throws MOGLiPluginException, IOException
	{
		// prepare test
		final File defaultModelFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
                                               StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		defaultModelFile.delete();
		assertFileDoesNotExist(defaultModelFile);
		final File testPropertiesFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
                                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		testPropertiesFile.delete();
		assertFileDoesNotExist(testPropertiesFile);
		final File testModelFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
				                            modelName + ".txt");
		MOGLiFileUtil.createNewFileWithContent(testModelFile, modelFileContent);
		MOGLiFileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=" + modelName + ".txt");
		assertFileExists(testModelFile);

		File inputDir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
		FileUtil.deleteDirWithContent(inputDir);
		assertFileDoesNotExist(inputDir);
		inputDir = new File(inputDir, "Test");
		inputDir.mkdirs();
		final File templateFile = new File(inputDir, "SkipTest.tpl");
		MOGLiFileUtil.createNewFileWithContent(templateFile, templateFileContent);
		assertFileExists(templateFile);

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final File resultFile1 = new File(applicationRootDir, "example/Test1.java");
		assertFileDoesNotExist(resultFile1);
		final File resultFile2 = new File(applicationRootDir, "example/Test2.java");
		assertFileExists(resultFile2);

		assertStringContains(velocityClassBasedFileMakerStarter.getGeneratorReport(), 
				                   "configured to skip generation:" + FileUtil.getSystemLineSeparator() + "Test1.java");
		assertStringContains(velocityClassBasedFileMakerStarter.getGeneratorReport(), "Test2.java was created in");

		final String logfileContent = FileUtil.getFileContent(velocityClassBasedFileMakerStarter.getInfrastructure().getPluginLogFile());
		assertStringContains(logfileContent, "Test1.java");
		assertStringDoesNotContain(logfileContent, "Test2.java");
	}

	@Test
	public void acceptsTwoModelsAsValid() throws Exception {
		// prepare test
		final File templateFile = prepareOutputEncodingFormatTest();
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName Umlauts.txt" + FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel MOGLiCC_JavaBeanModel" + FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel anotherValidModel" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example");

		// ##############  Test 1: execute with model 1  ###############
		final File resultFile = new File(velocityClassBasedFileMakerStarter.getInfrastructure().getApplicationRootDir(),
		                                 "example/Umlauts.txt");
		assertFileDoesNotExist(resultFile);

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		assertFileExists(resultFile);

		// ##############  Test 2: execute with model 2  ###############
		FileUtil.replaceLinesInTextFile(modelFile, "MOGLiCC_JavaBeanModel", "anotherValidModel");
		FileUtil.replaceLinesInTextFile(modelFile, "metainfo useExtensionPlugin ExcelStandardModelProvider", "");
		resultFile.delete();
		assertFileDoesNotExist(resultFile);

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		assertFileExists(resultFile);

		// ##############  Test 3: execute with invalid model  ###############
		FileUtil.replaceLinesInTextFile(modelFile, "anotherValidModel", "anInvalidModel");
		resultFile.delete();
		assertFileDoesNotExist(resultFile);

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		assertFileDoesNotExist(resultFile);
	}

	@Test
	public void createsMemberClassImplementingCloneableAndSerializable() throws Exception {
		// prepare test
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verified if no exception was thrown
		final File result = new File(velocityClassBasedFileMakerStarter.getInfrastructure().getPluginOutputDir()
				                     + "/MOGLiJavaBean", "Member.java");
		assertFileExists(result);
		assertFileContainsEntry(result, "implements Serializable, Cloneable");
		assertFileContainsEntry(result, "private static final long serialVersionUID = ");
		assertFileContainsEntry(result, "public Object clone()");
	}
	
}
