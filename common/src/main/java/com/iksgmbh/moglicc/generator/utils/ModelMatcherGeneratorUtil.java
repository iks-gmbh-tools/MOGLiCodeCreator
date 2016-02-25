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

import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;

public class ModelMatcherGeneratorUtil {

	public static boolean doesItMatch(final VelocityGeneratorResultData resultData,
			                          final String modelName) throws MOGLiPluginException
	{
		final String key = VelocityGeneratorResultData.KnownGeneratorPropertyNames.NameOfValidModel.name();
		final List<String> namesOfValidModels = resultData.getAllPropertyValues(key);

		if (namesOfValidModels == null || namesOfValidModels.isEmpty()) {
			return false; 
		}

		for (final String nameOfValidModel : namesOfValidModels) {
			if (nameOfValidModel.equals(modelName)) {
				return true; // ok, template has to be applied to this model
			}
		}

		return false;
	}

}