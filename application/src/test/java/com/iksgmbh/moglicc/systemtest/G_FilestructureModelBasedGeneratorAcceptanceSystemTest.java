package com.iksgmbh.moglicc.systemtest;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.iksgmbh.moglicc.generator.modelbased.filestructure.FilestructureModelBasedGeneratorStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class G_FilestructureModelBasedGeneratorAcceptanceSystemTest extends __AbstractSystemTest {

	public static final String GENERATOR_PLUGIN_ID = FilestructureModelBasedGeneratorStarter.PLUGIN_ID;
	private static final String PLACEHOLDER = "<placeholder>";
	private static final String UMLAUT_TEST_FILE = "UmlautTestFile.txt";


	// *****************************  test methods  ************************************

	@Test
	public void createsPluginLogFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationInputDir);
		FileUtil.deleteDirWithContent(applicationLogDir);
		final File pluginLogFile = new File(applicationLogDir, GENERATOR_PLUGIN_ID + ".log");
		assertFileDoesNotExist(pluginLogFile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(pluginLogFile);
	}

	@Test
	public void createsGeneratorResultFiles() throws IOException {
		// prepare test
		FileUtil.deleteDirWithContent(applicationOutputDir);
		assertFileDoesNotExist(applicationOutputDir);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(applicationOutputDir);
		assertAllResultFileCreated();
	}

	private void assertAllResultFileCreated() throws IOException {
		final File resultDir = new File(applicationRootDir, FilestructureModelBasedGeneratorStarter.MOGLICC_JAVA_BEAN_PROJECT);
		assertChildrenNumberInDirectory(resultDir, 2);		
		
		final File demoDir = new File(resultDir, "src/main/java/com/iksgmbh/moglicc/demo");
		assertChildrenNumberInDirectory(demoDir, 14);
		final File validatorDir = new File(demoDir, "validator");
		assertChildrenNumberInDirectory(validatorDir, 5);
		final File typesDir = new File(validatorDir, "types");
		assertChildrenNumberInDirectory(typesDir, 3);
		final File resourcesDir = new File(resultDir, "src/main/resources");
		assertFileExists(resourcesDir);
		
		final File testDemoDir = new File(resultDir, "src/test/java/com/iksgmbh/moglicc/demo");
		assertChildrenNumberInDirectory(testDemoDir, 8);
		final File testResourcesDir = new File(resultDir, "src/test/resources");
		assertFileExists(testResourcesDir);		
	}

	@Test
	public void createsHelpData() {
		// prepare test
		final File pluginHelpDir = new File(applicationHelpDir, GENERATOR_PLUGIN_ID);
		FileUtil.deleteDirWithContent(applicationHelpDir);
		assertFileDoesNotExist(pluginHelpDir);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(pluginHelpDir);
		assertChildrenNumberInDirectory(pluginHelpDir, 1);
	}

	@Test
	public void createsOutputFilesWithASCIIEncodingReadFromArtefactProperties() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareArtefactDirectory("artefact.properties", GENERATOR_PLUGIN_ID, "umlautTestArtefact");
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName FileStructureGeneratorUmlautTest" + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@OutputEncodingFormat ASCII" + FileUtil.getSystemLineSeparator() +
        		"@ReplaceIn " + UMLAUT_TEST_FILE + " " + PLACEHOLDER + " äöüßÄÖÜ");
		final File contentFile = new File(artefactPropertiesFile.getParentFile(), "UmlautTestFile.txt");
		MOGLiFileUtil.createNewFileWithContent(contentFile, PLACEHOLDER);
		
		// call functionality under test
		executeMogliApplication();

		// verify test result
		final File outputFile = new File(applicationRootDir, "FileStructureGeneratorUmlautTest/UmlautTestFile.txt");
		assertStringEquals("outputFileContent", "???????", MOGLiFileUtil.getFileContent(outputFile));
	}

	@Test
	public void createsOutputFilesWithDefaultEncodingReadFromArtefactProperties() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareArtefactDirectory("artefact.properties", GENERATOR_PLUGIN_ID, "umlautTestArtefact");
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName FileStructureGeneratorUmlautTest" + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
        		"@ReplaceIn " + UMLAUT_TEST_FILE + " " + PLACEHOLDER + " äöüßÄÖÜ");
		final File contentFile = new File(artefactPropertiesFile.getParentFile(), "UmlautTestFile.txt");
		MOGLiFileUtil.createNewFileWithContent(contentFile, PLACEHOLDER);
		
		// call functionality under test
		executeMogliApplication();

		// verify test result
		final File outputFile = new File(applicationRootDir, "FileStructureGeneratorUmlautTest/UmlautTestFile.txt");
		assertStringEquals("outputFileContent", "äöüßÄÖÜ", MOGLiFileUtil.getFileContent(outputFile));
	}

	@Test
	public void renamesFileWithNameReadFromArtefactProperties() throws Exception {
		// prepare test		
		final String targetDir = "RenamingTestResult";
		final String artefactName = "RenamingTestArtefact";
		final String filenameToReplace = "ToReplace.txt";
		final String filenameReplaced = "Replaced.txt";
		final String contentToReplace = "new content";
		
		// create test artefact
		final File artefactPropertiesFile = prepareArtefactDirectory("artefact.properties", GENERATOR_PLUGIN_ID, artefactName);
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName " + targetDir + " " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@RenameFile " + filenameToReplace + " " + filenameReplaced + FileUtil.getSystemLineSeparator() +
                "@ReplaceIn " + filenameReplaced + " " + PLACEHOLDER + " " + contentToReplace + FileUtil.getSystemLineSeparator());
		final File fileToRename = new File(applicationInputDir, GENERATOR_PLUGIN_ID + "/" + artefactName + "/" + filenameToReplace );
		MOGLiFileUtil.createNewFileWithContent(fileToRename, PLACEHOLDER);
		assertFileExists(fileToRename);
		
		// create test model
		modelPropertiesFile.getParentFile().mkdirs();
		MOGLiFileUtil.createNewFileWithContent(modelTextfile, "model FileStructureRenamingTest" + FileUtil.getSystemLineSeparator() +
                "  metainfo filename RenamingTest.txt" + FileUtil.getSystemLineSeparator() + 
                FileUtil.getSystemLineSeparator() +
                "class de.Test" + FileUtil.getSystemLineSeparator());
				
		// call functionality under test
		executeMogliApplication();
		
		// cleanup
		FileUtil.deleteDirWithContent(modelPropertiesFile.getParent());

		// verify test result
		File artefactFile = new File(applicationRootDir, targetDir + "/" + filenameToReplace );
		assertFileDoesNotExist(artefactFile);
		artefactFile = new File(applicationRootDir, targetDir + "/" + filenameReplaced );
		assertFileExists(artefactFile);
		final String content = MOGLiFileUtil.getFileContent(artefactFile);
		assertStringEquals("File content", contentToReplace, content);
	}
}
