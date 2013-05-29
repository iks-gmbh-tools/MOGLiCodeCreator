package com.iksgmbh.moglicc.generator.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.utils.FileUtil;

public class TemplateUtil {
	
	public static final String NO_MAIN_TEMPLATE_FOUND = "File to read artefact properties from not found: ";

	/**
	 * Expects a directory with exactly one main template.
	 * @param templateDir
	 * @param identifier part of the filename that identifies the file as main template
	 * @return
	 * @throws MOGLiPluginException
	 */
	public static String findMainTemplate(final File templateDir, final String identifier) throws MOGLiPluginException {
		final List<File> files = FileUtil.getOnlyFileChildren(templateDir);
		if (files.size() == 1) {
			return files.get(0).getName();
		}
		for (File file : files) {
			if (file.isFile() && file.getName().contains(identifier)) {
				return file.getName();
			}
		}
		throw new MOGLiPluginException(NO_MAIN_TEMPLATE_FOUND + templateDir.getAbsolutePath());
	}

	/**
	 * Expects a directory to contain more than one main template.
	 * @param templateDir
	 * @param identifier part of the filename that identifies the file as main template
	 * @return
	 * @throws MOGLiPluginException
	 */
	public static List<String> findMainTemplates(final File templateDir, final String identifier) throws MOGLiPluginException {
		final List<File> files = FileUtil.getOnlyFileChildren(templateDir);
		final List<String> toReturn = new ArrayList<String>();
		if (files.size() == 0) {
			throw new MOGLiPluginException(NO_MAIN_TEMPLATE_FOUND + templateDir.getAbsolutePath());
		} else if (files.size() == 1) {
			toReturn.add(files.get(0).getName());
		} else  {			
			for (final File file : files) {
				if (file.isFile() && file.getName().contains(identifier)) {
					toReturn.add(file.getName());
				}
			}
		}
		return toReturn;
	}

}
