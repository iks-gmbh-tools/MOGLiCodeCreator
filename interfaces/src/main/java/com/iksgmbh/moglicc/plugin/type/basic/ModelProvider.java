package com.iksgmbh.moglicc.plugin.type.basic;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.provider.model.standard.Model;

/**
 * Interface for other plugins to use the modelProvider's functionality.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface ModelProvider extends MOGLiPlugin {

	Model getModel() throws MOGLiPluginException;
	String getModelName();

}