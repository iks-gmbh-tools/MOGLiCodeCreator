package com.iksgmbh.moglicc.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.moglicc.plugin.MogliPlugin;
import com.iksgmbh.moglicc.plugin.MogliPlugin.PluginType;
import com.iksgmbh.moglicc.plugin.PluginExecutable;
import com.iksgmbh.moglicc.plugin.type.basic.DataProvider;
import com.iksgmbh.moglicc.plugin.type.basic.EngineProvider;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;
import com.iksgmbh.moglicc.plugin.type.basic.ModelProvider;

/**
 * Implementation to provide the MogliCodeCreator core functionality to the plugins.
 * For each plugin a logger with an individual logfile and an individual resultDir is provided.
 * 
 * @author Reik Oberrath
 */
public class MogliInfrastructure implements InfrastructureService {
	
	private InfrastructureInitData initData;

	private HashMap<String, MogliPlugin> pluginMap;
	
	private Properties applicationProperties;
	
	private File applicationRootDir;
	private File applicationHelpDir;

	// workspace dependent fields
	private File workspaceInputDir;
	private File workspaceOutputDir;
	private File workspaceLogsDir;
	private File workspaceTempDir;
	
	// plugin dependent fields
	private String idOfCurrentlyExecutedPlugin;
	private File pluginInputDir;
	private File pluginOutputDir;
	private File pluginLogFile;
	private File pluginTempDir;
	private File pluginHelpDir;
	private Logger pluginLogger;

	
	
	public MogliInfrastructure(InfrastructureInitData initData) {
		this.initData = initData;
		
		idOfCurrentlyExecutedPlugin = initData.idOfThePluginToThisInfrastructure;
		pluginMap = createPluginMap();
		
		applicationRootDir = initData.dirApplicationRoot;
		applicationHelpDir = initData.helpDir;
		
		workspaceInputDir = initData.inputDir;
		workspaceOutputDir = initData.outputDir;
		workspaceLogsDir = initData.logsDir;
		workspaceTempDir = initData.tempDir;
		applicationProperties = initData.applicationProperties;
	}

	private HashMap<String, MogliPlugin> createPluginMap() {
		final HashMap<String, MogliPlugin> map = new HashMap<String, MogliPlugin>();
		for (PluginExecutable plugin : initData.pluginList) {
			map.put(plugin.getId(), (MogliPlugin)plugin);
		}
		return map;
	}

	@Override
	public ModelProvider getModelProvider(String id) {
		 MogliPlugin plugin = pluginMap.get(id);
		 if (plugin != null && 
			 PluginType.MODEL_PROVIDER == plugin.getPluginType()) {
			 return (ModelProvider) plugin;
		 }
		 return null;
	}

	@Override
	public DataProvider getDataProvider(String id) {
		 MogliPlugin plugin = pluginMap.get(id);
		 if (plugin != null && 
			 PluginType.DATA_PROVIDER == plugin.getPluginType()) {
			 return (DataProvider) plugin;
		 }
		 return null;
	}

	@Override
	public EngineProvider getEngineProvider(String id) {
		 MogliPlugin plugin = pluginMap.get(id);
		 if (plugin != null && 
			 PluginType.ENGINE_PROVIDER == plugin.getPluginType()) {
			 return (EngineProvider) plugin;
		 }
		 return null;
	}

	@Override
	public Generator getGenerator(String id) {
		 MogliPlugin plugin = pluginMap.get(id);
		 if (plugin != null && 
			 PluginType.GENERATOR == plugin.getPluginType()) {
			 return (Generator) plugin;
		 }
		 return null;
	}

	@Override
	public File getWorkspaceTempDir() {
		if (! workspaceTempDir.exists()) {
			workspaceTempDir.mkdirs();
		}
		return workspaceTempDir;
	}
	
	@Override
	public File getWorkspaceLogsDir() {
		if (! workspaceLogsDir.exists()) {
			workspaceLogsDir.mkdirs();
		}
		return workspaceLogsDir;
	}


	@Override
	public File getPluginInputDir() {
		if (pluginInputDir == null) {
			pluginInputDir = new File(workspaceInputDir, idOfCurrentlyExecutedPlugin);
		}
		return pluginInputDir;
	}

	@Override
	public File getPluginOutputDir() {
		if (pluginOutputDir == null) {
			pluginOutputDir = new File(workspaceOutputDir, idOfCurrentlyExecutedPlugin);
			pluginOutputDir.mkdirs(); // create if not existing
		}
		return pluginOutputDir;
	}

	@Override
	public File getPluginTempDir() {
		if (pluginTempDir == null) {
			pluginTempDir = new File(workspaceTempDir, idOfCurrentlyExecutedPlugin);
			pluginTempDir.mkdirs(); // create if not existing
		}
		return pluginTempDir;
	}

	@Override
	public File getPluginHelpDir() {
		if (pluginHelpDir == null) {
			pluginHelpDir = new File(applicationHelpDir, idOfCurrentlyExecutedPlugin);
		}
		return pluginHelpDir;
	}

	@Override
	public String toString() {
		return "MogliInfrastructure [pluginMap=" + pluginMap
				+ ", applicationProperties=" + applicationProperties
				+ ", applicationRootDir=" + applicationRootDir
				+ ", workspaceInputDir=" + workspaceInputDir
				+ ", workspaceOutputDir=" + workspaceOutputDir
				+ ", workspaceLogsDir=" + workspaceLogsDir
				+ ", workspaceTempDir=" + workspaceTempDir
				+ ", idOfCurrentlyExecutedPlugin="
				+ idOfCurrentlyExecutedPlugin + ", pluginInputDir="
				+ pluginInputDir + ", pluginOutputDir=" + pluginOutputDir
				+ ", pluginTempDir=" + pluginTempDir + ", pluginLogFile="
				+ pluginLogFile + ", pluginLogger=" + pluginLogger + "]";
	}

	@Override
	public File getPluginLogFile() {
		if (pluginLogFile == null) {
			pluginLogFile = new File(getWorkspaceLogsDir(), idOfCurrentlyExecutedPlugin + ".log");
			try {
				pluginLogFile.createNewFile();
			} catch (IOException e) {
				throw new MogliCoreException("Could not create file " + pluginLogFile.getAbsolutePath(), e);
			}
		}
		return pluginLogFile;
	}

	@Override
	public Logger getPluginLogger() {
		if (pluginLogger == null) {
			pluginLogger = new MogliLogger(getPluginLogFile());
		}
		return pluginLogger;
	}
	

	@Override
	public File getApplicationRootDir() {
		return applicationRootDir;
	}

	@Override
	public String getWorkspaceProperty(final String key) {
		return applicationProperties.getProperty(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getPluginsOfType(final Class<T> wantedType) {
		final List<T> toReturn = new ArrayList<T>();
		final Collection<MogliPlugin> plugins = pluginMap.values();
		for (final MogliPlugin plugin : plugins) {
			final Class<?>[] interfaces = plugin.getClass().getInterfaces();
			for (final Class<?> type : interfaces) {
				if (type.getName().equals(wantedType.getName())) {
					toReturn.add((T) plugin);
					break;
				}	
			}
		}
		return toReturn;
	}

}
