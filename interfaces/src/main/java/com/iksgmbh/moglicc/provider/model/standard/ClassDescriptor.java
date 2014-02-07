package com.iksgmbh.moglicc.provider.model.standard;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;

public interface ClassDescriptor extends MetaInfoSupport {

	/**
	 * @return simple name of class read from model file
	 */
	String getSimpleName();

	/**
	 * @return package to which class belongs
	 */
	String getPackage();

	/**
	 * @return fully qualified of class read from model file
	 */
	String getFullyQualifiedName();
	
	/**
	 * @return list of attribute descriptors that contain class definition details read from model file 
	 */
	List<AttributeDescriptor> getAttributeDescriptorList();

	
	/**
	 * @return attribute descriptor that name equals attributeName 
	 */
	AttributeDescriptor getAttributeDescriptor(String attributeName);	
}
