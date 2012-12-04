package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LIB_PLUGIN;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DEACTIVATED_PLUGIN_INFO;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_INFOMESSAGE_OK;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_MANIFEST_FOUND;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.exceptions.MissingManifestException;
import com.iksgmbh.moglicc.exceptions.MissingStarterclassException;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;

public class MetaDataLoaderUnitTest extends CoreTestParent {

	// **************************  Instance fields  *********************************	
	
	private MetaDataLoader metaDataLoader;


	// **************************  Instantiation stuff  *********************************	
	
	@Before
	public void setup() {
		MOGLiLogUtil.setCoreLogfile(null);
		super.setup();
		metaDataLoader = new MetaDataLoader(applicationProperties);
	}

	// **************************  Test Methods  *********************************	
	
	@Test
	public void testLoadMetaData() throws FileNotFoundException, IOException {		
		deactivatePluginsForTest("DummyPluginStarter");
		List<PluginMetaData> pluginMetaDataList = MetaDataLoader.doYourJob(applicationProperties);

		assertNotNull(pluginMetaDataList);
		assertPluginData(pluginMetaDataList, "DummyPluginStarter.jar", PluginStatus.ANALYSED, 
				"com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter", 
				TEXT_DEACTIVATED_PLUGIN_INFO);
		assertPluginData(pluginMetaDataList, "pluginWithoutPropertiesFile.jar", PluginStatus.ANALYSED, 
				PluginMetaData.NO_MANIFEST, TEXT_NO_MANIFEST_FOUND);
		assertPluginData(pluginMetaDataList, "pluginWithoutStarterClassEntry.jar", PluginStatus.ANALYSED, 
				PluginMetaData.NO_STARTERCLASS, TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE);
		assertPluginData (pluginMetaDataList, "pluginWithoutExistingStarterClass.jar", PluginStatus.ANALYSED, 
				"notExistingStarterClass", TEXT_INFOMESSAGE_OK);
	}

	@Test
	public void testSearchForAvailablePlugins() {		
		List<PluginMetaData> pluginMetaDataList = metaDataLoader.searchForAvailablePlugins();
		assertNotNull(pluginMetaDataList);
		assertPluginData (pluginMetaDataList, "DummyPluginStarter.jar", PluginStatus.ANALYSED, 
				"com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter", TEXT_INFOMESSAGE_OK);
		assertPluginData(pluginMetaDataList, "pluginWithoutPropertiesFile.jar", PluginStatus.ANALYSED, 
				PluginMetaData.NO_MANIFEST, TEXT_NO_MANIFEST_FOUND);
		assertPluginData (pluginMetaDataList, "pluginWithoutStarterClassEntry.jar", PluginStatus.ANALYSED, 
				PluginMetaData.NO_STARTERCLASS, TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE);
		assertPluginData(pluginMetaDataList, "pluginWithoutExistingStarterClass.jar", PluginStatus.ANALYSED, 
				"notExistingStarterClass", TEXT_INFOMESSAGE_OK);
	}
	
	@Test
	public void testSearchPluginsJars() {		
		File[] pluginMetaDataList = metaDataLoader.searchPluginJars();
		assertNotNull(pluginMetaDataList);
		assertPluginJarFound(pluginMetaDataList, "DummyPluginStarter.jar");
		assertPluginJarFound(pluginMetaDataList, "pluginWithoutPropertiesFile.jar");
		assertPluginJarFound(pluginMetaDataList, "pluginWithoutStarterClassEntry.jar");
		assertPluginJarFound(pluginMetaDataList, "pluginWithoutExistingStarterClass.jar");
	}
	
	@Test
	public void testReadStarterClassFromJar() throws MissingManifestException, MissingStarterclassException {
		File jar = MOGLiFileUtil.getNewFileInstance(DIR_LIB_PLUGIN + "/DummyPluginStarter.jar");
		String starterClassFromJar = metaDataLoader.readStarterClassFromJar(jar);
		assertStringEquals ("Wrong starterClass read from jar", "com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter", starterClassFromJar);
		
		jar = MOGLiFileUtil.getNewFileInstance(DIR_LIB_PLUGIN + "/pluginWithoutExistingStarterClass.jar");
		starterClassFromJar = metaDataLoader.readStarterClassFromJar(jar);
		assertStringEquals ("Wrong starterClass read from jar", "notExistingStarterClass", starterClassFromJar);

		jar = MOGLiFileUtil.getNewFileInstance(DIR_LIB_PLUGIN + "/pluginWithoutStarterClassEntry.jar");
		boolean expectedExceptionThrown = false;
		try {
			starterClassFromJar = metaDataLoader.readStarterClassFromJar(jar);
		} catch (MissingStarterclassException e) {
			expectedExceptionThrown = true;
		}
		assertTrue("Expected exception not thrown", expectedExceptionThrown);

		jar = MOGLiFileUtil.getNewFileInstance(DIR_LIB_PLUGIN + "/pluginWithNoManifest.jar");
		expectedExceptionThrown = false;
		try {
			metaDataLoader.readStarterClassFromJar(jar);
		} catch (Exception e) {
			expectedExceptionThrown = true;
		}
		assertTrue("Expected exception not thrown", expectedExceptionThrown);
	}

	@Test
	public void testCheckActivationPluginState() throws FileNotFoundException, IOException {
		// prepare test
		deactivatePluginsForTest("a");
		metaDataLoader = new MetaDataLoader(applicationProperties);
		List<PluginMetaData> pluginMetaDataList = getPluginMetaDataListForTest();
		
		// call functionality under test
		metaDataLoader.checkActivationPluginState(pluginMetaDataList);

		// verify test result
		assertPluginData(pluginMetaDataList, "a", PluginStatus.ANALYSED, "b", TEXT_DEACTIVATED_PLUGIN_INFO);
		assertPluginData(pluginMetaDataList, "c", PluginStatus.ANALYSED, "d", TEXT_INFOMESSAGE_OK);
	}

	
	@Test
	public void testSearchPluginsJarsNoJarsFound() {
		String applicationRootDir = MOGLiCodeCreator.getApplicationRootDir();
		MOGLiCodeCreator.setApplicationRootDir("");
		File[] plugins = metaDataLoader.searchPluginJars();
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);

		assertNotNull(plugins);
		assertEquals("Unexpected number of plugins found.", 0, plugins.length);
	}

	// **************************  Private Methods  *********************************	

	private void assertPluginJarFound(File[] plugins, String pluginName) {
		boolean found = false;
		for (int i = 0; i < plugins.length; i++) {
			found = plugins[i].getName().equals(pluginName);
			if (found) break;
		}
		assertTrue("Expected plugin not found: " + pluginName, found);
	}

}
