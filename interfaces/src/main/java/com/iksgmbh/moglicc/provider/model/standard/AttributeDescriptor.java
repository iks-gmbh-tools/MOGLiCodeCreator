package com.iksgmbh.moglicc.provider.model.standard;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;

public interface AttributeDescriptor extends MetaInfoSupport {
	
	/**
	 * @return name of attribute read from model file
	 */
	String getName();

}
