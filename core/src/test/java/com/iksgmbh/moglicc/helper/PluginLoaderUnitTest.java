package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DUPLICATE_PLUGINIDS;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_STARTERCLASS_UNKNOWN;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_STARTERCLASS_WRONG_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.DuplicatePluginIdException;
import com.iksgmbh.moglicc.helper.PluginLoader.LoadResult;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin.PluginType;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;

public class PluginLoaderUnitTest extends CoreTestParent {

	// **************************  Instance fields  *********************************	
	
	private PluginLoader pluginLoader;

	// **************************  Instantiation stuff  *********************************	
	
	@Before
	public void setup() {
		MOGLiLogUtil.setCoreLogfile(null);
		pluginLoader = new PluginLoader();
		super.setup();
	}
	
	// **************************  Test Methods  *********************************	

	@Test
	public void testLoadPlugin_TwoWithSameId() {
		final String starterClassName = "com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter";
		List<PluginMetaData> pluginMetaDataList = new ArrayList<PluginMetaData>();
		final PluginMetaData pluginMetaData1 = new PluginMetaData("a", starterClassName);
		final PluginMetaData pluginMetaData2 = new PluginMetaData("c", starterClassName);
		pluginMetaDataList.add(pluginMetaData1);
		pluginMetaDataList.add(pluginMetaData2);
		boolean exceptionThrown = false;
		try {
			pluginLoader.loadPlugins(pluginMetaDataList);
		} catch (DuplicatePluginIdException e) {
			final String id = e.getPluginId();
			final String expectedLogEntry = TEXT_DUPLICATE_PLUGINIDS + id;
			assertFileContainsEntry(applicationLogfile, expectedLogEntry);
			exceptionThrown = true;
		}
		if (! exceptionThrown) fail("Expected excepltion was not thrown: DuplicatePluginIdException");
	}

	@Test
	public void testLoadThisPlugin() {
		final String starterClassName = "com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter";
		final MOGLiPlugin loadedPlugin = pluginLoader.loadThisPlugin(starterClassName).plugin;
		
		assertNotNull("Plugin not found!", loadedPlugin);	
		assertStringEquals("Unexpected plugin id!", "DummyPlugin", loadedPlugin.getId());
		assertNotNull("Dependencies undefined!", loadedPlugin.getDependencies());
		assertEquals("Unexpected number of dependencies!", 1, loadedPlugin.getDependencies().size());
		assertStringEquals("Unexpected dependency!", "OtherPlugin", loadedPlugin.getDependencies().get(0));
		assertStringEquals("Unexpected PluginType!", PluginType.GENERATOR.name(),  loadedPlugin.getPluginType().name());
	}

	@Test
	public void testPluginThisPlugin_StarterClassNotFound() {
		final String starterClassName = "com.iksgmbh.moglicc.test.starterclasses.notExistingPluginStarter";
		final LoadResult loadResult = pluginLoader.loadThisPlugin(starterClassName);
		
		final String expected = TEXT_STARTERCLASS_UNKNOWN;
		assertStringEquals("Unexpected, Error Message", expected, loadResult.errorMessage);
		assertNull("Plugin loaded unexpectedly!", loadResult.plugin);
	}
	

	@Test
	public void testPluginThisPlugin_StarterClassWithoutInterface() {
		final String starterClassName = "com.iksgmbh.moglicc.test.starterclasses.StarterClassWithMissingInterface";
		final LoadResult loadResult = pluginLoader.loadThisPlugin(starterClassName);
		
		final String expected = TEXT_STARTERCLASS_WRONG_TYPE;
		assertStringEquals("Unexpected, Error Message", expected, loadResult.errorMessage);
		assertNull("Plugin loaded unexpectedly!", loadResult.plugin);
	}
}
