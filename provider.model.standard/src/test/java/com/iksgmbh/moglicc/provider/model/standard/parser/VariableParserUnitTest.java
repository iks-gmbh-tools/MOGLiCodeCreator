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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.test.StandardModelProviderTestParent;

public class VariableParserUnitTest  extends StandardModelProviderTestParent 
{
	private VariableParser variableParser;
	
	@Override
	@Before
	public void setup() {
		super.setup();
		variableParser = new VariableParser();
	}
	
	@Test
	public void convertsVariableValueToUpperCase() throws MOGLiPluginException {
		// prepare test
		final String line = MetaModelConstants.VARIABLE_IDENTIFIER + " testVariable " + 
		                    MetaModelConstants.UPPERCASE_IDENTIFIER + " to be converted to upperCase";

		// call functionality under test
		final Annotation result = variableParser.parse(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("key", "testVariable", result.getName());
		assertEquals("value", " TO BE CONVERTED TO UPPERCASE", result.getAdditionalInfo());
	}
	
	@Test
	public void convertsVariableValueToLowerCase() throws MOGLiPluginException {
		// prepare test
		final String line = MetaModelConstants.VARIABLE_IDENTIFIER + " testVariable " + 
		                    MetaModelConstants.LOWERCASE_IDENTIFIER + " TO BE CONVERTED TO LOWERCASE";

		// call functionality under test
		final Annotation result = variableParser.parse(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("key", "testVariable", result.getName());
		assertEquals("value", " to be converted to lowercase", result.getAdditionalInfo());
	}
	
	@Test
	public void convertsVariableValueToUpperCaseWithQuotes() throws MOGLiPluginException {
		// prepare test
		final String line = MetaModelConstants.VARIABLE_IDENTIFIER + " testVariable " + 
				            MetaModelConstants.UPPERCASE_IDENTIFIER + "\" to be converted to upperCase\"";

		// call functionality under test
		final Annotation result = variableParser.parse(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("key", "testVariable", result.getName());
		assertEquals("value", "\" TO BE CONVERTED TO UPPERCASE\"", result.getAdditionalInfo());
	}
	
	@Test
	public void convertsVariableValueToLowerCaseWithQuotes() throws MOGLiPluginException {
		// prepare test
		final String line = MetaModelConstants.VARIABLE_IDENTIFIER + " testVariable " + 
				            MetaModelConstants.LOWERCASE_IDENTIFIER + "\" TO BE CONVERTED TO LOWERCASE\"";

		// call functionality under test
		final Annotation result = variableParser.parse(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("key", "testVariable", result.getName());
		assertEquals("value", "\" to be converted to lowercase\"", result.getAdditionalInfo());
	}

	@Test
	public void convertsVariableWithoutUpperAndLowerConversion() throws MOGLiPluginException {
		// prepare test
		final String line = MetaModelConstants.VARIABLE_IDENTIFIER + " testVariable " + 
				            "\"This reMains unChanged.\"";

		// call functionality under test
		final Annotation result = variableParser.parse(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("key", "testVariable", result.getName());
		assertEquals("value", "\"This reMains unChanged.\"", result.getAdditionalInfo());
	}
	
}