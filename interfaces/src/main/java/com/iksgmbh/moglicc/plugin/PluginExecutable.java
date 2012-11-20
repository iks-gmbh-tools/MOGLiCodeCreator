package com.iksgmbh.moglicc.plugin;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;

/**
 * Interface for communication between Core and Plugins.
 * @author Reik Oberrath
 */
public interface PluginExecutable extends MogliPlugin {
	
	String DEFAULT_DATA_DIR = "defaultInputData";
	String HELP_DATA_DIR = "helpData";
	
	/**
	 * Called from core to inject the Mogli infrastructure.
	 * @param infrastructure
	 */
	void setMogliInfrastructure(InfrastructureService infrastructure);
	
	InfrastructureService getMogliInfrastructure();
	
	/**
	 * Called from core to execute the plugins.
	 * @throws MogliPluginException
	 */
	void doYourJob() throws MogliPluginException;
	
	/**
	 * If plugin's input directory does not exist, the Core calls this method 
	 * to create it and unpacks default data from plugin's jarfile into it.
	 * @return true if default data has been initialized successfully
	 * @throws MogliPluginException
	 */
	boolean unpackDefaultInputData() throws MogliPluginException;
	
	/**
	 * If plugin's help directory does not exist, the Core calls this method 
	 * to create it and unpacks help data from plugin's jarfile into it.
	 * @return true if help data has been initialized successfully
	 * @throws MogliPluginException
	 */
	boolean unpackPluginHelpFiles() throws MogliPluginException;
}
