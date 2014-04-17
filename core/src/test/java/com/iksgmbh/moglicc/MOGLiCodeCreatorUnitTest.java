package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LOGS_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_OUTPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_APPLICATION_PROPERTIES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_WORKSPACE_PROPERTIES;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_APPLICATION_TERMINATED;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DEACTIVATED_PLUGIN_INFO;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DUPLICATE_PLUGINIDS;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_INFOMESSAGE_OK;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NOTHING_TO_DO;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_MANIFEST_FOUND;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_STARTERCLASS_UNKNOWN;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_STARTERCLASS_WRONG_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.FileUtil;

public class MOGLiCodeCreatorUnitTest extends CoreTestParent {

	private MOGLiCodeCreator mogliCodeCreator;

	@Before
	public void setup() 
	{
		MOGLiLogUtil.setCoreLogfile(null);
		super.setup();  // this recreates the same logfile and stores a reference to it in the parent class
		mogliCodeCreator = new MOGLiCodeCreator();  // this creates new logfile
		initProperties();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// *****************************  test methods  ************************************

	@Test
	public void cleanupOnInstantiation() throws IOException {
		// prepare test
		final File logsDir = MOGLiFileUtil.getNewFileInstance(DIR_LOGS_FILES);
		createFileIn(logsDir);
		assertChildrenNumberInDirectory(logsDir, 2);
		final File resultDir = MOGLiFileUtil.getNewFileInstance(DIR_OUTPUT_FILES);
		createFileIn(resultDir);
		assertChildrenNumberInDirectory(resultDir, 1);

		// call functionality under test
		mogliCodeCreator = new MOGLiCodeCreator();

		// verify test result
		assertChildrenNumberInDirectory(logsDir, 1);
		assertChildrenNumberInDirectory(resultDir, 0);
	}

	private File createFileIn(final File dir) throws IOException {
		dir.mkdir();
		final File file = new File(dir, "Test.txt");
		file.createNewFile();
		return file;
	}

	@Test
	public void createsMogliLogFile() {
		// prepare test
		String applicationRootDir = MOGLiCodeCreator.getApplicationRootDir();
		initMogliWithNotExistingLogfile();
		assertFileDoesNotExist(applicationLogfile);

		// call functionality under test
		mogliCodeCreator.doYourJob();

		// verify test result
		assertFileExists(applicationLogfile);
		assertFileContainsEntry(applicationLogfile, TEXT_NOTHING_TO_DO);

		// cleanup
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);
	}

	private void initMogliWithNotExistingLogfile() {
		final String dirname = getProjectRootDir() + TARGET_DIR + "xyz";
		MOGLiCodeCreator.setApplicationRootDir(dirname);
		final File dir = new File(dirname);
		dir.mkdirs();
		mogliCodeCreator = new MOGLiCodeCreator();
		applicationLogfile = MOGLiCodeCreator.getLogFile();
		applicationLogfile.delete();
	}

	@Test
	public void terminatesTwoPluginWithSameId() throws FileNotFoundException, IOException {
		// prepare test
		activatePluginsForTest("DummyPluginStarter", "DummyPluginStarter2");
		mogliCodeCreator = new MOGLiCodeCreator();  // read properties again

		// call functionality under test
		mogliCodeCreator.doYourJob();

		// verify test result
		final String expectedLogEntry = TEXT_DUPLICATE_PLUGINIDS + "DummyPlugin";

		final String expectedLogFileEnding = TEXT_APPLICATION_TERMINATED + expectedLogEntry;
		final String actualFileContent = MOGLiFileUtil.getFileContent(applicationLogfile);
		assertTrue("Unexpected logfile ending!" + actualFileContent, actualFileContent.endsWith(expectedLogFileEnding));
	}

	@Test
	public void handlePluginWithNotExistingStarterClass() {
		// prepare test
		activatePluginsForTest("pluginWithoutExistingStarterClass");
		mogliCodeCreator = new MOGLiCodeCreator();  // read properties again

		// call functionality under test
		mogliCodeCreator.doYourJob();

		// verify test result
		List<PluginMetaData> pluginMetaDataList = mogliCodeCreator.getPluginMetaDataList();
		assertPluginData(pluginMetaDataList, "pluginWithoutExistingStarterClass.jar", PluginStatus.ANALYSED,
				"notExistingStarterClass", TEXT_STARTERCLASS_UNKNOWN);
	}

	@Test
	public void executesPluginSuccessfully() {
		// prepare test
		activatePluginsForTest("DummyStandardModelProviderStarter");
		mogliCodeCreator = new MOGLiCodeCreator();  // read properties again

		// call functionality under test
		mogliCodeCreator.doYourJob();

		// verify test result
		List<PluginMetaData> pluginMetaDataList = mogliCodeCreator.getPluginMetaDataList();
		assertPluginData(pluginMetaDataList, "DummyStandardModelProviderStarter.jar", PluginStatus.EXECUTED,
				"com.iksgmbh.moglicc.test.starterclasses.DummyStandardModelProviderStarter", TEXT_INFOMESSAGE_OK);
	}

	@Test
	public void handlesPluginExecutedWithError() {
		// prepare test
		activatePluginsForTest("DummyPluginThrowsRuntimeExceptionStarter");
		mogliCodeCreator = new MOGLiCodeCreator();  // read properties again

		// call functionality under test^
		mogliCodeCreator.doYourJob();

		// verify test result
		List<PluginMetaData> pluginMetaDataList = mogliCodeCreator.getPluginMetaDataList();
		assertPluginData(pluginMetaDataList, "DummyPluginThrowsRuntimeExceptionStarter.jar", PluginStatus.LOADED,
				"com.iksgmbh.moglicc.test.starterclasses.DummyPluginThrowsRuntimeExceptionStarter",
				"UNEXPECTED PROBLEM: RuntimeException: fatal");
	}

	@Test
	public void logsPluginMetaData() {
		// prepare test
		activatePluginsForTest("DummyPluginThrowsRuntimeExceptionStarter",
							   "DummyStandardModelProviderStarter",
							   "DummyDataProviderStarter",
							   "pluginWithoutExistingStarterClass",
							   "pluginWithoutPropertiesFile",
							   "pluginWithoutStarterClassEntry",
				               "PluginWithStarterClassWithMissingInterface");
		mogliCodeCreator = new MOGLiCodeCreator();

		// call functionality under test
		mogliCodeCreator.doYourJob();

		// verify test result
		assertFileContainsEntry(applicationLogfile, "Plugins found:");
		assertFileContainsEntry(applicationLogfile, "jarName=DummyPluginThrowsRuntimeExceptionStarter.jar, id=DummyPlugin, pluginType=GENERATOR, status=LOADED, infoMessage=UNEXPECTED PROBLEM: RuntimeException: fatal");
		assertFileContainsEntry(applicationLogfile, "jarName=DummyStandardModelProviderStarter.jar, id=StandardModelProvider, pluginType=PROVIDER, status=EXECUTED, infoMessage=" + TEXT_INFOMESSAGE_OK);
		assertFileContainsEntry(applicationLogfile, "jarName=DummyDataProviderStarter.jar, id=DummyDataProvider, pluginType=PROVIDER, status=EXECUTED, infoMessage=" + TEXT_INFOMESSAGE_OK);
		assertFileContainsEntry(applicationLogfile, "jarName=DummyPluginStarter.jar, id=null, pluginType=null, status=ANALYSED, infoMessage=" + TEXT_DEACTIVATED_PLUGIN_INFO);
		assertFileContainsEntry(applicationLogfile, "jarName=DummyPluginStarter2.jar, id=null, pluginType=null, status=ANALYSED, infoMessage=" + TEXT_DEACTIVATED_PLUGIN_INFO);
		assertFileContainsEntry(applicationLogfile, "jarName=pluginWithoutExistingStarterClass.jar, id=null, pluginType=null, status=ANALYSED, infoMessage=" + TEXT_STARTERCLASS_UNKNOWN);
		assertFileContainsEntry(applicationLogfile, "jarName=pluginWithoutPropertiesFile.jar, id=null, pluginType=null, status=ANALYSED, infoMessage=" + TEXT_NO_MANIFEST_FOUND);
		assertFileContainsEntry(applicationLogfile, "jarName=pluginWithoutStarterClassEntry.jar, id=null, pluginType=null, status=ANALYSED, infoMessage=" + TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE);
		assertFileContainsEntry(applicationLogfile, "jarName=PluginWithStarterClassWithMissingInterface.jar, id=null, pluginType=null, status=ANALYSED, infoMessage=" + TEXT_STARTERCLASS_WRONG_TYPE);
	}

	@Test
	public void countNumberOfActivePlugins() {
		// prepare test
		final List<PluginMetaData> pluginMetaDataList = getPluginMetaDataListForTest();
		pluginMetaDataList.get(0).setInfoMessage(TEXT_DEACTIVATED_PLUGIN_INFO);

		// call functionality under test
		final int numberOfActivePlugins = mogliCodeCreator.getNumberOfPluginsToLoad(pluginMetaDataList);

		// verify test result
		assertEquals("Unexpected number of active plugins", 1, numberOfActivePlugins);
	}


	@Test
	public void createsApplicationPropertiesFileIfDoesNotExist() throws IOException {
		// prepare test
		initApplicationPropertiesWith("");
		boolean delete = applicationPropertiesFile.delete();
		assertTrue("Could not delete " + applicationPropertiesFile.getAbsolutePath(), delete);

		// call functionality under test
		mogliCodeCreator.checkApplicationPropertiesFile();

		// verify test result
		final String fileContent = MOGLiFileUtil.getFileContent(applicationPropertiesFile).trim();
		final File defaultPropertiesFile = new File(getProjectResourcesDir(), 
				                                    MOGLiCodeCreator.PROPERTIES_DIR 
				                                    + System.getProperty("file.separator") 
				                                    + FILENAME_APPLICATION_PROPERTIES);
		final String expected = FileUtil.getFileContent(defaultPropertiesFile).trim();
		assertEquals("Content of '" + FILENAME_APPLICATION_PROPERTIES + "'", expected , fileContent);
	}
	
	@Test
	public void createsWorkspacePropertiesFileIfDoesNotExist() throws IOException {
		// prepare test
		initApplicationPropertiesWith("");
		boolean delete = workspacePropertiesFile.delete();
		assertTrue("Could not delete " + workspacePropertiesFile.getAbsolutePath(), delete);

		// call functionality under test
		mogliCodeCreator.readWorkspaceProperties();

		// verify test result
		final String fileContent = MOGLiFileUtil.getFileContent(workspacePropertiesFile).trim();
		final File defaultPropertiesFile = new File(getProjectResourcesDir(), MOGLiCodeCreator.PROPERTIES_DIR + System.getProperty("file.separator") + FILENAME_WORKSPACE_PROPERTIES);
		final String expected = FileUtil.getFileContent(defaultPropertiesFile).trim();
		assertEquals("Content of '" + workspacePropertiesFile + "'", expected , fileContent);
	}
	

	@Test
	public void readsWorkspaceFromApplicationProperties() {
		// prepare test
		initApplicationPropertiesWith("workspace=workspaces/demo");
		mogliCodeCreator = new MOGLiCodeCreator();  // read properties again

		// call functionality under test
		final String workspace = mogliCodeCreator.readWorkspaceDirFromApplicationProperties();

		// verify test result
		assertStringEquals("workspace", "workspaces/demo", workspace);
	}
}
