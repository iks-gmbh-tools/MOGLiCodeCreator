package com.iksgmbh.moglicc.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.iksgmbh.moglicc.MogliTextConstants;
import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.moglicc.exceptions.UnresolvableDependenciesException;
import com.iksgmbh.moglicc.plugin.PluginExecutable;

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
	private List<PluginExecutable> listOfPluginsWithUnresolvableDependencies;
	
	private List<PluginMetaData> pluginMetaDataList;
	
	// *****************************  Constructor  ************************************	
	
	DependencyResolver(List<PluginMetaData> pluginMetaDataList) {
		this.pluginMetaDataList = pluginMetaDataList;
	}

	public static List<PluginExecutable> doYourJob(List<PluginExecutable> pluginListToSort, 
			                                        List<PluginMetaData> pluginMetaDataList) {
		DependencyResolver dependencyResolver = new DependencyResolver(pluginMetaDataList);
		return dependencyResolver.resolveDependencies(pluginListToSort);
	}

	// *****************************  explicitely tested methods  ************************************
	
	List<PluginExecutable> resolveDependencies(List<PluginExecutable> pluginListToSort) throws UnresolvableDependenciesException {
		state = SOLVING_STATE.GO_ON;
		List<PluginExecutable> sortedPluginList = new ArrayList<PluginExecutable>();
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
	List<PluginExecutable> solveDependenciesIfPossible(
			                     List<PluginExecutable> pluginListToSort,
			                     List<PluginExecutable> sortedPluginList) {
		List<PluginExecutable> toReturn = new ArrayList<PluginExecutable>();
		for (PluginExecutable plugin : pluginListToSort) {
			if (areAllDependenciesSolved(plugin)) {
				markPluginAsSolved(pluginListToSort, sortedPluginList, plugin);
			} else {
				toReturn.add(plugin);
			}
		}
		return toReturn;	
	}
	
	void updatePluginMetaData() {
		for (PluginExecutable plugin : listOfPluginsWithUnresolvableDependencies) {
			boolean doneForThisPlugin = false;
			for (PluginMetaData pluginMetaData : pluginMetaDataList) {
				if (pluginMetaData.getId().equals(plugin.getId())) {
					pluginMetaData.setInfoMessage(MogliTextConstants.TEXT_UNRESOLVABLE_DEPENDENCIES);
					doneForThisPlugin = true;
					break;
				}
			}
			if (! doneForThisPlugin) {
				throw new MogliCoreException("unknownPlugin: " + plugin.getId());
			}
		}
	}

	// *****************************  private methods  ************************************
	
	private void solveAllDependenciesIfPossible(List<PluginExecutable> pluginListToSort,
			                                    List<PluginExecutable> sortedPluginList) {
		List<PluginExecutable> updatedPluginListToSort = null;
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

	private boolean areAllDependenciesSolved(PluginExecutable plugin) {
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

	private void markPluginAsSolved(List<PluginExecutable> pluginListToSort,
			List<PluginExecutable> sortedPluginList, PluginExecutable plugin) {
		sortedPluginList.add(plugin);
		solvedPluginsIDs.add(plugin.getId());
	}
	
	
	/**
	 * FOR TEST PURPOSE ONLY
	 */
	List<PluginExecutable> getListOfPluginsWithUnresolvableDependencies() {
		return listOfPluginsWithUnresolvableDependencies;
	}

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	 void setListOfPluginsWithUnresolvableDependencies(List<PluginExecutable> plugins) {
		this.listOfPluginsWithUnresolvableDependencies = plugins;
	}

}
