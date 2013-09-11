package com.iksgmbh.moglicc;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.FileUtil;

/**
 * Helper to create generatorReportFile, providerReportFile and MOGLiCC-resultFile.
 *   
 * @author Reik Oberrath
 */
public class ReportWriter
{
	enum Status { OK, OK_WITH_WARNING, ERROR };  // TODO OK_WITH_WARNING for job runs with deactivated plugins
	
	private List<MOGLiPlugin> plugins;
	private List<PluginMetaData> pluginMetaDataList;
	private String shortReportHeader;
	private String workspace;
	private Status status;

	public ReportWriter(final List<MOGLiPlugin> plugins, 
			             final List<PluginMetaData> pluginMetaDataList, 
			             final String workspace) 
	{
		this.plugins = plugins;
		this.pluginMetaDataList = pluginMetaDataList;
		this.workspace = workspace;
		this.shortReportHeader = buildShortReportHeader();
	}
	

	public void writeErrorReportIfNecessary(final File errorReportFile)
	{
		if (status == Status.ERROR) {
			final StringBuffer report = new StringBuffer("############## MOGLiCC ERROR REPORT ###############");
			report.append(FileUtil.getSystemLineSeparator());
			report.append(FileUtil.getSystemLineSeparator());

			for (PluginMetaData pluginMetaData : pluginMetaDataList) {
				if (pluginMetaData.getStatus() != PluginStatus.EXECUTED) 
				{
					report.append("Problem for plugin '"+ pluginMetaData.getId() + "': " + pluginMetaData.getInfoMessage());
					report.append(FileUtil.getSystemLineSeparator());
				}
			}

			report.append(FileUtil.getSystemLineSeparator());
	        report.append("######################  End  ######################");
	        
			try {
				FileUtil.createNewFileWithContent(errorReportFile, report.toString().trim());
				MOGLiLogUtil.logInfo("Report file created: " + errorReportFile.getAbsolutePath());
			} catch (Exception e) {
				MOGLiLogUtil.logError("Error creating report file " + errorReportFile.getAbsolutePath() + ": " + e.getMessage());
			}   	        
		}
		
	}	

	public void writeShortReport(final File reportFile)
	{
		if (plugins.size() == 0) {
			return;
		}

		final List<GeneratorPlugin> generators = plugins.get(0).getInfrastructure().getPluginsOfType(GeneratorPlugin.class);
		final List<ProviderPlugin> providers = plugins.get(0).getInfrastructure().getPluginsOfType(ProviderPlugin.class);
		
		final StringBuffer report = new StringBuffer("############## MOGLiCC SHORT REPORT ###############");

		report.append(FileUtil.getSystemLineSeparator());
		report.append(FileUtil.getSystemLineSeparator());
		report.append("Status: " + status.toString());
		report.append(FileUtil.getSystemLineSeparator());
		report.append(FileUtil.getSystemLineSeparator());
		report.append(shortReportHeader);
		report.append(FileUtil.getSystemLineSeparator());
		report.append(FileUtil.getSystemLineSeparator());

		for (final ProviderPlugin provider : providers) {
			report.append(provider.getId() + ": " + provider.getShortReport());
			report.append(FileUtil.getSystemLineSeparator());
		}

		report.append(FileUtil.getSystemLineSeparator());

		for (final GeneratorPlugin generator : generators) {
			report.append(generator.getId() + ": " + generator.getShortReport());
			report.append(FileUtil.getSystemLineSeparator());
		}
		
		report.append(FileUtil.getSystemLineSeparator());
        report.append("######################  End  ######################");

		try {
			FileUtil.createNewFileWithContent(reportFile, report.toString().trim());
			MOGLiLogUtil.logInfo("Report file created: " + reportFile.getAbsolutePath());
		} catch (Exception e) {
			MOGLiLogUtil.logError("Error creating report file " + reportFile.getAbsolutePath() + ": " + e.getMessage());
		}        
	}

	public void writeProviderReport(final File providerReportFile)
	{
		if (plugins.size() == 0) {
			return;
		}
		final List<ProviderPlugin> providers = plugins.get(0).getInfrastructure().getPluginsOfType(ProviderPlugin.class);
		final StringBuffer report = new StringBuffer("***************************   P R O V I D E R   R E P O R T   ********************************");

		report.append(FileUtil.getSystemLineSeparator());
		report.append(FileUtil.getSystemLineSeparator());
		report.append(buildProviderReportHeader());
		report.append(FileUtil.getSystemLineSeparator());
		report.append(FileUtil.getSystemLineSeparator());

		for (final ProviderPlugin provider : providers) {
			report.append("----------------------------------------------------------------------------------------------");
			report.append(FileUtil.getSystemLineSeparator());
			report.append(FileUtil.getSystemLineSeparator());
			report.append("Report for " + provider.getId() + ":");
			report.append(FileUtil.getSystemLineSeparator());
			report.append(FileUtil.getSystemLineSeparator());
			report.append(provider.getProviderReport().trim());
			report.append(FileUtil.getSystemLineSeparator());
			report.append(FileUtil.getSystemLineSeparator());
		}
        report.append("**************************************  E N D  ***********************************************");

		try {
			FileUtil.createNewFileWithContent(providerReportFile, report.toString().trim());
			MOGLiLogUtil.logInfo("Report file created: " + providerReportFile.getAbsolutePath());
		} catch (Exception e) {
			MOGLiLogUtil.logError("Error creating report file " + providerReportFile.getAbsolutePath() + ": " + e.getMessage());
		}		
	}

	private String buildProviderReportHeader()
	{
		final int numberOfProviderPlugins = countNumberPlugins(MOGLiPlugin.PluginType.PROVIDER);
		final int numberOfSuccessfullyExecutedProviderPlugins = countNumberOfSuccessfullyExecutedPlugins(MOGLiPlugin.PluginType.PROVIDER);
		if (numberOfProviderPlugins == numberOfSuccessfullyExecutedProviderPlugins)
		{
			return "All " + numberOfProviderPlugins + " provider plugins executed successfully.";
		}
		
		return numberOfSuccessfullyExecutedProviderPlugins + " from " 
		        + numberOfProviderPlugins + " provider plugins ecexuted successfully.";
	}

	private String buildGeneratorReportHeader()
	{
		final int numberOfGeneratorPlugins = countNumberPlugins(MOGLiPlugin.PluginType.GENERATOR);
		final int numberOfSuccessfullyExecutedGeneratorPlugins = countNumberOfSuccessfullyExecutedPlugins(MOGLiPlugin.PluginType.GENERATOR);
		final StringBuffer header = new StringBuffer("");
		
		if (numberOfGeneratorPlugins == numberOfSuccessfullyExecutedGeneratorPlugins)
		{
			header.append("All " + numberOfGeneratorPlugins + " generator plugins executed successfully.");
		} else {
			header.append( numberOfSuccessfullyExecutedGeneratorPlugins + " from " 
					     + numberOfGeneratorPlugins + " generator plugins ecexuted successfully.");
		}
		
		header.append(FileUtil.getSystemLineSeparator());
		final String modelName = getModelName();
		if (StringUtils.isEmpty(modelName)) {
			header.append("Model file used is '" + getModelFileName() + "', but no model is available.");
		} else {
			header.append("Model used is '" + modelName + "'.");
			header.append(FileUtil.getSystemLineSeparator());
			header.append("A total of " + countTotalNumberOfGenerations() + " generation events for " 
					+ countTotalNumberOfGeneratedArtefacts() + " output artefact(s) have been performed.");
		}
		
		return header.toString();
	}

	public void writeGeneratorReport(final File generatorReportFile)
	{
		if (plugins.size() == 0) {
			return;
		}
		
		final List<GeneratorPlugin> generators = plugins.get(0).getInfrastructure().getPluginsOfType(GeneratorPlugin.class);
		final StringBuffer report = new StringBuffer("**************************   G E N E R A T O R   R E P O R T   *******************************");

		report.append(FileUtil.getSystemLineSeparator());
		report.append(FileUtil.getSystemLineSeparator());
		report.append(buildGeneratorReportHeader().trim());
		report.append(FileUtil.getSystemLineSeparator());
		
		for (final GeneratorPlugin generator : generators) {
			report.append(FileUtil.getSystemLineSeparator());
			report.append("----------------------------------------------------------------------------------------------");
			report.append(FileUtil.getSystemLineSeparator());
			report.append(FileUtil.getSystemLineSeparator());
			report.append(generator.getId() + ": ");
			report.append(FileUtil.getSystemLineSeparator());
			report.append(FileUtil.getSystemLineSeparator());
			report.append(generator.getGeneratorReport().trim());
			report.append(FileUtil.getSystemLineSeparator());
		}
		report.append(FileUtil.getSystemLineSeparator());

        report.append("**************************************  E N D  ***********************************************");

		try {
			FileUtil.createNewFileWithContent(generatorReportFile, report.toString().trim());
			MOGLiLogUtil.logInfo("Report file created: " + generatorReportFile.getAbsolutePath());
		} catch (Exception e) {
			MOGLiLogUtil.logError("Error creating report file " + generatorReportFile.getAbsolutePath() + ": " + e.getMessage());
		}
	}
	

	private final String buildShortReportHeader() 
	{
		final int numberOfSuccessfullyExecutedPlugins = countNumberOfAllSuccessfullyExecutedPlugins();
		final int numberOfNotExecutedPlugins = countNumberOfNotExecutedPlugins();
		final StringBuffer toReturn = new StringBuffer("");
		
		toReturn.append("Workspace used: " + workspace);
		toReturn.append(FileUtil.getSystemLineSeparator());
		toReturn.append("Model read from file: " + getModelFileName());
		toReturn.append(FileUtil.getSystemLineSeparator());
		toReturn.append(FileUtil.getSystemLineSeparator());

		if (numberOfNotExecutedPlugins == 0) {
			toReturn.append("All " + numberOfSuccessfullyExecutedPlugins + " plugins executed successfully.");
			status = Status.OK;
		} else {
			toReturn.append(numberOfSuccessfullyExecutedPlugins + " plugin(s) executed successfully.");
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append(numberOfNotExecutedPlugins + " plugin(s) not or erroneously executed!");
			status = Status.ERROR;
		}

		toReturn.append(FileUtil.getSystemLineSeparator());
		toReturn.append("A total of " + countTotalNumberOfGenerations() + " generation events for "
		                  + countTotalNumberOfGeneratedArtefacts() + " output artefact(s) have been performed.");

		return toReturn.toString();
	}

	
	private int countNumberPlugins(final MOGLiPlugin.PluginType pluginType) {
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getPluginType() == pluginType) {
				counter++;
			}
		}
		return counter;
	}
	
	private int countNumberOfAllSuccessfullyExecutedPlugins() {
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getStatus() == PluginStatus.EXECUTED) {
				counter++;
			}
		}
		return counter;
	}
	
	private int countNumberOfSuccessfullyExecutedPlugins(final MOGLiPlugin.PluginType pluginType) 
	{
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getStatus() == PluginStatus.EXECUTED
				&& pluginMetaData.getPluginType() == pluginType) 
			{
				counter++;
			}
		}
		return counter;
	}


	private int countNumberOfNotExecutedPlugins() {
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getStatus() != PluginStatus.EXECUTED) {
				counter++;
			}
		}
		return counter;
	}

	private int countTotalNumberOfGenerations() {
		int counter = 0;
		if (plugins != null && plugins.size() > 0) 
		{
			final List<GeneratorPlugin> generators = plugins.get(0).getInfrastructure().getPluginsOfType(GeneratorPlugin.class);
			
			for (final GeneratorPlugin generator : generators) {
				counter += generator.getNumberOfGenerations();
			}
		}
		return counter;
	}

	private int countTotalNumberOfGeneratedArtefacts() 
	{
		int counter = 0;
		if (plugins != null && plugins.size() > 0) 
		{
			final List<GeneratorPlugin> generators = plugins.get(0).getInfrastructure().getPluginsOfType(GeneratorPlugin.class);
			
			for (final GeneratorPlugin generator : generators) {
				counter += generator.getNumberOfGeneratedArtefacts();
			}
		}
		return counter;
	}

	private String getModelName() {
		if (plugins != null && plugins.size() > 0) {
			final List<ModelProvider> modelProviders = plugins.get(0).getInfrastructure().getPluginsOfType(ModelProvider.class);
			if (modelProviders.size() > 0) {				
				return modelProviders.get(0).getModelName();
			}
		}
		return null;
	}

	private String getModelFileName() {
		String toReturn = "  ";
		if (plugins != null && plugins.size() > 0) {
			final List<ModelProvider> modelProviders = plugins.get(0).getInfrastructure().getPluginsOfType(ModelProvider.class);
			for (final ModelProvider modelProvider : modelProviders) {
				toReturn += modelProvider.getModelFileName() + ", ";  // in case their are more than one modelProviders
			}
			toReturn = toReturn.substring(0, toReturn.length() - 2);
		}

		return toReturn.trim();
	}

	public String getShortReportHeader()
	{
		return shortReportHeader;
	}

}
