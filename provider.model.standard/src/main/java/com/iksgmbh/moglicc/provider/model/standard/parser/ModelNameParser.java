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
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;

public class ModelNameParser extends AnnotationParser {
	
	public ModelNameParser() {
		super(MetaModelConstants.MODEL_IDENTIFIER + " ", AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER, AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}
	
	public String parse(String line) {
		final Annotation annotation = super.doYourJob(line);
		return annotation.getFullInfo();
	}
}