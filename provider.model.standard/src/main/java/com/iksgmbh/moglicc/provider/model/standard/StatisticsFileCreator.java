package com.iksgmbh.moglicc.provider.model.standard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoCounter;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.utils.FileUtil;

public class StatisticsFileCreator {
	
	private StandardModelProviderStarter modelProvider;
	private InfrastructureService infrastructure;
	private BuildUpModel model;
	
	public static void doYourJob(final StandardModelProviderStarter modelProvider) throws MOGLiPluginException {
		new StatisticsFileCreator(modelProvider).createStatisticsFile();
	}

	public StatisticsFileCreator(final StandardModelProviderStarter modelProvider) throws MOGLiPluginException {
		this.modelProvider = modelProvider;
		infrastructure = modelProvider.getInfrastructure();
		model = (BuildUpModel) modelProvider.getUnvalidatedModel();
	}
	
	private void createStatisticsFile()	throws MOGLiPluginException {
		final File file = new File(infrastructure.getPluginOutputDir(), 
				                   StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		final StringBuffer sb = new StringBuffer();
		sb.append("Model Name: ").append(model.getName()).append(FileUtil.getSystemLineSeparator());
		sb.append("Number of class descriptions in model file: ").append(model.getSize()).append(FileUtil.getSystemLineSeparator());
		appendPluginInformation(sb);
		appendWarningForUnusedMetaInfos(sb);
		appendWarningForUnusedMetaInfoValidators(sb);
		
		try {
			FileUtil.createNewFileWithContent(file, sb.toString());
		} catch (Exception e) {
			throw new MOGLiPluginException("Error creating file "
					+ file.getAbsolutePath());
		}
		infrastructure.getPluginLogger().logInfo("Model Statistics file created.");
		
	}

	private void appendWarningForUnusedMetaInfoValidators(StringBuffer sb) throws MOGLiPluginException {
		final String sep = FileUtil.getSystemLineSeparator();
		final List<MetaInfoValidator> metaInfoValidatorList = modelProvider.getAllMetaInfoValidators();
		boolean firstTime = true;
		for (final MetaInfoValidator metaInfoValidator : metaInfoValidatorList) {
			if (metaInfoValidator instanceof MetaInfoCounter) {
				MetaInfoCounter metaInfoCounter = (MetaInfoCounter) metaInfoValidator;
				if (metaInfoCounter.getMetaInfoMatches() == 0) {
					if (firstTime) {
						firstTime = false;
						sb.append(sep).append(sep).append("WARNING:").append(sep);
						sb.append("Following MetaInfoValidator are unused:");
						sb.append(sep);
					}
					sb.append(metaInfoValidator.getValidationType() + " Validator ");
					sb.append("for MetaInfo " + "'" + metaInfoValidator.getMetaInfoName());
					sb.append("' in " + metaInfoValidator.getMetaInfoHierarchyLevel().name() + " Level ");
					sb.append("defined in " + metaInfoValidator.getVendorPluginId());
					sb.append(sep);
				}
			}
		}
	}

	private void appendWarningForUnusedMetaInfos(final StringBuffer sb) throws MOGLiPluginException {
		final String sep = FileUtil.getSystemLineSeparator();
		final List<MetaInfo> allMetaInfos = model.getAllMetaInfos();
		final List<String> uniqueUnusedMetaInfoMessages = new ArrayList<String>();
		for (final MetaInfo metaInfo : allMetaInfos) {
			if (metaInfo.getPluginList().size() == 0) {
				uniqueUnusedMetaInfoMessages.add("'" + metaInfo.getName() + "' in " 
						+ metaInfo.getHierarchyLevel().name() + "Level");
			}
		}
		if (uniqueUnusedMetaInfoMessages.size() > 0) {
			sb.append(sep).append(sep).append("WARNING:").append(sep);
			sb.append("Following MetaInfos defined in '" + modelProvider.getModelFile().getName() 
					   + "' are unused:");
			sb.append(sep);
			int counter = 0;
			for (final String entry : uniqueUnusedMetaInfoMessages) {
				counter++;
				sb.append(entry);
				if (counter < uniqueUnusedMetaInfoMessages.size()) sb.append(sep);
			}
		}
	}

	private void appendPluginInformation(final StringBuffer sb) {
		final HashMap<String, HashSet<String>> metaInfoNamesForPluginIds = model.getMetaInfoNamesForPluginIds();
		sb.append("Number of vendor plugins defining MetaInfoValidators: ").append(metaInfoNamesForPluginIds.size());
		appendUsageInfo(sb, metaInfoNamesForPluginIds);
	}

	private void appendUsageInfo(final StringBuffer sb, final HashMap<String, HashSet<String>> metaInfoNamesForPluginIds) {
		final Set<String> keySet = metaInfoNamesForPluginIds.keySet();
		final List<String> sortedList = new ArrayList<String>(keySet);
		Collections.sort(sortedList);
		
		for (final String pluginID : sortedList) {		
			sb.append(FileUtil.getSystemLineSeparator()).append(FileUtil.getSystemLineSeparator());
			sb.append("Plugin '" + pluginID + "' uses the following MetaInfo elements:").append(FileUtil.getSystemLineSeparator());
			final HashSet<String> metaInfoNames = metaInfoNamesForPluginIds.get(pluginID);
			
			final List<String> sortedNames = new ArrayList<String>(metaInfoNames);
			Collections.sort(sortedNames);
			
			final int size = sortedNames.size();
			int counter = 0;
			for (final String metaInfoName : sortedNames) {
				sb.append(metaInfoName);
				counter++;
				if (counter < size) {
					sb.append(FileUtil.getSystemLineSeparator());
				}
			}
		}
	}

}
