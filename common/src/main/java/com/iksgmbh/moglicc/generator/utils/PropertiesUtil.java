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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;

public class PropertiesUtil {
	
	public static Properties readProperties(final File propertiesFile) throws MOGLiPluginException {
		final Properties properties = new Properties();
		try {
			final FileInputStream fileInputStream = new FileInputStream(propertiesFile);
			properties.load(fileInputStream);
		    fileInputStream.close();
		} catch (IOException e) {
			throw new MOGLiPluginException("Could not load " + propertiesFile.getAbsolutePath(), e);
		}
		return properties;
	}
}