package com.iksgmbh.moglicc.inserter.modelbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.inserter.modelbased.velocity.VelocityInserterResultData.KnownInserterPropertyNames;
import com.iksgmbh.moglicc.inserter.modelbased.velocity.test.VelocityModelBasedInserterTestParent;
import com.iksgmbh.moglicc.utils.MogliFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedInserterUnitTest extends VelocityModelBasedInserterTestParent {
	
	private static final String TARGET_FILE_TXT = "targetFile.txt";
	private static final String MAIN_TEMPLATE = "BeanFactoryClass.tpl";
	private static final String ARTEFACT_BEAN_FACTORY = "BeanFactoryClass";
	
	private File targetFile;
	private File generatorPropertiesFile;

	@Override
	@Before
	public void setup() {
		super.setup();
		applicationTempDir.mkdirs();
		
		final File sourcefile = new File(getProjectTestResourcesDir(), TARGET_FILE_TXT);
		targetFile = new File(applicationTempDir, TARGET_FILE_TXT);
		FileUtil.copyTextFile(sourcefile, targetFile);
		
		generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), 
	            VelocityModelBasedInserterStarter.PLUGIN_PROPERTIES_FILE);
		MogliFileUtil.createNewFileWithContent(generatorPropertiesFile, "");
	}
	
	@Test
	public void findsArtefactList() throws MogliPluginException {
		// call functionality under test
		final List<String> artefactList = velocityModelBasedInserter.getArtefactList();

		// verify test result
		assertEquals("artefact number", 4, artefactList.size());
	}
	
	@Test
	public void throwsExceptionIfMainTemplateIsNotFound() {
		// prepare test
		final File artefactDir = new File(infrastructure.getPluginInputDir(), ARTEFACT_BEAN_FACTORY);
		final File mainTemplateFile = new File(artefactDir, MAIN_TEMPLATE);
		mainTemplateFile.delete();

		// call functionality under test
		try {
			velocityModelBasedInserter.findMainTemplate(artefactDir);
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), TemplateUtil.NO_MAIN_TEMPLATE_FOUND);

			// cleanup
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
			
			return;
		}
		
		fail("Expected exception not thrown!");
		
	}

	@Test
	public void findsMainTemplate() {
		// prepare test
		final File artefactDir = new File(infrastructure.getPluginInputDir(), ARTEFACT_BEAN_FACTORY);

		// call functionality under test
		 String mainTemplate = null;
		try {
			mainTemplate = velocityModelBasedInserter.findMainTemplate(artefactDir);
		} catch (MogliPluginException e) {
			fail(e.getMessage());
		}
		
		// verify test result
		assertEquals("Main Template filename", MAIN_TEMPLATE, mainTemplate);
	}
	
	@Test
	public void unpacksInputDefaultData() throws MogliPluginException {
		// prepare test
		FileUtil.deleteDirWithContent(applicationInputDir);

		// call functionality under test
		velocityModelBasedInserter.unpackDefaultInputData();
		
		// verify test result
		assertFileExists(applicationInputDir);
	}
	
	@Test
	public void savesGeneratedContentInPluginOutputDir() throws MogliPluginException {
		// prepare test
		VelocityInserterResultData resultData = buildVelocityInserterResultData("Content", 
				"temp", TARGET_FILE_TXT, KnownGeneratorPropertyNames.CreateNew.name(), "true");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		final File targetFile = prepareTargetFile(applicationOutputDir, VelocityModelBasedInserterStarter.PLUGIN_ID 
                                                                        + "/" + ARTEFACT_BEAN_FACTORY 
                                                                        + "/" + TARGET_FILE_TXT);
		assertFileDoesNotExist(targetFile);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		assertFileExists(targetFile);
		assertFileContainsEntry(targetFile, "Content");
	}

	private BuildUpVelocityInserterResultData buildVelocityInserterResultData(final String content, 
			final String targetdir, final String targetFileName, 
			final String propertyName, final String propertyValue) {
		
		final BuildUpGeneratorResultData buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		buildUpGeneratorResultData.setGeneratedContent(content);
		
		final BuildUpVelocityInserterResultData toReturn = 
			   new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);
		
		if (targetFileName != null) {
			toReturn.addProperty(KnownGeneratorPropertyNames.TargetFileName.name(), targetFileName);
		}
		if (targetdir != null) {
			toReturn.addProperty(KnownGeneratorPropertyNames.TargetDir.name(), targetdir);
		}
		
		if (StringUtils.isNotBlank(propertyName) && StringUtils.isNotBlank(propertyValue)) {
			toReturn.addProperty(propertyName, propertyValue);
		}
		return toReturn;
	}

	@Test
	public void savesGeneratedContentInNewFileInTemplateTargetDir() throws MogliPluginException {
		// prepare test
		VelocityInserterResultData resultData = buildVelocityInserterResultData("Content", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownGeneratorPropertyNames.CreateNew.name(), "true");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		assertFileExists(targetFile);
		assertFileContainsEntry(targetFile, "Content");
	}

	private File prepareTargetFile(final File targetDir, final String filename) {
		final File targetFile = new File(targetDir, filename);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);
		return targetFile;
	}
	
	@Test
	public void throwsExceptionIfTargetDirFromTemplateFileIsNotFound() throws MogliPluginException {
		// prepare test
		VelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/FOO", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.InsertAbove.name(), "-InsertAbove");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		final File targetFile = prepareTargetFile(applicationTempDir, TARGET_FILE_TXT);
		assertFileDoesNotExist(targetFile);

		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), VelocityGeneratorResultData.TEXT_TARGET_DIR_NOT_FOUND);

			// cleanup
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
			
			return;
		}
		
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionIfTargetFileFromTemplateFileIsNotFound() throws MogliPluginException {
		// prepare test
		VelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.InsertAbove.name(), "-InsertAbove");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);

		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), VelocityGeneratorResultData.TEXT_TARGET_FILE_NOT_FOUND);

			// cleanup
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
			
			return;
		}
		
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void insertsGeneratedContentAboveMarkerInTemplateTargetDir() throws MogliPluginException {
		// prepare test
		velocityModelBasedInserter = new VelocityModelBasedInserterStarter();
		velocityModelBasedInserter.setMogliInfrastructure(createInfrastructure(new File(getProjectTestResourcesDir(), 
				"applicationTestInputDir")));
		final VelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.InsertAbove.name(), "-InsertAbove");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		final File expectedFile = new File(getProjectTestResourcesDir(), "expectedInsertAboveResultContent.txt");
		assertFileEquals(expectedFile, targetFile);
	}
	
	@Test
	public void insertsGeneratedContentBelowMarkerInTemplateTargetDir() throws MogliPluginException {
		// prepare test
		velocityModelBasedInserter = new VelocityModelBasedInserterStarter();
		velocityModelBasedInserter.setMogliInfrastructure(createInfrastructure(new File(getProjectTestResourcesDir(), "applicationTestInputDir")));
		VelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.InsertBelow.name(), "-InsertBelow");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		final File expectedFile = new File(getProjectTestResourcesDir(), "expectedInsertBelowResultContent.txt");
		assertFileEquals(expectedFile, targetFile);
	}

	@Test
	public void replacesGeneratedContentInTemplateTargetFile() throws MogliPluginException {
		// prepare test
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.ReplaceStart.name(), "-ReplaceStart");
		resultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "-ReplaceEnd");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		final File expectedFile = new File(getProjectTestResourcesDir(), "expectedReplaceResultContent.txt");
		assertFileEquals(expectedFile, targetFile);
	}
	
	@Test
	public void throwsExceptionIfReplaceStartIndicatorNotFound() throws MogliPluginException {
		// prepare test
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.ReplaceStart.name(), "-replStart");
		resultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "-ReplaceEnd");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), TextConstants.TEXT_START_REPLACE_INDICATOR_NOT_FOUND);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionIfReplaceEndIndicatorNotFound() throws MogliPluginException {
		// prepare test
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.ReplaceStart.name(), "-ReplaceStart");
		resultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "-replEnd");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), TextConstants.TEXT_END_REPLACE_INDICATOR_NOT_FOUND);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfInsertBelowIndicatorNotFound() throws MogliPluginException {
		// prepare test
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.InsertBelow.name(), "-BI");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), TextConstants.TEXT_INSERT_BELOW_INDICATOR_NOT_FOUND);
			return;
		}
		fail("Expected exception not thrown!");
	}


	@Test
	public void throwsExceptionIfInsertAboveIndicatorNotFound() throws MogliPluginException {
		// prepare test
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.InsertAbove.name(), "-AI");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), TextConstants.TEXT_INSERT_ABOVE_INDICATOR_NOT_FOUND);
			return;
		}
		fail("Expected exception not thrown!");
	}


	@Test
	public void throwsExceptionIfOutputFileNameIsMissingInTemplateFile() throws MogliPluginException {
		// prepare test
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				null, null, null);
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), VelocityGeneratorResultData.NO_TARGET_FILE_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionIfOutputDirIsMissingInTemplateFile() throws MogliPluginException {
		// prepare test
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				null, TARGET_FILE_TXT, null, null);
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		assertFileExists(targetFile);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), VelocityGeneratorResultData.NO_TARGET_DIR);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void createsNotExistingTargetDirWithCreateNewInstructions() throws MogliPluginException {
		// prepare test
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				targetDir, TARGET_FILE_TXT, 
				KnownGeneratorPropertyNames.CreateNew.name(), "true");
		resultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "true");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		final File dir = new File(targetDir);
		FileUtil.deleteDirWithContent(dir);
		assertFileDoesNotExist(dir);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		assertFileExists(dir);
	}

	@Test
	public void createsNotExistingTargetFileDirWithoutCreateNewInstruction() throws MogliPluginException {
		// prepare test
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				targetDir, TARGET_FILE_TXT, null, null);
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		final File targetDirAsFile = new File(targetDir);
		targetDirAsFile.mkdirs();
		final File targetFile = new File(targetDir, TARGET_FILE_TXT);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		assertFileExists(targetFile);
	}

	@Test
	public void createsExistingTargetFileNewlyWithCreateNewInstructions() throws Exception {
		// prepare test
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		final String content = "ContentToInsert";
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData(content, 
				targetDir, TARGET_FILE_TXT, 
				KnownGeneratorPropertyNames.CreateNew.name(), "true");
		resultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "true");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		targetFile = new File(targetDir, TARGET_FILE_TXT);
		FileUtil.createNewFileWithContent(targetFile, "Test");
		assertFileDoesNotContainEntry(targetFile, "ContentToInsert");

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		assertFileContainsEntry(targetFile, "ContentToInsert");
	}

	@Test
	public void createsTargetFileWithRootDirDefinedInTemplateFile() throws MogliPluginException {
		// prepare test
		final String targetDir = VelocityGeneratorResultData.ROOT_IDENTIFIER + "/example";
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				targetDir, TARGET_FILE_TXT, 
				KnownGeneratorPropertyNames.CreateNew.name(), "true");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		targetFile = new File(targetDir, TARGET_FILE_TXT);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);

		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		final File file = new File(PROJECT_ROOT_DIR + TEST_SUBDIR + "/example", TARGET_FILE_TXT);
		assertFileExists(file);
	}
	
	@Test
	public void doesNotOverwriteExistingTargetFile() throws Exception {
		// prepare test
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		BuildUpVelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				targetDir, TARGET_FILE_TXT, 
				KnownGeneratorPropertyNames.CreateNew.name(), "false");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		final File targetFile = new File(targetDir, TARGET_FILE_TXT);
		FileUtil.createNewFileWithContent(targetFile, "Test");
		assertFileContainsEntry(targetFile, "Test");
		
		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// verify test result
		assertFileContainsEntry(targetFile, "Test");
	}
	
	@Test
	public void throwsExceptionIfSubdirContainsNoMainTemplate() {
		// prepare test
		final File subdir = new File(infrastructure.getPluginInputDir(), ".svn");
		subdir.mkdirs();
		assertFileExists(subdir);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), "No main template found");
			return;
		}
		fail("Expected exception not thrown!");
	}

	
	@Test
	public void throwsExceptionIfSubdirContainsNoMainTemplateAndIgnorePropertyIsWrong() {
		// prepare test
		final File subdir = new File(infrastructure.getPluginInputDir(), ".svn");
		subdir.mkdirs();
		assertFileExists(subdir);
		MogliFileUtil.createNewFileWithContent(generatorPropertiesFile, ".svn=doNOT" + ArtefactListUtil.IGNORE);
		
		// call functionality under test
		try {
			velocityModelBasedInserter.doYourJob();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), "No main template found");
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void ignoresSubdirAsArtefactIfDefinedInPluginPropertiesFile() throws MogliPluginException {
		// prepare test
		final File subdir = new File(infrastructure.getPluginInputDir(), ".svn");
		subdir.mkdirs();
		assertFileExists(subdir);
		MogliFileUtil.createNewFileWithContent(generatorPropertiesFile, ".svn=" + ArtefactListUtil.IGNORE);
		final VelocityInserterResultData resultData = buildVelocityInserterResultData("ContentToInsert", 
				PROJECT_ROOT_DIR + TEST_SUBDIR + "/temp", 
				TARGET_FILE_TXT, KnownInserterPropertyNames.InsertAbove.name(), "-InsertAbove");
		velocityEngineProvider.setVelocityInserterResultData(resultData);
		
		// call functionality under test
		velocityModelBasedInserter.doYourJob();
		
		// cleanup
		subdir.delete();
		assertFileDoesNotExist(subdir);
		
		// verify test result
		final File outputDir = new File(infrastructure.getPluginOutputDir(), ".svn");
		assertFileDoesNotExist(outputDir);
	}

}
