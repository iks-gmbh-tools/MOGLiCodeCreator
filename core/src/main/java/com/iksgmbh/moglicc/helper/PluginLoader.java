package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_DUPLICATE_PLUGINIDS;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_STARTERCLASS_UNKNOWN;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_STARTERCLASS_WRONG_TYPE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.moglicc.exceptions.DuplicatePluginIdException;
import com.iksgmbh.moglicc.plugin.MogliPlugin;
import com.iksgmbh.moglicc.plugin.PluginExecutable;
import com.iksgmbh.moglicc.utils.MogliLogUtil;

/**
 * Helps Mogli class to do its job
 * @author Reik Oberrath
 */
public class PluginLoader {

	PluginLoader() {}
	
	public static List<PluginExecutable> doYourJob(List<PluginMetaData> pluginMetaDataList) {
		return new PluginLoader().loadPlugins(pluginMetaDataList);
	}

	List<PluginExecutable> loadPlugins(List<PluginMetaData> pluginMetaDataList) {
		List<PluginExecutable> plugins = new ArrayList<PluginExecutable>(); 

		if (pluginMetaDataList.size() == 0) {
			return plugins;
		}
		
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.isStatusOK()) {
				LoadResult loadResult = loadThisPlugin(pluginMetaData.getStarterClass());
				if (loadResult.plugin == null) {
					pluginMetaData.setInfoMessage(loadResult.errorMessage);
					MogliLogUtil.logWarning(pluginMetaData.toString());
				} else {
					plugins.add(loadResult.plugin);
					updateMetaData(pluginMetaData, loadResult.plugin);
				}
			}
		}
		
		checkPluginIdsUnique(plugins);
		
		return plugins;
	}

	LoadResult loadThisPlugin(String starterClassName) {
		LoadResult loadResult = new LoadResult();
		try {
			loadResult.plugin = (PluginExecutable) Class.forName(starterClassName).newInstance();
		} catch (ClassNotFoundException e) {
			loadResult.errorMessage = TEXT_STARTERCLASS_UNKNOWN;
		} catch (ClassCastException e) {
			loadResult.errorMessage = TEXT_STARTERCLASS_WRONG_TYPE;
		} catch (Exception e) {
			throw new MogliCoreException("Unexpected Error instanziating " + starterClassName,  e);
		}
		return loadResult;
	}

	// *****************************  private methods  ************************************
	
	private void checkPluginIdsUnique(List<PluginExecutable> plugins) {
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0; i < plugins.size(); i++) {
			String id = plugins.get(i).getId();
			if (hs.contains(id)) {
				MogliLogUtil.logError(TEXT_DUPLICATE_PLUGINIDS + id);
				throw new DuplicatePluginIdException(id);
			}
			hs.add(id);
		}
	}

	private void updateMetaData(PluginMetaData pluginMetaData,
			                            MogliPlugin loadedPlugin) {
		pluginMetaData.setDependencies(loadedPlugin.getDependencies());
		pluginMetaData.setId(loadedPlugin.getId());
		pluginMetaData.setPluginType(loadedPlugin.getPluginType());
		pluginMetaData.setPluginStatus(PluginStatus.LOADED);
	}
	
	class LoadResult {
		PluginExecutable plugin;
		String errorMessage;
	}

}
