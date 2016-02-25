/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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