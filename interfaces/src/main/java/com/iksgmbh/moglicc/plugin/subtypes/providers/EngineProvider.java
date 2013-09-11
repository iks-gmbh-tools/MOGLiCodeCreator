package com.iksgmbh.moglicc.plugin.subtypes.providers;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;

public interface EngineProvider extends ProviderPlugin {

	/**
	 * Called by a plugin to feed a engine provider with neccassary data.
	 * Two interacting plugins know their interface and can cast 
	 * the engineData object on a concrete type
	 * @throws MOGLiPluginException
	 */
	void setEngineData(Object engineData) throws MOGLiPluginException;
	
	/**
	 * Called by a plugin to use the engineProvider's functionality to do its job.
	 * Two interacting plugins know their interface and can cast the return object 
	 * on the corresponding type
	 * @return arbitrary result object
	 * @throws MOGLiPluginException
	 */
	Object startEngine() throws MOGLiPluginException;
}
