package com.iksgmbh.moglicc.plugin.type.basic;

import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.plugin.PluginExecutable;

public interface EngineProvider extends PluginExecutable {

	/**
	 * Called by a plugin to feed a engine provider with neccassary data.
	 * Two interacting plugins know their interface and can cast 
	 * the engineData object on a concrete type
	 * @throws MogliPluginException
	 */
	void setEngineData(Object engineData) throws MogliPluginException;
	
	/**
	 * Called by a plugin to use the engineProvider's functionality to do its job.
	 * Two interacting plugins know their interface and can cast the return object 
	 * on the corresponding type
	 * @return arbitrary result object
	 * @throws MogliPluginException
	 */
	Object startEngine() throws MogliPluginException;
}
