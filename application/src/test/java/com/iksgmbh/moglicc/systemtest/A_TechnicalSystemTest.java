package com.iksgmbh.moglicc.systemtest;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_APPLICATION_PROPERTIES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_LOG_FILE;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_REPORT_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.build.MOGLiReleaseBuilder;
import com.iksgmbh.utils.FileUtil;

public class A_TechnicalSystemTest extends _AbstractSystemTest {

	private Properties buildProperties;

	@Before
	@Override
	public void setup() {
		super.setup();
		prepareTestDir();
		try {
			buildProperties = MOGLiReleaseBuilder.readBuildPropertiesFile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@After
	@Override
	public void teardown() {
		super.teardown();
	}

	// *****************************  test methods  ************************************

	@Test
	public void assertInitialFileStructure() {
		assertTrue("TestsDir does not exist!", testDir.exists());
		assertEquals("File number in root", 3, testDir.listFiles().length);

		final File libDir = FileUtil.getSubDir(testDir, "lib");
		assertEquals("File number in lib subdir", getNumberOfJarFilesToExpectInLibDir() + 1, libDir.listFiles().length);

		final File pluginsDir = FileUtil.getSubDir(libDir, "plugins");
		assertEquals("File number in plugins subdir", getNumberOfJarFilesToExpectPluginsDir(), pluginsDir.listFiles().length);
	}

	private int getNumberOfJarFilesToExpectPluginsDir() {
		return MOGLiReleaseBuilder.PLUGIN_MODULES.size();
	}

	protected int getNumberOfJarFilesToExpectInLibDir() {
		return MOGLiReleaseBuilder.THIRD_PARTY_LIBRARIES.size() +
		       MOGLiReleaseBuilder.CORE_MODULES.size();
	}

	@Test
	public void createsDefaultApplicationPropertiesFile() {
		// prepare test
		final File propertiesFile = new File(testDir, FILENAME_APPLICATION_PROPERTIES);
		assertFileDoesNotExist(propertiesFile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(propertiesFile);
	}


	@Test
	public void createsMogliLogfile() {
		// prepare test
		assertFileDoesNotExist(applicationLogDir);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(applicationLogDir);
		assertFileExists(applicationLogfile);
	}

	@Test
	public void countNumberFoundPluginsInMogliLogfile() {
		// prepare test
		applicationLogfile.delete();
		assertFileDoesNotExist(applicationLogfile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(applicationLogfile);
		assertEquals("Number Plugins", MOGLiReleaseBuilder.PLUGIN_MODULES.size(), countMatchesInContainedFile(applicationLogfile, "PluginMetaData"));
	}


	@Test
	public void logsCurrentVersionString() {
		// prepare test
		applicationLogfile.delete();
		assertFileDoesNotExist(applicationLogfile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(applicationLogfile);
		final String expectedVersionString = buildProperties.getProperty(MOGLiReleaseBuilder.PROPERTY_RELEASE_VERSION);
		assertFileContainsEntry(applicationLogfile, expectedVersionString);
	}

	@Test
	public void createsNewWorkspaceDirWithRelativePathToSubdir() {
		// prepare test
		initApplicationPropertiesWith("workspace=workspaces/demo");
		final File workspaceDir = new File(applicationRootDir, "workspaces/demo");
		assertFileDoesNotExist(workspaceDir.getParentFile());

		// call functionality under test
		executeMogliApplication();

		// cleanup critical stuff before possible test failures
		applicationPropertiesFile.delete();
		assertFileDoesNotExist(applicationPropertiesFile);

		// verify test result
		assertFileExists(workspaceDir);
		final File workspaceLogDir = new File(workspaceDir, MOGLiSystemConstants.DIR_LOGS_FILES);
		assertChildrenNumberInDirectory(workspaceLogDir, 5);
		final File workspaceInputDir = new File(workspaceDir, MOGLiSystemConstants.DIR_INPUT_FILES);
		assertChildrenNumberInDirectory(workspaceInputDir, 3);
		final File workspaceOutputDir = new File(workspaceDir, MOGLiSystemConstants.DIR_OUTPUT_FILES);
		assertChildrenNumberInDirectory(workspaceOutputDir, 3);

		// cleanup
		FileUtil.deleteDirWithContent(workspaceDir.getParentFile());
	}

	@Test
	public void createsEmergencyLogFileIfDefinedWorkspaceDirCannotBeCreated() {
		// prepare test
		initApplicationPropertiesWith("workspace=");
		final File emergencyLogFile = new File(applicationRootDir, FILENAME_LOG_FILE);
		assertFileDoesNotExist(emergencyLogFile);

		// call functionality under test
		executeMogliApplication();

		// cleanup critical stuff before possible test failures
		applicationPropertiesFile.delete();
		assertFileDoesNotExist(applicationPropertiesFile);

		// verify test result
		assertFileExists(emergencyLogFile);
		assertFileContainsEntry(emergencyLogFile, "ERROR: Error creating workspaceDir");
	}

	@Test
	public void createsMOGLiCCMainHelpFile() {
		// call functionality under test
		executeMogliApplication();

		// cleanup critical stuff before possible test failures
		final File mainHelpFile = new File(applicationHelpDir, MOGLiSystemConstants.FILENAME_INTRODUCTION_HELPFILE);
		assertFileExists(mainHelpFile);
	}

	@Test
	public void executesAllPluginsSuccessfully() {
		// call functionality under test
		executeMogliApplication();

		// cleanup critical stuff before possible test failures
		assertFileContainsEntry(applicationLogfile, "All " + getNumberOfJarFilesToExpectPluginsDir()
				                                   + " plugins executed successfully");
	}
	
	@Test
	public void createsReportFile() {
		// prepare test
		final File reportFile = new File(testDir, FILENAME_REPORT_FILE);
		assertFileDoesNotExist(reportFile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(reportFile);
	}

}
