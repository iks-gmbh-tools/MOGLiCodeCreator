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
package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.List;

/**
 * Interface for model elements that provide MetaInfo functionality
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface MetaInfoSupport {

	String META_INFO_NOT_FOUND_START = "!MetaInfo FOR";
	String META_INFO_NOT_FOUND_END = "NOT FOUND!";
	String META_INFO_NOT_FOUND = META_INFO_NOT_FOUND_START + " '#' " + META_INFO_NOT_FOUND_END;

	/**
	 * @param metaInfoName
	 * @return value for (first) MetaInfo element with metaInfoName
	 */
	String getMetaInfoValueFor(String metaInfoName);

	/**
	 * @param metaInfoName
	 * @return List of values for MetaInfo elements with metaInfoName
	 */
	List<String> getAllMetaInfoValuesFor(String metaInfoName);

	/**
	 * @param metaInfoName
	 * @return List of values for MetaInfo elements with metaInfoName
	 *         as one comma separated String
	 */
	String getCommaSeparatedListOfAllMetaInfoValuesFor(String metaInfoName);


	/**
	 * @return List of MetaInfo elements of the current hierarchy level
	 */
	List<MetaInfo> getMetaInfoList();

	/**
	 * @param prefix
	 * @return List of MetaInfo elements of the current hierarchy level with names starting with <prefix>
	 */
	List<MetaInfo> getMetaInfosWithNameStartingWith(String prefix);
	
	/**
	 * @return List of MetaInfo elements including those of containing hierarchy levels
	 */
	List<MetaInfo> getAllMetaInfos();

	/**
	 * @param metaInfoName
	 * @param value
	 * @return true if the current MetaInfo list contains an element with metaInfoName and value
	 */
	boolean doesHaveMetaInfo(String metaInfoName, String value);

	/**
	 * @param metaInfoName
	 * @return true if the current MetaInfo list contains at least one element with metaInfoName
	 */
	boolean doesHaveAnyMetaInfosWithName(String metaInfoName);

	/**
	 * @param metaInfoValue
	 * @return true if the value does contain META_INFO_NOT_FOUND
	 * @since 1.3.0
	 */
	boolean isValueAvailable(String metaInfoValue);
	
	/**
	 * @param metaInfoValue
	 * @return false if the value does contain META_INFO_NOT_FOUND
	 * @since 1.5.0
	 */
	boolean isValueNotAvailable(String metaInfoValue);	
	
}