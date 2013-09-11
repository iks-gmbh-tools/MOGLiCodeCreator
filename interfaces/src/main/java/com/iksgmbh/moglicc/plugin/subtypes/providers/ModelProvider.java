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
public interface ModelProvider extends ProviderPlugin {

	String getModelFileName();
	String getModelName();
	Model getModel(String pluginId) throws MOGLiPluginException;

}