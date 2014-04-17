package com.iksgmbh.moglicc.generator.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.utils.FileUtil;

public class ArtefactListUtil {
	
	public static final String IGNORE = "ignore";

	/**
	 * @param dir to read artefacts as subdirs
	 * @param propertiesFile contains subdirs to ignore 
	 * @return returns artefact list consisting of subdir that are not ignored
	 * @throws MOGLiPluginException 
	 */
	public static List<String> getArtefactListFrom(final File dir, final File propertiesFile) {
		Properties properties;
		try {
			properties = PropertiesUtil.readProperties(propertiesFile);
		} catch (MOGLiPluginException e) {
			properties = new Properties();
		}
		final List<String> namesOfSubdirs = FileUtil.getNamesOfSubdirs(dir);
		final List<String> toReturn = new ArrayList<String>();
		for (final String subdir : namesOfSubdirs) {
			final String property = properties.getProperty(subdir);
			if (property == null || ! property.equalsIgnoreCase(IGNORE)) {
				toReturn.add(subdir);
			}
		}		
		Collections.sort(toReturn);
		return toReturn;
	}

}
