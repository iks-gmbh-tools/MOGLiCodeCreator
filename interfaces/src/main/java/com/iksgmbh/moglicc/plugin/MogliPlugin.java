package com.iksgmbh.moglicc.plugin;

import java.util.List;

/**
 * Basic plugin type on which Mogli plugins are casted when they are loaded by reflection.
 * 
 * @author Reik Oberrath
 */
public interface MogliPlugin {
	
	String FILENAME_PLUGIN_JAR_PROPERTIES = "Mogli.properties";
	
	enum PluginType { GENERATOR, MODEL_PROVIDER, DATA_PROVIDER, ENGINE_PROVIDER};
	
	/**
	 * @return PluginType to indicate the plugin's purpose 
	 */
	PluginType getPluginType();	
	
	/**
	 * @return Unique id of plugin
	 */
	String getId();	
	
	/**
	 * @return List of other Mogli plugins which this plugin needs to execute
	 */
	List<String> getDependencies();
	
	
}
