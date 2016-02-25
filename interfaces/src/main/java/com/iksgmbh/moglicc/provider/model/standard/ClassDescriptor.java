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

public interface ClassDescriptor extends MetaInfoSupport {

	/**
	 * @return simple name of class read from model file
	 */
	String getSimpleName();
	
	/**
	 * @return simple name of class read from model file.
	 *         Analog to the corresponding model and attribute method.
	 */
	String getName();
	

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