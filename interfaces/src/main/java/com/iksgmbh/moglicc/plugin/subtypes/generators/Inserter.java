package com.iksgmbh.moglicc.plugin.subtypes.generators;

import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;


/**
 * Marker interface for inserter plugins.
 * Typically a inserter generates only a part of content and 
 * inserts such a part into a designated area within a existing file.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface Inserter extends GeneratorPlugin {

}