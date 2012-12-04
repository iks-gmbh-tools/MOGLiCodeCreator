package com.iksgmbh.moglicc.plugin.type.basic;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;

public interface EngineProvider extends MOGLiPlugin2 {

	/**
	 * Called by a plugin to feed a engine provider with neccassary data.
	 * Two interacting plugins know their interface and can cast 
	 * the engineData object on a concrete type
	 * @throws MOGLiPluginException2
	 */
	void setEngineData(Object engineData) throws MOGLiPluginException2;
	
	/**
	 * Called by a plugin to use the engineProvider's functionality to do its job.
	 * Two interacting plugins know their interface and can cast the return object 
	 * on the corresponding type
	 * @return arbitrary result object
	 * @throws MOGLiPluginException2
	 */
	Object startEngine() throws MOGLiPluginException2;
}
