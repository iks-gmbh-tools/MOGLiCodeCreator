package com.iksgmbh.moglicc.plugin;

import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;

/**
 * Basic plugin type on which Mogli plugins are casted when they are loaded by reflection.
 * It allows interaction between Core and Plugins.
 * 
 * @author Reik Oberrath
 */
public interface MOGLiPlugin2 {
	
	String DEFAULT_DATA_DIR = "defaultInputData";
	String HELP_DATA_DIR = "helpData";
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

	/**
	 * Called from core to inject the Mogli infrastructure.
	 * @param infrastructure
	 */
	void setMOGLiInfrastructure(InfrastructureService infrastructure);
	
	InfrastructureService getMOGLiInfrastructure();
	
	/**
	 * Called from core to execute the plugins.
	 * @throws MOGLiPluginException2
	 */
	void doYourJob() throws MOGLiPluginException2;
	
	/**
	 * If plugin's input directory does not exist, the Core calls this method 
	 * to create it and unpacks default data from plugin's jarfile into it.
	 * @return true if default data has been initialized successfully
	 * @throws MOGLiPluginException2
	 */
	boolean unpackDefaultInputData() throws MOGLiPluginException2;
	
	/**
	 * If plugin's help directory does not exist, the Core calls this method 
	 * to create it and unpacks help data from plugin's jarfile into it.
	 * @return true if help data has been initialized successfully
	 * @throws MOGLiPluginException2
	 */
	boolean unpackPluginHelpFiles() throws MOGLiPluginException2;
}
