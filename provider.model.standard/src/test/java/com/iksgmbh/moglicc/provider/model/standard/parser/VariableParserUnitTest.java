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
		assertEquals("value", " TO BE CONVERTED TO UPPERCASE", result.getAdditionalInfo());
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
		assertEquals("value", " to be converted to lowercase", result.getAdditionalInfo());
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
		assertEquals("value", "This reMains unChanged.", result.getAdditionalInfo());
	}
	
}
