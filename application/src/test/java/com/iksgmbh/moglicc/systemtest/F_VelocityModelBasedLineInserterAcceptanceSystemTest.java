package com.iksgmbh.moglicc.systemtest;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.lineinserter.modelbased.velocity.VelocityModelBasedLineInserterStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class F_VelocityModelBasedLineInserterAcceptanceSystemTest extends __AbstractSystemTest {

	private static final String LINEINSERTER_TEST_EXPECTED_FILE = "expectedInserterResult.txt";
	public static final String LINEINSERTER_PLUGIN_ID = VelocityModelBasedLineInserterStarter.PLUGIN_ID;

	@Before
	@Override
	public void setup() {
		super.setup();
		applicationTempDir.mkdirs();
	}

	@Test
	public void createsPluginLogFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationLogDir);
		final File pluginLogFile = new File(applicationLogDir, LINEINSERTER_PLUGIN_ID + ".log");
		assertFileDoesNotExist(pluginLogFile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(pluginLogFile);
	}


	@Test
	public void insertsInTargetFile() {
		// prepare test
		final File targetfile = new File(applicationRootDir, "example/BeanFactory.java");
		targetfile.delete();
		assertFileDoesNotExist(targetfile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		final File expectedFile = new File(getProjectTestResourcesDir(), LINEINSERTER_TEST_EXPECTED_FILE);
		assertFileEquals(expectedFile, targetfile);
	}

	@Test
	public void createsHelpData() {
		// prepare test
		final File pluginHelpDir = new File(applicationHelpDir, LINEINSERTER_PLUGIN_ID);
		FileUtil.deleteDirWithContent(applicationHelpDir);
		assertFileDoesNotExist(pluginHelpDir);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(pluginHelpDir);
		assertChildrenNumberInDirectory(pluginHelpDir, 1);
	}

	@Test
	public void createsOutputFilesWithASCIIEncodingReadFromMainTemplate() throws Exception {
		final File templateFile = prepareArtefactDirectory("main.tpl", LINEINSERTER_PLUGIN_ID, "myNewArtefact");
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName UmlautTest.txt" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                "@OutputEncodingFormat ASCII" + FileUtil.getSystemLineSeparator() + "äüößÜÖÄ");

		// call functionality under test
		executeMogliApplication();

		// verify test result
		final File outputFile = new File(applicationOutputDir, LINEINSERTER_PLUGIN_ID + "/myNewArtefact/UmlautTest.txt");
		assertStringEquals("outputFileContent", "???????", MOGLiFileUtil.getFileContent(outputFile));
	}
	
	@Test
	public void createsOutputFilesWithDefaultEncodingReadFromMainTemplate() throws Exception {
		final File templateFile = prepareArtefactDirectory("main.tpl", LINEINSERTER_PLUGIN_ID, "myNewArtefact");

		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName UmlautTest.txt" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                "äüößÜÖÄ");

		// call functionality under test
		executeMogliApplication();

		// verify test result
		final File outputFile = new File(applicationOutputDir, LINEINSERTER_PLUGIN_ID + "/myNewArtefact/UmlautTest.txt");
		assertStringEquals("outputFileContent", "äüößÜÖÄ", MOGLiFileUtil.getFileContent(outputFile));
	}
}
