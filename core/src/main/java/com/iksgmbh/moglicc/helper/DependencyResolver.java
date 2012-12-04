package com.iksgmbh.moglicc.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiTextConstants;
import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.exceptions.UnresolvableDependenciesException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;

/**
 * Helps PluginExecutor to verify that all dependencies defined 
 * between the Mogli Plugins can be satisfied, when executing each plugin.
 * 
 * As byproduct, the original plugin list created by the PluginLoader
 * is sorted in a way, that plugins without dependencies are executed first
 * and plugins that depend on others are executed later.
 * 
 * @author Reik Oberrath
 */
public class DependencyResolver {
	
	private enum SOLVING_STATE { GO_ON, RESOLVED, UNRESOLVABLE };
	
	// **************************  Instance fields  *********************************	
	
	private SOLVING_STATE state;
	private HashSet<String> solvedPluginsIDs = new HashSet<String>();
	private List<MOGLiPlugin> listOfPluginsWithUnresolvableDependencies;
	
	private List<PluginMetaData> pluginMetaDataList;
	
	// *****************************  Constructor  ************************************	
	
	DependencyResolver(List<PluginMetaData> pluginMetaDataList) {
		this.pluginMetaDataList = pluginMetaDataList;
	}

	public static List<MOGLiPlugin> doYourJob(List<MOGLiPlugin> pluginListToSort, 
			                                        List<PluginMetaData> pluginMetaDataList) {
		DependencyResolver dependencyResolver = new DependencyResolver(pluginMetaDataList);
		return dependencyResolver.resolveDependencies(pluginListToSort);
	}

	// *****************************  explicitely tested methods  ************************************
	
	List<MOGLiPlugin> resolveDependencies(List<MOGLiPlugin> pluginListToSort) throws UnresolvableDependenciesException {
		state = SOLVING_STATE.GO_ON;
		List<MOGLiPlugin> sortedPluginList = new ArrayList<MOGLiPlugin>();
		solveAllDependenciesIfPossible(pluginListToSort, sortedPluginList);
		if (state == SOLVING_STATE.UNRESOLVABLE) {
			updatePluginMetaData();
			throw new UnresolvableDependenciesException();
		}
		return sortedPluginList;
	}

	/**
	 * Tries to resolve the dependencies of each plugin in pluginListToSort
	 * @param pluginListToSort: List of plugins with at least one unresolved dependency
	 * @param sortedPluginList: List of plugins with all dependencies resolved 
	 * @return updated list of plugins (pluginListToSort)
	 *         whose dependencies could (not) yet be resolved
	 */
	List<MOGLiPlugin> solveDependenciesIfPossible(
			                     List<MOGLiPlugin> pluginListToSort,
			                     List<MOGLiPlugin> sortedPluginList) {
		List<MOGLiPlugin> toReturn = new ArrayList<MOGLiPlugin>();
		for (MOGLiPlugin plugin : pluginListToSort) {
			if (areAllDependenciesSolved(plugin)) {
				markPluginAsSolved(pluginListToSort, sortedPluginList, plugin);
			} else {
				toReturn.add(plugin);
			}
		}
		return toReturn;	
	}
	
	void updatePluginMetaData() {
		for (MOGLiPlugin plugin : listOfPluginsWithUnresolvableDependencies) {
			boolean doneForThisPlugin = false;
			for (PluginMetaData pluginMetaData : pluginMetaDataList) {
				if (pluginMetaData.getId().equals(plugin.getId())) {
					pluginMetaData.setInfoMessage(MOGLiTextConstants.TEXT_UNRESOLVABLE_DEPENDENCIES);
					doneForThisPlugin = true;
					break;
				}
			}
			if (! doneForThisPlugin) {
				throw new MOGLiCoreException("unknownPlugin: " + plugin.getId());
			}
		}
	}

	// *****************************  private methods  ************************************
	
	private void solveAllDependenciesIfPossible(List<MOGLiPlugin> pluginListToSort,
			                                    List<MOGLiPlugin> sortedPluginList) {
		List<MOGLiPlugin> updatedPluginListToSort = null;
		while (state == SOLVING_STATE.GO_ON) {
			int numberUnsolvedPlugins = pluginListToSort.size();
			updatedPluginListToSort = solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
			if (updatedPluginListToSort.size() == 0) {
				state = SOLVING_STATE.RESOLVED;
			} else if (numberUnsolvedPlugins == updatedPluginListToSort.size()) {
				// number did not change so no more plugin can be resolved now
				state = SOLVING_STATE.UNRESOLVABLE;
				listOfPluginsWithUnresolvableDependencies = updatedPluginListToSort;
			}
			pluginListToSort = updatedPluginListToSort;
		}
	}

	private boolean areAllDependenciesSolved(MOGLiPlugin plugin) {
		List<String> dependencies = plugin.getDependencies();
		if (dependencies.size() == 0) {
			return true;
		}
		
		for (String dependency : dependencies) {
			if (! solvedPluginsIDs.contains(dependency)) {
				return false;
			}
		}
		return true;
	}

	private void markPluginAsSolved(List<MOGLiPlugin> pluginListToSort,
			List<MOGLiPlugin> sortedPluginList, MOGLiPlugin plugin) {
		sortedPluginList.add(plugin);
		solvedPluginsIDs.add(plugin.getId());
	}
	
	
	/**
	 * FOR TEST PURPOSE ONLY
	 */
	List<MOGLiPlugin> getListOfPluginsWithUnresolvableDependencies() {
		return listOfPluginsWithUnresolvableDependencies;
	}

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	 void setListOfPluginsWithUnresolvableDependencies(List<MOGLiPlugin> plugins) {
		this.listOfPluginsWithUnresolvableDependencies = plugins;
	}

}
