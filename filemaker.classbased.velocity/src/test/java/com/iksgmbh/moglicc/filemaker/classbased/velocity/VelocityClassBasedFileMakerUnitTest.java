package com.iksgmbh.moglicc.filemaker.classbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.ConditionalMetaInfoValidator;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.FileUtil.FileCreationStatus;

public class VelocityClassBasedFileMakerUnitTest extends VelocityClassBasedFileMakerTestParent {

	private static final String MAIN_TEMPLATE = "A_MainTemplate.tpl";

	private File generatorPropertiesFile;

	@Override
	@Before
	public void setup() 
	{
		super.setup();
		
		applicationTempDir.mkdirs();
		generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), VelocityClassBasedFileMakerStarter.PLUGIN_PROPERTIES_FILE);
		giveSystemTimeToExecute();
	}

	@Test
	public void findsArtefactList() throws MOGLiPluginException 
	{
		// prepare test		
		MOGLiFileUtil.createNewFileWithContent(generatorPropertiesFile, "");
		giveSystemTimeToExecute();

		// call functionality under test
		final List<String> artefactList = velocityClassBasedGenerator.getArtefactList();

		// verify test result
		final StringBuffer sb = new StringBuffer(System.getProperty("line.separator"));
		for (String artefactName : artefactList) {
			sb.append(artefactName);
			sb.append(System.getProperty("line.separator"));
			System.out.println(artefactName);
		}
		assertEquals("Unexpected artefact list: " + sb.toString(), 11, artefactList.size());
	}

	@Test
	public void throwsExceptionIfMainTemplateIsNotFound() {
		// prepare test
		final File artefactDir = new File(infrastructure.getPluginInputDir(), VelocityClassBasedFileMakerStarter.ARTEFACT_JAVABEAN);
		final File mainTemplateFile = new File(artefactDir, MAIN_TEMPLATE);
		mainTemplateFile.delete();

		// call functionality under test
		try {
			velocityClassBasedGenerator.findMainTemplate(artefactDir);
			fail("Expected exception not thrown!");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), TemplateUtil.NO_MAIN_TEMPLATE_FOUND);

			// cleanup
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
		}
	}

	@Test
	public void findsMainTemplate() {
		// prepare test
		final File artefactDir = new File(infrastructure.getPluginInputDir(), VelocityClassBasedFileMakerStarter.ARTEFACT_JAVABEAN);

		// call functionality under test
		String mainTemplate = null;
		try {
			mainTemplate = velocityClassBasedGenerator.findMainTemplate(artefactDir);
		} catch (MOGLiPluginException e) {
			fail(e.getMessage());
		}

		// verify test result
		assertEquals("Main Template filename", MAIN_TEMPLATE, mainTemplate);
	}

	@Test
	public void unpacksInputDefaultData() throws MOGLiPluginException {
		// prepare test
		FileUtil.deleteDirWithContent(applicationInputDir);

		// call functionality under test
		velocityClassBasedGenerator.unpackDefaultInputData();

		// verify test result
		assertFileExists(applicationInputDir);
	}

	@Test
	public void savesTargetFilesInPluginOutputDir() throws MOGLiPluginException {
		// prepare test
		final String targetFileName = "targetFile.txt";
		VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "temp", "Content", true);
		prepareResultData(resultData);
		final File targetFile = prepareTargetFile(applicationOutputDir, VelocityClassBasedFileMakerStarter.PLUGIN_ID
                                                                        + "/" + VelocityClassBasedFileMakerStarter.ARTEFACT_JAVABEAN
                                                                        + "/" + targetFileName);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// verify test result
		assertFileExists(targetFile);
		assertFileContainsEntry(targetFile, "Content");
	}

	private VelocityFileMakerResultData buildVelocityGeneratorResultData(final String targetFileName,
																		 final String targetdir, 
																		 final String content, 
																		 final boolean createNew) 
	{
		final BuildUpGeneratorResultData buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		buildUpGeneratorResultData.setGeneratedContent(content);
		final VelocityFileMakerResultData toReturn =
			   new VelocityFileMakerResultData(buildUpGeneratorResultData);

		if (targetFileName != null) {
			toReturn.addProperty(KnownGeneratorPropertyNames.TargetFileName.name(), targetFileName);
		}
		if (targetdir != null) {
			toReturn.addProperty(KnownGeneratorPropertyNames.TargetDir.name(), targetdir);
		}
		if (createNew) {
			toReturn.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "true");
		}
		return toReturn;
	}

	private void prepareResultData(VelocityGeneratorResultData resultData) {
		final List<VelocityGeneratorResultData> resultDataList = new ArrayList<VelocityGeneratorResultData>();
		resultDataList.add(resultData);
		velocityEngineProvider.setVelocityGeneratorResultDataList(resultDataList);
	}

	private File prepareTargetFile(final File targetDir, final String filename) {
		final File targetFile = new File(targetDir, filename);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);
		return targetFile;
	}

	@Test
	public void savesTargetFilesInTargetDirReadFromTemplateFile() throws MOGLiPluginException {
		// prepare test
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "temp", "Content", true);
		prepareResultData(resultData);
		final File targetFile = prepareTargetFile(applicationTempDir, targetFileName);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// verify test result
		assertFileExists(targetFile);
		assertFileContainsEntry(targetFile, "Content");
	}

	@Test
	public void throwsExceptionIfOutputFileNameIsMissingInTemplateFile() throws MOGLiPluginException {
		// prepare test
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(null, "temp", "Content", false);
		prepareResultData(resultData);

		// call functionality under test
		try {
			velocityClassBasedGenerator.doYourJob();
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityGeneratorResultData.NO_TARGET_FILE_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}


	@Test
	public void createsNotExistingTargetDirWithCreateNewInstructions() throws MOGLiPluginException {
		// prepare test
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, targetDir, "Content", true);
		prepareResultData(resultData);
		final File dir = new File(targetDir);
		FileUtil.deleteDirWithContent(dir);
		assertFileDoesNotExist(dir);
		velocityClassBasedGenerator.setTestDir(null);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// verify test result
		assertFileExists(dir);
	}

	@Test
	public void createsNotExistingTargetFileDirWithoutCreateNewInstruction() throws MOGLiPluginException {
		// prepare test
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName,
				                                       targetDir, "Content", false);
		prepareResultData(resultData);
		final File targetDirAsFile = new File(targetDir);
		targetDirAsFile.mkdirs();
		final File targetFile = new File(targetDir, targetFileName);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);
		velocityClassBasedGenerator.setTestDir(null);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// verify test result
		assertFileExists(targetFile);
	}

	@Test
	public void createsExistingTargetFileNewlyWithCreateNewInstructions() throws Exception {
		// prepare test
		final String targetDir = applicationRootDir.getAbsolutePath() + "/example";
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, targetDir, "ContentToInsert", true);
		prepareResultData(resultData);
		final File targetFile = new File(targetDir, targetFileName);
		targetFile.getParentFile().mkdirs();
		FileUtil.createNewFileWithContent(targetFile, "Test");
		assertFileDoesNotContainEntry(targetFile, "ContentToInsert");
		velocityClassBasedGenerator.setTestDir(null);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// verify test result
		assertFileContainsEntry(targetFile, "ContentToInsert");
	}

	@Test
	public void doesNotOverwriteExistingTargetFile() throws Exception {
		// prepare test
		final String targetDir = applicationRootDir.getAbsolutePath() + "/example";
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName,
				                                       targetDir, "ContentToInsert", false);
		prepareResultData(resultData);
		final File targetFile = new File(targetDir, targetFileName);
		targetFile.getParentFile().mkdirs();
		FileUtil.createNewFileWithContent(targetFile, "Test");
		assertFileContainsEntry(targetFile, "Test");
		velocityClassBasedGenerator.setTestDir(null);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// verify test result
		assertFileContainsEntry(targetFile, "Test");
	}

	@Test
	public void createsTargetFileWithRootDirDefinedInTemplateFile() throws MOGLiPluginException {
		// prepare test
		final String targetDir = applicationRootDir.getAbsolutePath() + "/temp";
		final String targetFileName = "targetFile.txt";
		final File targetFile = new File(targetDir, targetFileName);
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetFile);
		targetFile.getParentFile().mkdirs();
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "temp", "Content", false);
		prepareResultData(resultData);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// verify test result
		assertFileExists(targetFile);
	}

	@Test
	public void ignoresSubdirAsArtefactIfDefinedInPluginPropertiesFile() throws MOGLiPluginException {
		// prepare test
		final File subdir = new File(infrastructure.getPluginInputDir(), ".svn");
		subdir.mkdirs();
		assertFileExists(subdir);
		MOGLiFileUtil.appendToFile(generatorPropertiesFile, ".svn=" + ArtefactListUtil.IGNORE );
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "example",
				"package com.iksgmbh.test", true);
		prepareResultData(resultData);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();

		// cleanup
		subdir.delete();
		assertFileDoesNotExist(subdir);


		// verify test result
		final File outputDir = new File(infrastructure.getPluginOutputDir(), ".svn");
		assertFileDoesNotExist(outputDir);
	}

	@Test
	public void writesUmlautsIntoTargetFile() throws MOGLiPluginException {
		// prepare test
		final List<VelocityFileMakerResultData> resultList = new ArrayList<VelocityFileMakerResultData>();
		final VelocityFileMakerResultData resultData = buildVelocityGeneratorResultData("Umlaute.txt", "example",
				                                                                        "ßäüöÄÜÖ", true);
		resultList.add(resultData);

		// call functionality under test
		velocityClassBasedGenerator.writeFilesIntoTargetDirReadFromTemplateFile(resultList, "artefact");

		// verify test result
		final File file = new File(applicationRootDir + "/example", "Umlaute.txt");
		String actualFileContent = MOGLiFileUtil.getFileContent(file);
		assertStringEquals("file content", "ßäüöÄÜÖ", actualFileContent);
	}

	@Test
	public void returnsMetaInfoValidatorList() throws MOGLiPluginException {
		// prepare test
		final File conditionFile = new File(velocityClassBasedGenerator.getInfrastructure().getPluginInputDir(), "condition.txt");
		MOGLiFileUtil.createNewFileWithContent(conditionFile, "|if MetaInfo| The \"other\" MetaInfo Name1 |exists.|"
													          + FileUtil.getSystemLineSeparator() +
													          "|if MetaInfo| The \"other\" MetaInfo Name2 |does not exist.|"
													          + FileUtil.getSystemLineSeparator() +
													          "|if MetaInfo| The \"other\" MetaInfo Name3 |with value| my value |does not exist.|" );

        final File validatorFile = new File(velocityClassBasedGenerator.getInfrastructure().getPluginInputDir(), MetaInfoValidationUtil.FILENAME_VALIDATION);
		MOGLiFileUtil.createNewFileWithContent(validatorFile, "|MetaInfo| MetaInfoTestName1 |is| optional |for| attributes |in| ModelName |.|"
				                                             + FileUtil.getSystemLineSeparator() +
				                                             "|MetaInfo| MetaInfoTestName2 |is| mandatory |for| attributes |in| ModelName |.|"
				                                             + FileUtil.getSystemLineSeparator() +
				                                             "|MetaInfo| MetaInfoTestName3 |is valid to occur| 0-4 |time(s) for| attributes |in| ModelName |.|"
				                                             + FileUtil.getSystemLineSeparator() +
				                                             "|MetaInfo| MetaInfoTestName4 |is valid to occur| 1-2 |time(s) for| attributes |in| ModelName |if| condition.txt |is true.|" );

		// call functionality under test
		final List<MetaInfoValidator> metaInfoValidatorList = velocityClassBasedGenerator.getMetaInfoValidatorList();

		// verify test result
		assertEquals("validator number", 4, metaInfoValidatorList.size());
		assertEquals("condition number", 3, ((ConditionalMetaInfoValidator)metaInfoValidatorList.get(3)).getTotalNumberOfConditions());
	}

	@Test
	public void throwsExceptionIfConditionFileWasNotFound() throws MOGLiPluginException {
		// prepare test
        final File validatorFile = new File(velocityClassBasedGenerator.getInfrastructure().getPluginInputDir(), MetaInfoValidationUtil.FILENAME_VALIDATION);
		MOGLiFileUtil.createNewFileWithContent(validatorFile, "|MetaInfo| MetaInfoTestName1 |is| optional |for| attributes |in| ModelName |.|"
				                                             + FileUtil.getSystemLineSeparator() +
				                                             "|MetaInfo| MetaInfoTestName4 |is valid to occur| 1-2 |time(s) for| attributes |in| ModelName |if| notExistingConditionFile.txt |is true.|" );

		// call functionality under test
		try {
			velocityClassBasedGenerator.getMetaInfoValidatorList();
			fail("Expected exception not thrown!");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), "Expected condition file does not exist: ");
			assertStringContains(e.getMessage(), "notExistingConditionFile.txt");
		}
	}
	
	@Test
	public void doesNotOverwriteExistingFilesInTargetDirAndcreatesCorrespondingGenerationReport() throws Exception {
		// prepare test
		final String artefactName = "testArtefact";
		final File artefactDir = new File(infrastructure.getPluginInputDir(), artefactName);
		artefactDir.mkdirs();
		assertFileExists(artefactDir);
		final File templateFile = new File(artefactDir, "main.tpl");
		MOGLiFileUtil.createNewFileWithContent(templateFile, "content of template is not relevant here");
		final String targetFileName = "targetFile.txt";
		final File targetDir = new File(applicationRootDir, artefactName);
		FileUtil.deleteDirWithContent(targetDir);
		targetDir.mkdirs();
		final File targetFile = new File(targetDir, artefactName);
		targetFile.createNewFile();
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, artefactName,
				"TargetFileContent", false);
		prepareResultData(resultData);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();
		
		// cleanup
		FileUtil.deleteDirWithContent(artefactDir);

		// verify test result
		final String generationReport = velocityClassBasedGenerator.getGeneratorReport();
		assertTrue("unexpected generation result", generationReport.contains("targetFile.txt did already exist and was NOT overwritten in testArtefact"));
	}

	final HashMap<String, FileCreationStatus> result = new HashMap<String, FileCreationStatus>();
}
