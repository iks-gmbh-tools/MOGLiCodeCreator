package com.iksgmbh.moglicc.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiTextConstants2;
import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException2;
import com.iksgmbh.moglicc.exceptions.UnresolvableDependenciesException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;

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
	private List<MOGLiPlugin2> listOfPluginsWithUnresolvableDependencies;
	
	private List<PluginMetaData> pluginMetaDataList;
	
	// *****************************  Constructor  ************************************	
	
	DependencyResolver(List<PluginMetaData> pluginMetaDataList) {
		this.pluginMetaDataList = pluginMetaDataList;
	}

	public static List<MOGLiPlugin2> doYourJob(List<MOGLiPlugin2> pluginListToSort, 
			                                        List<PluginMetaData> pluginMetaDataList) {
		DependencyResolver dependencyResolver = new DependencyResolver(pluginMetaDataList);
		return dependencyResolver.resolveDependencies(pluginListToSort);
	}

	// *****************************  explicitely tested methods  ************************************
	
	List<MOGLiPlugin2> resolveDependencies(List<MOGLiPlugin2> pluginListToSort) throws UnresolvableDependenciesException {
		state = SOLVING_STATE.GO_ON;
		List<MOGLiPlugin2> sortedPluginList = new ArrayList<MOGLiPlugin2>();
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
	List<MOGLiPlugin2> solveDependenciesIfPossible(
			                     List<MOGLiPlugin2> pluginListToSort,
			                     List<MOGLiPlugin2> sortedPluginList) {
		List<MOGLiPlugin2> toReturn = new ArrayList<MOGLiPlugin2>();
		for (MOGLiPlugin2 plugin : pluginListToSort) {
			if (areAllDependenciesSolved(plugin)) {
				markPluginAsSolved(pluginListToSort, sortedPluginList, plugin);
			} else {
				toReturn.add(plugin);
			}
		}
		return toReturn;	
	}
	
	void updatePluginMetaData() {
		for (MOGLiPlugin2 plugin : listOfPluginsWithUnresolvableDependencies) {
			boolean doneForThisPlugin = false;
			for (PluginMetaData pluginMetaData : pluginMetaDataList) {
				if (pluginMetaData.getId().equals(plugin.getId())) {
					pluginMetaData.setInfoMessage(MOGLiTextConstants2.TEXT_UNRESOLVABLE_DEPENDENCIES);
					doneForThisPlugin = true;
					break;
				}
			}
			if (! doneForThisPlugin) {
				throw new MOGLiCoreException2("unknownPlugin: " + plugin.getId());
			}
		}
	}

	// *****************************  private methods  ************************************
	
	private void solveAllDependenciesIfPossible(List<MOGLiPlugin2> pluginListToSort,
			                                    List<MOGLiPlugin2> sortedPluginList) {
		List<MOGLiPlugin2> updatedPluginListToSort = null;
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

	private boolean areAllDependenciesSolved(MOGLiPlugin2 plugin) {
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

	private void markPluginAsSolved(List<MOGLiPlugin2> pluginListToSort,
			List<MOGLiPlugin2> sortedPluginList, MOGLiPlugin2 plugin) {
		sortedPluginList.add(plugin);
		solvedPluginsIDs.add(plugin.getId());
	}
	
	
	/**
	 * FOR TEST PURPOSE ONLY
	 */
	List<MOGLiPlugin2> getListOfPluginsWithUnresolvableDependencies() {
		return listOfPluginsWithUnresolvableDependencies;
	}

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	 void setListOfPluginsWithUnresolvableDependencies(List<MOGLiPlugin2> plugins) {
		this.listOfPluginsWithUnresolvableDependencies = plugins;
	}

}
