package com.iksgmbh.moglicc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin.PluginType;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.test.plugin.GeneratorDummy;
import com.iksgmbh.moglicc.test.plugin.ModelProviderDummy;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;

public class ReportWriterUnitTest extends CoreTestParent {
	
	private ReportWriter reportWriter;
	private List<MOGLiPlugin> plugins;
	private List<PluginMetaData> pluginMetaDataList;
	
	@Before
	@Override
	public void setup()
	{
		super.setup();
		
		plugins = new ArrayList<MOGLiPlugin>();
		
		final String pluginID1 = "ModelProviderDummy";
		final String pluginID2 = "GeneratorDummy";
		final ModelProviderDummy modelProviderDummy = new ModelProviderDummy(pluginID1, "DummyModelFile.xyz");
		final GeneratorDummy generatorDummy = new GeneratorDummy(pluginID2);
		
		plugins.add(modelProviderDummy);
		plugins.add(generatorDummy);
		
		modelProviderDummy.setInfrastructure(createInfrastructure(modelProviderDummy.getId()));
		modelProviderDummy.setInfrastructure(createInfrastructure(generatorDummy.getId()));
				
		pluginMetaDataList = new ArrayList<PluginMetaData>();
		final PluginMetaData metaDataForTheGenerator = new PluginMetaData("jar", null);
		metaDataForTheGenerator.setPluginStatus(PluginStatus.EXECUTED);
		metaDataForTheGenerator.setPluginType(PluginType.GENERATOR);
		metaDataForTheGenerator.setId(pluginID1);
		pluginMetaDataList.add(metaDataForTheGenerator);
		final PluginMetaData metaDataForTheProvider = new PluginMetaData("jar", null);
		metaDataForTheProvider.setPluginStatus(PluginStatus.EXECUTED);
		metaDataForTheProvider.setPluginType(PluginType.PROVIDER);
		metaDataForTheProvider.setId(pluginID2);
		pluginMetaDataList.add(metaDataForTheProvider);

		reportWriter = new ReportWriter(plugins, pluginMetaDataList, "DummyWorkspace");
		
		applicationReportDir.mkdir();
	}
	
	private final InfrastructureService createInfrastructure(final String pluginId) 
	{
		final InfrastructureInitData infrastructureInitData = createInfrastructureInitData(applicationProperties, plugins, pluginId);
		return new MOGLiInfrastructure(infrastructureInitData);
	}

	

	@Test
	public void createShortResultFile() throws Exception
	{
		// arrange
		final File resultFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_SHORT_REPORT_FILE);
		resultFile.delete();
		assertFileDoesNotExist(resultFile);

		// act
		reportWriter.writeShortReport(resultFile);

		// assert
		assertFileExists(resultFile);
		final File file = new File(getProjectTestResourcesDir(), "ExpectedDummyResult.txt");
		final String expected = MOGLiFileUtil.getFileContent(file);
		assertStringEquals("MOGLiCC Result", expected, MOGLiFileUtil.getFileContent(resultFile));
	}
	
	@Test
	public void createGeneratorReportFile() throws Exception
	{
		// arrange
		final File reportFile = new File(applicationReportDir, MOGLiSystemConstants.FILENAME_GENERATION_REPORT_FILE);
		reportFile.delete();
		assertFileDoesNotExist(reportFile);		

		// act
		reportWriter.writeGeneratorReport(reportFile);

		// assert
		assertFileExists(reportFile);
		final File file = new File(getProjectTestResourcesDir(), "ExpectedGeneratorReport.txt");
		final String expected = MOGLiFileUtil.getFileContent(file);
		assertStringEquals("MOGLiCC Short Report", expected, MOGLiFileUtil.getFileContent(reportFile));
	}
	
	@Test
	public void createsProviderReportFile() throws Exception
	{
		// arrange
		final File reportFile = new File(applicationReportDir, MOGLiSystemConstants.FILENAME_PROVIDER_REPORT_FILE);
		reportFile.delete();
		assertFileDoesNotExist(reportFile);		

		// act
		reportWriter.writeProviderReport(reportFile);

		// assert
		assertFileExists(reportFile);
		final File file = new File(getProjectTestResourcesDir(), "ExpectedProviderReport.txt");
		final String expected = MOGLiFileUtil.getFileContent(file);
		assertStringEquals("MOGLiCC Result", expected, MOGLiFileUtil.getFileContent(reportFile));
	}

	@Test
	public void doesNotHandleDeactivatedPluginsAsError() throws Exception
	{
		// arrange
		pluginMetaDataList.get(0).setInfoMessage(MOGLiTextConstants.TEXT_DEACTIVATED_PLUGIN_INFO);
		pluginMetaDataList.get(0).setPluginStatus(PluginStatus.ANALYSED);
		final File errorReportFile = new File(applicationReportDir, MOGLiSystemConstants.FILENAME_ERROR_REPORT_FILE);
		errorReportFile.delete();
		assertFileDoesNotExist(errorReportFile);		

		// act
		reportWriter = new ReportWriter(plugins, pluginMetaDataList, "DummyWorkspace");
		reportWriter.writeErrorReportIfNecessary(errorReportFile);
		final String shortReportHeader = reportWriter.getShortReportHeader();

		// assert
		assertEquals("deactivated plugin not found", 1, reportWriter.getIdsOfDeactivatedPlugins().size());
		assertEquals("deactivated plugin not found", pluginMetaDataList.get(0).getId(), reportWriter.getIdsOfDeactivatedPlugins().get(0));
		assertFileDoesNotExist(errorReportFile);
	
		final File file = new File(getProjectTestResourcesDir(), "ExpectedReportHeaderForDeactivatedPlugin.txt");
		final String expected = MOGLiFileUtil.getFileContent(file);
		assertEquals("shortReportHeader", expected, shortReportHeader);
	}
	
}
