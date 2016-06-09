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
import java.io.IOException;
import java.util.List;

import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.utils.FileUtil;

public class ModelMatcherGeneratorUtil {

	public static boolean doesModelAndTemplateMatch(final String modelName, 
			                                        final File templateFile,
			                                        final Logger logger,
			                                        final String artifactName) 
			                                        throws MOGLiPluginException
	{
		final List<String> fileContentAsList;
		String nameOfValidModel = null;
		
		try {
			fileContentAsList = FileUtil.getFileContentAsList(templateFile);
		} catch (IOException e) {
			throw new MOGLiPluginException("Error reading template file.", e);
		}
		
		final String nameOfValidModelPropertyKey = "@" + KnownGeneratorPropertyNames.NameOfValidModel.name();
		
		for (String line : fileContentAsList) {
			if (line.trim().startsWith( nameOfValidModelPropertyKey )) {
				nameOfValidModel = line.substring(nameOfValidModelPropertyKey.length());
				if (nameOfValidModel.contains( modelName )) {
					return true;
				}
			}
		}
		
		logger.logInfo("Artefact '" + artifactName + "' has defined '" + nameOfValidModel + "' as valid model.");
        logger.logInfo("This artefact is not generated for current model '" + modelName + "'.");

		return false;
	}

}