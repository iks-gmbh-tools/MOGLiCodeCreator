package com.iksgmbh.moglicc.generator.utils;

import java.io.File;
import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.utils.FileUtil;

public class TemplateUtil {
	
	public static final String NO_MAIN_TEMPLATE_FOUND = "No main template found in ";
	
	public static String findMainTemplate(final File templateDir, final String identifier) throws MOGLiPluginException2 {
		final List<File> files = FileUtil.getOnlyFileChildren(templateDir);
		if (files.size() == 1) {
			return files.get(0).getName();
		}
		for (File file : files) {
			if (file.isFile() && file.getName().contains(identifier)) {
				return file.getName();
			}
		}
		throw new MOGLiPluginException2(NO_MAIN_TEMPLATE_FOUND + templateDir.getAbsolutePath());
	}

}
