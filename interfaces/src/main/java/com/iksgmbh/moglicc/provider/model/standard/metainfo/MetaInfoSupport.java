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
	 * @return true if the value does not contain META_INFO_NOT_FOUND
	 * @since 1.3.0
	 */
	boolean isValueAvailable(String metaInfoValue);	
}