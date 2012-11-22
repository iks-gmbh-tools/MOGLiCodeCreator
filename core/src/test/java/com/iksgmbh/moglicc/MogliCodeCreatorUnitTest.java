package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_LOGS_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_OUTPUT_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.FILENAME_APPLICATION_PROPERTIES;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_APPLICATION_TERMINATED;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_DEACTIVATED_PLUGIN_INFO;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_DUPLICATE_PLUGINIDS;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_INFOMESSAGE_OK;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_NOTHING_TO_DO;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_NO_MANIFEST_FOUND;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_STARTERCLASS_UNKNOWN;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_STARTERCLASS_WRONG_TYPE;
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
import com.iksgmbh.moglicc.utils.MogliFileUtil;
import com.iksgmbh.moglicc.utils.MogliLogUtil;
import com.iksgmbh.utils.FileUtil;

public class MogliCodeCreatorUnitTest extends CoreTestParent {

	private MogliCodeCreator mogliCodeCreator;

	@Before
	public void setup() {
		MogliLogUtil.setCoreLogfile(null);
		super.setup();  // this recreates the same logfile and stores a reference to it in the parent class
		mogliCodeCreator = new MogliCodeCreator();  // this creates new logfile
		initProperties();
	}

	// *****************************  test methods  ************************************	

	@Test
	public void cleanupOnInstantiation() throws IOException {
		// prepare test
		final File logsDir = MogliFileUtil.getNewFileInstance(DIR_LOGS_FILES);
		createFileIn(logsDir);
		assertChildrenNumberInDirectory(logsDir, 2);
		final File resultDir = MogliFileUtil.getNewFileInstance(DIR_OUTPUT_FILES);
		createFileIn(resultDir);
		assertChildrenNumberInDirectory(resultDir, 1);
				
		// call functionality under test
		mogliCodeCreator = new MogliCodeCreator();  
		
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
	public void canCreateMogliLogFile() {
		// prepare test
		String applicationRootDir = MogliCodeCreator.getApplicationRootDir();
		initMogliWithNotExistingLogfile();
		assertFileDoesNotExist(applicationLogfile);
		
		// call functionality under test
		mogliCodeCreator.doYourJob();
		
		// verify test result
		assertFileExists(applicationLogfile);
		assertFileContainsEntry(applicationLogfile, TEXT_NOTHING_TO_DO);
		
		// cleanup
		MogliCodeCreator.setApplicationRootDir(applicationRootDir);
	}

	private void initMogliWithNotExistingLogfile() {
		final String dirname = getProjectRootDir() + TARGET_DIR + "xyz";
		MogliCodeCreator.setApplicationRootDir(dirname);
		final File dir = new File(dirname);
		dir.mkdirs();
		mogliCodeCreator = new MogliCodeCreator();
		applicationLogfile = MogliCodeCreator.getLogFile();
		applicationLogfile.delete();
	}

	@Test
	public void terminatesTwoPluginWithSameId() throws FileNotFoundException, IOException {
		// prepare test
		setEmptyProperties();
		mogliCodeCreator = new MogliCodeCreator();  // read properties again
		
		// call functionality under test
		mogliCodeCreator.doYourJob();
		
		// verify test result
		final String expectedLogEntry = TEXT_DUPLICATE_PLUGINIDS + "DummyPlugin";
		
		final String expectedLogFileEnding = TEXT_APPLICATION_TERMINATED + expectedLogEntry
		                                      + FileUtil.getSystemLineSeparator();
		final String actualFileContent = MogliFileUtil.getFileContent(applicationLogfile);
		assertTrue("Unexpected logfile ending!" + actualFileContent, actualFileContent.endsWith(expectedLogFileEnding));
	}
	
	@Test
	public void handlePluginWithNotExistingStarterClass() {
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
		deactivatePluginsForTest("DummyPluginStarter", "DummyPluginStarter2");
		mogliCodeCreator = new MogliCodeCreator();  // read properties again
		
		// call functionality under test
		mogliCodeCreator.doYourJob();
		
		// verify test result
		List<PluginMetaData> pluginMetaDataList = mogliCodeCreator.getPluginMetaDataList();
		assertPluginData(pluginMetaDataList, "DummyStandardModelProviderStarter.jar", PluginStatus.EXECUTED, 
				"com.iksgmbh.moglicc.test.starterclasses.DummyStandardModelProviderStarter", TEXT_INFOMESSAGE_OK);
	}
	
	@Test
	public void handlePluginExecutedWithError() {
		// prepare test
		deactivatePluginsForTest("DummyPluginStarter", "DummyPluginStarter2");
		mogliCodeCreator = new MogliCodeCreator();
		
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
		deactivatePluginsForTest("DummyPluginStarter", "DummyPluginStarter2");
		mogliCodeCreator = new MogliCodeCreator(); 
		
		// call functionality under test
		mogliCodeCreator.doYourJob();
		
		// verify test result
		assertFileContainsEntry(applicationLogfile, "Plugins found:");
		assertFileContainsEntry(applicationLogfile, "jarName=DummyPluginThrowsRuntimeExceptionStarter.jar, id=DummyPlugin, pluginType=GENERATOR, status=LOADED, infoMessage=UNEXPECTED PROBLEM: RuntimeException: fatal");
		assertFileContainsEntry(applicationLogfile, "jarName=DummyStandardModelProviderStarter.jar, id=StandardModelProvider, pluginType=MODEL_PROVIDER, status=EXECUTED, infoMessage=" + TEXT_INFOMESSAGE_OK);
		assertFileContainsEntry(applicationLogfile, "jarName=DummyDataProviderStarter.jar, id=DummyDataProvider, pluginType=DATA_PROVIDER, status=EXECUTED, infoMessage=" + TEXT_INFOMESSAGE_OK);
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
	public void createPluginsPropertiesFileIfDoesNotExist() throws IOException {
		// prepare test
		boolean delete = applicationPropertiesFile.delete();
		assertTrue("Could not delete " + applicationPropertiesFile.getAbsolutePath(), delete);
		
		// call functionality under test
		mogliCodeCreator.checkApplicationPropertiesFile();

		// verify test result
		final String fileContent = MogliFileUtil.getFileContent(applicationPropertiesFile).trim();
		final File defaultPropertiesFile = new File(getProjectResourcesDir(), FILENAME_APPLICATION_PROPERTIES);
		final String expected = FileUtil.getFileContent(defaultPropertiesFile).trim();
		assertEquals("Content of '" + FILENAME_APPLICATION_PROPERTIES + "'", expected , fileContent);
	}
	
	@Test
	public void readsWorkspaceFromProperties() {
		// prepare test
		initPropertiesWith("workspace=workspaces/demo");
		mogliCodeCreator = new MogliCodeCreator();
		
		// call functionality under test
		final String workspace = mogliCodeCreator.readWorkspaceDirFromApplicationProperties();
		
		// verify test result
		assertStringEquals("workspace", "workspaces/demo", workspace);
	}
}
