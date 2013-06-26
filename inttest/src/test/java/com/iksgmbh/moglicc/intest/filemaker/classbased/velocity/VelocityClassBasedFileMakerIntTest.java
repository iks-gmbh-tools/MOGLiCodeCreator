package com.iksgmbh.moglicc.intest.filemaker.classbased.velocity;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
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
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiJavaBean");
		assertFileDoesNotExist(file);
		final String generationReport = velocityClassBasedFileMakerStarter.getGenerationReport();
		assertStringEquals("generationReport", velocityClassBasedFileMakerStarter.getId() + " have had nothing to do. " +
				                               "6 artifacts, but no class in model file found.", generationReport);
	}
	
	@Test
	public void createsJavaBeanMiscJavaFile() throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiJavaBean/Misc.java");
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedMisc.java");
		assertFileEquals(expectedFile, file);
	}

	@Test
	public void createsArtefactOnlyIfModelIsValid() throws Exception {
		// prepare test
		final String artefactName = "TestArtefact";
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File artefactTemplateDir = new File(velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir(),
				artefactName);
		artefactTemplateDir.mkdirs();
		final File testTemplate = new File(artefactTemplateDir, "Main.tpl");
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" + FileUtil.getSystemLineSeparator() +
				                                     "@NameOfValidModel na" + FileUtil.getSystemLineSeparator() +
				                                     "${classDescriptor.simpleName}");

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final File artefactTargetDir = new File(velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginOutputDir(),
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
		final File defaultModelFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
                                               StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		defaultModelFile.delete();
		assertFileDoesNotExist(defaultModelFile);
		final File testPropertiesFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
                                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		testPropertiesFile.delete();
		assertFileDoesNotExist(testPropertiesFile);
		final File testModelFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
				                            "Umlauts.txt");
		MOGLiFileUtil.createNewFileWithContent(testModelFile, "model DemoModel" + FileUtil.getSystemLineSeparator() +
				                                              "metainfo umlauts ßüäöÜÄÖ" + FileUtil.getSystemLineSeparator() +
                                                              "class de.Test");
		MOGLiFileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=Umlauts.txt");

		assertFileExists(testModelFile);

		File inputDir = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir();
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
		final File generatorPluginInputDir = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir();
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
		final File f = new File(velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir(), MetaInfoValidationUtil.FILENAME_VALIDATION);
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
		final File defaultModelFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
                                               StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		defaultModelFile.delete();
		assertFileDoesNotExist(defaultModelFile);
		final File testPropertiesFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
                                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		testPropertiesFile.delete();
		assertFileDoesNotExist(testPropertiesFile);
		final File testModelFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
				                            modelName + ".txt");
		MOGLiFileUtil.createNewFileWithContent(testModelFile, modelFileContent);
		MOGLiFileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=" + modelName + ".txt");
		assertFileExists(testModelFile);

		File inputDir = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir();
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
		
		assertStringDoesNotContain(velocityClassBasedFileMakerStarter.getGenerationReport(), "Test1.java");
		assertStringContains(velocityClassBasedFileMakerStarter.getGenerationReport(), "Test2.java");
		
		final String logfileContent = FileUtil.getFileContent(velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginLogFile());
		assertStringContains(logfileContent, "Test1.java");
		assertStringDoesNotContain(logfileContent, "Test2.java");
	}
}
