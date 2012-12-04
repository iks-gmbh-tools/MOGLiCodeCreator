package com.iksgmbh.moglicc.generator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;

public class PropertiesUtil {
	
	public static Properties readProperties(final File propertiesFile) throws MOGLiPluginException2 {
		final Properties properties = new Properties();
		try {
			final FileInputStream fileInputStream = new FileInputStream(propertiesFile);
			properties.load(fileInputStream);
		    fileInputStream.close();
		} catch (IOException e) {
			throw new MOGLiPluginException2("Could not load " + propertiesFile.getAbsolutePath(), e);
		}
		return properties;
	}
}
