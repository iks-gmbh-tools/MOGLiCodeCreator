package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;

/**
 * Functionality for generator plugins to provide information how to validate 
 * the MetaInfo elements used in the model.
 *  
 * @author Reik Oberrath
 */
public interface MetaInfoValidatorVendor {
	
	List<MetaInfoValidator> getMetaInfoValidatorList() throws MOGLiPluginException;

}
