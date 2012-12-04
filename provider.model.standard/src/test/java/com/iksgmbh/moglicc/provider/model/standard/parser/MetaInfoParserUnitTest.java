package com.iksgmbh.moglicc.provider.model.standard.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpMetaInfo;
import com.iksgmbh.moglicc.test.StandardModelProviderTestParent;
import com.iksgmbh.helper.AnnotationParser;

public class MetaInfoParserUnitTest extends StandardModelProviderTestParent {

	private MetaInfoParser metaInfoParser;
	
	@Override
	@Before
	public void setup() {
		super.setup();
		metaInfoParser = new MetaInfoParser();
	}
	
	@Test
	public void returnsMetaInfoWithManySpacesInInputLine() throws MOGLiPluginException {
		// prepare test
		final String line = MetaModelConstants.META_INFO_IDENTIFIER + "   Name  abc";

		// call functionality under test
		final BuildUpMetaInfo result = metaInfoParser.parse(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("Name", "Name", result.getName());
		assertEquals("value", "abc", result.getValue());
	}

	@Test
	public void returnsMetaInfoWithSpaces() throws MOGLiPluginException {
		// prepare test
		final String line = MetaModelConstants.META_INFO_IDENTIFIER + "   \"N a m e\"   \"a b c\"";

		// call functionality under test
		final BuildUpMetaInfo result = metaInfoParser.parse(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("Name", "N a m e", result.getName());
		assertEquals("value", "a b c", result.getValue());
	}
	
	@Test
	public void throwsExceptionIfTrailingBraceForNameIsMissing() {
		// prepare test
		final String line = MetaModelConstants.META_INFO_IDENTIFIER + " \"N a m e abc";
		
		// call functionality under test
		try {
			metaInfoParser.parse(line);
		} catch (IllegalArgumentException e) {
			assertStringContains(e.getMessage(), AnnotationParser.ERROR);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfTrailingBraceForValueIsMissing() {
		// prepare test
		final String line = MetaModelConstants.META_INFO_IDENTIFIER + " Name \"a b c";
		
		// call functionality under test
		try {
			metaInfoParser.parse(line);
		} catch (IllegalArgumentException e) {
			assertStringContains(e.getMessage(), AnnotationParser.ERROR);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfValueIsMissing() {
		// prepare test
		final String line = MetaModelConstants.META_INFO_IDENTIFIER + " Name";
		
		// call functionality under test
		try {
			metaInfoParser.parse(line);
		} catch (IllegalArgumentException e) {
			assertStringContains(e.getMessage(), TextConstants.MISSING_VALUE);
			return;
		}
		fail("Expected exception not thrown!");
	}
}
