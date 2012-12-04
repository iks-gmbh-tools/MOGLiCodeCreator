package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_UNEXPECTED_PROBLEM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.ExceptionUtil;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

/**
 * Helps MOGLi class to do its job
 * @author Reik Oberrath
 */
public class PluginExecutor {

	private HashMap<String, PluginMetaData> pluginMetaDataHashMap;
	private InfrastructureInitData infrastructureInitData;
	private List<PluginMetaData> pluginMetaDataListForNotExecutedPlugins;
	
	public static class PluginExecutionData {
		List<MOGLiPlugin> pluginsToExecute;
		List<PluginMetaData> pluginMetaDataList;
		InfrastructureInitData infrastructureInitData;
		
		public PluginExecutionData( final List<MOGLiPlugin> pluginsToExecute,
									final List<PluginMetaData> pluginMetaDataList, 
									final InfrastructureInitData infrastructureInitData) {
			this.pluginsToExecute = pluginsToExecute;
			this.pluginMetaDataList = pluginMetaDataList;
			this.infrastructureInitData = infrastructureInitData;
		}
	}
	
	/**
	 * @param pluginExecutionData
	 * @return pluginMetaDataList sorted by execution order
	 */
	public static List<PluginMetaData> doYourJob(PluginExecutionData pluginExecutionData) {
		PluginExecutor pluginExecutor = new PluginExecutor(pluginExecutionData);
		List<MOGLiPlugin> sortedPluginsToExecute = 
						DependencyResolver.doYourJob(pluginExecutionData.pluginsToExecute,
													 pluginExecutionData.pluginMetaDataList);
		pluginExecutor.executePlugins(sortedPluginsToExecute);
		return pluginExecutor.sortPluginMetaDataListByExecutionOrder(sortedPluginsToExecute);
	}

	@SuppressWarnings("unchecked")
	List<PluginMetaData> sortPluginMetaDataListByExecutionOrder(final List<MOGLiPlugin> sortedPluginsToExecute) {
		final List<PluginMetaData> list = new ArrayList<PluginMetaData>();
		
		// add pluginMetaData in execution order to list
		for (MOGLiPlugin plugin : sortedPluginsToExecute) {
			list.add(pluginMetaDataHashMap.get(plugin.getId()));
			pluginMetaDataHashMap.remove(plugin.getId());
		}
		
		// add pluginMetaData of not executed plugins due to missing dependencies
		final Set<String> keySet = pluginMetaDataHashMap.keySet();
		for (String key : keySet) {
			list.add(pluginMetaDataHashMap.get(key));
		}
		
		// add pluginMetaData of not executed plugins due to missing ID
		list.addAll(pluginMetaDataListForNotExecutedPlugins);

		return ImmutableUtil.getImmutableListFromLists(list);
	}

	PluginExecutor(final PluginExecutionData pluginExecutionData) {
		pluginMetaDataHashMap = new HashMap<String, PluginMetaData>();
		pluginMetaDataListForNotExecutedPlugins = new ArrayList<PluginMetaData>();
		filterPluginsWithId(pluginExecutionData);
		this.infrastructureInitData = pluginExecutionData.infrastructureInitData;
	}

	private void filterPluginsWithId(final PluginExecutionData pluginExecutionData) {
		for (PluginMetaData pluginMetaData : pluginExecutionData.pluginMetaDataList) {
			final String id = pluginMetaData.getId();
			if (id != null) {
				pluginMetaDataHashMap.put(id, pluginMetaData); // these are possibly executable
			} else {
				pluginMetaDataListForNotExecutedPlugins.add(pluginMetaData);
			}
		}
	}
	
	private void setInfrastructureToPlugins(final List<MOGLiPlugin> sortedPluginsToExecute) {
		for (final MOGLiPlugin plugin : sortedPluginsToExecute) {
			plugin.setMOGLiInfrastructure(getInfrastructureFor(plugin.getId()));
		}
	}
	
	MOGLiInfrastructure getInfrastructureFor(final String pluginID) {
		infrastructureInitData.idOfThePluginToThisInfrastructure = pluginID;
		return new MOGLiInfrastructure(infrastructureInitData);
	}
	
	private void unpackPluginData(final List<MOGLiPlugin> sortedPluginsToExecute) {
		MOGLiLogUtil.logInfo("---");
		MOGLiLogUtil.logInfo("Unpacking Plugin Data...");
		int counter = 0;
		for (final MOGLiPlugin plugin : sortedPluginsToExecute) {
			counter++;
			MOGLiLogUtil.logInfo("\n");
			MOGLiLogUtil.logInfo(counter + ". Checking for unpacking plugin data from plugin '" 
					+ plugin.getId() + "'...");
			try {
				unpackDefaultInputDataIfNecessary(plugin);
				unpackHelpDataIfNecessary(plugin);
			} catch (MOGLiPluginException e) {
				final PluginMetaData pluginMetaData = getMetaData(plugin);
				pluginMetaData.setInfoMessage(e.getPluginErrorMessage());
			}
		}
	}

	void executePlugins(final List<MOGLiPlugin> sortedPluginsToExecute) {
		infrastructureInitData.pluginList = sortedPluginsToExecute;
		
		// step 1: set individual infrastructure to each plugin
		setInfrastructureToPlugins(sortedPluginsToExecute);
		
		// step 2: unpack Plugin Data of all plugins
		unpackPluginData(sortedPluginsToExecute);
		
		// step 3: call doYourJob and set Plugin Status
		letPluginsDoTheirJob(sortedPluginsToExecute);
	}

	protected void letPluginsDoTheirJob(
		final List<MOGLiPlugin> sortedPluginsToExecute) {
		MOGLiLogUtil.logInfo("---");
		MOGLiLogUtil.logInfo("Executing Plugins...");
		int counter = 0;
		for (MOGLiPlugin plugin : sortedPluginsToExecute) {
			counter++;
			MOGLiLogUtil.logInfo("\n");
			MOGLiLogUtil.logInfo(counter + ". Executing " + plugin.getId() + "...");
			PluginMetaData pluginMetaData = getMetaData(plugin);
			String infoMessage = pluginMetaData.getInfoMessage();
			try {
				plugin.doYourJob();
				pluginMetaData.setPluginStatus(PluginStatus.EXECUTED);
				MOGLiLogUtil.logInfo(plugin.getId() + " Done!");
			} catch (MOGLiPluginException e) {
				infoMessage = e.getPluginErrorMessage();
			} catch (Throwable t) {
				final Throwable rootCause = ExceptionUtils.getRootCause(t);
				if (rootCause == null) {
					infoMessage = TEXT_UNEXPECTED_PROBLEM + t.getClass().getSimpleName() + ": " + t.getMessage();
				} else {
					infoMessage = TEXT_UNEXPECTED_PROBLEM + rootCause.getMessage();
				}
				infoMessage += FileUtil.getSystemLineSeparator() + ExceptionUtil.getStackTraceAsString(t);
			}
			pluginMetaData.setInfoMessage(infoMessage);
		}
	}

	private void unpackDefaultInputDataIfNecessary(final MOGLiPlugin plugin) throws MOGLiPluginException {
		final MOGLiInfrastructure infrastructure = (MOGLiInfrastructure) plugin.getMOGLiInfrastructure();
		if (! infrastructure.getPluginInputDir().exists()) {
			boolean created = plugin.unpackDefaultInputData();
			if (created) {
				try {
					MOGLiLogUtil.logInfo("For " + plugin.getId() + " pluginInputDir "  
							+ infrastructure.getPluginInputDir().getCanonicalPath() + " has been created!");
				} catch (IOException e) {
					throw new MOGLiCoreException("Error building CanonicalPath for " + 
							infrastructure.getPluginInputDir().getAbsolutePath(), e);
				} 
			} else {
				MOGLiLogUtil.logInfo("For " + plugin.getId() + " no default input data to unpack.");
			}
		} else {
			MOGLiLogUtil.logInfo("For " + plugin.getId() + " the pluginInputDir exists.");  
		}
	}

	private void unpackHelpDataIfNecessary(final MOGLiPlugin plugin) throws MOGLiPluginException {
		final MOGLiInfrastructure infrastructure = (MOGLiInfrastructure) plugin.getMOGLiInfrastructure();
		if (! infrastructure.getPluginHelpDir().exists()) {
			boolean created = plugin.unpackPluginHelpFiles();
			if (created) {
				try {
					MOGLiLogUtil.logInfo("For " + plugin.getId() + " pluginHelpDir "  
							+ infrastructure.getPluginInputDir().getCanonicalPath() + " has been created!");
				} catch (IOException e) {
					throw new MOGLiCoreException("Error building CanonicalPath for " + 
							infrastructure.getPluginInputDir().getAbsolutePath(), e);
				}
			} else {
				MOGLiLogUtil.logInfo("For " + plugin.getId() + " no help data to unpack.");
			}
		} else {
			MOGLiLogUtil.logInfo("For " + plugin.getId() + " the pluginHelpDir exists.");  
		}
	}

	PluginMetaData getMetaData(final MOGLiPlugin plugin) {
		return pluginMetaDataHashMap.get(plugin.getId());
	}
	
}
