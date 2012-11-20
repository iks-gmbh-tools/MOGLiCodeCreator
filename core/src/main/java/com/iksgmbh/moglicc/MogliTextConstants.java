package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MogliSystemConstants.FILENAME_APPLICATION_PROPERTIES;
import com.iksgmbh.moglicc.plugin.MogliPlugin;
import com.iksgmbh.moglicc.plugin.PluginExecutable;

public class MogliTextConstants {
	public static final String TEXT_PLUGINS_FOUND = "Plugins found:";
	public static final String TEXT_APPLICATION_TERMINATED = "MOGLI Code Creator terminated: ";
	public static final String TEXT_NOTHING_TO_DO = "No Plugin Found! Nothing to do.";
	public static final String TEXT_FILES_FOUND = " plugin(s) found to load:";
	public static final String TEXT_DONE = "Done!";
	public static final String TEXT_STARTERCLASS_MANIFEST_PROPERTIES = "starterclass";
	public static final String TEXT_DUPLICATE_PLUGINIDS = "Plugin ID not unique or plugin registered twice: ";
	public static final String TEXT_NO_MANIFEST_FOUND = "NO " 
		                        + MogliPlugin.FILENAME_PLUGIN_JAR_PROPERTIES + " FOUND IN PLUGIN JAR";
	public static final String TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE = "NO " 
		                        + TEXT_STARTERCLASS_MANIFEST_PROPERTIES + " FOUND IN " 
		                        + MogliPlugin.FILENAME_PLUGIN_JAR_PROPERTIES;
	public static final String TEXT_STARTERCLASS_UNKNOWN = "STARTERCLASS UNKNOWN";	
	public static final String TEXT_DEACTIVATED_PLUGIN_PROPERTY = "DEACTIVATE";
	public static final String TEXT_DEACTIVATED_PLUGIN_INFO = "DEACTIVATE by user in " 
		                        + FILENAME_APPLICATION_PROPERTIES;
	public static final String TEXT_STARTERCLASS_WRONG_TYPE = "STARTERCLASS IS NO " 
		                                                      + PluginExecutable.class.getSimpleName() 
		                                                      + " TYPE";
	public static final String TEXT_UNRESOLVABLE_DEPENDENCIES = "DEPENDENCIES NOT RESOLVABLE";
	public static final String TEXT_PLUGIN_EXECUTED = "Executed successfully";
	public static final String TEXT_UNEXPECTED_PROBLEM = "UNEXPECTED PROBLEM: ";
	public static final String TEXT_INFOMESSAGE_OK = "OK";
}
