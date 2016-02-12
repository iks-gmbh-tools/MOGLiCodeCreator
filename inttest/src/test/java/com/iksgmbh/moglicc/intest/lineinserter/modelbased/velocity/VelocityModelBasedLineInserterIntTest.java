package com.iksgmbh.moglicc.intest.lineinserter.modelbased.velocity;

import static com.iksgmbh.moglicc.lineinserter.modelbased.velocity.VelocityModelBasedLineInserterStarter.BEAN_FACTORY_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedLineInserterIntTest extends IntTestParent {

	@Test
	public void createsBeanFactory() throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();

		// call functionality under test
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result in plugin directory
		final InfrastructureService infrastructure = velocityModelBasedLineInserterStarter.getInfrastructure();
		File file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);

		// verify test result in target directory read from template file
		file = new File(applicationRootDir + "/example", "BeanFactory.java");
		List<String> fileContentAsList = MOGLiFileUtil.getFileContentAsList(file);
		assertEquals("Line number", 36, fileContentAsList.size());
	}

	@Test
	public void createsArtefactOnlyIfModelIsValid() throws Exception {
		// prepare test
		final String artefactName = "TestArtefact";
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File artefactTemplateDir = new File(velocityModelBasedLineInserterStarter.getInfrastructure().getPluginInputDir(),
				artefactName);
		artefactTemplateDir.mkdirs();
		final File testTemplate = new File(artefactTemplateDir, "Main.tpl");
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName Test.txt" +
														FileUtil.getSystemLineSeparator() +
														"@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" +
														FileUtil.getSystemLineSeparator() +
                                                        "@NameOfValidModel NotExistingModel" + FileUtil.getSystemLineSeparator() +
				                                        "Test");

		// call functionality under test
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result
		final File artefactTargetDir = new File(velocityModelBasedLineInserterStarter.getInfrastructure().getPluginOutputDir(),
				artefactName);
		assertFileDoesNotExist(artefactTargetDir);

		// prepare follow up test
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName Test.txt" +
				                                        FileUtil.getSystemLineSeparator() +
				                                        "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" +
				                                        FileUtil.getSystemLineSeparator() +
                                                        "@NameOfValidModel MOGLiCC_JavaBeanModel" + FileUtil.getSystemLineSeparator() +
                                                        "Test");

		// call functionality under test
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result
		assertFileExists(artefactTargetDir);
	}

	@Test
	public void throwsExceptionIfTwoMainTemplatesDoNotDifferInTheirTargetFilename() throws MOGLiPluginException {
		executeTargetDefinitionTestWith("file1.txt", "file2.txt", "temp", "temp");
	}

	@Test
	public void throwsExceptionIfTwoMainTemplatesDoNotDifferInTheirTargetDir() throws MOGLiPluginException {
		executeTargetDefinitionTestWith("file.txt", "file.txt", "temp1", "temp2");
	}

	public void executeTargetDefinitionTestWith(final String filename1, final String filename2,
			                                    final String targetDirString1, final String targetDirString2)
	                                            throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File targetDir1 = new File(applicationRootDir, targetDirString1);
		targetDir1.mkdirs();
		final File targetFile1 = new File(targetDir1, filename1);
		MOGLiFileUtil.createNewFileWithContent(targetFile1, "test");
		final File targetDir2 = new File(applicationRootDir, targetDirString2);

		if(! targetDirString1.equals(targetDirString2)) {
			targetDir2.mkdirs();
			final File targetFile2 = new File(targetDir1, filename2);
			MOGLiFileUtil.createNewFileWithContent(targetFile2, "test");

		}

		final File templateDir = new File(velocityModelBasedLineInserterStarter.getInfrastructure().getPluginInputDir(),
				                          "TestArtifact");
		templateDir.mkdirs();
		final File mainTemplate1 = new File(templateDir, "Main1.tpl");
		MOGLiFileUtil.createNewFileWithContent(mainTemplate1, "@" + KnownGeneratorPropertyNames.TargetFileName.name()
				                                                  + " " + filename1
				                                                  + FileUtil.getSystemLineSeparator()
													                + "@" + KnownGeneratorPropertyNames.TargetDir
													                + " " + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER
													                + "/" + targetDirString1);
		final File mainTemplate2 = new File(templateDir, "Main2.tpl");
		MOGLiFileUtil.createNewFileWithContent(mainTemplate2, "@" + KnownGeneratorPropertyNames.TargetFileName.name()
												                + " " + filename2
												                + FileUtil.getSystemLineSeparator()
												                + "@" + KnownGeneratorPropertyNames.TargetDir
												                + " " + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER
												                + "/" + targetDirString2);

		// call functionality under test
		try {
			velocityModelBasedLineInserterStarter.doYourJob();
			// cleanup
			FileUtil.deleteDirWithContent(targetDir1);
			FileUtil.deleteDirWithContent(targetDir2);

			fail("Expected exception not thrown!");
		} catch (MOGLiPluginException e) {
			// verify test result
			assertStringEquals("error message", "There are main templates for artefact 'TestArtifact' " +
					                            "that differ in there targetFileName or targetDir!", e.getMessage());

			// cleanup
			FileUtil.deleteDirWithContent(targetDir1);
			FileUtil.deleteDirWithContent(targetDir2);
		}
	}

	@Test
	public void parsesTemplateFileWithTargetNameLeadingNonAsciiChars() throws Exception {
		// prepare test
		final String artefactName = "TestArtefact";
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File artefactTemplateDir = new File(velocityModelBasedLineInserterStarter.getInfrastructure().getPluginInputDir(),
				artefactName);
		artefactTemplateDir.mkdirs();
		final File testTemplate = new File(artefactTemplateDir, "Main.tpl");
		final File sourceFile = new File(getProjectTestResourcesDir(), "InserterTemplateWithTargetNameLeadingNonAsciiChars.tpl");
		System.out.println(sourceFile.getAbsolutePath());
		FileUtil.copyBinaryFile(sourceFile, testTemplate);

		// call functionality under test
		velocityModelBasedLineInserterStarter.doYourJob();

		//  verify test result by no exception thrown
	}


	@Test
	public void skipsTargetFileCreationWhenConfiguredSoInTemplateProperties()
			throws MOGLiPluginException, IOException
	{
		// prepare test
		final String modelName = "SkipTestModel";
		final String targetFileName = "config.xml";

		final String modelFileContent = "model " + modelName + FileUtil.getSystemLineSeparator()
				+ "  metainfo includesDB false"
				+ FileUtil.getSystemLineSeparator() + "class de.Test1";
		prepareModelFile(modelName, modelFileContent);

		final String templateFileContent = "@CreateNew true"
				+ FileUtil.getSystemLineSeparator()
				+ "@TargetFileName " + targetFileName
				+ FileUtil.getSystemLineSeparator()
				+ "@TargetDir "
				+ MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER
				+ "/example"
				+ FileUtil.getSystemLineSeparator()
				+ "@SkipGeneration  NOT $model.getMetaInfoValueFor(\"includesDB\")";
		prepareTemplateFile(templateFileContent);

		final File resultFile = new File(applicationRootDir, "example/" + targetFileName);
		resultFile.delete();
		assertFileDoesNotExist(resultFile);


		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result
		assertFileDoesNotExist(resultFile);
	}

	private void prepareModelFile(final String modelName, final String modelFileContent) throws MOGLiPluginException, IOException
	{
		final File defaultModelFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
				                               StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		defaultModelFile.delete();
		assertFileDoesNotExist(defaultModelFile);
		final File testPropertiesFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(),
				                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		testPropertiesFile.delete();
		assertFileDoesNotExist(testPropertiesFile); // asserts preparation is correct
		final File testModelFile = new File(standardModelProviderStarter.getInfrastructure().getPluginInputDir(), modelName
				                            + ".txt");
		MOGLiFileUtil.createNewFileWithContent(testModelFile, modelFileContent);
		MOGLiFileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=" + modelName + ".txt");
		assertFileExists(testModelFile); // asserts preparation is correct
	}


	private void prepareTemplateFile(final String templateFileContent) throws MOGLiPluginException, IOException
	{
		File inputDir = velocityModelBasedLineInserterStarter.getInfrastructure().getPluginInputDir();
		FileUtil.deleteDirWithContent(inputDir);
		assertFileDoesNotExist(inputDir);
		inputDir = new File(inputDir, "Test");
		inputDir.mkdirs();
		final File templateFile = new File(inputDir, "SkipTest.tpl");
		MOGLiFileUtil.createNewFileWithContent(templateFile, templateFileContent);
		assertFileExists(templateFile);  // asserts preparation is correct
	}


	@Test
	public void correctsVelocityBug_fileExtensionOfTargetFileNameWasCut() throws Exception {
		// prepare test
		final String modelName = "SkipTestModel";

		final String modelFileContent = "model " + modelName + FileUtil.getSystemLineSeparator()
				+ "  metainfo includesDB true"
				+ FileUtil.getSystemLineSeparator() + "class de.Test1";
		prepareModelFile(modelName, modelFileContent);

		final String templateFileContent = "@CreateNew true"
				+ FileUtil.getSystemLineSeparator()
				+ "@TargetFileName " + "test_$model.getSize()_filename.java # any comment"
				+ FileUtil.getSystemLineSeparator()
				+ "@TargetDir "
				+ MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER
				+ "/example"
				+ FileUtil.getSystemLineSeparator()
				+ "@SkipGeneration  NOT $model.getMetaInfoValueFor(\"includesDB\")";
		prepareTemplateFile(templateFileContent);

		final File resultFile = new File(applicationRootDir, "example/test_1_filename.java");
		resultFile.delete();
		assertFileDoesNotExist(resultFile);


		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result
		assertFileExists(resultFile);
	}

	@Test
	public void replacesNumberSignInTheGeneratedOutput() throws Exception 
	{
		// prepare test
		final String modelName = "TestModel";
		final String targetFileName = "shellScript.sh";

		final String modelFileContent = "model " + modelName + FileUtil.getSystemLineSeparator()
				+ "  metainfo includesDB false"
				+ FileUtil.getSystemLineSeparator() + "class de.Test1";
		prepareModelFile(modelName, modelFileContent);

		final String templateFileContent = "@CreateNew true"
				+ FileUtil.getSystemLineSeparator()
				+ "@TargetFileName " + targetFileName
				+ FileUtil.getSystemLineSeparator()
				+ "@TargetDir "
				+ MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER
				+ "/target"
				+ FileUtil.getSystemLineSeparator()
				+ "@ReplaceToNumberSign rem"
				+ FileUtil.getSystemLineSeparator()
				+ "rem This is a comment.";
		prepareTemplateFile(templateFileContent);

		final File resultFile = new File(applicationRootDir, "target/" + targetFileName);
		resultFile.delete();
		assertFileDoesNotExist(resultFile);


		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result
		assertFileContainsEntry(resultFile, "# This is a comment");
	}
	
}
