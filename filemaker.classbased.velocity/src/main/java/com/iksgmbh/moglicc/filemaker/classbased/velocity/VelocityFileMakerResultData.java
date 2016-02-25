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
package com.iksgmbh.moglicc.filemaker.classbased.velocity;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.BuildUpVelocityGeneratorResultData;

/**
 * Object to build a data structure with information needed to create a new target file.
 *
 * @author Reik Oberrath
 * @since 1.4.0
 */
public class VelocityFileMakerResultData extends BuildUpVelocityGeneratorResultData {

	public VelocityFileMakerResultData(final GeneratorResultData generatorResultData) {
		super(generatorResultData);
	}
	
	@Override
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException 
	{
		if (getTargetFileName() == null) {
			validationErrors.add(NO_TARGET_FILE_NAME);
		}
		
		super.validatePropertyKeys(artefact);
	}
	
}