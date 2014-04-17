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
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;

/**
 * Implementation to provide the MOGLiCodeCreator core functionality to the plugins.
 * For each plugin a logger with an individual logfile and an individual resultDir is provided.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class MOGLiInfrastructure implements InfrastructureService {

	private InfrastructureInitData initData;

	private HashMap<String, MOGLiPlugin> pluginMap;

	private Properties workspaceProperties;

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



	public MOGLiInfrastructure(final InfrastructureInitData initData) {
		this.initData = initData;

		idOfCurrentlyExecutedPlugin = initData.idOfThePluginToThisInfrastructure;
		pluginMap = createPluginMap();

		applicationRootDir = initData.dirApplicationRoot;
		applicationHelpDir = initData.helpDir;

		workspaceInputDir = initData.inputDir;
		workspaceOutputDir = initData.outputDir;
		workspaceLogsDir = initData.logsDir;
		workspaceTempDir = initData.tempDir;
		workspaceProperties = initData.workspaceProperties;
	}

	private HashMap<String, MOGLiPlugin> createPluginMap() {
		final HashMap<String, MOGLiPlugin> map = new HashMap<String, MOGLiPlugin>();
		for (MOGLiPlugin plugin : initData.pluginList) {
			map.put(plugin.getId(), (MOGLiPlugin)plugin);
		}
		return map;
	}

	@Override
	public ProviderPlugin getProvider(String id) {
		 MOGLiPlugin plugin = pluginMap.get(id);
		 if (plugin != null &&
			 MOGLiPlugin.PluginType.PROVIDER == plugin.getPluginType()) {
			 return (ProviderPlugin) plugin;
		 }
		 return null;
	}

	@Override
	public GeneratorPlugin getGenerator(String id) {
		 MOGLiPlugin plugin = pluginMap.get(id);
		 if (plugin != null &&
			 MOGLiPlugin.PluginType.GENERATOR == plugin.getPluginType()) {
			 return (GeneratorPlugin) plugin;
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
				+ ", applicationProperties=" + workspaceProperties
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
				throw new MOGLiCoreException("Could not create file " + pluginLogFile.getAbsolutePath(), e);
			}
		}
		return pluginLogFile;
	}

	@Override
	public Logger getPluginLogger() {
		if (pluginLogger == null) {
			pluginLogger = new MOGLiLogger(getPluginLogFile());
		}
		return pluginLogger;
	}


	@Override
	public File getApplicationRootDir() {
		return applicationRootDir;
	}

	@Override
	public String getWorkspaceProperty(final String key) {
		return workspaceProperties.getProperty(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getPluginsOfType(final Class<T> wantedType) {
		final List<T> toReturn = new ArrayList<T>();
		final Collection<MOGLiPlugin> plugins = pluginMap.values();
		for (final MOGLiPlugin plugin : plugins) {
			final List<Class<?>> interfaces = collectInterfacesFor(plugin);
			for (final Class<?> type : interfaces) {
				if (type.getName().equals(wantedType.getName())) {
					toReturn.add((T) plugin);
					break;
				}
			}
		}
		return toReturn;
	}

	protected List<Class<?>> collectInterfacesFor(final MOGLiPlugin plugin) {
		final List<Class<?>> toReturn = new ArrayList<Class<?>>();
		final Class<?>[] interfaces = plugin.getClass().getInterfaces();
		for (final Class<?> interfaceType : interfaces) {
			toReturn.add(interfaceType);
			collectInheritedInterfaces(toReturn, interfaceType);
		}
		return toReturn;
	}

	private void collectInheritedInterfaces(final List<Class<?>> toReturn, final Class<?> interfaceType) {
		final Class<?>[] inhertitedInterfaces = interfaceType.getInterfaces();
		if (inhertitedInterfaces != null && inhertitedInterfaces.length > 0) {
			for (final Class<?> inhertitedInterfaceType : inhertitedInterfaces) {
				toReturn.add(inhertitedInterfaceType);
				collectInheritedInterfaces(toReturn, inhertitedInterfaceType);
			}
		}

	}

}