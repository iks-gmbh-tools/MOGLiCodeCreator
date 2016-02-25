/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.StringUtil;

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
	private List<String> idsOfDeactivatedPlugins;
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
		
		findDeactivatedPlugins();
		this.shortReportHeader = buildShortReportHeader();
	}
	
	private void findDeactivatedPlugins()
	{
		idsOfDeactivatedPlugins = new ArrayList<String>();
		
		for (final PluginMetaData pluginMetaData : pluginMetaDataList) 
		{
			if ( MOGLiTextConstants.TEXT_DEACTIVATED_PLUGIN_INFO.equals(pluginMetaData.getInfoMessage()) )
			{
				idsOfDeactivatedPlugins.add(pluginMetaData.getId());
			}
		}
	}
	
	public String getShortReportHeader()
	{
		return shortReportHeader;
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
		sortGenerators(generators);
		final List<ProviderPlugin> providers = plugins.get(0).getInfrastructure().getPluginsOfType(ProviderPlugin.class);
		sortProviders(providers);
		
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
		sortProviders(providers);
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
		sortGenerators(generators);
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
		final int numberOfDeactivatedPlugins = idsOfDeactivatedPlugins.size();
		final StringBuffer toReturn = new StringBuffer("");
		
		toReturn.append("Workspace: " + workspace);
		toReturn.append(FileUtil.getSystemLineSeparator());
		toReturn.append("Model file: " + getModelFileName());
		toReturn.append(FileUtil.getSystemLineSeparator());
		toReturn.append(FileUtil.getSystemLineSeparator());

		if ( numberOfNotExecutedPlugins == 0) {
			toReturn.append("Execution of all " + numberOfSuccessfullyExecutedPlugins + " plugins successful.");
			status = Status.OK;
		} else if ( (numberOfNotExecutedPlugins - numberOfDeactivatedPlugins) == 0) {
				toReturn.append("Execution of all " + numberOfSuccessfullyExecutedPlugins + " activated plugins successful.");
				status = Status.OK;
		} else {
			toReturn.append(numberOfSuccessfullyExecutedPlugins + " plugin(s) executed successfully.");
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append(numberOfNotExecutedPlugins + " plugin(s) not or erroneously executed!");
			status = Status.ERROR;
		}

		if (numberOfDeactivatedPlugins > 0)
		{
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append("WARNING: The following plugin(s) are deactivated in the workspace properties: " + StringUtil.concat(idsOfDeactivatedPlugins));
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
			for (final ModelProvider modelProvider : modelProviders) 
			{
				final String modelFileName = modelProvider.getModelFileName();
				if (! StringUtils.isEmpty(modelFileName))
				{
					toReturn += modelFileName + ", ";  // in case their are more than one modelProviders
				}
			}
			toReturn = toReturn.substring(0, toReturn.length() - 2);
		}

		return toReturn.trim();
	}
	
	private void sortProviders(List<ProviderPlugin> providers) 
	{
		Collections.sort(providers, new Comparator<ProviderPlugin>() {

			@Override
			public int compare(ProviderPlugin p1, ProviderPlugin p2)
			{
				return p1.getId().compareTo(p2.getId());
			}
		});
	}
	
	private void sortGenerators(List<GeneratorPlugin> generators) 
	{
		Collections.sort(generators, new Comparator<GeneratorPlugin>() {

			@Override
			public int compare(GeneratorPlugin p1, GeneratorPlugin p2)
			{
				return p1.getId().compareTo(p2.getId());
			}
		});
	}

	List<String> getIdsOfDeactivatedPlugins()
	{
		return idsOfDeactivatedPlugins;
	}

}