package com.iksgmbh.moglicc.plugin.type.basic;

import com.iksgmbh.moglicc.plugin.MOGLiPlugin;

/**
 * Interface for generator plugins.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface Generator extends MOGLiPlugin {

	String getGenerationReport();

	int getNumberOfGenerations();

	int getNumberOfArtefacts();
}