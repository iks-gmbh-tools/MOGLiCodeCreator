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
package com.iksgmbh.moglicc.provider.model.standard;

import static com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants.MODEL_IDENTIFIER;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.moglicc.provider.model.standard.exceptions.ModelParserException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.parser.ModelParser;
import com.iksgmbh.moglicc.test.StandardModelProviderTestParent;

public class ModelUnitTest extends StandardModelProviderTestParent {
	
	@Test
	public void returnsAllMetaInfos() throws ModelParserException {
		// prepare test
		final List<String> fileContentAsList = getFileContent(
				MODEL_IDENTIFIER + " Model Name", 
				"metainfo targetDir C:\\",
                "class de.test.Person", 	
                "metainfo extends java.lang.Object", 
                "metainfo implements java.lang.Cloneable",
                "attribute FirstName",
                "metainfo JavaType int",
                "attribute LastName",
                "metainfo JavaType String",
                "class de.test.Address", 
                "metainfo extends java.lang.Object",
                "attribute Street");
		final Model model = ModelParser.doYourJob(fileContentAsList, AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER);		

		// call functionality under test
		final List<MetaInfo> allMetaInfos = model.getAllMetaInfos();

		// verify test result
		assertEquals("MetaInfo number", 6, allMetaInfos.size());
	}
	
}