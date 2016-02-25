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
 * @since 1.0.0
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

	public static List<MOGLiPlugin> doYourJob(final List<MOGLiPlugin> pluginListToSort, 
			                                  final List<PluginMetaData> pluginMetaDataList) 
	{
		DependencyResolver dependencyResolver = new DependencyResolver(pluginMetaDataList);
		return dependencyResolver.resolveDependencies(pluginListToSort);
	}

	// *****************************  explicitely tested methods  ************************************
	
	List<MOGLiPlugin> resolveDependencies(final List<MOGLiPlugin> pluginListToSort) throws UnresolvableDependenciesException 
	{
		state = SOLVING_STATE.GO_ON;
		List<MOGLiPlugin> sortedPluginList = new ArrayList<MOGLiPlugin>();
		solveAllDependenciesIfPossible(clone(pluginListToSort), sortedPluginList);
		if (state == SOLVING_STATE.UNRESOLVABLE) {
			updatePluginMetaData();
			throw new UnresolvableDependenciesException();
		}
		return sortedPluginList;
	}

	private List<MOGLiPlugin> clone(final List<MOGLiPlugin> pluginListToSort)
	{
		final List<MOGLiPlugin> toReturn = new ArrayList<MOGLiPlugin>();
		for (final MOGLiPlugin mogLiPlugin : pluginListToSort)
		{
			toReturn.add(mogLiPlugin);
		}
		return toReturn;
	}

	/**
	 * Tries to resolve the dependencies of each plugin in pluginListToSort
	 * @param pluginListToSort: List of plugins with at least one unresolved dependency
	 * @param sortedPluginList: List of plugins with all dependencies resolved 
	 * @return updated list of plugins (pluginListToSort)
	 *         whose dependencies could (not) yet be resolved
	 */
	List<MOGLiPlugin> solveDependenciesIfPossible(final List<MOGLiPlugin> pluginListToSort,
			                                      final List<MOGLiPlugin> sortedPluginList) 
	{
		final List<MOGLiPlugin> toReturn = new ArrayList<MOGLiPlugin>();
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
			for (final PluginMetaData pluginMetaData : pluginMetaDataList) {
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
	
	private void solveAllDependenciesIfPossible(final List<MOGLiPlugin> pluginListToSort,
			                                    final List<MOGLiPlugin> sortedPluginList) 
	{
		List<MOGLiPlugin> presortedPluginList = presortForSuggestedExecutionOrder(pluginListToSort);
		List<MOGLiPlugin> updatedPluginListToSort = null;
		while (state == SOLVING_STATE.GO_ON) {
			int numberUnsolvedPlugins = presortedPluginList.size();
			updatedPluginListToSort = solveDependenciesIfPossible(presortedPluginList, sortedPluginList);
			if (updatedPluginListToSort.size() == 0) {
				state = SOLVING_STATE.RESOLVED;
			} else if (numberUnsolvedPlugins == updatedPluginListToSort.size()) {
				// number did not change so no more plugin can be resolved now
				state = SOLVING_STATE.UNRESOLVABLE;
				listOfPluginsWithUnresolvableDependencies = updatedPluginListToSort;
			}
			presortedPluginList = updatedPluginListToSort;
		}
	}

	private List<MOGLiPlugin> presortForSuggestedExecutionOrder(final List<MOGLiPlugin> pluginListToSort)
	{
		final List<MOGLiPlugin> toReturn = new ArrayList<MOGLiPlugin>();
		while (pluginListToSort.size() > 0) 
		{
			MOGLiPlugin pluginWithMinExecOrder = pluginListToSort.get(0); 
			for (final MOGLiPlugin mogLiPlugin : pluginListToSort)
			{
				if (mogLiPlugin.getSuggestedPositionInExecutionOrder() < pluginWithMinExecOrder.getSuggestedPositionInExecutionOrder()) {
					pluginWithMinExecOrder = mogLiPlugin;
				}
			}
			toReturn.add(pluginWithMinExecOrder);
			pluginListToSort.remove(pluginWithMinExecOrder);
		}
		
		return toReturn;
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