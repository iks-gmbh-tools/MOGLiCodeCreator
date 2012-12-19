package com.iksgmbh.moglicc.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.utils.FileUtil;

/**
 * Object to build a data structure with information needed to create a result file
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class BuildUpGeneratorResultData implements GeneratorResultData {

	public static final String NO_CONTENT = "No generated content set.";

	protected Properties properties = new Properties();

	protected String generatedContent;
	protected List<String> validationErrors = new ArrayList<String>();

	public void setGeneratedContent(String generatedContent) {
		this.generatedContent = generatedContent;
	}

	public void addProperty(final String key, final String value) {
		properties.put(key.toLowerCase(), value); // case insensitive!
	}

	@Override
	public String getGeneratedContent() {
		return generatedContent;
	}

	@Override
	public String getProperty(final String key) {
		return (String) properties.get(key.toLowerCase()); // case insensitive!
	}

	public int getPropertiesNumber() {
		return properties.size();
	}

	public Properties getProperties() {
		return properties;
	}

	public void validate() throws MOGLiPluginException {
		if (generatedContent == null) {
			validationErrors.add(NO_CONTENT);
		}

		if (validationErrors.size() > 0) {
			throw new MOGLiPluginException(buildErrorString());
		}

	}

	private String buildErrorString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("Error(s) validating GeneratorResultData:").append(FileUtil.getSystemLineSeparator());
		for (final String errorMessage : validationErrors) {
			sb.append("   ").append(errorMessage).append(FileUtil.getSystemLineSeparator());
		}
		sb.append("generated content:").append(FileUtil.getSystemLineSeparator());
		sb.append(generatedContent).append(FileUtil.getSystemLineSeparator());
		sb.append("header attributes found:").append(FileUtil.getSystemLineSeparator());
		final Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			final String key = (String) keys.nextElement();
			final String value = properties.getProperty(key);
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