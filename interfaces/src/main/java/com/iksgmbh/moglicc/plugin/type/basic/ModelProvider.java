package com.iksgmbh.moglicc.plugin.type.basic;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;
import com.iksgmbh.moglicc.provider.model.standard.Model;

/**
 * Interface for other plugins to use the modelProvider's functionality.
 * 
 * @author Reik Oberrath
 */
public interface ModelProvider extends MOGLiPlugin2 {

	Model getModel() throws MOGLiPluginException2;

}
