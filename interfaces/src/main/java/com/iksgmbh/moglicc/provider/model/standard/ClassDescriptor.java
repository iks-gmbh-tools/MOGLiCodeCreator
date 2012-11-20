package com.iksgmbh.moglicc.provider.model.standard;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;

public interface ClassDescriptor extends MetaInfoSupport {

	String getSimpleName();
	String getPackage();
	String getFullyQualifiedName();
	
	List<AttributeDescriptor> getAttributeDescriptorList();

}
