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
package com.iksgmbh.moglicc.generator.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.utils.FileUtil;

public class TemplateUtil {
	
	public static final String NO_MAIN_TEMPLATE_FOUND = "Main template file containing the artefact properties not found: ";

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