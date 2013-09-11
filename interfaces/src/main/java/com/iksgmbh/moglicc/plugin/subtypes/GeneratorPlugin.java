package com.iksgmbh.moglicc.plugin.subtypes;

import com.iksgmbh.moglicc.plugin.MOGLiPlugin;

/**
 * Interface for generator plugins.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface GeneratorPlugin extends MOGLiPlugin {

	String getGeneratorReport();

	int getNumberOfGenerations();

	int getNumberOfGeneratedArtefacts();
}