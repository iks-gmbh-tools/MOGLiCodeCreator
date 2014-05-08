package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LOGS_FILES;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_PLUGIN_EXECUTED;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_UNEXPECTED_PROBLEM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.helper.PluginExecutor.PluginExecutionData;
import com.iksgmbh.moglicc.infrastructure.MOGLiLogger;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.test.starterclasses.DummyDataProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyPluginThrowsRuntimeExceptionStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyStandardModelProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class PluginExecutorUnitTest extends CoreTestParent {

	// **************************  Instance fields  *********************************	
	
	private PluginExecutor pluginExecutor;
	private ArrayList<PluginMetaData> pluginMetaDataList;
	private PluginExecutionData pluginExecutionData;

	// **************************  Instantiation stuff  *********************************	
	
	@Before
	public void setup() {
		super.setup();
		pluginMetaDataList = new ArrayList<PluginMetaData>();
		
		pluginMetaDataList.add(createPluginMetaData("StandardModelProvider"));
		pluginMetaDataList.add(createPluginMetaData("DummyPlugin"));		
		pluginMetaDataList.add(createPluginMetaData("DummyGenerator"));
		pluginMetaDataList.add(createPluginMetaData("DummyDataProvider"));
		pluginMetaDataList.add(createPluginMetaData("VelocityEngineProvider"));
		pluginMetaDataList.add(createPluginMetaData("DummyPluginThrowsRuntimeException"));
		
		final InfrastructureInitData infrastructureInitData = createInfrastructureInitData(null, null, null);

		pluginExecutionData = new PluginExecutionData(null,
				pluginMetaDataList, infrastructureInitData);
	
		pluginExecutor = new PluginExecutor(pluginExecutionData);
	}

	private PluginMetaData createPluginMetaData(String id) {
		PluginMetaData pluginMetaData = new PluginMetaData("JAR", id + "Starter");
		pluginMetaData.setId(id);
		pluginMetaData.setInfoMessage(null);
		return pluginMetaData;
	}
	
	// **************************  Test Methods  *********************************	

	@Test
	public void testExecutePlugins_1PluginExecutedSuccessfully() {
		// prepare test
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		plugins.add(modelProvider);
		File pluginLogfile = MOGLiFileUtil.getNewFileInstance(DIR_LOGS_FILES 
                + "/StandardModelProvider.log");
		pluginLogfile.delete();
		assertFalse("Logfile not deleted", pluginLogfile.exists());
		
		// call functionality under test
		pluginExecutor.executePlugins(plugins);
		
		// verify test result
		assertStringEquals("Unexpected Info Message", PluginStatus.EXECUTED.name(), 
				            pluginExecutor.getMetaData(modelProvider).getStatus().name());
		MOGLiLogger logger = (MOGLiLogger) 
		                 pluginExecutor.getInfrastructureFor(modelProvider.getId()).getPluginLogger();

		assertEquals("Unexpected Logfile", pluginLogfile.getAbsolutePath(), logger.getLogfile().getAbsolutePath()); 
		assertTrue("Logfile not created", pluginLogfile.exists());
		assertFileContainsEntry(pluginLogfile, modelProvider.getId());
	}
	

	@Test
	public void testExecutePlugins_1PluginExecutedException() {
		// prepare test
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyPluginStarter dummyPlugin = new DummyPluginStarter();
		plugins.add(dummyPlugin);
		File pluginLogfile = MOGLiFileUtil.getNewFileInstance(DIR_LOGS_FILES + "/DummyPlugin.log");
		pluginLogfile.delete();
		assertFalse("Logfile not deleted", pluginLogfile.exists());
		
		// call functionality under test
		pluginExecutor.executePlugins(plugins);
		
		// verify test result
		assertStringEquals("Unexpected Info Message", "Testfehler", 
				                                      pluginMetaDataList.get(1).getInfoMessage());
		MOGLiLogger logger = (MOGLiLogger) pluginExecutor.getInfrastructureFor(dummyPlugin.getId()).getPluginLogger();

		assertTrue("Unexpected Logfile", FileUtil.areFilePathsIdentical(logger.getLogfile(), 
				pluginLogfile));
		assertTrue("Logfile not created", pluginLogfile.exists());
	}
	
	@Test
	public void testExecutePlugins_1PluginExecutedRuntimeException() {
		// prepare test
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyPluginThrowsRuntimeExceptionStarter errorPlugin = new DummyPluginThrowsRuntimeExceptionStarter();
		plugins.add(errorPlugin);
		File pluginLogfile = MOGLiFileUtil.getNewFileInstance(DIR_LOGS_FILES + "/DummyPlugin.log");
		pluginLogfile.delete();
		assertFalse("Logfile not deleted", pluginLogfile.exists());
		
		// call functionality under test
		pluginExecutor.executePlugins(plugins);
		
		// verify test result
		assertStringContains(pluginMetaDataList.get(1).getInfoMessage(), TEXT_UNEXPECTED_PROBLEM + "RuntimeException: fatal");
		MOGLiLogger logger = (MOGLiLogger) pluginExecutor.getInfrastructureFor(
				                                        errorPlugin.getId()).getPluginLogger();

		assertTrue("Unexpected Logfile", FileUtil.areFilePathsIdentical(logger.getLogfile(), 
				pluginLogfile));
		assertTrue("Logfile not created", pluginLogfile.exists());
	}
	
	@Test
	public void testExecutePlugins_2PluginExecutedSuccessfully() {
		// verify test result
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		plugins.add(modelProvider);
		plugins.add(generator);
		
		// call functionality under test
		pluginExecutor.executePlugins(plugins);
		
		// verify test result
		assertAllPluginsExecutedSuccessfullyExcept(-1);
		final MOGLiLogger logger = (MOGLiLogger) pluginExecutor.getInfrastructureFor(
				generator.getId()).getPluginLogger();
		assertStringEquals("Unexpected Logfile Name", "DummyGenerator.log", logger.getLogfile().getName());
		assertTrue("Logfile not created", logger.getLogfile().exists());
		assertFileContainsEntry(logger.getLogfile(), generator.getId());
	}
	
	@Test
	public void testExecutePlugins_3PluginExecutedSuccessfully() {
		// prepare test
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		plugins.add(modelProvider);
		plugins.add(generator);
		plugins.add(dataProvider);
		
		// call functionality under test
		pluginExecutor.executePlugins(plugins);
		
		// verify test result
		assertAllPluginsExecutedSuccessfullyExcept(-1);
		MOGLiLogger logger = (MOGLiLogger) pluginExecutor.getInfrastructureFor(dataProvider.getId()).getPluginLogger();
		assertStringEquals("Unexpected Logfile Name", "DummyDataProvider.log", logger.getLogfile().getName());
		assertTrue("Logfile not created", logger.getLogfile().exists());
		assertFileContainsEntry(logger.getLogfile(), dataProvider.getId());
	}
	
	@Test
	public void testExecutePlugins_4PluginExecutedSuccessfully() {
		// prepare test
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		plugins.add(modelProvider);
		plugins.add(generator);
		plugins.add(dataProvider);
		plugins.add(engineProvider);
		
		// call functionality under test
		pluginExecutor.executePlugins(plugins);
		
		// verify test result
		assertAllPluginsExecutedSuccessfullyExcept(-1);
		MOGLiLogger logger = (MOGLiLogger) pluginExecutor.getInfrastructureFor(engineProvider.getId()).getPluginLogger();
		assertStringEquals("Unexpected Logfile Name", "VelocityEngineProvider.log", logger.getLogfile().getName());
		assertTrue("Logfile not created", logger.getLogfile().exists());
		assertFileContainsEntry(logger.getLogfile(), engineProvider.getId());
	}
	
	@Test
	public void testExecutePlugins_5PluginExecuted1Exception() {
		// prepare test
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyPluginStarter dummyPlugin = new DummyPluginStarter();
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		plugins.add(modelProvider);
		plugins.add(dummyPlugin);
		plugins.add(generator);
		plugins.add(dataProvider);
		plugins.add(engineProvider);
		
		// call functionality under test
		pluginExecutor.executePlugins(plugins);
		
		// verify test result
		int index = 1;
		assertStringEquals("Unexpected Info Message", "Testfehler", 
                pluginMetaDataList.get(index).getInfoMessage());
		assertAllPluginsExecutedSuccessfullyExcept(index);
	}
	
	@Test
	public void testSortPluginMetaDataList() {
		// assert precondition
		assertStringEquals("Unexpected list order!", "StandardModelProvider", pluginMetaDataList.get(0).getId());
		assertStringEquals("Unexpected list order!", "DummyPlugin", pluginMetaDataList.get(1).getId());
		assertStringEquals("Unexpected list order!", "DummyGenerator", pluginMetaDataList.get(2).getId());
		assertStringEquals("Unexpected list order!", "DummyDataProvider", pluginMetaDataList.get(3).getId());
		assertStringEquals("Unexpected list order!", "VelocityEngineProvider", pluginMetaDataList.get(4).getId());
		assertStringEquals("Unexpected list order!", "DummyPluginThrowsRuntimeException", pluginMetaDataList.get(5).getId());

		// prepare test
		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		plugins.add(generator);
		plugins.add(modelProvider);
		plugins.add(engineProvider);		
		plugins.add(dataProvider);
		pluginExecutionData.pluginsToExecute = plugins;
		
		// call functionality under test
		List<PluginMetaData> sortedList = PluginExecutor.doYourJob(pluginExecutionData);
		
		// verify test result
		assertStringEquals("Unexpected list order!", "StandardModelProvider", sortedList.get(0).getId());
		assertStringEquals("Unexpected list order!", "DummyDataProvider", sortedList.get(1).getId());
		assertStringEquals("Unexpected list order!", "VelocityEngineProvider", sortedList.get(2).getId());
		assertStringEquals("Unexpected list order!", "DummyGenerator", sortedList.get(3).getId());
		assertStringEquals("Unexpected list order!", "DummyPlugin", sortedList.get(4).getId());
		assertStringEquals("Unexpected list order!", "DummyPluginThrowsRuntimeException", sortedList.get(5).getId());
	}
	
	private void assertAllPluginsExecutedSuccessfullyExcept(int exceptIndex) {
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getInfoMessage() != null && counter != exceptIndex) {
				assertStringEquals("Unexpected Info Message for " + pluginMetaData.getId() + "!", TEXT_PLUGIN_EXECUTED, 
						pluginMetaData.getInfoMessage());
			}
			counter++;
		}
	}
	
}
