package com.iksgmbh.moglicc;

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
	
	private ReportWriter reportManager;
	private List<MOGLiPlugin> plugins;
	
	@Before
	@Override
	public void setup()
	{
		super.setup();
		
		plugins = new ArrayList<MOGLiPlugin>();
		
		final ModelProviderDummy modelProviderDummy = new ModelProviderDummy("ModelProviderDummy", "DummyModelFile.xyz");
		final GeneratorDummy generatorDummy = new GeneratorDummy("GeneratorDummy");
		
		plugins.add(modelProviderDummy);
		plugins.add(generatorDummy);
		
		modelProviderDummy.setInfrastructure(createInfrastructure(modelProviderDummy.getId()));
		modelProviderDummy.setInfrastructure(createInfrastructure(generatorDummy.getId()));
				
		final List<PluginMetaData> pluginMetaDataList = new ArrayList<PluginMetaData>();
		final PluginMetaData metaDataForTheGenerator = new PluginMetaData("jar", null);
		metaDataForTheGenerator.setPluginStatus(PluginStatus.EXECUTED);
		metaDataForTheGenerator.setPluginType(PluginType.GENERATOR);
		pluginMetaDataList.add(metaDataForTheGenerator);
		final PluginMetaData metaDataForTheProvider = new PluginMetaData("jar", null);
		metaDataForTheProvider.setPluginStatus(PluginStatus.EXECUTED);
		metaDataForTheProvider.setPluginType(PluginType.PROVIDER);
		pluginMetaDataList.add(metaDataForTheProvider);

		reportManager = new ReportWriter(plugins, pluginMetaDataList, "DummyWorkspace");
		
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
		reportManager.writeShortReport(resultFile);

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
		reportManager.writeGeneratorReport(reportFile);

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
		reportManager.writeProviderReport(reportFile);

		// assert
		assertFileExists(reportFile);
		final File file = new File(getProjectTestResourcesDir(), "ExpectedProviderReport.txt");
		final String expected = MOGLiFileUtil.getFileContent(file);
		assertStringEquals("MOGLiCC Result", expected, MOGLiFileUtil.getFileContent(reportFile));
	}
	
}
