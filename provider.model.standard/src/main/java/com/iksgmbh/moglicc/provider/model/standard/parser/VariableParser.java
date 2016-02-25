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
package com.iksgmbh.moglicc.provider.model.standard.parser;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;

public class VariableParser extends AnnotationParser {
	
	public VariableParser() {
		super(MetaModelConstants.VARIABLE_IDENTIFIER + " ", AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER, AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}
	
	public Annotation parse(String line) throws MOGLiPluginException 
	{
		final Annotation toReturn = super.doYourJob(line);
		if (toReturn.getAdditionalInfo() == null) {
			throw new MOGLiPluginException(TextConstants.INVALID_INFORMATION + "for variable '" + toReturn.getName() + "'");
		}
		
		String additionalInfo = toReturn.getAdditionalInfo();
		
		if (additionalInfo.startsWith(MetaModelConstants.UPPERCASE_IDENTIFIER))
		{
			int charsToCut = MetaModelConstants.UPPERCASE_IDENTIFIER.length();
			additionalInfo = additionalInfo.substring(charsToCut).toUpperCase();
			toReturn.setAdditionalInfo(additionalInfo);
		}

		if (additionalInfo.startsWith(MetaModelConstants.LOWERCASE_IDENTIFIER))
		{
			int charsToCut = MetaModelConstants.LOWERCASE_IDENTIFIER.length();
			additionalInfo = additionalInfo.substring(charsToCut).toLowerCase();
			toReturn.setAdditionalInfo(additionalInfo);
		}
		
		if ( ! additionalInfo.contains(" ") 
			 && 
			(additionalInfo.startsWith("\"") && additionalInfo.endsWith("\"")))
		{
			additionalInfo = additionalInfo.substring(1, additionalInfo.length()-1);
			toReturn.setAdditionalInfo(additionalInfo);
		}
		
		return toReturn;
	}

}