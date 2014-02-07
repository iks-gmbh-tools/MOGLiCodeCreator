package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DUPLICATE_PLUGINIDS;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_STARTERCLASS_UNKNOWN;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_STARTERCLASS_WRONG_TYPE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.exceptions.DuplicatePluginIdException;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;

/**
 * Helps MOGLi class to do its job
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class PluginLoader {

	PluginLoader() {}
	
	public static List<MOGLiPlugin> doYourJob(List<PluginMetaData> pluginMetaDataList) {
		return new PluginLoader().loadPlugins(pluginMetaDataList);
	}

	List<MOGLiPlugin> loadPlugins(List<PluginMetaData> pluginMetaDataList) {
		List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>(); 

		if (pluginMetaDataList.size() == 0) {
			return plugins;
		}
		
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.isStatusOK()) {
				LoadResult loadResult = loadThisPlugin(pluginMetaData.getStarterClass());
				if (loadResult.plugin == null) {
					pluginMetaData.setInfoMessage(loadResult.errorMessage);
					MOGLiLogUtil.logWarning(pluginMetaData.toString());
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
			loadResult.plugin = (MOGLiPlugin) Class.forName(starterClassName).newInstance();
		} catch (ClassNotFoundException e) {
			loadResult.errorMessage = TEXT_STARTERCLASS_UNKNOWN;
		} catch (ClassCastException e) {
			loadResult.errorMessage = TEXT_STARTERCLASS_WRONG_TYPE;
		} catch (Exception e) {
			throw new MOGLiCoreException("Unexpected Error instanziating " + starterClassName,  e);
		}
		return loadResult;
	}

	// *****************************  private methods  ************************************
	
	private void checkPluginIdsUnique(List<MOGLiPlugin> plugins) {
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0; i < plugins.size(); i++) {
			String id = plugins.get(i).getId();
			if (hs.contains(id)) {
				MOGLiLogUtil.logError(TEXT_DUPLICATE_PLUGINIDS + id);
				throw new DuplicatePluginIdException(id);
			}
			hs.add(id);
		}
	}

	private void updateMetaData(final PluginMetaData pluginMetaData,
			                    final MOGLiPlugin loadedPlugin) 
	{
		pluginMetaData.setDependencies(loadedPlugin.getDependencies());
		pluginMetaData.setId(loadedPlugin.getId());
		pluginMetaData.setPluginType(loadedPlugin.getPluginType());
		pluginMetaData.setPluginStatus(PluginStatus.LOADED);
		pluginMetaData.setSuggestedExecutionOrder(loadedPlugin.getSuggestedPositionInExecutionOrder());

	}
	
	class LoadResult {
		MOGLiPlugin plugin;
		String errorMessage;
	}

}