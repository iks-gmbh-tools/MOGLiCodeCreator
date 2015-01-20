package com.iksgmbh.moglicc.plugin.subtypes.providers;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;
import com.iksgmbh.moglicc.provider.model.standard.Model;

/**
 * Interface for other plugins to use the modelProvider's functionality.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface ModelProvider extends ProviderPlugin 
{
	String getModelFileName();
	
	String getModelName();
	
	/**
	 * @param pluginId of the calling plugin
	 * @return model
	 * @throws MOGLiPluginException
	 */
	Model getModel(String pluginId) throws MOGLiPluginException;
	
	/**
	 * @param pluginId of the calling plugin
	 * @param inputData data needed to build the model (if neccesarry)
	 *        The specific type needs only to be known by the caller and the implementation of this method. 
	 * @return model
	 * @throws MOGLiPluginException
	 */
	Model getModel(String pluginId, Object inputData) throws MOGLiPluginException;

}