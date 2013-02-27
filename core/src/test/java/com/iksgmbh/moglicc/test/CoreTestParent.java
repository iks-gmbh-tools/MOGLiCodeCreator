package com.iksgmbh.moglicc.test;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.MOGLiTextConstants;
import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.test.starterclasses.DummyDataProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyStandardModelProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.FileUtil;

public class CoreTestParent extends AbstractMOGLiTest {

	private static final String PROJECT_ROOT_DIR = "../core/";
	private static boolean isFirstTest = true;

	@Override
	public void setup() {
		super.setup();
		if (isFirstTest) {
			isFirstTest = false;
			initForFirstUnitTest();
		}
		initPluginSubdir();
		createMogliLogFile();
		MOGLiLogUtil.setCoreLogfile(applicationLogfile);
	}

	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String initTestApplicationRootDir() {
		final String applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}

	protected void assertPluginData(List<PluginMetaData> pluginMetaDataList, String pluginName, PluginStatus status,
			String starterClass, String infoMessage) {
		PluginMetaData pluginData = null;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getJarName().equals(pluginName)) {
				pluginData = pluginMetaData;
				break;
			}
		}
		assertNotNull("Expected plugin not found: " + pluginName, pluginData);
		assertStringEquals("plugin status", status.name(), pluginData.getStatus().name());
		if (status != null) {
		}
		assertStringEquals("starterClass", starterClass, pluginData.getStarterClass());
		if (starterClass != null) {
		}
		assertStringContains(pluginData.getInfoMessage(), infoMessage);
		if (infoMessage != null) {
		}
	}

	protected void activatePluginsForTest(String... pluginNames) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < pluginNames.length; i++) {
			sb.append(pluginNames[i] + "="
				+ MOGLiTextConstants.TEXT_ACTIVATED_PLUGIN_PROPERTY
				+ FileUtil.getSystemLineSeparator());
		}
		try {
			initWorkspacePropertiesWith(sb.toString());
		} catch (Exception e) {
			throw new MOGLiCoreException(e);
		}
	}

	protected List<MOGLiPlugin> getPluginListForTest() {
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();

		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		plugins.add(generator);
		plugins.add(modelProvider);
		plugins.add(dataProvider);
		plugins.add(engineProvider);
		return plugins;
	}

	protected List<PluginMetaData> getPluginMetaDataListForTest() {
		List<PluginMetaData> pluginMetaDataList = new ArrayList<PluginMetaData>();
		final PluginMetaData pluginMetaData1 = new PluginMetaData("a", "b");
		final PluginMetaData pluginMetaData2 = new PluginMetaData("c", "d");
		pluginMetaDataList.add(pluginMetaData1);
		pluginMetaDataList.add(pluginMetaData2);
		return pluginMetaDataList;
	}


}
