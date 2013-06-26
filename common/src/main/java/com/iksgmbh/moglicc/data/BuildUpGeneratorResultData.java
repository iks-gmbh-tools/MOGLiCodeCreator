package com.iksgmbh.moglicc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.utils.FileUtil;

/**
 * Object to build a data structure with information for a generator plugin needed to create artefacts.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class BuildUpGeneratorResultData implements GeneratorResultData {

	public static final String NO_CONTENT = "No generated content set.";

	protected HashMap<String, List<String >> propertyMap = new HashMap<String, List<String>>();

	protected String generatedContent;
	protected List<String> validationErrors = new ArrayList<String>();

	public void setGeneratedContent(String generatedContent) {
		this.generatedContent = generatedContent;
	}

	public void addProperty(final String key, final String value) {
		List<String> values = propertyMap.get(key.toLowerCase()); // case insensitive!
		if (values == null) {
			values = new ArrayList<String>();
			propertyMap.put(key.toLowerCase(), values); // case insensitive!
		}
		values.add(value);			
	}

	@Override
	public String getGeneratedContent() {
		return generatedContent;
	}

	@Override
	/**
	 * Returns first value of the value list of the corresponding key
	 */
	public String getProperty(final String key) {
		final List<String> list = propertyMap.get(key.toLowerCase()); // case insensitive!
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0); 
	}

	@Override
	public List<String> getAllPropertyValues(final String key) {
		return propertyMap.get(key.toLowerCase());
	}

	public int getPropertiesNumber() {
		return propertyMap.size();
	}

	public HashMap<String, List<String >> getPropertyMap() {
		return propertyMap;
	}

	public List<String> getAllKeys() {
		final Set<String> keySet = propertyMap.keySet();
		return new ArrayList<String>(keySet);
	}

	public List<String> getAllPropertiesValues() {
		final List<String> toReturn = new ArrayList<String>();
		final List<String> allKeys = getAllKeys();
		for (final String key : allKeys) {
			toReturn.addAll(propertyMap.get(key));
		}
		return toReturn;
	}

	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException {
		if (generatedContent == null) {
			validationErrors.add(NO_CONTENT);
		}

		if (validationErrors.size() > 0) {
			throw new MOGLiPluginException(buildErrorString(artefact));
		}
	}

	protected String buildErrorString(final String artefact) {
		final StringBuffer sb = new StringBuffer();
		sb.append("Error(s) validating GeneratorResultData for artefact '" + artefact + "':");
		sb.append(FileUtil.getSystemLineSeparator());
		for (final String errorMessage : validationErrors) {
			sb.append("   ").append(errorMessage).append(FileUtil.getSystemLineSeparator());
		}
		sb.append("generated content:").append(FileUtil.getSystemLineSeparator());
		sb.append(generatedContent).append(FileUtil.getSystemLineSeparator());
		sb.append("header attributes found:").append(FileUtil.getSystemLineSeparator());
		final Set<String> keySet = propertyMap.keySet();
		for (final String key : keySet) {
			final String value = getProperty(key);
			sb.append(key + " = " + value).append(FileUtil.getSystemLineSeparator());
		}

		return sb.toString().trim();
	}

	public String searchTextInGeneratedContentBetween(final String s1, final String s2) {
		int pos = generatedContent.indexOf(s1);
		if (pos == -1) {
			return "";
		}
		String substring = generatedContent.substring(pos);
		pos = substring.indexOf(s2);
		if (pos == -1) {
			return "";
		}
		return substring.substring(s1.length() + 1, pos);
	}


}