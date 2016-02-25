/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_WORKSPACE_PROPERTIES;

import com.iksgmbh.moglicc.plugin.MOGLiPlugin;

public class MOGLiTextConstants {
	public static final String TEXT_PLUGINS_FOUND = "Plugins found:";
	public static final String TEXT_APPLICATION_TERMINATED = "MOGLi Code Creator terminated: ";
	public static final String TEXT_NOTHING_TO_DO = "No active Plugin! Nothing to do.";
	public static final String TEXT_FILES_FOUND = " plugin(s) found to load:";
	public static final String TEXT_DONE = "Done!";
	public static final String TEXT_STARTERCLASS_MANIFEST_PROPERTIES = "starterclass";
	public static final String TEXT_DUPLICATE_PLUGINIDS = "Plugin ID not unique or plugin registered twice: ";
	public static final String TEXT_NO_MANIFEST_FOUND = "NO "
		                        + MOGLiPlugin.FILENAME_PLUGIN_JAR_PROPERTIES + " FOUND IN PLUGIN JAR";
	public static final String TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE = "NO "
		                        + TEXT_STARTERCLASS_MANIFEST_PROPERTIES + " FOUND IN "
		                        + MOGLiPlugin.FILENAME_PLUGIN_JAR_PROPERTIES;
	public static final String TEXT_STARTERCLASS_UNKNOWN = "STARTERCLASS UNKNOWN";
	public static final String TEXT_ACTIVATED_PLUGIN_PROPERTY = "ACTIVATED";
	public static final String TEXT_DEACTIVATED_PLUGIN_PROPERTY = "DEACTIVATED";
	public static final String TEXT_DEACTIVATED_PLUGIN_INFO = "DEACTIVATED. To activate see "
		                        + FILENAME_WORKSPACE_PROPERTIES + ".";
	public static final String TEXT_STARTERCLASS_WRONG_TYPE = "STARTERCLASS IS NO "
		                                                      + MOGLiPlugin.class.getSimpleName()
		                                                      + " TYPE";
	public static final String TEXT_UNRESOLVABLE_DEPENDENCIES = "DEPENDENCIES NOT RESOLVABLE";
	public static final String TEXT_PLUGIN_EXECUTED = "Executed successfully";
	public static final String TEXT_UNEXPECTED_PROBLEM = "UNEXPECTED PROBLEM: ";
	public static final String TEXT_INFOMESSAGE_OK = "OK";
}