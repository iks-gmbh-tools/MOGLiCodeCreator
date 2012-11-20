package com.iksgmbh.moglicc.provider.model.standard;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;

public interface Model extends MetaInfoSupport {
	
	List<ClassDescriptor> getClassDescriptorList();
	
	/**
	 * @return number of classes
	 */
	int getSize();
	
	String getName();

}
