package com.iksgmbh.moglicc.generator.utils;

import java.io.File;

import com.iksgmbh.moglicc.exceptions.MogliPluginException;

public class TemplateUtil {
	
	public static final String NO_MAIN_TEMPLATE_FOUND = "No main template found in ";
	
	public static String findMainTemplate(final File templateDir, final String identifier) throws MogliPluginException {
		final File[] files = templateDir.listFiles();
		if (files.length == 1) {
			return files[0].getName();
		}
		for (File file : files) {
			if (file.isFile() && file.getName().contains(identifier)) {
				return file.getName();
			}
		}
		throw new MogliPluginException(NO_MAIN_TEMPLATE_FOUND + templateDir.getAbsolutePath());
	}

}
