package com.iksgmbh.moglicc.plugin.type.basic;

import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.plugin.PluginExecutable;
import com.iksgmbh.moglicc.provider.model.standard.Model;

/**
 * Interface for other plugins to use the modelProvider's functionality.
 * 
 * @author Reik Oberrath
 */
public interface ModelProvider extends PluginExecutable {

	Model getModel() throws MogliPluginException;

}
