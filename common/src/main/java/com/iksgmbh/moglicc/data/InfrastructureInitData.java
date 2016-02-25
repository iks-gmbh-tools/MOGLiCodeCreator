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
package com.iksgmbh.moglicc.data;

import java.io.File;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.plugin.MOGLiPlugin;

/**
 * Group of data fields used by the InfrastructureService and needed to instantiate it.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class InfrastructureInitData {
	
	public final File dirApplicationRoot;
	public final File logsDir;
	public final File outputDir;
	public final File tempDir;
	public final File inputDir;
	public final File helpDir;
	
	public final Properties workspaceProperties;
	
	public List<MOGLiPlugin> pluginList;
	
	public String idOfThePluginToThisInfrastructure;

	public InfrastructureInitData(final File dirApplicationRoot, final File dirLogsFiles, 
			final File dirResultFiles, final File dirTempFiles, final File inputDir,
			final File helpDir, final Properties applicationProperties) {
		this.dirApplicationRoot = dirApplicationRoot;
		this.logsDir = dirLogsFiles;
		this.outputDir = dirResultFiles;
		this.tempDir = dirTempFiles;
		this.inputDir = inputDir;
		this.helpDir = helpDir;
		this.workspaceProperties = applicationProperties;
	}
}