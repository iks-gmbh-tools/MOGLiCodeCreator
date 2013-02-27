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