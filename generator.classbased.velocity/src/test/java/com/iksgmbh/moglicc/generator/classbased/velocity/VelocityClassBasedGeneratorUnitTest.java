package com.iksgmbh.moglicc.generator.classbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.generator.classbased.velocity.test.VelocityClassBasedGeneratorTestParent;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil2;
import com.iksgmbh.utils.FileUtil;

public class VelocityClassBasedGeneratorUnitTest extends VelocityClassBasedGeneratorTestParent {
	
	private static final String MAIN_TEMPLATE = "A_MainTemplate.tpl";
	
	private File generatorPropertiesFile;

	@Override
	@Before
	public void setup() {
		super.setup();
		applicationTempDir.mkdirs();
		generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), 
		            VelocityClassBasedGeneratorStarter.PLUGIN_PROPERTIES_FILE);
		MOGLiFileUtil2.createNewFileWithContent(generatorPropertiesFile, "");
	}
	
	@Test
	public void findsArtefactList() throws MOGLiPluginException2 {
		// call functionality under test
		final List<String> artefactList = velocityClassBasedGenerator.getArtefactList();

		// verify test result
		assertEquals("artefact number", 5, artefactList.size());
	}
	
	@Test
	public void throwsExceptionIfMainTemplateIsNotFound() {
		// prepare test
		final File artefactDir = new File(infrastructure.getPluginInputDir(), VelocityClassBasedGeneratorStarter.ARTEFACT_JAVABEAN);
		final File mainTemplateFile = new File(artefactDir, MAIN_TEMPLATE);
		mainTemplateFile.delete();

		// call functionality under test
		try {
			velocityClassBasedGenerator.findMainTemplate(artefactDir);
		} catch (MOGLiPluginException2 e) {
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
		final File artefactDir = new File(infrastructure.getPluginInputDir(), VelocityClassBasedGeneratorStarter.ARTEFACT_JAVABEAN);

		// call functionality under test
		 String mainTemplate = null;
		try {
			mainTemplate = velocityClassBasedGenerator.findMainTemplate(artefactDir);
		} catch (MOGLiPluginException2 e) {
			fail(e.getMessage());
		}
		
		// verify test result
		assertEquals("Main Template filename", MAIN_TEMPLATE, mainTemplate);
	}
	
	@Test
	public void unpacksInputDefaultData() throws MOGLiPluginException2 {
		// prepare test
		FileUtil.deleteDirWithContent(applicationInputDir);

		// call functionality under test
		velocityClassBasedGenerator.unpackDefaultInputData();
		
		// verify test result
		assertFileExists(applicationInputDir);
	}
	
	@Test
	public void savesTargetFilesInPluginOutputDir() throws MOGLiPluginException2 {
		// prepare test
		final String targetFileName = "targetFile.txt";
		VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "temp", "Content", true);
		prepareResultData(resultData);
		final File targetFile = prepareTargetFile(applicationOutputDir, VelocityClassBasedGeneratorStarter.PLUGIN_ID 
                                                                        + "/" + VelocityClassBasedGeneratorStarter.ARTEFACT_JAVABEAN 
                                                                        + "/" + targetFileName);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();
		
		// verify test result
		assertFileExists(targetFile);
		assertFileContainsEntry(targetFile, "Content");
	}

	private VelocityGeneratorResultData buildVelocityGeneratorResultData(final String targetFileName, 
			final String targetdir, final String content, final boolean createNew) {
		final BuildUpGeneratorResultData buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		buildUpGeneratorResultData.setGeneratedContent(content);
		final BuildUpVelocityGeneratorResultData toReturn = 
			   new BuildUpVelocityGeneratorResultData(buildUpGeneratorResultData);
		
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
	public void savesTargetFilesInTargetDirReadFromTemplateFile() throws MOGLiPluginException2 {
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
	public void throwsExceptionIfOutputFileNameIsMissingInTemplateFile() throws MOGLiPluginException2 {
		// prepare test
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(null, "temp", "Content", false);
		prepareResultData(resultData);
		
		// call functionality under test
		try {
			velocityClassBasedGenerator.doYourJob();
		} catch (MOGLiPluginException2 e) {
			assertStringContains(e.getMessage(), VelocityGeneratorResultData.NO_TARGET_FILE_NAME);
			return;
		}
		fail("Expected exception not thrown!");
	}
	

	@Test
	public void createsNotExistingTargetDirWithCreateNewInstructions() throws MOGLiPluginException2 {
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
	public void createsNotExistingTargetFileDirWithoutCreateNewInstruction() throws MOGLiPluginException2 {
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
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, targetDir, "ContentToInsert", true);
		prepareResultData(resultData);
		final File targetFile = new File(targetDir, targetFileName);
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
		final String targetDir = PROJECT_ROOT_DIR + TEST_SUBDIR + "/example";
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, 
				                                       targetDir, "ContentToInsert", false);
		prepareResultData(resultData);
		final File targetFile = new File(targetDir, targetFileName);
		FileUtil.createNewFileWithContent(targetFile, "Test");
		assertFileContainsEntry(targetFile, "Test");
		velocityClassBasedGenerator.setTestDir(null);
		
		// call functionality under test
		velocityClassBasedGenerator.doYourJob();
		
		// verify test result
		assertFileContainsEntry(targetFile, "Test");
	}

	@Test
	public void createsTargetFileWithRootDirDefinedInTemplateFile() throws MOGLiPluginException2 {
		// prepare test
		final String targetDir = VelocityGeneratorResultData.ROOT_IDENTIFIER + "/example";
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "temp", "Content", false);
		prepareResultData(resultData);
		final File targetFile = new File(targetDir, targetFileName);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();
		
		// verify test result
		final File file = new File(PROJECT_ROOT_DIR + TEST_SUBDIR + "/example", targetFileName);
		assertFileExists(file);
	}
	
	@Test
	public void generatesTargetFileInPackageSubDirThatAlreadyExists() throws MOGLiPluginException2 {
		// prepare test
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "example/"
				                                          + VelocityGeneratorResultData.PACKAGE_IDENTIFIER, 
				"package com.iksgmbh.test;", false);
		prepareResultData(resultData);
		final File targetDirAsFile = new File(PROJECT_ROOT_DIR + TEST_SUBDIR + "/example/com/iksgmbh/test");
		targetDirAsFile.mkdirs();
		final File targetFile = new File(targetDirAsFile, targetFileName);
		targetFile.delete();
		assertFileDoesNotExist(targetFile);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();
		
		// verify test result
		assertFileExists(targetFile);
	}

	@Test
	public void generatesTargetFileInPackageSubDirThatMustBeCreated() throws MOGLiPluginException2 {
		// prepare test
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "example/<package>", 
				"package com.iksgmbh.test;", true);
		prepareResultData(resultData);
		final File dir = new File(PROJECT_ROOT_DIR + TEST_SUBDIR + "/example");
		FileUtil.deleteDirWithContent(dir);
		assertFileDoesNotExist(dir);

		// call functionality under test
		velocityClassBasedGenerator.doYourJob();
		
		// verify test result
		final File targetFile = new File(dir, "com/iksgmbh/test");
		assertFileExists(targetFile);
	}
	
	@Test
	public void throwsExceptionIfPackageForTargetDirIsNotContainedInGeneratedContent() throws MOGLiPluginException2 {
		// prepare test
		final String targetFileName = "targetFile.txt";
		final VelocityGeneratorResultData resultData = buildVelocityGeneratorResultData(targetFileName, "example/<package>", 
				"package com.iksgmbh.test", true);
		prepareResultData(resultData);
		
		// call functionality under test
		try {
			velocityClassBasedGenerator.doYourJob();
		} catch (MOGLiPluginException2 e) {
			assertStringContains(e.getMessage(), VelocityGeneratorResultData.TEXT_PACKAGE_NOT_FOUND);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void ignoresSubdirAsArtefactIfDefinedInPluginPropertiesFile() throws MOGLiPluginException2 {
		// prepare test
		final File subdir = new File(infrastructure.getPluginInputDir(), ".svn");
		subdir.mkdirs();
		assertFileExists(subdir);
		MOGLiFileUtil2.createNewFileWithContent(generatorPropertiesFile, ".svn=" + ArtefactListUtil.IGNORE);
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
}
