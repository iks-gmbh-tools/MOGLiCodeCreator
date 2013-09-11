package com.iksgmbh.moglicc.provider.model.standard;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;

public interface Model extends MetaInfoSupport {
	
	/**
	 * @return list of class descriptors that contain class definitions read from model file 
	 */
	List<ClassDescriptor> getClassDescriptorList();
	
	/**
	 * @return number of classes read from model file
	 */
	int getSize();
	
	/**
	 * @return name of model read from model file
	 */
	String getName();

	/**
	 * @param classname simple or fully qualified name of class to be returned 
	 * @return class descriptors that corresponds to classname
	 */
	ClassDescriptor getClassDescriptor(String classname);	

}
